/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.beans.Bean;
import rssagregator.beans.Flux;

/**
 *
 * @author clem
 */
public class ComparatorBeanTest {

    public ComparatorBeanTest() {
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
     * Test of compare method, of class ComparatorBean.
     */
    @Test
    public void testCompare() {


        Flux f1 = new Flux();
        Flux f2 = new Flux();


        f1.setID(new Long(1));
        f2.setID(new Long(2));

        ComparatorBean instance = new ComparatorBean();
        int expResult = -1;
        int result = instance.compare(f1, f2);
        assertEquals(expResult, result);


        // Si les deux sont null retour = 0
        f1.setID(null);
        f2.setID(null);
        result = instance.compare(f1, f2);
        expResult = 0;
        assertEquals(expResult, result);
        
        

        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");

        
        
        f2.setID(new Long(999));
        Flux f3 = new Flux();
        Flux f4 = new Flux();
        f4.setID(new Long(5));
        Flux f5 = new Flux();
        f5.setID(new Long(1));
        Flux f6 = new Flux();
        Flux f7 = new Flux();
        f7.setID(new Long(500));
        Flux f8 = new Flux();
        Flux f9 = new Flux();
        
        List<Flux> lf = new ArrayList<Flux>();
        lf.add(f1);
        lf.add(f2);
        lf.add(f3);
        lf.add(f4);
        lf.add(f5);
        lf.add(f6);
        lf.add(f7);
        lf.add(f8);
        lf.add(f9);
        
        
        Collections.sort(lf, new ComparatorBean());
        
        for (int i = 0; i < lf.size(); i++) {
            Flux flPrec = null;
            Flux flux = lf.get(i);
            
            if(i>0){
                flPrec = lf.get(i-1);
            }
            
            System.out.println("ID : " + flux.getID());
            
        }
//        
//        

//        
//        
//        
//        System.out.println("compare");
//        Bean o1 = null;
//        Bean o2 = null;
//        ComparatorBean instance = new ComparatorBean();
//        int expResult = 0;
//        int result = instance.compare(o1, o2);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}