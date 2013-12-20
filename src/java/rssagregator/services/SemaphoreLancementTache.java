/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import rssagregator.utils.ThreadUtils;
//import sun.misc.Lock;

/**
 * Ce singleton permet d'instancier concerver retourner des sémaphores associés a l'ensembles objets pour lequels on
 * veut un blocage. Chaque journal ou flux se voit ainsi associé une semaphore et chaque tache peut attendre son jeton.
 * Pour l'acquisition des semaphore il faut respecter une ordre fixe concernant la pose des vérrou voir programmation
 * corrurente en java briant Goetz, Person ed 2009 pp213. : <ul>
 * <li>journal</li>
 * <li>flux</li>
 * <li>Incident</li>
 * <li>item</li>
 * <li>ItemBrute</li>
 * </ul>
 *
 * // TODO : Il faudrait aussi un méchanisme permettant de retirer les anciennes sémaphores. Ce ne sevrait pas être un
 * problème dans que le projet ne gère pas des millions de sémaphore...
 *
 * @author clem
 */
public class SemaphoreLancementTache implements Runnable {

//    private final Map<String, Semaphore> mapSema = new HashMap();
    Map<Class, Map<String, Semaphore>> mapSemaphore = new HashMap<Class, Map<String, Semaphore>>();
    private static SemaphoreLancementTache instance = new SemaphoreLancementTache();
//    private Lock lockMenage = new ConditionLock();
//     final Lock lock = new ReentrantLock();
    final ReentrantLock reentrantLock = new ReentrantLock();
    final Condition menage = reentrantLock.newCondition();
//    Lock l = new Lock();

    public static SemaphoreLancementTache getinstance() {
        if (instance == null) {
            instance = new SemaphoreLancementTache();

        }
        return instance;
    }

    /**
     * *
     * Retourne la sémaphore pour l'object envoyé en argument
     *
     * @param o
     * @return
     */
    public synchronized Semaphore returnSemaphoreForRessource(Object o) {

//        try {
//            menage.await();
//        if (reentrantLock.isLocked()) {
//            try {
//                System.out.println("ATTENTE");
//                menage.await();
//                
//                System.out.println("C'est reparti");
//
//            } catch (InterruptedException ex) {
//                Logger.getLogger(SemaphoreLancementTache.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        }

//        } catch (InterruptedException ex) {
//            Logger.getLogger(SemaphoreLancementTache.class.getName()).log(Level.SEVERE, null, ex);
//        }

        // On récupère la map 
        Map<String, Semaphore> map = mapSemaphore.get(o.getClass());
        if (map == null) {
            map = new HashMap<String, Semaphore>();
            mapSemaphore.put(o.getClass(), map);
        }

        String cle = o.getClass().getSimpleName() + o.hashCode();

        Semaphore semaphore = map.get(cle);

        if (semaphore == null) {
            System.out.println("Ajout d'une sem : " + cle);

            System.out.println("Nombre de sem " + map.size());
            semaphore = new Semaphore(1);
            synchronized (map) {
                map.put(cle, semaphore);
            }
        }

        return semaphore;
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().setDaemon(true); // Ca fait plaisir à la jmv
            
            while (true) {
                try {
                    synchronized (this) {
                        System.out.println("=====================================");
                        System.out.println("LANCEMENT DE la tache clean Sema");

                        ThreadUtils.interruptCheck();
                        for (Map.Entry<Class, Map<String, Semaphore>> entry : mapSemaphore.entrySet()) {
//                            Class class1 = entry.getKey();
                            Map<String, Semaphore> map = entry.getValue();
                            for (Iterator<Entry<String, Semaphore>> it = map.entrySet().iterator(); it.hasNext();) {

                                Entry<String, Semaphore> entr = it.next();
//                                String string = entr.getKey();
                                Semaphore semaphore = entr.getValue();
                                try {
                                    semaphore.tryAcquire(1, TimeUnit.MILLISECONDS);
                                    if (!semaphore.hasQueuedThreads()) {
                                        System.out.println("------------------> Suppression d'une semaphore");
                                        it.remove();
                                    }
                                } catch (Exception e) {
                                    
                                }
                            }
                        }
                        ThreadUtils.interruptCheck();
//                        Thread.sleep(5000);

                        System.out.println("=====================================");
                    }
//                    menage.signal();
//                    menage.signalAll();

                    Thread.sleep(3600 *1000);
                } finally {
                }
            }

        } catch (InterruptedException e) {

            System.out.println("Femerture du gestionnaire de semaphore");
        }

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
