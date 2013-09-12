/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.IOException;
import java.util.Date;
import java.util.Observer;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.MailIncident;

/**
 * Ce callable permet d'envoyer un mail. La tâche est effectué à l'intérieur
 * d'une thread car on ne peut savoir combien de temps cela va prendre.
 *Incident associé a l'échec de la tache : Mail incident
 * @author clem
 */
public class TacheEnvoyerMail extends AbstrTacheSchedule<TacheEnvoyerMail> implements Incidable{

    public TacheEnvoyerMail(Observer s) {
        super(s);
    }
    private final static String MAILER_VERSION = "Java";
    private Properties propertiesMail;
    InternetAddress[] toMailAdresses;
    String subject;
    String content;
        protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheEnvoyerMail.class);

    @Override
    public TacheEnvoyerMail call() throws AddressException, MessagingException, IOException, Exception {
        try {
            this.exeption = null;
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
            message.setRecipients(Message.RecipientType.TO, toMailAdresses);

            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");

            message.setHeader("X-Mailer", MAILER_VERSION);
            message.setSentDate(new Date());

            message.setSentDate(new Date());
            session.setDebug(true);
            Transport.send(message);
            return this;
        } catch (Exception e) {
            logger.error(e);
            this.exeption = e;
            return this;
        } finally {
            nbrTentative++;
            this.setChanged();
            this.notifyObservers();
        }
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

    @Override
    public void fermerLesIncidentOuvert() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AbstrIncident getIncidenOuvert() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class getTypeIncident() {
        return MailIncident.class;
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



}
