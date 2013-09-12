/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import java.util.Date;
import java.util.Map;
import javax.persistence.Entity;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.services.TacheVerifComportementFLux;

/**
 * Ce n'est pas à proprement parlé un incident. Lorsque le collecteur repère une
 * modification du comportement du flux il créer ce type d'incident.
 *
 * @author clem
 */
@Entity(name = "i_anomaliecollecte")
public class AnomalieCollecte extends CollecteIncident {

//    Map<Date, Integer> compteflux;
    

    /***
     * Permet de construire le message de l'incident à partir de la tache. Le message est au format html
     * @param task 
     */
    public void feedWithTask(TacheVerifComportementFLux task) {

        if (task != null && task.getResult() != null) {
            this.messageEreur = "<ul>";
             DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
            for (Map.Entry<Date, Integer> entry : task.getResult().entrySet()) {
                Date date = entry.getKey();
                DateTime dateDt = new DateTime(date);
                Integer val = entry.getValue();
                
                if(date!=null && val != null){
                    messageEreur+= fmt.print(dateDt)+". Nombre d'item : " + val.toString();
                }
            }
            messageEreur+="</ul>";
            messageEreur="<p>Moyenne attendu : "+task.getMoy()+"</p>";
            messageEreur="<p>Seuil min attendu : "+task.getSeuilMax()+"</p>";
            messageEreur="<p>Seuil max attendu : "+task.getSeuilMax()+"</p>";
            
        }
    }
}
