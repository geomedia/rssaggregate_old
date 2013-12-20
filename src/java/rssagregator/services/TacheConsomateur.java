/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.concurrent.Future;
import rssagregator.services.tache.AbstrTacheSchedule;

/**
 * Le consomateur est chargé de vider les tache de la queueTacheALancer du service en les lancant dans le pool d'execution
 * @author clem
 */
public class TacheConsomateur implements Runnable {

    AbstrService service;
    protected final Object lock = new Object();

    public TacheConsomateur(AbstrService service) {
        this.service = service;
    }

    @Override
    public void run() {

        try {
            while (true) {

                synchronized (lock) {
                    lock.wait();
                }

                // Récupération des tache a consommer
                while (!service.queueTacheALancer.isEmpty()) {

                    AbstrTacheSchedule t = service.queueTacheALancer.take();
                    // On place la tache dans le pool d'execution
                    if (!t.getAnnuler()) {
                      Future fut = service.submit(t);
                      service.addTask(t, fut);
                    }
                }
            }



        } catch (InterruptedException e) {
            System.out.println("Interruption du consommateur de tache du service " + service);
        }

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
