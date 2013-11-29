/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import rssagregator.services.tache.AbstrTacheSchedule;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observer;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import rssagregator.beans.exception.ArgumentIncorrect;

/**
 *
 * @author clem
 */
public abstract class AbstrService implements Observer {

    protected ScheduledExecutorService executorService;
//    List<AbstrTacheSchedule> listTache = new ArrayList<AbstrTacheSchedule>();
    /**
     * *
     * Permet de retrouver les tache et leur future. Cette map est observer pas la tâche {@link CalableAntiDeadBlock}
     */
    protected Map<AbstrTacheSchedule, Future> mapTache = new HashMap<AbstrTacheSchedule, Future>();
    /**
     * *
     * Une thread qui tourne en boucle pour vérifier les taches contenues dans {@link #mapTache}. Permet d'interrompre
     * des tache pou réviter les deadblock
     */
    CalableAntiDeadBlock antiDeadBlock = new CalableAntiDeadBlock();

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
     * La méthode qui permet à un service de scheduler un callable. Cette méthode doit être redéfinit afin d'intéragir
     * avec le retour du callable. Elle doit notamment rescheduler le callable en fonction du paramettre delay en
     * argument
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
     * Cette méthode est utilisée après qu'une tache shedulé se soie notifié auprès du service. Cette méthode doit être
     * redéfinie dans chacun des service. Si des exeptions se sont produites lors de la tache tache.exeption != null,
     * alors cette méthode doit par exemple créer un incident approprié
     *
     * @param tache
     */
    protected abstract void gererIncident(AbstrTacheSchedule tache);

    /**
     * *
     * permet de scheduler une tache suivant les paramettre définit dans la tache. soit par timeScjedule qui définit un
     * nombre de seconde soit grace aux variable jour heure minute qui définissent la prochaine execution.
     *
     * @param tache
     */
    public void schedule(AbstrTacheSchedule tache) {
        Future fut = null;
        if (!tache.getAnnuler()) { // Si la tache a été annulée il ne faut pas la rescheduler
            // pour une tache devant être schedulé suivant un nombre fixe de seconde
            if (tache.getTimeSchedule() != null) {
                fut = this.executorService.schedule(tache, tache.getTimeSchedule(), TimeUnit.SECONDS);
                // Pour une tache devant être schedule une fois pas semaine (jour heure et minute non null dans la tache
            } else if (tache.getHeureSchedule() != null && tache.getJourSchedule() != null && tache.getMinuteSchedule() != null) {

                DateTime dtCurrent = new DateTime();
                DateTime next = dtCurrent.withDayOfWeek(new Integer(tache.getJourSchedule()));
                Duration dur = new Duration(dtCurrent, next);
                fut = this.executorService.schedule(tache, dur.getStandardSeconds(), TimeUnit.SECONDS);
            } //Si c'est une tache devant être schedulé tous les jours à la meme heure (jour null mais heure et minute non null
            else if (tache.getJourSchedule() == null && tache.getHeureSchedule() != null && tache.getMinuteSchedule() != null) {
                DateTime dtCurrent = new DateTime();
                DateTime next = dtCurrent.withTime(tache.getHeureSchedule(), tache.getMinuteSchedule(), 0, 0);
                Duration dur = new Duration(dtCurrent, next);
                fut = this.executorService.schedule(tache, dur.getStandardSeconds(), TimeUnit.SECONDS);
            }

            if (fut != null) {
                addTask(tache, fut); // Ajout de la tache au map permettant au service de retrouver et gérer l'ensemble de ses tâches.
            } else {
                throw new UnsupportedOperationException("Il n'est pas possible de schedulé la tache envoyé en argument");
            }
        }
    }

    /***
     * Soumission de la tâche dans le pool du service.
     * @param tache
     * @return 
     */
    public Future submit(AbstrTacheSchedule tache) {
        if(tache == null){
            throw new NullPointerException("Impossible de soumettre une tache null");
        }
        
        Future fut = executorService.submit(tache);
        addTask(tache, fut); // Ajout de la tache au map permettant au service de retrouver et gérer l'ensemble de ses tâches.
        return fut;
    }

    /**
     * *
     * Permet de stoper le service. Cette méthode doit être redéfini par tous les service. Chacun est chargé de clore
     * les tache lancée et détruire son pool de thread
     */
    public void stopService() throws SecurityException, RuntimeException {
        if (this.executorService != null) {
            this.executorService.shutdownNow();
        }
    }

    public CalableAntiDeadBlock getAntiDeadBlock() {
        return antiDeadBlock;
    }

    public void setAntiDeadBlock(CalableAntiDeadBlock antiDeadBlock) {
        this.antiDeadBlock = antiDeadBlock;
    }

    public Map<AbstrTacheSchedule, Future> getMapTache() {
        return mapTache;
    }

    public void setMapTache(Map<AbstrTacheSchedule, Future> mapTache) {
        this.mapTache = mapTache;
    }

    /**
     * *
     * Ajoute une tâche et son future dans la map permettant au service de gérer ses tâches lancées. Les tache n'ont pas forcément à être schedulée.
     *
     * @param tache
     * @param fut
     */
    public void addTask(AbstrTacheSchedule tache, Future fut) {

        synchronized (mapTache) {
            mapTache.put(tache, fut);
        }

    }

    
    public void remTask(AbstrTacheSchedule tache) {
        synchronized (mapTache) {
            mapTache.remove(tache);
        }
    }

    /**
     * *
     * Lance le service, notamment en démarrant la tache antideadBlock {@link CalableAntiDeadBlock}
     */
    public void lancerService() {

        if (antiDeadBlock != null) {
            antiDeadBlock.setService(this);
            this.executorService.submit(antiDeadBlock);
        }
    }

    /**
     * *
     * Cherche la tache dans la map des tache controlé, annule la tache et la reschedule
     *
     * @param tache
     */
    public void relancerTache(AbstrTacheSchedule tache) throws ArgumentIncorrect {

        if (tache == null) {
            throw new NullPointerException("impossible d'annuler une tache null");
        }
        if (tache.getSchedule() == null) {
            throw new ArgumentIncorrect("Null schedule");
        } else if (!tache.getSchedule()) {
            throw new ArgumentIncorrect("On ne peut relancer qu'une tache schedule");
        }


        // On trouve le futur de la tache
        Entry<AbstrTacheSchedule, Future> entr = null;
        for (Map.Entry<AbstrTacheSchedule, Future> entry : mapTache.entrySet()) {
            AbstrTacheSchedule abstrTacheSchedule = entry.getKey();
            Future future = entry.getValue();

            if (abstrTacheSchedule.equals(tache)) {
                entr = entry;
                break;
            }
        }

        if (entr != null) {

            System.out.println("ON A UNE ENTRY");
            // Call de la tache
            try {
                entr.getValue().cancel(true);
                int i = 0; // Il faut attendre que la tache ne soit plus en running pour la rescheduler
                while (i < 100) {
                    System.out.println("EXECUTING : " + tache.isRunning());
                   
                    if (!tache.isRunning()) {
                        break;
                    }
                     Thread.sleep(100);
                    i++;
                }


            } catch (Exception e) {
            }
            if (tache.getSchedule()) {
                tache.setAnnuler(false);
                System.out.println("Reschedule");
                schedule(tache);
            }

        }


    }
}
