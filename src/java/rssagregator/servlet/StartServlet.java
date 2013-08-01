/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import rssagregator.dao.DAOConf;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.ServiceJMS;

/**
 *
 * @author clem
 */
public class StartServlet implements ServletContextListener {

    private static final String ATT_LIST_FLUX = "listflux";
//    private ListeFluxCollecteEtConfigConrante listflux;
    private static final String ATT_SERVICE_COLLECTE = "collecte";
    private ServiceCollecteur collecte;

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
            System.out.println("DEBUTT");
            
            DAOFactory.getInstance();
            
            DaoFlux daoflux = DAOFactory.getInstance().getDAOFlux();
            DAOConf daoconf = DAOFactory.getInstance().getDAOConf();
            System.out.println("");
            
            // Initialisation de la liste des flux.
//            listflux = ListeFluxCollecteEtConfigConrante.getInstance();

            // Initialisation du  collecteur
            collecte = ServiceCollecteur.getInstance();

            // On enregistre les service comme observer et la liste des flux comme observable
//            listflux.addObserver(collecte);
            daoflux.addObserver(collecte);
            daoconf.addObserver(collecte);


            
            // On charge la liste des flux depuis la base de donnée
//            daoflux.chargerDepuisBd();
            
            
//            daoconf.chargerDepuisBd();
            
            try {
                daoconf.charger();
                
    //            Object fl = daoflux.find(new Long(451));
    //            DAOFactory.getInstance().getEntityManager().;
    //            DAOFactory.getInstance().getEntityManager().;
            } catch (IOException ex) {
                Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            

            daoflux.forceNotifyObserver();
            daoconf.forceNotifyObservers();
            //        listflux.chargerDepuisBd();
//            daoflux.notifyObservers();
//            daoconf.notifyObservers();
            
            
            
              ServiceJMS jMS = ServiceJMS.getInstance();
//            ServiceJMS.getInstance().run();
            
            ExecutorService es = Executors.newFixedThreadPool(1);
            es.submit(jMS);
            
            
            
            
//            try {
////                ServiceJMS.getInstance().startService();
//            } catch (IOException ex) {
//                Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
//            }
            

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // On arrete les taches de collecte

        collecte.stopCollecte();
        ServiceJMS.getInstance().close();

        destroyDriver();
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
        
        ServiceJMS.getInstance().close();
        

    }
}
