/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.ServeurSlave;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;
import rssagregator.utils.XMLTool;

/**
 * Cette tâche est chargé de récuper les items que le serveur maitre n'aurait
 * pas récupéré. Pour chaque serveur esclave enregistrée dans le maitre, un
 * requete est effectuée pour chaque flux.
 *
 * @author clem
 */
public class TacheSynchroRecupItem implements Callable<List<Item>> {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheSynchroRecupItem.class);
    /***
     * Si périodique est à true, en fin de tâche on réajoute
     */
    Boolean periodique;

    /**
     * *
     * Lance la tâche de récupération des items sur chacun des esclaves voir la
     * doc : http://sigterm.sh/2009/10/simple-post-in-java/
     *
     * @return le nombre d'item récupéré lors de la synchronisation
     * @throws Exception
     */
    @Override
    public synchronized List<Item> call() {

        //Récupération de la liste des flux. Le travail de synchronisation devra être effectué pour chaque flux...
        List<Flux> fluxs = DAOFactory.getInstance().getDAOFlux().findAllFlux(Boolean.TRUE);
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        
        List<ServeurSlave> listSlave = DAOFactory.getInstance().getDAOConf().getConfCourante().getServeurSlave(); // Pour chaque serveur esclave
        for (int i = 0; i < listSlave.size(); i++) {
            ServeurSlave serveurSlave = listSlave.get(i);
            logger.debug("slave : " + serveurSlave);
            for (int j = 0; j < fluxs.size(); j++) { // Pour chaque flux
                logger.debug("travail pour le flux : " + fluxs);
                Flux flux = fluxs.get(j);

                //-----------------Construction de la liste des hash des items capturée sur le serveur maitre pour le flux considéré---------
                daoItem.initcriteria();
                List<Flux> lFlcriteria = new ArrayList<Flux>();
                lFlcriteria.add(flux);
                daoItem.setWhere_clause_Flux(lFlcriteria);
               
                List<Item> items = daoItem.findCretaria();
                 String hashString = "";
                for (int k = 0; k < items.size(); k++) {
                    Item item = items.get(k);
                    hashString += items.get(k).getHashContenu() + ", ";
                }
                if (hashString.length() > 2) {
                    hashString = hashString.substring(0, hashString.length() - 2);
                }
                
                //Préparation des données a envoyer en POST au serveur esclave
                String post = null;
                try {
                    post = "idflux=" + URLEncoder.encode(flux.getID().toString(), "UTF-8");
                    post += "&hash=" + URLEncoder.encode(hashString, "UTF-8");
                    post += "&vue=" + URLEncoder.encode("xmlsync", "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(TacheSynchroRecupItem.class.getName()).log(Level.SEVERE, null, ex);
                }

                //TODO : Il faudra ajouter les critères de date, on ne répère la synch que suivant le critère définit dans la conf


                //------------------------CONFIGURATION ET LANCEMENT DE LA CONNECTION AU SERVEUR SLAVE--------------------------------------

                //On effectue une requete sur le serveur esclave.
//                URL url = new URL("http://" + serveurSlave.getHost() + ":8080/RSSAgregate/item/xmlsync");
                URL url;
                DataOutputStream output = null;
                DataInputStream input = null;
                HttpURLConnection connection;
                try {
                    url = new URL(serveurSlave.getUrl() + "/item/xmlsync");
                    connection = (HttpURLConnection) url.openConnection();
                    // definition des paramettres de conenction
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);// Let the run-time system (RTS) know that we want input.
                    connection.setDoOutput(true); // Let the RTS know that we want to do output.
                    connection.setUseCaches(false); // No caching, we want the real thing.

                    connection.connect();

                    output = new DataOutputStream(connection.getOutputStream());
                    output.writeBytes(post);
                    output.flush();

                    input = new DataInputStream(connection.getInputStream());
                    
              //---------------------------------EXPLOITATION DU CONTENU TROUVÉ---------------------------------------------------
                if (input != null) {
                    Object serialisation = XMLTool.unSerialize(input);
                    List<Item> itemTrouvees = (List<Item>) serialisation;
                    for (int k = 0; k < itemTrouvees.size(); k++) {
                        Item item = itemTrouvees.get(k);
                        //Les items récupérée depuis le serveur esclave doivent être considérée comme nouvelle. Il ne faut ainsi pas s'intéresser à l'ID de cette item (id qui est propre au serveur escalve)
                        item.setID(null);
                        //On enregistre le nouvel item
                        daoItem.enregistrement(item, flux);
                    }
                    return itemTrouvees;
                }

                } catch (MalformedURLException ex) {
                    Logger.getLogger(TacheSynchroRecupItem.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(TacheSynchroRecupItem.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    //Fermeture du input et du output
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException ex) {
                            Logger.getLogger(TacheSynchroRecupItem.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException ex) {
                            Logger.getLogger(TacheSynchroRecupItem.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        //Pour chaque flux
        return null;
    }
}
