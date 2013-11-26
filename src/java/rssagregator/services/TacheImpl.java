/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import org.eclipse.persistence.exceptions.EntityManagerSetupException;
import rssagregator.dao.DAOConf;
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
     * recherché dans la base de données
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
        }
        listRessourcesLocke.add(obj);

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

        if (em != null && em.isJoinedToTransaction()) { // Un commit de base
            try {
                em.getTransaction().commit();
            } catch (Exception e) {
                logger.error("Erreur lors du commir", e);
            }

        }

        if (em != null && em.isOpen()) {
            try {
                em.close();
            } catch (Exception e) {
                logger.error("Erreur à la fermeture de l'EM ", e);
            }

        }

        this.setChanged();
        this.notifyObservers();
        return (T) this;
    }
    
    
    /***
     * Block exécuter lorsqu'une exception survient lors de l'exécution de la tâche. Il n'est le plus souvent pas la peine de le redéclarer dans les classes filles.
     * @param e 
     */
    protected void callCatchException(Exception e){
          this.exeption = e;
          
          if(!DAOFactory.getInstance().getDAOConf().getConfCourante().getProd()){
                    logger.debug("Debug exeption Tache : "+this.getClass().getSimpleName()+"\n "+this, e);
          }
    
          
          if(em != null && em.isOpen()){
              if(em.isJoinedToTransaction()){
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
            if (!annuler) {
                this.exeption = null;
                callCorps();
            }

        } catch (Exception e) {

            callCatchException(e);
          

        } finally {
            return callFinalyse(); // > Appel de callFinalyse
        }
    }
}
