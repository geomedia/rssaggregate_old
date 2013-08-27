/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.joda.time.DateTime;
import rssagregator.beans.Conf;
import rssagregator.beans.Flux;
import rssagregator.beans.TacheRecupCallable;
import rssagregator.dao.DAOFactory;

/**
 * Cette classe permet d'instancier le service de collecte du projet. Elle est
 * organisé autours de deux objets priomordiaux : le pool de tache schedulé qui
 * permet de lancer périodiquement les tache lié aux flux ; et le pool de tache
 * manuelle qui permet annectodiquement de lancer la mise à jour des flux
 *
 * @author clem
 */
public class ServiceCollecteur implements Observer {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceCollecteur.class);
//    ListeFluxCollecte fluxCollecte; On le récupère maintenant directement depuis le singleton de collecte
    private static ServiceCollecteur instance = new ServiceCollecteur();

    /**
     * *
     * Constructeur du singleton
     */
    private ServiceCollecteur() {
        poolSchedule = Executors.newScheduledThreadPool(10);

        ThreadFactoryPrioitaire factoryPrioitaire = new ThreadFactoryPrioitaire();
//        poolPrioritaire = Executors.newFixedThreadPool(30, factoryPrioitaire);
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
     * Le pool schedulé permettant de lancer périodiquement des tâches.
     */
    private ScheduledExecutorService poolSchedule;
    /**
     * *
     * Le pool de thread permettant de lancer des récupération de flux en
     * passant devant le pool schedulé
     */
    private ExecutorService poolPrioritaire;

    /**
     * *
     * Update le service de récupération avec les données du dernier beans. Pour
     * recharger complètement le service, on peut demander la mise à jour avec
     * Observable null et argument Strin "reload all"
     *
     * @param o : Le beans ne notifiant auprès du service : un Flux ou la Conf
     * @param arg Une information sur la nature du changement. A ce jour, C'est
     * une chaine de caractère : add, mod, del.
     */
    @Override
    public void update(Observable o, Object arg) {


        /***========================================================================================
         *                                Ajout ou modification d'un FLUX       
         *///========================================================================================
        if (o instanceof Flux) {
            Flux flux = (Flux) o;
            // Si c'est flux a ajouter
            if (arg instanceof String && arg.equals("add")) {
                logger.debug("ajout d'un flux");

                // Si le flux est actif alors, on ajoute une tache schedulé dans le collecteur
                if (flux.getActive()) {
                    TacheRecupCallable tmpTache = new TacheRecupCallable(flux, true, true);
                    tmpTache.setTacheSchedule(true);
                    //TODO : Scheduler en fonction du temps restant. il faut modifier de deuxieme paramettre de la commande. 
                    this.poolSchedule.schedule(tmpTache, flux.getMediatorFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
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
                if(flux.getTacheRechup()!=null){
                                    flux.getTacheRechup().setAnnulerTache(Boolean.TRUE);
                }
                //SI le flux est actif, on lui crée une nouvelle tâche que l'on réinjecte
                if (flux.getActive()) {
                    TacheRecupCallable tmpTache = new TacheRecupCallable(flux, true, true);
                    tmpTache.setTacheSchedule(true);
                    //TODO : Scheduler en fonction du temps restant. il faut modifier de deuxieme paramettre de la commande. 
                    this.poolSchedule.schedule(tmpTache, flux.getMediatorFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
                    logger.debug("renouvellement de la tâche du flux modifié");
                    flux.setTacheRechup(tmpTache);
                }
            }
        }
        
        /**========================================================================================
         *              BLOC PERMETTANT LE RECHARGEMENT COMPLET DU SERVICE.
         *///======================================================================================

        // Si l'élément Observable est la conf ou si On a donnée l'ordre reload all
        if ((o instanceof Conf) || (o == null && arg instanceof String && arg.equals("reload all"))) {
                        logger.info("Rechargement complet du Service de Collecte");
            // On va recharger tout le Pool Schedulé
            Integer nbThread = DAOFactory.getInstance().getDAOConf().getConfCourante().getNbThreadRecup();
            // On tue les tache en cours
            List<Runnable> listFutur = this.poolSchedule.shutdownNow();
            
            // On recréer le Pool
            this.poolSchedule = Executors.newScheduledThreadPool(nbThread);

            List<Flux> listFlux = DAOFactory.getInstance().getDAOFlux().findAllFlux(true);
            System.out.println("NBR DE FLUX : " + listFlux.size());

            // On inscrit les taches actives au pool schedule
            int i;
            for (i = 0; i < listFlux.size(); i++) {
                if (listFlux.get(i).getActive()) {
                    // On schedule
                    TacheRecupCallable tmpTache = new TacheRecupCallable(listFlux.get(i), true, true);
                    listFlux.get(i).setTacheRechup(tmpTache);
                    //TODO : Scheduler en fonction du temps restant. il faut modifier de deuxieme paramettre de la commande. 
                    this.poolSchedule.schedule(tmpTache, listFlux.get(i).getMediatorFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
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
     * Stope le service de collecte en fermant proprement les deux pool de tâches
     * de collecte
     */
    public void stopCollecte() {
        // Fermeture du scheduler
        this.poolSchedule.shutdownNow();
        this.poolPrioritaire.shutdownNow();
    }
    

    /***
     * Cette méthode n'est maintenant plus utilisée au profit de majManuellAll()
     * @param flux
     * @throws Exception
     * @deprecated
     */
    @Deprecated
    public void majManuelle(Flux flux) throws Exception {
        System.out.println("");
        TacheRecupCallable task = new TacheRecupCallable(flux, false, true);

        Future<Boolean> t = this.poolPrioritaire.submit(task);

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
            TacheRecupCallable task = new TacheRecupCallable(listFlux.get(i), false, true);
            listTache.add(task);
            listFlux.get(i).setTacheRechupManuelle(task);
        }
        DateTime dtDebut = new DateTime();
        List<Future<Boolean>> listFutur = this.poolPrioritaire.invokeAll(listTache);
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
    public void addScheduledCallable(TacheRecupCallable t) {
//        this.poolSchedule.schedule(t, t.getFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
        this.poolSchedule.schedule(t, t.getFlux().getMediatorFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
    }

    /***
     * Retoune le pool l'executeur de thread dans lequel sont inscrit les flux actifs
     * @return 
     */
    public ScheduledExecutorService getPoolSchedule() {
        return poolSchedule;
    }

    /**
     * modifier le pool l'executeur de thread dans lequel sont inscrit les flux actifs
     * @param poolSchedule 
     */
    public void setPoolSchedule(ScheduledExecutorService poolSchedule) {
        this.poolSchedule = poolSchedule;
    }

    
    /***
     * Retoune le pool prioritaire du service. Il s'agit du pool pour lancer des collectes manuelle. Celles ci sont lancée avec une priorité suppréieure au pool schedulé
     * @return 
     */
    public ExecutorService getPoolPrioritaire() {
        return poolPrioritaire;
    }

    /***
     * Définir le pool prioritaire
     * @param poolPrioritaire 
     */
    public void setPoolPrioritaire(ExecutorService poolPrioritaire) {
        this.poolPrioritaire = poolPrioritaire;
    }
}
