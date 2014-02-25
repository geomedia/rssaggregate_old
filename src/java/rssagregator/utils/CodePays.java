/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author clem
 */
public class CodePays {

    /**
     * *
     * Une map avec en clé les code iso3 en value les pays.
     */
    private static Map<String, String> iso3mapCountry;
    /**
     * *
     * Une map avec les code iso 2 et en valeue les pays
     */
    private static Map<String, String> iso2MapCountry;

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
            @Override
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

        if (iso2MapCountry == null) {
            String[] IsoCountry = Locale.getISOCountries();
            Map<String, String> country = new HashMap<String, String>();
            int i;
            for (i = 0; i < IsoCountry.length; i++) {
                Locale l = new Locale("", IsoCountry[i]);
                country.put(l.getCountry(), l.getDisplayCountry());
            }

            iso2MapCountry = sortByComparator(country);
        }
        return iso2MapCountry;
    }

    /**
     * *
     * Retourne une map de correspondance iso 3 et display
     *
     * @return
     */
    public static Map<String, String> getCountryMap3() {

        if (iso3mapCountry == null) { // Si la map n'est pas déjà générée. 
            Locale loc = Locale.getDefault();
            String[] IsoCountry = loc.getISOCountries();

            Map<String, String> country = new HashMap<String, String>();

            int i;
            for (i = 0; i < IsoCountry.length; i++) {
                Locale l = new Locale("", IsoCountry[i]);
                country.put(l.getISO3Country(), l.getDisplayCountry());
            }
            iso3mapCountry = sortByComparator(country);
        }
        return iso3mapCountry;
    }
    private static Map<String, Locale> localeMap;


    public static void main(String[] args) {



        Map<String, String> map = getCountryMap3();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String string = entry.getKey();
            String string1 = entry.getValue();
            System.out.println(string + "  -  " + string1);

        }

    }
}
