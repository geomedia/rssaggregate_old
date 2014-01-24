/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.joda.time.DateTime;
import rssagregator.beans.Journal;
import rssagregator.services.tache.AbstrTacheSchedule;
import rssagregator.services.tache.TacheRecupCallable;
import rssagregator.utils.ThreadUtils;

/**
 * /!\ N'est plus utilisé au profit du producteur consomateur
 * @author clem
 */
public class CallableCollecteSubmiter implements Callable<Object> {

    @Override
    public Object call() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    AbstrService service;
//    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CallableCollecteSubmiter.class);
//    /**
//     * *
//     * Permet de stoquer les taches éxecuté pour un journal
//     */
//    Map<Long, AbstrTacheSchedule> mapJournalTache = new HashMap<Long, AbstrTacheSchedule>();
//
//    @Override
//    public Object call() throws Exception {
//
//        Thread.sleep(30000);
//
//        try {
//
//
//            while (true) {
//                logger.debug("Lancement de " + this);
//                ThreadUtils.interruptCheck();
//                Map<AbstrTacheSchedule, Future> map = service.getMapTache();
//                
//                synchronized (map) {
//
//                    List<Long> JournauxLanceCetour = new ArrayList<Long>();
//
//
//                    Iterator<Map.Entry<AbstrTacheSchedule, Future>> iterator = map.entrySet().iterator();
//                    while (iterator.hasNext()) {
//
//                        Map.Entry<AbstrTacheSchedule, Future> entry = iterator.next();
//                        AbstrTacheSchedule abstrTacheSchedule = entry.getKey();
////                        logger.debug("IT : " + abstrTacheSchedule);
//
//                        
//                        
//                        //---> Gestion des taches de récupération
//                        if (abstrTacheSchedule.getClass().equals(TacheRecupCallable.class)) {
//
//                            TacheRecupCallable cast = (TacheRecupCallable) abstrTacheSchedule;
//
//                            if (cast.getSchedule() && !cast.isRunning()) { // Si c'est une tache schedulé et qu'elle n'est pas déja encours d'execution
//
//                                DateTime dtNow = new DateTime();
//                                // 
//                                if (cast.getNextExecution() == null) { // Lorsqu'on a pas encore d'exe la variable peut être null
//                                    DateTime next = new DateTime().plusSeconds(cast.getTimeSchedule().intValue());
//                                    cast.setNextExecution(next.toDate());
//                                }
//
//                                if (cast.getNextExecution() != null) {
//                                    DateTime dtNext = new DateTime(cast.getNextExecution());
//
//                                    // Si il faut realcer la tache
//                                    if (dtNext.isBefore(dtNow)) {
//
//
//                                        if (cast.getFlux().getJournalLie() != null) { // Si le flux a un journal
//
//                                            if (!JournauxLanceCetour.contains(cast.getFlux().getJournalLie().getID())) {
//                                                if (!existanceDeTacheEncoursPourleJournal(cast.getFlux().getJournalLie())) { // On vérifie si on a déjà une tache en cours pour le journal
//                                                    logger.debug("--> Submit de " + cast);
//                                                    mapJournalTache.put(cast.getFlux().getJournalLie().getID(), abstrTacheSchedule);
//                                                    Future fut = service.submit(abstrTacheSchedule);
////                                                    service.addTask(abstrTacheSchedule, fut); // On place le nouveau future dans la map du service
//                                                    JournauxLanceCetour.add(cast.getFlux().getJournalLie().getID());
//                                                }
//                                            }
//
//
//                                        } else { // Si pas de journal
//                                            logger.debug("ajout " + abstrTacheSchedule);
//                                            Future fut = service.submit(abstrTacheSchedule);
//                                            service.addTask(abstrTacheSchedule, fut); // On place le nouveau future dans la map du service
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        
//                        
//                        
//                        
//                    }
//                }
//                Thread.sleep(10000);
//            }
//        } catch (InterruptedException e) {
//            logger.debug("Interruption de " + this);
//        }
//
//
//
//        return null;
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    public AbstrService getService() {
//        return service;
//    }
//
//    public void setService(AbstrService service) {
//        this.service = service;
//    }
//
//    /**
//     * *
//     * Vérifie si il existe une tache en cours d'execution pour le journal envoyé en argument
//     *
//     * @param j
//     * @return true si une tache est déjà lancé pour ce journal
//     */
//    private boolean existanceDeTacheEncoursPourleJournal(Journal j) {
//
//        Long id = j.getID();
//        AbstrTacheSchedule tache = mapJournalTache.get(id);
////        logger.debug("tache ds la map : " + mapJournalTache);
//
//        if (tache != null && tache.isRunning()) {
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public String toString() {
//        return "CallableCollecteSubmiter{" + "service=" + service + '}';
//    }
//    
}
