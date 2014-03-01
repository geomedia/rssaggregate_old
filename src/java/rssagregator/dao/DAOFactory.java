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
import rssagregator.beans.Conf;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxType;
import rssagregator.beans.Item;
import rssagregator.beans.Journal;
import rssagregator.beans.UserAccount;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.AliveIncident;
import rssagregator.beans.incident.AnomalieCollecte;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.IncidentDecouverteRSS;
import rssagregator.beans.incident.IncidentFactory;
//import rssagregator.beans.incident.MailIncident;
import rssagregator.beans.incident.NotificationAjoutFlux;
import rssagregator.beans.incident.RecupIncident;
import rssagregator.beans.incident.ServerIncident;
import rssagregator.beans.traitement.ComportementCollecte;
import rssagregator.services.tache.AbstrTache;

/**
 *
 * @author clem
 */
public class DAOFactory<T extends AbstrDao> {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DAOFactory.class);
    /**
     * *
     * Le nom de la persistence unit voir dans persistance.xml
     */
    public static String PERSISTENCE_UNIT_NAME = "RSSAgregatePU2";
//    private static DAOFactory instance = new DAOFactory();
    private static DAOFactory instance;
    public List<EntityManager> listEm = new ArrayList<EntityManager>();
    EntityManager em;
    private DAOConf daoConf;
//     private static DaoItem daoItem;
    EntityManagerFactory emf;

    public static DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    public static DAOFactory getInstanceWithSpecificPU(String pu) {
        PERSISTENCE_UNIT_NAME = pu;
        if (instance == null) {
            instance = new DAOFactory(pu);
        }
        return instance;
    }

    private DAOFactory() {
        System.out.println("---->CREATION PERSISTENCE CONTEXR PU : " + PERSISTENCE_UNIT_NAME);
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);


        em = emf.createEntityManager();


        // Attention aux singleton et au multi threading
//        daoItem = new DaoItem(this);
//        daoflux = new DaoFlux(this);
        daoConf = new DAOConf(this);
    }

    private DAOFactory(String pu) {
        System.out.println("---->CREATION PERSISTENCE CONTEXR PU : " + PERSISTENCE_UNIT_NAME);

        PERSISTENCE_UNIT_NAME = pu;
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

        em = emf.createEntityManager();
        daoConf = new DAOConf(this);

    }

    /***
     * Renvoie une nouvelle daoflux avec un em instanciée.
     * @return 
     */
    public DaoFlux getDAOFlux() {

        DaoFlux daof = new DaoFlux(this, true);
        daof.setClassAssocie(Flux.class);
        return daof;
//        return daoFlux;
    }

    
    /***
     * Charger renvoi une dao flux avec ou non un em préchargé
     * @param withEm true si on en veut un em. Sinon false
     * @return 
     */
    public DaoFlux getDAOFlux(boolean withEm) {
        DaoFlux daof;
            daof = new DaoFlux(this, withEm);
//        }
//        DaoFlux daof = new DaoFlux(this);
        daof.setClassAssocie(Flux.class);
        return daof;
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

//        if (daoItem == null) {
//            daoItem = new DaoItem(this);
//        }
////        DaoItem daoItem = new DaoItem(this);
//        return daoItem;

        DaoItem dao = new DaoItem(this);
        dao.setClassAssocie(Item.class);
        return dao;
    }
    
        public DaoItem getDaoItem(boolean withEm) {

        DaoItem dao = new DaoItem(this, withEm);
        dao.setClassAssocie(Item.class);
        return dao;
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

        EntityManager nEm = emf.createEntityManager();
//        em = emf.createEntityManager();
        return nEm;
    }

    public DAOUser getDAOUser() {
        DAOUser dao = new DAOUser(this);
        return dao;
    }

    /**
     * *
     * Instancie et retourne une dao a partir du type de beans envoyé en argument. Si on envoie un flux, on obtient une
     * daoFLUX
     *
     * @param beansClass : La class du beans devant être géré par la dao
     * @throws UnsupportedOperationException : Si aucune dao n'a été trouvé, emission d'une exception
     * @return : La dao permettant de gérer le type de beans correspondant à la class envoyée en argument
     */
    public T getDaoFromType(Class beansClass) throws UnsupportedOperationException {

        T dao = null;

        if (beansClass.equals(Flux.class)) {
            dao = (T) getDAOFlux();
//            return (T) getDAOFlux();
        } else if (CollecteIncident.class.equals(beansClass)) {
            dao = (T) new DAOIncident<CollecteIncident>(this);
            dao.setClassAssocie(CollecteIncident.class);
//            dao = (T) getDAOIncident();
        } else if (beansClass.equals(RecupIncident.class)) {
            dao = (T) new DAOIncident<RecupIncident>(this);
            dao.setClassAssocie(RecupIncident.class);
        } else if (beansClass.equals(Journal.class)) {
            dao = (T) getDaoJournal();
        } else if (beansClass.equals(ComportementCollecte.class)) {
            dao = (T) getDAOComportementCollecte();
        } else if (beansClass.equals(FluxType.class)) {
            DAOGenerique d = getDAOGenerique();
            d.setClassAssocie(beansClass);
            dao = (T) d;
        } else if (beansClass.equals(UserAccount.class)) {
            dao = (T) getDAOUser();
        } //        else if (beansClass.equals(MailIncident.class)) {
        //            dao = (T) new DAOIncident<MailIncident>(this);
        //            dao.setClassAssocie(beansClass);
        //        } 

        
        else if (beansClass.equals(AbstrIncident.class)) {
            dao = (T) new DAOIncident<AbstrIncident>(this);
            dao.setClassAssocie(beansClass);
        } else if (beansClass.equals(AnomalieCollecte.class)) {
            dao = (T) new DAOIncident<AnomalieCollecte>(this);
            dao.setClassAssocie(beansClass);
        } else if (beansClass.equals(AliveIncident.class)) {
            dao = (T) new DAOIncident<AliveIncident>(this);
            dao.setClassAssocie(beansClass);
        } else if (beansClass.equals(ServerIncident.class)) {
            dao = (T) new DAOIncident<ServerIncident>(this);
            dao.setClassAssocie(beansClass);
        } 
        else if (beansClass.equals(Conf.class)) {
            dao = (T) daoConf;
        } else if (beansClass.equals(Item.class)) {
            dao = (T) getDaoItem();
        } else if (beansClass.equals(NotificationAjoutFlux.class)) {
            dao = (T) new DAOIncident<NotificationAjoutFlux>(this);
            dao.setClassAssocie(NotificationAjoutFlux.class);
        } else if (beansClass.equals(IncidentDecouverteRSS.class)) {
            dao = (T) new DAOIncident<IncidentDecouverteRSS>(this);
            dao.setClassAssocie(IncidentDecouverteRSS.class);
        }


        if (dao != null) {
            return dao;
        } else {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.            
        }
    }

    /**
     * *
     * Pour obtenir une permettant de gérer des incidents en rapport avec la tâche envoyé en argument. Exemple si on
     * envoie une TacheLancerConnectionJMS, on obtient une DAOIncident<JMSPerteConnectionIncident>
     *
     * @param tache
     * @return retourne la dao
     * @throws UnsupportedOperationException : si la tache envoyé n'inplémenta pas incidable ou si la factory n'est pas
     * capable de générer une tao pour la tâche
     */
    /**
     * *
     *
     * @param tache
     * @return
     * @throws TypeNotPresentException
     */
    public T getDAOFromTask(AbstrTache tache) {

        IncidentFactory s = new IncidentFactory();

        if (Incidable.class.isAssignableFrom(tache.getClass())) {
            Incidable cast = (Incidable) tache;

            Class incidClass = cast.getTypeIncident();
            return (T) getDaoFromType(incidClass);
        } else {
            throw new UnsupportedOperationException("La tâche envoyée n'est pas incidable."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * *
     * Fermer L'Entity Manager factory. Doit être lancé à la fermeture de l'application
     */
    public void closeEMF() {

        if (emf.isOpen()) {
            try {
                emf.close();
            } catch (Exception e) {

                logger.error("Erreur lors de la fermeture de l'EMF ", e);
            }
        }
    }
}
