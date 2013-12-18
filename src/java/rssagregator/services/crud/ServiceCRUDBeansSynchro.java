/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import java.util.List;
import javax.persistence.EntityManager;
import rssagregator.beans.BeanSynchronise;
import rssagregator.beans.exception.ArgumentIncorrect;
import rssagregator.dao.DAOFactory;
import rssagregator.services.ServiceSynchro;
import rssagregator.utils.ExceptionTool;
import rssagregator.utils.TransactionTool;

/**
 * Un certain nombre de beans doivent être synchronisé. Exemple les journaux. Les utilisateur etc. Le traitement des
 * action de CRUD est ainsi différent
 *
 * @author clem
 */
public class ServiceCRUDBeansSynchro extends AbstrServiceCRUD {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceCRUDBeansSynchro.class);

    protected ServiceCRUDBeansSynchro() {
    }

    /**
     * *
     * Ajoute le beans à la base de donnée. Si l'ajout s'est bien réalisé diffusion par le biais du service de synchro.
     * SI la diffusion est OK, alors on applique le commit
     *
     * @param obj
     * @throws Exception
     */
    @Override
    public void ajouter(Object obj) throws Exception {
        ExceptionTool.argumentNonNull(obj);
        checkSynchronisableBeans(obj);

        EntityManager em = DAOFactory.getInstance().getEntityManager();
        em.getTransaction().begin();

        boolean err = false;
        try {
            ajouter(obj, em);
        } catch (Exception e) {
            err = true;
            logger.error("erreur lors de l'ajout", e);
            throw e;
        } finally {
            if (err) {
                // TODO : voire la notion de transaction et commit pour JMS au moment de retaper la synchro
                TransactionTool.commitRollBackIfPossible(em, false); // Si erreur
            } else {
                TransactionTool.commitRollBackIfPossible(em, true); // Si ok on commit
            }
        }
    }

    /**
     * *
     * Ajoute mais ne comit pas
     *
     * @param obj
     * @param em
     * @throws Exception
     */
    @Override
    public void ajouter(Object obj, EntityManager em) throws Exception {
        ExceptionTool.argumentNonNull(obj);
        TransactionTool.checkEmTransaction(em);
        boolean err = false;
        try {
            em.persist(obj);
            ServiceSynchro.getInstance().diffuser(obj, "add");
        } catch (Exception e) {
            err = true;
            throw e;
        }

    }

    /**
     * *
     * Modifie le beans envoyé en argument. Le beans sera comité dans la base de donnée uniquement si la diffusion a été
     * possible auprès du {@link ServiceSynchro}
     *
     * @param obj
     * @throws Exception
     */
    @Override
    public void modifier(Object obj) throws Exception {
        ExceptionTool.argumentNonNull(obj);
        EntityManager em = DAOFactory.getInstance().getEntityManager();
        em.getTransaction().begin();


        boolean err = false;
        try {
            modifier(obj, em);
        } catch (Exception e) {
            err = true;
            logger.error("Erreur lors de la modification", e);
            throw e;
        } finally {
            if (err) {
                TransactionTool.commitRollBackIfPossible(em, false);
            } else {
                TransactionTool.commitRollBackIfPossible(em, true);
            }
//              em.getTransaction().commit();
        }
    }

    @Override
    public void modifier(Object obj, EntityManager em) throws Exception {
        ExceptionTool.argumentNonNull(obj);
        TransactionTool.checkEmTransaction(em);
        checkSynchronisableBeans(obj);

        try {
            em.merge(obj);
            ServiceSynchro.getInstance().diffuser(obj, "mod");
        } catch (Exception e) {
            logger.error("erreur lors de la modification", e);
            throw e;
        }
    }

    @Override
    public void supprimer(Object obj) throws Exception {
        ExceptionTool.argumentNonNull(obj);
        checkSynchronisableBeans(obj);


        EntityManager em = DAOFactory.getInstance().getEntityManager();
        em.getTransaction().begin();

        boolean err = false;
        try {
            supprimer(obj, em);
        } catch (Exception e) {
            err = true;
            logger.error("Erreur lors de la suppression", e);
            throw e;
        } finally {
            if (err) {
                TransactionTool.commitRollBackIfPossible(em, false);
            } else {
                TransactionTool.commitRollBackIfPossible(em, true);
            }
        }


    }

    /**
     * *
     * Utilise l'em envoyé en argument pour supprimer l(objet et demande au service de Synchronisation de répercuter la
     * suppression. LA transaction n'est pas commité, c'est a l'appelant de le faire.
     *
     * @param obj object qu'il faut supprimer
     * @param em EntityManager a utiliser pou reffectuer la transaction
     * @throws Exception
     */
    @Override
    public void supprimer(Object obj, EntityManager em) throws Exception {
        ExceptionTool.argumentNonNull(obj);
        TransactionTool.checkEmTransaction(em);
        checkSynchronisableBeans(obj);

        try {
            
            em.remove(em.merge(obj));
            ServiceSynchro.getInstance().diffuser(obj, "rem");
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression", e);
            throw e;

        }
    }

    /**
     * *
     * Si le beans n'est pas un bean synchronisable, lève une exception
     *
     * @param obj
     * @throws ArgumentIncorrect
     */
    protected void checkSynchronisableBeans(Object obj) throws ArgumentIncorrect {
        if (!BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
            throw new ArgumentIncorrect("Ce beans n'est pas synchronisable");
        }
    }

    @Override
    public void supprimerList(List objs) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void supprimerList(List<Object> objs, EntityManager em) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
