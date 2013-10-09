/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.exception.CollecteUnactiveFlux;
import rssagregator.beans.exception.UnIncidableException;
import rssagregator.services.AbstrTacheSchedule;

/**
 * Permet de créer un incident
 *
 * @author clem
 */
public class IncidentFactory<T extends AbstrIncident> {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(IncidentFactory.class);

    /**
     * *
     * Retourne un incident en utilisant la généricité. Les champs dateDebut message et log sont complétés.
     *
     * @param typeRetourne : la class de l'incident
     * @param message : le message à destination des administrateurs
     * @param tw : l'exeption levée par la tâche
     * @return
     */
    public T getIncident(Class<T> typeRetourne, String message, Throwable tw) {
        T incid = null;

        try {
            incid = typeRetourne.newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(IncidentFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(IncidentFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        configurerIncident(incid, message, tw);
        System.out.println("C'est un incident de type  : " + incid.getClass());


        return incid;
    }

    /**
     * *
     * Crée un incident à partir de la tache envoyée en paramettre. Pour cela il faut que la tâche envoyé soit incidable
     *
     * @param tache :
     * @param message
     */
    public T createIncidentFromTask(AbstrTacheSchedule tache, String message) throws InstantiationException, IllegalAccessException, UnIncidableException {
        AbstrIncident incid;

        // On vérifie que la tache est incidable;
        if (Incidable.class.isAssignableFrom(tache.getClass())) {
            if (tache.getExeption().getClass().equals(CollecteUnactiveFlux.class)) {
                return null;
            }
            logger.debug("c'est une tâche incidable");

            Incidable cast = (Incidable) tache;
            Class c = cast.getTypeIncident();

            Object o = c.newInstance();
            incid = (AbstrIncident) o;
            configurerIncident(incid, message, tache.getExeption());
//            incid.setMessageEreur(message);
//            incid.setNombreTentativeEnEchec(1);
//            incid.setDateDebut(new Date());
            return (T) o;
        } else {
            throw new UnIncidableException("La tache envoyée en paramettre n'implémente pas l'interface incidable.");
        }
    }

    /**
     * *
     * Cette méthode permet de gérer la configuration d'un incident. Elle est utilisée par les deux méthodes permettant
     * d'obtenir un indident.
     *
     * @param incid
     * @param message : le message qui doit être délivré.
     * @param tw L'exception java a l'origine de l'incident
     */
    private static void configurerIncident(AbstrIncident incid, String message, Throwable tw) {
        incid.setMessageEreur(message);
        incid.setNombreTentativeEnEchec(1);
        incid.setDateDebut(new Date());
        
        
        if(incid.getClass().equals(MailIncident.class)){
            incid.setNotificationImperative(false);
        }
        else{
            incid.setNotificationImperative(true);
        }
        
        if(tw != null){
            incid.setLogErreur(tw.toString());
        }
    }
}
