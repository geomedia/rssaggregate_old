/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import rssagregator.services.tache.AbstrTache;

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



                    boolean allaquire = true;
//                    logger.debug("iteration " + this);

                    // Itération sur chaque tache de la queue
                    for (Iterator<AbstrTache> it = service.queueTacheALancer.iterator(); it.hasNext();) {
                        AbstrTache t = it.next();

                        if (t != null) {
                            boolean semPreconditionAquises = t.tryAcquireSem();

                            if (semPreconditionAquises) { // Si la tache a pu acquerir ses pré condition On la lance

                                SoumissionTache soumissionTache = new SoumissionTache();
                                soumissionTache.setTache(t);
                                es.submit(soumissionTache);


                                service.queueTacheALancer.remove(t); // Supression de la tache du pool
                            } else {
                                allaquire = false;
                            }
                        }
                    }


                    if (allaquire) {
//                        synchronized (lock) {
                            lock.wait(30000); // On attend 30 s, Le consomateur peut aussi être notifié pour se réveiller avant la fin du wait
//                        }
                    } else {
//                        synchronized (lock) {
                            lock.wait(2000); // Si on n'avait pas pu aquerir toutes les sem, on n'attend pas si longtemps.
//                        }
                    }

                }


//                while (!service.queueTacheALancer.isEmpty()) {
//
//                    // Nouvelle version
//
//                    AbstrTache t = service.queueTacheALancer.peek();
//
//                    if (t != null) {
//                        boolean semPreconditionAquises = t.tryAcquireSem();
//
//                        if (semPreconditionAquises) { // Si la tache a pu acquerir ses pré condition
//
//                            SoumissionTache soumissionTache = new SoumissionTache();
//                            soumissionTache.setTache(t);
//                            es.submit(soumissionTache);
//
//                            // Supression de la tache du pool
//                            service.queueTacheALancer.remove(t);
//                        }
//
//
//                    }
//
//
////             while (!service.queueTacheALancer.isEmpty()) {
//
////                    //Ancienne version
////                    
////                    // On place la tache dans le pool d'execution, si elle n'est pas annulée
////                                        AbstrTache t = service.queueTacheALancer.take(); // ce n'est pas la bonne
////                    
////                    if (!t.getAnnuler()) {
////                        SoumissionTache soumissionTache = new SoumissionTache();
////                        soumissionTache.tache = t;
////                        es.submit(soumissionTache);
////                    }
//                }
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

        AbstrTache tache;

        @Override
        public void run() {
            if (tache != null) {



                try {
//                   // Soumission de la tache
                    try {
                        tache.setFuture(service.submit(tache));
                    } catch (Exception e) {
                        logger.debug("Exception lors du lancement " + e);
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
                            logger.error("Erreur lors de l'attente du futur de la tache" + e);
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

        public AbstrTache getTache() {
            return tache;
        }

        public void setTache(AbstrTache tache) {
            this.tache = tache;
        }
    }
}