/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jdom.JDOMException;
import rssagregator.beans.Conf;
import rssagregator.dao.DAOConf;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import rssagregator.services.ServiceServer;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.ServiceMailNotifier;
import rssagregator.services.ServiceSynchro;
import rssagregator.utils.ServiceXMLTool;

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
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        }


        DAOFactory.getInstance();
        DAOConf daoconf = DAOFactory.getInstance().getDAOConf();
        try {
            daoconf.charger();
        } catch (IOException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        // On charge la conf et on l'enregistre auprès du service de 
        Conf conf = daoconf.getConfCourante();
//        conf.enregistrerAupresdesService();

        // -----------------Chargement des flux
        DaoFlux daoflux = DAOFactory.getInstance().getDAOFlux();
        daoflux.chargerDepuisBd();


        try {
            daoconf.verifRootAccount();

            //            executorServiceAdministratif.submit(serviceJMS);
        } catch (IOException ex) {
            Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
        }




        //============================================================================
        //...........Instanciation des services en utilisant le fichier de conf XML
        //============================================================================
        try {
            ServiceXMLTool.instancierServiceEtTache();
        } catch (IOException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Lancement de la collecte
        ServiceCollecteur.getInstance().lancerCollecte();

//        daemonCentral = ServiceServer.getInstance();
//        daemonCentral.instancierTaches();
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
          // Il faut fermer chacun des services.
        ServiceCollecteur.getInstance().stopService();
        ServiceServer.getInstance().stopService();
        ServiceMailNotifier.getInstance().stopService();
        ServiceSynchro.getInstance().stopService();

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
