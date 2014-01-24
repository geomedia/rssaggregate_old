/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.ArrayList;
import rssagregator.services.tache.AbstrTacheSchedule;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import rssagregator.beans.exception.ArgumentIncorrect;
import rssagregator.services.tache.TacheActionableSurUnBean;
import rssagregator.utils.BeansUtils;
import rssagregator.utils.ExceptionTool;

/**
 *
 * @author clem
 */
public abstract class AbstrService implements Observer {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
    protected ScheduledExecutorService executorService;
    /**
     * *
     * Ce pool de thread permet de gérer le producteur et consomateur de tache ainsi que les tache tel que detection des
     * deadLock
     */
    protected ExecutorService executorServiceAdministratif = Executors.newFixedThreadPool(5);
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
    BlockingQueue<AbstrTacheSchedule> queueTacheALancer = new ArrayBlockingQueue<AbstrTacheSchedule>(999);
    TacheProducteur tacheProducteur = new TacheProducteur(this);
    TacheConsomateur tacheConsomateur = new TacheConsomateur(this);

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
//    protected abstract void gererIncident(AbstrTacheSchedule tache);
    /**
     * *
     * permet de scheduler une tache suivant les paramettre définit dans la tache. soit par timeScjedule qui définit un
     * nombre de seconde soit grace aux variable jour heure minute qui définissent la prochaine execution.
     *
     * @param tache
     * @return true si la tache a pu être schedule sinon false;
     */
//    public Boolean schedule(AbstrTacheSchedule tache) {
//        Future fut = null;
//
//
//        // Les taches de collecte ont une schedulation particulière. Il suffit de les poser dans la map. C'est le CalableCollecteSubmitter qui vient les chercher
//        if (tache.getClass().equals(TacheRecupCallable.class)) {
//            if (!tache.getAnnuler()) {
//                addTask(tache, fut);
//                return true;
//            }
//        } else {
//            if (!tache.getAnnuler()) { // Si la tache a été annulée il ne faut pas la rescheduler
//                // pour une tache devant être schedulé suivant un nombre fixe de seconde
//
//                try {
//                    System.out.println("TACHE : " + tache);
////                    tache.initTask();
//                    tache.completerNextExecution();
//                    fut = this.executorService.schedule(tache, tache.returnNextDuration(), TimeUnit.SECONDS);
//                } catch (Exception e) {
//                    logger.debug("Impossible de déterminer la prochaine execution de " + tache, e);
//                }
//
//
//                if (fut != null) {
//                    addTask(tache, fut); // Ajout de la tache au map permettant au service de retrouver et gérer l'ensemble de ses tâches.
//                    return true;
//                } else {
//                    logger.debug("La tache n'a pas été schedule");
//                    return false;
////                throw new UnsupportedOperationException("Il n'est pas possible de schedulé la tache envoyé en argument");
//                }
//
//            }
//        }
//
//        logger.debug("La tache n'a pas été schedule");
//        return false;
//    }
    /**
     * *
     * Soumission de la tâche dans le pool du service. Si la tache est schedulée, elle est enregistrée dans la map
     * permettant au service de gérer ses taches.
     *
     * @param tache
     * @return
     */
//    @Deprecated
    public Future submit(AbstrTacheSchedule tache) {
        if (tache == null) {
            throw new NullPointerException("Impossible de soumettre une tache null");
        }

        Future fut = executorService.submit(tache);

        if (tache.getSchedule()) {
            addTask(tache, fut); // Ajout de la tache au map permettant au service de retrouver et gérer l'ensemble de ses tâches.
        }

        return fut;
    }

    /**
     * *
     * Permet de stoper le service. Cette méthode doit être redéfini par tous les service. Chacun est chargé de clore
     * les tache lancée et détruire son pool de thread
     */
    public void stopService() throws SecurityException, RuntimeException {
        if (this.executorService != null) {
            try {
                this.executorService.shutdownNow();
            } catch (Exception e) {
                logger.info("Erreur lors de la fermeture du pool central du service " + this, e);
            }
        }


        // fermeture du pool du consommateur de tache
        if (this.tacheProducteur != null) {
            try {
                this.tacheProducteur.es.shutdownNow();
            } catch (Exception e) {
                logger.info("Erreur lors de la fermeture du pool du tache producteur du service " + this, e);
            }
        }

        if (tacheConsomateur != null) {
            try {
                tacheConsomateur.es.shutdownNow();
            } catch (Exception e) {
                logger.info("Erreur lors de la fermeture du pool du consomateur de tache du service " + this, e);
            }
        }



        // Fermeture du pool de thread administratif
        if (this.executorServiceAdministratif != null) {
            try {
                this.executorServiceAdministratif.shutdownNow();
            } catch (Exception e) {
                logger.info("Erreur lors de la fermeture du pool administratif du service " + this, e);
            }
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
     * Ajoute une tâche et son future dans la map permettant au service de gérer ses tâches lancées. Pour être
     * enregistrée les taches doivent être schedulées.
     *
     * @param tache
     * @param fut
     * @return true si la tache a bien été ajouté a la map. Sinon false.
     * @throws NullPointerException : Si la tache envoyé en arguement est nuill ou si la valeure de schedule est null pour cette tache. 
     */
    public boolean addTask(AbstrTacheSchedule tache, Future fut) throws NullPointerException{

        ExceptionTool.argumentNonNull(tache);
        ExceptionTool.argumentNonNull(tache.getSchedule());
        
        if (tache.getSchedule()) {
            synchronized (mapTache) {
                mapTache.put(tache, fut);
                return true;
            }
        }
        return false;
    }

    /***
     * Supprime une tache de la liste des taches gérée par le service. 
     * @param tache 
     */
    
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

//        if (antiDeadBlock != null) {
//            antiDeadBlock.setService(this);
//            this.executorService.submit(antiDeadBlock);
//        }


        if (tacheProducteur != null) {
            logger.debug("Lancement de " + tacheProducteur);
            this.executorServiceAdministratif.submit(tacheProducteur);
        }


        if (tacheConsomateur != null) {
            logger.debug("Lancement de " + tacheConsomateur);
            this.executorServiceAdministratif.submit(tacheConsomateur);
        }


        synchronized (tacheConsomateur.lock) {
            tacheConsomateur.lock.notify();
        }


//        if(tacheProducteur != null){
//            this.executorServiceAdministratif.submit(tacheProducteur);
//        }


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


        // On trouve la tache dans la map
        Entry<AbstrTacheSchedule, Future> entr = null;
        for (Map.Entry<AbstrTacheSchedule, Future> entry : mapTache.entrySet()) {
            AbstrTacheSchedule abstrTacheSchedule = entry.getKey();
            if (abstrTacheSchedule.equals(tache)) {
                entr = entry;
                break;
            }
        }

        if (entr != null) {

            boolean end = annulerTache(entr);

            if (end == false) {
                logger.error("La tache : " + entr.getKey() + " n'a jamais pu être annulé");
            } else {
                if (tache.getSchedule()) {
//                    tache.initTask();
                    System.out.println("Reschedule");
                    tacheProducteur.produire(tache);
//                    schedule(tache);
                }
            }
        }
    }

    /**
     * *
     * Supprime la tache du service. La tache est déterminé comme annulé puis interrompu. Elle n'est plus reschedulée
     *
     * @param entry Une entry tache// future tel qu'on trouve dans la {@link #mapTache}
     * @return true si l'annulation a fonctionné tache interrompu
     */
    public boolean annulerTache(Entry<AbstrTacheSchedule, Future> entry) throws ArgumentIncorrect {

        if (entry == null) {
            throw new NullPointerException("l'entry envoyé est null");
        }

        Future fut = entry.getValue();
        AbstrTacheSchedule tache = entry.getKey();

        if (fut == null) {
            throw new ArgumentIncorrect("La tache envoyé n'a pas de future");
        }
        if (tache == null) {
            throw new ArgumentIncorrect("l'entry envoyé n'a pas de tache !!");
        }

        logger.debug("annulation de " + tache);
        try {
            tache.setAnnuler(true);
            fut.cancel(true);

            int i = 0; // Il faut attendre que la tache ne soit plus en running pour la rescheduler

            boolean end = false;

            while (i < 1000 && !end) {
//                    System.out.println("EXECUTING : " + tache.isRunning());

                if (!tache.isRunning()) {
                    end = true;
                }
                Thread.sleep(100);
                i++;
            }
            if (end) {
                return true;
            }

        } catch (Exception e) {
            logger.error("Erreur lors de l'annulation de la tâche" + tache);
        }
        return false;
    }

    /**
     * *
     * Retourne toute les taches associé au bean envoyé en argument
     *
     * @param bean
     * @return
     */
    protected List< Entry<AbstrTacheSchedule, Future>> retriveAllForBeans(Object bean) {

        List< Entry<AbstrTacheSchedule, Future>> listRetourn = new ArrayList< Entry<AbstrTacheSchedule, Future>>();
        for (Entry<AbstrTacheSchedule, Future> entry : mapTache.entrySet()) {
            AbstrTacheSchedule abstrTacheSchedule = entry.getKey();
            Future future = entry.getValue();

            if (TacheActionableSurUnBean.class.isAssignableFrom(abstrTacheSchedule.getClass())) {
                TacheActionableSurUnBean cast = (TacheActionableSurUnBean) abstrTacheSchedule;
                Object beanCible = cast.returnBeanCible();
                if (beanCible != null && beanCible.getClass().equals(bean.getClass())) {
                    try {
                        boolean retour = BeansUtils.compareBeanFromId(bean, beanCible);
                        if (retour) {
                            listRetourn.add(entry);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        return listRetourn;
    }

    /**
     * *
     * Supprime toutes les taches associé au bean envoyé en argument. Les taches sont annulées puis supprimé de la
     * liste.
     *
     * @param beans
     */
    public void cancelAndRemoveTaskFromAssociedWithBeans(Object beans) {

        if (beans == null) {
            throw new NullPointerException("Le beans est null");
        }

        // On retrouve les tache
        List< Entry<AbstrTacheSchedule, Future>> list = retriveAllForBeans(beans);


        for (int i = 0; i < list.size(); i++) {
            Entry<AbstrTacheSchedule, Future> entry = list.get(i);
            AbstrTacheSchedule tache = entry.getKey();
            Future fut = entry.getValue();

            try {
                // Annulation
                annulerTache(entry);
            } catch (ArgumentIncorrect ex) {
                logger.debug("Impossible d'annuler la tache " , ex);
            }

            // Suppression de la tache de la map.
            remTask(tache);

        }
    }

    public TacheProducteur getTacheProducteur() {
        return tacheProducteur;
    }
}
