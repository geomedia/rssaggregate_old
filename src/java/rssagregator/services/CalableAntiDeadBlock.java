/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import rssagregator.services.tache.AbstrTacheSchedule;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import rssagregator.utils.ThreadUtils;

/**
 * Callable lancé dans chaque service afin de vérifier que les tâches du service ne dépassent pas leurs temps maximal d'éxecution. Le but est de prévenir des dead Lock
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
                Map<AbstrTacheSchedule, Future> map = service.getMapTache();

                synchronized (service.getMapTache()) {
                    logger.debug("Lancement "+this+". Nombre de tache à vérifier : " + service.getMapTache().size());
//                    logger.debug("Nombre de tâche a vérifier " + service.getMapTache().size());

                    Iterator<Map.Entry<AbstrTacheSchedule, Future>> iterator = map.entrySet().iterator();
                    while (iterator.hasNext()) {


                        Entry<AbstrTacheSchedule, Future> entry = iterator.next();
                        AbstrTacheSchedule abstrTacheSchedule = entry.getKey();
//                        logger.debug("Verif de " + abstrTacheSchedule);
//                        logger.debug("running : " + abstrTacheSchedule.isRunning());
//                        logger.debug("MaxexecuteTime : " + abstrTacheSchedule.getMaxExecuteTime());

//                        DateTime executeMaxTime = new DateTime(abstrTacheSchedule.getLasExecution()).plusSeconds(abstrTacheSchedule.getMaxExecuteTime().intValue());
//                        DateTime now = new DateTime();
//                        
//                        DateTime dateLastExe = new DateTime(abstrTacheSchedule.getLasExecution());
//                        Duration dureExe = new Duration(dateLastExe, now);
                        
                        

                        if (abstrTacheSchedule.isRunning()) { // Si la tâche est en cours d'éxécution
                            
                            if(abstrTacheSchedule.returnExecutionDuration()> abstrTacheSchedule.getMaxExecuteTime()){
                                  logger.error("La tache " + abstrTacheSchedule+ ". Dépasse son temps d'execuction de " + abstrTacheSchedule.returnExecutionDuration());
                            }

//                            if (executeMaxTime.isBefore(now)) { // Si la tache dépasse son temps d'execution alloué
////                                logger.error("Une tache va être fermé car elle dépasse son temps d'execution : " + abstrTacheSchedule);
//                              
//                                
//
////                                if (abstrTacheSchedule.getService() != null) {
////                                    try {
////                                        abstrTacheSchedule.getService().relancerTache(abstrTacheSchedule);
////                                    } catch (Exception e) {
////                                        logger.debug("Debug", e);
////                                    }
////                                }
//                            }
                        } else { // Si la tâche n'est en train de se dérouler


                            if (abstrTacheSchedule.getLasExecution() != null) { // Si la tache a été lancée
                                if (!abstrTacheSchedule.getSchedule()) { // Si ce n'est pas une tache schedule
                                    iterator.remove(); // On la supprime des tâche a observer
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
