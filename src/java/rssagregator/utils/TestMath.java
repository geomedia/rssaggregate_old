/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author clem
 */
public class TestMath {
    
    public static void main(String[] args) {
        
        DescriptiveStatistics stats = new DescriptiveStatistics();
        
        for (int i = 0; i < 10; i++) {
            stats.addValue(i);
        }
        stats.addValue(500);
        
        System.out.println("Mediane : " + stats.getPercentile(50));
        System.out.println("Ecart type " + stats.getStandardDeviation());
        System.out.println("MAX : " + stats.getMax());
        System.out.println("1er quartile" + stats.getPercentile(25));
        System.out.println("3eme quartile " + stats.getPercentile(75));
    }
    
}
