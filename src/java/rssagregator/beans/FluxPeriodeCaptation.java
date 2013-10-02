/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import org.apache.poi.util.Beta;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.traitement.MediatorCollecteAction;

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
public class FluxPeriodeCaptation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    
    
    
    /***
     * Le flux concerné par cette période de captation
     */
    @ManyToOne(optional = false)
    private Flux flux;
    
    
    /***
     * Il est important de concerver une trace du comportement de collecte utilisé durant cette période de captation. Cette donnée est utilisée dans le dédoublonnage.
     */
    @Beta
    @OneToOne
    private MediatorCollecteAction comportementDurantLaPeriode;
    
    /***
     * Constructeur par défault.
     */
    public FluxPeriodeCaptation() {
        
    }
    
    
    /**
     * *
     * Date de début de la période de captation du flux. Ce champs ne peut être null
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "dateDebut" , nullable = false)
    private Date dateDebut;
    /**
     * *
     * Date de fin de la période de captation du flux. Peut être null si la période est encore ouverte
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date datefin;
    
    
    

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

    public MediatorCollecteAction getComportementDurantLaPeriode() {
        return comportementDurantLaPeriode;
    }

    public void setComportementDurantLaPeriode(MediatorCollecteAction comportementDurantLaPeriode) {
        this.comportementDurantLaPeriode = comportementDurantLaPeriode;
    }
    
    

    @Override
    /**
     * *
     * Retourne l'intervale de date à un format lisible. "dd/MM/yyyy à
     * dd/MM/yyyy". C'est pas très MVC. Normalement c'est le role de la vue de
     * mettre en forme la date. Mais rien n'empeche de le faire par la suite et
     * d'oublier cette redéclaration de toString.
     */
    public String toString() {
        String retour = "";
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMMM yyyy à hh'h'mm");
        if (this.dateDebut != null) {
            DateTime dt = new DateTime(this.getDateDebut());
            retour = fmt.print(dt);
        }
        if (this.datefin != null) {
            DateTime dt = new DateTime(this.datefin);
            retour += " à " + fmt.print(dt);
        } else {
            DateTime dt = new DateTime();
            retour += " à Maintennant (" + fmt.print(dt) + ")";
        }

        return retour;
    }
}
