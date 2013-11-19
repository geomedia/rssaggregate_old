/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import rssagregator.dao.AbstrDao;
import rssagregator.dao.DAOFactory;

/**
 *
 * @author clem
 */
public class ServiceCRUDBeansBasique extends AbstrServiceCRUD {

    protected ServiceCRUDBeansBasique() {
    }

    @Override
    public synchronized void ajouter(Object obj) throws Exception {
        if (obj != null) {

            EntityManager em = DAOFactory.getInstance().getEntityManager();
            em.getTransaction().begin();
            ajouter(obj, em);
            em.getTransaction().commit();
        }
    }

    @Override
    public synchronized void ajouter(Object obj, EntityManager em) throws Exception {
        System.out.println("CRUD AJOUTER");
        if (obj != null) {

            em.persist(obj);
            System.out.println("--> EM PERSIST");
//            AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass()); //Récupération de la dao
//            dao.beginTransaction();
//            dao.creer(obj);
//            dao.commit();
        }
        else{
            System.out.println("OBJ NULL");
        }
    }

    @Override
    public void modifier(Object obj) throws Exception {
        EntityManager em = DAOFactory.getInstance().getEntityManager();
        em.getTransaction().begin();
        modifier(obj, em);
        em.getTransaction().commit();
    }

    @Override
    public void modifier(Object obj, EntityManager em) throws Exception {

        if (obj != null) {
            System.out.println("CRUD BASIQUE MOD");
            AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass());
            dao.setEm(em);
//            dao.beginTransaction();

//            if(lock!=null){
//                dao.getEm().lock(obj, lock);
//            }

            dao.modifier(obj);
//            dao.commit();
        }

    }

//        @Override
//    public void modifier(Object obj, EntityManager em) throws Exception {
//        if (obj != null) {
//            System.out.println("CRUD BASIQUE MOD");
//            AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass());
//            dao.beginTransaction();
//            dao.modifier(obj);
//            dao.commit();
//        }
//    }
    @Override
    public void supprimer(Object obj) throws Exception {
        EntityManager em = DAOFactory.getInstance().getEntityManager();
        em.getTransaction().begin();
        supprimer(obj, em);
        em.getTransaction().commit();
    }

    @Override
    public void supprimer(Object obj, EntityManager em) throws Exception {
        if (obj != null) {

            AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass());
            dao.setEm(em);
//            dao.beginTransaction();
            dao.remove(obj);
//            dao.commit();
        }
    }
}
