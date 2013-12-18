/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import com.fasterxml.jackson.databind.util.BeanUtil;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;

/**
 *
 * @author clem
 */
public class ExceptionTool {

    /**
     * *
     * Vérifi que l'object envoyé n'est pas null. si null leve une NullPointerException
     *
     * @param o
     * @throws NullPointerException
     */
    public static void argumentNonNull(Object o) throws NullPointerException {

        if (o == null) {
            throw new NullPointerException("L'object envoyé en argument est null");
        }

    }

    public static void checkClass(Object o, Class c) throws ClassCastException {
        if (!o.getClass().equals(c)) {
            throw new ClassCastException("L'object " + o + "  N'est pas du type" + c.getName() + "Il est du type" + c.getName());
        }
    }

    
    /***
     * Vérifie le fieldname et l'object o. Si le champs est null on leve une {@link NullPointerException} si la méthode ne parvient pas à accèder au champs, on lève une {@link IllegalAccessError}
     * @param o l'object qu'il faut vérifier
     * @param fieldName le nom du champs qu'il faut vérifier
     * @throws NullPointerException exception levée si le champs est null
     * @throws IllegalAccessException  Execption levée si le champs n'a pas été accessible pour l'objet.
     */
    public static void checkNonNullField(Object o, String fieldName) throws NullPointerException, IllegalAccessException{

        Object val = null;
        try {
            val = PropertyUtils.getProperty(o, fieldName);
        } catch (Exception e) {
            throw new IllegalAccessException("L'objet " + o + "Ne possède pas de champs " + fieldName+" ou impossible d'accéder a celui ci");
        }
       
        if (val == null) {
            throw new NullPointerException("Le champs " + fieldName + " de l'objet " + o + "est null");
        }
    }
    
    
    /***
     * Retourne le stackTrace de l'exception envoyé en argument dans une chaine de caractère. Renvoi une chaine vide si impossible
     * @param tw exception
     * @throws NullPointerException : si l'exception envoyé en argument est null
     * @return chaine de caractère du stacktrace ou chaine vide.
     */
    public static String stackTraceToString(Throwable tw) throws NullPointerException{
        
        argumentNonNull(tw);
         StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tw.printStackTrace(pw);
            String s = sw.toString();
            if(s != null){
                return s;
            }
            else{
                return "";
            }
    }
    
}
