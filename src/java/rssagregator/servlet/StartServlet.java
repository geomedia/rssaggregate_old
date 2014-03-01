/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import rssagregator.beans.Conf;
import rssagregator.dao.DAOConf;
import rssagregator.dao.DAOFactory;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.ServiceMailNotifier;
import rssagregator.services.ServiceServer;
import rssagregator.utils.PropertyLoader;
import rssagregator.utils.ServiceXMLTool;

/**
 * Servlet utilisé au démarrage de l'application pour lancer les service. Elle est aussi chargé de les clore à la
 * fermeture de l'application.
 *
 * @author clem
 */
public class StartServlet implements ServletContextListener {

    private static final String ATT_LIST_FLUX = "listflux";
//    private ListeFluxCollecteEtConfigConrante listflux;
    private static final String ATT_SERVICE_COLLECTE = "collecte";
//    private ServiceCollecteur collecte;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(StartServlet.class);
    ServiceServer daemonCentral;

    /**
     * *
     * Méthode lancée au démarrage de l'application. permet d'initialiser les différents services
     *
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String confpath = (String) PropertyLoader.loadFromContext("confpath");
        System.setProperty("confpath", confpath);


        // Le répertoire web est parfois difficile a retrouver. On le définit comme une variable system afin de pouvoir le retrouver dans n'importe quelle classe non servlet.
        ServletContext servletContext = sce.getServletContext();
        String webDir = servletContext.getRealPath(File.separator);
        System.setProperty("webdir", webDir);


        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        }



        try {
            Thread.sleep(3000);
            DAOFactory.getInstance();

        } catch (Exception e) {
            logger.debug("ERREUR DAO FACTORY", e);
        }



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
        } //        catch (IOException ex) {
        //            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        //        } catch (JDOMException ex) {
        //            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        //        } catch (ClassNotFoundException ex) {
        //            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        //        } catch (NoSuchMethodException ex) {
        //            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        //        } catch (InstantiationException ex) {
        //            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        //        } catch (IllegalAccessException ex) {
        //            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        //        } catch (IllegalArgumentException ex) {
        //            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        //        } catch (InvocationTargetException ex) {
        //            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        //        } catch (NamingException ex) {
        //            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        //        } catch (RessourceIntrouvable ex) {
        //            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        //        }
        catch (Exception e) {
            logger.error("Erreur lors de l'instanciation des service depuis servicedef.xml ", e);
//            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, e);
        }

        // Lancement de la collecte
        ServiceCollecteur.getInstance().lancerService();
        ServiceMailNotifier.getInstance().lancerService();
//        ServiceSynchro.getInstance().lancerService();
        ServiceServer.getInstance().lancerService();


    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // On arrete les taches de collecte
        logger.debug("Fermeture de l'Application");
        // Il faut fermer chacun des services.
        ServiceCollecteur.getInstance().stopService();
        ServiceServer.getInstance().stopService();
        ServiceMailNotifier.getInstance().stopService();
//        ServiceSynchro.getInstance().stopService();

//        es.shutdown();
//        logger.debug("[OK] Fermeture du daemoncentral");

        destroyDriver();
        logger.debug("[OK] Déenregistremnet des driver JDBC");
        logger.debug("[OK] Fin de la procédure de fermeture");
    }

    public void destroyDriver() {



        String prefix = getClass().getSimpleName() + " destroy() ";

        DAOFactory.getInstance().closeEMF();

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
