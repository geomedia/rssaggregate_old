/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.mailtemplate;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.incident.CollecteIncident;

/**
 * Cette classe est utilisée pour générer de corps des mail de notification. Ce
 * n'est surement pas la solution la plus élégente, mais pour envoyer un unique
 * mail nous savons pas voulu intégrer à l'application un moteur de template
 * complet.
 * <p>
 * Pour construire un mail <u>
 * <li>il faut instancier un MailTemple.</li>
 * <li>il faut ensuite renseigner la liste des flux en erreur avec setListIncidentFlux</li>
 * <li> enfin on peut utiliser la methode getCorpsMail pour récupérer le contenu d'un mail</li>
 * </p>
 *
 * @author clem
 */
public class TemplateRecapFluxEchec {

    List<CollecteIncident> listIncidentFlux;

    
    /***
     * Construit le corps d'un mail de synthèse journalière des erreurs. 
     * @return 
     */
    public String getCorpsMail() {
        String corps = "";
        corps += "<p>Voici Un récapitulatif journalier des flux en erreur.</p>";
        corps += "<ul>";
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMMM yyyy à hh'h'mm");
//        DateTimeFormatter fmt = DateTimeFormat.;

        
        for (int i = 0; i < listIncidentFlux.size(); i++) {
            CollecteIncident fluxIncident = listIncidentFlux.get(i);

            corps += "<li>";
            corps += "<p>"+fluxIncident.getFluxLie()+". ";
            try {
                corps+= "<a href=\""+fluxIncident.getUrlAdmin()+"\">voir détail de l'erreur</a>";
                System.out.println("URL : " + fluxIncident.getUrlAdmin());
            } catch (IOException ex) {
                Logger.getLogger(TemplateRecapFluxEchec.class.getName()).log(Level.SEVERE, null, ex);
            }
            corps+="</p>";
            
            DateTime dt = new DateTime(fluxIncident.getDateDebut());

            corps += "<p>Depuis : " + fmt.print(dt)+ ". Durée de l'incident : " + fluxIncident.getDuree()+"</p>";
            corps += "<p>Desciption de l'erreur : " + fluxIncident.getMessageEreur()+"</p>";
            corps+="<p>Log JAVA"+fluxIncident.getLogErreur()+"</p>";
            corps += "</li>";
        }
        corps += "</ul>";

        return corps;
    }

    public List<CollecteIncident> getListIncidentFlux() {
        return listIncidentFlux;
    }

    public void setListIncidentFlux(List<CollecteIncident> listIncidentFlux) {
        this.listIncidentFlux = listIncidentFlux;
    }
}
