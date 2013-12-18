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
import rssagregator.beans.Flux;
import rssagregator.beans.Journal;

/**
 *
 * @author clem
 */
public class ExceptionToolTest {
    
    public ExceptionToolTest() {
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
//     * Test of argumentNonNull method, of class ExceptionTool.
//     */
//    @Test
//    public void testArgumentNonNull() {
//        System.out.println("argumentNonNull");
//        Object o = null;
//        ExceptionTool.argumentNonNull(o);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of checkClass method, of class ExceptionTool.
//     */
//    @Test
//    public void testCheckClass() {
//        System.out.println("checkClass");
//        Object o = null;
//        Class c = null;
//        ExceptionTool.checkClass(o, c);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
    /**
     * Test of checkNonNullField method, of class ExceptionTool.
     */
    @Test
    public void testCheckNonNullField() throws Exception {
        System.out.println("checkNonNullField");
        Flux f = new Flux();
        Journal j = new Journal();
        f.setID(new Long(115));
        f.setJournalLie(j);
        f.setUrl("http://test.com");
        
        try {
            ExceptionTool.checkNonNullField(f, "ID");
        } catch (Exception e) {
            
            fail("Ne devait pas lever d'exception");
        }
        
        try {
            ExceptionTool.checkNonNullField(f, "url");
        } catch (Exception e) {
            fail("Ne devait pas lever d'exception");
        }
        
        try {
            ExceptionTool.checkNonNullField(f, "journalLie");
        } catch (Exception e) {
            fail("Ne devait pas lever d'exception");
        }
        
        
        try {
            ExceptionTool.checkNonNullField(f, "zouzou");
            fail("devait lever une exception");
        } catch (Exception e) {
            if(!e.getClass().equals(IllegalAccessException.class)){
                fail("Devail lever une IllegalAccessException");
            }
        }
        
        
        try {
            ExceptionTool.checkNonNullField(f, "nom");
            fail("devait lever une exception");
        } catch (Exception e) {
            
            if (!e.getClass().equals(NullPointerException.class)) {
                fail("Devait lever un null pointeur exception");
            }
        }
    }
}