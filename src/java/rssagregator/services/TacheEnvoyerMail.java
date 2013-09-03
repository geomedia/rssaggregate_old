/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Callable;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import rssagregator.utils.PropertyLoader;

/**
 * Ce callable permet d'envoyer un mail. La tâche est effectué à l'intérieur
 * d'une thread car on ne peut savoir combien de temps cela va prendre.
 *
 * @author clem
 */
public class TacheEnvoyerMail implements Callable {

    private final static String MAILER_VERSION = "Java";
    private Properties propertiesMail;
    InternetAddress[] toMailAdresses;
    String subject;
    String content;

    @Override
    public Void call() throws AddressException, MessagingException, IOException, Exception {
        System.out.println("DEBUT TACHE SEND");
//          String fileConf = PropertyLoader.loadProperti("serv.properties", "mailconf");
//        propertiesMail = PropertyLoader.loadFromFile(fileConf);

        final String username = propertiesMail.getProperty("smtpuser");
        final String password = propertiesMail.getProperty("smtppass");

        Session session = Session.getInstance(propertiesMail,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("clement.rillon@gmail.com"));

//        InternetAddress[] internetAddresses = new InternetAddress[1];
//        internetAddresses[0] = new InternetAddress("clement.rillon@gmail.com");
        message.setRecipients(Message.RecipientType.TO, toMailAdresses);


        message.setSubject(subject);
//        message.setText(content);
        message.setContent(content, "text/html; charset=utf-8");

        message.setHeader("X-Mailer", MAILER_VERSION);
        message.setSentDate(new Date());

        message.setSentDate(new Date());
        session.setDebug(true);
        Transport.send(message);
        
        System.out.println("FIN TACHE SEND");
        return null;
    }

    /**
     * *
     * Permet d'envoyer un mail en utilisant une thread?.
     */
    public Properties getPropertiesMail() {
        return propertiesMail;
    }

    public void setPropertiesMail(Properties propertiesMail) {
        this.propertiesMail = propertiesMail;
    }

    public InternetAddress[] getToMailAdresses() {
        return toMailAdresses;
    }

    public void setToMailAdresses(InternetAddress[] toMailAdresses) {
        this.toMailAdresses = toMailAdresses;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    

}
