/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import javax.persistence.Entity;
import rssagregator.services.ServiceCollecteur;
//import rssagregator.services.TacheVerifComportementFLux;

/**
 * <p>Ce n'est pas à proprement parlé un incident. Lorsque le collecteur repère une modification du comportement du flux
 * il créer ce type d'incident. Cette anomalie consiste en une hausse ou baisse journalière importante du nombre de
 * capture.</p>
 * <p>Cet incident est générer par le service : {@link ServiceCollecteur} en fonction des résultats de la tâche
 * {@link TacheVerifComportementFLux}</p>
 *
 * @author clem
 */
@Entity(name = "i_anomaliecollecte")
public class AnomalieCollecte extends CollecteIncident implements Notification {

    public AnomalieCollecte() {
    }
    

//    Map<Date, Integer> compteflux;
    /**
     * *
     * A portériori, les administrateurs ont à déterminer si l'anolalie détecté pour un flux est due à une erreur
     * technique du site.
     */
    Boolean causeTechniqueSiteJournal;
    /**
     * *
     * A portériori, les administrateurs ont à déterminer si l'anolalie détecté pour un flux est due à un changement de
     * ligne éditoriale du site.
     */
    Boolean causeChangementLigneEditoriale;
    Integer nombreCaptureConstate;
    /**
     * *
     * La moyenne des captures constatées
     */
    Float moyenneDesCapture;
    /**
     * *
     * Le seuil de capture toléré pour le flux
     */
    Integer seuil;

    /**
     * *
     * Permet de construire le message de l'incident à partir de la tache. Le message est au format html
     *
     * @param task
     */
//    public void feedMessageFromTask(TacheVerifComportementFLux task) {
//
//        if (task != null && task.getResult() != null) {
//            this.messageEreur = "<ul>";
//            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
//            for (Map.Entry<Date, Integer> entry : task.getResult().entrySet()) {
//                Date date = entry.getKey();
//                DateTime dateDt = new DateTime(date);
//                Integer val = entry.getValue();
//
//                if (date != null && val != null) {
//                    messageEreur += fmt.print(dateDt) + ". Nombre d'item : " + val.toString();
//                }
//            }
//            messageEreur += "</ul>";
//            messageEreur = "<p>Moyenne attendu : " + task.getMoy() + "</p>";
//            messageEreur = "<p>Seuil min attendu : " + task.getSeuilMax() + "</p>";
//            messageEreur = "<p>Seuil max attendu : " + task.getSeuilMax() + "</p>";
//
//        }
//    }
    /***
     * @see #causeTechniqueSiteJournal
     * @return 
     */
    public Boolean getCauseTechniqueSiteJournal() {
        return causeTechniqueSiteJournal;
    }

    public void setCauseTechniqueSiteJournal(Boolean causeTechniqueSiteJournal) {
        this.causeTechniqueSiteJournal = causeTechniqueSiteJournal;
    }

    public Boolean getCauseChangementLigneEditoriale() {
        return causeChangementLigneEditoriale;
    }

    public void setCauseChangementLigneEditoriale(Boolean causeChangementLigneEditoriale) {
        this.causeChangementLigneEditoriale = causeChangementLigneEditoriale;
    }

    public Integer getNombreCaptureConstate() {
        return nombreCaptureConstate;
    }

    public void setNombreCaptureConstate(Integer nombreCaptureConstate) {
        this.nombreCaptureConstate = nombreCaptureConstate;
    }

    public Float getMoyenneDesCapture() {
        return moyenneDesCapture;
    }

    public void setMoyenneDesCapture(Float moyenneDesCapture) {
        this.moyenneDesCapture = moyenneDesCapture;
    }

    public Integer getSeuil() {
        return seuil;
    }

    public void setSeuil(Integer seuil) {
        this.seuil = seuil;
    }

    @Override
    public Boolean doitEtreNotifieParMail() {
        return true;
//        return super.doitEtreNotifieParMail(); //To change body of generated methods, choose Tools | Templates.
    }

    public Float returnSeuilMaxTolere() {

              
        return moyenneDesCapture + (moyenneDesCapture * seuil / 100 );

    }

    public Float returnSeuilMinTolere() {
        return moyenneDesCapture - (moyenneDesCapture * seuil / 100);
    }

    @Override
    public String toString() {
        return "Anomalie de Collecte flux : "+this.fluxLie;
    }
}
