/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.mail.internet.InternetAddress;
import rssagregator.beans.UserAccount;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.services.mailtemplate.TemplateMailAlertIncident;

/**
 * Cette tache a pour role de collecter toutes les 30 minutes les incidents
 * nouveaux et d'envoyer un mail aux administrateur. Cette notification
 * d'urgence ne sera pas réitéré par la suite.
 * 
 * ELLE N'A pas d'incident associé
 *
 * @author clem
 */
public class TacheAlerteMail extends AbstrTacheSchedule<TacheAlerteMail> {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheAlerteMail.class);
//    private String corps;
    private String objet;
    private InternetAddress[] address;
    private List<AbstrIncident> incidents;

    public TacheAlerteMail(Observer s) {
        super(s);
        
    }

    @Override
    public TacheAlerteMail call() throws Exception {
        logger.debug("Lancement");
        this.exeption =null;
//        TemplateMailAlertIncident template = new TemplateMailAlertIncident();
        
        DAOIncident<AbstrIncident> dao = (DAOIncident<AbstrIncident>) DAOFactory.getInstance().getDaoFromType(AbstrIncident.class);
        try {
            dao.setNullLastNotification(true);
            incidents = dao.findCriteria(AbstrIncident.class);
//            template.setListIncident(list);
//            this.corps = template.getCorpsMail();
//            for (int i = 0; i < list.size(); i++) {
//                AbstrIncident abstrIncident = list.get(i);
//                System.out.println("Incid non notifié : " + abstrIncident);
//            }

            // Construction de la liste des destinataire.
            List<UserAccount> listuser = DAOFactory.getInstance().getDAOUser().findUserANotifier();
            address = new InternetAddress[listuser.size()];
            for (int i = 0; i < listuser.size(); i++) {
                UserAccount userAccount = listuser.get(i);
                address[i] = new InternetAddress(userAccount.getMail());
                System.out.println("destinataire : " + userAccount.getMail());
            }
            
            this.setObjet("ALERT : Des évènement viennent de se produirent sur le serveur");
     

logger.debug("----> Succes");

        } catch (Exception e) {
            logger.error(e);
            System.out.println("errrr : " + e);
            this.exeption = e;
        } finally {
            this.setChanged();
            this.notifyObservers();
            return this;

        }
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String[] args) {
        ServiceMailNotifier sm = ServiceMailNotifier.getInstance();

        TacheAlerteMail tm = new TacheAlerteMail(sm);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        try {
            executorService.submit(tm);
        } catch (Exception e) {
            System.out.println("ERR");
        }



        System.out.println("FIN MAIN");

    }

//    public String getCorps() {
//        return corps;
//    }
//
//    public void setCorps(String corps) {
//        this.corps = corps;
//    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public InternetAddress[] getAddress() {
        return address;
    }

    public void setAddress(InternetAddress[] address) {
        this.address = address;
    }

    public List<AbstrIncident> getIncidents() {
        return incidents;
    }

    public void setIncidents(List<AbstrIncident> incidents) {
        this.incidents = incidents;
    }

    

}
