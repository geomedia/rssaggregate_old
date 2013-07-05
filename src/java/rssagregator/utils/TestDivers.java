/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import org.apache.log4j.Logger;

/**
 *
 * @author clem
 */
public class TestDivers {
    private static final Logger logger = Logger.getLogger(TestDivers.class);
    
    public static void main(String[] args) {
        System.out.println("lala");
        
        
        String[][] tab;
//        = new String[1][1];
        
        tab = new String[1][2];
        tab[0][1]="a";
        
        
        int i;
        for(i=0;i<tab.length;i++){
            System.out.println(""+tab[i][1]);
        }
        
    }
    
}
