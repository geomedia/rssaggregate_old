/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils.comparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rssagregator.services.CSVMackerTest;

/**
 *
 * @author clem
 */
public class FileNameComparatorTest {
    
    public FileNameComparatorTest() {
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
     * Test of compare method, of class FileNameComparator.
     */
    @Test
    public void testCompare() {
        System.out.println("compare");
        
        
                String dir = "/home/clem/TMP/tata/";
        String c1 = "A";
        String c2 = "B";
        String c3 = "C";

        File f1 = new File(dir + "toto1");
        File f2 = new File(dir + "toto2");
        File f3 = new File(dir + "toto3");
        
        try {
            FileUtils.write(f1, c1);
            FileUtils.write(f2, c2);
            FileUtils.write(f3, c3);

        } catch (IOException ex) {
            fail();
            Logger.getLogger(CSVMackerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        

        FileNameComparator instance = new FileNameComparator();
        int expResult = -1;
        int result = instance.compare(f1, f2);
        assertEquals(expResult, result);
        
        
        // On tente le trie
        List<File> lf = new ArrayList<File>();
        lf.add(f3);
        lf.add(f1);
        lf.add(f2);
        Collections.sort(lf, instance);
        if(!lf.get(0).equals(f1)){
            fail("Ca devait être le fichier 1");
        }
        
        
        if(!lf.get(2).equals(f3)){
            fail("On voulait le fichier 3 on a" + lf.get(2).getName());
        }
        
        
    }
}