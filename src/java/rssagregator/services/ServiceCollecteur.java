/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.Flux;
import rssagregator.beans.TacheRecup;

/**
 *
 * @author clem
 */
public class ServiceCollecteur implements Observer {

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

        // On récupère la liste des flux
        ListeFluxCollecteEtConfigConrante fluxCollecte = (ListeFluxCollecteEtConfigConrante) o;


        if (o instanceof ListeFluxCollecteEtConfigConrante) {

            // Si la config est active
            if (fluxCollecte.getConfCourante().getActive()) {

                // On supprime de scheduler pour le rechrer avec la liste de nouvelles tâches
                Integer nbThread = ListeFluxCollecteEtConfigConrante.getInstance().getConfCourante().getNbThreadRecup();
//                this.poolSchedule.shutdown();

                this.poolSchedule.shutdownNow();
                this.poolSchedule.shutdown();
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
//                }
                
                
                 //On attend que le pool soit bien mort
//                while(this.poolSchedule.isTerminated()){
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
                
                // On recré un pool
            
                this.poolSchedule = Executors.newScheduledThreadPool(nbThread);
                List<Flux> tmpList = fluxCollecte.getListFlux();
                
                
                // On inscrit les taches actives au pool
                int i;
                for (i = 0; i < tmpList.size(); i++) {
                    //Si le flux est actif, on l'ajoute au scheduler
                    if (tmpList.get(i).getActive()) {
                        // On schedule
                        tmpList.get(i).createTask();
                        TacheRecup tmpTache = tmpList.get(i).getTacheRechup();
                        //TODO : Scheduler en fonction du temps restant. il faut modifier de deuxieme paramettre de la commande. 
                        this.poolSchedule.scheduleAtFixedRate(tmpTache, 0, tmpList.get(i).getPeriodiciteCollecte(), TimeUnit.SECONDS);
                    }
                }
            } else {
                stopCollecte();
            }
            System.out.println("###### Rechargement du collecteur Terminé ######");
        }
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

    public void majManuelle(Flux flux) {
        try {
       
            flux.createTask();
            Future<TacheRecup> t = (Future<TacheRecup>) this.poolPrioritaire.submit(flux.getTacheRechup());

            t.get(30, TimeUnit.SECONDS);

        } catch (InterruptedException ex) {
            Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
