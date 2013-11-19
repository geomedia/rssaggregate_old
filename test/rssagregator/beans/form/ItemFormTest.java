/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.DateTime;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.beans.Flux;
import rssagregator.dao.AbstrDao;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.SearchFilter;
import rssagregator.servlet.ItemSrvl;

/**
 *
 * @author clem
 */
public class ItemFormTest {

    public ItemFormTest() {
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
//     * Test of bind method, of class ItemForm.
//     */
//    @Test
//    public void testBind() {
//        System.out.println("bind");
//        HttpServletRequest request = null;
//        Object objEntre = null;
//        Class type = null;
//        ItemForm instance = new ItemForm();
//        Object expResult = null;
//        Object result = instance.bind(request, objEntre, type);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of validate method, of class ItemForm.
//     */
//    @Test
//    public void testValidate() {
//        System.out.println("validate");
//        HttpServletRequest request = null;
//        ItemForm instance = new ItemForm();
//        Boolean expResult = null;
//        Boolean result = instance.validate(request);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of parseListeRequete method, of class ItemForm.
     */
    @Test
    public void testParseListeRequete() throws Exception {
        System.out.println("parseListeRequete");


        ServletRunner sr = new ServletRunner();
        sr.registerServlet("myServlet", ItemSrvl.class.getName());

        ServletUnitClient sc = sr.newClient();
        WebRequest webrequest = new PostMethodWebRequest("http://localhost//RSSAgregate/item/list");
        // filter param : {"caption":"truc modif","groupOp":"AND","rules":[],"spefield":[{"field":"idFlux","op":"in","data":[18328]},{"field":"date2","op":"lt","data":""},{"field":"date1","op":"gt","data":"01/11/2013"}]}
        webrequest.setParameter("filters", "{\"caption\":\"truc modif\",\"groupOp\":\"AND\",\"rules\":[],\"spefield\":[{\"field\":\"listFlux\",\"op\":\"in\",\"data\":[18328]},{\"field\":\"date2\",\"op\":\"lt\",\"data\":\"05/11/2013\"},{\"field\":\"date1\",\"op\":\"gt\",\"data\":\"01/11/2013\"}]}");

     
        InvocationContext ic = sc.newInvocation(webrequest);
        HttpServletRequest request = ic.getRequest();



        AbstrDao dao = DAOFactory.getInstance().getDaoItem();
        ItemForm instance = new ItemForm();
        instance.setAction("list"); // Normalement c'est le constructeur qui fait ca

        instance.parseListeRequete(request, dao);
        
        
        // Si on n'a pas 3 filtre c'est mauvais
        if(instance.getFiltersList().getFilters().size()!=3){
            fail("On attendait 3 filtre, on n'en a que "+instance.getFiltersList().getFilters().size());
        }
        
        SearchFilter s1 = instance.getFiltersList().getFilters().get(0);
        List<Flux> lf = (List<Flux>) s1.getData();
        if(!lf.get(0).getID().equals(new Long(18328))){
            fail("Le premier filtre devait être un flux avec id = 18328");
        }
        if(!s1.getOp().equals("in")){
            fail("opérateur devait être in");
        }
        
        
        SearchFilter s2 = instance.getFiltersList().getFilters().get(1);
        Date d2 = (Date) s2.getData();
        DateTime dt = new DateTime(d2);
        if(dt.getDayOfMonth() == 5){
            fail("jour devait être 5");
        }
        if(s2.getOp().equals("gt")){
            fail("opérateur devait être gt");
        }
        
   
    }
}