/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.util.Date;
import java.util.List;
import java.util.Observable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.beans.incident.FluxIncident;
import rssagregator.beans.incident.HTTPIndident;

/**
 *
 * @author clem
 */
public class FluxTest {
    
    public FluxTest() {
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
     * Test of autodiscovery method, of class Flux.
     */
    @Test
    public void testAutodiscovery() {
        System.out.println("autodiscovery");
        Flux instance = new Flux();
        instance.autodiscovery();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Obsolete_test method, of class Flux.
     */
    @Test
    public void testObsolete_test() {
        System.out.println("Obsolete_test");
        Flux instance = new Flux();
        instance.Obsolete_test();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Flux method, of class Flux.
     */
    @Test
    public void testFlux() {
        System.out.println("Flux");
        Flux instance = new Flux();
        instance.Flux();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }



 

 

    /**
     * Test of update method, of class Flux.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        Observable o = null;
        Object arg = null;
        Flux instance = new Flux();
        instance.update(o, arg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createTask method, of class Flux.
     */
    @Test
    public void testCreateTask() {
        System.out.println("createTask");
        Flux instance = new Flux();
        instance.createTask();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

  
   

    /**
     * Test of getIncidentEnCours method, of class Flux.
     */
    @Test
    public void testGetIncidentEnCours() {
        System.out.println("getIncidentEnCours");
        Flux instance = new Flux();
        
        FluxIncident incid1 = new HTTPIndident();
        FluxIncident incid2 = new HTTPIndident();
        
        incid1.setDateFin(new Date(new Long(6000)));
        
        instance.getIncident().add(incid1);
        instance.getIncident().add(incid2);
        
        List<FluxIncident> incidentEncours = instance.getIncidentEnCours();
        
        
        if(incidentEncours.size()!=1){
            fail("Il ne devrait y avoir qu'un incident dans la liste, on en a trouvé : " + instance.getIncident().size());
        }
        
        if(!incidentEncours.get(0).equals(incid2)){
            fail("L'indident trouvé n'est pas le bon");
        }
        
        int i;
        
        for(i = 0; i<incidentEncours.size(); i++){
            if(incidentEncours.get(i).getDateFin()!=null){
                fail("On a trouvé une date de fin dans un incident en cours. Cette valeur devait être null");
            }
        }
        
     
        
    }
}