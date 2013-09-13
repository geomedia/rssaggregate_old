/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import rssagregator.services.ServiceServer;
import rssagregator.services.ServiceCollecteur;

/**
 *
 * @author clem
 */
public class StartServlet implements ServletContextListener {

    private static final String ATT_LIST_FLUX = "listflux";
//    private ListeFluxCollecteEtConfigConrante listflux;
    private static final String ATT_SERVICE_COLLECTE = "collecte";
    private ServiceCollecteur collecte;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(StartServlet.class);
    ServiceServer daemonCentral;

    /**
     * *
     * Méthode lancée au démarrage de l'application. permet d'initialiser les
     * différents services
     *
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //On lance le daemon central avec une périodicité de 5 minutes 300 000 milisecondes
        daemonCentral = ServiceServer.getInstance();
        daemonCentral.instancierTaches();
//        es = Executors.newFixedThreadPool(1);
//        es.submit(daemonCentral);
//        daemonCentral.instancierTaches();

//        daemonCentral.run();

//        logger.info("Chargement du context initial");
//        try {
//            Class.forName("com.mysql.jdbc.Driver");

//            DAOFactory.getInstance();
//
//            DaoFlux daoflux = DAOFactory.getInstance().getDAOFlux();
//            DAOConf daoconf = DAOFactory.getInstance().getDAOConf();
//
//            daoflux.chargerDepuisBd();
//
//            try {
//                daoconf.charger();
//            } catch (IOException ex) {
//                Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (Exception ex) {
//                Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            // Initialisation du  collecteur
//            collecte = ServiceCollecteur.getInstance();
//
//            // On charge la conf et on notifi le collecteur
//            Conf conf = daoconf.getConfCourante();
//            conf.addObserver(collecte);
//            conf.forceNotifyObserver();
//
//            // Pour chaque flux, on inscrit le collecteur comme observeur
//            List<Flux> listflux = daoflux.findAllFlux(true);
//            int i;
//            for (i = 0; i < listflux.size(); i++) {
//                listflux.get(i).addObserver(collecte);
//                listflux.get(i).forceNotifyObserver();
//            }
//
//            //TODO : On sésactive JMS pour l'instant
//            ServiceJMS jMS = ServiceJMS.getInstance();
//            ExecutorService es = Executors.newFixedThreadPool(1);
//            es.submit(jMS);
//            
//
//
//
//            // On enregistre les service comme observer et la liste des flux comme observable
////            listflux.addObserver(collecte);
////            daoflux.addObserver(collecte);
//
//
//
//            // On charge la liste des flux depuis la base de donnée
////            daoflux.chargerDepuisBd();
//
//
////            daoconf.chargerDepuisBd();
//
////            try {
////                daoconf.charger();
////                
////    //            Object fl = daoflux.find(new Long(451));
////    //            DAOFactory.getInstance().getEntityManager().;
////    //            DAOFactory.getInstance().getEntityManager().;
////            } catch (IOException ex) {
////                Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
////            } catch (Exception ex) {
////                Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
////            }
//
//
//
//            //        listflux.chargerDepuisBd();
////            daoflux.notifyObservers();
////            daoconf.notifyObservers();
//
//
//
////            try {
//////                ServiceJMS.getInstance().startService();
////            } catch (IOException ex) {
////                Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
////            }
//
//
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // On arrete les taches de collecte
        logger.debug("Fermeture de l'Application");
        daemonCentral.stopService();
        
//        es.shutdown();
//        logger.debug("[OK] Fermeture du daemoncentral");

        destroyDriver();
        logger.debug("[OK] Déenregistremnet des driver JDBC");
        logger.debug("[OK] Fin de la procédure de fermeture");
    }

    public void destroyDriver() {
        String prefix = getClass().getSimpleName() + " destroy() ";


        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                DriverManager.deregisterDriver(drivers.nextElement());
            }
        } catch (Exception e) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Exception caught while deregistering JDBC drivers");
//        ctx.log(prefix + "Exception caught while deregistering JDBC drivers", e);
        }




    }
}
