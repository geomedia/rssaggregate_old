/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import com.sun.syndication.io.FeedException;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.RollbackException;
import javax.xml.ws.http.HTTPException;
import rssagregator.beans.Flux;
import rssagregator.beans.TacheRecupCallable;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.BDDIncident;
import rssagregator.beans.incident.FluxIncident;

/**
 * Cette classe permet d'interpréter les exeptions renvoyées et de générer des
 * incidents qui seront persistés dans la base de données.
 *
 * @author clem
 */
public class ServiceGestionIncident {

    private static ServiceGestionIncident instance = new ServiceGestionIncident();

    /**
     * *
     * Parcours les incidents du flux et clos si certain sont ouverts. Cette
     * méthode est utilisée à la fin de la tache de récup. Si la récup s'est
     * bien déroulé, alors il est nécessaire de clore. La liste des incident en cours est ensuite vidée.
     *
     * @param flux
     */
    public static void fermerLesIncidentsDuFlux(Flux flux) {
        int i;
        List<FluxIncident> incidentOuvert = flux.getIncidentEnCours();

        for (i = 0; i < incidentOuvert.size(); i++) {
            // On vérifi quand même que la date de fin est bien null
            if (incidentOuvert.get(i).getDateFin() == null) {
                try {
                    incidentOuvert.get(i).setDateFin(new Date());
                    DAOFactory.getInstance().getDAOIncident().modifier(incidentOuvert.get(i));
                    flux.getIncidentEnCours().remove(incidentOuvert.get(i));
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
    private static AbstrIncident creeIncidentFLux(String msg, Flux flux, Exception ex) {

        // Si on a déjà un incident ouvert de même type
        FluxIncident incident = flux.getIncidentOuverType(FluxIncident.class);
        if (incident == null) { // Si on n'a pas d'incident ouvert du même type
            incident = new FluxIncident();
            incident.setMessageEreur(msg);

            Date dateDebut = new Date();
            incident.setDateDebut(dateDebut);
            incident.setNombreTentativeEnEchec(1);

            incident.setLogErreur(ex.getClass().getSimpleName() + " : " + ex.getLocalizedMessage());

//            flux.getIncidentsLie().add(incident);
            incident.setFluxLie(flux);


            try {
                DAOFactory.getInstance().getDAOIncident().creer(incident);
                flux.getIncidentEnCours().add(incident);
            } catch (Exception ex1) {
                Logger.getLogger(ServiceGestionIncident.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } else {
            Integer nbr = incident.getNombreTentativeEnEchec();
            if(nbr == null){
                nbr=0;
            }
            nbr++;
            incident.setNombreTentativeEnEchec(nbr);

            try {
                DAOFactory.getInstance().getDAOIncident().modifier(incident);
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

    public static AbstrIncident creeIncidentBDD(String msg, Object objEnErreur, Exception e) {

        BDDIncident incident = new BDDIncident();
        incident.setEntiteErreur(objEnErreur.getClass());
        incident.setMessageEreur(msg);

        incident.setLogErreur(e.getClass().getSimpleName() + " : " + e.toString());

        Date dateDebut = new Date();
        incident.setDateDebut(dateDebut);
        incident.setNombreTentativeEnEchec(1);


        try {
            DAOFactory.getInstance().getDAOIncident().creer(incident);
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
    public AbstrIncident gererIncident(Exception exception, Object objEnErreur) {



        //=====================================
        //      GESTION DES ERREURS DE FLUX
        //====================================

        if (objEnErreur instanceof Flux) {
            Flux flux = (Flux) objEnErreur;
            System.out.println("ID du FLUX ENVOYE : " + flux.getID());

            // On récupère les incidents non clos du flux.
            int i;
            List<FluxIncident> incidenEnCours = flux.getIncidentEnCours();


            // Gestion de HTTPExeption
            if (exception instanceof HTTPException) {
                HTTPException ex = (HTTPException) exception;
                return creeIncidentFLux("HTTPException : Erreur sur le flux " + flux + ". Le serveur est joingnable mais retour d'un code erreur : " + ex.getStatusCode(), flux, exception);

            } // URL MAL FORMATE
            else if (exception instanceof UnknownHostException) {
                return creeIncidentFLux("UnknownHostException : Il est impossible de joindre l'host du flux", flux, exception);
            } else if (exception instanceof FeedException) { // Erreur de parsage du flux
                return creeIncidentFLux("FeedException : Impossible de parser le flux XML : " + flux, flux, exception);
            } else if (exception instanceof RollbackException) {
                creeIncidentBDD("Erreur RollbackException : ", objEnErreur, exception);
            } else if (exception instanceof Exception) {
                return creeIncidentFLux("ERREUR inconnue : " + flux, flux, exception);
            }
        }
        return null;
    }
}
