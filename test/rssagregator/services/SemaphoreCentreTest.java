/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import rssagregator.beans.Flux;
import rssagregator.beans.Item;

/**
 *
 * @author clem
 */
public class SemaphoreCentreTest {

    public SemaphoreCentreTest() {
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
     * Test of returnSemaphoreForRessource method, of class SemaphoreCentre.
     */
    @Test
    public void testReturnSemaphoreForRessource() throws NullPointerException, IllegalAccessException {
        System.out.println("returnSemaphoreForRessource");
        Object o = null;
        Flux f1 = new Flux();
        f1.setID(new Long(1));
        Flux f2 = new Flux();
        f2.setID(new Long(2));
        Flux f3 = new Flux();
        f3.setID(new Long(3));


        SemaphoreCentre instance = new SemaphoreCentre();

        Object o1 = instance.returnSemaphoreForRessource(f1);
        Object o2 = instance.returnSemaphoreForRessource(f2);
        Object o3 = instance.returnSemaphoreForRessource(f3);
        if (o1 == o2 || o2 == o3) {
            fail("Les trois semaphore devait être semblables");
        }


        Flux f1bis = new Flux();
        f1bis.setID(new Long(1));

        Object of1bis = instance.returnSemaphoreForRessource(f1bis);
        if (o1 != of1bis) {
            fail("Les semaphore devait être == ");
        }

        Item it = new Item();
        it.setID(new Long(1));
        Object oit = instance.returnSemaphoreForRessource(it);

        if (oit == o1 || oit == o2 || oit == of1bis) {
            fail("On ne devait pas avoir la même semaphore pou run item ou pour un flux.");
        }

        Object objetQuelquonque = new Object();

        try {
            Object retu = instance.returnSemaphoreForRessource(objetQuelquonque);
            fail("devait lever une exception car le l'objet n'est pas un beans respectant les règles du projet");
        } catch (Exception e) {
            System.out.println("Execption normale");
        }

        try {
            instance.returnSemaphoreForRessource(null);
            fail("doit lever une exception");
        } catch (Exception e) {
        }



    }

    /**
     * Test of returnSemaphoreForRessource method, of class SemaphoreCentre.
     */
    @Test
    public void menage() throws NullPointerException, IllegalAccessException, InterruptedException {
        System.out.println("TestMenage");

        // On crée quelques objet a enregistrer dans le semacentre
        Object o = null;
        Flux f1 = new Flux();
        f1.setID(new Long(1));
        Flux f2 = new Flux();
        f2.setID(new Long(2));
        Flux f3 = new Flux();
        f3.setID(new Long(3));


        SemaphoreCentre instance = new SemaphoreCentre();
        instance.setNbWaitTimeForClean(1); // le menage sera effectué toute les seconde

        Object o1 = instance.returnSemaphoreForRessource(f1);
        Object o2 = instance.returnSemaphoreForRessource(f2);
        Object o3 = instance.returnSemaphoreForRessource(f3);
        System.out.println("Nombre de sema avant menage " + instance.mapSemaphore.size());

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit(instance);

        instance.declancherMenage();


        try { // On attend que le menage soit fait
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        // Maintenant on compte le nombre de semaphore dans la map. Elle doit être vide
        Map<String, Semaphore> map = instance.mapSemaphore.get(Flux.class);

        if (map.size() > 0) {
            fail("Normalement il aurait du y avoir du menage il reste " + map.size());
        }

        // Maintenant on tente un autre test. l'une des semaphore ne doit pas être supprimée car elle est déja accédée
        Semaphore s1 = instance.returnSemaphoreForRessource(f1);
        s1.acquire();
        
        instance.declancherMenage();
//        es.submit(instance);
        map = instance.mapSemaphore.get(Flux.class);

        if (map.size() != 1) {
            fail("Normalement il devait rester une sem dans la map");
        } else {
            System.out.println("OK il reste " + map.size() + " dans la map");
        }

        // on release
        s1.release();
        
        // On déclanche le menage et on attend qu'il soit fait
        instance.declancherMenage();
        Thread.sleep(100);
                
        
        // On compte
        if(map.size()!= 0){
            fail("La map devait vide il reste " + map.size() );
        }
        else{
            System.out.println("La map est bien vide");
        }

        es.shutdownNow();

    }
//    /**
//     * Test of run method, of class SemaphoreCentre.
//     */
//    @Test
//    public void testRun() {
//        System.out.println("run");
//        SemaphoreCentre instance = new SemaphoreCentre();
//        instance.run();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}