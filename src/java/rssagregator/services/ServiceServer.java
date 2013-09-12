/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.Conf;
import rssagregator.beans.Flux;
import rssagregator.beans.incident.AliveIncident;
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
public class ServiceServer extends AbstrService implements Runnable {

    /**
     * *
     * Boolen critère de la boucle infinie du daemon. tant qu'il est vrai le
     * daemon boucle
     */
    private Boolean isStart;
    private ServiceCollecteur serviceCollecteur;
    private ServiceSynchro serviceJMS;
    private static ServiceServer instance = new ServiceServer(true, 10000);
    private ServiceMailNotifier serviceMail;
    /**
     * Permet de lancer des tâches tel que l'écriture dans le fichier "still
     * alive";
     */
//    ScheduledExecutorService executorServiceAdministratif;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceServer.class);
    /**
     * Durée d'attente dans la boucle du daemon en milisecodnes
     */
    private Integer daemonTime;

    private ServiceServer(Boolean isStart, Integer daemonTime) {
        this.isStart = isStart;
        this.daemonTime = daemonTime;
    }

    public static ServiceServer getInstance() {
        if (instance == null) {
            instance = new ServiceServer(true, 10000);
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
        executorService.shutdownNow();
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
//            executorServiceAdministratif = Executors.newFixedThreadPool(10);
            executorService = Executors.newScheduledThreadPool(10);

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

            serviceJMS = ServiceSynchro.getInstance();
            serviceJMS.instancierTaches();
//            executorServiceAdministratif.submit(serviceJMS);


            serviceMail = ServiceMailNotifier.getInstance();
            serviceMail.instancierTaches();

        instancierTaches();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (isStart) {
            try {
                Thread.sleep(daemonTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
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

    @Override
    public void instancierTaches() {
        Conf c = DAOFactory.getInstance().getDAOConf().getConfCourante();
        System.out.println("=======================================");
        System.out.println("VAR PATH : " + c.getVarpath());
        System.out.println("=======================================");


        TacheStillAlive stillAlive = new TacheStillAlive(new File(c.getVarpath() + "stillalive"), this);
        stillAlive.setSchedule(true);
        executorService.submit(stillAlive);

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    /**
     * *
     * Les tâche gérée par le service centrales se notifient par cette methode.
     * tache : <ul>
     * <li>steel alive <li>
     * </ul>
     */
    public void update(Observable o, Object arg) {


        if (o instanceof AbstrTacheSchedule) {


            if (o.getClass().equals(TacheStillAlive.class)) {
                TacheStillAlive cast = (TacheStillAlive) o;
                if (cast.getRupture()) {
                    AliveIncident incident = new AliveIncident();
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMMM yyyy à hh'h'mm");
                    incident.setMessageEreur("Il semble que l'application n'était pas ouverte entre : " + fmt.print(new DateTime(cast.getDebutRupture())) + " et : " + fmt.print(new DateTime(cast.getFinRupture())));

                }
                if (cast.getSchedule()) {
                    executorService.schedule(cast, 15, TimeUnit.SECONDS);
                }
            }
            gererIncident((AbstrTacheSchedule) o);
        }

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void gererIncident(AbstrTacheSchedule tache) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
