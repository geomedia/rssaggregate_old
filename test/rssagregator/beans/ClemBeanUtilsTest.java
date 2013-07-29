/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoJournal;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.beans.form.AbstrForm;
import rssagregator.beans.form.JournalForm;
import rssagregator.servlet.JournauxSrvl;

/**
 *
 * @author clem
 */
public class ClemBeanUtilsTest {

    public ClemBeanUtilsTest() {
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
     * Test of populate method, of class ClemBeanUtils.
     */
    @Test
    public void testPopulate() throws Exception {

//        Object bean = null;
//        HttpServletRequest request = null;
//        ClemBeanUtils.populate(bean, request);
        // TODO review the generated test code and remove the default call to fail.

        Journal j = new Journal();
        j.setNom("Le monde");
//        j.setLangue("zoulou");

        // On génère une requete en faisant passer des paramètres similaires au jounal 1 .
        ServletRunner sr = new ServletRunner();
        sr.registerServlet("myServlet", JournauxSrvl.class.getName());
        ServletUnitClient sc = sr.newClient();
        WebReques               t request = new PostMethodWebRequest("http://localhost//RSSAgregate/journaux?action=add");
        request.setParameter("nom", "Le monde");
//        request.setParameter("langue", "zoulou");

        InvocationContext ic = sc.newInvocation(request);
        HttpServletRequest req = ic.getRequest();

        Journal j2 = new Journal();
        JournalForm form = new JournalForm();
        ClemBeanUtils.populate(j2, req, form);


// On compare les deux jounaux (celui crée à la main t celui crée par la servlet
        if (!j.compareBeans(j2, false)) {
            fail("non");
        }

    }

    /**
     * Test of check method, of class ClemBeanUtils.
     */
    @Test
    public void testCheck() throws Exception {

//        DaoJournal dao = DAOFactory.getInstance().getDaoJournal();
        AbstrForm objetFormulaire = new JournalForm();

        Journal jou = new Journal();
        jou.setNom("Lemonde");

        jou.setLangue("fr");

        // test avec un beans qui a tout les champs remplies
        Map expResult = new HashMap<String, String[]>();
//        Map result = 
                ClemBeanUtils.check(objetFormulaire, jou);
                
        assertEquals(expResult, objetFormulaire.getErreurs());
        // TODO review the generated test code and remove the default call to fail.

// Test avec un champs manquant, la map contient une erreur
        jou.setLangue("");
        expResult.put("langue", new String[]{"", "Ne peut être null"});
        
                ClemBeanUtils.check(objetFormulaire, jou);
        
        System.out.println("expected");
        System.out.println("cle : " + expResult.containsKey("langue"));
        System.out.println("valeur : " + ((String[])expResult.get("langue"))[0]);
        System.out.println("erreur : "+  ((String[])expResult.get("langue"))[1]);
        System.out.println("IN");
        System.out.println("cle : " + objetFormulaire.getErreurs().containsKey("langue"));
        System.out.println("valeur : " + (objetFormulaire.getErreurs().get("langue"))[0]);
        System.out.println("erreur : "+ (objetFormulaire.getErreurs().get("langue"))[1]);

        // appel, Les map ne peuvent pas être comparé car on a des ableaux dedan.
        assertEquals((((String[])expResult.get("langue"))[1]), (objetFormulaire.getErreurs().get("langue"))[1]);
    }
}