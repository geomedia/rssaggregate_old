/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author clem
 */
public class RequesterTest {

    public RequesterTest() {
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
     * Test of requete method, of class Requester.
     */
    @Test
    public void testRequete() throws Exception {
        System.out.println("######################\n test Requete()");
        String urlArg = "http://rss.lemonde.fr/c/205/f/3050/index.rss";
        Requester instance = new Requester();
        instance.requete(urlArg);
        if (instance.getHttpStatut() != 200) {
            fail("On a requeté google et le retour n'est pas 200...");
        }
        else{
            System.out.println("Code retour  : " + instance.getHttpStatut());
        }
        if (instance.getHttpStatut() == null || instance.getHttpResult().isEmpty()) {
            fail("Le contenu retourné es null");
        }
        

        // On regarde le retour
        
       
//        
////        
//        DocumentBuilderFactory builderFactory = new DocumentBuilderFactoryImpl();
//        DocumentBuilder builder = builderFactory.newDocumentBuilder();
//        builder.parse(instance.getHttpResult());
        
//        Document doc = builder.parse(instance.getInputStream());
//
//        DOMSource domSource = new DOMSource(doc);
//        StringWriter writer = new StringWriter();
//        StreamResult result = new StreamResult(writer);
//        TransformerFactory tf = TransformerFactory.newInstance();
//        Transformer transformer = tf.newTransformer();
//        transformer.transform(domSource, result);
//        System.out.println("XML IN String format is: \n" + writer.toString());
//
//        System.out.println("#################################################");
    }

    /**
     * Test of getDefaulfInstance method, of class Requester.
     */
    @Test
    public void testGetDefaulfInstance() {
        System.out.println("######################\ngetDefaulfInstance");
        Requester expResult = null;
        AbstrRequesteur result = Requester.getDefaulfInstance();
//        assertEquals(expResult, result);
        if(result ==null){
            fail("retourne une instance null");
        }
        System.out.println("#############################");
    }


}
