/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.Date;
import java.util.List;
import java.util.Observer;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.incident.ZZOLDJMSPerteConnectionIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.services.ServiceSynchro;
import rssagregator.services.crud.AbstrServiceCRUD;
import rssagregator.services.crud.ServiceCRUDFactory;

/**
 * Tâche lancée périodiquement chargé de vérifier l'état de la connection JMS et de relancer celle ci en ca s de besoin.
 * Cette tâche est a l'origine des incident de type {@link ZZOLDJMSPerteConnectionIncident}. La tâche est gérée par le
 * service {@link ServiceSynchro}
 *
 * @author clem
 */
public class ZZOLDTacheLancerConnectionJMS extends TacheImpl<ZZOLDTacheLancerConnectionJMS> implements Incidable {

//    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ZZOLDTacheLancerConnectionJMS.class);

//    /**
//     * *
//     * Constructeur de la tâche
//     *
//     * @param s le service devant gérer le retour de la tâche
//     */
//    public ZZOLDTacheLancerConnectionJMS(Observer s) {
//        super(s);
//    }

    public ZZOLDTacheLancerConnectionJMS() {
        super();
    }

    @Override
    protected void callCorps() throws Exception {
          ServiceSynchro serviceJMS = ServiceSynchro.getInstance();
        if (!serviceJMS.getStatutConnection()) {
            serviceJMS.openConnection();
        }
    }





    @Override
    public Class getTypeIncident() {
        return ZZOLDJMSPerteConnectionIncident.class;
    }

    /**
     * *
     * Gestion de l'incident de la tâche, cette méthode est déclanché par le service {@link ServiceSynchro}. On va
     * chercher si un incident de connection JMS existe déjà. Si il est trouvé, il sera incrémenté. Si pas d'incident on
     * va le créer
     *
     * @throws Exception
     */
    @Override
    public void gererIncident() throws Exception {

        try {
            
            initialiserTransaction();

            // On cherche si il y avait des incidents ouverts de ce type
            DAOIncident<ZZOLDJMSPerteConnectionIncident> dao = (DAOIncident<ZZOLDJMSPerteConnectionIncident>) DAOFactory.getInstance().getDAOFromTask(this);
            dao.setEm(em);

            List<ZZOLDJMSPerteConnectionIncident> listIncid = dao.findIncidentNonClos(ZZOLDJMSPerteConnectionIncident.class);

            ZZOLDJMSPerteConnectionIncident incid = null;
            if (!listIncid.isEmpty()) {
                incid = listIncid.get(0);
                verrouillerObjectDansLEM(incid, LockModeType.PESSIMISTIC_WRITE);
            }

            if (incid == null) {
                IncidentFactory<ZZOLDJMSPerteConnectionIncident> factory = new IncidentFactory<ZZOLDJMSPerteConnectionIncident>();
                incid = factory.createIncidentFromTask(this, "Perte de connection JMS");

            } else {
                Integer nbr = incid.getNombreTentativeEnEchec();
                nbr++;
                incid.setNombreTentativeEnEchec(nbr);
            }

            // On retrouve le serviceCRUD
            ServiceCRUDFactory factory = ServiceCRUDFactory.getInstance();
            AbstrServiceCRUD serviceCRUD = factory.getServiceFor(ZZOLDJMSPerteConnectionIncident.class);


            // Enregistrement
            if (incid.getID() == null) {
                serviceCRUD.ajouter(incid, em);
            } else {
                serviceCRUD.modifier(incid, em);
            }

        } catch (Exception e) {
            logger.debug("erreur lors de la gestion de l'incident de la tache", e);
            commitTransaction(false);
            throw e; // On remonte l'exeption, c'est au service d'afficher et de gérer
        } finally { // On commit et ferme l'em
            commitTransaction(true);
        }
    }

    /**
     * *
     * Après le succès de la connection JMS, le Service {@link ServiceSynchro} lance cette méthode afin de fermer les
     * erreurs si il y en avait auraravant. La méthode va donc chercher la précédente erreur et ajouter une date de
     * cloture
     *
     * @throws Exception
     */
    @Override
    public void fermetureIncident() throws Exception {
        EntityManager em = DAOFactory.getInstance().getEntityManager();
        try {

            em.getTransaction().begin();

            // On cherche si il y avait des incidents ouverts de ce type
            DAOIncident<ZZOLDJMSPerteConnectionIncident> dao = (DAOIncident<ZZOLDJMSPerteConnectionIncident>) DAOFactory.getInstance().getDAOFromTask(this);
            dao.setEm(em);

            List<ZZOLDJMSPerteConnectionIncident> listIncid = dao.findIncidentNonClos(ZZOLDJMSPerteConnectionIncident.class);

            ZZOLDJMSPerteConnectionIncident incid = null;

            if (!listIncid.isEmpty()) {
                for (int i = 0; i < listIncid.size(); i++) {
                    ZZOLDJMSPerteConnectionIncident jMSPerteConnectionIncident = listIncid.get(i);
                    em.lock(jMSPerteConnectionIncident, LockModeType.PESSIMISTIC_WRITE);
                    jMSPerteConnectionIncident.setDateFin(new Date());
                }
            }

            // Enregistrement
            // On retrouve le serviceCRUD
            ServiceCRUDFactory facto = ServiceCRUDFactory.getInstance();
            AbstrServiceCRUD service = facto.getServiceFor(ZZOLDJMSPerteConnectionIncident.class);


            for (int i = 0; i < listIncid.size(); i++) {
                ZZOLDJMSPerteConnectionIncident jMSPerteConnectionIncident = listIncid.get(i);
//            em.merge(jMSPerteConnectionIncident);
                service.modifier(jMSPerteConnectionIncident, em);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
        } finally {
            if (em != null && em.isOpen()) {
                if (em.getTransaction() != null && em.getTransaction().isActive()) {
                    em.getTransaction().commit();
                }
                em.close();
            }
        }
    }
}
