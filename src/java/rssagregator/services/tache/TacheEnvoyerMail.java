/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.exception.AucunMailAdministateur;
import rssagregator.utils.ExceptionTool;

/**
 * Ce callable permet d'envoyer un mail. La tâche est effectué à l'intérieur d'une thread car on ne peut savoir combien
 * de temps cela va prendre. Incident associé a l'échec de la tache : Mail incident
 *
 * @author clem
 */
public class TacheEnvoyerMail extends TacheImpl<TacheEnvoyerMail> {

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
     * Il est possible d'envoyer un mail depuis un fichier
     */
    File fichier;

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

        logger.debug("Sauvegarde du mail dans un fichier");
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
    protected void callCorps() throws Exception {

        try {
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
            //-------------------- ENVOIE DU MAIL DEPUIS UN FICHIER SI IL EST PR2CISE
            if (fichier != null) {
                InputStream is = new FileInputStream(fichier);
                message = new MimeMessage(session, is);

            } else { //------------ Sinon on crée le message en y entrant les variables 
                message = new MimeMessage(session);

                message.setFrom(new InternetAddress("clement.rillon@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, toMailAdresses);

                message.setSubject(subject);
                message.setContent(content, "text/html; charset=utf-8");

                message.setHeader("X-Mailer", MAILER_VERSION);
                message.setSentDate(new Date());

                message.setSentDate(new Date());
            }

            Transport.send(message);

            // Si c'est un mail provenant d'un fichier, on supprime ce fichier car le mail est bien parti.
            if (fichier != null) {
                fichier.delete();
            }


        } //---------------------------------------------------
        //              Capture des Exception
        //---------------------------------------------------
        /**
         * *
         * Ce block catch permet de gérer les exeption en enregistrant le mail dans un fichier si il n'a pas pu partir.
         */
        catch (Exception e) {
            
            //---------> Enregistrement du mail dans un fichier
            if (e.getClass().equals(AucunMailAdministateur.class)) { // On exclu les mail si pas de destinataire
                logger.error("Aucun Mail d'admin n'est configuré !");
            } else {
                if (message != null) { // Si c'est un message nouveau ne possédant pas son fichier
                    if (fichier == null) { // Si ce n'est pas un mail provenant déjà d'un fichier (si c'est un nouveau message et pas un resend )
                        saveMessageToFile(message); // Enregistret le message sur le disque
                    }
                }
            }
            //------> Deplacement des mail innenvoyable
            /**
             * *
             * Si on n'a pas pu envoyer un mail pendant 3 jour, celui ci est déplacé dans le répertoire unsend. Il ne
             * sera plus réenvoyé
             */
//            if (fichier != null) { // La procédure ne s'applique qu'au mail ayant pour source un fichier (les resend) 
//
//                DateTime dateMessage = new DateTime(new Date(fichier.lastModified()));
//                Duration dur = new Duration(dateMessage, new DateTime());
//                if (dur.getStandardDays() > 3) { // Si le ficheir a plus de trois jour
//                    try {
//                        String path = System.getProperty("confpath");
//                        path += "unsend/";
//                        path += fichier.getName();
//                        fichier.renameTo(new File(path)); // On déplace le fichier vers un répertoire d'archive
//                 
//
//                    } catch (Exception ex) {
//                        logger.error("Erreur lors du déplacement d'un mail vers le répertoire unsend ", ex);
//                    }
//                }
//            }
        }
    }

    public File getFichier() {
        return fichier;
    }

    public void setFichier(File fichier) {
        this.fichier = fichier;
    }
    
    

    
}
