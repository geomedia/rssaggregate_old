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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import rssagregator.beans.UserAccount;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.incident.MailIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
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
    private ScheduledExecutorService executorService;
    private Properties propertiesMail;
    private final static String MAILER_VERSION = "Java";
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceMailNotifier.class);

    /**
     * *
     * Constructeur privée. Configure l'objet avec le contenu du fichier
     * properties du répertoire de configuration
     */
    private ServiceMailNotifier() {
        try {
            executorService = Executors.newSingleThreadScheduledExecutor();

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

    @Override
    public synchronized void update(Observable o, Object arg) {
        logger.debug("update");

        //=============================================================================================
        //                      GESTION DES TACHE SCHEDULE
        //=============================================================================================

        if (o instanceof AbstrTacheSchedule) {

            //=====================>VERIFICATION QUOTIDIENNE<================================
            /**
             * Tous les jours, à 8h, cette tâche est lancée pour vérifier nofier
             * ler erreurs en cours.
             */
            if (o.getClass().equals(TacheVerifFluxNotificationMail.class)) {
                TacheVerifFluxNotificationMail tvfnm = (TacheVerifFluxNotificationMail) o;
                if (tvfnm.getExeption() == null) {
                    TacheEnvoyerMail envoyerMail = new TacheEnvoyerMail(this);
                    envoyerMail.setContent(tvfnm.getCorps());
                    envoyerMail.setPropertiesMail(propertiesMail);
                    envoyerMail.setToMailAdresses(tvfnm.getAddress());
                    executorService.submit(envoyerMail);
                }
                if (tvfnm.getSchedule()) {
                    // Les mail doivent partir à 8h, pour que les admins aient ca a leur arrivée au boulot. On calcul le temps
                    DateTime dtCurrent = new DateTime();
                    DateTime next = dtCurrent.plusDays(1).withHourOfDay(8);// withDayOfWeek(DateTimeConstants.SUNDAY);
                    Duration dur = new Duration(dtCurrent, next);
                    executorService.schedule(tvfnm, dur.getStandardSeconds(), TimeUnit.SECONDS);
                }

            } //=========================>ENVOYER UN MAIL<===================================
            /**
             * Gestion du retour de la tache permettant d'envoyer un mail. On
             * tente par trois fois de le réenvoyer. Si c'est toujours un échec,
             * on créer un incident dans la base de données
             */
            else if (o.getClass().equals(TacheEnvoyerMail.class)) {

                TacheEnvoyerMail tacheSend = (TacheEnvoyerMail) o;
                if (tacheSend.getExeption() == null) {
                    logger.debug("Le mail est bien parti");
                } else { // Si le mail n'est pas parti. On tente de le réenvoyer au bout de trois tentatives, on crée un incident

                    if (tacheSend.getNbrTentative() < 3) {
                        executorService.schedule(tacheSend, 30, TimeUnit.SECONDS); // On réenvoie le mail dans 30 secondes
                    }
                }
            } //=======================>ALERT MAIL (30minutes)<==============================
            /**
             * Gestion du retour de la tache lancée toutes les 30 minutes afin
             * d'envoyer un mail alertant les administrateurs des derniers
             * incidents. En fonction de ce retour, on va envoyer un mail. A la
             * fin on modifie la date de dernière notification des incidents.
             */
            else if (o.getClass().equals(TacheAlerteMail.class)) {
                TacheAlerteMail cast = (TacheAlerteMail) o;
                logger.debug("Tache Alertmail s'est notifié. Nombre d'incident : " + cast.getIncidents().size());
                //Si la tache s'est déroulé sans exeption ET que de nouveau incident ont été revevé, on envoi un mail
                if (cast.getExeption() == null && cast.getIncidents().size() > 0) {
                    try {
                        TacheEnvoyerMail mailSendTask = new TacheEnvoyerMail(this);
                        TemplateMailAlertIncident template = new TemplateMailAlertIncident();
                        template.setListIncident(cast.getIncidents());
                        mailSendTask.setContent(template.getCorpsMail()); 
                        mailSendTask.setPropertiesMail(propertiesMail);
                        mailSendTask.setToMailAdresses(returnMailAdmin());
                        mailSendTask.setSubject("ALERTE : de nouveaux incidents se sont produits");
                        logger.debug("mail avant submit");
                        ExecutorService exe = Executors.newCachedThreadPool();
                        exe.submit(mailSendTask);
//                        Future<TacheEnvoyerMail> futurmail = executorService.submit(mailSendTask);
//                        futurmail.get();
                        logger.debug("mail après submit");
                        List<AbstrIncident> listIncid = cast.getIncidents();
                        for (int i = 0; i < listIncid.size(); i++) {
                            AbstrIncident abstrIncident = listIncid.get(i);
                            // Pour chacun de ces incident il faut modifier la date de last update
                            synchronized (abstrIncident) {
                                abstrIncident.setLastNotification(new Date());
                                DAOIncident<AbstrIncident> dao = (DAOIncident<AbstrIncident>) DAOFactory.getInstance().getDaoFromType(AbstrIncident.class);
                                try {
                                    dao.modifier(abstrIncident);
                                } catch (Exception ex) {
                                    logger.error(ex);
                                    Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    } 
//                    catch (InterruptedException ex) {
//                        logger.error(ex);
//                        Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
//                    } catch (ExecutionException ex) {
//                         logger.error(ex);
//                        Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
//                    } 
                    catch (AddressException ex) {
                         logger.error(ex);
                        Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (cast.getSchedule()) {
                    executorService.schedule(cast, 30, TimeUnit.SECONDS);
                }
            }
            gererIncident((AbstrTacheSchedule) o);
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
     * Permet à la tache qutotidienne d'envoie des email de se réinscrire auprès
     * du service.
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
     * Retourne la liste des adresses mail des admins en se basant sur la dao
     * UserAccount.
     *
     * @return
     */
    private InternetAddress[] returnMailAdmin() throws AddressException {
     
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


    @Override
    public void instancierTaches() {

        //--------> Lancement de la tâche d'alerte mail
        TacheAlerteMail alerteMail = new TacheAlerteMail(this);
        alerteMail.setSchedule(Boolean.TRUE);
        executorService.schedule(alerteMail, 30, TimeUnit.SECONDS);

        //---------> Lancement de la tâche de vérification journalière
        TacheVerifFluxNotificationMail notificationMail = new TacheVerifFluxNotificationMail(this);
        notificationMail.setSchedule(Boolean.TRUE);
        //calcul du delay
        DateTime dtCurrent = new DateTime();
        DateTime next = dtCurrent.plusDays(1).withHourOfDay(8);// withDayOfWeek(DateTimeConstants.SUNDAY);
        Duration dur = new Duration(dtCurrent, next);
        executorService.schedule(notificationMail, dur.getStandardSeconds(), TimeUnit.SECONDS);
    }

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
                si = (MailIncident) factory.createIncidentFromTask(tache, "blabla");
            } catch (InstantiationException ex) {
                Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
            }



            //=================================================================================================
            // ..................... GESTION DES INCIDENTS
            //=================================================================================================

            //TODO : Reprendre ce qui est dans update pour mieu gérer
            if (tache.getClass().equals(TacheEnvoyerMail.class)) {
                TacheEnvoyerMail cast = (TacheEnvoyerMail)tache;
                logger.error("Le mail ne semble pas être envoyé : " + cast.getExeption());
                si.setMessage(cast.getContent());
                si.setObjet(cast.getSubject());
                si.setLogErreur(cast.getExeption().toString());
                si.setMessage("Le mail n'a pas été envoyé");
//                try {
//                    DAOFactory.getInstance().getDaoFromType(MailIncident.class).modifier(mailIncident);
//                } catch (Exception ex) {
//                    Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }




            //=================================================================================================
            //...............................Enregistrment de l'incident
            //=================================================================================================

            if (si != null) {
                DAOIncident dao = (DAOIncident) DAOFactory.getInstance().getDAOFromTask(tache);
                try {
                    dao.creer(dao);
                } catch (Exception ex) {
                    logger.error("Erreur lors de la création : " + ex);
                    Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }


            //=================================================================================================
            //.........................Terminaison correct des TACHE et FERMETURE DE L'INCIDENT
            //=================================================================================================

        }

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args) throws AddressException {
        TacheEnvoyerMail envoyerMail = new TacheEnvoyerMail(ServiceMailNotifier.getInstance());
        envoyerMail.setContent("youpi");
        envoyerMail.setSubject("obj test");
        envoyerMail.setToMailAdresses(new InternetAddress[]{new InternetAddress("clement.rillon@gmail.com")});
        envoyerMail.setPropertiesMail(ServiceMailNotifier.getInstance().propertiesMail);
        ServiceMailNotifier.getInstance().getExecutorService().submit(envoyerMail);
        
    }
}
