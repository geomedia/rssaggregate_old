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
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.Bean;
import rssagregator.utils.ExceptionTool;
import rssagregator.utils.ThreadUtils;
//import sun.misc.Lock;

/**
 * Ce singleton permet d'instancier, concerver et retourner des sémaphores associés a l'ensembles objets pour lequels on
 * veut un blocage. Chaque journal ou flux se voit ainsi associé une semaphore et chaque tache peut attendre son jeton.
 * Pour l'acquisition des semaphore il faut respecter une ordre fixe concernant la pose des vérrou voir programmation
 * corrurente en java briant Goetz, Person ed 2009 pp213. : <ul>
 * <li>journal</li>
 * <li>flux</li>
 * <li>Incident</li>
 * <li>item</li>
 * <li>ItemBrute</li>
 * </ul>
 * <p>Les objet envoyé au SemaCentre doivent être des beans possédant un ID. </p>
 * <p>Une thread est associé au SemaCentre afin de périodiquement vider les semaphores innutiles</p>
 *
 *
 * @author Klemm
 */
public class SemaphoreCentre implements Runnable {

    /**
     * *
     * Map contenant les semaphore. Elle sont classé par class. Les sous map permettant d'identier les objet par la key
     * String qui contient le type de l'objet ainsi que l'id du beans concerné.
     */
    Map<Class, Map<String, Semaphore>> mapSemaphore = new HashMap<Class, Map<String, Semaphore>>();
    /**
     * *
     * Instance du singleton ....
     */
    private static SemaphoreCentre instance = new SemaphoreCentre();
    /**
     * *
     * Le logger
     */
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
    /**
     * *
     * Objet verrou qu'il faut notifier pour déclancher le menage. c'est la methode {@link #declancherMenage() } qui est
     * chargé de notifier
     */
//    private final Object lockMenage = new Object();
    /**
     * *
     * Nombre de seconde entre chaque passage du manage la map de semaphore
     */
    private Integer nbWaitTimeForClean = 3600;
//   public Semaphore semMenage = new Semaphore(999);
    /**
     * *
     * Etat du menage.
     */
    private Boolean menage = false;
    /**
     * *
     * Object sur lequel il faut attendre lorsque le manage est en cours
     */
    private Object finMenage = new Object();
    /**
     * *
     * Notifier cet object provoque le ménage
     */
    private Object declancherMenage = new Object();

    /**
     * *
     * Obtenir l'instance du singleton
     *
     * @return
     */
    public static SemaphoreCentre getinstance() {
        if (instance == null) {
            instance = new SemaphoreCentre();
        }
        return instance;
    }

    /**
     * *
     * Retourne la sémaphore pour l'object envoyé en argument. La méthode parcour le map {@link #mapSemaphore} afin de
     * trouver une semaphore précédement instancié pour l'objet envoyé en argument, si aucune semaphore n'est trouvée,
     * la méthode crée alors cette nouvelle semaphore et l'enregistre dans la map
     *
     * @param o Objet devant être un beans respectant les règles du projet. Il doit posséder un id sous forme d'un Long
     * @return
     */
    public synchronized Semaphore returnSemaphoreForRessource(Object o) throws NullPointerException, IllegalAccessException {
        ExceptionTool.argumentNonNull(o);
        ExceptionTool.checkNonNullField(o, "ID");

        // On récupère la map 
        Map<String, Semaphore> map = mapSemaphore.get(o.getClass());
        if (map == null) {
            map = new HashMap<String, Semaphore>();
            mapSemaphore.put(o.getClass(), map);
        }

        // La clé est basé sur le nom de classe du beans concat à l'id du beans
        String cle;

        if (Bean.class.isAssignableFrom(o.getClass())) {
            Bean b = (Bean) o;
            cle = o.getClass().getSimpleName() + b.getID();
        } else {
            cle = o.getClass().getSimpleName() + o.hashCode();
        }

        Semaphore semaphore = map.get(cle);

        if (semaphore == null) {
            semaphore = new Semaphore(1, true);
            map.put(cle, semaphore);
        }
        return semaphore;
    }

    /**
     * *
     * Lancement de la tache permettant de nettoyer périodiquement la map de semaphore.
     */
    @Override
    public void run() {
        logger.debug("Lancement de " + this);
        try {
            while (true) {
                try {
                    synchronized (this) {

                        menage = true;

                        logger.debug("-------------------------------------\nPurge des semaphores innutiles");

                        ThreadUtils.interruptCheck();
                        for (Map.Entry<Class, Map<String, Semaphore>> entry : mapSemaphore.entrySet()) {
                            Class class1 = entry.getKey();
                            Map<String, Semaphore> map = entry.getValue();
                            for (Iterator<Entry<String, Semaphore>> it = map.entrySet().iterator(); it.hasNext();) {

                                Entry<String, Semaphore> entr = it.next();
//                                String string = entr.getKey();
                                Semaphore semaphore = entr.getValue();

                                try {

                                    boolean acquis = semaphore.tryAcquire();

                                    if (acquis) { // Si on peut acquerir la semaphore
                                        if (!semaphore.hasQueuedThreads()) { // et si elle n'est attendu par aucune thread
                                            it.remove(); // On supprime la semaphore. Le sema centre est synchronisé. Aucune autre thread n'a pu demander la semaphore pendant le menage donc c'est Therad Safe


                                            logger.debug("--->Suppression d'une semphore : " + class1.getSimpleName() + semaphore);
                                        } else {
                                            semaphore.release();
                                        }
                                    }

                                } catch (Exception e) {
                                    logger.debug("Erreur dans le SemaCentre", e);
                                }
                            }
                        }
                        ThreadUtils.interruptCheck();
//                        semMenage.release(drainPerm);

                        menage = false;
                        synchronized (finMenage) {
                            finMenage.notifyAll();
                        }

                        logger.debug("Fin de purge \n -------------------------------------------");
                    }

                    synchronized (declancherMenage) {
                        declancherMenage.wait(nbWaitTimeForClean * 1000);
                    }

                } finally {
                }
            }

        } catch (InterruptedException e) {
            logger.debug("Interruption de " + this);
        }
    }

    /**
     * *
     * @see #nbWaitTimeForClean
     * @return
     */
    public Integer getNbWaitTimeForClean() {
        return nbWaitTimeForClean;
    }

    /**
     * *
     * @see #nbWaitTimeForClean
     * @param nbWaitTimeForClean
     */
    public void setNbWaitTimeForClean(Integer nbWaitTimeForClean) {
        this.nbWaitTimeForClean = nbWaitTimeForClean;
    }

    /**
     * *
     * Declanche le ménage de la map de semaphore en provoquant l'itération de la Thread chargé d'effectuer le ménage.
     *
     * @see #run()
     */
    public void declancherMenage() {
        
        synchronized(declancherMenage){
            declancherMenage.notifyAll();
        }

    }

}
