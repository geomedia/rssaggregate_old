/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import rssagregator.services.tache.AbstrTache;

/**
 * Cette classe permet de gérer le lancement et le maintient des services de
 * l'application. Tout les x seconde elle écrit dans un fichier pour signifier
 * que le serveur est toujours en vie. Elle vérifié l'état de laconnection JMS.
 *
 * @author clem
 */
public class ServiceServer extends ServiceImpl {

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

//    public ServiceServer(ScheduledExecutorService executorService1) {
//        super(executorService1);
//    }
    private ServiceServer(Boolean isStart, Integer daemonTime) {
        super();
//        this(Executors.newSingleThreadScheduledExecutor());
        this.isStart = isStart;
    }

    public static ServiceServer getInstance() {
        if (instance == null) {
            instance = new ServiceServer(true, 10000);
        }
        return instance;
    }

    /**
     * Démarre le service. Commence par charger la conf et des informations dans
     * la base de données avant de lancer les services (Collecteur, JMS pour la
     * sync, mail). termine sur une boucle infinie permettant d'écrire toute les
     * x seconces dans un fichier still alive
     */
//    @Override
//    public void run() {
//      
//        while (isStart) {
//            try {
//                Thread.sleep(daemonTime);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//
//    }
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

    /**
     * *
     * Lance les autres services de l'application : <ul>
     * <li>ServiceMail</li>
     * <li>ServiceCollecte</li>
     * <li>ServiceSynchro</li>
     * </ul>
     * Ainsi que les tâches propres au ServiceServeur : <ul>
     * <li>StillAlive</li>
     * <li>...</li>
     * </ul>
     */
//    @Override
//    public void instancierTaches() {
//        logger.info("Lancement des services");
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//
//            //On initialise initialise le pool du service
//            executorService = Executors.newScheduledThreadPool(10);
//
//            // On initialise la DAO FACTORY
//            DAOFactory.getInstance();
//
//            //-------------------CHARGEMENT DE LA CONF
//
//            DAOConf daoconf = DAOFactory.getInstance().getDAOConf();
//            try {
//                daoconf.charger();
//            } catch (IOException ex) {
//                Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (Exception ex) {
//                Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            // On charge la conf et on notifi le collecteur
//            Conf conf = daoconf.getConfCourante();
//            conf.enregistrerAupresdesService();
//
//
//            // -----------------Chargement des flux
//            DaoFlux daoflux = DAOFactory.getInstance().getDAOFlux();
//            daoflux.chargerDepuisBd();
//
//            // On demande au collecteur de charger chacun des flux. On ne peut pas passer par le classique notifiObserver car les flux sont aussi enregistré au service JMS qui lui ne doit pas être avertis à ce moment
////            List<Flux> listflux = daoflux.findAllFlux(true);
////            for (int i = 0; i < listflux.size(); i++) {
////                listflux.get(i).enregistrerAupresdesService();
////                listflux.get(i).forceChangeStatut();
////                // Il ne faut pour une fois notifier que le service de collecte pas de service JMS
////                ServiceCollecteur.getInstance().update(listflux.get(i), "add");
////            }
//            
//            
////            try {
////                ServiceXMLTool.instancierServiceEtTache();
////            } catch (IOException ex) {
////                Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
////            } catch (JDOMException ex) {
////                Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
////            } catch (NoSuchMethodException ex) {
////                Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
////            } catch (InstantiationException ex) {
////                Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
////            } catch (IllegalAccessException ex) {
////                Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
////            } catch (IllegalArgumentException ex) {
////                Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
////            } catch (InvocationTargetException ex) {
////                Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
////            }
//            
//            
//            
//
////            // Instanciation des services
////            serviceCollecteur = ServiceCollecteur.getInstance();
////            serviceJMS = ServiceSynchro.getInstance();
////            serviceMail = ServiceMailNotifier.getInstance();
////
////            //Lancement des service
////
////            serviceCollecteur.lancerCollecte();
////            serviceJMS.lancerCollecte();
////            serviceMail.lancerCollecte();
//            
//            
//            try {
//                daoconf.verifRootAccount();
//
//                //            executorServiceAdministratif.submit(serviceJMS);
//            } catch (IOException ex) {
//                Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (Exception ex) {
//                Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            
//            
//            
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(StartServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        Conf c = DAOFactory.getInstance().getDAOConf().getConfCourante();
//        TacheStillAlive stillAlive = new TacheStillAlive(this);
//        stillAlive.setSchedule(true);
//        executorService.submit(stillAlive);
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    
    
    
//    @Override
//    /**
//     * *
//     * Les tâche gérée par le service centrales se notifient par cette methode.
//     * tache : <ul>
//     * <li>steel alive <li>
//     * </ul>
//     */
//    public void update(Observable o, Object arg) {
//        if (o instanceof AbstrTache) {
//            if (o.getClass().equals(TacheStillAlive.class)) {
//                TacheStillAlive cast = (TacheStillAlive) o;
//                if (cast.getRupture()) {
//                    AliveIncident incident = new AliveIncident();
//                    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMMM yyyy à hh'h'mm");
//                    incident.setMessageEreur("Il semble que l'application n'était pas ouverte entre : " + fmt.print(new DateTime(cast.getDebutRupture())) + " et : " + fmt.print(new DateTime(cast.getFinRupture())));
//
//                }
//                if (cast.getSchedule()) {
//                    schedule(cast);
//                }
//            }
//            else if(o.getClass().equals(TacheDetectDeadLock.class)){
//                TacheDetectDeadLock cast = (TacheDetectDeadLock)o;
//                schedule(cast);
//                
//                
//            }
//            
////            gererIncident((AbstrTache) o);
//        }
//
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    

    @Override
    public void stopService() throws RuntimeException, SecurityException {
        //On ferme les services
//        logger.info("Fermeture des Service");
//        try {
//            serviceCollecteur.stopService();
//            logger.debug("[OK] Fin du Processus de collecte");
//        } catch (Exception e) {
//            logger.error("Echec de la fermeture du Service de collecte : " + e);
//        }
//        
//        try {
//            serviceJMS.stopService();
//            logger.debug("[OK] Fin du service de Synchro");
//        } catch (Exception e) {
//            logger.error("Echec lors de la fermeture du service de Synchro : " + e);
//        }
//        
//        
//        try {
//            serviceMail.stopService();
//            logger.debug("[OK] FIN de service Mail");
//        } catch (Exception e) {
//            logger.error("Echec de la fermeture du Service MAIL : " + e);
//        }



        //On ferme le daemon
        this.isStart = false;
        super.stopService();

    }

    @Override
    public void lancerService() {
        super.lancerService(); //To change body of generated methods, choose Tools | Templates.
        SemaphoreCentre lancementTache = SemaphoreCentre.getinstance();
        this.executorServiceAdministratif.submit(lancementTache);
        
        
    }
    
    
    
}
