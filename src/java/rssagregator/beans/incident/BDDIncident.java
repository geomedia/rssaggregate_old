/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import rssagregator.beans.Flux;
import rssagregator.beans.Journal;

/**
 *
 * @author clem
 */
@Entity(name = "incidentBDD")
public class BDDIncident extends AbstrIncident implements Serializable{

    
    public BDDIncident() {
    }
    
    
    private Class entiteErreur;

    //    @OneToOne
    //    private Flux fluxLie;
    //
    //    @OneToOne
    //    private Journal journalLie;
    //
    //
    //    public Flux getFluxLie() {
    //        return fluxLie;
    //    }
    //
    //    public void setFluxLie(Flux fluxLie) {
    //        this.fluxLie = fluxLie;
    //    }
    public Class getEntiteErreur() {
        return entiteErreur;
    }

    public void setEntiteErreur(Class entiteErreur) {
        this.entiteErreur = entiteErreur;
    }
    
    
    
    
    
}
