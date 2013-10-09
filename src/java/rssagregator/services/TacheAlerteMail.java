/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import javax.mail.internet.InternetAddress;
import rssagregator.beans.UserAccount;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;

/**
 * Cette tache a pour role de collecter toutes les 30 minutes les incidents nouveaux et d'envoyer un mail aux
 * administrateur. Cette notification d'urgence ne sera pas réitéré par la suite.
 *
 * ELLE N'A pas d'incident associé
 *
 * @author clem
 */
public class TacheAlerteMail extends AbstrTacheSchedule<TacheAlerteMail> {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheAlerteMail.class);
//    private String corps;
//    private String objet;
//    private InternetAddress[] address;
    /***
     * Les incidents relevés par la tâche et devant être notifié par mail.
     */
    private List<AbstrIncident> incidents;

    public TacheAlerteMail(Observer s) {
        super(s);
    }

    public TacheAlerteMail() {
        super();
    }

    @Override
    public TacheAlerteMail call() throws Exception {
        logger.debug("Lancement");
        this.exeption = null;
//        TemplateMailAlertIncident template = new TemplateMailAlertIncident();

        DAOIncident<AbstrIncident> dao = (DAOIncident<AbstrIncident>) DAOFactory.getInstance().getDaoFromType(AbstrIncident.class);
        try {

            // Doivent être notifié, tous les incident dont le booleean notificationImperative est a true
            dao.setCriteriaNotificationImperative(true);
            dao.setClos(null);
            dao.setNullLastNotification(null);
   
            
//            dao.setNullLastNotification(true);
//            dao.setCriteriaNotificationImperative(null);
//            dao.setClos(null);
            incidents = dao.findCriteria(AbstrIncident.class);


            // On effectue une seconde requete pour trouver les incidents dont la notification est impérative
//            dao = (DAOIncident<AbstrIncident>) DAOFactory.getInstance().getDaoFromType(AbstrIncident.class);
//            dao.setCriteriaNotificationImperative(true);
//            dao.setNullLastNotification(null);
//            dao.setClos(null);
//            List<AbstrIncident> otherIncid = dao.findCriteria(AbstrIncident.class);
//            for (int i = 0; i < otherIncid.size(); i++) {
//                AbstrIncident abstrIncident = otherIncid.get(i);
//                if (!incidents.contains(abstrIncident)) {
//                    incidents.add(abstrIncident);
//                }
//            }


            //On supprimer de la liste les incident ne devant pas êter notifié (usage de la methode doitEtreNotifieParMail() des incidents.
            ListIterator<AbstrIncident> iterator = incidents.listIterator();
            for (Iterator<AbstrIncident> it = incidents.iterator(); it.hasNext();) {
                AbstrIncident abstrIncident = it.next();
                if (!abstrIncident.doitEtreNotifieParMail()) {
                    iterator.remove();
                }
            }




            // Construction de la liste des destinataire.
//            List<UserAccount> listuser = DAOFactory.getInstance().getDAOUser().findUserANotifier();
//            address = new InternetAddress[listuser.size()];
//            for (int i = 0; i < listuser.size(); i++) {
//                UserAccount userAccount = listuser.get(i);
//                address[i] = new InternetAddress(userAccount.getMail());
//                logger.debug("destinataire : " + userAccount.getMail());
//            }

//            this.setObjet("ALERT : Des évènement viennent de se produirent sur le serveur");

        } catch (Exception e) {
            logger.error("erreur de la tâche", e);
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
            Future f = executorService.submit(tm);
            f.get();
        } catch (Exception e) {
            System.out.println("ERR"+e);
            e.printStackTrace();
        }
        
        for (int i = 0; i < tm.getIncidents().size(); i++) {
            Object object = tm.getIncidents().get(i);
            System.out.println("+" +object);
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
}
