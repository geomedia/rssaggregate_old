/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import rssagregator.beans.UserAccount;
import rssagregator.beans.exception.AucunMailAdministateur;
import rssagregator.beans.exception.UnIncidableException;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.incident.MailIncident;
import rssagregator.beans.incident.ServerIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.services.crud.AbstrServiceCRUD;
import rssagregator.services.crud.ServiceCRUDFactory;
import rssagregator.services.mailtemplate.TemplateMailAlertIncident;
import rssagregator.utils.PropertyLoader;

/**
 * Le service mail gère la vie des Tache : <ul>
 * <li>TacheEnvoyerMail : Envoie d'un mail </li>
 * <li></li>
 * </ul>
 *
 * @author clem
 */
public class ServiceMailNotifier extends AbstrService {

    private static ServiceMailNotifier instance = new ServiceMailNotifier();
//    private ScheduledExecutorService executorService;
    private Properties propertiesMail;
    private final static String MAILER_VERSION = "Java";
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceMailNotifier.class);

    /**
     * *
     * Constructeur privée. Configure l'objet avec le contenu du fichier properties du répertoire de configuration
     */
    private ServiceMailNotifier() {
        super();
        try {
            //On doit charger le fichier de conf.
            //On commence par rechercher l'emplacement du fichier propertymail dans le fichier propertie du projet.
            String fileConf = PropertyLoader.loadProperti("serv.properties", "mailconf");
            propertiesMail = PropertyLoader.loadFromFile(fileConf);
        } catch (IOException ex) {
            Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ServiceMailNotifier getInstance() {
        if (instance == null) {
            instance = new ServiceMailNotifier();
        }
        return instance;
    }

    /**
     * *
     * Permet d'obtenir une instance du service configuré statiquement (les instances normale sont configurée en
     * utilisant le fichier XML servicedef.xml au démarrage de l'appli)
     *
     * @return
     */
    public static ServiceMailNotifier getTestInstance() {
        ServiceMailNotifier service = new ServiceMailNotifier();
        service.executorService = Executors.newScheduledThreadPool(5);

        return service;

    }

    @Override
    public synchronized void update(Observable o, Object arg) {

        //=============================================================================================
        //                      GESTION DES TACHE SCHEDULE
        //=============================================================================================

        if (o instanceof AbstrTacheSchedule) {

            //=====================>VERIFICATION QUOTIDIENNE<================================
            /**
             * Tous les jours, à 8h, cette tâche est lancée pour vérifier nofier ler erreurs en cours.
             */
            if (o.getClass().equals(TacheVerifFluxNotificationMail.class)) {
                TacheVerifFluxNotificationMail tvfnm = (TacheVerifFluxNotificationMail) o;
                if (tvfnm.getExeption() == null) {
                    // C'est maintenant la tache elle me qui s'occupe de sa notification email
//                    TacheEnvoyerMail envoyerMail = new TacheEnvoyerMail(this);
//                    envoyerMail.setContent(tvfnm.getCorps());
//                    envoyerMail.setPropertiesMail(propertiesMail);
//                    try {
//                        envoyerMail.setToMailAdresses(returnMailAdmin());
//                        executorService.submit(envoyerMail);
//                    } catch (AddressException ex) {
//                        logger.error("Erreur lors de la récupération des mail a notifier", ex);
//                        Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                }
                if (tvfnm.getSchedule()) {
                    schedule(tvfnm);
                    // Les mail doivent partir à 8h, pour que les admins aient ca a leur arrivée au boulot. On calcul le temps
//                    DateTime dtCurrent = new DateTime();
//                    DateTime next = dtCurrent.plusDays(1).withHourOfDay(8);// withDayOfWeek(DateTimeConstants.SUNDAY);
//                    Duration dur = new Duration(dtCurrent, next);
//                    executorService.schedule(tvfnm, dur.getStandardSeconds(), TimeUnit.SECONDS);
                }

            } //=========================>ENVOYER UN MAIL<===================================
            /**
             * Gestion du retour de la tache permettant d'envoyer un mail. On tente par trois fois de le réenvoyer. Si
             * c'est toujours un échec, on créer un incident dans la base de données
             */
            else if (o.getClass().equals(TacheEnvoyerMail.class)) {

                TacheEnvoyerMail tacheSend = (TacheEnvoyerMail) o;
                if (tacheSend.getExeption() == null) {
                    logger.debug("Le mail est bien parti");
                    
                    this.schedule(tacheSend); // On reschedule la tache
                    
                } else { // Si le mail n'est pas parti. On tente de le réenvoyer au bout de trois tentatives, on crée un incident

//                    if (tacheSend.getExeption().getClass().equals(AucunMailAdministateur.class)) {
//                        IncidentFactory<ServerIncident> facto = new IncidentFactory<ServerIncident>();
//                        ServerIncident serverIncident = facto.getIncident(ServerIncident.class, "pas de mail admin dans la conf", tacheSend.getExeption());
//                        AbstrServiceCRUD serviceCRUD = ServiceCRUDFactory.getInstance().getServiceFor(serverIncident.getClass());
//                        try {
//                            serviceCRUD.ajouter(serverIncident);
//                        } catch (Exception ex) {
//                            Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    } else
                        
                        
                        if (tacheSend.getNbrTentative() < 3) {
                            
                        executorService.schedule(tacheSend, 30, TimeUnit.SECONDS); // On réexecute la tache 30 seconde plus 
                    } else {
                        try {
                            tacheSend.gererIncident();
                        } catch (Exception ex) {
                            Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } //=======================>ALERT MAIL (30minutes)<==============================
            /**
             * Gestion du retour de la tache lancée toutes les 30 minutes afin d'envoyer un mail alertant les
             * administrateurs des derniers incidents. En fonction de ce retour, on va envoyer un mail. A la fin on
             * modifie la date de dernière notification des incidents.
             */ 
            else if (o.getClass().equals(TacheAlerteMail.class)) {
                TacheAlerteMail cast = (TacheAlerteMail) o;
                logger.debug("Tache Alertmail s'est notifié. Nombre d'incident : " + cast.getIncidents().size());
                //Si la tache s'est déroulé sans exeption ET que de nouveau incident ont été revevé, on envoi un mail
                if (cast.getExeption() == null && cast.getIncidents().size() > 0) {
                    logger.debug("erreur de la tache " + cast.getExeption());
                    // C'est maintenant la tache qui gère l'envoie de son mail rien pour l'instant dans ce bloc
                }
                if (cast.getSchedule()) {
                    schedule(cast);
//                    executorService.schedule(cast, 30, TimeUnit.SECONDS);
                }
            }
//            gererIncident((AbstrTacheSchedule) o);
        }
    }

    /**
     * *
     * Permet d'envoyer un mail. Une thread est créer pour cette tache
     */
//    public void envoyerMail(InternetAddress[] toMailAdresses, String subject, String content) throws AddressException, MessagingException, IOException {
//
//        String fileConf = PropertyLoader.loadProperti("serv.properties", "mailconf");
//        propertiesMail = PropertyLoader.loadFromFile(fileConf);
//
////        final String username = propertiesMail.getProperty("smtpuser");
////        final String password = propertiesMail.getProperty("smtppass");
////
////        Session session = Session.getInstance(propertiesMail,
////                new javax.mail.Authenticator() {
////            @Override
////            protected PasswordAuthentication getPasswordAuthentication() {
////                return new PasswordAuthentication(username, password);
////            }
////        });
//
//        TacheEnvoyerMail tacheEnvoyerMail = new TacheEnvoyerMail();
//        tacheEnvoyerMail.setContent(content);
//        tacheEnvoyerMail.setPropertiesMail(propertiesMail);
//        tacheEnvoyerMail.setSubject(subject);
//        tacheEnvoyerMail.setToMailAdresses(toMailAdresses);
//  
//
//        this.executorService.submit(tacheEnvoyerMail);
//
////        Message message = new MimeMessage(session);
////        message.setFrom(new InternetAddress("clement.rillon@gmail.com"));
//
////        InternetAddress[] internetAddresses = new InternetAddress[1];
////        internetAddresses[0] = new InternetAddress("clement.rillon@gmail.com");
////        message.setRecipients(Message.RecipientType.TO, toMailAdresses);
//
//
////        message.setSubject(subject);
////        message.setText(content);
////        message.setContent(content, "text/html; charset=utf-8");
//
////        message.setHeader("X-Mailer", MAILER_VERSION);
////        message.setSentDate(new Date());
//
////        message.setSentDate(new Date());
////        session.setDebug(true);
////        Transport.send(message);
//    }
    /**
     * Permet à la tache qutotidienne d'envoie des email de se réinscrire auprès du service.
     */
//    public void taskEnd(Callable tache) {
//
//        if (tache instanceof TacheVerifFluxNotificationMail) {
//            // TODO : Il serait bon de mettre en place un calcul de lheure afin d'aviter que l'heure de la tâche ne se décalle. Il faut soustraire la date au moment de la réception avec une date fixé"e dans la configuration. 
//            this.executorService.schedule(tache, 1, TimeUnit.DAYS);
//        }
//    }
    /**
     * *
     * Retourne la liste des adresses mail des admins en se basant sur la dao UserAccount.
     *
     * @return
     */
    protected InternetAddress[] returnMailAdmin() throws AddressException {

        List<UserAccount> list = DAOFactory.getInstance().getDAOUser().findUserANotifier();
        logger.debug("recherche de la liste des mail a joindre list size : " + list.size());
        InternetAddress[] retour = new InternetAddress[list.size()];
        for (int i = 0; i < list.size(); i++) {
            UserAccount userAccount = list.get(i);
            retour[i] = new InternetAddress(userAccount.getMail());
            logger.debug("email : " + retour[i].getAddress());
        }
        return retour;
    }

    public Properties getPropertiesMail() {
        return propertiesMail;
    }

    public void setPropertiesMail(Properties propertiesMail) {
        this.propertiesMail = propertiesMail;
    }

//    @Override
//    public void instancierTaches() {
//
//        //--------> Lancement de la tâche d'alerte mail
//        TacheAlerteMail alerteMail = new TacheAlerteMail(this);
//        alerteMail.setSchedule(Boolean.TRUE);
//        executorService.schedule(alerteMail, 30, TimeUnit.SECONDS);
//
//
//        //---------> Lancement de la tâche de vérification journalière
//        TacheVerifFluxNotificationMail notificationMail = new TacheVerifFluxNotificationMail(this);
//        notificationMail.setSchedule(Boolean.TRUE);
//        //calcul du delay
//        DateTime dtCurrent = new DateTime();
//        DateTime next = dtCurrent.plusDays(1).withHourOfDay(8);// withDayOfWeek(DateTimeConstants.SUNDAY);
//        Duration dur = new Duration(dtCurrent, next);
//        executorService.schedule(notificationMail, dur.getStandardSeconds(), TimeUnit.SECONDS);
//    }
    @Override
    protected void gererIncident(AbstrTacheSchedule tache) {


        if (tache.exeption != null && Incidable.class.isAssignableFrom(tache.getClass())) {
            logger.debug("Gestion d'une erreur");


            //================================================================================================
            //                      INSTANCIATION OU RECUPERATION D'INCIDENT
            //================================================================================================
            MailIncident si = null;
            IncidentFactory factory = new IncidentFactory();
            try {
                si = (MailIncident) factory.createIncidentFromTask(tache, "Le mail n'a pu être envoyé");
            } catch (InstantiationException ex) {
                Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnIncidableException ex) {
                Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
            }



            //=================================================================================================
            // ..................... GESTION DES INCIDENTS
            //=================================================================================================

            if (si != null) {
                synchronized (si) {

                    //TODO : Reprendre ce qui est dans update pour mieu gérer
                    if (tache.getClass().equals(TacheEnvoyerMail.class)) {
                        TacheEnvoyerMail cast = (TacheEnvoyerMail) tache;
//                    logger.error("Le mail ne semble pas être envoyé : ", cast.getExeption());
                        si.setMessage(cast.getContent());
                        si.setObjet(cast.getSubject());
                        si.setLogErreur(cast.getExeption().toString());
                        si.setMessage("Le mail n'a pas été envoyé");
                    }


                    //=================================================================================================
                    //...............................Enregistrment de l'incident
                    //=================================================================================================

                    DAOIncident dao = (DAOIncident) DAOFactory.getInstance().getDAOFromTask(tache);
                    try {
                        if (si.getID() == null) {
                            dao.beginTransaction();
                            dao.creer(si);
                            dao.commit();
                        } else {
                            dao.beginTransaction();
                            dao.modifier(si);
                            dao.commit();
                        }
                    } catch (Exception ex) {
                        logger.error("Erreur lors de la création : ", ex);
                    }


//                DAOIncident dao = (DAOIncident) DAOFactory.getInstance().getDAOFromTask(tache);
//                try {
//                    dao.creer(si);
//                } catch (Exception ex) {
//                    logger.error("Erreur lors de la création : " + ex);
//                    Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
//                }
                }
            }


            //=================================================================================================
            //.........................Terminaison correct des TACHE et FERMETURE DE L'INCIDENT
            //=================================================================================================

        }

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String[] args) throws AddressException {
        TacheEnvoyerMail envoyerMail = new TacheEnvoyerMail(ServiceMailNotifier.getInstance());
        envoyerMail.setContent("youpi");
        envoyerMail.setSubject("obj test");
        envoyerMail.setToMailAdresses(new InternetAddress[]{new InternetAddress("clement.rillon@gmail.com")});
        envoyerMail.setPropertiesMail(ServiceMailNotifier.getInstance().propertiesMail);
        ServiceMailNotifier.getInstance().getExecutorService().submit(envoyerMail);

    }

    @Override
    public void stopService() throws SecurityException, RuntimeException {
        super.stopService();

    }
}
