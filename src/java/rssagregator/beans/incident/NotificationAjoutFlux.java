/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import rssagregator.beans.Flux;
import rssagregator.beans.Journal;
import rssagregator.services.tache.TacheDecouverteAjoutFlux;

/**
 * Ce type d'incident permet de notifier à l'administrateur que de nouveau Flux ont été découvert et ajouté par la tâche
 * {@link TacheDecouverteAjoutFlux}. Ce n'est pas une erreur mais simplement une information devant laisser une trace.
 *
 * @author clem
 */
@Entity(name = "i_ajoutflux")
public class NotificationAjoutFlux extends AbstrIncident implements Notification {

    /***
     * Journal concerné
     */
    @OneToOne()
    Journal journal;
    
    /***
     * Liste des flux ajouté
     */
    @OneToMany(targetEntity = Flux.class, fetch = FetchType.EAGER, cascade = {})
    List<Flux> fluxAjoute = new ArrayList<Flux>();

    /***
     * @see #fluxAjoute
     * @return 
     */
    public List<Flux> getFluxAjoute() {
        return fluxAjoute;
    }

    /***
     * @see #fluxAjoute
     * @param fluxAjouté 
     */
    public void setFluxAjouté(List<Flux> fluxAjouté) {
        this.fluxAjoute = fluxAjouté;
    }
    
    
    /***
     * @see #journal
     * @return 
     */
    public Journal getJournal() {
        return journal;
    }

    /***
     * @see #journal
     * @param journal 
     */
    public void setJournal(Journal journal) {
        this.journal = journal;
    }
}
