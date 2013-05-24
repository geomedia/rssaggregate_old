/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author clem
 */
public abstract class AbstrDao {

    protected EntityManager em;
    protected EntityManagerFactory emf;
    protected String PERSISTENCE_UNIT_NAME = "RSSAgregatePU2";
    protected DAOFactory dAOFactory;
//    protected static String REQ_FIND_ALL = "SELECT zazaza";
    protected Class classAssocie;

    public void creer(Object obj) {
        //Il faut initialiser le em
        em = dAOFactory.getEntityManager();
        em.getTransaction().begin();
        em.persist(obj);
        em.getTransaction().commit();

        em.close();
    }

    public void modifier(Object obj) {
        
                System.out.println("");
        try {
            // Test si le flux possède bien un id

            // On récupère l'id
            Method getter = obj.getClass().getMethod("getID");
            Object retour = getter.invoke(obj);

            if (retour != null && retour instanceof Long && (Long) retour >= 0) {
                em = dAOFactory.getEntityManager();
                em.getTransaction().begin();
      
                em.merge(obj);
                em.getTransaction().commit();
            }

            //        if (obj.getID() != null && obj.getID() >= 0) {
            //        }
            //        }
          
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (em != null) {
                em.close();
            }
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
        em = dAOFactory.getEntityManager();
        em.getTransaction().begin();

        
        Class laclass = this.getClassAssocie();

        Object resu = em.find(laclass, id);
        em.getTransaction().commit();
        em.close();
        return resu;

    }

    /**
     * *
     * Supprimer le flux et tous ses objets liées (item, incident,
     * Infocollecte...)
     *
     * @param obj
     */
    public void remove(Object obj) {
        em = dAOFactory.getEntityManager();
        em.getTransaction().begin();
//        em.remove(obj);
        em.remove(em.merge(obj));
        

        em.getTransaction().commit();
        em.close();
    }

    public List<Object> findall() {
        try {
            em = dAOFactory.getEntityManager();
            em.getTransaction().begin();

            Class classasso = this.getClassAssocie();

            String req = "SELECT f FROM " + classasso.getSimpleName() + " f";
            Query query = em.createQuery(req);
            List<Object> result = query.getResultList();
            return result;
        } catch (SecurityException ex) {
            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
            if (em != null) {
                em.close();
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
}
