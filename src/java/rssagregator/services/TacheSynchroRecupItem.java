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
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.ServeurSlave;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.SynroRecupItemIncident;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;
import rssagregator.utils.XMLTool;

/**
 * Cette tâche est chargé de récuper les items que le serveur maitre n'aurait pas récupéré sur un serveur esclaves.
 * Cette tache est appelée par la tache TacheSynchroHebdomadaire
 *
 * @author clem
 */
public class TacheSynchroRecupItem extends TacheImpl<TacheSynchroRecupItem> implements Incidable {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheSynchroRecupItem.class);
    private List<Item> itemTrouvees;
    private ServeurSlave serveurSlave;
    private DataOutputStream output = null;
    private DataInputStream input = null;

    /**
     * *
     * Si périodique est à true, en fin de tâche on réajoute
     */
    /**
     * *
     * Un pointeur veur l'executor service permettant d'executer et controler la tâche
     */
    //    ScheduledExecutorService executorService;
    //    public TacheSynchroRecupItem(ScheduledExecutorService executorService) {
    //        this.executorService = executorService;
    //    }
    public TacheSynchroRecupItem(AbstrService executorService) {
        super(executorService);
        this.itemTrouvees = new ArrayList<Item>();
    }

    @Override
    protected void callCorps() throws Exception {

        this.exeption = null;
        //Récupération de la liste des flux. Le travail de synchronisation devra être effectué pour chaque flux...
        List<Flux> fluxs = DAOFactory.getInstance().getDAOFlux().findAllFlux(Boolean.TRUE);
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();

        List<ServeurSlave> listSlave = DAOFactory.getInstance().getDAOConf().getConfCourante().getServeurSlave(); // Pour chaque serveur esclave
//            for (int i = 0; i < listSlave.size(); i++) {
//                ServeurSlave serveurSlave = listSlave.get(i);

        for (int j = 0; j < fluxs.size(); j++) { // Pour chaque flux
            Flux flux = fluxs.get(j);

            //-----------------Construction de la liste des hash des items capturée sur le serveur maitre pour le flux considéré---------
            daoItem.initcriteria();
            List<Flux> lFlcriteria = new ArrayList<Flux>();
            lFlcriteria.add(flux);
            daoItem.setWhere_clause_Flux(lFlcriteria);

            //----------------Mise en place des limites de date pour la synchronisation
            DateTime date1 = new DateTime();
            date1 = date1.minusDays(8).withTime(0, 0, 0, 0);
            DateTime date2 = new DateTime(date1);
            date2 = date2.plusDays(8);
            daoItem.setDate1(date1.toDate());
            daoItem.setDate2(date2.toDate());

            //-----------Rechercher des items et construction de la liste de hash à envoyer aux serveurs esclaves
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



            //------------------------CONFIGURATION ET LANCEMENT DE LA CONNECTION AU SERVEUR SLAVE--------------------------------------

            //On effectue une requete sur le serveur esclave.
//                URL url = new URL("http://" + serveurSlave.getHost() + ":8080/RSSAgregate/item/xmlsync");
            URL url;
            output = null;
            input = null;
            HttpURLConnection connection;

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

                // Par sécurité, on va utiliser le dédoublonneur du comportement associé au flux pour évincer les icones doublons dans le flux
                MediatorCollecteAction comportement = flux.getMediatorFlux().genererClone();
                itemTrouvees = comportement.getDedoubloneur().dedoublonne(itemTrouvees, flux);

                //On enregistre chacune des items
                for (int k = 0; k < itemTrouvees.size(); k++) {
                    Item item = itemTrouvees.get(k);
                    //Les items récupérée depuis le serveur esclave doivent être considérée comme nouvelle. Il ne faut ainsi pas s'intéresser à l'ID de cette item (id qui est propre au serveur escalve)
                    item.setID(null);
                    //On enregistre le nouvel item
//                        daoItem.enregistrement(item, flux);
                    ServiceCollecteur.getInstance().ajouterItemAuFlux(flux, item, null, true, null);
                }
//                    return this;
            }

        }
//            return null;
    }

    @Override
    protected TacheSynchroRecupItem callFinalyse() {
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
        return super.callFinalyse();
    }

    /**
     * *
     * Lance la tâche de récupération des items sur chacun des esclaves. Pour la syntaxe d'envoie de requete HTT
     * l'exemple a été pris sur: http://sigterm.sh/2009/10/simple-post-in-java/
     *
     * @return le nombre d'item récupéré lors de la synchronisation
     * @throws Exception
     */
//    @Override
//    public synchronized TacheSynchroRecupItem call() {
//        try {
//            this.exeption = null;
//            //Récupération de la liste des flux. Le travail de synchronisation devra être effectué pour chaque flux...
//            List<Flux> fluxs = DAOFactory.getInstance().getDAOFlux().findAllFlux(Boolean.TRUE);
//            DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
//
//            List<ServeurSlave> listSlave = DAOFactory.getInstance().getDAOConf().getConfCourante().getServeurSlave(); // Pour chaque serveur esclave
////            for (int i = 0; i < listSlave.size(); i++) {
////                ServeurSlave serveurSlave = listSlave.get(i);
//
//            for (int j = 0; j < fluxs.size(); j++) { // Pour chaque flux
//                Flux flux = fluxs.get(j);
//
//                //-----------------Construction de la liste des hash des items capturée sur le serveur maitre pour le flux considéré---------
//                daoItem.initcriteria();
//                List<Flux> lFlcriteria = new ArrayList<Flux>();
//                lFlcriteria.add(flux);
//                daoItem.setWhere_clause_Flux(lFlcriteria);
//
//                //----------------Mise en place des limites de date pour la synchronisation
//                DateTime date1 = new DateTime();
//                date1 = date1.minusDays(8).withTime(0, 0, 0, 0);
//                DateTime date2 = new DateTime(date1);
//                date2 = date2.plusDays(8);
//                daoItem.setDate1(date1.toDate());
//                daoItem.setDate2(date2.toDate());
//
//                //-----------Rechercher des items et construction de la liste de hash à envoyer aux serveurs esclaves
//                List<Item> items = daoItem.findCretaria();
//                String hashString = "";
//                for (int k = 0; k < items.size(); k++) {
//                    Item item = items.get(k);
//                    hashString += items.get(k).getHashContenu() + ", ";
//                }
//                if (hashString.length() > 2) {
//                    hashString = hashString.substring(0, hashString.length() - 2);
//                }
//
//                //Préparation des données a envoyer en POST au serveur esclave
//                String post = null;
//                try {
//                    post = "idflux=" + URLEncoder.encode(flux.getID().toString(), "UTF-8");
//                    post += "&hash=" + URLEncoder.encode(hashString, "UTF-8");
//                    post += "&vue=" + URLEncoder.encode("xmlsync", "UTF-8");
//
//
//                } catch (UnsupportedEncodingException ex) {
//                    Logger.getLogger(TacheSynchroRecupItem.class
//                            .getName()).log(Level.SEVERE, null, ex);
//                }
//
//
//
//                //------------------------CONFIGURATION ET LANCEMENT DE LA CONNECTION AU SERVEUR SLAVE--------------------------------------
//
//                //On effectue une requete sur le serveur esclave.
////                URL url = new URL("http://" + serveurSlave.getHost() + ":8080/RSSAgregate/item/xmlsync");
//                URL url;
//                output = null;
//                input = null;
//                HttpURLConnection connection;
//
//                url = new URL(serveurSlave.getUrl() + "/item/xmlsync");
//                connection = (HttpURLConnection) url.openConnection();
//                // definition des paramettres de conenction
//                connection.setRequestMethod("POST");
//                connection.setDoInput(true);// Let the run-time system (RTS) know that we want input.
//                connection.setDoOutput(true); // Let the RTS know that we want to do output.
//                connection.setUseCaches(false); // No caching, we want the real thing.
//
//                connection.connect();
//
//                output = new DataOutputStream(connection.getOutputStream());
//                output.writeBytes(post);
//                output.flush();
//
//                input = new DataInputStream(connection.getInputStream());
//
//                //---------------------------------EXPLOITATION DU CONTENU TROUVÉ---------------------------------------------------
//                if (input != null) {
//                    Object serialisation = XMLTool.unSerialize(input);
//                    List<Item> itemTrouvees = (List<Item>) serialisation;
//
//                    // Par sécurité, on va utiliser le dédoublonneur du comportement associé au flux pour évincer les icones doublons dans le flux
//                    MediatorCollecteAction comportement = flux.getMediatorFlux().genererClone();
//                    itemTrouvees = comportement.getDedoubloneur().dedoublonne(itemTrouvees, flux);
//
//                    //On enregistre chacune des items
//                    for (int k = 0; k < itemTrouvees.size(); k++) {
//                        Item item = itemTrouvees.get(k);
//                        //Les items récupérée depuis le serveur esclave doivent être considérée comme nouvelle. Il ne faut ainsi pas s'intéresser à l'ID de cette item (id qui est propre au serveur escalve)
//                        item.setID(null);
//                        //On enregistre le nouvel item
////                        daoItem.enregistrement(item, flux);
//                        ServiceCollecteur.getInstance().ajouterItemAuFlux(flux, item, null, true);
//                    }
//                    return this;
//                }
//
//            }
//            return null;
//        } catch (Exception e) {
//            this.exeption = e;
//            return this;
//        } finally {
//            //Fermeture du input et du output
//            if (output != null) {
//                try {
//                    output.close();
//
//
//                } catch (IOException ex) {
//                    Logger.getLogger(TacheSynchroRecupItem.class
//                            .getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//            if (input != null) {
//                try {
//                    input.close();
//
//
//                } catch (IOException ex) {
//                    Logger.getLogger(TacheSynchroRecupItem.class
//                            .getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//            this.nbrTentative++;
//            this.setChanged();
//            this.notifyObservers();
//
////            }
//        }
//    }
    public List<Item> getItemTrouvees() {
        return itemTrouvees;
    }

    public void setItemTrouvees(List<Item> itemTrouvees) {
        this.itemTrouvees = itemTrouvees;
    }

    public ServeurSlave getServeurSlave() {
        return serveurSlave;
    }

    public void setServeurSlave(ServeurSlave serveurSlave) {
        this.serveurSlave = serveurSlave;
    }

    @Override
    public Class getTypeIncident() {
        return SynroRecupItemIncident.class;
    }

    @Override
    public void gererIncident() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fermetureIncident() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
