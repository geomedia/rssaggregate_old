/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.Journal;
import rssagregator.services.tache.AbstrTacheSchedule;
import rssagregator.services.tache.TacheRecupCallable;

/**
 *
 * @author clem
 */
public class TacheProducteur {

    ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
    AbstrService service;

    public TacheProducteur(AbstrService service) {
        this.service = service;

    }

    public void produire(AbstrTacheSchedule t) {

        Lanceur lanceur = new Lanceur();
        lanceur.setTache(t);

        if (t.getSchedule()) {
            es.schedule(lanceur, t.returnNextDuration(), TimeUnit.SECONDS); // On schedle le lancement de la tache
        }
        else{ // Si ce n'est pas une tache schedulé. On soumet le lanceur directement
            es.submit(lanceur);
        }
        
        

    }

    private class Lanceur implements Runnable {

        AbstrTacheSchedule tache;

        @Override
        public void run() {
            // On vérifie que la tache n'est pas deja dans la queue des taches a lancer
            if (!service.queueTacheALancer.contains(tache)) {

                try {
                    // Acquisition de la semaphore
                    Semaphore sema = null;


                    // Certaine tache ne doivent pas être executé en même temps. Exemple typyque la collecte de flux de même journaux. Pour ces tache on va donc obtenir une semaphore
                    if (tache.getClass().equals(TacheRecupCallable.class)) {
                        TacheRecupCallable cast = (TacheRecupCallable) tache;
                        if (cast.getFlux() != null && cast.getFlux().getJournalLie() != null) {
                            Journal j = cast.getFlux().getJournalLie();
                            sema = SemaphoreLancementTache.getinstance().returnSemaphoreForRessource(j);
                        }
                    }

                    if (sema != null) {
                        sema.acquire();
                    }

                    // On ajoute une tache a executer a la queue. Elle va être consommé par le consommateur de tache...
                    service.queueTacheALancer.put(tache);
                    


                    // On notifi le consomateur
                    synchronized (service.tacheConsomateur.lock) {
                        service.tacheConsomateur.lock.notify();
                    }



                    if (sema != null) {
                        sema.release();
                    }



                } catch (InterruptedException ex) {
                    System.out.println("Interruption de " + this);
//                    Logger.getLogger(TacheProducteur.class.getName()).log(Level.SEVERE, null, ex);
                }
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
