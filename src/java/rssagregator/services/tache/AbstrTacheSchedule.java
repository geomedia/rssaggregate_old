/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import rssagregator.services.AbstrService;

/**
 * Toutes les tâche schedule de l'application doivent hériter de cette classe abstraite
 *
 * @author clem
 */
public abstract class AbstrTacheSchedule<T> extends Observable implements Callable<T> {

//    ScheduledExecutorService executorService;
//    AbstrService service;
    /**
     * *
     * Détermine si l'instance de la tache doit avoir ou non un comportment périodique
     */
    Boolean schedule;
    /**
     * *
     * Jour auquella tâche doit être executé
     */
    Integer jourSchedule;
    /**
     * *
     * Heure à laquelle la tâche doit être executé
     */
    Integer heureSchedule;
    /**
     * *
     * minute à laquelle la tâche doit être exécuté
     */
    Integer minuteSchedule;
    /**
     * *
     * Nombre de secondes devant séparer 2 exécution de la tâche pour une tâche a execution régulière
     */
    Integer timeSchedule;
    /**
     * *
     * si il y a eu une erreur lors de l'execuption de la tâche, On inclu cette erreur ici. La runnable se retourne avec
     * son erreur auprès du service qui le gère.
     */
    Throwable exeption;
    /**
     * *
     * Boollean permettant d'annuler la tache
     */
    Boolean annuler = false;
    /**
     * *
     * Une tache peut être en erreur. Les services utilise ce Integer pour savoir combien de fois cette tache a été en
     * erreur. Il peuvent relancer la tâche ou alors créer un incident. La gestion des erreur est propre à chaque tâche
     * et chaque service
     */
    Integer nbrTentative;
    /**
     * *
     * Le service Controlant la tâche
     */
    AbstrService service;
    /**
     * *
     * Temps maximal d'execution de la tache en seconde. Le service va interrompre la tache si il est dépassé voit la
     * tache {@link CalableAntiDeadBlock}
     */
    protected Short maxExecuteTime = 60;
    protected Date lasExecution;
    protected boolean running = false;

    protected AbstrTacheSchedule() {
        this.schedule = false;
        exeption = null;
        nbrTentative = 0;
    }

    /**
     * *
     * Par default le bollean schedule est à false;
     *
     * @param executorService
     */
    protected AbstrTacheSchedule(Observer s) {
        this.addObserver(s);
        this.schedule = false;
        exeption = null;
        nbrTentative = 0;
    }

    /**
     * *
     * Détermine si l'instance de la tache doit avoir ou non un comportment périodique
     *
     * @return
     */
    public Boolean getSchedule() {

        return schedule;
    }

    /**
     * Détermine si l'instance de la tache doit avoir ou non un comportment périodique
     *
     * @param schedule
     */
    public void setSchedule(Boolean schedule) {
        this.schedule = schedule;
    }

    public Throwable getExeption() {
        return exeption;
    }

    public void setExeption(Throwable exeption) {
        this.exeption = exeption;
    }

    public Integer getNbrTentative() {
        return nbrTentative;
    }

    public void setNbrTentative(Integer nbrTentative) {
        this.nbrTentative = nbrTentative;
    }

    public Integer getJourSchedule() {
        return jourSchedule;
    }

    public AbstrService getService() {
        return service;
    }

    public void setService(AbstrService service) {
        this.service = service;
    }

    /**
     * *
     * Revoie une chaine de caractère renseignant la périodicité d'exécution de la tache. exemple "toutes les heure" ou
     * "tous les mardi a 2h"...
     *
     * @return
     */
    public String printSchedule() {
        String retour = "";
        if (this.timeSchedule != null) {
            Duration dur = new Duration(timeSchedule.longValue() * 1000);
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendDays()
                    .appendSuffix("d")
                    .appendHours()
                    .appendSuffix("h")
                    .appendMinutes()
                    .appendSuffix("m")
                    .appendSeconds()
                    .appendSuffix("s")
                    .toFormatter();
            String formatted = formatter.print(dur.toPeriod());
            retour = formatted;
        } else if (jourSchedule == null && heureSchedule != null && minuteSchedule != null) {
            retour += "tous les jours à " + heureSchedule + "h" + minuteSchedule;
        } else if (jourSchedule != null && heureSchedule != null && minuteSchedule != null) {
            retour += "tous les " + Days.days(jourSchedule) + " à " + heureSchedule + "h" + minuteSchedule;
        }
        return retour;
    }

    /**
     * *
     * Le jour de la schedul. Suivant les constante de datetime. 1 = monday; 2 = tuesday...
     *
     * @param jourSchedule
     */
    public void setJourSchedule(Integer jourSchedule) {
        this.jourSchedule = jourSchedule;
    }

    public Integer getHeureSchedule() {
        return heureSchedule;
    }

    public void setHeureSchedule(Integer heureSchedule) {
        this.heureSchedule = heureSchedule;
    }

    public Integer getMinuteSchedule() {
        return minuteSchedule;
    }

    public void setMinuteSchedule(Integer minuteSchedule) {
        this.minuteSchedule = minuteSchedule;
    }

    public Integer getTimeSchedule() {
        return timeSchedule;
    }

    public void setTimeSchedule(Integer timeSchedule) {
        this.timeSchedule = timeSchedule;
    }

    public Boolean getAnnuler() {
        return annuler;
    }

    public void setAnnuler(Boolean annuler) {
        this.annuler = annuler;
    }


//    /**
//     * *
//     * Methode permettant d'annuler la tache
//     */
//    public void annuler() throws Exception {
//        annuler = true;
////        call();
//        Thread.currentThread().interrupt();
//    }
    public Date getLasExecution() {
        return lasExecution;
    }

    public void setLasExecution(Date lasExecution) {
        this.lasExecution = lasExecution;
    }

    protected abstract void callCorps() throws Exception;

    /**
     * *
     * Le bloc exécuté a la fin de l'appel de la tache. Il faut notifier le service. Libérer les ressources, Retourner
     * la tache elle meme.
     *
     * @return
     */
    protected abstract T callFinalyse();

    public Short getMaxExecuteTime() {
        return maxExecuteTime;
    }

    public void setMaxExecuteTime(Short maxExecuteTime) {
        this.maxExecuteTime = maxExecuteTime;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void forceChange() {
        this.setChanged();
    }
}
