/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author clem
 */
public class StringUtilsTest {

    public StringUtilsTest() {
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
     * Test of returnAbrege method, of class StringUtils.
     */
    @Test
    public void testReturnAbrege() {

        System.out.println("returnAbrege");
        String grosString = "^ù%\\azertyUIOP654$";
        int nbrChar = 60;
        String expResult = "azertyUIOP654";
        String result = StringUtils.returnAbrege(grosString, nbrChar, "[^a-zA-Z0-9]");
        assertEquals(expResult, result);



        grosString = "^ù%\\azertyUIOP654$azertyUIOP654$azertyUIOP654$azertyUIOP654$azertyUIOP654$azertyUIOP654$azertyUIOP654$azertyUIOP654$azertyUIOP654$azertyUIOP654$azertyUIOP654$azertyUIOP654$azertyUIOP654$azertyUIOP654$azertyUIOP654$azertyUIOP654$";
        nbrChar = 5;
        expResult = "azert";
        result = StringUtils.returnAbrege(grosString, nbrChar, "[^a-zA-Z0-9]");
        assertEquals(expResult, result);


        grosString = "$&azerty&";
        nbrChar = 50;
        expResult = "azerty";
        result = StringUtils.returnAbrege(grosString, nbrChar, "[^a-zA-Z0-9]");
        assertEquals(expResult, result);

        grosString = "#";
        nbrChar = 50;
        expResult = "";
        result = StringUtils.returnAbrege(grosString, nbrChar, "[^a-zA-Z0-9]");
        assertEquals(expResult, result);

        grosString = "";
        nbrChar = 50;
        expResult = "";
        result = StringUtils.returnAbrege(grosString, nbrChar, "[^a-zA-Z0-9]");
        assertEquals(expResult, result);

    }
}