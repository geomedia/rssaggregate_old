/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;
import java.io.IOException;
import rssagregator.dao.DAOFactory;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.RollbackException;
import javax.xml.ws.http.HTTPException;
import rssagregator.beans.Flux;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.MailIncident;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.dao.DAOIncident;

/**
 * <strong>N'EST PLUS UTILISÉ LA GESTION DES INCIDENTS EST MAINTENANT INCLUE DANS LES SERVICES</strong>
 * Cette classe permet d'interpréter les exeptions renvoyées et de générer des
 * incidents qui seront persistés dans la base de données.
 *
 * @author clem
 */
@Deprecated
public class ServiceGestionIncident {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceGestionIncident.class);
    private static ServiceGestionIncident instance = new ServiceGestionIncident();

    /**
     * *
     * Parcours les incidents du flux et clos si certain sont ouverts. Cette
     * méthode est utilisée à la fin de la tache de récup. Si la récup s'est
     * bien déroulé, alors il est nécessaire de clore. 
     *
     * @param flux
     */
    public static void fermerLesIncidentsDuFlux(Flux flux) {
        int i;
        System.out.println("CLOTURE");
        List<CollecteIncident> incidentOuvert = flux.getIncidentEnCours();

        for (i = 0; i < incidentOuvert.size(); i++) {
            System.out.println("OO");
            // On vérifi quand même que la date de fin est bien null
            if (incidentOuvert.get(i).getDateFin() == null) {
                try {
                    Date datefin = new Date();
                    incidentOuvert.get(i).setDateFin(datefin);
                    System.out.println("DATE DE FIN : " + datefin);
                    DAOFactory.getInstance().getDAOIncident().modifier(incidentOuvert.get(i));
//                    flux.getIncidentEnCours().remove(incidentOuvert.get(i));
                    
                } catch (Exception ex) {
                    Logger.getLogger(ServiceGestionIncident.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        flux.getIncidentEnCours().clear();
    }

    /**
     * *
     * Construceur prive, c'est un singleton...
     */
    private ServiceGestionIncident() {
    }

    /**
     * *
     * Methode pour factoriser la création de l'incident flux. Crée un flux
     * incident avec le message envoyé pour le flux envoyé. L'incident est
     * persisté par la méthode, puis une référence vers l'objet crée est
     * retournée
     *
     * @param msg 
     * @param flux
     */
    private static AbstrIncident creeIncidentFLux(String msg, Flux flux, Throwable ex) {

        // Si on a déjà un incident ouvert de même type
        CollecteIncident incident = flux.getIncidentOuverType(CollecteIncident.class);
        if (incident == null) { // Si on n'a pas d'incident ouvert du même type
            incident = new CollecteIncident();
            incident.setMessageEreur(msg);

            Date dateDebut = new Date();
            incident.setDateDebut(dateDebut); 
            incident.setNombreTentativeEnEchec(1);


                               System.out.println("On y arrive");
            incident.setLogErreur(ex.getClass().getSimpleName() + " : " + ex.getLocalizedMessage());

//            flux.getIncidentsLie().add(incident);
            incident.setFluxLie(flux);


            try {
                DAOFactory.getInstance().getDAOIncident().creer(incident);
//                flux.getIncidentEnCours().add(incident);
                flux.getIncidentsLie().add(incident);
//                            incident.EnregistrerAupresdesService();
//            incident.forceChangeStatut();
//     
//            incident.notifyObservers();
                
                
            } catch (Exception ex1) {
                Logger.getLogger(ServiceGestionIncident.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } else {
            Integer nbr = incident.getNombreTentativeEnEchec();
            if (nbr == null) {
                nbr = 0;
            }
            nbr++;
            incident.setNombreTentativeEnEchec(nbr);

            try {
                DAOFactory.getInstance().getDAOIncident().modifier(incident);
//                flux.getIncidentsLie().add(incident);
//                DAOFactory.getInstance().getDAOIncident().creer(incident);
            } catch (Exception ex1) {
                Logger.getLogger(ServiceGestionIncident.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

//       // On gère la réinscription du flux dans le Service de collecte. Pou rl'instant, on fait très simple.
//        TacheRecupCallable tache = new TacheRecupCallable(flux);
//        ServiceCollecteur.getInstance().addScheduledCallable(tache);




        return incident;
    }

    public static void main(String[] args) throws AddressException, MessagingException, IOException, Exception {
        ServiceMailNotifier sm= ServiceMailNotifier.getInstance();
        TacheEnvoyerMail mail = new TacheEnvoyerMail(sm);
        mail.setContent("glouglou");
        mail.setPropertiesMail(sm.getPropertiesMail());
        mail.setSubject("zouzou");
        InternetAddress[] a = new InternetAddress[1];
        a[0]= new InternetAddress("clement.rillon@gmail.com");
        mail.setToMailAdresses(a);
         sm.getExecutorService().submit(mail);
        
    }
    
    public static AbstrIncident creeIncidentServer(String msg, Object objEnErreur, Throwable e) {

        MailIncident incident = new MailIncident();
//        incident.setEntiteErreur(objEnErreur.getClass());
        incident.setMessageEreur(msg);
        incident.setLogErreur(e.getClass().getSimpleName() + " : " + e.toString());
        Date dateDebut = new Date();
        incident.setDateDebut(dateDebut);
        incident.setNombreTentativeEnEchec(1);
//        incident.setServiceEnErreur(objEnErreur.getClass().getSimpleName());
  
        
        // Il faut chercher si cet incident est nouveau
        
        try {
            DAOIncident<MailIncident> dao = (DAOIncident<MailIncident>) DAOFactory.getInstance().getDaoFromType(MailIncident.class);
            dao.creer(incident);
//            DAOFactory.getInstance().getDAOIncident().creer(incident);
        } catch (Exception ex) {
            Logger.getLogger(ServiceGestionIncident.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * *
     * Le service est un singleton...
     *
     * @return
     */
    public static ServiceGestionIncident getInstance() {
        if (ServiceGestionIncident.instance == null) {
            ServiceGestionIncident.instance = new ServiceGestionIncident();
        }
        return ServiceGestionIncident.instance;
    }

    /**
     * *
     * Permet de transformer les exeption en incident. Les incidents sont des
     * beans persités dans la base de données
     *
     * @param exception : L'exeption généré
     * @param objEnErreur Le beens pour lequel l'exemption a été généré (un flux
     * un serveur ...
     */
    @Deprecated
    public AbstrIncident gererIncident(Throwable exception, Object objEnErreur) {

        //=====================================
        //      GESTION DES ERREURS DE FLUX
        //====================================
        logger.debug("Captation d'une exeption : " + exception.getClass());
        if (objEnErreur instanceof Flux) {
            Flux flux = (Flux) objEnErreur;

            // On récupère les incidents non clos du flux.
            int i;
//            List<FluxIncident> incidenEnCours = flux.getIncidentEnCours();

            if (exception instanceof ExecutionException) {
                ExecutionException cast = (ExecutionException) exception;
                if (cast.getCause() != null && Exception.class.isAssignableFrom(cast.getCause().getClass())) {
                    exception = (Exception) cast.getCause();
                }
            }


            // Gestion de HTTPExeption
            if (exception instanceof HTTPException) {
                HTTPException ex = (HTTPException) exception;
                return creeIncidentFLux("HTTPException : Erreur sur le flux " + flux + ". Le serveur est joingnable mais retour d'un code erreur : " + ex.getStatusCode(), flux, exception);

            } // URL MAL FORMATE
            else if (exception instanceof UnknownHostException) {
                return creeIncidentFLux("UnknownHostException : Il est impossible de joindre l'host du flux", flux, exception);
            } 
             else if (exception.getClass().equals(ParsingFeedException.class)) {
                return creeIncidentFLux("ParsingFeedException : Impossible de parser le flux XML. Erreur : " + flux, flux, exception);
            } 
            else if (exception instanceof FeedException) { // Erreur de parsage du flux
                return creeIncidentFLux("FeedException : Impossible de parser le flux XML. Erreur : " + flux, flux, exception);
            } 
            
            
            else if (exception instanceof RollbackException) {
                creeIncidentServer("Erreur RollbackException : ", objEnErreur, exception);
            } else if (exception instanceof Exception) {
                   System.out.println("LALA - 5 " + exception.getClass());
                return creeIncidentFLux("ERREUR inconnue : " + flux, flux, exception);
             
            }
        }
        else if(objEnErreur instanceof ServiceMailNotifier){
            creeIncidentServer("erreur a l'envoie de mail", ServiceMailNotifier.class.toString(), exception);
        }
        return null;
    }
    
    /***
     * Lorsqu'une tâche s'est déroulé avec succes le service gérant la tache doit prévenir le service des exeption pour que celui solve potentiellement les exeptions ouvertes
     * @param tache 
     */
    public void succes(AbstrTacheSchedule tache){
        if(tache instanceof TacheLancerConnectionJMS){
            DAOIncident<MailIncident> dao = (DAOIncident<MailIncident>) DAOFactory.getInstance().getDaoFromType(MailIncident.class);
            
      
        }
        
    }
}
