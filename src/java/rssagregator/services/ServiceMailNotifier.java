/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.IOException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import rssagregator.beans.Flux;
import rssagregator.utils.PropertyLoader;

/**
 *
 * @author clem
 */
public class ServiceMailNotifier implements Observer {

    private static ServiceMailNotifier instance = new ServiceMailNotifier();
    private ScheduledExecutorService executorService;
    private Properties propertiesMail;
    private final static String MAILER_VERSION = "Java";

    
    
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
    public void update(Observable o, Object arg) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        // Si c'est un flux. qui disfonctionne.
        if (o instanceof Flux) {
            if (arg != null && arg instanceof String && arg.equals("error")) {
            }
        }
    }

    /**
     * *
     * Permet d'envoyer un mail. Une thread est créer pour cette tache
     */
    public void envoyerMail(InternetAddress[] toMailAdresses, String subject, String content) throws AddressException, MessagingException, IOException {

        String fileConf = PropertyLoader.loadProperti("serv.properties", "mailconf");
        propertiesMail = PropertyLoader.loadFromFile(fileConf);

//        final String username = propertiesMail.getProperty("smtpuser");
//        final String password = propertiesMail.getProperty("smtppass");
//
//        Session session = Session.getInstance(propertiesMail,
//                new javax.mail.Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username, password);
//            }
//        });

        TacheEnvoyerMail tacheEnvoyerMail = new TacheEnvoyerMail();
        tacheEnvoyerMail.setContent(content);
        tacheEnvoyerMail.setPropertiesMail(propertiesMail);
        tacheEnvoyerMail.setSubject(subject);
        tacheEnvoyerMail.setToMailAdresses(toMailAdresses);
  

        this.executorService.submit(tacheEnvoyerMail);

//        Message message = new MimeMessage(session);
//        message.setFrom(new InternetAddress("clement.rillon@gmail.com"));

//        InternetAddress[] internetAddresses = new InternetAddress[1];
//        internetAddresses[0] = new InternetAddress("clement.rillon@gmail.com");
//        message.setRecipients(Message.RecipientType.TO, toMailAdresses);


//        message.setSubject(subject);
//        message.setText(content);
//        message.setContent(content, "text/html; charset=utf-8");

//        message.setHeader("X-Mailer", MAILER_VERSION);
//        message.setSentDate(new Date());

//        message.setSentDate(new Date());
//        session.setDebug(true);
//        Transport.send(message);
    }

    /**
     * Permet à la tache qutotidienne d'envoie des email de se réinscrire auprès
     * du service.
     */
    public void taskEnd(Callable tache) {

        if (tache instanceof TacheVerifFluxNotificationMail) {
            // TODO : Il serait bon de mettre en place un calcul de lheure afin d'aviter que l'heure de la tâche ne se décalle. Il faut soustraire la date au moment de la réception avec une date fixé"e dans la configuration. 
            this.executorService.schedule(tache, 1, TimeUnit.DAYS);
        }
    }
}
