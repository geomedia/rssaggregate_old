/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.File;
import java.util.Properties;
import rssagregator.services.tache.TacheEnvoyerMail;
import rssagregator.services.tache.TacheFactory;
import rssagregator.utils.ThreadUtils;

/**
 * Une tache parcourant le répertoire des mail afin de réenvoyer les potentiel mail qui ne sont pas part
 *
 * @author clem
 */
@Deprecated
public class ThreadReenvoyerMail implements Runnable {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ThreadReenvoyerMail.class);
    String path;

    @Override
    public void run() {

        // On construit la path 
        path = System.getProperty("confpath");
        path += "mail/";

        Properties propertiesMail = ServiceMailNotifier.getInstance().getPropertiesMail();


        try {
            while (true) {


                // On charge chaque fichier
                File upDir = new File(path);
                File sousDir[] = upDir.listFiles();


                for (int i = 0; i < sousDir.length; i++) {
                 
                    File file = sousDir[i];
                    
                    // On crée une tache d'envoie de mail
                    TacheEnvoyerMail envoyerMail = (TacheEnvoyerMail) TacheFactory.getInstance().getNewTask(TacheEnvoyerMail.class, false);
                    envoyerMail.setFichier(file);
                    
                    ServiceMailNotifier.getInstance().getTacheProducteur().produireMaintenant(envoyerMail);
                }

                ThreadUtils.interruptCheck();
                
                Thread.sleep(60 * 1000); 
                
            }
        } catch (InterruptedException e) {
            logger.debug("Interruption");


        }

    }
}
