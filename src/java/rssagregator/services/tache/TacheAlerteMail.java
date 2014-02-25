/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import org.apache.velocity.VelocityContext;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.services.ServiceMailNotifier;
import rssagregator.services.crud.AbstrServiceCRUD;
import rssagregator.services.crud.ServiceCRUDFactory;
import rssagregator.services.mailtemplate.VelocityTemplateLoad;

/**
 * Cette tache a pour role de collecter toutes les 30 minutes les incidents nouveaux et d'envoyer un mail aux
 * administrateur. Cette notification d'urgence ne sera pas réitéré par la suite.
 *
 * ELLE N'A pas d'incident associé. Si le mail ne part pas il faut gérer l'incident lié à la tache
 * {@link TacheEnvoyerMail}
 *
 * @author clem
 */
public class TacheAlerteMail extends TacheImpl<TacheAlerteMail> {

//    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheAlerteMail.class);
    /**
     * *
     * Les incidents relevés par la tâche et devant être notifié par mail.
     */
    private List<AbstrIncident> incidents;

    public List<AbstrIncident> getIncidents() {
        return incidents;
    }

    public void setIncidents(List<AbstrIncident> incidents) {
        this.incidents = incidents;
    }

    @Override
    protected void callCorps() throws Exception {


        initialiserTransaction();

        DAOIncident<AbstrIncident> dao = (DAOIncident<AbstrIncident>) DAOFactory.getInstance().getDaoFromType(AbstrIncident.class);
        dao.setEm(em);

//        incidents = dao.findIncidentANotifier();

        // Doivent être notifie 
        Query query = em.createQuery("SELECT i FROM i_superclass i WHERE i.lastNotification is null");
        incidents = query.getResultList();



        //On supprimer de la liste les incident ne devant pas êter notifié (usage de la methode doitEtreNotifieParMail() des incidents.
        for (Iterator<AbstrIncident> it = incidents.iterator(); it.hasNext();) {
            AbstrIncident abstrIncident = it.next();
            if (!abstrIncident.doitEtreNotifieParMail()) {
                it.remove();
            } else { // Si la ressources doit être concervé. On la lock car elle va être modifiée dans le reste de la procédure
                verrouillerObjectDansLEM(abstrIncident, LockModeType.PESSIMISTIC_READ);
            }
        }

        //Si on a toujours des incident, On va envoyer le mail.
        if (!incidents.isEmpty()) {
            // On construit le mail à envoyer

            ServiceMailNotifier serviceMail = ServiceMailNotifier.getInstance();

            TacheEnvoyerMail mailSendTask = (TacheEnvoyerMail) TacheFactory.getInstance().getNewTask(TacheEnvoyerMail.class, false);

            VelocityContext vCtxt = new VelocityContext();
            vCtxt.put("incidents", incidents);
            vCtxt.put("titreMail", "Alerte d'incident ou évènements survenu sur le serveur");
            vCtxt.put("descMail", "Des incident ou évènement se sont produit sur le serveur. Cette tâche d'alerte est executée toute les " + this.printSchedule() + " afin de vérifier la présence de nouveau incidents de de nous en notifier l'existence.");
            String txtMail = VelocityTemplateLoad.rendu("rssagregator/services/mailtemplate/MailAlertTemplate.vsl", vCtxt);
            mailSendTask.setContent(txtMail);

            mailSendTask.setPropertiesMail(serviceMail.getPropertiesMail());
            mailSendTask.setToMailAdresses(serviceMail.returnMailAdmin());
            mailSendTask.setSubject("ALERTE : de nouveaux incidents se sont produits");
            Future<TacheEnvoyerMail> fut = serviceMail.submit(mailSendTask);

            fut.get(30, TimeUnit.SECONDS); // On donne 30 seconde au mail

            // ------------------MISE A JOUR DES INCIDENT (si le mail est bien parti)
            ServiceCRUDFactory factoryCRUD = ServiceCRUDFactory.getInstance();
            AbstrServiceCRUD serviceCrud = factoryCRUD.getServiceFor(AbstrIncident.class);
            for (int i = 0; i < incidents.size(); i++) {
                AbstrIncident abstrIncident = incidents.get(i);
                abstrIncident.setLastNotification(new Date());
                serviceCrud.modifier(abstrIncident, em);
            }
        }
    }
}
