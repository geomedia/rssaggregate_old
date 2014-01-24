/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.tool;

import java.util.Comparator;
import rssagregator.beans.Bean;

/**
 * Permet de comparer des been en fonction de leur ID. Pratique pour trier une liste d'item ou de flux en fonction de l'id par exemple
 * @author clem
 */
public class ComparatorBean  implements Comparator<Bean>{

    @Override
    public int compare(Bean o1, Bean o2) {
        
        // Si les deux ont des id null il sont equau
        if(o1.getID() == null && o2.getID()==null){
            return 0;
        }
        
        
        if(o1.getID()!= null && o2.getID()==null){
            return 1;
        }

        
        if(o1.getID()==null && o2.getID() !=null){
            return -1;
        }
        
        
        return o1.getID().compareTo(o2.getID());
        
//        if(o1.getID().>o2.getID()){
//            return 1;
//        }
//        
//        if(o2.getID()<o2.getID()){
//            return -1;
//        }
//        
//        if(o1.getID().equals(o2.getID())){
//            return 0;
//        }
        
//        
//        System.out.println("o1" + o1.getID());
//        System.out.println("o2" + o2.getID());
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
