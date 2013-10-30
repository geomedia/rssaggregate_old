/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.TransactionRequiredException;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.Journal;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import rssagregator.dao.DaoItem;
import rssagregator.dao.DaoJournal;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.ServiceSynchro;

/**
 *
 * @author clem
 */
public class ServiceCRUDJournal extends ServiceCRUDBeansSynchro {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceCRUDBeansSynchro.class);

    /**
     * *
     * Lors de la supression d'un journal, On supprimer tous les flux liée au journal ainsi que toutes les items liés au
     * flux
     *
     * @param obj Le journal a supprimer
     * @throws NullPointerException : Si l'objet envoyé est null
     * @throws  ClassCastException : Si l'objet envoyé n'est pas un journal
     * @throws Exception : toute autre exception
     */
    @Override
    public void supprimer(Object obj) throws NullPointerException,ClassCastException, Exception {
        System.out.println("========================");
        System.out.println("--------> SUP JOURNAL");
        System.out.println("======================");
        if (obj == null) {
            throw new NullPointerException("Le journal envoyé en argument est null");
        }
        else if(!obj.getClass().equals(Journal.class)){
            throw new ClassCastException("L'objet envoyé n'est pas un Journal");
        }

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
        } catch (Exception e) {
            logger.error("erreur lors du commit d'une suppression de journal", e);
            journal.setFluxLie(listFlux); // On remet la liste des flux ca évitera des problèmes en cas de changement de politique sur le cache d'entitées
            throw e;
        }
    }
}
