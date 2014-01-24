/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.services.ServiceMailNotifier;
import testgeneraux.DemarrageTest;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author clem
 */
public class TacheEnvoyerMailTest {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

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
        DemarrageTest demarrageTest = new DemarrageTest();
        demarrageTest.setUp();

    }

    @After
    public void tearDown() {
    }

//    /**
//     * Test of getPropertiesMail method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testGetPropertiesMail() {
//        System.out.println("getPropertiesMail");
//        TacheEnvoyerMail instance = new TacheEnvoyerMail();
//        Properties expResult = null;
//        Properties result = instance.getPropertiesMail();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of setPropertiesMail method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testSetPropertiesMail() {
//        System.out.println("setPropertiesMail");
//        Properties propertiesMail = null;
//        TacheEnvoyerMail instance = new TacheEnvoyerMail();
//        instance.setPropertiesMail(propertiesMail);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getToMailAdresses method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testGetToMailAdresses() {
//        System.out.println("getToMailAdresses");
//        TacheEnvoyerMail instance = new TacheEnvoyerMail();
//        InternetAddress[] expResult = null;
//        InternetAddress[] result = instance.getToMailAdresses();
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of setToMailAdresses method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testSetToMailAdresses() {
//        System.out.println("setToMailAdresses");
//        InternetAddress[] toMailAdresses = null;
//        TacheEnvoyerMail instance = new TacheEnvoyerMail();
//        instance.setToMailAdresses(toMailAdresses);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getSubject method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testGetSubject() {
//        System.out.println("getSubject");
//        TacheEnvoyerMail instance = new TacheEnvoyerMail();
//        String expResult = "";
//        String result = instance.getSubject();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of setSubject method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testSetSubject() {
//        System.out.println("setSubject");
//        String subject = "";
//        TacheEnvoyerMail instance = new TacheEnvoyerMail();
//        instance.setSubject(subject);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getContent method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testGetContent() {
//        System.out.println("getContent");
//        TacheEnvoyerMail instance = new TacheEnvoyerMail();
//        String expResult = "";
//        String result = instance.getContent();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of setContent method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testSetContent() {
//        System.out.println("setContent");
//        String content = "";
//        TacheEnvoyerMail instance = new TacheEnvoyerMail();
//        instance.setContent(content);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getTypeIncident method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testGetTypeIncident() {
//        System.out.println("getTypeIncident");
//        TacheEnvoyerMail instance = new TacheEnvoyerMail();
//        Class expResult = null;
//        Class result = instance.getTypeIncident();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of gererIncident method, of class TacheEnvoyerMail.
     */
    @Test
    public void testGererIncident() throws Exception {
        System.out.println("gererIncident");
        TacheEnvoyerMail instance = new TacheEnvoyerMail();
        instance.setContent("truc");



        testEnvoyerUnmail();
        instance.setExeption(new Exception());

        instance.gererIncident();
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    @Test
    public void testEnvoyerUnmail() throws AddressException {
        TacheEnvoyerMail envoyerMail = (TacheEnvoyerMail) TacheFactory.getInstance().getNewTask(TacheEnvoyerMail.class, false);

        envoyerMail.setContent("truc");
        envoyerMail.setSubject("Truc");

        InternetAddress[] to = new InternetAddress[1];
        to[0] = new InternetAddress("clement.rillon@gmail.com");
        envoyerMail.setToMailAdresses(to);
        envoyerMail.setPropertiesMail(ServiceMailNotifier.getInstance().getPropertiesMail());

        ExecutorService es = Executors.newSingleThreadExecutor();
        Future fut = es.submit(envoyerMail);
        try {
            fut.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            fail("Impossible d'envoyer un mai l? ");
        }
        if (envoyerMail.getExeption() != null) {
            fail("erreur lors de l'envoie du mail une exeption : " + envoyerMail.getExeption());
        }
    }

    @Test
    public void testSaveMessageToFile() throws MessagingException {
        try {
            TacheEnvoyerMail alerteMail = new TacheEnvoyerMail();
            Properties propertiesMail = ServiceMailNotifier.getInstance().getPropertiesMail();

            final String username = propertiesMail.getProperty("smtpuser");
            final String password = propertiesMail.getProperty("smtppass");
            Session session = Session.getInstance(propertiesMail,
                    new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message msg = new MimeMessage(session);
            try {
                boolean retour = alerteMail.saveMessageToFile(msg);
                if (retour == true) {
                    fail("ce mail n'aurait pas du être inscrit sur le disque car il est incorrecpte (no content)");
                }
            } catch (Exception e) {
                logger.debug("Exception ", e);
                fail("erreur lors de l'enregistrement !!");
            }

            msg.setContent("Ceci est un contenu", "text/html; charset=utf-8");
            msg.setSubject("Sujet");
            msg.setFrom(new InternetAddress("clement.rillon@gmail.com"));

            InternetAddress[] addresses = new InternetAddress[]{
                new InternetAddress("clement.rillon@gmail.com")
            };
            msg.setRecipients(Message.RecipientType.TO, addresses);
            alerteMail.saveMessageToFile(msg);
        } catch (Exception e){
            logger.error("Ne devait pas lever d'excpetion", e);
            fail("ne devait pas lever d'exeption");
        }
    }

    @Test
    public void testContructMailFileName() {
        System.out.println("testContructMailFileName");

        TacheEnvoyerMail alerteMail = new TacheEnvoyerMail();
        String str = alerteMail.contructMailFileName();


        if (str == null) {
            fail("ne peut être null");
        }

        if (str.isEmpty()) {
            fail("Ne peut être vide");
        }

        String str2 = alerteMail.contructMailFileName();
        String str3 = alerteMail.contructMailFileName();
        String str4 = alerteMail.contructMailFileName();
        String str5 = alerteMail.contructMailFileName();
        String str6 = alerteMail.contructMailFileName();
        String str7 = alerteMail.contructMailFileName();

        if (str.equals(str2) || str2.equals(str3) || str3.equals(str4) || str4.equals(str5) || str5.equals(str6) || str6.equals(str7)) {
            fail("on a lancer plusieurs fois et on a des chaines semblables...");
        }

    }
//    /**
//     * Test of fermetureIncident method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testFermetureIncident() throws Exception {
//        System.out.println("fermetureIncident");
//        TacheEnvoyerMail instance = new TacheEnvoyerMail();
//        instance.fermetureIncident();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of callCorps method, of class TacheEnvoyerMail.
//     */
//    @Test
//    public void testCallCorps() throws Exception {
//        System.out.println("callCorps");
//        TacheEnvoyerMail instance = new TacheEnvoyerMail();
//        instance.callCorps();
//        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
//    }
}