/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.search.DateTerm;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import rssagregator.beans.exception.DonneeInterneCoherente;
import rssagregator.beans.incident.CollecteIncident;

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
     * Test of getIncidentEnCours method, of class Flux.
     */
    @Test
    public void testGetIncidentEnCours() {
        System.out.println("getIncidentEnCours");
        Flux instance = new Flux();

        CollecteIncident incid1 = new CollecteIncident();
        CollecteIncident incid2 = new CollecteIncident();

        incid1.setDateFin(new Date(new Long(6000)));


        instance.getIncidentsLie().add(incid1);
        instance.getIncidentsLie().add(incid2);

        List<CollecteIncident> incidentEncours = instance.getIncidentEnCours();


        if (incidentEncours.size() != 1) {
            fail("Il ne devrait y avoir qu'un incident dans la liste, on en a trouvé : " + instance.getIncidentsLie().size());
        }

        if (!incidentEncours.get(0).equals(incid2)) {
            fail("L'indident trouvé n'est pas le bon");
        }

        int i;

        for (i = 0; i < incidentEncours.size(); i++) {
            if (incidentEncours.get(i).getDateFin() != null) {
                fail("On a trouvé une date de fin dans un incident en cours. Cette valeur devait être null");
            }
        }
    }

    @Test
    public void testReturnDerniereFluxPeriodeCaptation() {
        System.out.println("returnDerniereFluxPeriodeCaptation");

        Flux flux = getTestFlux();


        FluxPeriodeCaptation p2 = new FluxPeriodeCaptation();
        p2.setDateDebut(new DateTime(2016, 2, 1, 1, 1).toDate());
        p2.setDatefin(new DateTime(2016, 2, 3, 4, 5).toDate());
        flux.getPeriodeCaptations().add(p2);


        flux.setActive(Boolean.TRUE);
        System.out.println("Nombre de période : " + flux.getPeriodeCaptations().size());

        FluxPeriodeCaptation last = flux.returnDerniereFluxPeriodeCaptation();
        if (!last.equals(p2)) {
            fail("Le dernier flux devait être le 2016");
        }
    }

    @Test
    public void testReturnCaptationDuration() {
        
        Flux fl = getTestFlux();
        try {
            Long retour = fl.returnCaptationDuration();
            if(retour == null || retour<0){
                fail("valeur incorrect");
            }
            
            System.out.println("NOMB SECONDE : " + retour);
            
        } catch (DonneeInterneCoherente ex) {
            fail("ne devait pas générer d'exception");
//            Logger.getLogger(FluxTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * Test of getIncidentEnCours method, of class Flux.
     */
    @Test
    public void testGetOpml() {
    }

    /**
     * *
     * Retourne un flux intéressant pour faire des test dessus
     *
     * @return
     */
    public static Flux getTestFlux() {

        Flux flux = new Flux();

        FluxPeriodeCaptation p1 = new FluxPeriodeCaptation();
        p1.setDateDebut(new DateTime(2010, 1, 1, 1, 1).toDate());
        p1.setDatefin(new DateTime(2010, 2, 1, 1, 1).toDate());
        flux.getPeriodeCaptations().add(p1);


        FluxPeriodeCaptation p2 = new FluxPeriodeCaptation();
        p2.setDateDebut(new DateTime(2011, 2, 1, 1, 1).toDate());
        p2.setDatefin(new DateTime(2011, 2, 3, 4, 5).toDate());
        flux.getPeriodeCaptations().add(p2);
        return flux;
    }
}