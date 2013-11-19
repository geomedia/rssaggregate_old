///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package rssagregator.services.crud;
//
//import javax.persistence.EntityManager;
//import javax.persistence.LockModeType;
//import rssagregator.dao.AbstrDao;
//import rssagregator.dao.DAOFactory;
//
///**
// *  Pour ajouter modifier un bean qui n'est pas synchronisé mais dont la ressource doit être locké durant la modification
// * @author clem
// */
//public class ServiceCRUDBeansBasiqueLock extends ServiceCRUDBeansBasique{
//
//    @Override
//    public void modifier(Object obj) throws Exception {
//        EntityManager em = DAOFactory.getInstance().getEntityManager();
//        em.getTransaction().begin();
//        modifier(obj, em);
//        em.getTransaction().commit();
//        
////        super.modifier(obj); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void modifier(Object obj, EntityManager em) throws Exception {
//        
//        System.out.println("SERVICE MERGE");
////        AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass());
//        try {
//            em.lock(obj, LockModeType.PESSIMISTIC_WRITE);            
//        } catch (Exception e) {
//            System.out.println("=======================");
//            e.printStackTrace();
//            System.out.println("=======================");
//        }
//
//       
//        em.merge(obj);
//        
////        super.modifier(obj, em); //To change body of generated methods, choose Tools | Templates.
//    }
//
//
//    
//}
