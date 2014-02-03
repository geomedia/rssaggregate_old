/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import rssagregator.services.tache.AbstrTache;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import rssagregator.utils.ThreadUtils;

/**
 * Callable lancé dans chaque service afin de vérifier que les tâches du service ne dépassent pas leurs temps maximal
 * d'éxecution. Le but est de prévenir des dead Lock
 *
 * @author clem
 */
public class CalableAntiDeadBlock implements Callable<Object> {

    AbstrService service;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CalableAntiDeadBlock.class);

    @Override
    public Object call() throws Exception {
        try {
            Thread.sleep(10000); //On lance la tache 30 seconde aprèe le démarrage du service
            while (true) {



                ThreadUtils.interruptCheck();
                List<AbstrTache> list = service.tacheGereeParLeService;

                synchronized (service.tacheGereeParLeService) {

//                    logger.debug("Lancement "+this+". Nombre de tache à vérifier : " + service.getMapTache().size());
                    logger.debug("Lancement " + this + ". Nombre de tache à vérifier : " + list.size());
//                    logger.debug("Nombre de tâche a vérifier " + service.getMapTache().size());



                    for (Iterator<AbstrTache> it = list.iterator(); it.hasNext();) {
                        AbstrTache abstrTacheSchedule = it.next();

                        if (abstrTacheSchedule.isRunning()) { // Si la tâche est en cours d'éxécution

                            if (abstrTacheSchedule.returnExecutionDuration() > abstrTacheSchedule.getMaxExecuteTime()) {
                                logger.error("La tache " + abstrTacheSchedule + ". Dépasse son temps d'execuction de " + abstrTacheSchedule.returnExecutionDuration());
                                service.relancerTache(abstrTacheSchedule);
                            }

                        } else { // Si la tâche n'est en train de se dérouler


                            if (abstrTacheSchedule.getLasExecution() != null) { // Si la tache a été lancée
                                if (!abstrTacheSchedule.getSchedule()) { // Si ce n'est pas une tache schedule
                                    it.remove(); // On la supprime des tâche a observer
                                }
                            }
                        }
                    }

                }
                ThreadUtils.interruptCheck();
                Thread.currentThread().sleep(120000); // La tache boucle


            }
        } catch (InterruptedException e) {
            logger.debug("Interruption de la tache anti Deadlock du service " + service.getClass().getName());
        } catch (Exception e) {
            logger.error("Erreur de la tache " + this, e);
        }
        return null;
    }

    public AbstrService getService() {
        return service;
    }

    public void setService(AbstrService service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "CalableAntiDeadBlock{" + "service=" + service + '}';
    }
}
