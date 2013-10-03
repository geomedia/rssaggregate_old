/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;

/**
 *
 * @author clem
 */
public class AbstrDedoublonneurTest {

    public AbstrDedoublonneurTest() {
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

    /**
     * Test of testDoublonageMemoire method, of class AbstrDedoublonneur.
     */
//    @Test
//    public void testTestDoublonageMemoire() {
//
//
//
//
//        Item itemdsBDD = new Item();
//        itemdsBDD.setHashContenu("abc");
//
//        Item itemCapture = new Item();
//        itemCapture.setHashContenu("abc");
//
//        Flux flux = new Flux();
//        flux.getItem().add(itemdsBDD);
//
//
//
//
//
//        AbstrDedoublonneur instance = new Dedoubloneur();
//        Boolean expResult = null;
//        Boolean result = instance.testDoublonageMemoire(item, flux);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of testDoublonageBDD method, of class AbstrDedoublonneur.
//     */
//    @Test
//    public void testTestDoublonageBDD() {
//        System.out.println("testDoublonageBDD");
//        Item get = null;
//        Flux flux = null;
//        AbstrDedoublonneur instance = new Dedoubloneur();
//        Boolean expResult = null;
//        Boolean result = instance.testDoublonageBDD(get, flux);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of dedoublonne method, of class AbstrDedoublonneur.
     */
    @Test
    public void testDedoublonne() {

        System.out.println("dedoublonne");
        Item itemdsBDD = new Item();
        itemdsBDD.setHashContenu("abc");

        Item itemCapture = new Item();
        itemCapture.setHashContenu("abc");

        Flux flux = new Flux();
        flux.getItem().add(itemdsBDD);


        List<Item> listItemCapture = new ArrayList<Item>();
        listItemCapture.add(itemCapture);

        AbstrDedoublonneur instance = new Dedoubloneur();

        
        // Les les items dans les deux listes possèdent des items de avec le même hash. Le dédoubloneur doit renvoyer une liste vide
        
        List<Item> result = instance.dedoublonne(listItemCapture, flux);
        
        if(!result.isEmpty()){
            fail("la liste devrait être vide");
        }
        
        
//        List expResult = null;
//        List result = instance.dedoublonne(listItemCapture, flux);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
     
    }
}