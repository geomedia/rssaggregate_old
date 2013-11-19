/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import org.apache.velocity.VelocityContext;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
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

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheAlerteMail.class);
//    private String corps;
//    private String objet;
//    private InternetAddress[] address;
    /**
     * *
     * Les incidents relevés par la tâche et devant être notifié par mail.
     */
    private List<AbstrIncident> incidents;

    public TacheAlerteMail(Observer s) {
        super(s);
    }

    public TacheAlerteMail() {
        super();
    }

//    @Override
//    public TacheAlerteMail call() throws Exception {
//        logger.debug("Lancement");
//        this.exeption = null;
////        TemplateMailAlertIncident template = new TemplateMailAlertIncident();
//
//        em = DAOFactory.getInstance().getEntityManager();
//        try {
//
//            em.getTransaction().begin();
//
//            DAOIncident<AbstrIncident> dao = (DAOIncident<AbstrIncident>) DAOFactory.getInstance().getDaoFromType(AbstrIncident.class);
//            dao.setEm(em);
//
//            incidents = dao.findIncidentANotifier();
//
//            //On supprimer de la liste les incident ne devant pas êter notifié (usage de la methode doitEtreNotifieParMail() des incidents.
//            for (Iterator<AbstrIncident> it = incidents.iterator(); it.hasNext();) {
//
//                AbstrIncident abstrIncident = it.next();
//
//                System.out.println("Incident : " + abstrIncident);
//                System.out.println("Nombre tentative : " + abstrIncident.getNombreTentativeEnEchec());
//
//                if (!abstrIncident.doitEtreNotifieParMail()) {
////                    iterator.remove();
//                    it.remove();
//                }
//            }
//
//            //Si on a toujours des incident, On va envoyer le mail.
//            if (!incidents.isEmpty()) {
//                // On construit le mail à envoyer
//
//                ServiceMailNotifier serviceMail = ServiceMailNotifier.getInstance();
//
//                TacheEnvoyerMail mailSendTask = new TacheEnvoyerMail(ServiceMailNotifier.getInstance());
//
//
//
//                VelocityContext vCtxt = new VelocityContext();
//                vCtxt.put("incidents", incidents);
//                String txtMail = VelocityTemplateLoad.rendu("rssagregator/services/mailtemplate/MailAlertTemplate.vsl", vCtxt);
//                mailSendTask.setContent(txtMail);
//
////                TemplateMailAlertIncident template = new TemplateMailAlertIncident();
////                template.setListIncident(incidents);
////                mailSendTask.setContent(template.getCorpsMail());
//
//
//                mailSendTask.setPropertiesMail(serviceMail.getPropertiesMail());
//                mailSendTask.setToMailAdresses(serviceMail.returnMailAdmin());
//                mailSendTask.setSubject("ALERTE : de nouveaux incidents se sont produits");
//                Future<TacheEnvoyerMail> fut = serviceMail.executorService.submit(mailSendTask);
//
//                fut.get(30, TimeUnit.SECONDS);
//
//                // ------------------MISE A JOUR DES INCIDENT
//                for (int i = 0; i < incidents.size(); i++) {
//                    AbstrIncident abstrIncident = incidents.get(i);
//                    abstrIncident.setLastNotification(new Date());
//                }
//
//                ServiceCRUDFactory factoryCRUD = ServiceCRUDFactory.getInstance();
//
//                AbstrServiceCRUD serviceCrud = factoryCRUD.getServiceFor(AbstrIncident.class);
//
//                for (int i = 0; i < incidents.size(); i++) {
//                    AbstrIncident abstrIncident = incidents.get(i);
//                    serviceCrud.modifier(abstrIncident, em);
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error("erreur de la tâche", e);
//            this.exeption = e;
//        } finally {
//            nbrTentative++;
//            if (em != null && em.isOpen()) {
//                if (em.getTransaction() != null && em.getTransaction().isActive()) {
//                    em.getTransaction().commit();
//                }
//                em.close();
//            }
//
//            this.setChanged();
//            this.notifyObservers();
//            return this;
//        }
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

//    public String getCorps() {
//        return corps;
//    }
//
//    public void setCorps(String corps) {
//        this.corps = corps;
//    }
//    public String getObjet() {
//        return objet;
//    }
//
//    public void setObjet(String objet) {
//        this.objet = objet;
//    }
//    public InternetAddress[] getAddress() {
//        return address;
//    }
//
//    public void setAddress(InternetAddress[] address) {
//        this.address = address;
//    }
    public List<AbstrIncident> getIncidents() {
        return incidents;
    }

    public void setIncidents(List<AbstrIncident> incidents) {
        this.incidents = incidents;
    }

    @Override
    protected void callCorps() throws Exception {


        em = DAOFactory.getInstance().getEntityManager();
        em.getTransaction().begin();

        DAOIncident<AbstrIncident> dao = (DAOIncident<AbstrIncident>) DAOFactory.getInstance().getDaoFromType(AbstrIncident.class);
        dao.setEm(em);

        incidents = dao.findIncidentANotifier();

        //On supprimer de la liste les incident ne devant pas êter notifié (usage de la methode doitEtreNotifieParMail() des incidents.
        for (Iterator<AbstrIncident> it = incidents.iterator(); it.hasNext();) {

            AbstrIncident abstrIncident = it.next();

            System.out.println("Incident : " + abstrIncident);
            System.out.println("Nombre tentative : " + abstrIncident.getNombreTentativeEnEchec());

            if (!abstrIncident.doitEtreNotifieParMail()) {
//                    iterator.remove();
                it.remove();
            }
        }

        //Si on a toujours des incident, On va envoyer le mail.
        if (!incidents.isEmpty()) {
            // On construit le mail à envoyer

            ServiceMailNotifier serviceMail = ServiceMailNotifier.getInstance();

            TacheEnvoyerMail mailSendTask = new TacheEnvoyerMail(ServiceMailNotifier.getInstance());
            



            VelocityContext vCtxt = new VelocityContext();
            vCtxt.put("incidents", incidents);
            vCtxt.put("titreMail", "Alerte d'incident ou évènements survenu sur le serveur");
            vCtxt.put("descMail", "Des incident ou évènement se sont produit sur le serveur. Cette tâche d'alerte est executée toute les " + this.printSchedule()+" afin de vérifier la présence de nouveau incidents de de nous en notifier l'existence.");
            String txtMail = VelocityTemplateLoad.rendu("rssagregator/services/mailtemplate/MailAlertTemplate.vsl", vCtxt);
            mailSendTask.setContent(txtMail);

//                TemplateMailAlertIncident template = new TemplateMailAlertIncident();
//                template.setListIncident(incidents);
//                mailSendTask.setContent(template.getCorpsMail());


            mailSendTask.setPropertiesMail(serviceMail.getPropertiesMail());
            mailSendTask.setToMailAdresses(serviceMail.returnMailAdmin());
            mailSendTask.setSubject("ALERTE : de nouveaux incidents se sont produits");
            Future<TacheEnvoyerMail> fut = serviceMail.executorService.submit(mailSendTask);
            System.out.println("SUB");

            fut.get(30, TimeUnit.SECONDS);

            // ------------------MISE A JOUR DES INCIDENT
            for (int i = 0; i < incidents.size(); i++) {
                AbstrIncident abstrIncident = incidents.get(i);
                abstrIncident.setLastNotification(new Date());
            }

            ServiceCRUDFactory factoryCRUD = ServiceCRUDFactory.getInstance();

            AbstrServiceCRUD serviceCrud = factoryCRUD.getServiceFor(AbstrIncident.class);

            for (int i = 0; i < incidents.size(); i++) {
                AbstrIncident abstrIncident = incidents.get(i);
                serviceCrud.modifier(abstrIncident, em);
            }
        }
    }

//    @Override
//    protected TacheAlerteMail callFinalyse() {
//
//                   nbrTentative++;
//            if (em != null && em.isOpen()) {
//                if (em.getTransaction() != null && em.getTransaction().isActive()) {
//                    em.getTransaction().commit();
//                }
//                em.close();
//            }
//
//            this.setChanged();
//            this.notifyObservers();
//            return this;
//    }
}
