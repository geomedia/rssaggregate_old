/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import rssagregator.beans.Conf;
import rssagregator.beans.Flux;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.AnomalieCollecte;
import rssagregator.dao.DAOFactory;

/**
 * Cette classe permet d'instancier le service de collecte du projet. Elle est
 * organisée autours de deux objets priomordiaux : le pool de tache schedulé qui
 * permet de lancer périodiquement les tache lié aux flux ; et le pool de tache
 * manuelle qui permet annectodiquement de lancer la mise à jour des flux
 *
 * @author clem
 */
public class ServiceCollecteur extends AbstrService {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceCollecteur.class);
//    ListeFluxCollecte fluxCollecte; On le récupère maintenant directement depuis le singleton de collecte
    private static ServiceCollecteur instance = new ServiceCollecteur();

    /**
     * *
     * Constructeur du singleton
     */
    private ServiceCollecteur() {
        super(Executors.newScheduledThreadPool(30));
        ThreadFactoryPrioitaire factoryPrioitaire = new ThreadFactoryPrioitaire();
        poolPrioritaire = Executors.newFixedThreadPool(30);
    }

    /**
     * *
     * Ce service est in singleton.
     *
     * @return
     */
    public static ServiceCollecteur getInstance() {
        if (ServiceCollecteur.instance == null) {
            ServiceCollecteur.instance = new ServiceCollecteur();
        }
        return ServiceCollecteur.instance;
    }
    /**
     * *
     * Le pool de thread permettant de lancer des récupération de flux en
     * passant devant le pool schedulé
     */
    private ExecutorService poolPrioritaire;

    /**
     * *
     * Update le service de récupération. Plusieurs observable sont gérés : <ul>
     * <li>Les flux : lors de l'ajout de la modification ou suppression, le
     * service doit être informé afin de recharger son pool de thread en
     * conséquence</li>
     * <li>Tache de récup : A la fin de l'éxecution d'une tâche de récupération,
     * le service est notifié. Il décide si il faut schedule la tache, cad le
     * remettre dans son pool. Si la tache est en échec (présence d'une
     * exeption), il faut appel au service de gestion des incidents</li>
     * <li>Conf</li>
     * </ul>
     * <p>Le deuxieme argument permet de préciser les actions au service. C'est
     * notamment utile lors de la modifiction ou ajour d'un flux. Les actions
     * suivantes doivent être gérée : <ul>
     * <li>add</li>
     * <li>mod</li>
     * <li>rem</li>
     * </li>reload all : permet de recharger completement le service</li>
     * </ul>
     *
     * </p>
     *
     *
     * @param o : L'observable se notifiant auprès du service (Flux, Conf,
     * Tache)
     * @param arg Une précision sur l'action : une chaine de caractère exemple :
     * add, mod, del.
     */
    @Override
    public void update(Observable o, Object arg) {


        /**
         * *========================================================================================
         * ........................Ajout ou modification d'un FLUX
         *///========================================================================================
        //Lorsque l'utilisateur modifi les flux, le service de collecte doit en être informé. 
        if (o instanceof Flux) {
            Flux flux = (Flux) o;
            // Si c'est flux a ajouter
            if (arg instanceof String && arg.equals("add")) {
                logger.debug("ajout d'un flux");

                // Si le flux est actif alors, on ajoute une tache schedulé dans le collecteur
                if (flux.getActive()) {
                    TacheRecupCallable tmpTache = new TacheRecupCallable(flux, this, true, true);
                    tmpTache.setTacheSchedule(true);
                    //TODO : Scheduler en fonction du temps restant. il faut modifier de deuxieme paramettre de la commande. 
                    this.executorService.schedule(tmpTache, flux.getMediatorFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
                    flux.setTacheRechup(tmpTache);
                    logger.debug("ajout de sa tâche");
                }
            }
            if (arg instanceof String && arg.equals("mod")) {
                logger.debug("modification d'un flux");
                /**
                 * Si le flux a été modifié, On annule sa tâche, on en crée une
                 * nouvelle et l'on l'envoie dans le scheduler.
                 */
                if (flux.getTacheRechup() != null) {
                    flux.getTacheRechup().setAnnulerTache(Boolean.TRUE);
                }
                //SI le flux est actif, on lui crée une nouvelle tâche que l'on réinjecte
                if (flux.getActive()) {
                    TacheRecupCallable tmpTache = new TacheRecupCallable(flux, this, true, true);
                    tmpTache.setTacheSchedule(true);
                    //TODO : Scheduler en fonction du temps restant. il faut modifier de deuxieme paramettre de la commande. 
                    this.executorService.schedule(tmpTache, flux.getMediatorFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
                    logger.debug("renouvellement de la tâche du flux modifié");
                    flux.setTacheRechup(tmpTache);
                }
            }
        }

        /**
         * *========================================================================================
         * .........................GESTION DU RETOUR DES TACHES SCHEDULE
         *///========================================================================================
        if (o instanceof AbstrTacheSchedule) {
            //-----------------------------------TACHE DE RECUPÉRATION DES FLUX-----------------------------
            if (o.getClass().equals(TacheRecupCallable.class)) {
                TacheRecupCallable tache = (TacheRecupCallable) o;
                logger.debug("reception du retour d'un flux");
                //Si c'est une tâche schedulé, on la réajoute 
                if (tache.getTacheSchedule() && !tache.annulerTache) {
                    executorService.schedule(tache, tache.getFlux().getMediatorFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
                }
                //Si il y a eu une exeption lors de l'execution de la tache. On fait fait gérer l'incident
                if (tache.getExeption() != null) {
                    AbstrIncident incident = ServiceGestionIncident.getInstance().gererIncident(tache.getExeption(), tache.getFlux());
                    tache.setIncident(incident);
                }
            } //---------------------------Tache générale de vérification de la capture
            else if (o.getClass().equals(TacheVerifComportementFluxGeneral.class)) {
                TacheVerifComportementFluxGeneral cast = (TacheVerifComportementFluxGeneral) o;
                if (cast.schedule) {
                    DateTime dtCurrent = new DateTime();
                    DateTime next = dtCurrent.plusDays(1).withHourOfDay(2);// withDayOfWeek(DateTimeConstants.SUNDAY);
                    Duration dur = new Duration(dtCurrent, next);
                    executorService.schedule(cast, dur.getStandardSeconds(), TimeUnit.SECONDS);
                }
            } //------------------------Tache de vérification de la capture pour un flux
            else if (o.getClass().equals(TacheVerifComportementFLux.class)) {
                TacheVerifComportementFLux cast = (TacheVerifComportementFLux) o;
                if (cast.getExeption() == null) {
                    if (cast.getAnomalie()) { // Si la tache a déterminée une annomalie de capture
                        AnomalieCollecte anomalie = new AnomalieCollecte();
                        anomalie.setDateDebut(new Date());
                        anomalie.setFluxLie(cast.getFlux());
                        anomalie.feedWithTask(cast);
                        try {
                            DAOFactory.getInstance().getDaoFromType(AnomalieCollecte.class).creer(anomalie);
                        } catch (Exception ex) {
                            Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            gererIncident((AbstrTacheSchedule) o);
        }

        /**
         * ========================================================================================
         * ............BLOC PERMETTANT LE RECHARGEMENT COMPLET DU SERVICE.
         *///======================================================================================
        // Si l'élément Observable est la conf ou si On a donnée l'ordre reload all
        if ((o instanceof Conf) || (o == null && arg instanceof String && arg.equals("reload all"))) {
            logger.info("Rechargement complet du Service de Collecte");
            // On va recharger tout le Pool Schedulé
            Integer nbThread = DAOFactory.getInstance().getDAOConf().getConfCourante().getNbThreadRecup();
            // On tue les tache en cours
            List<Runnable> listFutur = this.executorService.shutdownNow();

            // On recréer le Pool
            this.executorService = Executors.newScheduledThreadPool(nbThread);

            List<Flux> listFlux = DAOFactory.getInstance().getDAOFlux().findAllFlux(true);
            System.out.println("NBR DE FLUX : " + listFlux.size());

            // On inscrit les taches actives au pool schedule
            int i;
            for (i = 0; i < listFlux.size(); i++) {
                if (listFlux.get(i).getActive()) {
                    // On schedule
                    TacheRecupCallable tmpTache = new TacheRecupCallable(listFlux.get(i), this, true, true);
                    listFlux.get(i).setTacheRechup(tmpTache);
                    //TODO : Scheduler en fonction du temps restant. il faut modifier de deuxieme paramettre de la commande. 
                    this.executorService.schedule(tmpTache, listFlux.get(i).getMediatorFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
                }
            }
        }




//        if (o == null && arg instanceof String && arg.equals("reload all")) {
//
//            Integer nbThread = DAOFactory.getInstance().getDAOConf().getConfCourante().getNbThreadRecup();
//            // On tue les tache en cours
//            List<Runnable> listFutur = this.poolSchedule.shutdownNow();
//
//            // On recréer le Pool
//            this.poolSchedule = Executors.newScheduledThreadPool(nbThread);
//            
//            // On inscrit chaque flux actif au pool
//            List<Flux> listFlux = DAOFactory.getInstance().getDAOFlux().findAllFlux(true);
//            int i;
//            for (i = 0; i < listFlux.size(); i++) {
//                if (listFlux.get(i).getActive()) {
//                    // On schedule
//                    TacheRecupCallable tmpTache = new TacheRecupCallable(listFlux.get(i), true, true);
//                    listFlux.get(i).setTacheRechup(tmpTache);
//                    //TODO : Scheduler en fonction du temps restant. il faut modifier de deuxieme paramettre de la commande. 
//                    this.poolSchedule.schedule(tmpTache, listFlux.get(i).getMediatorFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
//                }
//            }
//
//            logger.info("Rechargement du Service de Collecte");
//        }

        // Si l'observable notifiant est la DAO FLUX. Il faut recréer le pool avec la liste des nouveau flux à suivre
//        if (o instanceof DaoFlux || o instanceof DAOConf) {
//
//
//
//            if (DAOFactory.getInstance().getDAOConf().getConfCourante().getActive()) {
//
//                // On supprime de scheduler pour le rechrer avec la liste de nouvelles tâches
//                Integer nbThread = DAOFactory.getInstance().getDAOConf().getConfCourante().getNbThreadRecup();
//
//                // On tue les tache en cours
//                List<Runnable> listFutur = this.poolSchedule.shutdownNow();
//
//                this.poolSchedule = Executors.newScheduledThreadPool(nbThread);
//
//                List<Flux> listFlux = DAOFactory.getInstance().getDAOFlux().findAllFlux(true);
//                System.out.println("NBR DE FLUX : " + listFlux.size());
//
//                // On inscrit les taches actives au pool schedule
//                int i;
//                for (i = 0; i < listFlux.size(); i++) {
//                    if (listFlux.get(i).getActive()) {
//                        // On schedule
////                        listFlux.get(i).createTask();
//
//                        TacheRecupCallable tmpTache = new TacheRecupCallable(listFlux.get(i));
//                        tmpTache.setTacheSchedule(true);
//
//
//
//
//                        //TODO : Scheduler en fonction du temps restant. il faut modifier de deuxieme paramettre de la commande. 
////                        this.poolSchedule.schedule(tmpTache, listFlux.get(i).getPeriodiciteCollecte(), TimeUnit.SECONDS);
//                        this.poolSchedule.schedule(tmpTache, listFlux.get(i).getMediatorFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
//                    }
//                }
//            } else {
//                poolSchedule.shutdown();
//            }
//        }


//        if (o instanceof ListeFluxCollecteEtConfigConrante) {
//
//
////            ListeFluxCollecteEtConfigConrante.getInstance().chargerDepuisBd();
//
//            // Si la config est active
//            if (fluxCollecte.getConfCourante().getActive()) {
//
//                // On supprime de scheduler pour le rechrer avec la liste de nouvelles tâches
//                Integer nbThread = ListeFluxCollecteEtConfigConrante.getInstance().getConfCourante().getNbThreadRecup();
////                this.poolSchedule.shutdown();
//
//                this.poolSchedule.shutdownNow();
//                this.poolSchedule.shutdown();
//
//
//                this.poolSchedule = Executors.newScheduledThreadPool(nbThread);
//
////                List<Flux> tmpList = fluxCollecte.findAllFlux();
//                List<Flux> tmpList = DAOFactory.getInstance().getDAOFlux().findAllFlux();
//
//
//                // On inscrit les taches actives au pool
//                int i;
//                for (i = 0; i < tmpList.size(); i++) {
//                    //Si le flux est actif, on l'ajoute au scheduler
//                    if (tmpList.get(i).getActive()) {
//                        // On schedule
//                        tmpList.get(i).createTask();
//                        TacheRecup tmpTache = tmpList.get(i).getTacheRechup();
//                        //TODO : Scheduler en fonction du temps restant. il faut modifier de deuxieme paramettre de la commande. 
//                        this.poolSchedule.scheduleAtFixedRate(tmpTache, 0, tmpList.get(i).getPeriodiciteCollecte(), TimeUnit.SECONDS);
//                    }
//                }
//            } else {
//                stopCollecte();
//            }
//            System.out.println("###### Rechargement du collecteur Terminé ######");
//        }
    }

    /**
     * *
     * Stope le service de collecte en fermant proprement les deux pool de
     * tâches de collecte
     */
//    public void stopCollecte() {
//        // Fermeture du scheduler
//        this.executorService.shutdownNow();
//        this.poolPrioritaire.shutdownNow();
//    }
    /**
     * *
     * Cette méthode n'est maintenant plus utilisée au profit de majManuellAll()
     *
     * @param flux
     * @throws Exception
     * @deprecated
     */
    @Deprecated
    public void majManuelle(Flux flux) throws Exception {
        System.out.println("");
        TacheRecupCallable task = new TacheRecupCallable(flux, this, false, true);

        Future<TacheRecupCallable> t = this.poolPrioritaire.submit(task);

        t.get(30, TimeUnit.SECONDS);
        // A la fin de la tache, il faut rafraichir le context objet et la base de donnée.
//            DAOFactory.getInstance().getEntityManager().refresh(flux);
    }

    /**
     * *
     * Cette méthode lance la mise à jour manuelle de chacun des flux envoyés en
     * parametres
     *
     * @param listFlux Liste de flux pour lequels il faut lancer une mise à jour
     * manuelle
     * @throws Exception
     */
    public void majManuellAll(List<Flux> listFlux) throws Exception {
        int i;
        List<TacheRecupCallable> listTache = new ArrayList<TacheRecupCallable>();
        for (i = 0; i < listFlux.size(); i++) {
            TacheRecupCallable task = new TacheRecupCallable(listFlux.get(i), this, false, true);
            listTache.add(task);
            listFlux.get(i).setTacheRechupManuelle(task);
        }
        DateTime dtDebut = new DateTime();
        List<Future<TacheRecupCallable>> listFutur = this.poolPrioritaire.invokeAll(listTache);
    }

    /**
     * *
     * Permet d'ajouter un callable au pool schedulé. La méthode
     * scheduleAtFixedRate ne permet pas d'ajouter des Callable, seulement des
     * runnable. Pour cette raison, les renable doivent se réajouter en fin de
     * tache pour avoir un effet scheduleAtFixedRate
     *
     * @param t Le RUNNABLE qui doit être ajouté au pool
     */
//    public void addScheduledCallable(TacheRecupCallable t) {
////        this.poolSchedule.schedule(t, t.getFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
//        this.executorService.schedule(t, t.getFlux().getMediatorFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
//    }
    /**
     * *
     * Retoune le pool prioritaire du service. Il s'agit du pool pour lancer des
     * collectes manuelle. Celles ci sont lancée avec une priorité suppréieure
     * au pool schedulé
     *
     * @return
     */
    public ExecutorService getPoolPrioritaire() {
        return poolPrioritaire;
    }

    /**
     * *
     * Définir le pool prioritaire
     *
     * @param poolPrioritaire
     */
    public void setPoolPrioritaire(ExecutorService poolPrioritaire) {
        this.poolPrioritaire = poolPrioritaire;
    }

    @Override
    public void instancierTaches() {

        //Il doit commencer par charger la conf 
        Conf conf = DAOFactory.getInstance().getDAOConf().getConfCourante();
        update(conf, null);

        //---------------TACHES DE COLLECTE--------------

        List<Flux> listf = DAOFactory.getInstance().getDAOFlux().findAllFlux(Boolean.TRUE);
        for (int i = 0; i < listf.size(); i++) {
            Flux flux = listf.get(i);
            update(flux, "add");
        }

        //----------------TACHE TacheVerifComportementFluxGeneral
        TacheVerifComportementFluxGeneral comportementFluxGeneral = new TacheVerifComportementFluxGeneral(this);
        DateTime dtCurrent = new DateTime();
        DateTime next = dtCurrent.plusDays(1).withHourOfDay(2);// withDayOfWeek(DateTimeConstants.SUNDAY);
        Duration dur = new Duration(dtCurrent, next);
        this.executorService.schedule(comportementFluxGeneral, dur.getStandardSeconds(), TimeUnit.SECONDS);

    }

    @Override
    protected void gererIncident(AbstrTacheSchedule tache) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stopService() throws SecurityException, RuntimeException {
        this.poolPrioritaire.shutdownNow();
        this.executorService.shutdownNow();

    }
}
