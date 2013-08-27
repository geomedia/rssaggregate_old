/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.Conf;
import rssagregator.beans.Flux;
import rssagregator.dao.DAOConf;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import rssagregator.servlet.StartServlet;

/**
 * Cette classe permet de gérer le lancement et le maintient des services de
 * l'application. Tout les x seconde elle écrit dans un fichier pour signifier
 * que le serveur est toujours en vie. Elle vérifié l'état de laconnection JMS.
 *
 * @author clem
 */
public class DaemonCentral implements Runnable {

    /**
     * *
     * Boolen critère de la boucle infinie du daemon. tant qu'il est vrai le
     * daemon boucle
     */
    private Boolean isStart;
    private ServiceCollecteur serviceCollecteur;
    private ServiceJMS serviceJMS;
    private static DaemonCentral instance = new DaemonCentral(true, 10000);
    /**
     * Permet de lancer des tâches tel que l'écriture dans le fichier "still alive";
     */
    ExecutorService executorServiceAdministratif;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DaemonCentral.class);
    /**
     * Durée d'attente dans la boucle du daemon en milisecodnes
     */
    private Integer daemonTime;

    private DaemonCentral(Boolean isStart, Integer daemonTime) {
        this.isStart = isStart;
        this.daemonTime = daemonTime;
    }

    public static DaemonCentral getInstance() {
        if (instance == null) {
            instance = new DaemonCentral(true, 10000);
        }
        return instance;
    }

    /**
     * *
     * Demande au daemon de fermer tous les services lancés
     */
    public void stop() {
        //On ferme les services
        serviceCollecteur.stopCollecte();
        logger.debug("[OK] Fin du Processus de collecte");
        serviceJMS.close();
        logger.debug("[OK] Fin du service JMS");

        //On ferme le daemon
        this.isStart = false;
        executorServiceAdministratif.shutdownNow();
        logger.debug("[OK] Fermeture des tâches administratives");
    }

    /**
     * Démarre le service. Commence par charger la conf et des informations dans
     * la base de données avant de lancer les services (Collecteur, JMS pour la
     * sync, mail). termine sur une boucle infinie permettant d'écrire toute les
     * x seconces dans un fichier still alive
     */
    @Override
    public void run() {
        logger.info("Chargement du context initial");
        try {
            Class.forName("com.mysql.jdbc.Driver");

            //On initialise l'executor du daemon
            executorServiceAdministratif = Executors.newFixedThreadPool(10);


            DAOFactory.getInstance();

            DaoFlux daoflux = DAOFactory.getInstance().getDAOFlux();
            DAOConf daoconf = DAOFactory.getInstance().getDAOConf();

            daoflux.chargerDepuisBd();

            try {
                daoconf.charger();
            } catch (IOException ex) {
                Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Initialisation du  collecteur
            serviceCollecteur = ServiceCollecteur.getInstance();

            // On charge la conf et on notifi le collecteur
            Conf conf = daoconf.getConfCourante();
            conf.addObserver(serviceCollecteur);
            conf.forceNotifyObserver();

            // On demande au collecteur de charger chacun des flux. On ne peut pas passer par le classique notifiObserver car les flux sont aussi enregistré au service JMS qui lui ne doit pas être avertis à ce moment
            List<Flux> listflux = daoflux.findAllFlux(true);
            int i;
            for (i = 0; i < listflux.size(); i++) {
                listflux.get(i).enregistrerAupresdesService();
                listflux.get(i).forceChangeStatut();
                // Il ne faut pour une fois notifier que le service de collecte pas de service JMS
                ServiceCollecteur.getInstance().update(listflux.get(i), "add");
            }

            serviceJMS = ServiceJMS.getInstance();
            executorServiceAdministratif.submit(serviceJMS);



        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (isStart) {
            try {
                Thread.sleep(daemonTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(DaemonCentral.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }

    /**
     * *
     * Boolen critère de la boucle infinie du daemon. tant qu'il est vrai le
     * daemon boucle
     *
     * @return
     */
    public Boolean getIsStart() {
        return isStart;
    }

    /**
     * *
     * Boolen critère de la boucle infinie du daemon. tant qu'il est vrai le
     * daemon boucle
     *
     * @param isStart
     */
    public void setIsStart(Boolean isStart) {
        this.isStart = isStart;
    }
}
