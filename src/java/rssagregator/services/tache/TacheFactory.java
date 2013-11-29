/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import rssagregator.beans.exception.RessourceIntrouvable;
import rssagregator.services.AbstrService;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.ServiceMailNotifier;
import rssagregator.services.ServiceServer;
import rssagregator.services.ServiceSynchro;
import rssagregator.utils.PropertyLoader;

/**
 *
 * @author clem
 */
public class TacheFactory {

    private static TacheFactory instance;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheFactory.class);
    private static org.apache.log4j.Logger logger2 = org.apache.log4j.Logger.getLogger(TacheFactory.class);

    private TacheFactory() {
    }

    public static TacheFactory getInstance() {

        if (instance == null) {
            logger2.debug("Chargement de la TacheFactory");
            ObjectMapper mapper = new ObjectMapper();
            TacheFactory fact;
            File f;
            f = new File(((String) PropertyLoader.loadFromContext("conf/varpath"))+"tacheFactory.json");

            try {
                fact = mapper.readValue(f, TacheFactory.class);
                System.out.println("OK");
                instance = fact;
            } catch (IOException ex) {
                logger2.error("Impossible de charger le fichier " + f, ex);
            }
            
            if (instance == null) { // Si pas possible de trouver le fichier on charge une factory par défault
                logger2.error("Il n'a pas été possible de charger la factory depuis les fichiers de conf, création d'une factory avec les paramettres par défault");
                instance = new TacheFactory();
                
                // On tente d'enregistrer la nouvelle factory
                    try {
                        mapper.writeValue(f, instance);
                    } catch (IOException ex) {
                        logger2.error("erreur lors de l'enregistremnet de la nouvelle TacheFactory");
                    } 
                
            }
        }
        return instance;
    }
    
    /***
     * Définition des paramettres des tâches
     */
    //----maxExecuteTime---
    public Short TacheRecupCallable_maxExecuteTime = 30;
    public Short TacheAlerteMail_maxExecuteTime = 30;
    public Short TacheEnvoyerMail_maxExecuteTime = 20;
    public Short TacheStillAlive_maxExecuteTime = 10;
    public Short TacheVerifFluxNotificationMail_maxExecuteTime = 10;
    public Short TacheLancerConnectionJMS_maxExecuteTime = 10;
    public Short TacheSynchroHebdomadaire_maxExecuteTime = 10;
    public Short TacheDetectDeadLock_maxExecuteTime = 10;

    
    public AbstrTacheSchedule getNewTask(Class c, Boolean scheduled) {

        AbstrTacheSchedule newTache = null;
        Short maxExecuteTime = null;
        AbstrService service = null;


        if (c.equals(TacheRecupCallable.class)) {
            newTache = new TacheRecupCallable();
            maxExecuteTime = TacheRecupCallable_maxExecuteTime;
            service = ServiceCollecteur.getInstance();
        } else if (c.equals(TacheAlerteMail.class)) {
            newTache = new TacheAlerteMail();
            maxExecuteTime = TacheAlerteMail_maxExecuteTime;
            service = ServiceMailNotifier.getInstance();
//            scheduled = false ; // Un envoie de mail n'est jamais schedule
        } else if (c.equals(TacheEnvoyerMail.class)) {
            newTache = new TacheEnvoyerMail();
            maxExecuteTime = TacheEnvoyerMail_maxExecuteTime;
            service = ServiceMailNotifier.getInstance();
        } else if (c.equals(TacheStillAlive.class)) {
            newTache = new TacheStillAlive();
            maxExecuteTime = TacheStillAlive_maxExecuteTime;
            service = ServiceServer.getInstance();
        } else if (c.equals(TacheVerifFluxNotificationMail.class)) {
            newTache = new TacheVerifFluxNotificationMail();
            maxExecuteTime = TacheVerifFluxNotificationMail_maxExecuteTime;
            service = ServiceMailNotifier.getInstance();
        } else if (c.equals(TacheLancerConnectionJMS.class)) {
            newTache = new TacheLancerConnectionJMS();
            maxExecuteTime = TacheLancerConnectionJMS_maxExecuteTime;
            service = ServiceSynchro.getInstance();
        } else if (c.equals(TacheSynchroHebdomadaire.class)) {
            newTache = new TacheSynchroHebdomadaire();
            service = ServiceSynchro.getInstance();
            maxExecuteTime = TacheSynchroHebdomadaire_maxExecuteTime;
        } else if (c.equals(TacheDetectDeadLock.class)) {
            newTache = new TacheDetectDeadLock();
            service = ServiceServer.getInstance();
            maxExecuteTime = TacheDetectDeadLock_maxExecuteTime;
        }



        if (newTache == null) {
            throw new UnsupportedOperationException("La tache n'est pas configuré dans la factory : " + c.getName());
        } else {
            newTache.setMaxExecuteTime(maxExecuteTime);
            newTache.addObserver(service);
            newTache.setService(service);
            newTache.setExeption(null);
            newTache.setNbrTentative(0);
            newTache.setSchedule(scheduled);
            newTache.setAnnuler(false);

            return newTache;
        }
    }

    public static void main(String[] args) {
//        try {
        System.out.println("AAA");


        TacheFactory factory = TacheFactory.getInstance();
        System.out.println("facto : " + factory);
        System.out.println("tache TacheEnvoyerMail" + factory.TacheEnvoyerMail_maxExecuteTime);
        System.out.println("tache Recup" + factory.TacheRecupCallable_maxExecuteTime);

        //        ObjectMapper mapper = new ObjectMapper();
        //        File f = new File("/var/lib/RSSAgregate/tacheFactory.json");
        //        try {
        //            mapper.writeValue(f, factory);
        //        } catch (IOException ex) {
        //        } 
        //        }
//        } catch (NamingException ex) {
//            Logger.getLogger(TacheFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (RessourceIntrouvable ex) {
//            Logger.getLogger(TacheFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
