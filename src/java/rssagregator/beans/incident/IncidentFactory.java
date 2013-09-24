/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.services.AbstrTacheSchedule;
import rssagregator.services.ServiceSynchro;
import rssagregator.services.TacheSynchroRecupItem;

/**
 *
 *
 * @author clem
 */
public class IncidentFactory<T extends AbstrIncident> {

        protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(IncidentFactory.class);
    /**
     * *
     * Retourne un incident en utilisant la généricité. Les champs dateDebut
     * message et log sont complétés.
     *
     * @param typeRetourne : la class de l'incident
     * @param message
     * @param tw
     * @return
     */
    public T getIncident(Class<T> typeRetourne, String message, Throwable tw) {
        T incid = null;

//        if (typeRetourne.equals(SynchroIncident.class)) {
//            System.out.println("LA");
//            incid = (T) new SynchroIncident();
//        }
        try {
            incid = typeRetourne.newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(IncidentFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(IncidentFactory.class.getName()).log(Level.SEVERE, null, ex);
        }


        // On configure ce qui est général à tout les incident
        incid.setDateDebut(new Date());
        incid.setMessageEreur(message);
        incid.setLogErreur(tw.toString());
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
    public T createIncidentFromTask(AbstrTacheSchedule tache, String message) throws InstantiationException, IllegalAccessException {

        AbstrIncident incid = null;
        
        // On vérifie que la tache est incidable;
        if(Incidable.class.isAssignableFrom(tache.getClass())){
            logger.debug("c'est une tâche incidable");
            
            Incidable cast = (Incidable) tache;
            Class c = cast.getTypeIncident();
            
            Object o = c.newInstance();
            incid = (AbstrIncident) o;
            incid.setMessageEreur(message);
            incid.setNombreTentativeEnEchec(1);
            return (T) o;
            
        }
//        if (tache.getClass().equals(TacheSynchroRecupItem.class)) {
//            incid = new SynroRecupItemIncident();
//        }
//
//        if (incid != null) {
//            return (T) incid;
//        }

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
