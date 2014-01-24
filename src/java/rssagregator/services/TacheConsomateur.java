/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.print.attribute.standard.Severity;
import rssagregator.services.tache.AbstrTacheSchedule;

/**
 * Le consomateur est chargé de vider les tache de la queueTacheALancer du service en les lancant dans le pool
 * d'execution.
 *
 * @author clem
 */
public class TacheConsomateur implements Runnable {

    AbstrService service;
    protected final Object lock = new Object();
    ExecutorService es = Executors.newCachedThreadPool();
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
    boolean run = true;

    public TacheConsomateur(AbstrService service) {
        this.service = service;
    }

    @Override
    public void run() {
        try {
            while (run) {
                synchronized (lock) {
                    lock.wait(30000); // On attend 30 s, Le consomateur peut aussi être notifié pour se réveiller avant la fin du wait
                }
                // Récupération des tache a consommer
                while (!service.queueTacheALancer.isEmpty()) {

                   
                    AbstrTacheSchedule t = service.queueTacheALancer.take(); // ce n'est pas la bonne
                    
                    
                    // Nouvelle version
                    
                   

                    //Ancienne version
                    
                    // On place la tache dans le pool d'execution, si elle n'est pas annulée
                    if (!t.getAnnuler()) {
                        SoumissionTache soumissionTache = new SoumissionTache();
                        soumissionTache.tache = t;
                        es.submit(soumissionTache);
                    }
                }
            }

        } catch (InterruptedException e) {
            logger.debug("Interruption de " + this);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
        try {
            if (es != null) {
                es.shutdownNow();
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la fermeture de l'executor service du cosommateur de tache", e);
        }

    }

    public void close() {

        try {
            if (es != null) {
                es.shutdownNow();
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la fermeture de l'executor service du cosommateur de tache", e);
        }
    }

    /**
     * *
     * Thread utilisée pour le lancement d'une tache. La cette thread va commencer par monopoliser les sémaphore
     * adhéquat puis va placer la tache dans le pool du service.
     */
    private class SoumissionTache implements Runnable {

        AbstrTacheSchedule tache;

        @Override
        public void run() {
            if (tache != null) {
                
                
                try {
//
                    // On acquier chacune des semaphore de la tache avant de la placer dans le pool d'execution du service.
                    tache.acquireSem();

//                   // Soumission de la tache
                    try {
                             tache.setFuture(service.submit(tache));                        
                    } catch (Exception e) {
                        service.queueTacheALancer.put(tache);
                    }
                    
                    Future fut = tache.getFuture();

                    // Si il y a un temps maximal d'execution pour la tache, alors on limit. En cas de dépassement la tâche doit être tuées
                    if (tache.getMaxExecuteTime() != null) {
                        try {
                            fut.get(tache.getMaxExecuteTime(), TimeUnit.SECONDS);
                        } catch (Exception e) {
                            logger.error("La tache suivant a dépassé son temps d'execution de " + tache.getMaxExecuteTime() + tache);
                            fut.cancel(true);
                        }
                    } else {
                        try {
                            fut.get(10, TimeUnit.MINUTES); // Si une tache n'est pas résolue en 10 minutes, on la détruit
                        } catch (Exception e) {
                            logger.error("Erreur lors de l'attente du futur de la tache"+e);
                            fut.cancel(true);
                        }
                    }

                    // Libération des sémaphores 
                   tache.releaseSem();
                    

                } catch (InterruptedException e) {
                    logger.debug("Interruption de " + this);
                } catch (Exception ex) {
                    logger.error("Erreur lors de la cosommation de la tache " + tache, ex);
                }
            }
        }

        public AbstrTacheSchedule getTache() {
            return tache;
        }

        public void setTache(AbstrTacheSchedule tache) {
            this.tache = tache;
        }
    }
}