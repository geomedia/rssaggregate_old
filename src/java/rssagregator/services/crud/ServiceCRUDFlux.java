/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import rssagregator.services.crud.ServiceCRUDBeansSynchro;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.dao.DAOFactory;
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

    @Override
    public void ajouter(Object obj) throws Exception {
        super.ajouter(obj); //To change body of generated methods, choose Tools | Templates.

        // Le flux doit se notifier auprès du service de collecte
        Flux cast = (Flux) obj;
        cast.enregistrerAupresdesService();
        cast.forceChangeStatut();
        cast.notifyObservers("add");
    }

    @Override
    public void modifier(Object obj) throws Exception {
        super.modifier(obj); //To change body of generated methods, choose Tools | Templates.
        // Le flux doit se notifier auprès du service de collecte
        Flux cast = (Flux) obj;
        cast.enregistrerAupresdesService();
        cast.forceChangeStatut();
        cast.notifyObservers("mod");

    }

    protected ServiceCRUDFlux() {
    }
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AbstrServiceCRUD.class);

    @Override
    public void supprimer(Object obj) throws Exception {

        Flux flux = (Flux) obj;
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        daoItem.beginTransaction();
        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
        daoFlux.setEm(daoItem.getEm()); // On donne a la daoflux le même Entity manager afin d'avoir une trasaction unique pour toute la procédure

        Boolean err = false;
        List<Item> items = daoItem.itemLieAuFlux(flux);

        int i;
        for (i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            //Supppression des items qui vont devenir orphelines
            if (item.getListFlux().size() < 2) {
                // On supprimer la relation 
                item.getListFlux().clear();
                try {
                    daoItem.modifier(item);
                    daoItem.remove(item);
                } catch (Exception e) {
                    err = true;
                    logger.debug("Erreur lors de la suppression", e);
                }

            } else { // Sinon on détach le flux
                item.getListFlux().remove(flux);

                try {
                    daoItem.modifier(item);
                } catch (Exception ex) {
                    err = true;
                    logger.debug("Erreur lors de la modification", ex);
                }
            }
        }
        // On va supprimer le flux si la procédure de suppression des items s'est déroulée correctement
        flux.setItem(new ArrayList<Item>());
        System.out.println("================>>>>> BLA BLA");

//        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
//        daoFlux.beginTransaction();
        daoFlux.setEm(daoItem.getEm());
//        daoFlux.setTr(daoItem.getTr());

        try {
            daoFlux.remove(flux);
        } catch (IllegalArgumentException ex) {
            err = true;
            Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransactionRequiredException ex) {
            err = true;
            Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            err = true;
            Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Tous le monde est OK, Alors on commit
        if (!err) {
            System.out.println("COMMIT");
            try {
                daoItem.commit();
                System.out.println("1");
            } catch (Exception e) {
                logger.debug("Erreur lors du comit de l'item", e);
            }


            try {
                daoFlux.commit();
                ServiceCollecteur.getInstance().getCacheHashFlux().removeFlux(flux);
                System.out.println("2");
                // Le flux doit se notifier auprès du service de collecte
                flux.enregistrerAupresdesService();
                flux.forceChangeStatut();
                flux.notifyObservers("rem");
            } catch (Exception e) {
                logger.debug("erreur", e);
            }

        } else {
            throw new Exception("Erreur lors de la suppression");
        }
    }

    /**
     * *
     * Supprimer un ensemble de flux ainsi que toute les items liés
     *
     * @param listFlux
     * @param comit : Indique si la suppression doit être comité ou non. Permet notamment au {@link ServiceCRUDJournal} de commiter une suppression à la place du présent service.
     * @param  em : L'EntityManager manager a utiliser. Permet au service {@link ServiceCRUDJournal} de fournir l'entiti manager afin de comiter ou roolbacker l'ensemble des modif. Si ce paramettre est null, un nouvel em sera utilisé
     */
    public synchronized void SupprimerListFlux(List<Flux> listFlux, Boolean comit, EntityManager em) throws Exception {
        
        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        Boolean err = false;
        
        //Gestion de l'em. >Si un em a été envoyé en argument, il doit être utilisé par les dao. Sinon il faut que les deux dao utilisent le même
        if(em!=null){ // Si un em a été envoyé en param. Les deux DAO doivent l'utiliser
            daoFlux.setEm(em);
            daoItem.setEm(em);
        }
        else{ // Sinon la daoItem doit prendre le même em que la dao Flux
            daoItem.setEm(daoFlux.getEm());
            daoFlux.beginTransaction(); // Démarrage de la transaction (comme les deux dao on le même EM c'est valable pour les deux)
        }
        

        for (int i = 0; i < listFlux.size(); i++) {
            Flux flux = listFlux.get(i);

            List<Item> items = daoItem.itemLieAuFlux(flux); // La liste complete des item liées au flux 

            for (int j = 0; j < items.size(); j++) { // Suppression de chaque Item 
                Item item = items.get(j);

                //Supppression des items qui vont devenir orphelines
                if (item.getListFlux().size() < 2) {
                    // On supprimer la relation 
                    item.getListFlux().clear();
                    try {
//                        daoItem.modifier(item);
                        daoItem.remove(item);
                    } catch (Exception e) {
                        err = true;
                        logger.error("Erreur lors de la suppression", e);
                    }

                } else { // Sinon on détach le flux de l'item, mais elle doit être concervé. Exemple, on supprime le flux Politique d'un journal mais en concervant le flux à la Une. Il faut alors concerver les items communes des deux flux...
                    item.getListFlux().remove(flux);
                    try {
                        daoItem.modifier(item);
                    } catch (Exception ex) {
                        err = true;
                        logger.error("Erreur lors de la modification", ex);
                    }
                }
            }
            
            try {
                daoFlux.remove(flux); // Suppression du flux
            } catch (IllegalArgumentException ex) {
                err = true;
                logger.error("erreur lors de la suppression du flux", ex);
            } catch (TransactionRequiredException ex) {
                err = true;
                logger.error("erreur lors de la suppression du flux", ex);
            } catch (Exception ex) {
                err = true;
                logger.error("erreur lors de la suppression du flux", ex);
            }
        }
        
        // SI il y a eu des erreur on rollback
        if(err){
            daoFlux.roolbackTransaction();
        }

        // Si il n'y a pas eu d'erreur et qu'il faut commiter
        if (!err && comit) {
            try {
                daoFlux.commit();
                // On supprime aussi les hash du cache du service de collecte
                for (int i = 0; i < listFlux.size(); i++) {
                    Flux flux = listFlux.get(i);
                    ServiceCollecteur.getInstance().getCacheHashFlux().removeFlux(flux);
                }
            } catch (Exception e) {
                logger.error("erreur", e);
                throw e;
            }
        }
    }
    //    @Override
    //    /**
    //     * *
    //     * Ajoute le flux et procède a sa synchronisation
    //     */
    //    public void ajouter(Object obj) throws Exception {
    ////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    //        // On récupère la dao
    //        if (obj != null) {
    //            if (!obj.getClass().equals(Flux.class)) {
    //                throw new Exception("Ce service ne eput gérer ce type de beans");
    //            }
    //
    //            Flux cast = (Flux) obj;
    //
    //            DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
    //            dao.beginTransaction();
    //            dao.creer(cast);
    //            dao.commit();
    //
    //            // Le flux doit se notifier auprès du service de collecte
    //            cast.enregistrerAupresdesService();
    //            cast.forceChangeStatut();
    //            cast.notifyObservers();
    //        }
    //    }
    //    @Override
    //    public void modifier(Object obj) throws Exception {
    //
    //        DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
    //        dao.beginTransaction();
    //        dao.modifier(obj);
    //        dao.commit();
    //    }
    //    }
}
