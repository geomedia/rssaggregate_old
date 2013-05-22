package rssagregator.beans.incident;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import rssagregator.beans.Flux;

/** 
 *  Les erreurs de captation sont consigné dans des objets redéfinissant cette classe abstraite. Il peut s'agir d'erreur de parsage, d'erreur http (404, site indisponible etc...)
 */
@Entity (name = "incidentflux")
public class FluxIncident implements Serializable {

    
        @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    public FluxIncident() {
    }
    

    
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date dateDebut;
    
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date dateFin;
    
    @Column(name = "messageEreur")
    protected String messageEreur;
    
    @Column(name = "noteIndicent")
    private String noteIndicent;
    
    @Column(name = "logErreur")
    private String logErreur;
    
    @Column(name = "bloquant")
    private Boolean bloquant;
    
    @Column(name = "nombreTentativeEnEchec")
    private Integer nombreTentativeEnEchec;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date lastNotification;
    /**
     * nombre de 1 à 5 . 0 = normal : On ne notifie l'erreur qu'une fois par
     * jour. ( test currenttime > lastnotification + 24h) 1 = grave. On notifie
     * toute les 12h 2 = très grave. Le notifieur doit envoyer un mail à chaque
     * fois qu'il rencontre l'erreur. Incident.action peut aussi tester ce
     * champs afin de forcer le lancement du notifieur pour que la notification
     * parte instantanément.
     */
    private Integer gravite;
    
    
  /** 
   *  Un objet flux peut posséder différents incidents. Un incident ne possède qu'un flux. 
   */
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Flux fluxLie;
      
    
    
 
//  private Flux flux;

    public Flux getFluxLie() {
        return fluxLie;
    }

    public void setFluxLie(Flux fluxLie) {
        this.fluxLie = fluxLie;
    }

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

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getMessageEreur() {
        return messageEreur;
    }

    public void setMessageEreur(String messageEreur) {
        this.messageEreur = messageEreur;
    }

    public String getNoteIndicent() {
        return noteIndicent;
    }

    public void setNoteIndicent(String noteIndicent) {
        this.noteIndicent = noteIndicent;
    }

    public String getLogErreur() {
        return logErreur;
    }

    public void setLogErreur(String logErreur) {
        this.logErreur = logErreur;
    }

    public Boolean getBloquant() {
        return bloquant;
    }

    public void setBloquant(Boolean bloquant) {
        this.bloquant = bloquant;
    }

    public Integer getNombreTentativeEnEchec() {
        return nombreTentativeEnEchec;
    }

    public void setNombreTentativeEnEchec(Integer nombreTentativeEnEchec) {
        this.nombreTentativeEnEchec = nombreTentativeEnEchec;
    }

    public Date getLastNotification() {
        return lastNotification;
    }

    public void setLastNotification(Date lastNotification) {
        this.lastNotification = lastNotification;
    }

    public Integer getGravite() {
        return gravite;
    }

    public void setGravite(Integer gravite) {
        this.gravite = gravite;
    }
}