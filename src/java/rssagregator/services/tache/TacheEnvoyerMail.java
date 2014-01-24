/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.exception.AucunMailAdministateur;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.incident.MailIncident;
import rssagregator.beans.incident.ServerIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.services.ServiceMailNotifier;
import rssagregator.services.crud.AbstrServiceCRUD;
import rssagregator.services.crud.ServiceCRUDFactory;
import rssagregator.utils.ExceptionTool;

/**
 * Ce callable permet d'envoyer un mail. La tâche est effectué à l'intérieur d'une thread car on ne peut savoir combien
 * de temps cela va prendre. Incident associé a l'échec de la tache : Mail incident
 *
 * @author clem
 */
public class TacheEnvoyerMail extends TacheImpl<TacheEnvoyerMail> implements Incidable {

    private final static String MAILER_VERSION = "Java";
    private Properties propertiesMail;
    InternetAddress[] toMailAdresses;
    String subject;
    String content;
    Message message = null;
//    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheEnvoyerMail.class);
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

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

    /**
     * *
     * Génère un mail incident et l'enregistre dans la base de donnée
     */
    @Override
    public void gererIncident() throws Exception {
        // Si l'erreur vient du fait qu'il n'y a pas de mail d'admin dans la conf
//        try {
        if (this.exeption != null) {

//                initialiserTransaction();
//                AbstrServiceCRUD serviceCRUD = ServiceCRUDFactory.getInstance().getServiceFor(MailIncident.class);


            if (this.exeption.getClass().equals(AucunMailAdministateur.class)) { // C'est l'erreur est du au fait qu'il n'y a pas d'email dans la conf

                logger.error("Il semble qu'aucun mail d'administrateur n'a été définit. ");
//                    DAOIncident<ServerIncident> dao = (DAOIncident<ServerIncident>) DAOFactory.getInstance().getDaoFromType(ServerIncident.class);
//
//                    dao.setClos(false);
//                    ServerIncident incid = null;
//
//                    List<ServerIncident> list = dao.findIncidentNonClos(ServerIncident.class);
//                    if (!list.isEmpty()) {
//                        for (int i = 0; i < list.size(); i++) {
//                            ServerIncident serverIncident = list.get(i);
//                            if (serverIncident.getMessageEreur().equals("pas de mail admin dans la conf")) {
//                                incid = serverIncident;
//                                incid.setNombreTentativeEnEchec(incid.getNombreTentativeEnEchec() + 1);
//                            }
//                        }
//                    }
//                    if (incid == null) {
//                        IncidentFactory<ServerIncident> facto = new IncidentFactory<ServerIncident>();
//                        incid = facto.getIncident(ServerIncident.class, "pas de mail admin dans la conf", this.exeption);
//                        serviceCRUD.ajouter(incid);
//                    } else {
//                        serviceCRUD.modifier(incid);
//                    }


            } else { // Pour tout autre erreur
//                    IncidentFactory<MailIncident> factory = new IncidentFactory<MailIncident>();
//                    MailIncident incident = factory.createIncidentFromTask(this, "Le mail n'a pu être envoyé");
//                    incident.setMessage(this.getContent());
//                    incident.setObjet(this.getSubject());
//                    incident.setLogErreur(this.getExeption().toString());
//                    serviceCRUD.ajouter(incident, em);


                logger.error("Impossible d'envoyer un mail d'alerte");

                String varpath = "/home/clem/testmail";
//                varpath += "/mail/";
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(varpath);
                } catch (FileNotFoundException ex) {
                    logger.error("Erreur lors de la création de outputstream", ex);
                }

                if (message != null) {

                    message.writeTo(fileOutputStream);
                } else {
                    logger.error("Message null ");
                }
            }
        }
//        } catch (Exception e) {

//            commitTransaction(false);           
//        } 

//        finally {
//            commitTransaction(true);
//        }
    }

    /**
     * *
     * Enregistre un message dans un fichier. Si le mail n'est pas correct, aucun fichier ne doit être enregistré.
     *
     * @param message
     * @return true si un fichier a bien été crée false si aucun crée
     * @throws NullPointerException si message ou file is null
     * @throws FileNotFoundException ; Si le fichier envoyé en argumetn n'a pas été trouvé
     */
    public boolean saveMessageToFile(Message message) throws FileNotFoundException, IOException, MessagingException {

        ExceptionTool.argumentNonNull(message);

        String varpath = System.getProperty("confpath");
        varpath += "mail/";

        File f = new File(varpath + contructMailFileName());
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = null;
            fileOutputStream = new FileOutputStream(f);
            message.writeTo(fileOutputStream);
            return true;

        } catch (Exception e) {
            logger.error("Erreur lors de l'enregistrement du mail dans un fichier Mail incorrect ?", e);
            try {
                f.delete();
                return false;
            } catch (Exception exDelete) {
                logger.debug("Erreur lors de la suppression du fichier ", exDelete);
                return false;
            }

        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    logger.debug("Erreur lors de la fermeture du file output stream", e);
                }
            }
        }
    }

    /**
     * *
     * Crée une chaine aléatoire avec datage. utile pour crée le nom de fichier des email a sauvegarder dans des
     * fichiers.
     *
     * @param msg
     * @return
     */
           //TODO : on peut factoriser comme une méthode static
    public String contructMailFileName() {

 
        // On construit le datega
        DateTime dt = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd--kk-mm-s-S");
        String alea = UUID.randomUUID().toString();

        String retour = fmt.print(dt) + "__" + alea;
        return retour;
    }

    @Override
    public void fermetureIncident() throws Exception {
//        throw new UnsupportedOperationException("Not supported yet. La tache n'a pas a fermer automatiquement ses incident."); //To change body of generated methods, choose Tools | Templates.
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

        message = new MimeMessage(session);
        message.setFrom(new InternetAddress("clement.rillon@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, toMailAdresses);

        message.setSubject(subject);
        message.setContent(content, "text/html; charset=utf-8");

        message.setHeader("X-Mailer", MAILER_VERSION);
        message.setSentDate(new Date());

        message.setSentDate(new Date());


//        session.setDebug(true);
        Transport.send(message);
    }
}
