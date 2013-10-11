/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.joda.time.DateTime;

/**
 *
 * @author clem
 */
public class POJOCompteItem {
    Flux flux;
    Map<Date, Integer> compte;
    List<Item> items;
    

    public POJOCompteItem() {
    compte = new TreeMap<Date, Integer>();

    
    items = new ArrayList<Item>();
    }

    public Flux getFlux() {
        return flux;
    }

    public void setFlux(Flux flux) {
        this.flux = flux;
    }

    public Map<Date, Integer> getCompte() {
        return compte;
    }

    public void setCompte(Map<Date, Integer> compte) {
        this.compte = compte;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    public void compte(){
        for (Iterator<Item> it = items.iterator(); it.hasNext();) {
            Item item = it.next();
            // On récupère la date.
            DateTime dt = new DateTime(item.getDateRecup()).withTimeAtStartOfDay();
            
           Integer cptDay = compte.get(dt.toDate());
           if(cptDay==null){
               compte.put(dt.toDate(), 1);
               System.out.println("1");
           }
           else{
               cptDay++;
               compte.put(dt.toDate(), cptDay);
               System.out.println("++");
           }
        }
    }
}
