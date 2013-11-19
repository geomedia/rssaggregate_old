/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.beans.Journal;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoJournal;

/**
 *
 * @author clem
 */
public class TacheDecouverteAjoutFluxTest {
    
    public TacheDecouverteAjoutFluxTest() {
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
     * Test of call method, of class TacheDecouverteAjoutFlux.
     */
    @Test
    public void testCall() throws Exception {
        System.out.println("call");
        TacheDecouverteAjoutFlux instance = new TacheDecouverteAjoutFlux();
        
        

        
        DaoJournal daoj = DAOFactory.getInstance().getDaoJournal();
        Journal j = (Journal) daoj.find(new Long(1));
        
        
        instance.setJournal(j);

        instance.setNombredeSousTache(50);
        instance.call();
        
        
//        TacheDecouverteAjoutFlux expResult = null;
//        TacheDecouverteAjoutFlux result = instance.call();
        
        
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }




}