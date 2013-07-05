/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import rssagregator.beans.Flux;

/**
 * Les DAO étende observable car certaine (flux, conf), sont enregistrée auprès
 * du service de collecte des flux par le patterne observateur
 *
 * @author clem
 */
public abstract class AbstrDao extends Observable {

    protected EntityManager em;
    protected EntityManagerFactory emf;
    protected String PERSISTENCE_UNIT_NAME = "RSSAgregatePU2";
    protected DAOFactory dAOFactory;
//    protected static String REQ_FIND_ALL = "SELECT zazaza";
    protected Class classAssocie;

    public void creer(Object obj) throws Exception {
        //Il faut initialiser le em
//        em = dAOFactory.getEntityManager();
        em.getTransaction().begin();
        em.persist(obj);
        em.getTransaction().commit();

//        em.close();
    }

    public void modifier(Object obj) throws Exception {

        // Test si le flux possède bien un id
        // On récupère l'id
        Method getter = obj.getClass().getMethod("getID");
        Object retour = getter.invoke(obj);

        if (retour != null && retour instanceof Long && (Long) retour >= 0) {
//            em = dAOFactory.getEntityManager();
            em.getTransaction().begin();
            em.merge(obj);
            em.getTransaction().commit();
        }
    }

    /**
     * *
     * Retrouver un objet à patir de son id.
     *
     * @param id
     * @return
     */
    public Object find(Long id) {
//        em = dAOFactory.getEntityManager();
        Class laclass = this.getClassAssocie();
        try {
            Object resu = em.find(laclass, id);
            return resu;
        } catch (Exception e) {
        }
//        em.getTransaction().commit();
//        em.close();
        return null;
    }

    /**
     * *
     * Supprimer le un objet Infocollecte...)
     *
     * @param obj
     */
    public void remove(Object obj) throws Exception {
//        em = dAOFactory.getEntityManager();
        em.getTransaction().begin();
//        em.remove(obj);

        em.remove(em.merge(obj));

        em.getTransaction().commit();
//        em.close();
    }

    public List<Object> findall() {
        try {
//            em = dAOFactory.getEntityManager();

//            em.getTransaction().begin();

            Class classasso = this.getClassAssocie();

            String req = "SELECT f FROM " + classasso.getSimpleName() + " f";
            Query query = em.createQuery(req);
//            query.setHint("eclipselink.cache-usage", "CheckCacheOnly");



            List<Object> result = query.getResultList();

            return result;
        } catch (SecurityException ex) {
            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (em != null) {
//                em.close();
//      em.close();
                System.out.println("FINALLYY");
            }
        }
        return null;
    }

    public AbstrDao() {
    }

    public Class getClassAssocie() {
        return classAssocie;
    }

    public void setClassAssocie(Class classAssocie) {
        this.classAssocie = classAssocie;
    }

    public DAOFactory getdAOFactory() {
        return dAOFactory;
    }

    public void setdAOFactory(DAOFactory dAOFactory) {
        this.dAOFactory = dAOFactory;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
}
