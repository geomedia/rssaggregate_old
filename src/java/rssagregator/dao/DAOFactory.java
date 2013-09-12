/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxType;
import rssagregator.beans.Journal;
import rssagregator.beans.UserAccount;
import rssagregator.beans.form.DAOGenerique;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.AnomalieCollecte;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.incident.JMSPerteConnectionIncident;
import rssagregator.beans.incident.MailIncident;
import rssagregator.beans.incident.SynchroIncident;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.services.AbstrTacheSchedule;
import rssagregator.services.TacheLancerConnectionJMS;
import rssagregator.services.TacheSynchroRecupItem;

/**
 *
 * @author clem
 */
public class DAOFactory {

    private static DaoItem daoItem;
    protected String PERSISTENCE_UNIT_NAME = "RSSAgregatePU2";
    private static DAOFactory instance = new DAOFactory();
    public List<EntityManager> listEm = new ArrayList<EntityManager>();
    EntityManager em;
//    private DaoFlux daoflux = new DaoFlux(this);
//    private DAOConf daoConf = new DAOConf(this);
    private DaoFlux daoflux;
    private DAOConf daoConf;
    EntityManagerFactory emf;

    public static DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    private DAOFactory() {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = emf.createEntityManager();

        // Attention aux singleton et au multi threading
        daoItem = new DaoItem(this);
        daoflux = new DaoFlux(this);
        daoConf = new DAOConf(this);
    }

    public DaoFlux getDAOFlux() {
//        DaoFlux daoFlux = new DaoFlux(this);
        // La daoflux est une instance unique
        if (daoflux == null) {
            daoflux = new DaoFlux(this);
        }

        return daoflux;
//        return daoFlux;
    }

    public DAOConf getDAOConf() {
        if (daoConf == null) {
            daoConf = new DAOConf(this);
        }

        return daoConf;
    }

    public DaoJournal getDaoJournal() {
        DaoJournal daoJournal = new DaoJournal(this);
        return daoJournal;
    }

    public DaoItem getDaoItem() {

        if (daoItem == null) {
            daoItem = new DaoItem(this);
        }
//        DaoItem daoItem = new DaoItem(this);
        return daoItem;
    }

    public DAOGenerique getDAOGenerique() {
        return new DAOGenerique(this);
    }

    public DAOIncident getDAOIncident() {
        return new DAOIncident(this);
    }

    public DAOComportementCollecte getDAOComportementCollecte() {
        return new DAOComportementCollecte(this);
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
//        if(this.em==null || !this.em.isOpen()){
//            System.out.println("INSTANCIATION DE l EM");
//    
//            em=emf.createEntityManager();
//          
//        }
        // Maintenant on instancie pour chaque DAO un EntityManager. C'est le cache du persist unit qui doit permettre le stockage générale des objet comme flux en mémoire pas l'entity manager
        em = emf.createEntityManager();
        return this.em;
    }

    public DAOUser getDAOUser() {
        DAOUser dao = new DAOUser(this);
        return dao;
    }

    public void closeem() {
        this.em.close();
    }

    /**
     * *
     * Instancie et retourne une dao a partir du type de beans envoyé en
     * argument. Si on envoie un flux, on obtient une daoFLUX
     *
     * @param beansClass : La class du beans devant être géré par la dao
     * @throws UnsupportedOperationException : Si aucune dao n'a été trouvé,
     * emission d'une exception
     * @return : La dao permettant de gérer le type de beans correspondant à la
     * class envoyée en argument
     */
    public AbstrDao getDaoFromType(Class beansClass) throws UnsupportedOperationException {
        if (beansClass.equals(Flux.class)) {
            return getDAOFlux();
        } else if (CollecteIncident.class.isAssignableFrom(beansClass)) {
            return getDAOIncident();
        } else if (beansClass.equals(Journal.class)) {
            return getDaoJournal();
        } else if (beansClass.equals(MediatorCollecteAction.class)) {
            return getDAOComportementCollecte();
        } else if (beansClass.equals(FluxType.class)) {
            DAOGenerique d = getDAOGenerique();
            d.setClassAssocie(beansClass);
            return d;
        } else if (beansClass.equals(UserAccount.class)) {
            return getDAOUser();
        } else if (beansClass.equals(MailIncident.class)) {
            DAOIncident<MailIncident> dao = new DAOIncident<MailIncident>(this);
            dao.setClassAssocie(beansClass);
            return dao;
//            return new DAOIncident();
        } else if (beansClass.equals(SynchroIncident.class)) {
            System.out.println("FACTORY SynchroIncident");
            DAOIncident<SynchroIncident> dao = new DAOIncident<SynchroIncident>(this);
            dao.setClassAssocie(beansClass);
            return dao;
        } else if (beansClass.equals(JMSPerteConnectionIncident.class)) {
            System.out.println("FACTORY JMSPerteConnectionIncident");
            DAOIncident<JMSPerteConnectionIncident> dao = new DAOIncident<JMSPerteConnectionIncident>(this);
            dao.setClassAssocie(beansClass);
            return dao;
        }
        else if(beansClass.equals(AbstrIncident.class)){
            DAOIncident<AbstrIncident> dao = new DAOIncident<AbstrIncident>(this);
            dao.setClassAssocie(beansClass);
            System.out.println("DAO : AbstrIncident");
            
            return dao;
        }
        
        else if(beansClass.equals(AnomalieCollecte.class)){
            DAOIncident<AnomalieCollecte> dao = new DAOIncident<AnomalieCollecte>(this);
            dao.setClassAssocie(beansClass);
            return dao;
        }

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * *
     * Pour obtenir une permettant de gérer des incidents en rapport avec la
     * tâche envoyé en argument. Exemple si on envoie une TacheLancerConnectionJMS, on obtient une DAOIncident<JMSPerteConnectionIncident>
     *
     * @param tache
     * @return
     */
    public AbstrDao getDAOFromTask(AbstrTacheSchedule tache) {
        
        IncidentFactory s = new IncidentFactory();
        
        if(tache.getClass().equals(TacheLancerConnectionJMS.class)){
            DAOIncident<JMSPerteConnectionIncident> dao = new DAOIncident<JMSPerteConnectionIncident>(this);
            dao.setClassAssocie(JMSPerteConnectionIncident.class);
            return dao;
        }
        else if(tache.getClass().equals(TacheSynchroRecupItem.class)){
            DAOIncident<SynchroIncident> dao = new DAOIncident<SynchroIncident>(this);
            dao.setClassAssocie(SynchroIncident.class);
            return dao;
        }
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
