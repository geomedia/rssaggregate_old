/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.ServiceGestionIncident;

/**
 *
 * @author clem
 */
public class TacheRecupCallable extends Observable implements Callable<Boolean> {

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
    /**
     * *
     * Lorsqu'une exeption survient, on stocke sa référence ici
     */
    public AbstrIncident incident;
    Boolean tacheSchedule;
    Boolean persit;

    public TacheRecupCallable(Flux flux) {
        this.flux = flux;
        this.addObserver(flux);
        incident = null;
        tacheSchedule = false;
        persit = true;
    }

    /**
     * *
     *
     * @return un booleen true si ok false si exeption capturé et géré. mais pas
     * vraiment utilisé dans le reste du programme
     * @throws Exception
     */
    @Override
    public Boolean call() throws Exception {
        // On block le flux pour eviter que la tache automanique et la tache manuelle agissent en même temps
        synchronized (this.flux) {



            flux.setTacheRechup(this);
            //On crée une copie du mediator devant être employé. C'est notre façon d'être thread safe
            try {
                this.flux.setMediatorFluxAction(this.flux.getMediatorFlux().genererClone());

            } catch (Exception e) {
                logger.error("Erreur lors de la génération du clone");
            }

            //On lance la capture en utilisant le mediator qui vient d'être crée. Les erreurs sont générée ici
            try {
                nouvellesItems = this.flux.getMediatorFluxAction().executeActions(this.flux);
            } catch (Exception e) {
                //si erreur on leve l'exeption et on lance l'enregistremnet de celle ci. on stoppe la tache
                this.incident = ServiceGestionIncident.getInstance().gererIncident(e, flux);

                // On réajoute la tache c'est le flux est schedulé
                if (flux.getActive() && this.tacheSchedule) {
                    ServiceCollecteur.getInstance().addScheduledCallable(this);
                }


                logger.error("capture de l'erreur du mediateur : " + e); //Logger.getLogger(TacheRecupCallable.class.getName()).log(Level.SEVERE, null, e);
                return false;
            }


            // Tout s'est bien déroulé on va donc fermer les incidents du flux et renvoyé true
            ServiceGestionIncident.fermerLesIncidentsDuFlux(flux);

            // On réajoute la tache c'est le flux est schedulé
            if (flux.getActive() && this.tacheSchedule) {
                ServiceCollecteur.getInstance().addScheduledCallable(this);
            }

            // On returne true si tout est ok. 
            return true; // cette variable n'est pas vraiment utilisé
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

    public static void main(String[] args) {
        Flux f = new Flux();
        f.setUrl("http://rss.lemkonde.fr/c/205/f/3050/index.rss");


        TacheRecupCallable t = new TacheRecupCallable(f);
        try {
            t.call();
        } catch (Exception ex) {
            System.out.println("TACHERECUPCALLABLE : CAPTURE D'UNE EXEPTION");
            Logger.getLogger(TacheRecupCallable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Boolean getTacheSchedule() {
        return tacheSchedule;
    }

    public void setTacheSchedule(Boolean tacheSchedule) {
        this.tacheSchedule = tacheSchedule;
    }

    public Boolean getPersit() {
        return persit;
    }

    public void setPersit(Boolean persit) {
        this.persit = persit;
    }
}
