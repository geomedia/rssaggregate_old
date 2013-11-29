/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import javax.persistence.Entity;
import rssagregator.beans.Journal;
import rssagregator.services.tache.TacheDecouverteAjoutFlux;

/**
 *  Lors de l'ajout des flux des incidents peuvent se produirent. Incident de la tache {@link TacheDecouverteAjoutFlux}. Cet incident peut survenir lorsque la page du journal n'est pas accessible ou qu'aucun flux n'a pu être trouvé
 * @author clem
 */
@Entity(name = "i_decouverterss")
public class IncidentDecouverteRSS extends AbstrIncident{
    /***
     * L'ajour de flux se fait par rapport a un journal
     */
    Journal journal;

    public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    @Override
    public String incidDesc() {
        return "Lors de la découverte des flux RSS d'un journal, des incidents peuvent survenir notamment lorsque la page n'est pas joignable ou que le serveur du site joins n'est pas disponible";
    }
    
}
