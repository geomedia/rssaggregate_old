/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;

/**
 *  Toutes les tâche schedule de l'application doivent hériter de cette classe abstraite
 * @author clem
 */
public abstract class AbstrTacheSchedule<T> extends Observable implements Callable<T>{

//    ScheduledExecutorService executorService;
    
    
//    AbstrService service;
    
    /***
     * Détermine si l'instance de la tache doit avoir ou non un comportment périodique
     */
    Boolean schedule;
    
    /***
     * si il y a eu une erreur lors de l'execuption de la tâche, On inclu cette erreur ici. La runnable se retourne avec son erreur auprès du service qui le gère.
     */
    Throwable exeption;
    
    /***
     * Une tache peut être en erreur. Les services utilise ce Integer pour savoir combien de fois cette tache a été en erreur. Il peuvent relancer la tâche ou alors créer un incident. La gestion des erreur est propre à chaque tâche et chaque service
     */
    Integer nbrTentative;

    /***
     * Par default le bollean schedule est à false;
     * @param executorService 
     */
    public AbstrTacheSchedule(Observer s) {
        this.addObserver(s);
        this.schedule = false;
        exeption = null;
        nbrTentative =0;
    }

    /***
     * Détermine si l'instance de la tache doit avoir ou non un comportment périodique
     * @return 
     */
    public Boolean getSchedule() {
        return schedule;
    }
/**
 * Détermine si l'instance de la tache doit avoir ou non un comportment périodique
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
    
    
}
