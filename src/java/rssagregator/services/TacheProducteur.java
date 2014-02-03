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
import rssagregator.services.tache.AbstrTache;
import rssagregator.utils.ThreadUtils;

/**
 *
 * @author clem
 */
public class TacheProducteur implements Runnable {

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
    public void produire(AbstrTache t) {

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
    public void produireMaintenant(AbstrTache t) {
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
            while (true) {

                System.out.println("Tour - " + this);
                Thread.sleep(3600 * 1000);
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

    /**
     * *
     * Thread chargé simplement de placer la tache dans la queue des taches à lancer du service. 
     */
    private class Lanceur implements Runnable {

        AbstrTache tache;

        @Override
        public void run() {
            // On vérifie que la tache n'est pas deja dans la queue des taches a lancer
            if (!service.queueTacheALancer.contains(tache)) {

                try {

                    // On ajoute une tache a executer a la queue. Elle va être consommé par le consommateur de tache...
                    service.queueTacheALancer.put(tache);

                    // On notifi le consomateur Pour qu'il boucle et consomme la tache au plus vide
                    synchronized (service.tacheConsomateur.lock) {
                        service.tacheConsomateur.lock.notify();
                    }


                } catch (InterruptedException ex) {
                    logger.debug("Interruption de " + this);
                }
            } else {
                logger.error("On tente de produire une tache déjà dans la queue du service " + tache);
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
