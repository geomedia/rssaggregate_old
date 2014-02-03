package rssagregator.beans.incident;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import rssagregator.beans.Conf;
import rssagregator.dao.DAOFactory;
import rssagregator.services.tache.TacheAlerteMail;

/**
 * *
 * Tout incident doit implémenter cette classe. Elle permet de définir les paramètres communs ID, messageErreur,
 * dateDebut, dateFin...
 *
 * @author clem
 */
//@Cacheable(value = true)
//@Cache(shared = true)
@Entity(name = "i_superclass")
//@MappedSuperclass()
//@Cache(size = 100, type = CacheType.CACHE, isolation = CacheIsolationType.SHARED, coordinationType = CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES)
@Inheritance(strategy = InheritanceType.JOINED) // Peu de champs supplémentaires dans les autres entités, on va conserver la stratégie la plus simple
public abstract class AbstrIncident implements Serializable {

    public static final String desc = "ddd";

    /**
     * *
     * Constructeur par défaut d'un incident. IL FAUT PASSER PAR LA FACTORY {@link IncidentFactory} POUR INSTANCIER UN
     * INCIDENT
     */
    public AbstrIncident() {
//        this.notificationImperative = true;
    }
    @Transient
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AbstrIncident.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    /**
     * Lorsque l'incident est constaté (création ou incrémentation du compteur nombreTentativeEnEchec), cette méthode
     * est lancée. En cas d'erreur http, il est par exemple souhaitable de relancer une fois la récupération.
     */
    public void action() {
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }
    /**
     * *
     * Message destiné aux administrateurs afin d'expliquer l'erreur. Il ne s'agit pas d'un log mais d'une explication
     * en français pouvant être comprise par un non-informatien
     */
    @Column(name = "messageEreur", columnDefinition = "text")
    protected String messageEreur;
    /**
     * *
     * Un espace de note permettant aux administrateurs de commenter l'erreur depuis l'interface web.
     */
    @Column(name = "noteIndicent", columnDefinition = "text")
    protected String noteIndicent;
    /**
     * *
     * Log de l'erreur java. Il s'agit du message pouvant être récupéré depuis l'exception à l'origine de l'incident.
     */
    @Column(name = "logErreur", columnDefinition = "text")
    protected String logErreur;
    /**
     * *
     * Nombre de répétition de l'incident. A chaque échec la tâche concerné va incrémenter ce compteur tant qu'elle
     * n'est pas parvenu a retrouver un comportement normal (qui alors clos l'incident en ajoutant une dateFin).
     */
    @Column(name = "nombreTentativeEnEchec")
    protected Integer nombreTentativeEnEchec;
    /**
     * *
     * Un timestamp permettant de stoquer la dernière date de notification. Permet d'éviter le lancement d'allerte
     * redondante par la tache {@link TacheAlerteMail}
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date lastNotification;
    /**
     * *
     * Date de début de l'incident.
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "dateDebut", nullable = false)
    protected Date dateDebut;
    /**
     * *
     * Date de fin de l'incident. Cette variable est complété lorsqu'une tâche parvient à retrouver un comportement
     * normal
     */
    @Column(name = "dateFin", nullable = true)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date dateFin;
    /**
     * *
     * Dernière modification de l'entite. Permet l'Optimitic Lock
     */
    @Version
    Timestamp modified;

    /**
     * *
     * @see #modified
     * @return
     */
    public Timestamp getModified() {
        return modified;
    }

    /**
     * *
     * @see #modified
     * @param modified
     */
    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    /**
     * *
     * @see #messageEreur
     * @return
     */
    public String getMessageEreur() {
        return messageEreur;
    }

    /**
     * *
     * @see #messageEreur
     * @param messageEreur
     */
    public void setMessageEreur(String messageEreur) {
        this.messageEreur = messageEreur;
    }

    /**
     * *
     * @see #noteIndicent
     * @return
     */
    public String getNoteIndicent() {
        return noteIndicent;
    }

    /**
     * *
     * @see #noteIndicent
     * @param noteIndicent
     */
    public void setNoteIndicent(String noteIndicent) {
        this.noteIndicent = noteIndicent;
    }

    public String getLogErreur() {
        return logErreur;
    }

    /**
     * *
     * @see #logErreur
     * @param logErreur
     */
    public void setLogErreur(String logErreur) {
        this.logErreur = logErreur;
    }

    /**
     * *
     * @see #nombreTentativeEnEchec
     * @return
     */
    public Integer getNombreTentativeEnEchec() {
        return nombreTentativeEnEchec;
    }

    /***
     * @see #nombreTentativeEnEchec
     * @param nombreTentativeEnEchec 
     */
    public void setNombreTentativeEnEchec(Integer nombreTentativeEnEchec) {
        this.nombreTentativeEnEchec = nombreTentativeEnEchec;
    }

    /***
     * @see #lastNotification
     * @return 
     */
    public Date getLastNotification() {
        return lastNotification;
    }

    /***
     * @see #lastNotification
     * @param lastNotification 
     */
    public void setLastNotification(Date lastNotification) {
        this.lastNotification = lastNotification;
    }

    /***
     * @see #dateDebut
     * @return 
     */
    public Date getDateDebut() {
        return dateDebut;
    }

    /***
     * @see #dateDebut
     * @param dateDebut 
     */
    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    /***
     * @see #dateFin
     * @return 
     */
    public Date getDateFin() {
        return dateFin;
    }

    /***
     * @see #dateFin
     * @param dateFin 
     */
    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    /**
     * *
     * Retourne l'url permettant d'accéder à l'interface administrative de cette entitée (exemple
     * http://host/RSSagragate/incident?id=1
     *
     * @return
     * @throws IOException Emmet une exeption si la variable servurl n'a pu être chargée depuis le fichier
     */
    public String getUrlAdmin() throws IOException {
        Conf c = DAOFactory.getInstance().getDAOConf().getConfCourante();
        String url = c.getServurl();
        //On rajoute un / a la fin de l'url si besoin est
        if (url != null && url.length() > 1) {
            Character ch = url.charAt(url.length() - 1);
            if (!ch.equals(new Character('/'))) {
                url += "/";
            }
        }
        String retour = url + "incidents/read?id=" + ID.toString() + "&type=" + this.getClass().getSimpleName();
        return retour;
    }

    /**
     * *
     * Une méthode qui renvoir la durée sous forme d'une chaine de caractère. la chaine de caractère comprend l'unité
     *
     * @return "6 minute" , "3 heures" ou encore "4 jours"
     */
    public String getDureeHumanReadable() {

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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.messageEreur != null ? this.messageEreur.hashCode() : 0);
        hash = 89 * hash + (this.logErreur != null ? this.logErreur.hashCode() : 0);
        hash = 89 * hash + (this.dateDebut != null ? this.dateDebut.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstrIncident other = (AbstrIncident) obj;
        if ((this.messageEreur == null) ? (other.messageEreur != null) : !this.messageEreur.equals(other.messageEreur)) {
            return false;
        }
        if ((this.logErreur == null) ? (other.logErreur != null) : !this.logErreur.equals(other.logErreur)) {
            return false;
        }
        if (this.dateDebut != other.dateDebut && (this.dateDebut == null || !this.dateDebut.equals(other.dateDebut))) {
            return false;
        }
        return true;
    }

    /**
     * *
     * </p> Methode pourvant être redéclarer dans les incident afin d'empécher la notification de l'indident par la
     * tache {@link TacheAlerteMail}. La méthode redéclarée peut observer la dateDebut de l'incident ou le nombre de
     * répétition pour déterminer si il faut ou non notifier par mail.</p>
     * <p>Si elle n'est pas redéclarée, cette méthode renvoie true</p>
     *
     * @return true si pas de redéclaration de la méthode par les classe filles.
     */
    public Boolean doitEtreNotifieParMail() {
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * *
     * Retourne une description du type de l'incident.
     *
     * @return
     */
    public String incidDesc() {
        return desc;
    }
}