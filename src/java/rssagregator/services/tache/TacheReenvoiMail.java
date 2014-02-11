/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.io.File;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import rssagregator.services.ServiceMailNotifier;

/**
 *
 * Tache chargé de parcourir le répertoire des mail non envoyée. Pour chaque mail on tente de les réenvoyer. Si il sont
 * trop vieux. Il sont déplacé vers le dossier unsend
 *
 * @author clem
 */
public class TacheReenvoiMail extends TacheImpl<TacheReenvoiMail> {

    private final static String path; // La path avec la conf les log les mail exemple /var/lib/RSSAgregade
    private final static String mailpath;// rapertoire /var/.../RSSagragate/mail
    private final static String unsendPath; // Le répertoire contenant les mail non envoyé
    
    /***
     * Nombre de seconde correspondant au sejour maximal des mail dans le répertoire mailpath. Au dela de ce delai, la tache déplace les mail dans unsernpath
     */
    private long moveMailAfter;
    
    
    /***
     * Rempli les variable static path à la première execution de la classe
     */
    static {
        path = System.getProperty("confpath");
        mailpath = path + "mail"; // rapertoire /var/.../RSSagragate/mail
        unsendPath = path + "unsend/";
    }

    @Override
    protected void callCorps() throws InterruptedException, Exception {

//        Properties propertiesMail = ServiceMailNotifier.getInstance().getPropertiesMail();

        // On charge chaque fichier
        File repMail = new File(mailpath);
        File mailFile[] = repMail.listFiles();


        for (int i = 0; i < mailFile.length; i++) {

            File file = mailFile[i];
            // On test la date de création du fichier. 
            DateTime dateMessage = new DateTime(new Date(file.lastModified()));
            Duration dur = new Duration(dateMessage, new DateTime());

            //Si le fichier est trop vieux alors, on le déplace dans unsend
            if (dur.getStandardSeconds()> moveMailAfter) { // Si le ficheir a plus de trois jour
                try {
                    String fUnsend = unsendPath + file.getName();
                    file.renameTo(new File(fUnsend)); // On déplace le fichier vers un répertoire d'archive
                } catch (Exception ex) {
                    logger.error("Erreur lors du déplacement d'un mail vers le répertoire unsend ", ex);
                }
            } else { // Sinon on crée une tache envoie mail 
                TacheEnvoyerMail envoyerMail = (TacheEnvoyerMail) TacheFactory.getInstance().getNewTask(TacheEnvoyerMail.class, false);
                envoyerMail.setFichier(file);
                ServiceMailNotifier.getInstance().getTacheProducteur().produireMaintenant(envoyerMail);
            }
        }
    }

    
    /***
     * @see #moveMailAfter
     * @return 
     */
    public long getMoveMailAfter() {
        return moveMailAfter;
    }

    /***
     * @see #moveMailAfter
     * @param moveMailAfter 
     */
    public void setMoveMailAfter(long moveMailAfter) {
        this.moveMailAfter = moveMailAfter;
    }

    
    
    
    
    
}
