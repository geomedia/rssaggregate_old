/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.ArrayList;
import rssagregator.dao.DAOConf;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.TacheRecupCallable;
import rssagregator.servlet.Test;

/**
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
        poolPrioritaire = Executors.newFixedThreadPool(5, factoryPrioitaire);
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

    @Override
    public void update(Observable o, Object arg) {
       
        logger.info("Rechargement du Service de Collecte");
        // On récupère la liste des flux
//        ListeFluxCollecteEtConfigConrante fluxCollecte = (ListeFluxCollecteEtConfigConrante) o;

        // Si l'observable notifiant est la DAO FLUX. Il faut recréer le pool avec la liste des nouveau flux à suivre
        if (o instanceof DaoFlux || o instanceof DAOConf) {
            

            
            if (DAOFactory.getInstance().getDAOConf().getConfCourante().getActive()) {
                // On supprime de scheduler pour le rechrer avec la liste de nouvelles tâches
                Integer nbThread = DAOFactory.getInstance().getDAOConf().getConfCourante().getNbThreadRecup();
                
                // On tue les tache en cours
                 List<Runnable> listFutur= this.poolSchedule.shutdownNow();

                this.poolSchedule = Executors.newScheduledThreadPool(nbThread);

                List<Flux> listFlux = DAOFactory.getInstance().getDAOFlux().findAllFlux(false);

                // On inscrit les taches actives au pool schedule
                int i;
                for (i = 0; i < listFlux.size(); i++) {
                    if (listFlux.get(i).getActive()) {
                        // On schedule
                        listFlux.get(i).createTask();
                        
                        TacheRecupCallable tmpTache =   new TacheRecupCallable(listFlux.get(i));
                        tmpTache.setTacheSchedule(true);
                         
                        //TODO : Scheduler en fonction du temps restant. il faut modifier de deuxieme paramettre de la commande. 
                        this.poolSchedule.schedule(tmpTache, listFlux.get(i).getPeriodiciteCollecte(), TimeUnit.SECONDS);
                    }
                }
            }
            else {
                poolSchedule.shutdown();
            }
        }


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

    public void startScheduledCollecte() {
        //On se contente de reloader
        update(null, null);
    }

    public void stopCollecte() {
        // Fermeture du scheduler
        this.poolSchedule.shutdown();
        this.poolPrioritaire.shutdown();
    }

    public void majManuelle(Flux flux) throws Exception{
        System.out.println("");
             TacheRecupCallable task = new TacheRecupCallable(flux);
            
            Future<List<Item>> t =  this.poolPrioritaire.submit(task);
            
            t.get(30, TimeUnit.SECONDS);

           
            
            // A la fin de la tache, il faut rafraichir le context objet et la base de donnée.
//            DAOFactory.getInstance().getEntityManager().refresh(flux);
    }
    
    
    public void majManuellAll(List<Flux> listFlux)throws Exception{
           int i;
           
           List<TacheRecupCallable> listTache = new ArrayList<TacheRecupCallable>();
           
                for(i=0;i<listFlux.size(); i++){
                    
                     TacheRecupCallable task = new TacheRecupCallable(listFlux.get(i));
                    listTache.add(task);
//                    this.poolPrioritaire.submit(task);
                }
                
                
                DateTime dtDebut = new DateTime();
                this.poolPrioritaire.invokeAll(listTache);

                
         
                
//                 this.poolPrioritaire.awaitTermination(30, TimeUnit.SECONDS);
        
                 
    }
    
    
    /***
     * Permet d'ajouter un callable au pool schedulé. La méthode scheduleAtFixedRate ne permet pas d'ajouter des Callable, seulement des runnable. Pour cette raison, les renable doivent se réajouter en fin de tache pour avoir un effet scheduleAtFixedRate
     * @param t 
     */
    public void addScheduledCallable(TacheRecupCallable t ){
        this.poolSchedule.schedule(t, t.getFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
    }

    public ScheduledExecutorService getPoolSchedule() {
        return poolSchedule;
    }

    public void setPoolSchedule(ScheduledExecutorService poolSchedule) {
        this.poolSchedule = poolSchedule;
    }

    public ExecutorService getPoolPrioritaire() {
        return poolPrioritaire;
    }

    public void setPoolPrioritaire(ExecutorService poolPrioritaire) {
        this.poolPrioritaire = poolPrioritaire;
    }
    
    
    public void infoCollecteur(){
        
    }
    
    
    
}
