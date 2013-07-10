/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import java.util.Comparator;

/**
 * Cette classe est utilisée par le jsp ItemCSV pour trier une liste de résultats
 *
 * @author clem
 */
public class CsvComparator1 implements Comparator<String[]> {

    @Override
    public int compare(String[] o1, String[] o2) {

// Si les colonne 7 sont les mêmes
        if (o1[7].compareTo(o2[7]) == 0) {
            // on compara par rapport à la colonne 6
            if(o2[6].compareTo(o2[6])==0){
                return 0;
            }
            else if(o2[6].compareTo(o2[6])>1){
                return -1;
            }
            else if(o2[6].compareTo(o2[6])<1){
                return 1;
            }
        } else if (o1[7].compareTo(o2[7]) > 0) {
            return -1;
        } else if (o1[7].compareTo(o2[7]) < 0) {
            return 1;
        }
        return 0;
    }
}
