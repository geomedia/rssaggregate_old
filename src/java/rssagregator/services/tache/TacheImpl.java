/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.LockModeType;
import org.apache.log4j.Priority;
import org.eclipse.persistence.exceptions.EntityManagerSetupException;
import rssagregator.beans.incident.Incidable;
import rssagregator.dao.DAOFactory;
import rssagregator.utils.ThreadUtils;

/**
 *
 * @author clem
 * @version 0.1
 *
 */
public class TacheImpl<T> extends AbstrTacheSchedule<T> {

//    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheImpl.class);
    public TacheImpl() {
    }

    public TacheImpl(Observer s) {
        super(s);
    }

    /**
     * *
     * Permet de blocker une ressources dans l'em propre à la Tâche. Si l'objet n'est pas contenu dans l'em il sera
     * recherché dans la base de données. Si l'objet est déjà dans l'em on le refresh pour s'assurer de bien avoir les
     * dernières données.
     *
     * @param obj
     * @param lockMode
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public void verrouillerObjectDansLEM(Object obj, LockModeType lockMode) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        if (obj == null) {
            throw new NullPointerException("impossible de locker une ressources null");
        }

        if (em == null) {
            throw new EntityManagerSetupException();
        }


        Method getter = obj.getClass().getMethod("getID");
        Object retour = getter.invoke(obj);


//        if (!em.contains(obj)) {
////            Object objFind = em.find(obj.getClass(), retour);
//            obj = em.find(obj.getClass(), retour);
//            em.lock(obj, lockMode);
//          
//        } else {
//            em.lock(obj, lockMode);
////            em.refresh(obj); // Il faut s'assurer que la tache possède bien la dernière version de l'objet
//        }
        obj = em.find(obj.getClass(), retour, lockMode);
        em.lock(obj, lockMode);
        em.refresh(obj);
//        listRessourcesLocke.add(obj);

    }

    /**
     * *
     * Initialise l'em de la tache utilisé par les méthode {@link #callCorps() () } ou de gestion de l'incident. Si une
     * transaction était déjà présente, elle est roolbacké pour en ouvrir une nouvelle.
     */
    public void initialiserTransaction() throws InterruptedException {

        // On commence par vérifier que la transaction est bien close
        if (em != null) {
//            logger.debug("Il y avait un EM !!");
            if (em.isJoinedToTransaction()) {

                // On attend quelque seconde et on roolback

                int i = 0;
                while (i < 10) {
                    if (!em.getTransaction().isActive()) {
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                    }
                    i++;
                }

                if (em.isJoinedToTransaction()) {
                    try {
                        em.getTransaction().rollback();
                    } catch (Exception e) {
                        logger.error("erreur lors du roolback visant a détruire une transaction la transaction précédante");
                    }
                }
            }

            if (!em.isOpen()) { // Si l'em a été fermé on en ouvre un nouveau
                em = DAOFactory.getInstance().getEntityManager();
            }
        } else {
            em = DAOFactory.getInstance().getEntityManager();
        }
        // démarrage d'une nouvelle transaction
        em.getTransaction().begin();
    }

    /**
     * *
     * Le corps du traitement de la tâche. Doit être redéclaré dans chaque tâche
     *
     * @throws InterruptedException
     * @throws Exception
     */
    @Override
    protected void callCorps() throws InterruptedException, Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * *
     * Le bloc exécuté a la fin de l'appel de la tache. Il faut notifier le service. Libérer les ressources, Retourner
     * la tache elle meme. Commit la transaction. En cas d'annulation la transaction est roolbacké Il peut être
     * redéclarée.
     *
     * @return
     */
    @Override
    protected T callFinalyse() {
//        this.nbrTentative++;

        if (annuler) {
            try {
                commitTransaction(false);
            } catch (Exception ex) {
                Logger.getLogger(TacheImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                commitTransaction(true);
            } catch (Exception ex) {
                logger.debug("Erreur lors du commit", ex);
            }
        }

//        this.setChanged();
//        this.notifyObservers();
//        running = false;
        return (T) this;
    }

    /**
     * *
     * Block exécuter lorsqu'une exception survient lors de l'exécution de la tâche. Il n'est le plus souvent pas la
     * peine de le redéclarer dans les classes filles.
     *
     * @param e
     */
    protected void callCatchException(Exception e) {

        this.exeption = e;

        // Si c'est une interruption on met le booleean d'annulation de la tache
        if (e.getClass().equals(InterruptedException.class)) {
            this.annuler = true;
        }


//        if (!DAOFactory.getInstance().getDAOConf().getConfCourante().getProd()) {
//            logger.debug("Debug exeption Tache : " + this.getClass().getSimpleName() + "\n " + this, e);
//        }


        // En fonction du niveau de log on affiche ou non la trace. Certaine erreur sont récurante (Level info) , on n'affiche alors pas la trace. Si le debug est en error (envoie de mail par log 4j) on affiche la trace

//        LevelRangeFilter filter = new LevelRangeFilter();
//        filter.setLevelMin(org.apache.log4j.Level.ERROR);
//        filter.setLevelMax(org.apache.log4j.Level.FATAL);


        if (org.apache.log4j.Level.toLevel(logErrorLevel).isGreaterOrEqual(Priority.ERROR)) {
            logger.log(Priority.toPriority(logErrorLevel), "erreur sur la tache " + this + e, e);
        } else {
            logger.log(Priority.toPriority(logErrorLevel), "erreur sur la tache " + this + e);
        }

//        if (logger.getLevel().isGreaterOrEqual(Priority.ERROR)) {
// 
//        }
//        else{
//   
//        }
        try {
            //        if(logErrorLevel == org.apache.log4j.Level.INFO_INT){
            ////            logger.setLevel();
            //        }


            commitTransaction(false);


            //        if (em != null && em.isOpen()) {
            //            if (em.isJoinedToTransaction()) {
            //                try {
            //                    em.getTransaction().rollback();
            //                } catch (Exception ex) {
            //                    logger.error("Erreur lors du RollBack", ex);
            //                }
            //            }
            //        }
        } catch (Exception ex) {
            logger.error("RoolbackException", exeption);
//            Logger.getLogger(TacheImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * *
     * Méthode contenant l'ensemble de la procédure de traitement de la tâche. Elle est déclanché par {@link #call() }.
     * En réexécutant cette méthode il est possible de relancer la tache en la laissant toujorus dans la même thread.
     * (voir dans les services).
     *
     * @return
     */
    @Override
    public T executeProcessus() throws InterruptedException {
        nbrTentative++;
        try {
            if (!DAOFactory.getInstance().getDAOConf().getConfCourante().getProd()) { // Si ce n'est pas en prod on affiche un débug pour toutes les exécution
                logger.debug("Execute " + this);
            }

            if (!annuler) {
                callCorps();
            }

        } catch (InterruptedException e) { // Pour une interruption on ne déclanche pas le traitement classique de l'erreur
            logger.debug("Interruption");
            this.setAnnuler(true);
            throw e;
        } catch (Exception e) { // Si c'est une autre exception on lance la fonction de capture
            callCatchException(e);

        } finally {
            return callFinalyse(); // > Appel de callFinalyse qui effectue les commit libère les ressources etc.
        }
    }

    @Override
    public synchronized T call() throws Exception {
//        running = true; //
//        lasExecution = new Date();
//        nbrTentative = 0;
//        this.exeption = null; //
//        this.annuler = false; //
//        listRessourcesLocke = new ArrayList<Object>();


        initLancementTache();



        try {

            T resu = null;
            boolean continuer = true;
            //On execute le processus de traitement autant juqu'a ce qu'on répasse le nombre max ou que la tache réussisse
            while (nbrTentative < nbMaxReExecution && continuer && !annuler) {
                resu = executeProcessus();
                if (this.exeption == null) { // Si la tache s'est executé correctement, on sort de la boucle, pas de réexecution
                    continuer = false;
                } else {
                    try {
                        Thread.sleep(1000 * nbSleepError);
                    } catch (InterruptedException e) { // Si pendant le sleep annulation on remonte
                        annuler = true;
                        throw e;
                    }
                }
            }

            // Gestion des exeception de la tache si c'est incidable
            if (Incidable.class.isAssignableFrom(this.getClass())) {

                Incidable incidableTask = (Incidable) this;
                if (this.exeption == null) {
                    incidableTask.fermetureIncident();
                } else {
                    incidableTask.gererIncident();
                }
            }

            ThreadUtils.interruptCheck(); // 

            logger.debug("Notification");
            this.setChanged();   //On se notifi au service qui va rescheduler
            this.notifyObservers();
            return resu;
        } catch (InterruptedException e) {
            logger.debug("Interruption");
            annuler = true;
            throw e;
        } catch (Exception e) {
            logger.error("Exception annormale tache " + this, e);
            throw e;
        } finally {

            // On rollback la transaction si une transaction est encore ouverte
            try {
                commitTransaction(false); 
            } catch (Exception e) {
                logger.error("Erreur lors d'un roolback de " + this, e);
            }

            finTache(); // Running = false et completion de la date de prochaine execution ; fermeture de l'em
            logger.debug("END " + this);
        }
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

    /**
     * *
     * La méthode finalyse permet de s'assurer de la fermeture de l'em a la disparition d'une tache. Si une transaction
     * est en cours, elle est roolbacké.
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize(); //To change body of generated methods, choose Tools | Templates.

        if (em != null && em.isOpen()) {

            if (em.isJoinedToTransaction()) {
                commitTransaction(false);
            }
            em.close(); // On ferme l'em a la destructruction de la tache
        }
    }
}
