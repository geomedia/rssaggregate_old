/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.velocity.VelocityContext;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.services.ServiceMailNotifier;
import rssagregator.services.mailtemplate.VelocityTemplateLoad;

/**
 * Cette tâche est lancée toute les jours. Elle doit envoyer un mail récapitulant tout les incidents pour lesquel l'administrateur doit intervenir. CAD : <ul>
 * <li>Incident ne possédant pas de date de fin </li>
 * <li></li>
 * 
 * </ul>
 * si besoin.
 *
 * @author clem
 */
public class TacheVerifFluxNotificationMail extends TacheImpl<TacheVerifFluxNotificationMail> {

    private String corps;
    private String objet;
//    private InternetAddress[] address;


    @Override
    protected void callCorps() throws Exception {
        // On récupère la liste des Incidents 
        DAOIncident dao = DAOFactory.getInstance().getDAOIncident();

        dao.setClos(false);
        List<CollecteIncident> incidents = dao.findAllOpenIncident();

        if (!incidents.isEmpty()) {

            objet = "";

            ServiceMailNotifier serviceMail = ServiceMailNotifier.getInstance();
            TacheEnvoyerMail mailSendTask = (TacheEnvoyerMail) TacheFactory.getInstance().getNewTask(TacheEnvoyerMail.class, false);//new TacheEnvoyerMail(serviceMail);

            mailSendTask.setPropertiesMail(serviceMail.getPropertiesMail());
            mailSendTask.setToMailAdresses(serviceMail.returnMailAdmin());
            mailSendTask.setSubject("Récapitulatif des Incidents");
                                    VelocityContext vCtxt = new VelocityContext();
            vCtxt.put("incidents", incidents);
            vCtxt.put("titreMail", "Récapitulatif des incidents et évènements");
            vCtxt.put("descMail", "Ce mail est un récapitulatif journalier des incidents et évènements survenus sur le serveur. Il est envoyé toutes les "+this.printSchedule()+ ". Veillez a résondre chacun des cas. Lorsque votre travail de maintenance sera terminé, vous pouvez clore manuellement les incidents afin d'empêcher que ceux-ci se renotifient") ;
            
            String txtMail = VelocityTemplateLoad.rendu("rssagregator/services/mailtemplate/MailAlertTemplate.vsl", vCtxt);
            mailSendTask.setContent(txtMail);
            

            Future<TacheEnvoyerMail> fut = serviceMail.submit(mailSendTask); // On envoi le mail en lui laissant 30 secondes
            fut.get(30, TimeUnit.SECONDS);
        }
    }

   
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
}
