///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package rssagregator.beans;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
//import org.joda.time.DateTime;
//
///**
// *
// * @author clem
// */
//public class POJOCompteItem {
//
//    Flux flux;
//    Map<Date, Integer> compte;
//    List<Item> items;
//    Date date1;
//    Date date2;
//
//    public POJOCompteItem() {
//        compte = new TreeMap<Date, Integer>();
//
//
//        items = new ArrayList<Item>();
//    }
//
//    public Flux getFlux() {
//        return flux;
//    }
//
//    public void setFlux(Flux flux) {
//        this.flux = flux;
//    }
//
//    public Map<Date, Integer> getCompte() {
//        return compte;
//    }
//
//    public void setCompte(Map<Date, Integer> compte) {
//        this.compte = compte;
//    }
//
//    public List<Item> getItems() {
//        return items;
//    }
//
//    public void setItems(List<Item> items) {
//        this.items = items;
//    }
//
//    public Date getDate1() {
//        return date1;
//    }
//
//    public void setDate1(Date date1) {
//        this.date1 = date1;
//    }
//
//    public Date getDate2() {
//        return date2;
//    }
//
//    public void setDate2(Date date2) {
//        this.date2 = date2;
//    }
//
//    public void compte() {
//
//        // On commence par initialiser la map de compte
//        DateTime dt1 = new DateTime(date1).withTimeAtStartOfDay();
//        DateTime dt2 = new DateTime(date2).withTimeAtStartOfDay();
//        DateTime dtIt = new DateTime(date1).withTimeAtStartOfDay(); 
//
//        while (dtIt.isBefore(dt2)) {
//            compte.put(dtIt.toDate(), 0);
//            dtIt = dtIt.plusDays(1);
//        }
//
//        for (Iterator<Item> it = items.iterator(); it.hasNext();) {
//            Item item = it.next();
//            // On récupère la date.
//            DateTime dt = new DateTime(item.getDateRecup()).withTimeAtStartOfDay();
//
//            Integer cptDay = compte.get(dt.toDate());
//            if (cptDay == null) {
//                compte.put(dt.toDate(), 1);
//            } else {
//                cptDay++;
//                compte.put(dt.toDate(), cptDay);
//            }
//        }
//    }
//}
