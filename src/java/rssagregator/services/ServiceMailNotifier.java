/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import rssagregator.services.tache.TacheFactory;
import rssagregator.services.tache.TacheEnvoyerMail;
import rssagregator.services.tache.AbstrTacheSchedule;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import rssagregator.beans.UserAccount;
import rssagregator.beans.exception.UnIncidableException;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.incident.MailIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.utils.PropertyLoader;

/**
 * Le service mail gère la vie des Tache : <ul>
 * <li>TacheEnvoyerMail : Envoie d'un mail </li>
 * <li></li>
 * </ul>
 *
 * @author clem
 */
public class ServiceMailNotifier extends ServiceImpl {

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
            
            String varPath = (String) PropertyLoader.returnConfPath()+"mailconf.properties";
            propertiesMail = PropertyLoader.loadFromFile(varPath);
        } catch (Exception ex) {
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

    /**
     * *
     * Retourne la liste des adresses mail des admins en se basant sur la dao UserAccount.
     *
     * @return
     */
    public InternetAddress[] returnMailAdmin() throws AddressException {

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


        if (tache.getExeption() != null && Incidable.class.isAssignableFrom(tache.getClass())) {
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
        TacheEnvoyerMail envoyerMail = (TacheEnvoyerMail) TacheFactory.getInstance().getNewTask(TacheEnvoyerMail.class, Boolean.FALSE);
//        TacheEnvoyerMail envoyerMail = new TacheEnvoyerMail(ServiceMailNotifier.getInstance());
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
