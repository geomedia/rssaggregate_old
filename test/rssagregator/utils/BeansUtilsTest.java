/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.beans.Journal;

/**
 *
 * @author clem
 */
public class BeansUtilsTest {

    public BeansUtilsTest() {
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
     * Test of compareBeanFromId method, of class BeansUtils.
     */
    @Test
    public void testCompareBeanFromId() {
        System.out.println("compareBeanFromId");
        Journal o1 = new Journal();
        Journal o2 = new Journal();

        o1.setID(new Long(10));
        o2.setID(new Long(10));


        boolean resu = BeansUtils.compareBeanFromId(o1, o2);
        if (resu == false) {
            fail("Devait être true");
        }


        o1.setID(new Long(22));

        resu = BeansUtils.compareBeanFromId(o1, o2);
        if (resu == true) {
            fail("Devait être false");
        }

        
        
        o1.setID(null);
         resu = BeansUtils.compareBeanFromId(o1, o2);
        if (resu == true) {
            fail("Devait être false");
        }
        

        o1 = null;
        try {
            resu = BeansUtils.compareBeanFromId(o1, o2);
      
        } catch (Exception e) {
            if(!e.getClass().equals(NullPointerException.class)){
                fail("Devait lever un null pointeur");
            }
        }
        



    }
}