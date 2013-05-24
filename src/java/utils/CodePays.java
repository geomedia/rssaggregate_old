/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.mysql.jdbc.util.TimezoneDump;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.spi.LocaleServiceProvider;
import javax.persistence.Tuple;
import javax.security.auth.callback.LanguageCallback;
import org.apache.naming.java.javaURLContextFactory;
import org.apache.tomcat.jni.Local;
import org.eclipse.persistence.internal.jpa.querydef.TupleImpl;
import org.eclipse.persistence.internal.queries.SortedCollectionContainerPolicy;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrderByItem;
import sun.util.locale.LanguageTag;

/**
 *
 * @author clem
 */
public class CodePays {

    public static Map<String, String> getLanMap() {
        Locale.getISOCountries();
        
        String[] IsoLang = Locale.getISOLanguages();
        Map<String, String> lang = new HashMap<String, String>();

        int i;
        for (i = 0; i < IsoLang.length; i++) {
            Locale l = new Locale(IsoLang[i]);
            lang.put(l.getLanguage(), l.getDisplayLanguage());
        }
        return sortByComparator(lang);
    }

    private static Map sortByComparator(Map unsortMap) {

        List list = new LinkedList(unsortMap.entrySet());

        // sort list based on comparator
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // put sorted list into map again
        //LinkedHashMap make sure order in which keys were inserted
        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static Map<String, String> getCountryMap() {

        String[] IsoCountry = Locale.getISOCountries();
        Map<String, String> country = new HashMap<String, String>();


        int i;
        for (i = 0; i < IsoCountry.length; i++) {
            Locale l = new Locale("", IsoCountry[i]);
            country.put(l.getCountry(), l.getDisplayCountry());
        }

        return sortByComparator(country);
    }

    public static void main(String[] args) {
        
        
        String[] tab =  TimeZone.getAvailableIDs();
        
        int i;
         for(i=0; i<tab.length; i++){
             System.out.println(""+ tab[i]);
         }
        
        
    }
}
