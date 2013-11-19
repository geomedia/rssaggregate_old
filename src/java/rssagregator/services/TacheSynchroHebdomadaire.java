/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.Future;
import rssagregator.beans.ServeurSlave;
import rssagregator.dao.DAOFactory;

/**
 * Cette tache est lancée toutes les semaines afin de récupérérer sur les serveurs esclaves les items collectées qui
 * manqueraient sur le serveur maitre
 *
 * @author clem
 */
public class TacheSynchroHebdomadaire extends TacheImpl<TacheSynchroHebdomadaire> {

    public TacheSynchroHebdomadaire(Observer s) {
        super(s);
        erreur = false;
    }

    public TacheSynchroHebdomadaire() {
        super();
    }
    /**
     * *
     * Un flag utilisé dans le call;
     */
    private Boolean erreur;
    private List<TacheSynchroRecupItem> synchroSlave;

    @Override
    protected void callCorps() throws Exception {
        synchroSlave = new ArrayList<TacheSynchroRecupItem>();
        // Pour chaque serveur slave
        List<ServeurSlave> listSlave = DAOFactory.getInstance().getDAOConf().getConfCourante().getServeurSlave(); // Pour chaque serveur esclave
        for (int i = 0; i < listSlave.size(); i++) {
            ServeurSlave serveurSlave = listSlave.get(i);
            TacheSynchroRecupItem t = new TacheSynchroRecupItem(ServiceSynchro.getInstance());
            t.setServeurSlave(serveurSlave);
            synchroSlave.add(t);
            Future<TacheSynchroRecupItem> futur = ServiceSynchro.getInstance().getExecutorService().submit(t);
            TacheSynchroRecupItem recupItem = futur.get();

            if (recupItem.getExeption() != null) {
                erreur = true;
            }
        }
    }

//    @Override
//    public TacheSynchroHebdomadaire call() throws Exception {
//        this.exeption = null;
//        try {
//            synchroSlave = new ArrayList<TacheSynchroRecupItem>();
//
//
//            // Pour chaque serveur slave
//            List<ServeurSlave> listSlave = DAOFactory.getInstance().getDAOConf().getConfCourante().getServeurSlave(); // Pour chaque serveur esclave
//            for (int i = 0; i < listSlave.size(); i++) {
//                ServeurSlave serveurSlave = listSlave.get(i);
//                TacheSynchroRecupItem t = new TacheSynchroRecupItem(ServiceSynchro.getInstance());
//                t.setServeurSlave(serveurSlave);
//                synchroSlave.add(t);
//                Future<TacheSynchroRecupItem> futur = ServiceSynchro.getInstance().getExecutorService().submit(t);
//                TacheSynchroRecupItem recupItem = futur.get();
//
//                if (recupItem.getExeption() != null) {
//                    erreur = true;
//                }
//            }
//
//        } catch (Exception e) {
//        } finally {
//            if (erreur) {
//                this.exeption = new Exception("Des erreurs se sont produitent lors de la Synchronisation hebdomadaire");
//            }
//            return this;
//        }
//
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    public Boolean getErreur() {
        return erreur;
    }

    public void setErreur(Boolean erreur) {
        this.erreur = erreur;
    }

    public List<TacheSynchroRecupItem> getSynchroSlave() {
        return synchroSlave;
    }

    public void setSynchroSlave(List<TacheSynchroRecupItem> synchroSlave) {
        this.synchroSlave = synchroSlave;
    }
}
