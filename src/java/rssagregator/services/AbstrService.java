/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Observer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 *
 * @author clem
 */
public abstract class AbstrService implements Observer {

    protected ScheduledExecutorService executorService;

    public AbstrService(ScheduledExecutorService executorService1) {
        executorService = executorService1;
    }

    public AbstrService() {
    }
    
    

//    public AbstrService() {
//        ScheduledExecutorService exe = Executors.newScheduledThreadPool(1);
//        this.executorService = exe;
//    }
    /**
     * *
     * Lance toutes les tâches par défaut devant être gérée par le service
     */
//    public abstract void instancierTaches();

    //    @Override
    //    public void update(Observable o, Object arg){
    //
    //    };
    /**
     * *
     * La méthode qui permet à un service de scheduler un callable. Cette
     * méthode doit être redéfinit afin d'intéragir avec le retour du callable.
     * Elle doit notamment rescheduler le callable en fonction du paramettre
     * delay en argument
     *
     * @param c
     */
    //    public abstract void scheduleCallable(Callable<T> c, Long delay);
    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * *
     * Cette méthode est utilisée après qu'une tache shedulé se soie notifié
     * auprès du service. Cette méthode doit être redéfinie dans chacun des
     * service. Si des exeptions se sont produites lors de la tache
     * tache.exeption != null, alors cette méthode doit par exemple créer un
     * incident approprié
     *
     * @param tache
     */
    protected abstract void gererIncident(AbstrTacheSchedule tache);

    /**
     * *
     * permet de scheduler une tache suivant les paramettre définit dans la
     * tache. soit par timeScjedule qui définit un nombre de seconde soit grace
     * aux variable jour heure minute qui définissent la prochaine execution.
     *
     * @param tache
     */
    public void schedule(AbstrTacheSchedule tache) {

        
        // pour une tache devant être schedulé suivant un nombre fixe de seconde
        if (tache.getTimeSchedule() != null) {
            this.executorService.schedule(tache, tache.getTimeSchedule(), TimeUnit.SECONDS);
            
            // Pour une tache devant être schedule une fois pas semaine (jour heure et minute non null dans la tache
        } else if (tache.getHeureSchedule() != null && tache.getJourSchedule() != null && tache.getMinuteSchedule() != null) {

            DateTime dtCurrent = new DateTime();
            DateTime next = dtCurrent.withDayOfWeek(new Integer(tache.getJourSchedule()));
            Duration dur = new Duration(dtCurrent, next);
            this.executorService.schedule(tache, dur.getStandardSeconds(), TimeUnit.SECONDS);
        }
        //Si c'est une tache devant être schedulé tous les jours à la meme heure (jour null mais heure et minute non null
        else if (tache.getJourSchedule()==null && tache.getHeureSchedule()!=null && tache.getMinuteSchedule()!=null){
            DateTime dtCurrent = new DateTime();
            DateTime next = dtCurrent.withTime(tache.getHeureSchedule(), tache.getMinuteSchedule(), 0, 0);
            Duration dur = new Duration(dtCurrent, next);
            this.executorService.schedule(tache, dur.getStandardSeconds(), TimeUnit.SECONDS);
            
        }

    }

    /**
     * *
     * Permet de stoper le service. Cette méthode doit être redéfini par tous
     * les service. Chacun est chargé de clore les tache lancée et détruire son
     * pool de thread
     */
    public void stopService() throws SecurityException, RuntimeException{
        if (this.executorService != null) {
            this.executorService.shutdownNow();
        }
    };
}
