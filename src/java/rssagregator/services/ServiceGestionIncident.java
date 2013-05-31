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
     * Methode pour factoriser la création de l'incident flux. Crée un flux incident avec le message envoyé pour le flux envoyé
     * @param msg
     * @param flux 
     */
    private  static void creeIncidentFLux(String msg, Flux flux, Exception ex){
             
                // Si on a déjà un incident ouvert de même type
                FluxIncident ouvert = flux.getIncidentOuverType(FluxIncident.class);
                if (ouvert == null) {
                    FluxIncident incident = new FluxIncident();
                    incident.setMessageEreur(msg);
                    
                    Date dateDebut = new Date();
                    incident.setDateDebut(dateDebut);
                    incident.setNombreTentativeEnEchec(1);
                    
                    incident.setLogErreur(ex.getClass().getSimpleName()+" : "+ ex.getLocalizedMessage());
                    
                    flux.getIncidentsLie().add(incident);
                    incident.setFluxLie(flux);
                    
                } else {
                    int nbr = ouvert.getNombreTentativeEnEchec();
                    nbr++;
                    ouvert.setNombreTentativeEnEchec(nbr);
                                       
                }
                ListeFluxCollecteEtConfigConrante.getInstance().modifierFlux(flux);
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
    public void gererIncident(Exception exception, Object objEnErreur) {
        
        
        
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
                     creeIncidentFLux("HTTPException : Erreur sur le flux "+flux+". Le serveur est joingnable mais retour d'un code erreur : " + ex.getStatusCode(), flux, exception);
            }

            // URL MAL FORMATE
            if (exception instanceof UnknownHostException) {
                creeIncidentFLux("UnknownHostException : Il est impossible de joindre l'host du flux", flux, exception);
            }
            
            if(exception instanceof FeedException){ // Erreur de parsage du flux
                creeIncidentFLux("FeedException : Impossible de parser le flux XML : " + flux, flux, exception);
            }
        }
    }
}
