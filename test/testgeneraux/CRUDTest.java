/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testgeneraux;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.beans.Flux;
import rssagregator.beans.Journal;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.services.crud.ServiceCRUDComportement;
import rssagregator.services.crud.ServiceCRUDFactory;
import rssagregator.services.crud.ServiceCRUDFlux;
import rssagregator.services.crud.ServiceCRUDJournal;

/**
 *
 * @author clem
 */
public class CRUDTest {

    public MediatorCollecteAction comportement;
    public Journal journal_KlemZeitung;
    public Journal journal_Cist;

    public CRUDTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        DemarrageTest demarrageTest = new DemarrageTest();
        demarrageTest.setUp();

    }

    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    /**
     * *
     * Crée le set d'entité pour faire des test
     */
    @Test
    public void create() {
        ServiceCRUDComportement cRUDComportement = (ServiceCRUDComportement) ServiceCRUDFactory.getInstance().getServiceFor(MediatorCollecteAction.class);


        comportement = new MediatorCollecteAction();
        comportement.getDedoubloneur().setDeboubTitle(Boolean.TRUE);
        comportement.getDedoubloneur().setDeboudDesc(Boolean.TRUE);
        comportement.getDedoubloneur().setDedouGUID(Boolean.TRUE);
        comportement.getDedoubloneur().setDedoubCategory(Boolean.TRUE);
        comportement.getDedoubloneur().setDedoubDatePub(Boolean.TRUE);
        comportement.getDedoubloneur().setDedoubLink(Boolean.TRUE);
        comportement.setNom("Compo Test");
        comportement.setDescription("Comportement utilisé dans le test");
        comportement.getRequesteur().setTimeOut(12);
        comportement.setPeriodiciteCollecte(3600);
        try {
            cRUDComportement.ajouter(comportement);
        } catch (Exception ex) {
            fail("Erreur lors de la création du comportement");
            Logger.getLogger(CRUDTest.class.getName()).log(Level.SEVERE, null, ex);
        }


        ServiceCRUDFlux cRUDFlux = (ServiceCRUDFlux) ServiceCRUDFactory.getInstance().getServiceFor(Flux.class);

        ServiceCRUDJournal cRUDJournal = (ServiceCRUDJournal) ServiceCRUDFactory.getInstance().getServiceFor(Journal.class);
        // ----> Journaux
        journal_KlemZeitung = new Journal();
        journal_KlemZeitung.setID(new Long(1));
        journal_KlemZeitung.setNom("Klemm Zeitung");
        journal_KlemZeitung.setComportementParDefaultDesFlux(comportement);
        journal_KlemZeitung.setUrlAccueil("http://AucuneURL");
        journal_KlemZeitung.setLangue("fr");
        journal_KlemZeitung.setPays("fr");
        journal_KlemZeitung.setAutoUpdateFlux(false);
        journal_KlemZeitung.setTypeJournal("hebdo");

        journal_Cist = new Journal();
        journal_Cist.setID(new Long(2));
        journal_Cist.setNom("The New Cistercien");
        journal_Cist.setComportementParDefaultDesFlux(comportement);
        journal_Cist.setUrlAccueil("http://TheNewCistercien.com");
        journal_Cist.setLangue("fr");
        journal_Cist.setPays("fr");
        journal_Cist.setAutoUpdateFlux(false);
        journal_Cist.setTypeJournal("hebdo");

        try {
            cRUDJournal.ajouter(journal_KlemZeitung);
            cRUDJournal.ajouter(journal_Cist);
        } catch (Exception ex) {
            Logger.getLogger(CRUDTest.class.getName()).log(Level.SEVERE, null, ex);
        }




    }
//    @Test
//    public void remove() {
//
//        ColleteTest colleteTest = new ColleteTest();
//        colleteTest.MajManuelleTest();
//        // --->Comportement de collecte
//
//
//        //------------COMPORETEMENT DE COLLECTE
//        ServiceCRUDComportement cRUDComportement = (ServiceCRUDComportement) ServiceCRUDFactory.getInstance().getServiceFor(MediatorCollecteAction.class);
//
////        MediatorCollecteAction comportement = new MediatorCollecteAction();
//        comportement = new MediatorCollecteAction();
//        comportement.getDedoubloneur().setDeboubTitle(Boolean.TRUE);
//        comportement.getDedoubloneur().setDeboudDesc(Boolean.TRUE);
//        comportement.getDedoubloneur().setDedouGUID(Boolean.TRUE);
//        comportement.getDedoubloneur().setDedoubCategory(Boolean.TRUE);
//        comportement.getDedoubloneur().setDedoubDatePub(Boolean.TRUE);
//        comportement.getDedoubloneur().setDedoubLink(Boolean.TRUE);
//        comportement.setNom("Compo Test");
//        comportement.setDescription("Comportement utilisé dans le test");
//        comportement.getRequesteur().setTimeOut(12);
//        comportement.setPeriodiciteCollecte(3600);
//
//        try {
//            cRUDComportement.ajouter(comportement);
//        } catch (Exception ex) {
//            fail("Erreur lors de la création du comportement");
//            Logger.getLogger(CRUDTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}