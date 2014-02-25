/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.eclipse.persistence.tools.file.FileUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.beans.Flux;
import rssagregator.dao.SearchFiltersList;

/**
 *
 * @author clem
 */
public class CSVMackerTest {

    public CSVMackerTest() {
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
//     * Test of call method, of class CSVMacker.
//     */
//    @Test
//    public void testCall() throws Exception {
//        System.out.println("call");
//        CSVMacker instance = null;
//        Object expResult = null;
//        Object result = instance.call();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of main method, of class CSVMacker.
//     */
//    @Test
//    public void testMain() {
//        System.out.println("main");
//        String[] args = null;
//        CSVMacker.main(args);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of assemblageFichier method, of class CSVMacker.
     */
    @Test
    public void testAssemblageFichier() {

        String dir = "/home/clem/TMP/tata/";
        String c1 = "A";
        String c2 = "B";
        String c3 = "C";

        File f1 = new File(dir + "toto1");
        File f2 = new File(dir + "toto2");
        File f3 = new File(dir + "toto3");
        try {
            FileUtils.write(f1, c1);
            FileUtils.write(f2, c2);
            FileUtils.write(f3, c3);

        } catch (IOException ex) {
            fail();
            Logger.getLogger(CSVMackerTest.class.getName()).log(Level.SEVERE, null, ex);
        }


        CSVMacker instance = new CSVMacker(dir);
        instance.assemblageFichier("toto", dir);

        // Il doit rester le fichier 3 avec pour contenu abc

        try {
//            f1.canRead();
            if (f2.exists() || (f3.exists())) {
                fail("Les fichiers 2 et 3 ne doivent pas exister");
            }
            if(!f1.exists()){
                fail("Le fichier 1 doit exister");
            }
        } catch (Exception e) {
            fail();
        }
        
        
        
        
        try {
            // On lit le fichier 3 qui doit contenir abc
          String contenu =  FileUtils.readFileToString(f1);
          if(!contenu.equals("ABC")){
              fail("devait contenir ABC mais contient : " + contenu);
          }
        } catch (IOException ex) {
            Logger.getLogger(CSVMackerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
//    /**
//     * Test of getFiltre method, of class CSVMacker.
//     */
//    @Test
//    public void testGetFiltre() {
//        System.out.println("getFiltre");
//        CSVMacker instance = null;
//        SearchFiltersList expResult = null;
//        SearchFiltersList result = instance.getFiltre();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFiltre method, of class CSVMacker.
//     */
//    @Test
//    public void testSetFiltre() {
//        System.out.println("setFiltre");
//        SearchFiltersList filtre = null;
//        CSVMacker instance = null;
//        instance.setFiltre(filtre);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFluxDemande method, of class CSVMacker.
//     */
//    @Test
//    public void testGetFluxDemande() {
//        System.out.println("getFluxDemande");
//        CSVMacker instance = null;
//        List expResult = null;
//        List result = instance.getFluxDemande();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFluxDemande method, of class CSVMacker.
//     */
//    @Test
//    public void testSetFluxDemande() {
//        System.out.println("setFluxDemande");
//        List<Flux> fluxDemande = null;
//        CSVMacker instance = null;
//        instance.setFluxDemande(fluxDemande);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDate1 method, of class CSVMacker.
//     */
//    @Test
//    public void testGetDate1() {
//        System.out.println("getDate1");
//        CSVMacker instance = null;
//        Date expResult = null;
//        Date result = instance.getDate1();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDate1 method, of class CSVMacker.
//     */
//    @Test
//    public void testSetDate1() {
//        System.out.println("setDate1");
//        Date date1 = null;
//        CSVMacker instance = null;
//        instance.setDate1(date1);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDate2 method, of class CSVMacker.
//     */
//    @Test
//    public void testGetDate2() {
//        System.out.println("getDate2");
//        CSVMacker instance = null;
//        Date expResult = null;
//        Date result = instance.getDate2();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDate2 method, of class CSVMacker.
//     */
//    @Test
//    public void testSetDate2() {
//        System.out.println("setDate2");
//        Date date2 = null;
//        CSVMacker instance = null;
//        instance.setDate2(date2);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRedirPath method, of class CSVMacker.
//     */
//    @Test
//    public void testGetRedirPath() {
//        System.out.println("getRedirPath");
//        CSVMacker instance = null;
//        String expResult = "";
//        String result = instance.getRedirPath();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRedirPath method, of class CSVMacker.
//     */
//    @Test
//    public void testSetRedirPath() {
//        System.out.println("setRedirPath");
//        String redirPath = "";
//        CSVMacker instance = null;
//        instance.setRedirPath(redirPath);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isRafine method, of class CSVMacker.
//     */
//    @Test
//    public void testIsRafine() {
//        System.out.println("isRafine");
//        CSVMacker instance = null;
//        boolean expResult = false;
//        boolean result = instance.isRafine();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRafine method, of class CSVMacker.
//     */
//    @Test
//    public void testSetRafine() {
//        System.out.println("setRafine");
//        boolean rafine = false;
//        CSVMacker instance = null;
//        instance.setRafine(rafine);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}