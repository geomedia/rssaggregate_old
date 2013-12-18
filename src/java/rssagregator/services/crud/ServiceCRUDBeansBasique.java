/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import java.util.List;
import javax.persistence.EntityManager;
import rssagregator.dao.DAOFactory;
import rssagregator.utils.ExceptionTool;
import rssagregator.utils.TransactionTool;

/**
 * Défintit ajouter modifier supprimer pour tout beans n'ayant pas besoin d'être synchro. Peut être redéclaré pour des
 * besoins spécifiques
 *
 * @author clem
 */
public class ServiceCRUDBeansBasique extends AbstrServiceCRUD {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceCRUDBeansBasique.class);

    protected ServiceCRUDBeansBasique() {
    }

    @Override
    public synchronized void ajouter(Object obj) throws Exception {
        ExceptionTool.argumentNonNull(obj);

        EntityManager em = DAOFactory.getInstance().getEntityManager();

        try {
            em.getTransaction().begin();
            ajouter(obj, em);
        } catch (Exception e) {
            logger.error("Erreur lors du comit", e);
            throw e;
        } finally {
            TransactionTool.commitRollBackIfPossible(em, true);
        }
    }

    @Override
    public synchronized void ajouter(Object obj, EntityManager em) throws Exception {
        ExceptionTool.argumentNonNull(obj);
        TransactionTool.checkEmTransaction(em);
        em.persist(obj);

    }

    @Override
    public void modifier(Object obj) throws Exception {
        ExceptionTool.argumentNonNull(obj);
        EntityManager em = DAOFactory.getInstance().getEntityManager();

        try {
            em.getTransaction().begin();
            modifier(obj, em);
        } catch (Exception e) {
            logger.error("Erreur lors de la modification", e);
            throw e;
        } finally {
            TransactionTool.commitRollBackIfPossible(em, true);
        }
    }

    @Override
    public void modifier(Object obj, EntityManager em) throws Exception {
        ExceptionTool.argumentNonNull(obj);
        TransactionTool.checkEmTransaction(em);
        em.merge(obj);

    }

    /**
     * *
     *
     * @param obj
     * @throws Exception
     */
    @Override
    public void supprimer(Object obj) throws Exception {
        ExceptionTool.argumentNonNull(obj);
        EntityManager em = DAOFactory.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            supprimer(obj, em);
        } catch (Exception e) {
            logger.debug("Erreur lors de la suppression ", e);
            throw e;
        } finally {
            TransactionTool.commitRollBackIfPossible(em, true);
        }
    }

    @Override
    public void supprimer(Object obj, EntityManager em) throws Exception {
        ExceptionTool.argumentNonNull(obj);
        TransactionTool.checkEmTransaction(em);
        try {
            em.remove(obj);
        } catch (Exception e) {
            logger.debug("Erreur lors de la suppression", e);
        }
    }

    /**
     * *
     * Supprime une liste d'entité. Un em va être créer le commit est effectué
     *
     * @param objs
     * @throws Exception
     */
//    @Override
    public void supprimerList(List objs) throws Exception {

        EntityManager em = DAOFactory.getInstance().getEntityManager();
        em.getTransaction().begin();
        try {
            supprimerList(objs, em);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression ", e);
        } finally {
            TransactionTool.commitRollBackIfPossible(em, true);
        }

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void supprimerList(List objs, EntityManager em) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        ExceptionTool.argumentNonNull(objs);
        TransactionTool.checkEmTransaction(em);

        try {

            for (int i = 0; i < objs.size(); i++) {
                Object object = objs.get(i);
                em.remove(em.merge(object));
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de ", e);
        } 

    }

}
