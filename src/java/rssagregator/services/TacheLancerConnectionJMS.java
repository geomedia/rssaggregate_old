/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Date;
import java.util.List;
import java.util.Observer;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.incident.JMSPerteConnectionIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.services.crud.AbstrServiceCRUD;
import rssagregator.services.crud.ServiceCRUDFactory;

/**
 * Tâche lancée périodiquement chargé de vérifier l'état de la connection JMS et de relancer celle ci en ca s de besoin.
 * Cette tâche est a l'origine des incident de type {@link JMSPerteConnectionIncident}. La tâche est gérée par le
 * service {@link ServiceSynchro}
 *
 * @author clem
 */
public class TacheLancerConnectionJMS extends TacheImpl<TacheLancerConnectionJMS> implements Incidable {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheLancerConnectionJMS.class);

    /**
     * *
     * Constructeur de la tâche
     *
     * @param s le service devant gérer le retour de la tâche
     */
    public TacheLancerConnectionJMS(Observer s) {
        super(s);
    }

    public TacheLancerConnectionJMS() {
        super();
    }

    @Override
    protected void callCorps() throws Exception {
          ServiceSynchro serviceJMS = ServiceSynchro.getInstance();
        logger.debug("Vérification connectionJMS");
        if (!serviceJMS.statutConnection) {
            serviceJMS.openConnection();
        }
    }



    /**
     * *
     * Lance la tâche. Cette tâche peut être périodique regarder le statut du boolean périodique pour fixer ce
     * comportement.
     *
     * @return true si la connection à pu être initialisé ou si elle était déjà ok. False en cas d'erreur
     * @throws Exception
     */
//    @Override
//    public TacheLancerConnectionJMS call() throws Exception {
//
//        this.exeption = null;
//        ServiceSynchro serviceJMS = ServiceSynchro.getInstance();
//        try {
//            logger.debug("Vérification connectionJMS");
//            if (!serviceJMS.statutConnection) {
//                serviceJMS.openConnection();
//            }
//        } catch (Exception e) {
//            this.exeption = e;
//        } finally {
//            // A la fin de son execution, la tâche se notifie auprès de son ou ses observer
//            this.setChanged();
//            this.notifyObservers();
//            return this;
//        }
//    }

    @Override
    public Class getTypeIncident() {
        return JMSPerteConnectionIncident.class;
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
        EntityManager em = DAOFactory.getInstance().getEntityManager();

        try {
            em.getTransaction().begin();

            // On cherche si il y avait des incidents ouverts de ce type
            DAOIncident<JMSPerteConnectionIncident> dao = (DAOIncident<JMSPerteConnectionIncident>) DAOFactory.getInstance().getDAOFromTask(this);
            dao.setEm(em);

//            List<JMSPerteConnectionIncident> listIncid = dao.findCriteria(JMSPerteConnectionIncident.class);
            List<JMSPerteConnectionIncident> listIncid = dao.findIncidentNonClos(JMSPerteConnectionIncident.class);



            JMSPerteConnectionIncident incid = null;
            if (!listIncid.isEmpty()) {
                incid = listIncid.get(0);
                em.lock(incid, LockModeType.PESSIMISTIC_WRITE);
            }

            if (incid == null) {
                IncidentFactory<JMSPerteConnectionIncident> factory = new IncidentFactory<JMSPerteConnectionIncident>();
                incid = factory.createIncidentFromTask(this, "Perte de connection JMS");

            } else {
                Integer nbr = incid.getNombreTentativeEnEchec();
                nbr++;
                incid.setNombreTentativeEnEchec(nbr);
            }

            // On retrouve le serviceCRUD
            ServiceCRUDFactory facto = ServiceCRUDFactory.getInstance();
            AbstrServiceCRUD service = facto.getServiceFor(JMSPerteConnectionIncident.class);


            // Enregistrement
            if (incid.getID() == null) {
                service.ajouter(incid, em);
            } else {
                service.modifier(incid, em);
            }

        } catch (Exception e) {
            logger.debug("erreur lors de la gestion de l'incident de la tache", e);
            throw e; // On remonte l'exeption, c'est au service d'afficher et de gérer
        } finally { // On commit et ferme l'em
            if (em != null && em.isOpen()) {
                if (em.getTransaction() != null && em.getTransaction().isActive()) {
                    em.getTransaction().commit();
                }
                em.close();
            }
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
            DAOIncident<JMSPerteConnectionIncident> dao = (DAOIncident<JMSPerteConnectionIncident>) DAOFactory.getInstance().getDAOFromTask(this);
            dao.setEm(em);

            List<JMSPerteConnectionIncident> listIncid = dao.findIncidentNonClos(JMSPerteConnectionIncident.class);

            System.out.println("LIST INCIDE ----> " + listIncid.size());
            JMSPerteConnectionIncident incid = null;

            if (!listIncid.isEmpty()) {
                for (int i = 0; i < listIncid.size(); i++) {
                    JMSPerteConnectionIncident jMSPerteConnectionIncident = listIncid.get(i);
                    em.lock(jMSPerteConnectionIncident, LockModeType.PESSIMISTIC_WRITE);
                    jMSPerteConnectionIncident.setDateFin(new Date());
                }
            }

            // Enregistrement
            // On retrouve le serviceCRUD
            ServiceCRUDFactory facto = ServiceCRUDFactory.getInstance();
            AbstrServiceCRUD service = facto.getServiceFor(JMSPerteConnectionIncident.class);


            for (int i = 0; i < listIncid.size(); i++) {
                JMSPerteConnectionIncident jMSPerteConnectionIncident = listIncid.get(i);
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
