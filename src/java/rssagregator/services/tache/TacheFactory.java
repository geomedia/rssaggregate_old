/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Level;
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

    protected TacheFactory() {
    }

    public static TacheFactory getInstance() {

        if (instance == null) {
            logger2.debug("Chargement de la TacheFactory");
            ObjectMapper mapper = new ObjectMapper();
            TacheFactory fact;
            File f;
            f = new File(((String) PropertyLoader.returnConfPath() + "tacheFactory.json"));

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
    /**
     * *
     * Définition des paramettres des tâches
     */
    //----maxExecuteTime---
    public Short TacheRecupCallable_maxExecuteTime = 30;
    public Byte TacheRecupCallable_typeSchedule = 1;
    //--
    public Short TacheAlerteMail_maxExecuteTime = 30;
    public Byte TacheAlerteMail_typeSchedule = 1;
    //--
    public Short TacheEnvoyerMail_maxExecuteTime = 20;
    //--
    public Short TacheStillAlive_maxExecuteTime = 10;
    public Byte TacheStillAlive_typeSchedule = 1;
    //--
    public Short TacheVerifFluxNotificationMail_maxExecuteTime = 10;
    public Byte TacheVerifFluxNotificationMail_typeSchedule = 2;
    //--
    public Short TacheLancerConnectionJMS_maxExecuteTime = 10;
    public Byte TacheLancerConnectionJMS_typeSchedule = 1;
    //--
    public Short TacheSynchroHebdomadaire_maxExecuteTime = 10;
    public Byte TacheSynchroHebdomadaire_typeSchedule = 2;
    //---
    public Short TacheDetectDeadLock_maxExecuteTime = 10;
    public Byte TacheDetectDeadLock_typeSchedule = 1;
    //---
    public Short TacheCalculQualiteFluxLancementTous_maxExecuteTime = 600;
    public Byte TacheCalculQualiteFluxLancementTous_typeSchedule = 2;

    public AbstrTacheSchedule getNewTask(Class c, Boolean scheduled) {

        AbstrTacheSchedule newTache = null;
        Short maxExecuteTime = null;
        AbstrService service = null;
        Byte typeSchedule = null;


        if (c.equals(TacheRecupCallable.class)) {
            newTache = new TacheRecupCallable();
            maxExecuteTime = TacheRecupCallable_maxExecuteTime;
            typeSchedule = TacheRecupCallable_typeSchedule;
            service = ServiceCollecteur.getInstance();


        } else if (c.equals(TacheAlerteMail.class)) {
            newTache = new TacheAlerteMail();
            maxExecuteTime = TacheAlerteMail_maxExecuteTime;
            typeSchedule = TacheAlerteMail_typeSchedule;

            service = ServiceMailNotifier.getInstance();
//            scheduled = false ; // Un envoie de mail n'est jamais schedule
        } else if (c.equals(TacheEnvoyerMail.class)) {
            newTache = new TacheEnvoyerMail();
            maxExecuteTime = TacheEnvoyerMail_maxExecuteTime;
            service = ServiceMailNotifier.getInstance();
        } else if (c.equals(TacheStillAlive.class)) {
            newTache = new TacheStillAlive();
            maxExecuteTime = TacheStillAlive_maxExecuteTime;
            typeSchedule = TacheStillAlive_typeSchedule;
            service = ServiceServer.getInstance();
        } else if (c.equals(TacheVerifFluxNotificationMail.class)) {
            newTache = new TacheVerifFluxNotificationMail();
            maxExecuteTime = TacheVerifFluxNotificationMail_maxExecuteTime;
            typeSchedule = TacheVerifFluxNotificationMail_typeSchedule;
            service = ServiceMailNotifier.getInstance();
        } else if (c.equals(TacheLancerConnectionJMS.class)) {
            newTache = new TacheLancerConnectionJMS();
            maxExecuteTime = TacheLancerConnectionJMS_maxExecuteTime;
            typeSchedule = TacheLancerConnectionJMS_typeSchedule;
            service = ServiceSynchro.getInstance();
        } else if (c.equals(TacheSynchroHebdomadaire.class)) {
            newTache = new TacheSynchroHebdomadaire();
            service = ServiceSynchro.getInstance();
            typeSchedule = TacheSynchroHebdomadaire_typeSchedule;
            maxExecuteTime = TacheSynchroHebdomadaire_maxExecuteTime;
        } else if (c.equals(TacheDetectDeadLock.class)) {
            newTache = new TacheDetectDeadLock();
            service = ServiceServer.getInstance();
            maxExecuteTime = TacheDetectDeadLock_maxExecuteTime;
            typeSchedule = TacheDetectDeadLock_typeSchedule;
            
        } else if (c.equals(TacheCalculQualiteFlux.class)) {
            newTache = new TacheCalculQualiteFlux();
            service = ServiceCollecteur.getInstance();
            maxExecuteTime = 60;
            typeSchedule = TacheCalculQualiteFluxLancementTous_typeSchedule;
        } else if (c.equals(TacheCalculQualiteFluxLancementTous.class)) {
            newTache = new TacheCalculQualiteFluxLancementTous();
            service = ServiceCollecteur.getInstance();
            maxExecuteTime = TacheCalculQualiteFluxLancementTous_maxExecuteTime;
            typeSchedule = TacheCalculQualiteFluxLancementTous_typeSchedule;
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
            newTache.setTypeSchedule(typeSchedule);

            // Configuration de level de log de la tache
//            newTache.getLogger().setLevel(Level.toLevel(newTache.getLogErrorLevel()));
            return newTache;
        }
    }

    public static void main(String[] args) {
//        try {


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
