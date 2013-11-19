/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
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
public class CacheHashFluxTest {

    public CacheHashFluxTest() {
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
     * Test of getInstance method, of class CacheHashFlux.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        CacheHashFlux expResult = null;
        CacheHashFlux result = CacheHashFlux.getInstance();
        if (result == null) {
            fail("Getinstance ne doit pas renvoyer un résultat null");
        }
        if (result != null && !result.getClass().equals(CacheHashFlux.class)) {
            fail("Getinstance ne devrait pas renvoyer un élément de ce type");
        }


    }

//    /**
//     * Test of ChargerLesHashdesFluxdepuisBDD method, of class CacheHashFlux.
//     */
//    @Test
//    public void testChargerLesHashdesFluxdepuisBDD() {
//        System.out.println("ChargerLesHashdesFluxdepuisBDD");
//        CacheHashFlux instance = new CacheHashFlux();
//        instance.ChargerLesHashdesFluxdepuisBDD();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of returnLashHash method, of class CacheHashFlux.
     */
    @Test
    public void testReturnLashHash() {
        System.out.println("returnLashHash");
        Flux flux = new Flux();
        flux.setID(new Long(1));
        CacheHashFlux instance = new CacheHashFlux();

        instance.addHash(flux, "86c477244852dd686ee7a3124f5152d0");
        instance.addHash(flux, "86c477244852dd686ee7a3124f5152d1");
        instance.addHash(flux, "86c477244852dd686ee7a3124f5152d3");

        Set<String> assertSet = new HashSet<String>();
        assertSet.add("86c477244852dd686ee7a3124f5152d0");
        assertSet.add("86c477244852dd686ee7a3124f5152d1");
        assertSet.add("86c477244852dd686ee7a3124f5152d3");

        Set expResult = assertSet;
        Set result = instance.returnLashHash(flux);
        assertEquals(expResult, result);



        Flux f2 = new Flux();
        f2.setID(new Long(55));
        Set<String> setreturn = instance.returnLashHash(f2);
        if (setreturn != null) {
            fail("On attendait ici un retour null");
        }

        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of addHash method, of class CacheHashFlux.
     */
    @Test
    public void testAddHash() {
        System.out.println("addHash");
        Flux flux = new Flux();
        flux.setID(new Long(5));


        String hash = "28953b9f664cabc2dea4bea1de2619tt";
        CacheHashFlux instance = new CacheHashFlux();
        instance.addHash(flux, "28953b9f664cabc2dea4bea1de2619tt");
        instance.addHash(flux, "28953b9f664cabc2dea4bea1de2619t2");
        instance.addHash(flux, "28953b9f664cabc2dea4bea1de2619t3");
        instance.addHash(flux, "");
        instance.addHash(flux, null);
        instance.addHash(null, null);

        Set<String> setresu = instance.cacheHash.get(flux);
        if (setresu.size() != 3) {
            fail("la taille du resu devait être 3");
        }

    }

    /**
     * Test of addAll method, of class CacheHashFlux.
     */
    @Test
    public void testAddAll() {
        System.out.println("addAll");

        Flux flux = new Flux();
        flux.setID(new Long(1));

        Set<String> setHash = new HashSet<String>();
        setHash.add("28953b9f664cabc2dea4bea1de261918");
        setHash.add("86c477244852dd686ee7a3124f5152d0");
        setHash.add("86c477244852dd686ee7a3124f5152d0");
        setHash.add("");
        setHash.add(null);

        CacheHashFlux instance = new CacheHashFlux();
        instance.addAll(flux, setHash);

        if (instance.cacheHash.size() != 1) {
            fail("Size!=1 il devrait pourtant n'y avoir qu'un flux");
        }


        Set<String> setcontenu = instance.cacheHash.get(flux);
        if (setcontenu.size() != 2) {
            fail("Il devrait y avoir deux hash pour ce flux dans le cache");
        }

        Set<String> setHash2 = new HashSet<String>();
        setHash2.add("f040cd212572c7ff93efa21920f3a6ae");
        instance.addAll(flux, setHash2);

        if (instance.cacheHash.size() != 1) {
            fail("Addall aurait du ajouter au flux existant");
        }

        Set<String> set = instance.cacheHash.get(flux);
        if (set.size() != 3) {
            fail("La taille devrait maintenant être de 3");
        }
    }

    /**
     * Test of reomveHash method, of class CacheHashFlux.
     */
    @Test
    public void testReomveHash() {
        System.out.println("reomveHash");
        Flux flux = new Flux();
        flux.setID(new Long(1));
        CacheHashFlux instance = new CacheHashFlux();
        instance.addHash(flux, "4f5b710694b0ff5184449fb501bfb35b");
        instance.addHash(flux, "5G5b710694b0ff5184449fb501bfb35b");

        instance.reomveHash(flux, "5G5b710694b0ff5184449fb501bfb35b");
        instance.reomveHash(flux, "");
        instance.reomveHash(flux, null);

        Set<String> set = instance.cacheHash.get(flux);
        if (set.size() != 1) {
            fail("La taille devrait être de 1");
        }
        if (!set.contains("4f5b710694b0ff5184449fb501bfb35b")) {
            fail("Le hash 4f5b710694b0ff5184449fb501bfb35b devrait être contenu");
        }

        // Test ajout de valeur null
        instance.addAll(null, null);
        instance.addAll(null, set);
        instance.addAll(flux, null);
    }
//     public void testRemoveFlux() {
//         
//         
//         
//     }

    /**
     * Test of removeFlux method, of class CacheHashFlux.
     */
    @Test
    public void testRemoveFlux() {
        System.out.println("removeFlux");
        Flux flux = new Flux();
        flux.setID(new Long(50));
        CacheHashFlux instance = new CacheHashFlux();
        instance.addHash(flux, "AAAA");
        Boolean expResult = true;
        Boolean result = instance.removeFlux(flux);
        assertEquals(expResult, result);
        assertEquals(false, instance.removeFlux(null));
        assertEquals(false, instance.removeFlux(new Flux()));
        Flux fl = new Flux();
        fl.setID(new Long(5555));
        assertEquals(false, instance.removeFlux(fl));
    }

    /**
     * Test of ChargerLesHashdesFluxdepuisBDD method, of class CacheHashFlux.
     */
    @Test
    public void testChargerLesHashdesFluxdepuisBDD() {
        System.out.println("ChargerLesHashdesFluxdepuisBDD");
        CacheHashFlux instance = new CacheHashFlux();
        instance.ChargerLesHashdesFluxdepuisBDD();
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of removeXHash method, of class CacheHashFlux.
     */
    @Test
    public void testRemoveXHash() {

        System.out.println("removeXHash");
        int x = 0;
        Flux flux = new Flux();
        CacheHashFlux instance = new CacheHashFlux();


        flux.setID(new Long(1));
        instance.addHash(flux, "hash1");
        instance.addHash(flux, "hash2");
        instance.addHash(flux, "hash3");
        instance.addHash(flux, "hash4");
        instance.addHash(flux, "hash5");


        instance.removeXHash(2, flux);
        
       Set<String> returnSet =  instance.returnLashHash(flux);
       Set<String> expectSet = new TreeSet<String>();
       
       expectSet.add("hash3");
       expectSet.add("hash4");
       expectSet.add("hash5");
       
       assertEquals(returnSet, expectSet);
       
       // On essai maintenant de retirer plus qu'il n'y a. Puis on test les valeur incohérente. Ne doit lever aucune exception simplement pas faire de traitement.
       instance.removeXHash(20, flux);
       instance.removeXHash(null, null);
       instance.removeXHash(-1, null);
       instance.removeXHash(-1, flux);
       
    }

    /**
     * Test of main method, of class CacheHashFlux.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        CacheHashFlux.main(args);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}