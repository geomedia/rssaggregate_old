package rssagregator.beans.incident;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.config.CacheIsolationType;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import rssagregator.dao.DaoItem;
import rssagregator.utils.PropertyLoader;

/**
 * *
 *
 * @author clem
 */
@Cacheable(value = true)
//@Cache(shared = true)
//@Entity(name = "incident")
@MappedSuperclass()
@Cache(size = 100, type = CacheType.CACHE, isolation = CacheIsolationType.SHARED, coordinationType = CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Peu de champs supplémentaires dans les autres entités, on va conserver la stratégie la plus simple
public class AbstrIncident implements Serializable {

    @Transient
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AbstrIncident.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

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
    @Column(name = "messageEreur")
    protected String messageEreur;
    @Column(name = "noteIndicent", length = 3000)
    protected String noteIndicent;
    @Column(name = "logErreur", columnDefinition = "text")
    protected String logErreur;
    @Column(name = "bloquant")
    protected Boolean bloquant;
    @Column(name = "nombreTentativeEnEchec")
    protected Integer nombreTentativeEnEchec;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date lastNotification;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date dateDebut;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date dateFin;
//    @Transient
//    protected String duree;
    /**
     * nombre de 1 à 5 . 0 = normal : On ne notifie l'erreur qu'une fois par
     * jour. ( test currenttime > lastnotification + 24h) 1 = grave. On notifie
     * toute les 12h 2 = très grave. Le notifieur doit envoyer un mail à chaque
     * fois qu'il rencontre l'erreur. Incident.action peut aussi tester ce
     * champs afin de forcer le lancement du notifieur pour que la notification
     * parte instantanément.
     */
    private Integer gravite;

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

    public Integer getGravite() {
        return gravite;
    }

    public void setGravite(Integer gravite) {
        this.gravite = gravite;
    }
    
    /***
     * Retourne l'url permettant d'accéder à l'interface administrative de cette entitée
     * @return
     * @throws IOException Emmet une exeption si la variable servurl n'a pu être chargée depuis le fichier serv.properties
     */
    public String getUrlAdmin() throws IOException{
        String url = PropertyLoader.loadProperti("serv.properties", "servurl");
        //On rajoute un / a la fin de l'url si besoin est
        if(url!=null && url.charAt(url.length()-1)!='/'){
            url+="/";
        }
        String retour = url+"incidents/read?id="+ID.toString(); 
        System.out.println("URL : " + url);
        return retour;
    }

    /**
     * *
     * Une méthode qui renvoir la durée sous forme d'une chaine de caractère. la
     * chaine de caractère comprend l'unité
     *
     * @return "6 minute" , "3 heures" ou encore "4 jours"
     */
    public String getDuree() {

        Date datefin = dateFin;
        if (datefin == null);
        datefin = new Date();

        if (dateDebut != null && datefin != null) {

            DateTime start = new DateTime(this.dateDebut);
            DateTime end = new DateTime(this.dateFin);

            Duration dur = new Duration(start, end);
            if (dur.getStandardDays() > 0) {
                return dur.getStandardDays() + " jours";
            }
            if (dur.getStandardHours() > 0) {
                return dur.getStandardHours() + " heures";
            }

            if (dur.getStandardMinutes() > 0) {
                return dur.getStandardMinutes() + " minutes";
            }
            if (dur.getStandardSeconds() > 0) {
                return dur.getStandardSeconds() + " secondes";
            }
        }
        return null;
    }
}