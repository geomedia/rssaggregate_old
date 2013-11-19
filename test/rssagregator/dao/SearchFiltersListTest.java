/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author clem
 */
public class SearchFiltersListTest {
    
    public SearchFiltersListTest() {
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
//     * Test of returnJsonString method, of class SearchFiltersList.
//     */
//    @Test
//    public void testReturnJsonString() {
//        System.out.println("returnJsonString");
//        SearchFiltersList instance = new SearchFiltersList();
//        String expResult = "";
//        String result = instance.returnJsonString();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFilters method, of class SearchFiltersList.
//     */
//    @Test
//    public void testGetFilters() {
//        System.out.println("getFilters");
//        SearchFiltersList instance = new SearchFiltersList();
//        List expResult = null;
//        List result = instance.getFilters();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFilters method, of class SearchFiltersList.
//     */
//    @Test
//    public void testSetFilters() {
//        System.out.println("setFilters");
//        List<SearchFilter> filters = null;
//        SearchFiltersList instance = new SearchFiltersList();
//        instance.setFilters(filters);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of feedFromJSONString method, of class SearchFiltersList.
//     */
//    @Test
//    public void testFeedFromJSONString() {
//            System.out.println("feedFromJSONString");
//        /***
//         * Test avec : {"caption":"truc modif","groupOp":"AND","rules":[],"spefield":[{"field":"idFlux","op":"in","data":[18328,18352]},{"field":"date2","op":"lt","data":"31/10/2013"},{"field":"date1","op":"gt","data":"29/10/2013"}]}
//         */
//        String filtersAParser = "{\"caption\":\"truc modif\",\"groupOp\":\"AND\",\"rules\":[],\"spefield\":[{\"field\":\"idFlux\",\"op\":\"in\",\"data\":[18328,18352]},{\"field\":\"date2\",\"op\":\"lt\",\"data\":\"31/10/2013\"},{\"field\":\"date1\",\"op\":\"gt\",\"data\":\"29/10/2013\"}]}";
//        
//   
//        SearchFiltersList instance = new SearchFiltersList();
//        instance.feedFromJSONString(filtersAParser);
//        
//        //L'instance doit posséder les trois flux
//        if(instance.getFilters().size()!=3){
//            fail("On attendait 3 filtre il y en a : " + instance.getFilters().size());
//        }
//        
//        
//        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
//    }
}