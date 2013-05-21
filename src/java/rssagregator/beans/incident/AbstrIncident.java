package rssagregator.beans.incident;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;

@Entity(name = "incident")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Peu de champs supplémentaires dans les autres entités, on va conserver la stratégie la plus simple
public abstract class AbstrIncident implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateDebut;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateFin;
    
    @Column(name = "messageEreur")
    protected String messageEreur;
    
    private String noteIndicent;
    private String logErreur;
    private Boolean bloquant;
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

    public void AbstrIncident() {
    }

    /**
     * Lorsque l'incident est constaté (création ou incrémentation du compteur
     * nombreTentativeEnEchec), cette méthode est lancée. En cas d'erreur http,
     * il est par exemple souhaitable de relancer une fois la récupération.
     */
    public void action() {
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

    public String getMessageEreur() {
        return messageEreur;
    }

    public void setMessageEreur(String messageEreur) {
        this.messageEreur = messageEreur;
    }
    
    
    
}