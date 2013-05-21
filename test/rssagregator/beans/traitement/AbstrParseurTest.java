/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.beans.Item;

/**
 *
 * @author clem
 */
public class AbstrParseurTest {
    
    public AbstrParseurTest() {
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
     * Test of calculHash method, of class AbstrParseur.
     */
    @Test
    public void testCalculHash() throws Exception {
        System.out.println("calculHash");
        List<Item> listItem = new ArrayList<Item>();
        
        Item item = new Item();
        item.setTitre("Mon titre");
        item.setDescription("lalala description");
        listItem.add(item);
        AbstrParseur.calculHash(listItem);
        
        // On vérifi le hash calcule
        System.out.println("HASH : " + listItem.get(0).getHashContenu());
        if(!listItem.get(0).getHashContenu().equals("6faca59a73adfcc1d50d215699163997")){
            fail("Ce n'est pas la hash atendu pou un MD5");
        }
        
    }

//    /**
//     * Test of testParse method, of class AbstrParseur.
//     */
//    @Test
//    public void testTestParse() {
//        System.out.println("testParse");
//        AbstrParseur instance = new AbstrParseur();
//        instance.testParse();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of execute method, of class AbstrParseur.
//     */
//    @Test
//    public void testExecute() throws Exception {
//        System.out.println("execute");
//        String xml = "";
//        AbstrParseur instance = new AbstrParseur();
//        List expResult = null;
//        List result = instance.execute(xml);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

 
}