/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.mailtemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.Flux;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.AnomalieCollecte;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.beans.incident.Notification;
import rssagregator.beans.incident.NotificationAjoutFlux;

/**
 * Une template de mail pour réaliser le corps des mail d'alert.
 *
 * @author clem
 */
public class TemplateMailAlertIncident {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TemplateMailAlertIncident.class);

    public TemplateMailAlertIncident() {

        listIncident = new ArrayList<AbstrIncident>();
    }
    private List<AbstrIncident> listIncident;

    public List<AbstrIncident> getListIncident() {
        return listIncident;
    }

    public void setListIncident(List<AbstrIncident> listIncident) {
        this.listIncident = listIncident;
    }

    /**
     * *
     * Retourne une chaine de caractère (mise en forme html) construite a partir les incidents.
     *
     * @return
     * @throws Exception
     */
    public String getCorpsMail() throws Exception {
        logger.debug("corps de mail debut");
        String corps = "";
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMMM yyyy à hh'h'mm");
        DateTime dt = new DateTime(new Date());
        logger.debug("après la date");
        corps += "<h1>Incidents </h1>";
        corps += "<p>Le " + fmt.print(dt) + "</p>";




        //--------------------------------------
        //      Notification des Incident
        //--------------------------------------
        corps += "<ul>";
        for (int i = 0; i < listIncident.size(); i++) {


            AbstrIncident abstrIncident = listIncident.get(i);
            if (!Notification.class.isAssignableFrom(abstrIncident.getClass())) {
                logger.debug("dans la boucle");
                corps += "<li>";
                if (abstrIncident instanceof CollecteIncident) {
                    corps += "<p><strong>Flux : </strong>" + ((CollecteIncident) abstrIncident).getFluxLie() + "</p>";
                }

                corps += "<p><strong>Erreur : </strong>" + abstrIncident.getMessageEreur() + "</p>";
                corps += "<p><strong>Log Erreur : </strong>" + abstrIncident.getLogErreur() + "</p>";
                try {
                    logger.debug("try");
                    corps += "Voir plus de détails sur l'incident: " + abstrIncident.getUrlAdmin();
                } catch (IOException ex) {
                    Logger.getLogger(TemplateMailAlertIncident.class.getName()).log(Level.SEVERE, null, ex);
                    logger.debug("catch");
                }

                corps += "</li>";
            }


        }
        logger.debug("fin de boucle");
        corps += "</ul>";

        //------------------------------------
        //Notification des Evènement
        //------------------------------------
        corps += "<h1>Evènement : </h1>";
        corps += "<ul>";
        for (int i = 0; i < listIncident.size(); i++) {
            AbstrIncident abstrIncident = listIncident.get(i);
            if (Notification.class.isAssignableFrom(abstrIncident.getClass())) {
                //--------------> AJOUT DE FLUX PAR LA TACHE DE DECOUVERTE
                if (abstrIncident.getClass().equals(NotificationAjoutFlux.class)) {
                    NotificationAjoutFlux notifAjout = (NotificationAjoutFlux) abstrIncident;
                    corps += "<h2>Ajout automatique de Flux</h2>";
                    corps+="<p>Journal : " + notifAjout.getJournal() +"</p>";
                    corps += "<ul>";
                    for (int j = 0; j < notifAjout.getFluxAjoute().size(); j++) {
                        Flux fl = notifAjout.getFluxAjoute().get(j);
                        corps += "<li>" + fl + "</li>";

                    }
                    corps+="<p><a href=\""+notifAjout.getUrlAdmin()+"\">Détail</a></p>";
                    corps += "</ul>";
                }

                //-----------> Anomalie de collecte
                if (abstrIncident.getClass().equals(AnomalieCollecte.class)) {
                    AnomalieCollecte anomalie = (AnomalieCollecte) abstrIncident;

                    corps += "<h2>Anomalie de collecte supposé</h2>";
                    corps += "<p>Flux : " + anomalie.getFluxLie() + "</p>";
                    corps += "<p>detail " + anomalie.getMessageEreur() + "</p>";
                    corps+="<p><a href=\""+anomalie.getUrlAdmin()+"\">Voir détails</p>";
                }

            }
        }
        corps += "</ul>";

        System.out.println("CORPS : " + corps);
        logger.debug("conenu" + corps);
        return corps;
    }
}
