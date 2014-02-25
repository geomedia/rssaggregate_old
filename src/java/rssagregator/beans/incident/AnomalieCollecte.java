/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.joda.time.DateTime;
import rssagregator.beans.exception.IncompleteBeanExeption;
import rssagregator.services.ServiceCollecteur;
import rssagregator.utils.ExceptionTool;
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
    /**
     * *
     * Une anomalie de collecte est lie a un ou plusieurs jours avec pour chaque un nombre d'item collecte. Ces
     * informations sont stockés dans le petite entite PeriodeAnormale
     */
    @OneToMany(cascade = CascadeType.ALL)
//    List<PeriodeAnormale> periodeAnormale = new ArrayList<PeriodeAnormale>();
//    @OneToMany
    List<PeriodeAnormale> periodeAnormale = new ArrayList<PeriodeAnormale>();

//    Set<PeriodeAnormale> setp = new 

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
    /**
     * *
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

    @Override
    public Boolean doitEtreNotifieParMail() {
        return true;
//        return super.doitEtreNotifieParMail(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "Anomalie de Collecte flux : " + this.fluxLie;
    }

    /**
     * *
     * Parcours les {@link #periodeAnormale} de l'anomalie et vérifi si l'on peut trouver la date envoyé en argument
     *
     * @param date
     * @return
     */
    public boolean contientDate(Date date) {

        DateTime dtDemande = new DateTime(date);
        // On parcours 
        for (int i = 0; i < periodeAnormale.size(); i++) {
            PeriodeAnormale periodeAnormale1 = periodeAnormale.get(i);
//          Object d=   ((SortedSet)periodeAnormale).last();
            
            
//            PeriodeAnormale periodeAnormale1 = (PeriodeAnormale) ((SortedSet)periodeAnormale).last();
            DateTime dtObs = new DateTime(periodeAnormale1.getDateAnomalie());
            if (dtDemande.withTimeAtStartOfDay().equals(dtObs.withTimeAtStartOfDay())) {
                return true;
            }
        }
        return false;
    }

//        public boolean contientDate(Date d) {
//        
//        DateTime dtArg = new DateTime(d).withTimeAtStartOfDay();
//        if (periodeAnormale != null && !periodeAnormale.isEmpty()) {
//            for (int i = 0; i < periodeAnormale.size(); i++) {
//                PeriodeAnormale periodeAnormale1 = periodeAnormale.get(i);
//                DateTime dtObs = new DateTime(periodeAnormale1.dateAnomalie).withTimeAtStartOfDay();
//                if(dtArg.equals(dtObs)){
//                    return true;
//                }
//                
//            }
//        }
//        return false;
//    }
    /**
     * *
     * Si la date envoyé en argument est consécutive à la dernière date der la {@link #periodeAnormale} envoie true.
     * Sinon false
     *
     * @param d
     * @return
     */
    public boolean peutAjouterDate(Date d) {


        if (periodeAnormale != null && !periodeAnormale.isEmpty()) {
            // On commence par trier les période en ordre chrono;
            Collections.sort(periodeAnormale);

            // On récupère la dernière
//            periodeAnormale.
            PeriodeAnormale p = this.periodeAnormale.get(this.periodeAnormale.size() - 1);

//            PeriodeAnormale p = (PeriodeAnormale) ((SortedSet)this.periodeAnormale).last();

            DateTime dt = new DateTime(p.dateAnomalie).withTimeAtStartOfDay();
            DateTime dtArg = new DateTime(d).withTimeAtStartOfDay();
            if (dt.plusDays(1).equals(dtArg)) {
                return true;
            } else {
                return false;
            }
        }
        return true; // si l'anomalie de collecte ne possède pas de période alors elle peut ajouter.
    }

    //    public List<PeriodeAnormale> getPeriodeAnormale() {
    //        return periodeAnormale;
    //    }
    //
    //    public void setPeriodeAnormale(List<PeriodeAnormale> periodeAnormale) {
    //        this.periodeAnormale = periodeAnormale;
    //    }
    //    public Set<PeriodeAnormale> getPeriodeAnormale() {
    //        return periodeAnormale;
    //    }
    //
    //    public void setPeriodeAnormale(Set<PeriodeAnormale> periodeAnormale) {
    //        this.periodeAnormale = periodeAnormale;
    //    }
    public List<PeriodeAnormale> getPeriodeAnormale() {
        return periodeAnormale;
    }

    public void setPeriodeAnormale(List<PeriodeAnormale> periodeAnormale) {
        this.periodeAnormale = periodeAnormale;
    }

    
    
    

    /**
     * *
     *
     * Ajoute une période d'anomalie si cela est possible
     *
     * @param p
     * @return true si l'ajout a bien été effectué sinon false
     * @throws NullPointerException : null pointeur si le période est null ou si la période ne possède pas de date
     */
    public synchronized boolean addPeriode(PeriodeAnormale p) throws NullPointerException {

        ExceptionTool.argumentNonNull(p);
        ExceptionTool.argumentNonNull(p.dateAnomalie);
        // On commence par vérifier si la periode peut être ajouté
        if (peutAjouterDate(p.dateAnomalie)) {
            this.periodeAnormale.add(p);
            return true;
        }
        return false;
    }

    /**
     * *
     * Retourne la date de la dernière anomalie
     *
     * @return
     */
    public Date returnLastAnomalieDate() throws IncompleteBeanExeption {

        if (this.periodeAnormale != null) {
            if (!this.periodeAnormale.isEmpty()) {
                // On trie la liste
                Collections.sort(periodeAnormale);
                // Récupération de la derniere période
                PeriodeAnormale p = periodeAnormale.get(periodeAnormale.size() - 1);
//                PeriodeAnormale p = ((SortedSet<PeriodeAnormale>)periodeAnormale).last();

                return p.dateAnomalie;
            }
        }
        
        throw new IncompleteBeanExeption("L'anomalie ne posséde pas de période impossible de renvoyer la date de la dernière période");
        
        
        

    }
}
