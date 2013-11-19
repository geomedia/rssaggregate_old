/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Date;
import java.util.List;
import java.util.Observer;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import rssagregator.beans.exception.AucunMailAdministateur;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.incident.MailIncident;
import rssagregator.beans.incident.ServerIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.services.crud.AbstrServiceCRUD;
import rssagregator.services.crud.ServiceCRUDFactory;

/**
 * Ce callable permet d'envoyer un mail. La tâche est effectué à l'intérieur d'une thread car on ne peut savoir combien
 * de temps cela va prendre. Incident associé a l'échec de la tache : Mail incident
 *
 * @author clem
 */
public class TacheEnvoyerMail extends TacheImpl<TacheEnvoyerMail> implements Incidable {

    public TacheEnvoyerMail(Observer s) {
        super(s);
    }
    private final static String MAILER_VERSION = "Java";
    private Properties propertiesMail;
    InternetAddress[] toMailAdresses;
    String subject;
    String content;
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheEnvoyerMail.class);

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
    public Class getTypeIncident() {
        return MailIncident.class;
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    /**
     * *
     * Génère un mail incident et l'enregistre dans la base de donnée
     */
    public void gererIncident() throws Exception {
        // Si l'erreur vient du fait qu'il n'y a pas de mail d'admin dans la conf
        try {
            if (this.exeption != null) {
                em = DAOFactory.getInstance().getEntityManager();
                em.getTransaction().begin();
                AbstrServiceCRUD serviceCRUD = ServiceCRUDFactory.getInstance().getServiceFor(MailIncident.class);


                if (this.exeption.getClass().equals(AucunMailAdministateur.class)) { // C'est l'erreur est du au fait qu'il n'y a pas d'email dans la conf
                    DAOIncident<ServerIncident> dao = (DAOIncident<ServerIncident>) DAOFactory.getInstance().getDaoFromType(ServerIncident.class);

                    dao.setClos(false);
                    ServerIncident incid = null;

                    List<ServerIncident> list = dao.findIncidentNonClos(ServerIncident.class);
                    if (!list.isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            ServerIncident serverIncident = list.get(i);
                            if (serverIncident.getMessageEreur().equals("pas de mail admin dans la conf")) {
                                incid = serverIncident;
                                incid.setNombreTentativeEnEchec(incid.getNombreTentativeEnEchec() + 1);
                            }
                        }
                    }
                    if (incid == null) {
                        IncidentFactory<ServerIncident> facto = new IncidentFactory<ServerIncident>();
                        incid = facto.getIncident(ServerIncident.class, "pas de mail admin dans la conf", this.exeption);
                        serviceCRUD.ajouter(incid);
                    } else {
                        serviceCRUD.modifier(incid);
                    }


                } else { // Pour tout autre erreur
                    IncidentFactory<MailIncident> factory = new IncidentFactory<MailIncident>();
                    MailIncident incident = factory.createIncidentFromTask(this, "Le mail n'a pu être envoyé");
                    incident.setMessage(this.getContent());
                    incident.setObjet(this.getSubject());
                    incident.setLogErreur(this.getExeption().toString());
                    serviceCRUD.ajouter(incident, em);
                }
            }
        } catch (Exception e) {
            logger.error("Erreur lors du commit de l'incident", e);
            if (em != null && em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
            
        } finally {
            if (em != null && em.isJoinedToTransaction()) {
                try {
                    em.getTransaction().commit();
                } catch (Exception e) {
                    logger.error("Erreur lors du commit de l'incident", e);
                }
                try {
                    em.close();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void fermetureIncident() throws Exception {

        throw new UnsupportedOperationException("Not supported yet. La tache n'a pas a fermer automatiquement ses incident."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void callCorps() throws Exception {

        if (toMailAdresses == null || (toMailAdresses != null && toMailAdresses.length < 1)) {
            throw new AucunMailAdministateur("Aucun mail d'administrateur dans la conf");
        }

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

    }
}
