/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import com.sun.syndication.io.FeedException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import javax.xml.ws.http.HTTPException;
import rssagregator.beans.Flux;
import rssagregator.beans.incident.AbstrIncident;
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
     * Construceur prive, c'est un singleton...
     */
    private ServiceGestionIncident() {
    }
    
    
    /***
     * Methode pour factoriser la création de l'incident flux. Crée un flux incident avec le message envoyé pour le flux envoyé. L'incident est persisté par la méthode, puis une référence vers l'objet crée est retournée
     * @param msg
     * @param flux 
     */
    private  static AbstrIncident creeIncidentFLux(String msg, Flux flux, Exception ex){
             
                // Si on a déjà un incident ouvert de même type
                FluxIncident incident = flux.getIncidentOuverType(FluxIncident.class);
                if (incident == null) {
                    incident = new FluxIncident();
                    incident.setMessageEreur(msg);
                    
                    Date dateDebut = new Date();
                    incident.setDateDebut(dateDebut);
                    incident.setNombreTentativeEnEchec(1);
                    
                    incident.setLogErreur(ex.getClass().getSimpleName()+" : "+ ex.getLocalizedMessage());
                    
                    flux.getIncidentsLie().add(incident);
                    incident.setFluxLie(flux);
                    
                } else {
                    int nbr = incident.getNombreTentativeEnEchec();
                    nbr++;
                    incident.setNombreTentativeEnEchec(nbr);
                                       
                }
                ListeFluxCollecteEtConfigConrante.getInstance().modifierFlux(flux);
                return incident;
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
     *  Permet de transformer les exeption en incident. Les incidents sont des beans persités dans la base de données
     * @param exception : L'exeption généré
     * @param objEnErreur Le beens pour lequel l'exemption a été généré (un flux un serveur ...
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
                     return creeIncidentFLux("HTTPException : Erreur sur le flux "+flux+". Le serveur est joingnable mais retour d'un code erreur : " + ex.getStatusCode(), flux, exception);
                     
            }

            // URL MAL FORMATE
            if (exception instanceof UnknownHostException) {
                return creeIncidentFLux("UnknownHostException : Il est impossible de joindre l'host du flux", flux, exception);
            }
            
            if(exception instanceof FeedException){ // Erreur de parsage du flux
                return creeIncidentFLux("FeedException : Impossible de parser le flux XML : " + flux, flux, exception);
            }
        }
        return null;
    }
}
