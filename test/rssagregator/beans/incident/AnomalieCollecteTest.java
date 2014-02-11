/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import java.util.Date;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author clem
 */
public class AnomalieCollecteTest {
    
    public AnomalieCollecteTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

//    /**
//     * Test of getCauseTechniqueSiteJournal method, of class AnomalieCollecte.
//     */
//    @Test
//    public void testGetCauseTechniqueSiteJournal() {
//        System.out.println("getCauseTechniqueSiteJournal");
//        AnomalieCollecte instance = new AnomalieCollecte();
//        Boolean expResult = null;
//        Boolean result = instance.getCauseTechniqueSiteJournal();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCauseTechniqueSiteJournal method, of class AnomalieCollecte.
//     */
//    @Test
//    public void testSetCauseTechniqueSiteJournal() {
//        System.out.println("setCauseTechniqueSiteJournal");
//        Boolean causeTechniqueSiteJournal = null;
//        AnomalieCollecte instance = new AnomalieCollecte();
//        instance.setCauseTechniqueSiteJournal(causeTechniqueSiteJournal);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCauseChangementLigneEditoriale method, of class AnomalieCollecte.
//     */
//    @Test
//    public void testGetCauseChangementLigneEditoriale() {
//        System.out.println("getCauseChangementLigneEditoriale");
//        AnomalieCollecte instance = new AnomalieCollecte();
//        Boolean expResult = null;
//        Boolean result = instance.getCauseChangementLigneEditoriale();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCauseChangementLigneEditoriale method, of class AnomalieCollecte.
//     */
//    @Test
//    public void testSetCauseChangementLigneEditoriale() {
//        System.out.println("setCauseChangementLigneEditoriale");
//        Boolean causeChangementLigneEditoriale = null;
//        AnomalieCollecte instance = new AnomalieCollecte();
//        instance.setCauseChangementLigneEditoriale(causeChangementLigneEditoriale);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of doitEtreNotifieParMail method, of class AnomalieCollecte.
//     */
//    @Test
//    public void testDoitEtreNotifieParMail() {
//        System.out.println("doitEtreNotifieParMail");
//        AnomalieCollecte instance = new AnomalieCollecte();
//        Boolean expResult = null;
//        Boolean result = instance.doitEtreNotifieParMail();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of toString method, of class AnomalieCollecte.
//     */
//    @Test
//    public void testToString() {
//        System.out.println("toString");
//        AnomalieCollecte instance = new AnomalieCollecte();
//        String expResult = "";
//        String result = instance.toString();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of contientDate method, of class AnomalieCollecte.
//     */
//    @Test
//    public void testContientDate() {
//        System.out.println("contientDate");
//        Date date = null;
//        AnomalieCollecte instance = new AnomalieCollecte();
//        boolean expResult = false;
//        boolean result = instance.contientDate(date);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of peutAjouterDate method, of class AnomalieCollecte.
     */
    @Test
    public void testPeutAjouterDate() {
        
        System.out.println("peutAjouterDate");
        Date d = new DateTime(2012, 1, 1, 1, 1).toDate();
        
        AnomalieCollecte instance = new AnomalieCollecte();
        
        
        boolean expResult = false;
        boolean result = instance.peutAjouterDate(d);
        if(result != true){
            fail("l'anomalie n'a pas de période elle pouvait donc ajouter");
        }
        
        
        PeriodeAnormale p = new PeriodeAnormale();
        p.setDateAnomalie(new DateTime(2005, 2,1, 1, 3).toDate());
        instance.getPeriodeAnormale().add(p);
        result = instance.peutAjouterDate(d);
        if(result!=false){
            fail("devait êter false");
        }
        
        if(instance.peutAjouterDate(new DateTime(2005, 2,1, 1, 3).toDate())){
            System.out.println("lalalal"+instance.peutAjouterDate(new DateTime(2005, 2,1, 1, 3).toDate()));
            fail("la période ne devait pas pouvoir être ajouté");
        }
        
        if(!instance.peutAjouterDate(new DateTime(2005, 2,2, 1, 3).toDate())){
            fail("La période devait pouvoir être ajouté");
        }
    }
}