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
public class PeriodeAnormaleTest {

    public PeriodeAnormaleTest() {
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
//     * Test of getDateAnomalie method, of class PeriodeAnormale.
//     */
//    @Test
//    public void testGetDateAnomalie() {
//        System.out.println("getDateAnomalie");
//        PeriodeAnormale instance = new PeriodeAnormale();
//        Date expResult = null;
//        Date result = instance.getDateAnomalie();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDateAnomalie method, of class PeriodeAnormale.
//     */
//    @Test
//    public void testSetDateAnomalie() {
//        System.out.println("setDateAnomalie");
//        Date dateAnomalie = null;
//        PeriodeAnormale instance = new PeriodeAnormale();
//        instance.setDateAnomalie(dateAnomalie);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNbrItemCollecte method, of class PeriodeAnormale.
//     */
//    @Test
//    public void testGetNbrItemCollecte() {
//        System.out.println("getNbrItemCollecte");
//        PeriodeAnormale instance = new PeriodeAnormale();
//        Short expResult = null;
//        Short result = instance.getNbrItemCollecte();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setNbrItemCollecte method, of class PeriodeAnormale.
//     */
//    @Test
//    public void testSetNbrItemCollecte() {
//        System.out.println("setNbrItemCollecte");
//        Short nbrItemCollecte = null;
//        PeriodeAnormale instance = new PeriodeAnormale();
//        instance.setNbrItemCollecte(nbrItemCollecte);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getID method, of class PeriodeAnormale.
//     */
//    @Test
//    public void testGetID() {
//        System.out.println("getID");
//        PeriodeAnormale instance = new PeriodeAnormale();
//        Long expResult = null;
//        Long result = instance.getID();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setID method, of class PeriodeAnormale.
//     */
//    @Test
//    public void testSetID() {
//        System.out.println("setID");
//        Long ID = null;
//        PeriodeAnormale instance = new PeriodeAnormale();
//        instance.setID(ID);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of compareTo method, of class PeriodeAnormale.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        PeriodeAnormale o = new PeriodeAnormale();
        o.setDateAnomalie(new DateTime(2013, 1, 1, 1, 1).toDate());
        PeriodeAnormale instance = new PeriodeAnormale();
        instance.setDateAnomalie(new DateTime(2013, 1, 1, 1, 1).toDate());

        int r = instance.compareTo(o);
        if (r != 0) {
            fail("devait être ==");
        }

        o.setDateAnomalie(new DateTime(2015, 1, 1, 1, 1).toDate());
        r = instance.compareTo(o);
        if (r > 0) {
            fail("devait être -1 mais " + r);
        }


        o.setDateAnomalie(new DateTime(2000, 1, 1, 1, 1).toDate());
        r = instance.compareTo(o);
        if (r < 0) {
            fail("devait être 1 mais " + r);
        }

        o.setDateAnomalie(null);
        r = instance.compareTo(o);
        if (r < 0) {
            fail("devait être 1 mais " + r);
        }

        instance.setDateAnomalie(null);
        r = instance.compareTo(o);
        if (r != 0) {
            fail("devait être 0 mais " + r);
        }

    }
}