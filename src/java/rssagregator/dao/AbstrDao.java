/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import org.apache.poi.util.Beta;
import rssagregator.beans.BeanSynchronise;
import rssagregator.services.ServiceSynchro;

/**
 * Toutes les DAO doivent hériter de cette class abstraite. Elle définit les actions de base (modifier, créer, find...)
 * pouvant être redéfinit dans les DAO spécialisée pour la gestion d'une entitée particulière.
 *
 * @author clem
 */
public abstract class AbstrDao {

    protected EntityManager em;
    protected EntityManagerFactory emf;
    /**
     * *
     * La persistence Unit définie dans la config d'Eclipse link. Voir le fichier persistence.xml
     */
    protected String PERSISTENCE_UNIT_NAME = "RSSAgregatePU2";
    /**
     * *
     * Instance de la daofactory.
     */
    protected DAOFactory dAOFactory;
    /**
     * *
     * Cette variable peut être utilisée par certaine dao exemple la daoGenerique pour savoir sur quel type d'entité
     * elle doit agir. Cette variable va peut être être supprimée au profit de la généricité pour une implémentation
     * plus standart
     */
    @Beta
    protected Class classAssocie;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AbstrDao.class);

    /**
     * *
     * Permet de créer l'entité envoyé en argument. Si le beans est un {@link BeanSynchronise}, la DAO va chercher à
     * diffuser la création. En cas d'échec de diffusion elle va rollbacker la création afin de ne pas avoir d'entitée
     * crée sur le serveur maître et absente sur les serveurs esclaves.
     *
     * @param obj Le beans devant être persisté
     * @throws Exception
     */
    public void creer(Object obj) throws Exception {
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.persist(obj);
        try {
            if (BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
                ServiceSynchro.getInstance().diffuser(obj, "add");
            }
            tr.commit();
        } catch (Exception e) {
            logger.error("Echec de la suppression du beans : " + e);
            tr.rollback();
            throw e;
        }
    }

    /**
     * *
     * Permet la mofification de l'entitée envoyée en argument. Si le beans est un {@link BeanSynchronise}, la DAO va
     * chercher à diffuser la création. En cas d'échec de diffusion elle va rollbacker la création afin de ne pas avoir
     * d'entitée crée sur le serveur maître et absente sur les serveurs esclaves.
     *
     * @param obj
     * @throws Exception
     */
    public void modifier(Object obj) throws Exception {
        // Test si le flux possède bien un id
        // On récupère l'id
        Method getter = obj.getClass().getMethod("getID");
        Object retour = getter.invoke(obj);
        if (retour != null && retour instanceof Long && (Long) retour >= 0) {
            EntityTransaction tr = em.getTransaction();
            tr.begin();
            em.merge(obj);
            try {
                // Si il s'agit d'un beans devant être synchronisé On lance la diff
                if (BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
                    ServiceSynchro.getInstance().diffuser(obj, "mod");
                }
                tr.commit();
                //En cas d'échec de la synchronisation, on rollback la modification.
            } catch (Exception e) {
                logger.error("erreur lors de la modification d'un beans : " + e +"\n trace : " + e.getStackTrace());
                tr.rollback();
                throw e;
            }
        }
    }

    /**
     * *
     * Retrouver un objet à patir de son id.
     *
     * @param id
     * @return
     */
    public Object find(Long id) {
        System.out.println("FIND ABSTR");
        Class laclass = this.getClassAssocie();
        try {
            Object resu = em.find(laclass, id);
            return resu;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * *
     * Supprimer l'objet envoyé en argument. Cette action utilise le même principe de diffusion de la modification auprès des serveurs esclaves que créer() et modifier().
     *
     * @param obj le bean à supprimé de la base de données
     */
    public void remove(Object obj) throws Exception {
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.remove(em.merge(obj));
        try {
            if (BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
                ServiceSynchro.getInstance().diffuser(obj, "rem");
            }
            tr.commit();
        } catch (Exception e) {

            tr.rollback();
            logger.error("Erreur lors de la suppression du beans : " + e);
        }
    }

    /***
     * Retourne tous les enregistrement pour le type définit par la variable classAssocie de la dao.
     * @return Une liste d'objet ou null si échec de la requête
     */
    public List<Object> findall() {
        try {
            Class classasso = this.getClassAssocie();
            String req = "SELECT f FROM " + classasso.getSimpleName() + " f";
            Query query = em.createQuery(req);
//            query.setHint("eclipselink.cache-usage", "CheckCacheOnly");
            List<Object> result = query.getResultList();
            return result;
        } catch (SecurityException ex) {
            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
            logger.error("erreur lors de l'execution de la methode findAll : " + ex);
        } catch (IllegalArgumentException ex) {
            logger.error("erreur lors de l'execution de la methode findAll : " + ex);
            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }

    public AbstrDao() {
    }

    public Class getClassAssocie() {
        return classAssocie;
    }

    public void setClassAssocie(Class classAssocie) {
        this.classAssocie = classAssocie;
    }

    public DAOFactory getdAOFactory() {
        return dAOFactory;
    }

    public void setdAOFactory(DAOFactory dAOFactory) {
        this.dAOFactory = dAOFactory;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
    

}
