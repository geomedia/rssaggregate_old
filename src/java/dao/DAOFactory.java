/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import rssagregator.beans.form.DAOGenerique;

/**
 *
 * @author clem
 */
public class DAOFactory {
    protected String PERSISTENCE_UNIT_NAME = "RSSAgregatePU2";
    private static DAOFactory instance = new DAOFactory();
    public List<EntityManager> listEm = new ArrayList<EntityManager>();
    EntityManager em;
    
    private DaoFlux daoflux = new DaoFlux(this);
    private DAOConf daoConf = new DAOConf(this);
    
    
    
EntityManagerFactory emf;
    public static DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    private DAOFactory() {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em=emf.createEntityManager();
    }

    public DaoFlux getDAOFlux() {
//        DaoFlux daoFlux = new DaoFlux(this);
        // La daoflux est une instance unique
        return daoflux;
//        return daoFlux;
    }
    
    public DAOConf getDAOConf(){
        return daoConf;
    }

    public DaoJournal getDaoJournal() {
        DaoJournal daoJournal = new DaoJournal(this);
        return daoJournal;
    }

    public DaoItem getDaoItem() {
        DaoItem daoItem = new DaoItem(this);
        return daoItem;
    }

    public DAOGenerique getDAOGenerique(){
        return new DAOGenerique(this);
    }
    
    public DAOIncident getDAOIncident(){
        return new DAOIncident(this);
    }
    
    public EntityManager getEntityManager() {
        //TODO : faire le point la créaion du EntityManager, il n'est peut être pas nécessaire de le créer à chaque fois. 
        
//        int i = 0;
//
//        
//        
//        System.out.println("DDE EME");
//        System.out.println("################################");
//        for(i=0; i<listEm.size(); i++){
//            System.out.println("OPEN : "+listEm.get(i).isOpen());;
//        }
       
//        em = emf.createEntityManager();
//        listEm.add(em);
        if(this.em==null || !this.em.isOpen()){
            System.out.println("INSTANCIATION DE l EM");
            em=emf.createEntityManager();
        }

        return this.em;
    }
    
    public void closeem(){
    this.em.close();
}
}
