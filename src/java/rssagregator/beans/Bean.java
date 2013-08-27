/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Id;

/**
 *      Cette classe n'est plus utilisé. Elle était pensée comme une classe mère à tous les beans. Mais comme certain beans doivent implémenter Observable On a préféré s'en passer
 * @author clem
 */
@Deprecated
public class Bean {

@Deprecated
    public boolean compareBeans(Object obj, boolean compareId) {
        // Pou chaque champs 
        Field[] tabField = this.getClass().getDeclaredFields();

        int i;
        for (i = 0; i < tabField.length; i++) {
            try {
                // On véifi si il s'agit de l'id
                Field field = tabField[i];
                String nomField = field.getName();
                Boolean pass = true;
                if (field.getAnnotation(Id.class) != null && !compareId) {
                    pass = false;
                }
                // On construit un getter
                if (pass) {
                    String getter = "get" + nomField.substring(0, 1).toUpperCase() + nomField.substring(1, nomField.length());
                    // On récupère le contenu
                    Class[] param = new Class[0];


                    Method methodeGetter = this.getClass().getMethod(getter);
//                
                    Object contenu1 = methodeGetter.invoke(this);
                    Object contenu2 = methodeGetter.invoke(obj);
//                
                    if (contenu1 != null && contenu2 != null) {
                        if (contenu1.hashCode() != contenu2.hashCode()) {
                            return false;
                        }

                    } else if ((contenu1 == null && contenu2 != null) || (contenu1 != null && contenu2 == null)) {
                        return false;
                    }
                }
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(Journal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(Journal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Journal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Journal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Journal.class.getName()).log(Level.SEVERE, null, ex);
            }
//        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
        }
        return true;
    }
}
