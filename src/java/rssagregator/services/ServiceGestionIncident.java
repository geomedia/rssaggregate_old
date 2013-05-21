/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import dao.DAOFactory;
import dao.DaoFlux;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.ws.http.HTTPException;
import rssagregator.beans.Flux;
import rssagregator.beans.incident.AbstrFluxIncident;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.HTTPIndident;

/**
 * Cette classe permet d'interpréter les exeption renvoyé et de générer des
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
     *
     * @param exception : L'exeption généré
     * @param objEnErreur Le beens pour lequel l'exemption a été généré
     */
    public void gererIncident(Exception exception, Object objEnErreur) {
        // Gestion des incident de flux
        if (objEnErreur instanceof Flux) {
            Flux flux = (Flux) objEnErreur;


            // On récupère les incidents non clos du flux.
            int i;
            List<AbstrFluxIncident> incidenEnCours = flux.getIncidentEnCours();


            // Gestion de HTTPExeption
            if (exception instanceof HTTPException) {
                System.out.println("HTTPExeption");
            }

            // URL MAL FORMATE
            if (exception instanceof UnknownHostException) {
                System.out.println("Impossible de joindre l'host");
                // Si on a déjà un incident ouvert de même type
                AbstrFluxIncident ouvert = flux.getIncidentOuverType(HTTPIndident.class);
                if (ouvert == null) {
                    HTTPIndident hTTPIndident = new HTTPIndident();
                    hTTPIndident.setMessageEreur("Il est impossible de joindre l'host du flux");
                    Date dateDebut = new Date();
                    System.out.println("DATE COURANTE : " + dateDebut);
                    hTTPIndident.setDateDebut(dateDebut);
                    hTTPIndident.setNombreTentativeEnEchec(1);
                    flux.getIncident().add(hTTPIndident);
                } else {
                    int nbr = ouvert.getNombreTentativeEnEchec();
                    nbr++;
                    ouvert.setNombreTentativeEnEchec(nbr);
                                       
                }





                ListeFluxCollecteEtConfigConrante.getInstance().modifierFlux(flux);
//                DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
//                daoFlux.modifier(flux);

            }


            //gestion d'une erreur HTTP
            if (exception.getMessage().equals("Erreur HTTP")) {
                // récup du code erreur
                int codeErreur = flux.getMediatorFlux().getRequesteur().getHttpStatut();


                //Si le flux était en erreur lors de la dernière levée

                if (flux.getErreurDerniereLevee()) {
                    // On récupère les incidents du flux.
                    // Si on trouve un incident non clos du même type que l'incident en cours, alors on va agir sur l'incident non clos
                    //TODO : On n'a pas fini l'objet de gestion des exeptions
                }
            }
        }
    }
}
