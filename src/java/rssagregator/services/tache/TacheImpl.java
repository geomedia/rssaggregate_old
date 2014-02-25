/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.LockModeType;
import org.apache.log4j.Priority;
import org.eclipse.persistence.exceptions.EntityManagerSetupException;
import rssagregator.beans.incident.Incidable;
import rssagregator.dao.DAOFactory;
import rssagregator.services.SemaphoreCentre;
import rssagregator.utils.ThreadUtils;

/**
 *
 * @author clem
 * @version 0.1
 *
 */
public class TacheImpl<T> extends AbstrTache<T> {

//    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheImpl.class);
    public TacheImpl() {
    }

//    public TacheImpl(Observer s) {
//        super(s);
//    }
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


        obj = em.find(obj.getClass(), retour);

        em.lock(obj, lockMode);
        em.refresh(obj);
    }

    /**
     * *
     * Initialise l'em de la tache utilisé par les méthode {@link #callCorps() () } ou de gestion de l'incident. Si une
     * transaction était déjà présente, elle est roolbacké pour en ouvrir une nouvelle.
     */
    public void initialiserTransaction() throws InterruptedException {

        // On commence par vérifier que la transaction est bien close
        if (em != null) {
            if (em.isJoinedToTransaction()) {

                // On attend quelque seconde et on roolback
                int i = 0;
                while (i < 10) {
                    if (!em.getTransaction().isActive()) { // Si la transaction n'est plus active on quite
                        break;
                    }
//                    try {
                    Thread.sleep(100);
//                    } catch (Exception e) {
//                    }
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

    @Override
    protected void callCorps() throws InterruptedException, Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * *
     * Le bloc exécuté a la fin de l'appel du traitement de la tache ({@link TacheImpl#executeProcessus()} qui déclanche
     * successivement {@link TacheImpl#callCorps() },  {@link TacheImpl#callCatchException(java.lang.Exception) }, {@link TacheImpl#callFinalyse()
     * }. Il faut notifier le service. Libérer les ressources, Retourner la tache elle meme. Commit la transaction. En
     * cas d'annulation la transaction est roolbacké Il peut être redéclarée.
     *
     * @return
     */
    @Override
    protected T callFinalyse() {
//        this.nbrTentative++;

        if (annuler) { // Si La tache est annulé on roolback la transaction
            try {
                commitTransaction(false);
            } catch (Exception ex) {
                logger.error("Erreur lors du rollback dans call Finalyse tache : " + this, ex);
            }
        } else { // Sinon, on la commit
            try {
                commitTransaction(true);
            } catch (Exception ex) {
                logger.debug("Erreur lors du commit dans callfinalyse tache : " + this, ex);
            }
        }
        
        // On ferme l'em 
        if(em != null && em.isOpen()){
            try {
            em.close();
            em = null;
            } catch (Exception e) {
                logger.debug("Erreur lros dela fermeture de l'em");
            }

        }
        

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
        // En fonction du niveau de log on affiche ou non la trace. Certaine erreur sont récurante (Level info) , on n'affiche alors pas la trace. Si le debug est en error (envoie de mail par log 4j) on affiche la trace

        if (org.apache.log4j.Level.toLevel(logErrorLevel).isGreaterOrEqual(Priority.ERROR)) {
            logger.log(Priority.toPriority(logErrorLevel), "erreur sur la tache " + this + e, e);
        } else {
            logger.log(Priority.toPriority(logErrorLevel), "erreur sur la tache " + this + e, e);
        }

        try { // On roolback la transaction
            commitTransaction(false);
        } catch (Exception ex) {
            logger.error("RoolbackException", exeption);
        }
    }

    @Override
    public T executeProcessus() throws InterruptedException {
        nbrTentative++;
        this.exeption = null;// A l'execution on nullify l'exeption

        try {
            if (!DAOFactory.getInstance().getDAOConf().getConfCourante().getProd()) { // Si ce n'est pas en prod on affiche un débug pour toutes les exécution
                logger.debug("Execute " + this);
            }

            if (!annuler) {
                callCorps();
            } else {
                throw new InterruptedException(); // Si la tache est annulé on emmet une interrupt exeption
            }

        } catch (InterruptedException e) { // Pour une interruption on ne déclanche pas le traitement classique de l'erreur mais on remonte l'erreur qui doit aller directement dans le catch du block call
            logger.debug("Interruption de " + this);
            throw e;
        } catch (Exception e) { // Si c'est une autre exception on lance la fonction de capture (qui peut être redéclarée dans les sous classe
            callCatchException(e);

        } finally {
            return callFinalyse(); // > Appel de callFinalyse qui effectue les commit libère les ressources etc.
        }
    }

    @Override
    public synchronized T call() throws Exception {
        initLancementTache();

        try {
            T resu = null;
            boolean continuer = true;

            //------------------------------------------------------
            //          Execution du Traitement
            //------------------------------------------------------
            //On execute le processus de traitement autant juqu'a ce qu'on répasse le nombre max ou que la tache réussisse
            while (nbrTentative < nbMaxReExecution && continuer && !annuler) {
                resu = executeProcessus();
                if (this.exeption == null) { // Si la tache s'est executé correctement, on sort de la boucle, pas de réexecution
                    continuer = false;
                } else { // Sinon on sleep un peu et on réexecute le block
                    try {
                        Thread.sleep(1000 * nbSleepError);
                    } catch (InterruptedException e) { // Si pendant le sleep annulation on remonte
                        throw e;
                    }
                }
            }

            //-----------------------------------------------------
            //              Gestion des incidents
            //-----------------------------------------------------

            if (Incidable.class.isAssignableFrom(this.getClass())) { // Si la tache est incidable
                Incidable incidableTask = (Incidable) this;
                if (this.exeption == null) { // Si la tache s'est déroulée correctement on ferme les possibles incidents ouverts
                    incidableTask.fermetureIncident();
                } else { // Sinon si il y a eu des incidents. On ouvre ou incrémente l'incident courent grace a la méthode gererIncident
                    incidableTask.gererIncident();
                }
            }
            ThreadUtils.interruptCheck();

        } catch (InterruptedException e) {
            logger.debug("Interruption de " + this);
            throw e;
        } catch (Exception e) {
            logger.error("Exception annormale tache " + this, e);
            throw e;
        } finally {
            // On rollback la transaction si une transaction est encore ouverte
            finTache(); // Running = false et completion de la date de prochaine execution ; fermeture de l'em; libération de la semaphore; notification des observer
            
            // Notification de l'object qui permet de réveiller d'autres thread
//            synchronized (this.waitFinish) {
//                this.waitFinish.notifyAll();
                this.notifyAll(); // Des taches threead peuvent s'être mise en attente. Il faut les notifier.
//            }

            return (T) this;
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
