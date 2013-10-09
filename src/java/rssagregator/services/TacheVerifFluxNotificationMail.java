/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.List;
import java.util.Observer;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.services.mailtemplate.TemplateRecapFluxEchec;

/**
 * Cette tâche est lancée toute les jours. Toutes les exception ouvertes sont
 * vérifiées et un mail de relance est envoyé si besoin.
 *
 * @author clem
 */
public class TacheVerifFluxNotificationMail extends AbstrTacheSchedule<TacheVerifFluxNotificationMail> {

    private String corps;
    private String objet;
//    private InternetAddress[] address;
    
    
    public TacheVerifFluxNotificationMail(Observer s) {
        super(s);
    }

    public TacheVerifFluxNotificationMail() {
    super();
    }
    
    

    
    /**
     * *
     * Tache d'observation de tout les incidents ouvert. construit un mail de
     * relance si besion UNE FOIS TOUT LES JOURS C'est bien suiffisant.
     *
     * @return
     * @throws Exception
     */
    @Override
    public TacheVerifFluxNotificationMail call() throws Exception {
        this.exeption =null;
        try {
                     // On récupère la liste des Incidents 
        DAOIncident dao = DAOFactory.getInstance().getDAOIncident();
        
        dao.setClos(false);
        List<CollecteIncident> incid = dao.findAllOpenIncident();

        //Préparation du corps du mail
        TemplateRecapFluxEchec template = new TemplateRecapFluxEchec();
        template.setListIncidentFlux(incid);
        corps = template.getCorpsMail();

        // Construction de la liste des destinataire.
//        List<UserAccount> listuser = DAOFactory.getInstance().getDAOUser().findUserANotifier();
//        address = new InternetAddress[listuser.size()];
//        for (int i = 0; i < listuser.size(); i++) {
//            UserAccount userAccount = listuser.get(i);
//            address[i] = new InternetAddress(userAccount.getMail());
//            System.out.println("destinataire : "+userAccount.getMail());
//        }
        
        objet = "Compte rendu des flux en erreur";

        return this;
            
        } catch (Exception e) {
            this.exeption = e;
            return this;
        }
        finally{
            this.setChanged();
            this.notifyObservers();
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

//    public InternetAddress[] getAddress() {
//        return address;
//    }
//
//    public void setAddress(InternetAddress[] address) {
//        this.address = address;
//    }


    
    
    
}
