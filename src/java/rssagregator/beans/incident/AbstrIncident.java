package rssagregator.beans.incident;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.config.CacheIsolationType;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import rssagregator.beans.Conf;
import rssagregator.dao.DAOFactory;
import rssagregator.services.TacheAlerteMail;

/**
 * *
 * Tout incident doit implémenter cette classe. Elle permet de définir les paramètres communs ID, messageErreur,
 * dateDebut, dateFin...
 *
 * @author clem
 */
@Cacheable(value = true)
//@Cache(shared = true)
@Entity(name = "i_superclass")
//@MappedSuperclass()
@Cache(size = 100, type = CacheType.CACHE, isolation = CacheIsolationType.SHARED, coordinationType = CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES)
@Inheritance(strategy = InheritanceType.JOINED) // Peu de champs supplémentaires dans les autres entités, on va conserver la stratégie la plus simple
public class AbstrIncident implements Serializable {

    /**
     * *
     * Constructeur par défaut d'un incident. La variable notificationImperative est définie à false
     */
    public AbstrIncident() {
        this.notificationImperative = false;
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
    @Column(name = "messageEreur")
    protected String messageEreur;
    /**
     * *
     * Un espace de note permettant aux administrateurs de commenter l'erreur depuis l'interface web.
     */
    @Column(name = "noteIndicent", length = 3000)
    protected String noteIndicent;
    /**
     * *
     * Log de l'erreur java. Il s'agit du message pouvant être récupéré depuis l'exception à l'origine de l'incident.
     */
    @Column(name = "logErreur", columnDefinition = "text")
    protected String logErreur;
    /**
     * *
     * N'EST PLUS UTILISÉ !
     *
     * @deprecated
     */
    @Deprecated
    @Column(name = "bloquant")
    protected Boolean bloquant;
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
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date dateFin;
    /**
     * *
     * Permet de forcer un incident à être notifié, même si il est déjà clos.
     */
    @Column(name = "notificationImperative")
    protected Boolean notificationImperative;
//    @Transient
//    protected String duree;
    /**
     * /!\ N'EST PLUS UTILISÉ nombre de 1 à 5 . 0 = normal : On ne notifie l'erreur qu'une fois par jour. ( test
     * currenttime > lastnotification + 24h) 1 = grave. On notifie toute les 12h 2 = très grave. Le notifieur doit
     * envoyer un mail à chaque fois qu'il rencontre l'erreur. Incident.action peut aussi tester ce champs afin de
     * forcer le lancement du notifieur pour que la notification parte instantanément.
     */
    @Deprecated
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

    public Boolean getNotificationImperative() {
        return notificationImperative;
    }

    public void setNotificationImperative(Boolean notificationImperative) {
        this.notificationImperative = notificationImperative;
    }

    public void setGravite(Integer gravite) {
        this.gravite = gravite;
    }

    /**
     * *
     * Retourne l'url permettant d'accéder à l'interface administrative de cette entitée (exemple
     * http://host/RSSagragate/incident?id=1
     *
     * @return
     * @throws IOException Emmet une exeption si la variable servurl n'a pu être chargée depuis le fichier
     * serv.properties
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

//    /**
//     * *<strong>/!\ N'EST PLUS UTILISÉ. LES INCIDENTS NE SONT PLUS DES OBSERVABLES</strong>
//     * Un incident doit être enregistré auprès des service : <ul>
//     * <li>Mail : car lors de la création d'un inscident le service mail doit
//     * réagir en envoyant une premiere alerte d'urgence</li>
//     * </ul>
//     */
//    @Deprecated
//    public void EnregistrerAupresdesService() {
//        //TODO : Les incident ne sont plus des observables non ?
//        this.addObserver(ServiceMailNotifier.getInstance());
//    }
//
//    /***
//     * <strong>/!\ N'EST PLUS UTILISÉ. LES INCIDENTS NE SONT PLUS DES OBSERVABLES</strong>
//     * Force le changeStatut utilisé par le patern Observer
//     */
//    @Deprecated
//    public void forceChangeStatut() {
//        this.setChanged();
//    }
    @Override
    public int hashCode() {
        int hash = 7;
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
        if ((this.noteIndicent == null) ? (other.noteIndicent != null) : !this.noteIndicent.equals(other.noteIndicent)) {
            return false;
        }
        if ((this.logErreur == null) ? (other.logErreur != null) : !this.logErreur.equals(other.logErreur)) {
            return false;
        }
//        if (this.bloquant != other.bloquant && (this.bloquant == null || !this.bloquant.equals(other.bloquant))) {
//            return false;
//        }
        if (this.dateDebut != other.dateDebut && (this.dateDebut == null || !this.dateDebut.equals(other.dateDebut))) {
            return false;
        }
        return true;
    }

    /**
     * *
     *</p> Methode pourvant être redéclarer dans les incident afin d'empécher la notification de l'indident par
     * la tache {@link TacheAlerteMail}. La méthode redéclarée peut observer la dateDebut de l'incident ou le nombre de
     * répétition pour déterminer si il faut ou non notifier par mail.</p>
     * <p>Si elle n'est pas redéclarée, cette méthode renvoie true</p>
     *
     * @return true si pas de redéclaration de la méthode par les classe filles.
     */
    public Boolean doitEtreNotifieParMail() {
        return true;
    }
}