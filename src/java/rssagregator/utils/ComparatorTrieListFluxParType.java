/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import java.util.Comparator;
import rssagregator.beans.Flux;
import rssagregator.servlet.TypeFluxSrvl;

/**
 *
 * Un comparator qui met tout les flux n'ayant pas de type ou ayant un type dit "Autre" a la fin de la liste. 
 * @author clem
 */
public class ComparatorTrieListFluxParType implements Comparator<Flux>{

    @Override
    public int compare(Flux o1, Flux o2) {
        
        boolean f1Autre = false;
        if(o1.getTypeFlux() == null){
                f1Autre = true;
        }
        if(o1.getTypeFlux()!= null && o1.getTypeFlux().getDenomination()==null){
            f1Autre = true;
        }
        if(o1.getTypeFlux()!=null && o1.getTypeFlux()!=null && o1.getTypeFlux().equals("Autre")){
            f1Autre = true;
        }
        
        
        boolean f2Autre = false;
        if(o2.getTypeFlux() == null){
            f2Autre = true;
        }
        if(o2.getTypeFlux()!= null && o2.getTypeFlux().getDenomination()==null){
            f2Autre = true;
        }
        if(o2.getTypeFlux()!=null && o2.getTypeFlux().getDenomination()!=null && o2.getTypeFlux().getDenomination().equals("Autre")){
            f2Autre = true;
        }
        
        if(f1Autre && f2Autre){
            return 0;
        }
                if(!f1Autre && !f2Autre){
            return 0;
        }
                
        if(f1Autre && !f2Autre){
            return 1;
        }
        
        if(!f1Autre && f2Autre){
            return -1;
        }
        
        return 0;

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
