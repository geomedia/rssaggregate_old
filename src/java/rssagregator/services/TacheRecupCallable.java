/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Date;
import java.util.List;
import java.util.Observer;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.exception.CollecteUnactiveFlux;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.traitement.MediatorCollecteAction;

/**
 * La tâche permettant au {@link Flux} d'être collecté périodiquement. Elle est gérée par le service {@link ServiceCollecteur}
 * @author clem
 */
public class TacheRecupCallable extends AbstrTacheSchedule<TacheRecupCallable> implements Incidable{

    public TacheRecupCallable(Observer s) {
        super(s);
        annulerTache = false;
    }

    /**
     * *
     *
     * @param flux :Le flux attribuer à la tâche
     * @param s : Le service devant gérer la tâche
     * @param tacheSchedule : Indique si il s'agit d'une tache schedulé (qui
     * doit être réajouté au service en fin de traitement)
     * @param persit :Faut t'il persister dans la base de données
     */
    public TacheRecupCallable(Flux flux, Observer s, Boolean tacheSchedule, Boolean persit) {
        this(s);
        this.flux = flux;
        this.tacheSchedule = tacheSchedule;
        this.persit = persit;
    }
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheRecupCallable.class);
    /**
     * *
     * Les items capturées par la tache
     */
    List<Item> nouvellesItems;
    /**
     * *
     * Le flux de la tache
     */
    public Flux flux;
    /**
     * A chaque récupération, il faut inscrire dans cette variable la date.
     * Cette variable est ainsi modifié au lancement de la méthode run
     */
    public Date DateDerniereRecup;
//    /**
//     * *
//     * Lorsqu'une exeption survient, on stocke sa référence ici
//     */
    public AbstrIncident incident;
    /**
     * *
     * Indique si cette tache est une tache schedulé devant être rajouté à la
     * fin de son traitment dans le pool schedulé. Si false, c'est une tâche
     * manuelle, elle ne sera pas réajouté
     */
    Boolean tacheSchedule;
    /**
     * *
     * Le boolenn persit indique si la tâche doit ou non enregistrer ses données
     * dans la base de données
     */
    Boolean persit;
    /**
     * *
     * Permet d'annuler la tâche. Lors de son déclanchement, elle ne va rien
     * faire et ne plus être ajouté au scheduler
     */
    Boolean annulerTache;

    /**
     * *
     * @return un booleen true si ok false si exeption capturé et géré. mais pas
     * vraiment utilisé dans le reste du programme
     * @throws Exception
     */
    @Override
    public TacheRecupCallable call() throws Exception {
        // On block le flux pour eviter que la tache automanique et la tache manuelle agissent en même temps.
        synchronized (this.flux) {
            this.exeption = null;
            // Si la tache n'a pas été annulé elle se déroule normalement.
            try {
                if(!flux.getActive()){
                    throw new CollecteUnactiveFlux("Ce flux doit être activé pour être récolté"); 
                }
                
                if (!annulerTache) {
                    flux.setTacheRechup(this);
                    //On crée une copie du mediator devant être employé. C'est notre façon d'être thread safe
//                    try {
                    MediatorCollecteAction cloneComportement = this.flux.getMediatorFlux().genererClone();
                    
                    this.flux.setMediatorFluxAction(cloneComportement);

                    nouvellesItems = cloneComportement.executeActions(this.flux);
                    
                    //On sauvegarde les résultats
                    
                   this.flux.getMediatorFluxAction().persiter(flux);

                    // Tout s'est bien déroulé on va donc fermer les incidents du flux et renvoyé true
                    ServiceGestionIncident.fermerLesIncidentsDuFlux(flux);
                    return this;

                } else { // Si la tache est annulée.
                    return null;
                }
            } catch (Exception e) {
                this.setExeption(e);
                return this;
            } finally { // Dans tous les cas on notifi
                setChanged();
                notifyObservers();
            }
        }
    }

    public List<Item> getNouvellesItems() {
        return nouvellesItems;
    }

    public void setNouvellesItems(List<Item> nouvellesItems) {
        this.nouvellesItems = nouvellesItems;
    }

    public Flux getFlux() {
        return flux;
    }

    public void setFlux(Flux flux) {
        this.flux = flux;
    }

    public Date getDateDerniereRecup() {
        return DateDerniereRecup;
    }

    public void setDateDerniereRecup(Date DateDerniereRecup) {
        this.DateDerniereRecup = DateDerniereRecup;
    }

    public AbstrIncident getIncident() {
        return incident;
    }

    public void setIncident(AbstrIncident incident) {
        this.incident = incident;
    }

    /**
     * *
     * Indique si cette tache est une tache schedulé devant être rajouté à la
     * fin de son traitment dans le pool schedulé. Si false, c'est une tâche
     * manuelle, elle ne sera pas réajouté
     *
     * @return
     */
    public Boolean getTacheSchedule() {
        return tacheSchedule;
    }

    /**
     * *
     * Indique si cette tache est une tache schedulé devant être rajouté à la
     * fin de son traitment dans le pool schedulé. Si false, c'est une tâche
     * manuelle, elle ne sera pas réajouté
     *
     * @param tacheSchedule
     */
    public void setTacheSchedule(Boolean tacheSchedule) {
        this.tacheSchedule = tacheSchedule;
    }

    /**
     * *
     * Le boolenn persit indique si la tâche doit ou non enregistrer ses données
     * dans la base de données
     *
     * @return
     */
    public Boolean getPersit() {
        return persit;
    }

    /**
     * *
     * Le boolenn persit indique si la tâche doit ou non enregistrer ses données
     * dans la base de données
     *
     * @param persit
     */
    public void setPersit(Boolean persit) {
        this.persit = persit;
    }

    /**
     * *
     * Permet d'annuler la tâche. Lors de son déclanchement, elle ne va rien
     * faire et ne plus être ajouté au scheduler
     *
     * @return
     */
    public Boolean getAnnulerTache() {
        return annulerTache;
    }

    /**
     * *
     * Permet d'annuler la tâche. Lors de son déclanchement, elle ne va rien
     * faire et ne plus être ajouté au scheduler
     *
     * @param annulerTache
     */
    public void setAnnulerTache(Boolean annulerTache) {
        this.annulerTache = annulerTache;
    }

//    @Override
//    public void fermerLesIncidentOuvert() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public AbstrIncident getIncidenOuvert() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public Class getTypeIncident() {
        return CollecteIncident.class;
    }


}
