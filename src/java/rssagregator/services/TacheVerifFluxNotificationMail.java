/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.velocity.VelocityContext;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.services.mailtemplate.TemplateRecapFluxEchec;
import rssagregator.services.mailtemplate.VelocityTemplateLoad;

/**
 * Cette tâche est lancée toute les jours. Toutes les exception ouvertes sont vérifiées et un mail de relance est envoyé
 * si besoin.
 *
 * @author clem
 */
public class TacheVerifFluxNotificationMail extends TacheImpl<TacheVerifFluxNotificationMail> {

    private String corps;
    private String objet;
//    private InternetAddress[] address;

    public TacheVerifFluxNotificationMail(Observer s) {
        super(s);
    }

    public TacheVerifFluxNotificationMail() {
        super();
    }

    @Override
    protected void callCorps() throws Exception {
        // On récupère la liste des Incidents 
        DAOIncident dao = DAOFactory.getInstance().getDAOIncident();

        dao.setClos(false);
        List<CollecteIncident> incidents = dao.findAllOpenIncident();

        if (!incidents.isEmpty()) {



            //Préparation du corps du mail
//            TemplateRecapFluxEchec template = new TemplateRecapFluxEchec();
//            template.setListIncidentFlux(incidents);
//            corps = template.getCorpsMail();

            // Construction de la liste des destinataire.
//        List<UserAccount> listuser = DAOFactory.getInstance().getDAOUser().findUserANotifier();
//        address = new InternetAddress[listuser.size()];
//        for (int i = 0; i < listuser.size(); i++) {
//            UserAccount userAccount = listuser.get(i);
//            address[i] = new InternetAddress(userAccount.getMail());
//            System.out.println("destinataire : "+userAccount.getMail());
//        }

            objet = "";

            ServiceMailNotifier serviceMail = ServiceMailNotifier.getInstance();
            TacheEnvoyerMail mailSendTask = new TacheEnvoyerMail(serviceMail);

            mailSendTask.setPropertiesMail(serviceMail.getPropertiesMail());
            mailSendTask.setToMailAdresses(serviceMail.returnMailAdmin());
            mailSendTask.setSubject("Récapitulatif des Incidents");
                                    VelocityContext vCtxt = new VelocityContext();
            vCtxt.put("incidents", incidents);
            vCtxt.put("titreMail", "Récapitulatif des incidents et évènements");
            vCtxt.put("descMail", "Ce mail est un récapitulatif journalier des incidents et évènements survenus sur le serveur. Il est envoyé toutes les "+this.printSchedule()+ ". Veillez a résondre chacun des cas. Lorsque votre travail de maintenance sera terminé, vous pouvez clore manuellement les incidents afin d'empêcher que ceux-ci se renotifient") ;
            
            
            
            String txtMail = VelocityTemplateLoad.rendu("rssagregator/services/mailtemplate/MailAlertTemplate.vsl", vCtxt);
            mailSendTask.setContent(txtMail);
            

            Future<TacheEnvoyerMail> fut = serviceMail.executorService.submit(mailSendTask); // On envoi le mail en lui laissant 30 secondes
            fut.get(30, TimeUnit.SECONDS);


            // Formulation du contenu en utilisant la template Velocity

 
        }
//        return this;
    }

    /**
     * *
     * Tache d'observation de tout les incidents ouvert. construit un mail de relance si besion UNE FOIS TOUT LES JOURS
     * C'est bien suiffisant.
     *
     * @return
     * @throws Exception
     */
//    @Override
//    public TacheVerifFluxNotificationMail call() throws Exception {
//        this.exeption =null;
//        try {
//                     // On récupère la liste des Incidents 
//        DAOIncident dao = DAOFactory.getInstance().getDAOIncident();
//        
//        dao.setClos(false);
//        List<CollecteIncident> incid = dao.findAllOpenIncident();
//
//        //Préparation du corps du mail
//        TemplateRecapFluxEchec template = new TemplateRecapFluxEchec();
//        template.setListIncidentFlux(incid);
//        corps = template.getCorpsMail();
//
//        // Construction de la liste des destinataire.
////        List<UserAccount> listuser = DAOFactory.getInstance().getDAOUser().findUserANotifier();
////        address = new InternetAddress[listuser.size()];
////        for (int i = 0; i < listuser.size(); i++) {
////            UserAccount userAccount = listuser.get(i);
////            address[i] = new InternetAddress(userAccount.getMail());
////            System.out.println("destinataire : "+userAccount.getMail());
////        }
//        
//        objet = "Compte rendu des flux en erreur";
//
//        return this;
//            
//        } catch (Exception e) {
//            this.exeption = e;
//            return this;
//        }
//        finally{
//            this.setChanged();
//            this.notifyObservers();
//        }
//    }
    public String getCorps() {
        return corps;
    }

    public void setCorps(String corps) {
        this.corps = corps;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }
//    public InternetAddress[] getAddress() {
//        return address;
//    }
//
//    public void setAddress(InternetAddress[] address) {
//        this.address = address;
//    }
}
