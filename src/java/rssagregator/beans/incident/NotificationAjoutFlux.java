/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import rssagregator.beans.Flux;
import rssagregator.beans.Journal;
import rssagregator.services.TacheDecouverteAjoutFlux;

/**
 * Ce type d'incident permet de notifier à l'administrateur que de nouveau Flux ont été découvert et ajouté par la tâche
 * {@link TacheDecouverteAjoutFlux}
 *
 * @author clem
 */
@Entity(name = "i_ajoutflux")
public class NotificationAjoutFlux extends AbstrIncident implements Notification {

    @OneToOne()
    Journal journal;
    /**
     * *
     * Liste des flux ajouté
     */
//    @OneToMany(mappedBy = "notificationAjoutFlux")
    
    @OneToMany(targetEntity = Flux.class, fetch = FetchType.EAGER, cascade = {})
    List<Flux> fluxAjoute = new ArrayList<Flux>();

    public List<Flux> getFluxAjoute() {
        return fluxAjoute;
    }

    public void setFluxAjouté(List<Flux> fluxAjouté) {
        this.fluxAjoute = fluxAjouté;
    }
    
    

//    public List<Flux> getFluxAjouté() {
//        return fluxAjouté;
//    }
//
//    public void setFluxAjouté(List<Flux> fluxAjouté) {
//        this.fluxAjouté = fluxAjouté;
//    }
    public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }
}
