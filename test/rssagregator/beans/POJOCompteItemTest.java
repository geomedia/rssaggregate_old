/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.persistence.jpa.jpql.parser.DatetimeExpressionBNF;
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
public class POJOCompteItemTest {

    public POJOCompteItemTest() {
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
//     * Test of getFlux method, of class POJOCompteItem.
//     */
//    @Test
//    public void testGetFlux() {
//        System.out.println("getFlux");
//        POJOCompteItem instance = new POJOCompteItem();
//        Flux expResult = null;
//        Flux result = instance.getFlux();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFlux method, of class POJOCompteItem.
//     */
//    @Test
//    public void testSetFlux() {
//        System.out.println("setFlux");
//        Flux flux = null;
//        POJOCompteItem instance = new POJOCompteItem();
//        instance.setFlux(flux);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getCompte method, of class POJOCompteItem.
//     */
//    @Test
//    public void testGetCompte() {
//        System.out.println("getCompte");
//        POJOCompteItem instance = new POJOCompteItem();
//        Map expResult = null;
//        Map result = instance.getCompte();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCompte method, of class POJOCompteItem.
//     */
//    @Test
//    public void testSetCompte() {
//        System.out.println("setCompte");
//        Map<Date, Integer> compte = null;
//        POJOCompteItem instance = new POJOCompteItem();
//        instance.setCompte(compte);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getItems method, of class POJOCompteItem.
//     */
//    @Test
//    public void testGetItems() {
//        System.out.println("getItems");
//        POJOCompteItem instance = new POJOCompteItem();
//        List expResult = null;
//        List result = instance.getItems();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setItems method, of class POJOCompteItem.
//     */
//    @Test
//    public void testSetItems() {
//        System.out.println("setItems");
//        List<Item> items = null;
//        POJOCompteItem instance = new POJOCompteItem();
//        instance.setItems(items);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDate1 method, of class POJOCompteItem.
//     */
//    @Test
//    public void testGetDate1() {
//        System.out.println("getDate1");
//        POJOCompteItem instance = new POJOCompteItem();
//        Date expResult = null;
//        Date result = instance.getDate1();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDate1 method, of class POJOCompteItem.
//     */
//    @Test
//    public void testSetDate1() {
//        System.out.println("setDate1");
//        Date date1 = null;
//        POJOCompteItem instance = new POJOCompteItem();
//        instance.setDate1(date1);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDate2 method, of class POJOCompteItem.
//     */
//    @Test
//    public void testGetDate2() {
//        System.out.println("getDate2");
//        POJOCompteItem instance = new POJOCompteItem();
//        Date expResult = null;
//        Date result = instance.getDate2();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDate2 method, of class POJOCompteItem.
//     */
//    @Test
//    public void testSetDate2() {
//        System.out.println("setDate2");
//        Date date2 = null;
//        POJOCompteItem instance = new POJOCompteItem();
//        instance.setDate2(date2);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of compte method, of class POJOCompteItem.
     */
    @Test
    public void testCompte() {
        System.out.println("compte");
//     

        POJOCompteItem instance = genererInstanceTest();

        try {
            instance.compte();
            if (instance.getCompte().size() != 6) {
                fail("On devrait avoir 6 compte car il y a 6 jours dans l'interval. il y en a " + instance.getCompte().size());
            }

            DateTime dt = new DateTime(new Date(2013, 1, 1)).withTimeAtStartOfDay();
            DateTime dt2 = new DateTime(2013, 1, 1, 0, 0);

            try {
                System.out.println("Retour : " + instance.getValue(new Date(2013, 1, 1)));
                if (!instance.getCompte().get(new DateTime(2013, 1, 1, 0, 0).toDate()).equals(3)) {
                    fail("le resultat devait être 3");
                }
                if (!instance.getCompte().get(new DateTime(2013, 1, 2, 0, 0).toDate()).equals(2)) {
                    fail("le resultat devait être 2");
                }
            } catch (Exception ex) {
                Logger.getLogger(POJOCompteItemTest.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception ex) {
            Logger.getLogger(POJOCompteItemTest.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    /**
     * Test of calculterBoxPloat method, of class POJOCompteItem.
     */
    @Test
    public void testCalculterBoxPloat() throws Exception {
        System.out.println("calculterBoxPloat");
        POJOCompteItem instance = genererInstanceTest();
        
        instance.compte();
        
        instance.calculterBoxPloat();
        
        
        instance = new POJOCompteItem();
        List<Item> listItem = new ArrayList<Item>();
        Item it1 = new Item();
        it1.setDatePub(new DateTime(2013, 1, 1, 1, 0).toDate());
        listItem.add(it1);
        instance.setItems(listItem);
        instance.setDate1(new DateTime(2013, 1, 1, 0, 0).toDate());
        instance.setDate2(new DateTime(2013, 1, 2, 0, 0).toDate());
        
        instance.compte();
        instance.calculterBoxPloat();
        
        
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
//
    /**
     * Test of calculerMoyenne method, of class POJOCompteItem.
     */
    @Test
    public void testCalculerMoyenne() {

        System.out.println("calculerMoyenne");
        Date date1 = new DateTime(2013, 1, 1, 0, 0).toDate();
        Date date2 = new DateTime(2013, 1, 6, 0, 0).toDate();

        POJOCompteItem instance = genererInstanceTest();
        try {
            instance.compte();
        } catch (Exception ex) {
            Logger.getLogger(POJOCompteItemTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Integer expResult = null;
        Float result = instance.calculerMoyenne(date1, date2);

        if (!result.equals(new Float(2.6))) {
            fail("le résultat est 2.6");
        }
//        assertEquals(expResult, result);
        System.out.println("MOY : " + result);

        try {
            instance.calculerMoyenne(null, null);
            fail("devait lever une exeception");
        } catch (Exception e) {
        }
    }

    /**
     * Test of detecterAnomalieParrapportAuSeuil method, of class POJOCompteItem.
     */
    @Test
    public void testDetecterCompteSousMoyenne() {
//        System.out.println("detecterAnomalieParrapportAuSeuil");
        Integer seuil = 30;
        POJOCompteItem instance = genererInstanceTest();

        try {
            instance.compte();
            instance.calculerMoyenne(new DateTime(2013, 1, 1, 0, 0).toDate(), new DateTime(2013, 1, 6, 0, 0).toDate());
        } catch (Exception ex) {
            Logger.getLogger(POJOCompteItemTest.class.getName()).log(Level.SEVERE, null, ex);
        }

//        List expResult = null;
        Map<Date,Integer> result = instance.detecterAnomalieParrapportAuSeuil(seuil);

        if (result.size() != 3) {
            fail("on attend 3 résultats");
        }
        
        
        try { // On tente de faire le calcul avec une valeur null doit lever une NullPointerException
            instance.detecterAnomalieParrapportAuSeuil(null);
            fail("devait lancer une exception");
        } catch (Exception e) {
            if(!e.getClass().equals(NullPointerException.class)){
                fail("devait lever une NullPointerException");
            }
        }
    }

    public static POJOCompteItem genererInstanceTest() {
        POJOCompteItem instance = new POJOCompteItem();

        List<Item> listItem = new ArrayList<Item>();
        Item it1 = new Item();
        it1.setDateRecup(new DateTime(2013, 1, 1, 0, 1).toDate());

        Item it2 = new Item();
        it2.setDateRecup(new DateTime(2013, 1, 1, 0, 1).toDate());

        Item it3 = new Item();
        it3.setDateRecup(new DateTime(2013, 1, 1, 0, 1).toDate());

        Item it4 = new Item();
        it4.setDateRecup(new DateTime(2013, 1, 2, 0, 0).toDate());

        Item it5 = new Item();
        it5.setDateRecup(new DateTime(2013, 1, 2, 0, 0).toDate());

        Item it6 = new Item();
        it6.setDateRecup(new DateTime(2013, 1, 3, 0, 3).toDate());

        Item it7 = new Item();
        it7.setDateRecup(new DateTime(2013, 1, 4, 0, 3).toDate());

        Item it8 = new Item();
        it8.setDateRecup(new DateTime(2013, 1, 4, 0, 0).toDate());

        Item it81 = new Item();
        it81.setDateRecup(new DateTime(2013, 1, 4, 0, 0).toDate());


        Item it82 = new Item();
        it82.setDateRecup(new DateTime(2013, 1, 4, 0, 0).toDate());

        Item it83 = new Item();
        it83.setDateRecup(new DateTime(2013, 1, 4, 0, 0).toDate());

        Item it84 = new Item();
        it84.setDateRecup(new DateTime(2013, 1, 4, 0, 0).toDate());

        Item it85 = new Item();
        it85.setDateRecup(new DateTime(2013, 1, 4, 0, 0).toDate());



        Item it9 = new Item();
        it9.setDateRecup(new DateTime(2013, 1, 6, 0, 0).toDate());

        Item it10 = new Item(); // On ajoute une item sans date Elle ne doit pas être prise en compte ni faire bugger




        listItem.add(it1);
        listItem.add(it2);
        listItem.add(it3);
        listItem.add(it4);
        listItem.add(it5);
        listItem.add(it6);
        listItem.add(it7);
        listItem.add(it8);
        listItem.add(it81);
        listItem.add(it82);
        listItem.add(it83);
        listItem.add(it84);
        listItem.add(it85);
        listItem.add(it9);
        listItem.add(it10);



        instance.setItems(listItem);
        instance.setDate1(new DateTime(2013, 1, 1, 0, 0).toDate());
        instance.setDate2(new DateTime(2013, 1, 6, 0, 0).toDate());
        return instance;
    }
}