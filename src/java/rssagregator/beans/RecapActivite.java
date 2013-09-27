/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.poi.util.Beta;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.json.simple.JSONArray;

/**
 * <strong>Implémentation beta des correctif à effectuer</strong>
 * permet de stocker des donnée de issues du formulaire recapactivité. Cette
 * entitée n'est pas persistable dans la base de donnée
 *
 * @author clem
 */

//TODO : Revoir le méchanisme de recap de l'activité avec affichage javascript. N'est pas un objectif prioritaire.
@Beta
public class RecapActivite {

    private List<Flux> listFlux;
    private Date date1;
    private Date date2;
    String json;
//    private String jsonurl;

    public RecapActivite() {

        this.listFlux = new ArrayList<Flux>();
    }

    public List<Flux> getListFlux() {
        return listFlux;
    }

    public void setListFlux(List<Flux> listFlux) {
        this.listFlux = listFlux;
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public Date getDate2() {
        return date2;
    }

    public void setDate2(Date date2) {
        this.date2 = date2;
    }

    //    public String getJsonurl() {
    //
    //        // construction de l'url en fonction des paramettres.
    //        //date
    //
    //
    //
    //
    //
    //
    //        return jsonurl;
    //    }
    //
    //    public void setJsonurl(String jsonurl) {
    //        this.jsonurl = jsonurl;
    //    }
    public String getJson() {
        int i;
        List<Item> result;
        
        JSONArray tabResult = new JSONArray();
        
        for (i = 0; i < this.getListFlux().size(); i++) {

            DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
            
            //TODO : revoir la visualisation avec la dao multiflux
            daoItem.setWhere_clause_Flux(this.getListFlux());
            daoItem.setDate1(this.getDate1());
            daoItem.setDate2(this.getDate2());

            result = daoItem.findCretaria();
//            System.out.println("Nombre de d'item : " + result.size());
            JSONArray jSONArray = jsonGraphEncode(result, this);
            tabResult.add(jSONArray);
//            return jSONArray.toJSONString();
        }
        
        
        
//        System.out.println("dd");
//        System.out.println("");
//        System.out.println(""+tabResult.toJSONString());
        return tabResult.toJSONString();
    }

    public void setJson(String json) {
        this.json = json;
    }
    
    public JSONArray jsonGraphEncode(List<Item> items, RecapActivite recap) {
        // On commence par trier la list des items par date;
        Collections.sort(items);

        DateTime start = new DateTime(recap.getDate1());
        DateTime end = new DateTime(recap.getDate2());

// period of 1 year and 7 days

        Days days = Days.daysBetween(start, end);

        int i;
        // On initialise la liste des résultat
        Integer[] tabRetour = new Integer[days.getDays()];

        // Initialisation du tableau à 0
        for (i = 0; i < tabRetour.length; i++) {
            tabRetour[i] = 0;
        }

        // Calcul par jour
        for(i=0;i<items.size();i++){
            DateTime d = new DateTime(items.get(i).getDateRecup());
            Period p = new Period(start, d);
            tabRetour[p.getDays()]++;
        }

// Formation du tableau Json
        JSONArray jsona2 = new JSONArray();
        for (i = 0; i < tabRetour.length; i++) {
            jsona2.add(tabRetour[i]);
        }

        return jsona2;
    }
}
