/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 * Cette entitée permet de stoquer un intervale de date permettant de renseigner
 * la ou les périodes pendant lesquel le flux a été capté. Un flux peut être
 * capté de sa création à aujourd'hui mais aussi être retiré et remis en
 * capture. Exemple il est capté de du 01/01/2010 au 02/02/2012 puis sa capture
 * reprend au 03/03/2013
 *
 * @author clem
 */
@Entity
public class FluxPeriodeCaptation implements Serializable{
    
    @ManyToOne(optional = false)
    private Flux flux;

    
        public FluxPeriodeCaptation() {
    }
    
    /***
     * Date de début de la période de captation du flux
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateDebut;
    
    
    /***
     * Date de fin de la période de captation du flux
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date datefin;
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDatefin() {
        return datefin;
    }

    public void setDatefin(Date datefin) {
        this.datefin = datefin;
    }

    public Flux getFlux() {
        return flux;
    }

    public void setFlux(Flux flux) {
        this.flux = flux;
    }


    
    
}
