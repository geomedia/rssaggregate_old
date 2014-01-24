/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import javax.servlet.ServletContextEvent;
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
public class StartServletTest {
    
    public StartServletTest() {
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
     * Test of contextInitialized method, of class StartServlet.
     */
    @Test
    public void testContextInitialized() {
        System.out.println("contextInitialized");
        ServletContextEvent sce = null;
        StartServlet instance = new StartServlet();
        instance.contextInitialized(sce);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of contextDestroyed method, of class StartServlet.
     */
    @Test
    public void testContextDestroyed() {
        System.out.println("contextDestroyed");
        ServletContextEvent sce = null;
        StartServlet instance = new StartServlet();
        instance.contextDestroyed(sce);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of destroyDriver method, of class StartServlet.
     */
    @Test
    public void testDestroyDriver() {
        System.out.println("destroyDriver");
        StartServlet instance = new StartServlet();
        instance.destroyDriver();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}