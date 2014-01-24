/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import org.apache.commons.math3.random.ISAACRandom;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.beans.Flux;

/**
 *
 * @author clem
 */
public class VisitorHTTPTest {
    
    public VisitorHTTPTest() {
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
     * Test of visit method, of class VisitorHTTP.
     */
    @Test
    public void testVisit() throws Exception {
        System.out.println("visit");
                Flux flux = new Flux();
        flux.setUrl("http://rss.lemonde.fr/c/205/f/3050/index.rss");
        
                MediatorCollecteAction comportement = MediatorCollecteAction.getDefaultCollectAction();
                flux.setMediatorFlux(comportement);
                
        
        VisitorHTTP instance = new VisitorHTTP();
        instance.visit(flux);
        
        
        if(instance.getListItem() == null){
            fail("La liste ne doit pas être null");
        }

        if(instance.getListItem() != null && instance.getListItem().isEmpty()){
            fail("Le flux du monde ne devrait pas être null");
        }
        else{
                System.out.println("Le visitor a récolté : " + instance.getListItem().size());
        }
            
        
 
    }
}