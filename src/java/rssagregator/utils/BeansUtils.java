/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author clem
 */
public class BeansUtils {

    
    
    /***
     * Compare les deux object en fonction de leur ID. On hésite jusqu'alors a redéclarer la méthode equels des beans alors on a fait un outils.
     * @param o1
     * @param o2
     * @return 
     */
    public static Boolean compareBeanFromId(Object o1, Object o2) {



        if (o1 == null) {
            throw new NullPointerException("object 1 est null");
        }
        if (o2 == null) {
            throw new NullPointerException("object 1 est null");
        }
        // Si les deux object ne sont pas de même classe
        if(!o1.getClass().equals(o2.getClass())){
            return false;
        }
        
        Long id1=null;
         Long id2=null;
        
        try {
            Method gettedId = o1.getClass().getMethod("getID");
            
            id1 = (Long) gettedId.invoke(o1);
            id2 =(Long) gettedId.invoke(o2);
            
            
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(BeansUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(BeansUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BeansUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BeansUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(BeansUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(id1 == null || id2 == null ){
            return false;
        }
        
        if(id1.equals(id2)){
            return true;
        }
        else{
            return false;
        }
        
       
        


    }
}
