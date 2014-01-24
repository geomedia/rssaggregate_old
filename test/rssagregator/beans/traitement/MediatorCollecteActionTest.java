/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import rssagregator.beans.Flux;

/**
 *
 * @author clem
 */
public class MediatorCollecteActionTest {

    public MediatorCollecteActionTest() {
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
     * Test of getDefaultCollectAction method, of class MediatorCollecteAction.
     */
    @Test
    public void testGetDefaultCollectAction() {
        System.out.println("getDefaultCollectAction");
//        MediatorCollecteAction expResult = null;
        MediatorCollecteAction result = MediatorCollecteAction.getDefaultCollectAction();
//        assertEquals(expResult, result);

        if (result == null) {
            fail("Retourne une instance null");
        }

    }
}