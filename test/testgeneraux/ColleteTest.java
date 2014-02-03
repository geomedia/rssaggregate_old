/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testgeneraux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.catalina.startup.Tomcat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.Journal;
import rssagregator.beans.exception.IncompleteBeanExeption;
import rssagregator.beans.traitement.ComportementCollecte;
import rssagregator.dao.DAOFactory;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.ServiceMailNotifier;
import rssagregator.services.ServiceServer;
import rssagregator.services.ServiceSynchro;

/**
 *
 * @author clem
 */
public class ColleteTest {

    Tomcat mTomcat;
    private String mWorkingDir = System.getProperty("java.io.tmpdir");
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
    DemarrageTest demarrageTest;
    CRUDTest cRUDTest;

    public ColleteTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException, SAXException {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * *
     * Lance le service de test avec une PU dédiée
     */
    @Before
    public void setUp() {
        // Démarrage des service
        demarrageTest = new DemarrageTest();
        demarrageTest.setUp();


        // création des entités
        cRUDTest = new CRUDTest();
        cRUDTest.create();
        
        


//        //La var context est définit statiquement
//        System.setProperty("confpath", "/var/lib/RSSAgregate/");
//        // * Instanciation de la daofactory avec un context Spécifique
//        DAOFactory dAOFactory = DAOFactory.getInstanceWithSpecificPU("RSSAgregatePUTest");
//        // On supprime les données de la base de test
//        clearDB();
//
//        DAOConf daoconf = dAOFactory.getDAOConf();
//
//        try {
//            daoconf.charger();
//        } catch (IOException ex) {
//            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        // On charge la conf et on l'enregistre auprès du service de 
//        Conf conf = daoconf.getConfCourante();
//
//        // -----------------Chargement des flux
//        DaoFlux daoflux = DAOFactory.getInstance().getDAOFlux();
//
//        try {
//            daoconf.verifRootAccount();
//        } catch (IOException ex) {
//            Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        try {
//            ServiceXMLTool.instancierServiceEtTache();
//        } catch (Exception e) {
//            logger.error("Erreur lors de l'instanciation des service depuis servicedef.xml ", e);
//        }
//
//        ServiceCollecteur.getInstance().lancerService();
//        ServiceMailNotifier.getInstance().lancerService();
//        ServiceSynchro.getInstance().lancerService();
//        ServiceServer.getInstance().lancerService();
//        try {
//            Thread.sleep(3000);
//        } catch (Exception e) {
//        }
//        System.out.println("###############################################");
//        System.out.println("###############################################");
//        System.out.println("###############################################");
    }

    /**
     * *
     * Ferme les service ouverts pour le test
     */
    @After
    public void tearDown() {

        ServiceCollecteur.getInstance().stopService();
        ServiceServer.getInstance().stopService();
        ServiceMailNotifier.getInstance().stopService();
        ServiceSynchro.getInstance().stopService();

    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    /**
     * *
     * Test la mise a jour manuelle des flux de test
     */
    @Test
    public void MajManuelleTest() {
        System.out.println("--------------------------------");
        System.out.println("|       MajManuelleTest");
        System.out.println("--------------------------------");
        EntityManager em = DAOFactory.getInstance().getEntityManager();


        //-------------------------------------------------------------------------
        //.                 . Ajout des Entités
        //-------------------------------------------------------------------------

        // --->Comportement de collecte

        ComportementCollecte comportement = cRUDTest.comportement;

//        // ----> Journaux
        Journal journal_KlemZeitung = cRUDTest.journal_KlemZeitung;
        Journal journal_Cist = cRUDTest.journal_Cist;


        // On commence par les flux et journaux
        List<Flux> listFlux = new ArrayList<Flux>();


        Flux flux_Klem_Inter = new Flux();
        flux_Klem_Inter.setID(new Long(1));
        flux_Klem_Inter.setUrl("http://localhost:8080/RSSAgregate/test/KZinter.xml");
        flux_Klem_Inter.setMediatorFlux(comportement);
        flux_Klem_Inter.setActive(Boolean.TRUE);
        flux_Klem_Inter.setJournalLie(journal_KlemZeitung);

        Flux flux_Klemm_Une = new Flux();
        flux_Klemm_Une.setID(new Long(2));
        flux_Klemm_Une.setUrl("http://localhost:8080/RSSAgregate/test/KZune.xml");
        flux_Klemm_Une.setMediatorFlux(comportement);
        flux_Klemm_Une.setActive(Boolean.TRUE);
        flux_Klemm_Une.setJournalLie(journal_KlemZeitung);


        Flux flux_Cist_Inter = new Flux();
        flux_Cist_Inter.setID(new Long(3));
        flux_Cist_Inter.setUrl("http://localhost:8080/RSSAgregate/test/CInter.xml");
        flux_Cist_Inter.setMediatorFlux(comportement);
        flux_Cist_Inter.setActive(true);
        flux_Cist_Inter.setJournalLie(journal_Cist);

        Flux flux_Cist_Une = new Flux();
        flux_Cist_Une.setID(new Long(4));
        flux_Cist_Une.setUrl("http://localhost:8080/RSSAgregate/test/CUne.xml");
        flux_Cist_Une.setMediatorFlux(comportement);
        flux_Cist_Une.setActive(true);
        flux_Cist_Une.setJournalLie(journal_Cist);


        Flux flux_Quibouge = new Flux();
        flux_Quibouge.setID(new Long(5));
        flux_Quibouge.setUrl("http://localhost:8080/RSSAgregate/test/quibouge1.xml");
        flux_Quibouge.setMediatorFlux(comportement);
        flux_Quibouge.setActive(true);
        // On ne lui donne pas de journal

//        // Enregistrmeent du comportement et des flux du journal
        try {
            System.out.println("--> Creation des entités");

            em.getTransaction().begin();
//            em.persist(comportement);
//            em.persist(journal_KlemZeitung);

            em.persist(flux_Klem_Inter);
            em.persist(flux_Cist_Inter);
            em.persist(flux_Cist_Une);
            em.persist(flux_Klemm_Une);
            em.persist(flux_Quibouge);
            em.getTransaction().commit();

            em.refresh(flux_Klem_Inter);
            em.refresh(flux_Klemm_Une);
            em.refresh(flux_Cist_Une);
            em.refresh(flux_Cist_Inter);
            em.refresh(flux_Quibouge);
//            em.refresh(journal_KlemZeitung);
            Thread.sleep(2000);
        } catch (Exception e) {
            logger.debug("erreur lors du commit ", e);
        }



        try {
            // Enregistrement du flux
            ServiceCollecteur.getInstance().enregistrerFluxAupresDuService(flux_Klem_Inter);
        } catch (IncompleteBeanExeption ex) {
            Logger.getLogger(ColleteTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Lancement de la collecte du flux


        listFlux.add(flux_Klem_Inter);
        listFlux.add(flux_Klemm_Une);
        listFlux.add(flux_Cist_Inter);
        listFlux.add(flux_Cist_Une);
        listFlux.add(flux_Quibouge);

        try {
            ServiceCollecteur.getInstance().majManuellAll(listFlux);
        } catch (Exception ex) {
            Logger.getLogger(ColleteTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        //-----------------------------------------------------------------------
        //                  Comptage des résultats
        //-----------------------------------------------------------------------
        //----> Klemm International
//        EntityManager em = DAOFactory.getInstance().getEntityManager();
        Query qCpt1 = em.createQuery("SELECT COUNT (i) FROM Item i JOIN i.listFlux f WHERE f.ID=1");
        Long resu = (Long) qCpt1.getSingleResult();

        if (resu != 4) {
            fail("Il devait y avoir 4 items dans le flux 1 il y en a " + resu);
        }

        //-----> Klem Une
        Query qcpt2 = em.createQuery("SELECT COUNT (i) FROM Item i JOIN i.listFlux f WHERE f.ID=2");
        Long resu2 = (Long) qcpt2.getSingleResult();
        if (resu2 != 5) {
            fail("Il devait y avoir 5 item");
        }

        //----> On cherche si on a bien 2 item appartenant aux deux journaux simultanémenet 
        Query qCpt3 = em.createQuery("SELECT DISTINCT(i) FROM Item i JOIN FETCH i.listFlux f WHERE f.ID IN(1,2)");

        List<Item> resu3 = qCpt3.getResultList();//  On arrive pas a faire all in in JPQL. version manuelle
        short sum = 0;
        for (int i = 0; i < resu3.size(); i++) {
            Item item = resu3.get(i);
            if (item.getListFlux().contains(flux_Klem_Inter) && item.getListFlux().contains(flux_Klemm_Une)) {
                sum++;
            }
        }

        if (sum != 1) {
            fail("Une possédé par les deux flux en même temps on en a " + sum);
        }

        //------------------------vérification de Cist inter
        Query qCistInter = em.createQuery("SELECT COUNT(i) FROM Item i JOIN i.listFlux f WHERE f.ID=3");
        Long nbr = (Long) qCistInter.getSingleResult();
        if (nbr != 7) {
            fail("Le journal devait ajour 7 items on a " + nbr);
        }

        //--> A t'on des item null. A t'on bien une item provenant du journal Klemm
        Query qCistInterItems = em.createQuery("SELECT i FROM Item i JOIN i.listFlux f WHERE f.ID=3");
        List<Item> itemsCistInter = qCistInterItems.getResultList();
        short cptKlemm = 0;
        for (int i = 0; i < itemsCistInter.size(); i++) {
            Item item = itemsCistInter.get(i);
            if (item.getTitre().isEmpty() && item.getDescription().isEmpty()) {
                fail("On a au moins une item avec titre et description null pour le flux Cist Internat");
            }
            List<Flux> fDuFlux = item.getListFlux();

            for (int j = 0; j < fDuFlux.size(); j++) {
                Flux flux = fDuFlux.get(j);
                if (flux.equals(flux_Klem_Inter)) {
                    cptKlemm++;
                }
            }

        }
        if (cptKlemm != 1) {
            fail("On devait avoir une item commune avec le flux Inter Klemm on a " + cptKlemm);
        }




        //-------> vérification de Cist Une
        Query qCisteUne = em.createQuery("SELECT COUNT(i) FROM Item i JOIN i.listFlux f WHERE f.ID=4");
        Long nbrCi = (Long) qCisteUne.getSingleResult();
        if (nbrCi != 8) {
            fail("On attendait 9 captures on en a " + nbrCi);
        }

        // On vérifie qu'on a bien aucune item sans titre et desc
        Query qCisteUne2 = em.createQuery("SELECT i FROM Item i JOIN i.listFlux f WHERE f.ID=4");
        List<Item> items = qCisteUne2.getResultList();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getTitre().isEmpty() && item.getDescription().isEmpty()) {
                fail("On a au moins une item sans titre et description");
            }
        }


        //------> vérification du flux qui bouge. On lui change l'url pour avoir un xml légèrement modifié qui doit donner naissance a une nouvelle item
        flux_Quibouge.setUrl("http://localhost:8080/RSSAgregate/test/quibouge2.xml");
        em.getTransaction().begin();
        em.merge(flux_Quibouge);
        em.getTransaction().commit();



        listFlux = new ArrayList<Flux>();
        listFlux.add(flux_Quibouge);
        try {
            ServiceCollecteur.getInstance().majManuellAll(listFlux);
        } catch (Exception ex) {
            Logger.getLogger(ColleteTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Query qFluxQuibouge = em.createQuery("SELECT COUNT(i) FROM Item i JOIN i.listFlux f WHERE f.ID=5");
        Long cpt = (Long) qFluxQuibouge.getSingleResult();
        if (cpt != 4) {
            fail("On devait avoir 4 item dans le flux qui bouge, on en a " + cpt);
        }
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