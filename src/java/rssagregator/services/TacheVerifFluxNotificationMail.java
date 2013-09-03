/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.InternetAddress;
import rssagregator.beans.UserAccount;
import rssagregator.beans.incident.FluxIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.dao.DAOUser;
import rssagregator.utils.MailTemplate;

/**
 * Cette tâche est lancée toute les jours. Toutes les exception ouvertes sont
 * vérifiées et un mail de relance est envoyé si besoin.
 *
 * @author clem
 */
public class TacheVerifFluxNotificationMail implements Callable<Object> {

    /**
     * *
     * Tache d'observation de tout les incidents ouvert. construit un mail de
     * relance si besion UNE FOIS TOUT LES JOURS C'est bien suiffisant.
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object call() throws Exception {
          // On récupère la liste des Incidents 
        DAOIncident dao = DAOFactory.getInstance().getDAOIncident();
        List<FluxIncident> incid = dao.findAllOpenIncident();

        //Préparation du corps du mail
        MailTemplate template = new MailTemplate();
        template.setListIncidentFlux(incid);
        String corps = template.getCorpsMail();

        // Construction de la liste des destinataire.
        List<UserAccount> listuser = DAOFactory.getInstance().getDAOUser().findUserANotifier();
        InternetAddress[] address = new InternetAddress[listuser.size()];
        for (int i = 0; i < listuser.size(); i++) {
            UserAccount userAccount = listuser.get(i);
            address[i] = new InternetAddress(userAccount.getMail());
            System.out.println("destinataire : "+userAccount.getMail());
        }

        
        ServiceMailNotifier.getInstance().envoyerMail(address, "Compte rendu des flux en erreur", corps);

        // Le mail doit réinscrire auprès du service d'envoie des email 
        ServiceMailNotifier.getInstance().taskEnd(this);
//        DaemonCentral.getInstance().executorServiceAdministratif.submit(this);
        return null;

    }

    public static void main(String[] args) {
        TacheVerifFluxNotificationMail tache = new TacheVerifFluxNotificationMail();
        try {
            tache.call();
        } catch (Exception ex) {
            Logger.getLogger(TacheVerifFluxNotificationMail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
