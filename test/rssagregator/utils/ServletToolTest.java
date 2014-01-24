/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.dao.AbstrDao;
import rssagregator.servlet.FluxSrvl;


/**
 *
 * @author clem
 */
public class ServletToolTest {

    public ServletToolTest() {
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
//     * Test of configAction method, of class ServletTool.
//     */
//    @Test
//    public void testConfigAction() {
//        System.out.println("configAction");
//        HttpServletRequest request = null;
//        String defaultAction = "";
//        String expResult = "";
//        String result = ServletTool.configAction(request, defaultAction);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of redir method, of class ServletTool.
//     */
//    @Test
//    public void testRedir() {
//        System.out.println("redir");
//        HttpServletRequest request = null;
//        String url = "";
//        String msg = "";
//        Boolean err = null;
//        ServletTool.redir(request, url, msg, err);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of accesControl method, of class ServletTool.
//     */
//    @Test
//    public void testAccesControl() {
//        System.out.println("accesControl");
//        HttpServletRequest request = null;
//        boolean expResult = false;
//        boolean result = ServletTool.accesControl(request);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of getListFluxFromRequest method, of class ServletTool.
//     */
//    @Test
//    public void testGetListFluxFromRequest() {
//        System.out.println("getListFluxFromRequest");
//        HttpServletRequest request = null;
//        AbstrDao dao = null;
//        List expResult = null;
//        List result = ServletTool.getListFluxFromRequest(request, dao);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of actionLIST method, of class ServletTool.
     */
//    @Test
//    public void testActionLIST() {
//        System.out.println("actionLIST");
//        HttpServletRequest request = null;
//        Class beansClass = null;
//        String beansnameJSP = "";
//        AbstrDao dao = null;
//        ServletTool.actionLIST(request, beansClass, beansnameJSP, dao);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of actionREAD method, of class ServletTool.
//     */
//    @Test
//    public void testActionREAD() {
//        System.out.println("actionREAD");
//        HttpServletRequest request = null;
//        Class beansClass = null;
//        String beansnameJSP = "";
//        ServletTool.actionREAD(request, beansClass, beansnameJSP);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of actionMOD method, of class ServletTool.
//     */
//    @Test
//    public void testActionMOD() {
//        System.out.println("actionMOD");
//        HttpServletRequest request = null;
//        String beansname = "";
//        String formNameJSP = "";
//        Class beansClass = null;
//        Boolean notifiObserver = null;
//        ServletTool.actionMOD(request, beansname, formNameJSP, beansClass, notifiObserver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of actionADD method, of class ServletTool.
     */
//    @Test
//    public void testActionADD() {
//        System.out.println("actionADD");
//        HttpServletRequest request = null;
//        String beansnameJSP = "";
//        String formNameJSP = "";
//        Class beansClass = null;
//        Boolean notifiObserver = null;
//        ServletTool.actionADD(request, beansnameJSP, formNameJSP, beansClass, notifiObserver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of actionADD2 method, of class ServletTool.
//     */
//    @Test
//    public void testActionADD2() {
//        System.out.println("actionADD2");
//        HttpServletRequest request = null;
//        String beansnameJSP = "";
//        String formNameJSP = "";
//        Class beansClass = null;
//        Boolean notifiObserver = null;
//        ServletTool.actionADD2(request, beansnameJSP, formNameJSP, beansClass, notifiObserver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of actionREM method, of class ServletTool.
//     */
//    @Test
//    public void testActionREM() {
//        System.out.println("actionREM");
//        HttpServletRequest request = null;
//        Class beansClass = null;
//        Boolean notifiObserver = null;
//        ServletTool.actionREM(request, beansClass, notifiObserver);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of parseidFromRequest method, of class ServletTool.
     */
    @Test
    public void testParseidFromRequest() throws IOException {
        System.out.println("parseidFromRequest");

        //--------------Test Form id=2,3
        ServletRunner sr = new ServletRunner();
        sr.registerServlet("myServlet", FluxSrvl.class.getName());
        ServletUnitClient sc = sr.newClient();
        WebRequest request = new PostMethodWebRequest("http://localhost//RSSAgregate/flux/maj?id=2,3");
        request.setParameter("nom", "Le monde");
        InvocationContext ic = sc.newInvocation(request);
        HttpServletRequest req = ic.getRequest();

        List<Long> listExpect = new ArrayList<Long>();
        listExpect.add(new Long(2));
        listExpect.add(new Long(3));
        List expResult = listExpect;
//        List result = ServletTool.parseidFromRequest(req);
        assertEquals(expResult, ServletTool.parseidFromRequest(req,null));

//        for (int i = 0; i < result.size(); i++) {
//            Object object = result.get(i);
//            System.out.println("resu : " + object);
//        }


        //------------Form id=1,id=2
         request = new PostMethodWebRequest("http://localhost//RSSAgregate/flux/maj?id=2,3");
        request.setParameter("nom", "Le monde");
        ic = sc.newInvocation(request);
        req = ic.getRequest();
        
        assertEquals(expResult, ServletTool.parseidFromRequest(req, null));
        
        
        //Forme id=1
        request = new PostMethodWebRequest("http://localhost//RSSAgregate/flux/maj?id=1");
        ic = sc.newInvocation(request);
        req = ic.getRequest();
        expResult = new ArrayList();
        expResult.add(new Long(1));
        assertEquals(expResult, ServletTool.parseidFromRequest(req, null));
        
        
        //En cas de null
        request = new PostMethodWebRequest("http://localhost//RSSAgregate/flux/maj");
        ic = sc.newInvocation(request);
        req = ic.getRequest();
        expResult = new ArrayList();
        expResult.add(new Long(1));
//        assertEquals(expResult, ServletTool.parseidFromRequest(req));
        try {
           List<Long> resu =  ServletTool.parseidFromRequest(req,null);
//            System.out.println("resu : " + resu.get(0));
//             System.out.println("------FIN");
        } catch (Exception e) {
            System.out.println("Execption");
            if(!e.getClass().equals(NoResultException.class)){
                fail("devait lever un no result exception");
            }
        }
       
//        NoResultException
        



//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}