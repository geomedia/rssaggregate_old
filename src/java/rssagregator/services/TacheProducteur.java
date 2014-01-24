/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.services.tache.AbstrTacheSchedule;
import rssagregator.utils.ThreadUtils;

/**
 *
 * @author clem
 */
public class TacheProducteur implements Runnable{

    ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
    AbstrService service;
    
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    public TacheProducteur(AbstrService service) {
        this.service = service;

    }

   

    /**
     * *
     * Produit la tache et la place dans le pool d'execution. Gere les tache schedulees
     *
     * @param t
     */
    public void produire(AbstrTacheSchedule t) {

        Lanceur lanceur = new Lanceur();
        lanceur.setTache(t);

        if (t.getSchedule()) {
            es.schedule(lanceur, t.returnNextDuration(), TimeUnit.SECONDS); // On schedle le lancement de la tache
            service.addTask(t, null);
        } else { // Si ce n'est pas une tache schedulé. On soumet le lanceur directement
            es.submit(lanceur);
        }
    }

    /**
     * *
     * Produit la tache et la place dans le pool d'execution di
     *
     * @param t
     */
    public void produireMaintenant(AbstrTacheSchedule t) {
        try {
            service.queueTacheALancer.put(t);
            synchronized (service.tacheConsomateur.lock) {
                service.tacheConsomateur.lock.notify();
            }
            //        service.
        } catch (InterruptedException ex) {
            Logger.getLogger(TacheProducteur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            while(true){
                
                System.out.println("Tour - " + this);
                Thread.sleep(3600*1000);
                ThreadUtils.interruptCheck();
            }
            
            
        } catch (InterruptedException e) {
            logger.debug("Interruption de  " + this);
        }
        
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "TacheProducteur{" + "service=" + service + '}';
    }
    
    
    

    private class Lanceur implements Runnable {

        AbstrTacheSchedule tache;

        @Override
        public void run() {
            // On vérifie que la tache n'est pas deja dans la queue des taches a lancer
            if (!service.queueTacheALancer.contains(tache)) {

                try {
                    // Acquisition de la semaphore
//                    Semaphore sema = null;


                    // Certaine tache ne doivent pas être executé en même temps. Exemple typyque la collecte de flux de même journaux. Pour ces tache on va donc obtenir une semaphore
//                    if (tache.getClass().equals(TacheRecupCallable.class)) {
//                        TacheRecupCallable cast = (TacheRecupCallable) tache;
//                        if (cast.getFlux() != null && cast.getFlux().getJournalLie() != null) {
//                            Journal j = cast.getFlux().getJournalLie();
//                            sema = SemaphoreLancementTache.getinstance().returnSemaphoreForRessource(j);
//                        }
//                    }
//
//                    if (sema != null) {
//                        sema.acquire();
//                    }

                    // On ajoute une tache a executer a la queue. Elle va être consommé par le consommateur de tache...
                    service.queueTacheALancer.put(tache);



                    // On notifi le consomateur
                    synchronized (service.tacheConsomateur.lock) {
                        service.tacheConsomateur.lock.notify();
                    }



//                    if (sema != null) {
//                        sema.release();
//                    }



                } catch (InterruptedException ex) {
                    System.out.println("Interruption de " + this);
//                    Logger.getLogger(TacheProducteur.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else{
                logger.error("On tente de produire une tache déjà dans la queue du service " + tache);
            }


//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public AbstrTacheSchedule getTache() {
            return tache;
        }

        public void setTache(AbstrTacheSchedule tache) {
            this.tache = tache;
        }
    }
}
