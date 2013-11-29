/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observer;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import org.eclipse.persistence.exceptions.EntityManagerSetupException;
import rssagregator.dao.DAOFactory;

/**
 *
 * @author clem
 * @version 0.1
 *
 */
public class TacheImpl<T> extends AbstrTacheSchedule<T> {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheImpl.class);

    public TacheImpl() {
    }

    public TacheImpl(Observer s) {
        super(s);
    }
    List<Object> listRessourcesLocke = new ArrayList<Object>();
    /**
     * *
     * De noubreuse tache utilise un entitymanager et gère une transaction
     */
    EntityManager em = null;

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


        if (!em.contains(obj)) {
            Object objFind = em.find(obj.getClass(), retour);
            em.lock(objFind, lockMode);
        } else {
            em.lock(obj, lockMode);
            em.refresh(obj); // Il faut s'assurer que la tache possède bien la dernière version de l'objet
        }
        listRessourcesLocke.add(obj);

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

    @Override
    protected void callCorps() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * *
     * Le bloc exécuté a la fin de l'appel de la tache. Il faut notifier le service. Libérer les ressources, Retourner
     * la tache elle meme. Il peut être redéclarée.
     *
     * @return
     */
    @Override
    protected T callFinalyse() {
        this.nbrTentative++;


        try {
            commitTransaction(true);
        } catch (Exception ex) {
            logger.debug("Erreur lors du commit");
        }


        this.setChanged();
        this.notifyObservers();
        running = false;
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


        if (!DAOFactory.getInstance().getDAOConf().getConfCourante().getProd()) {
            logger.debug("Debug exeption Tache : " + this.getClass().getSimpleName() + "\n " + this, e);
        }


        if (em != null && em.isOpen()) {
            if (em.isJoinedToTransaction()) {
                try {
                    em.getTransaction().rollback();
                } catch (Exception ex) {
                    logger.error("Erreur lors du RollBack", ex);
                }
            }
        }
    }

    @Override
    public synchronized T call() throws Exception {

        try {
            running = true;

            if (!DAOFactory.getInstance().getDAOConf().getConfCourante().getProd()) { // Si ce n'est pas en prod on affiche un débug pour toutes les exécution
                logger.debug("Execute " + this);
            }

            if (!annuler) {
                lasExecution = new Date();
                this.exeption = null;
                callCorps();
            }

        } catch (Exception e) {

            callCatchException(e);


        } finally {
            return callFinalyse(); // > Appel de callFinalyse
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
