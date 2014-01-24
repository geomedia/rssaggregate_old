/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testgeneraux;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.beans.Conf;
import rssagregator.dao.DAOConf;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.ServiceMailNotifier;
import rssagregator.services.ServiceServer;
import rssagregator.services.ServiceSynchro;
import rssagregator.servlet.StartServlet;
import rssagregator.utils.ServiceXMLTool;

/**
 *
 * @author clem
 */
public class DemarrageTest {
    
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
        
    
    public DemarrageTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
       
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
           //La var context est définit statiquement
        System.setProperty("confpath", "/var/lib/RSSAgregate/");
        // * Instanciation de la daofactory avec un context Spécifique
        DAOFactory dAOFactory = DAOFactory.getInstanceWithSpecificPU("RSSAgregatePUTest");
        // On supprime les données de la base de test
        clearDB();

        DAOConf daoconf = dAOFactory.getDAOConf();

        try {
            daoconf.charger();
        } catch (IOException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        // On charge la conf et on l'enregistre auprès du service de 
        Conf conf = daoconf.getConfCourante();

        // -----------------Chargement des flux
        DaoFlux daoflux = DAOFactory.getInstance().getDAOFlux();

        try {
            daoconf.verifRootAccount();
        } catch (IOException ex) {
            Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            ServiceXMLTool.instancierServiceEtTache();
        } catch (Exception e) {
            logger.error("Erreur lors de l'instanciation des service depuis servicedef.xml ", e);
        }

        ServiceCollecteur.getInstance().lancerService();
        ServiceMailNotifier.getInstance().lancerService();
        ServiceSynchro.getInstance().lancerService();
        ServiceServer.getInstance().lancerService();
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
        }
        System.out.println("###############################################");
        System.out.println("###############################################");
        System.out.println("###############################################");
        
        
    }
    
    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void hello() {
     
     }
     
     
       /**
     * *
     * Vide la base de données
     */
    private void clearDB() {
        EntityManager em = DAOFactory.getInstance().getEntityManager();
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM Flux f");
        Query q2 = em.createQuery("DELETE FROM FluxPeriodeCaptation f");
        Query q3 = em.createQuery("DELETE FROM Item f");
        Query q4 = em.createQuery("DELETE FROM ItemRaffinee f");

        Query q5 = em.createQuery("DELETE FROM MediatorCollecteAction m");
        Query q6 = em.createQuery("DELETE FROM Journal f");

        Query q7 = em.createQuery("DELETE FROM AbstrDedoublonneur m");
        Query q8 = em.createQuery("DELETE FROM AbstrParseur m");
        Query q9 = em.createQuery("DELETE FROM AbstrRequesteur m");
        Query q10 = em.createQuery("DELETE FROM i_superclass m");

        q10.executeUpdate();
        q2.executeUpdate();
        q.executeUpdate();
        q6.executeUpdate();

        q3.executeUpdate();
        q4.executeUpdate();
        q5.executeUpdate();
        q7.executeUpdate();
        q8.executeUpdate();
        q9.executeUpdate();

        em.getTransaction().commit();
    }
}