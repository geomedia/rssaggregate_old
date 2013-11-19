/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.Conf;
import rssagregator.beans.StillAlivePOJO;
import rssagregator.beans.incident.AliveIncident;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.dao.DAOFactory;
import rssagregator.services.crud.AbstrServiceCRUD;
import rssagregator.services.crud.ServiceCRUDFactory;
import rssagregator.utils.PropertyLoader;

/**
 * Cette tache écrit dans un fichier de log propre
 * /var/lib/RSSAgregate/log/stillalive. Ce fichier permet de vérifier qu'il n'y
 * a pas de coupure sur le service. La tâche est gérée par le service {@link ServiceServer}. Elle génère des incident de type {@link AliveIncident}
 *
 * @author clem
 */
public class TacheStillAlive extends TacheImpl<TacheStillAlive> implements Incidable {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheStillAlive.class);
    /**
     * *
     * Le fichier still alive avec lequel il faut travailler
     */
    File file;
    /**
     * *
     * Si la tache constate que le serveur n'a pas inscrit continuellement dans
     * le fichier still alive, cette variable est a true
     */
    Boolean rupture;
    Date debutRupture;
    Date finRupture;

    public TacheStillAlive() {
        super();
        try {
            rupture = false;
            // Chargement du fichier still alive.

          String varPath = PropertyLoader.loadProperti("serv.properties", "varpath");
                    
                    
            file = new File(varPath + "stillalive");
        } catch (IOException ex) {
            Logger.getLogger(TacheStillAlive.class.getName()).log(Level.SEVERE, null, ex);
            logger.error(ex);
        }
    }

    public TacheStillAlive(Observer s) {
        super(s);
        rupture = false;
        // Chargement du fichier still alive.
        Conf c = DAOFactory.getInstance().getDAOConf().getConfCourante();
        file = new File(c.getVarpath() + "stillalive");
    }

    //    public TacheStillAlive(File file, Observer s) {
    //        this(s);
    //        this.file = file;
    //        // Chargement du fichier still alive
    //        Conf c = DAOFactory.getInstance().getDAOConf().getConfCourante();
    //        file = new File(c.getVarpath() + "stillalive");
    //
    //        TacheStillAlive stillAlive = new TacheStillAlive(new File(c.getVarpath() + "stillalive"), this);
    //
    //    }
    @Override
    protected void callCorps() throws Exception {
        
            StillAlivePOJO alivePOJO = StillAlivePOJO.load(file);
            if (alivePOJO == null) {
                alivePOJO = new StillAlivePOJO();
            }
            rupture = alivePOJO.check(this.timeSchedule + 30);
            

            // SI il y a rupture on cherche les deux dates correspondat à l'interval d'innactivité
            if (rupture) {
                
                
                logger.debug("RUPTURE");
                List<Date[]> dates = alivePOJO.getAlive();

                logger.debug("size du alive : " + dates.size());
                Date[] d1 = dates.get(dates.size() - 1);
                Date[] d2 = dates.get(dates.size() - 2);

                debutRupture = d1[1];
                finRupture = d2[0];
                

                
                //----> Enregistrement de l'incident
                AbstrServiceCRUD service = ServiceCRUDFactory.getInstance().getServiceFor(AliveIncident.class);
                IncidentFactory<AliveIncident> facto = new IncidentFactory<AliveIncident>();
                AliveIncident inci = facto.getIncident(AliveIncident.class, "Il semble que le serveur ait arrété de fonctionner durant un laps de temps", null);
                inci.setDateDebut(debutRupture);
                inci.setDateFin(debutRupture);
                
                service.ajouter(inci);
                
                

            }
            else{
                logger.debug("OK");
            }

            alivePOJO.write(file);
    }

    
    
    
    
//    @Override
//    public TacheStillAlive call() throws Exception {
//        logger.debug("->RUN");
//        try {
//
//            StillAlivePOJO alivePOJO = StillAlivePOJO.load(file);
//            if (alivePOJO == null) {
//                alivePOJO = new StillAlivePOJO();
//            }
//            rupture = alivePOJO.check(this.timeSchedule + 30);
//            
//
//            // SI il y a rupture on cherche les deux dates correspondat à l'interval d'innactivité
//            if (rupture) {
//                
//                
//                logger.debug("RUPTURE");
//                List<Date[]> dates = alivePOJO.getAlive();
//
//                logger.debug("size du alive : " + dates.size());
//                Date[] d1 = dates.get(dates.size() - 1);
//                Date[] d2 = dates.get(dates.size() - 2);
//
//                debutRupture = d1[1];
//                finRupture = d2[0];
//                
//
//                
//                //----> Enregistrement de l'incident
//                AbstrServiceCRUD service = ServiceCRUDFactory.getInstance().getServiceFor(AliveIncident.class);
//                IncidentFactory<AliveIncident> facto = new IncidentFactory<AliveIncident>();
//                AliveIncident inci = facto.getIncident(AliveIncident.class, "Il semble que le serveur ait arrété de fonctionner durant un laps de temps", null);
//                inci.setDateDebut(debutRupture);
//                inci.setDateFin(debutRupture);
//                
//                service.ajouter(inci);
//                
//                
//
//            }
//            else{
//                logger.debug("OK");
//            }
//
//            alivePOJO.write(file);
//
//        } catch (Exception e) {
//            this.exeption = e;
//            logger.error(e);
//        } finally {
//            this.setChanged();
//            this.notifyObservers();
//            return this;
//        }
//
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    public Boolean getRupture() {
        return rupture;
    }

    public void setRupture(Boolean rupture) {
        this.rupture = rupture;
    }

    /**
     * *
     * début de la période d'innactivité du serveur constatée par la tâche
     *
     * @return
     */
    public Date getDebutRupture() {
        return debutRupture;
    }

    public void setDebutRupture(Date debutRupture) {
        this.debutRupture = debutRupture;
    }

    public Date getFinRupture() {
        return finRupture;
    }

    public void setFinRupture(Date finRupture) {
        this.finRupture = finRupture;
    }

//    @Override
//    public void fermerLesIncidentOuvert() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public AbstrIncident getIncidenOuvert() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public Class getTypeIncident() {
        return AliveIncident.class;
    }

    @Override
    public void gererIncident() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fermetureIncident() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
