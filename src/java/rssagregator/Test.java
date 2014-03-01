/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator;

import org.joda.time.DateTime;
import org.joda.time.chrono.IslamicChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author clem
 */
public class Test {

    public static void main(String[] args) {
        DateTime dtISO = new DateTime(2014, 2, 25, 0, 0, 0, 0);
        DateTime dtIslamic = dtISO.withChronology(IslamicChronology.getInstance());
        String formatIslamic = "dd MMMM yyyy";
        DateTimeFormatter formatter = DateTimeFormat.forPattern(formatIslamic).withChronology(IslamicChronology.getInstance());
        String islamicDateString = formatter.print(dtIslamic);
        System.out.println(islamicDateString);
    }
}
