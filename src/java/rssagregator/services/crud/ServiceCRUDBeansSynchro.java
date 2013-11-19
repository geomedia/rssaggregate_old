/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import rssagregator.beans.BeanSynchronise;
import rssagregator.dao.AbstrDao;
import rssagregator.dao.DAOFactory;
import rssagregator.services.ServiceSynchro;

/**
 * Un certain nombre de beans doivent être synchronisé. Exemple les journaux. Les utilisateur etc. Le traitement des
 * action de CRUD est ainsi différent
 *
 * @author clem
 */
public class ServiceCRUDBeansSynchro extends AbstrServiceCRUD {

    protected ServiceCRUDBeansSynchro() {
    }

    
    
    /***
     * Ajoute le beans à la base de donnée. Si l'ajout s'est bien réalisé diffusion par le biais du service de synchro. SI la diffusion est OK, alors on applique le commit
     * @param obj
     * @throws Exception 
     */
    @Override
    public void ajouter(Object obj) throws Exception {

        
        if (obj != null) {
            //Si le beans n'a pas vocation a être synchronisé on leve une exception
            if (!BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
                throw new Exception("Ce beans n'est pas synchronisable");
            }

            AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass()); //Récupération de la dao
            dao.beginTransaction();
            dao.creer(obj);
            ServiceSynchro.getInstance().diffuser(obj, "add");
            dao.commit();
        }
    }

    /**
     * *
     * Modifie le beans envoyé en argument. Le beans sera comité dans la base de donnée uniquement si la diffusion a été
     * possible auprès du {@link ServiceSynchro}
     *
     * @param obj
     * @throws Exception
     */
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

            if (!BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
                throw new Exception("Ce beans n'est pas synchronisable");
            }

            AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass());
            dao.setEm(em);
//            dao.beginTransaction();

            dao.modifier(obj);
            
            ServiceSynchro.getInstance().diffuser(obj, "mod");
//            dao.commit();
        }
    }

//    @Override
//    public void modifier(Object obj, EntityManager em) throws Exception {
//        if (obj != null) {
//
//            if (!BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
//                throw new Exception("Ce beans n'est pas synchronisable");
//            }
//            
//            em.
//        }
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

            if (!BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
                throw new Exception("Ce beans n'est pas synchronisable");
            }
            AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass());
//            dao.beginTransaction();
            dao.remove(obj);
            ServiceSynchro.getInstance().diffuser(obj, "rem");
//            dao.commit();
        }
             
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        

    @Override
    public void ajouter(Object obj, EntityManager em) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
