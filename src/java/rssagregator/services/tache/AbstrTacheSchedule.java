/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import rssagregator.beans.exception.ActionNonEffectuee;
import rssagregator.beans.incident.ObjectIncompatible;
import rssagregator.services.AbstrService;
import rssagregator.utils.ExceptionTool;

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
     * 1 = temps fixe 2 : tous les jour à 3 : un jour par semaine
     */
    Byte typeSchedule;
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
    Throwable exeption = null;
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
    Integer nbrTentative = 0;
    /**
     * *
     * Permet de savoir combien de fois la tache doit être réexecuté en cas d'erreur
     */
    short nbMaxReExecution = 1;
    /**
     * *
     * Temps d'attente en seconde avant une réexecution
     */
    short nbSleepError = 1;
    /**
     * *
     * Le service Controlant la tâche
     */
    AbstrService service;
    /**
     * *
     * Temps maximal d'execution de la tache en seconde. Le service va interrompre la tache si il est dépassé voit la
     * tache {@link CalableAntiDeadBlock} . Il faut prévoir 5 x ou plus le temps normal.
     */
    protected Short maxExecuteTime = 60;
    /**
     * *
     * Date de la dernière execution
     */
    protected Date lasExecution = null;
    /**
     * *
     * Permet de déterminer le log4j level qui doit être utilisé lorsque survient une erreur lors du traitement de la
     * tache. Par défault c'est info. Pour certaine tâche, on peut vouloir un erreur afin que log4j envoi
     * automatiquement un mail.
     */
    protected int logErrorLevel = org.apache.log4j.Level.INFO_INT;
    /**
     * *
     * De noubreuse tache utilise un entitymanager et gère une transaction
     */
    protected EntityManager em = null;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
    /**
     * *
     * La date de prochaine execution
     */
    protected Date nextExecution = null;
    /**
     * *
     * indique si la tache est en cours d'execution
     */
    protected boolean running = false;
    /**
     * *
     * Liste contenant les références aux objets locké par la tache
     */
    List<Object> listRessourcesLocke = new ArrayList<Object>();

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
     * Initialise la tache pour qu'elle soit prete a lancer. Modifi le statut anuller last exe,
     */
    public void initTask() {

        this.annuler = false;
        this.exeption = null;
        this.running = false;
        this.lasExecution = null;
        this.nbrTentative = 0;

    }

    /**
     * *
     * Initialise les variables de la tâche en début de traitement (block du try du call). - annuler = false exeption =
     * null running = true last
     */
    public void initLancementTache() {
        annuler = false;
        exeption = null;
        running = true;
        lasExecution = new Date();
        nbrTentative = 0;
        listRessourcesLocke = new ArrayList<Object>();
    }

    /**
     * *
     * Méthode nécessairement déclanché à la fin du call de la tâche (block finaly du call. Complete la next execution,
     * variable running etc...; Ferme l'em; libère la semaphore
     */
    public void finTache() {
        running = false;

        //--->roolback de la transaction si elle est encore ouverte
        try {
            commitTransaction(false);
        } catch (Exception e) {
            logger.error("Erreur lors d'un roolback de " + this, e);
        }

        //---> Fermeture de l'em
        closeEM();


        //----> Complétion de la prochaine execution des tache schedule
        if (this.schedule) {
            try {
                completerNextExecution();
            } catch (NullPointerException ex) {
                logger.error("Erreur Fin tache " + this, ex);
            } catch (ObjectIncompatible ex) {
                logger.error("Erreur Fin tache " + this, ex);
            } catch (ActionNonEffectuee ex) {
                logger.error("Erreur Fin tache " + this, ex);
            }
        }

        //---> Liberation de la sémaphore ce qui permet a d'autre tâche de se lancer pour par exemple collecter de la donnée sur le journal occupé
        if (sema != null) { //Libération de la semaphore si elle existe
            sema.release();
        }

        //--> notification auprès des observer (le Service lié a la tâche)
        this.setChanged();   //On se notifi au service qui va rescheduler
        this.notifyObservers();

//        closeEM();
    }

    public static void main(String[] args) {
        Byte b;
        b = 10;
        if (b == 110) {
            System.out.println("TRUE");
        } else {
            System.out.println("FALSE");
        }

    }

    /**
     * *
     * Complete la variable {@link #nextExecution} en fonction du type de Schedul ({@link #typeSchedule}) de
     * {@link #timeSchedule} , {@link #heureSchedule}...
     *
     * @throws NullPointerException Si la var schedule ou typeschedull == null
     * @throws ObjectIncompatible Si la tache n'est pas schedule ou si la schedulation n'est pas normale.
     * @throws ActionNonEffectuee : Si le traitement n'a pu aboutir a a la programmation dune tache bien que les
     * arguments soient correct
     */
    public void completerNextExecution() throws NullPointerException, ObjectIncompatible, ActionNonEffectuee {
        ExceptionTool.argumentNonNull(schedule);
        ExceptionTool.argumentNonNull(typeSchedule);
        if (!schedule) {
            throw new ObjectIncompatible("La tache n'est pas schedulée. ");
        }
        if (typeSchedule < 0 || typeSchedule > 3) {
            throw new ObjectIncompatible("Le type de schedulation n'est pas connu");
        }


        boolean act = false;
        if (schedule) {
            // Si c'est une tache schedulea temps de schedule fixe

            if (typeSchedule != null && typeSchedule == 1) {
                DateTime now = new DateTime();
                DateTime next = now.plusSeconds(timeSchedule.intValue());
                this.nextExecution = next.toDate();
                act = true;
            } else if (typeSchedule != null && typeSchedule == 2) { // Tous les jours a 
                ExceptionTool.argumentNonNull(this.heureSchedule);
                ExceptionTool.argumentNonNull(this.minuteSchedule);

                DateTime next = new DateTime().withHourOfDay(this.heureSchedule).withMinuteOfHour(this.minuteSchedule);

                if (next.isBefore(new DateTime())) {
                    next = next.plusDays(1);
                }
                this.nextExecution = next.toDate();
                act = true;

            } else if (typeSchedule != null && typeSchedule == 3) {
                DateTime next = new DateTime();

                next = next.withHourOfDay(this.heureSchedule);
                next = next.withMinuteOfHour(this.minuteSchedule);

                if (next.getDayOfWeek() < this.jourSchedule) {
                    next = next.withDayOfWeek(this.jourSchedule);
                } else {
                    next = next.plusWeeks(1);
                    next = next.withDayOfWeek(this.jourSchedule);
                }
                this.nextExecution = next.toDate();
                act = true;

            }
        }
        if (!act) {
            throw new ActionNonEffectuee("Aucune schedul n'a été éffectué");
        }
    }

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

    public abstract T executeProcessus() throws InterruptedException;

    public Short getMaxExecuteTime() {
        return maxExecuteTime;
    }

    public void setMaxExecuteTime(Short maxExecuteTime) {
        this.maxExecuteTime = maxExecuteTime;
    }

    public boolean isRunning() {
        return running;
    }

    public Boolean getRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void forceChange() {
        this.setChanged();
    }

    public short getNbMaxReExecution() {
        return nbMaxReExecution;
    }

    public void setNbMaxReExecution(short nbMaxReExecution) {
        this.nbMaxReExecution = nbMaxReExecution;
    }

    public short getNbSleepError() {
        return nbSleepError;
    }

    public void setNbSleepError(short nbSleepError) {
        this.nbSleepError = nbSleepError;
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

    public Date getNextExecution() {
        return nextExecution;
    }

    public void setNextExecution(Date nextExecution) {
        this.nextExecution = nextExecution;
    }
    /**
     * *
     * La semathore provenant de {@link SemaphoreLancementTache] pouvant être utilisé par les tâche pour locker une ressource sans passer par JPA
     */
    Semaphore sema = null;

    /**
     * *
     * Donne le nombre de seconde depuis la dernière execution
     *
     * @return
     */
    public long returnExecutionDuration() {

        DateTime now = new DateTime();
        DateTime lastExe = new DateTime(this.lasExecution);
        Duration dur = new Duration(lastExe, now);
        return dur.getStandardSeconds();

    }

    /**
     * *
     * Donne le nombre de seconde avant la prochaine execution de la Tâche
     *
     * @return
     * @throws NullPointerException : Si la {@link #nextExecution} est null le calcul est impossible.
     */
    public long returnNextDuration() throws NullPointerException {

//        ExceptionTool.argumentNonNull(this.nextExecution);

        if (this.nextExecution == null) {
            try {
                completerNextExecution();
            } catch (ObjectIncompatible ex) {
                java.util.logging.Logger.getLogger(AbstrTacheSchedule.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ActionNonEffectuee ex) {
                java.util.logging.Logger.getLogger(AbstrTacheSchedule.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        ExceptionTool.argumentNonNull(this.nextExecution);


        DateTime now = new DateTime();
        DateTime next = new DateTime(this.nextExecution);

        Duration dur = new Duration(now, next);

        return dur.getStandardSeconds();
    }

    public int getLogErrorLevel() {
        return logErrorLevel;
    }

    public void setLogErrorLevel(int logErrorLevel) {
        this.logErrorLevel = logErrorLevel;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Semaphore getSema() {
        return sema;
    }

    public void setSema(Semaphore sema) {
        this.sema = sema;
    }

    /**
     * Ferme l'Entiti Manager manage de la tache
     */
    public synchronized void closeEM() {

        if (em != null) {
            if (em.isOpen()) {
                em.close();
            }
        }
        em = null; // supprime la référence à l'em
    }

    /**
     * *
     * @see #typeSchedule
     * @return
     */
    public Byte getTypeSchedule() {
        return typeSchedule;
    }

    /**
     * *
     * @see #typeSchedule
     * @param typeSchedule
     */
    public void setTypeSchedule(Byte typeSchedule) {
        this.typeSchedule = typeSchedule;
    }

    /**
     * Commit ou roolback la transaction de l'em
     */
    public void commitTransaction(Boolean commit) throws Exception {

        if (em != null) {
            if (em.isJoinedToTransaction()) {


                if (commit) {
                    try {
                        em.getTransaction().commit();
                    } catch (Exception e) {
                        logger.error("erreur lors du commit ", e);
                        throw e;
                    }

                } else {
                    try {
                        em.getTransaction().rollback();
                    } catch (Exception e) {
                        logger.error("Erreur lors du roolback", e);
                        throw e;
                    }
                }
            }
        }
    }
}
