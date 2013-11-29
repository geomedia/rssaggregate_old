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
import java.util.concurrent.ScheduledFuture;
import org.joda.time.DateTime;

/**
 *
 * @author clem
 */
public class CalableAntiDeadBlock implements Callable<Object> {

    AbstrService service;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CalableAntiDeadBlock.class);

    @Override
    public Object call() throws Exception {
        try {
            Thread.sleep(30000); //On lance la tache 30 seconde aprèe le démarrage du service
            while (true) {
                Map<AbstrTacheSchedule, Future> map = service.getMapTache();

                synchronized (service.getMapTache()) {

                    Iterator<Map.Entry<AbstrTacheSchedule, Future>> iterator = map.entrySet().iterator();
                    while (iterator.hasNext()) {

                        Entry<AbstrTacheSchedule, Future> entry = iterator.next();
                        AbstrTacheSchedule abstrTacheSchedule = entry.getKey();
                        Future future = entry.getValue();

//                        logger.debug("Verif de la tache : " + abstrTacheSchedule);

                        DateTime executeTime = new DateTime(abstrTacheSchedule.getLasExecution()).plusSeconds(abstrTacheSchedule.getMaxExecuteTime().intValue());
                        DateTime now = new DateTime();

                        // Si la tache est en cours d'execution (le futur est indisponible)
//                        if (!future.isDone()) {
                        if(abstrTacheSchedule.isRunning()){

                            if (executeTime.isBefore(now)) { // Si la tache dépasse son temps d'execution alloué
                                logger.error("Une tache va être fermé car elle dépasse son temps d'execution : " + abstrTacheSchedule);
//                                try {
//                                    future.cancel(true);
//                                } catch (Exception e) {
//                                    logger.debug("Cancellation Exception: ", e);
                                
//                                }
                                 
                                
                              if(abstrTacheSchedule.getService() != null){
//                                  System.out.println("---------");
                                  try {
//                                      Thread.sleep(500);
                                  abstrTacheSchedule.getService().relancerTache(abstrTacheSchedule);                                      
                                  } catch (Exception e) {
                                      logger.debug("Debug", e);
                                  }

//                                  System.out.println("----------------");
                              }
                                 
                                 
                            }
                        } else { // tache terminé

                            if (!abstrTacheSchedule.getSchedule()) { // Si ce n'est pas une tache schedule on la supprimer de la surveillance
                                iterator.remove();
                            }
                        }
                    }
                }

                Thread.currentThread().sleep(3000); // La tache boucle
            }
        } catch (InterruptedException e) {
            logger.debug("Interruption de la tache anti Deadlock du service " + service.getClass().getName());
        } catch (Exception e) {
            logger.debug("Fin de la tache AntiDeadBlock ", e);
        }
        return null;
    }

    public AbstrService getService() {
        return service;
    }

    public void setService(AbstrService service) {
        this.service = service;
    }
}
