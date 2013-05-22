/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

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

    public static DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    private DAOFactory() {
    }

    public DaoFlux getDAOFlux() {
        DaoFlux daoFlux = new DaoFlux(this);
        return daoFlux;
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
    
    protected EntityManager getEntityManager() {
        //TODO : faire le point la créaion du EntityManager, il n'est peut être pas nécessaire de le créer à chaque fois. 
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = emf.createEntityManager();
        
        return em;
    }
}
