/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils.comparator;

import com.google.common.collect.Ordering;
import java.io.File;
import java.util.Comparator;

/**
 * permet de classer des fichier suivant leur nom
 * @author clem
 */
public class FileNameComparator implements Comparator<File>{

    @Override
    public int compare(File o1, File o2) {
        
        
        if(o1 == null && o2 == null){
            return 0;
        }
        
        if(o1==null && o2 !=null){
            return -1;
        }
        if(o2==null && o1 == null){
            return 1;
        }
        
        int comp = o1.getName().compareTo(o2.getName());
        
        
 return o1.getName().compareTo(o2.getName());
//        if(o1.getName().compareTo(o2.getName()) > 0)){
//        
//    }
        
    
        
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
