/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.incident.NotificationAjoutFlux;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.dao.DaoFlux;
import rssagregator.dao.DaoItem;
import rssagregator.services.ServiceCollecteur;

/**
 * Le service permettant de gérer les opération CRUD sur un flux. La suppression des flux demande une procédure
 * particulière, d'ou la définition d'un serviceCRUD
 *
 * @author clem
 */
public class ServiceCRUDFlux extends ServiceCRUDBeansSynchro {
    
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AbstrServiceCRUD.class);
    
    @Override
    public void ajouter(Object obj) throws Exception {
        Flux cast = (Flux) obj;
        cast.setCreated(new Date());
        super.ajouter(obj); //To change body of generated methods, choose Tools | Templates.
 
        // Le flux doit être enregistré dans le service de collecte

        ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
        collecteur.enregistrerFluxAupresDuService(cast);
    }
    
    
    
    
    
    @Override
    public void modifier(Object obj) throws Exception {
        super.modifier(obj); //To change body of generated methods, choose Tools | Templates.
        // Le flux doit se notifier auprès du service de collecte
        Flux cast = (Flux) obj;
        ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
        collecteur.enregistrerFluxAupresDuService(cast);

//        cast.enregistrerAupresdesService();
//        cast.forceChangeStatut();
//        cast.notifyObservers("mod");

    }
    
    protected ServiceCRUDFlux() {
    }

    /**
     * *
     * Cette méthode n'est pas utilisé au profit de la méthode {@link #SupprimerListFlux(java.util.List, java.lang.Boolean, javax.persistence.EntityManager)
     * }
     *
     * @param obj
     * @throws Exception
     */
    @Override
    public void supprimer(Object obj) throws Exception {
        
        throw new UnsupportedOperationException("Pas implémenter utilisez plutot SupprimerListFlux");
//
//        Flux flux = (Flux) obj;
//        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
//        daoItem.beginTransaction();
//        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
//        daoFlux.setEm(daoItem.getEm()); // On donne a la daoflux le même Entity manager afin d'avoir une trasaction unique pour toute la procédure
//
//        Boolean err = false;
//        List<Item> items = daoItem.itemLieAuFlux(flux);
//
//        int i;
//        for (i = 0; i < items.size(); i++) {
//            Item item = items.get(i);
//
//            //Supppression des items qui vont devenir orphelines
//            if (item.getListFlux().size() < 2) {
//                // On supprimer la relation 
//                item.getListFlux().clear();
//                try {
//                    daoItem.modifier(item);
//                    daoItem.remove(item);
//                } catch (Exception e) {
//                    err = true;
//                    logger.debug("Erreur lors de la suppression", e);
//                }
//
//            } else { // Sinon on détach le flux
//                item.getListFlux().remove(flux);
//
//                try {
//                    daoItem.modifier(item);
//                } catch (Exception ex) {
//                    err = true;
//                    logger.debug("Erreur lors de la modification", ex);
//                }
//            }
//        }
//        // On va supprimer le flux si la procédure de suppression des items s'est déroulée correctement
//        flux.setItem(new ArrayList<Item>());
//
////        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
////        daoFlux.beginTransaction();
//        daoFlux.setEm(daoItem.getEm());
////        daoFlux.setTr(daoItem.getTr());
//
//        try {
//            daoFlux.remove(flux);
//        } catch (IllegalArgumentException ex) {
//            err = true;
//            Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (TransactionRequiredException ex) {
//            err = true;
//            Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            err = true;
//            Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        // Tous le monde est OK, Alors on commit
//        if (!err) {
//            try {
//                daoItem.commit();
//            } catch (Exception e) {
//                logger.debug("Erreur lors du comit de l'item", e);
//            }
//
//
//            try {
//                daoFlux.commit();
//                ServiceCollecteur.getInstance().getCacheHashFlux().removeFlux(flux);
//                // Le flux doit se notifier auprès du service de collecte
//                ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
//                collecteur.retirerFluxDuService(flux);
////                flux.enregistrerAupresdesService();
////                flux.forceChangeStatut();
////                flux.notifyObservers("rem");
//            } catch (Exception e) {
//                logger.debug("erreur", e);
//            }
//
//        } else {
//            throw new Exception("Erreur lors de la suppression");
//        }
    }

    /**
     * *
     * Supprimer un ensemble de flux ainsi que toute les items liés
     *
     * @param listFlux
     * @param comit : Indique si la suppression doit être comité ou non. Permet notamment au {@link ServiceCRUDJournal}
     * de commiter une suppression à la place du présent service.
     * @param em : L'EntityManager manager a utiliser. Permet au service {@link ServiceCRUDJournal} de fournir l'entiti
     * manager afin de comiter ou roolbacker l'ensemble des modif. Si ce paramettre est null, un nouvel em sera utilisé
     * et une nouvelle transaction démarrée.
     */
    public synchronized void SupprimerListFlux(List<Flux> listFlux, Boolean comit, EntityManager em) throws Exception {
        
        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        DAOIncident<NotificationAjoutFlux> daoIncid = (DAOIncident<NotificationAjoutFlux>) DAOFactory.getInstance().getDaoFromType(NotificationAjoutFlux.class);
        Boolean err = false;
        
        if (em == null) { // Si aucun em n'est envoyé on récupère un em depuis la factory
            em = DAOFactory.getInstance().getEntityManager();
            em.getTransaction().begin();
        }
        daoFlux.setEm(em);
        daoItem.setEm(em);
        daoIncid.setEm(em);
        
        
        try {

            // Blockage des ressources impacté par la transaction
            for (int i = 0; i < listFlux.size(); i++) {
                Flux flux = listFlux.get(i);
                if (!em.contains(flux)) {
                    Flux f = em.find(Flux.class, flux.getID());
                    em.lock(f, LockModeType.PESSIMISTIC_WRITE);
                }
            }

            //----------------------------------------------------------------
            //.......Suppression ou retrait de la liaison pour tous les flux 
            //-----------------------------------------------------------------

            Set<Item> listItemConcerne = new HashSet<Item>(); // Liste de toutes les items impacté par la suppression des flux
            for (int i = 0; i < listFlux.size(); i++) {
                Flux flux = listFlux.get(i);
                List<Item> items = daoItem.itemLieAuFlux(flux);
                listItemConcerne.addAll(items);
            }
            
            for (Iterator<Item> it2 = listItemConcerne.iterator(); it2.hasNext();) {
                Item item = it2.next();
                
                item.getListFlux().removeAll(listFlux);
                
                List<Flux> fluxDeLitem = item.getListFlux();
                for (Iterator<Flux> it = fluxDeLitem.iterator(); it.hasNext();) {
                    Flux flux = it.next();
                    
                    for (int k = 0; k < listFlux.size(); k++) {
                        Flux flux1 = listFlux.get(k);
                        if (flux.getID().equals(flux1.getID())) {
                            it.remove();
                        }
                    }
                }
                
                if (item.getListFlux().isEmpty()) {
                    daoItem.remove(item);
                } else {
                    daoItem.modifier(item);
                }
            }

            //-----------------------------------------------------
            // Suppression d'incident Liés aux flux
            //-----------------------------------------------------

            for (int i = 0; i < listFlux.size(); i++) {
                Flux flux = listFlux.get(i);
                
                List listNotification = daoIncid.findNotificationAjoutFluxFromFlux(flux);// On doit supprimer les incidentdeNotification qui pourrait être orpheline. Les incident normaux sont gérée par la cascade
                for (int j = 0; j < listNotification.size(); j++) {
                    Object object = listNotification.get(j);
                    daoFlux.getEm().remove(object);
                }
                daoFlux.remove(flux); // Suppression du flux
            }

            // Si il n'y a pas eu d'erreur et qu'il faut commiter

            
        } catch (Exception e) { // Roolback si il y a des erreur

            if (em != null && em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
            
            throw e;
        } finally { // COMIT de la transaction si tout s'est bien passé
            if (em != null && em.isJoinedToTransaction() && comit) {
                em.getTransaction().commit();
                
                for (int i = 0; i < listFlux.size(); i++) { // Il faut supprimer les hash des flux du collecteur
                    try {
                        ServiceCollecteur.getInstance().retirerFluxDuService(listFlux.get(i)); // On retire le flux du service de collecte.              
                    } catch (Exception e) {
                        logger.debug("Debug", e);
                    }
                }
            }
        }
        
    }
}
