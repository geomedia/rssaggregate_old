/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
        ScheduledExecutorService exe = Executors.newScheduledThreadPool(1);
        this.executorService = exe;
    }

    /**
     * *
     * Lance toutes les tâches par défaut devant être gérée par le service
     */
    public abstract void instancierTaches();

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
}
