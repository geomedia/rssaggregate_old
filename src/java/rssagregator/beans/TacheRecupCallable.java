/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.util.ArrayList;
import rssagregator.dao.DAOFactory;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.dao.DaoItem;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.ServiceGestionIncident;

/**
 *
 * @author clem
 */
public class TacheRecupCallable extends Observable implements Callable<List<Item>> {

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

    @Override
    public List<Item> call() throws Exception {
        // On block le flux pour eviter que la tache automanique et la tache manuelle agissent en même temps
        synchronized (this.flux) {
            nouvellesItems = new ArrayList<Item>();
            flux.setTacheRechup(this);
            //On crée une copie du mediator devant être employé. C'est notre façon d'être thread safe
            this.flux.setMediatorFluxAction(this.flux.getMediatorFlux().genererClone());

            //On lance la capture en utilisant le mediator qui vient d'être crée. Les erreurs sont générée ici
            try {
                nouvellesItems = this.flux.getMediatorFluxAction().executeActions(this.flux);
            } catch (Exception e) {
                //si erreur on leve l'exeption et on lance l'enregistremnet de celle ci. on stoppe la tache
                this.incident = ServiceGestionIncident.getInstance().gererIncident(e, flux);
                logger.error(e);
                throw e;
            }

            // On enregistre ces nouvelles items
            int i;
            DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
            for (i = 0; i < nouvellesItems.size(); i++) {
                //On précise à la nouvelle item qu'elle appartient au flux collecté
                nouvellesItems.get(i).getListFlux().add(flux);
                if (persit) {
                    daoItem.enregistrement(nouvellesItems.get(i), flux);
//                    this.flux.getLastEmpruntes().add(0, nouvellesItems.get(i).getHashContenu());
                }
            }

            // On supprime des hash pour éviter l'accumulation. On en laisse 10 en plus du nombre d'item contenues dans le flux.
            Integer nbr = flux.getMediatorFluxAction().getDedoubloneur().getCompteCapture()[0] + 10;
            if (nbr > 0 && nbr < flux.getLastEmpruntes().size()) {
                for (i = nbr; i < flux.getLastEmpruntes().size(); i++) {
                    flux.getLastEmpruntes().remove(i);
                }
            }


            ServiceGestionIncident.fermerLesIncidentsDuFlux(flux);
//            flux.fermerLesIncidentOuvert();

            //Devra être supprimé à la fin
            DebugRecapLeveeFlux debug = new DebugRecapLeveeFlux();
            debug.setDate(new Date());
            debug.setNbrRecup(nouvellesItems.size());
            flux.getDebug().add(debug);


            // TODO le fait de réajouté la tache doit aussi être géré par le service de gestion des incidents
            // Si il s'agit d'une tache schedule, il faut la réajouter au scheduler
            if (tacheSchedule) {
                ServiceCollecteur.getInstance().addScheduledCallable(this);
            }

            // On supprimer les items capturée du cache de l'ORM pour éviter l'encombrement
            for (i = 0; i < nouvellesItems.size(); i++) {
                DAOFactory.getInstance().getEntityManager().detach(nouvellesItems.get(i));
            }

            return nouvellesItems;
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
