/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import javax.mail.internet.InternetAddress;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author clem
 */
public class TacheEnvoyerMailTest {
    
    public TacheEnvoyerMailTest() {
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
     * Test of call method, of class TacheEnvoyerMail.
     */
    @Test
    public void testCall() throws Exception {
        System.out.println("call");
        ServiceMailNotifier serviceMail = ServiceMailNotifier.getTestInstance();
        
        TacheEnvoyerMail instance = new TacheEnvoyerMail(serviceMail);
        instance.setContent("Ceci est un mail de test");
        instance.setPropertiesMail(serviceMail.getPropertiesMail());
        instance.setSubject("TestMail");
        InternetAddress[] addresses = new InternetAddress[]{new InternetAddress("clement.rillon@gmail.com")};
        instance.setToMailAdresses(addresses);
        
        serviceMail.executorService.submit(instance);

//        serviceMail.executorService.submit(instance);
                
        TacheEnvoyerMail expResult = null;
        TacheEnvoyerMail result = instance.call();
        
        
        
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getPropertiesMail method, of class TacheEnvoyerMail.
     */
//    @Test
//    public void testGetPropertiesMail() {
//        System.out.println("getPropertiesMail");
//        TacheEnvoyerMail instance = null;
//        Properties expResult = null;
//        Properties result = instance.getPropertiesMail();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setPropertiesMail method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testSetPropertiesMail() {
//        System.out.println("setPropertiesMail");
//        Properties propertiesMail = null;
//        TacheEnvoyerMail instance = null;
//        instance.setPropertiesMail(propertiesMail);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getToMailAdresses method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testGetToMailAdresses() {
//        System.out.println("getToMailAdresses");
//        TacheEnvoyerMail instance = null;
//        InternetAddress[] expResult = null;
//        InternetAddress[] result = instance.getToMailAdresses();
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setToMailAdresses method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testSetToMailAdresses() {
//        System.out.println("setToMailAdresses");
//        InternetAddress[] toMailAdresses = null;
//        TacheEnvoyerMail instance = null;
//        instance.setToMailAdresses(toMailAdresses);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSubject method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testGetSubject() {
//        System.out.println("getSubject");
//        TacheEnvoyerMail instance = null;
//        String expResult = "";
//        String result = instance.getSubject();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSubject method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testSetSubject() {
//        System.out.println("setSubject");
//        String subject = "";
//        TacheEnvoyerMail instance = null;
//        instance.setSubject(subject);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getContent method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testGetContent() {
//        System.out.println("getContent");
//        TacheEnvoyerMail instance = null;
//        String expResult = "";
//        String result = instance.getContent();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setContent method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testSetContent() {
//        System.out.println("setContent");
//        String content = "";
//        TacheEnvoyerMail instance = null;
//        instance.setContent(content);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getTypeIncident method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testGetTypeIncident() {
//        System.out.println("getTypeIncident");
//        TacheEnvoyerMail instance = null;
//        Class expResult = null;
//        Class result = instance.getTypeIncident();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
    /**
     * Test of gererIncident method, of class TacheEnvoyerMail.
     */
    @Test
    public void testGererIncident() throws Exception {
        System.out.println("gererIncident");
        
        ServiceMailNotifier service = ServiceMailNotifier.getTestInstance();
        TacheEnvoyerMail instance = new TacheEnvoyerMail(service);
        
        instance.setExeption(new Exception("Une exeption de test"));
        instance.setContent("Un contenu qui n'a jamais existé");
        instance.setPropertiesMail(service.getPropertiesMail());
        instance.setSubject("Sujet de test");
        
        instance.gererIncident();
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
//
//    /**
//     * Test of fermetureIncident method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testFermetureIncident() throws Exception {
//        System.out.println("fermetureIncident");
//        TacheEnvoyerMail instance = null;
//        instance.fermetureIncident();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}