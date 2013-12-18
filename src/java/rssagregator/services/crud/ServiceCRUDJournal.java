/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import rssagregator.beans.Flux;
import rssagregator.beans.Journal;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import rssagregator.dao.DaoJournal;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.ServiceSynchro;
import rssagregator.utils.ExceptionTool;

/**
 * Le service Crud Journal permet de gérer la création modification et suppression de journaux. Les journaux sont
 * enregistré dans la base de données diffuser par le biais du {@link ServiceSynchro} et enregistr auprès du
 * {@link ServiceCollecteur}
 *
 * @author clem
 */
public class ServiceCRUDJournal extends ServiceCRUDBeansSynchro {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceCRUDBeansSynchro.class);

    /**
     * *
     * Ajoute le journal, en diffusant l'ajout auprès du service de synchro puis enregistre le journal auprès du service
     * de collecte. La transaction est crée démarrée et close.
     *
     * @param obj
     * @throws Exception
     */
    @Override
    public void ajouter(Object obj) throws Exception {
        super.ajouter(obj); //To change body of generated methods, choose Tools | Templates.

        Journal cast = (Journal) obj;
        ServiceCollecteur.getInstance().enregistrerJournalAupresduService(cast);

    }

    /**
     * *
     * Ajoute le journal, en diffusant l'ajout auprès du service de synchro puis enregistre le journal auprès du service
     * de collecte. La transaction doit être founie en argument. Les modifications ne sont pas commité. C'est a
     * l'appelant de lancer le commit
     *
     * @param obj
     * @param em
     * @throws Exception
     */
    @Override
    public void ajouter(Object obj, EntityManager em) throws Exception {
        super.ajouter(obj, em); //To change body of generated methods, choose Tools | Templates.

        Journal cast = (Journal) obj;
        ServiceCollecteur.getInstance().enregistrerJournalAupresduService(cast);

    }

    @Override
    public void modifier(Object obj) throws Exception {
        super.modifier(obj); //To change body of generated methods, choose Tools | Templates.

        Journal cast = (Journal) obj;
        ServiceCollecteur.getInstance().enregistrerJournalAupresduService(cast);

    }

    @Override
    public void modifier(Object obj, EntityManager em) throws Exception {
        super.modifier(obj, em); //To change body of generated methods, choose Tools | Templates.

        Journal cast = (Journal) obj;
        ServiceCollecteur.getInstance().enregistrerJournalAupresduService(cast);

    }

    /**
     * *
     * Lors de la supression d'un journal, On supprimer tous les flux liée au journal ainsi que toutes les items liés au
     * flux
     *
     * @param obj Le journal a supprimer
     * @throws NullPointerException : Si l'objet envoyé est null
     * @throws ClassCastException : Si l'objet envoyé n'est pas un journal
     * @throws Exception : toute autre exception
     */
    @Override
    public void supprimer(Object obj) throws NullPointerException, ClassCastException, Exception {

        ExceptionTool.argumentNonNull(obj);
        ExceptionTool.checkClass(obj, Journal.class);

        logger.info("Suppression du journal " + obj);


        Journal journal = (Journal) obj;
        DaoJournal daoJournal = DAOFactory.getInstance().getDaoJournal();
        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
        ServiceCRUDFlux serviceCRUDFlux = (ServiceCRUDFlux) ServiceCRUDFactory.getInstance().getServiceFor(Flux.class);

        // On commence par retrouver tous les flux liés au journal.
        List<Flux> listFlux = daoFlux.findFluxParJournaux(journal);

        daoJournal.beginTransaction();
        try {
            serviceCRUDFlux.SupprimerListFlux(listFlux, false, daoJournal.getEm());
        } catch (Exception e) {
            logger.error("erreur lors de l'utilisation du service flux : ", e);
            daoJournal.roolbackTransaction(); // On roolback la transaction
            throw e; // On remonte l'exeption. La servlet pourra afficher ce qu'il se doit
        }

        // Suppression du journal et commit de la transaction

        try {
            journal.setFluxLie(new ArrayList<Flux>()); // Les flux sont déjà supprimé en cas de suppression, l'ORM tente de merger toutes les entités liées au journal. Cela pose problème. On retire donc la liste des flux du journal pour sa suppression
            daoJournal.remove(journal);
            ServiceSynchro.getInstance().diffuser(journal, "rem"); // On diffuse l'action auprès de la synch. Si erreur le commit n'aura pas lieu
            daoJournal.commit();
            // Il faut aussi supprimer les flux du service de collecte.
            List<Flux> fluxs = journal.getFluxLie();
            for (int i = 0; i < fluxs.size(); i++) {
                Flux flux = fluxs.get(i);
                ServiceCollecteur.getInstance().retirerFluxDuService(flux);
            }


        } catch (Exception e) {
            logger.error("erreur lors du commit d'une suppression de journal", e);
            journal.setFluxLie(listFlux); // On remet la liste des flux ca évitera des problèmes en cas de changement de politique sur le cache d'entitées
            throw e;
        }
    }
}
