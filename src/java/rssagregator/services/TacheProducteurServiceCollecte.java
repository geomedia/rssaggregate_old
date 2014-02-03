/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import rssagregator.beans.Flux;
import rssagregator.beans.exception.ArgumentIncorrect;
import rssagregator.beans.exception.IncompleteBeanExeption;
import rssagregator.dao.DAOFactory;
import rssagregator.services.tache.AbstrTache;
import rssagregator.services.tache.TacheRecupCallable;

/**
 *
 * @author clem
 */
public class TacheProducteurServiceCollecte extends TacheProducteur implements Runnable {

    public TacheProducteurServiceCollecte(AbstrService service) {
        super(service);
    }

    /**
     * *
     * Le producteur boucle toute les 30 minutes afin de vérifier que toutes les taches sont bien lancées
     */
    @Override
    public void run() {
        try {
            while (true) {
                // Récupération de l'ensemble des flux
                EntityManager em = DAOFactory.getInstance().getEntityManager();

                // ----------------------------------------------------------------------------------
                //                  Ajout de tous les flux 
                // ----------------------------------------------------------------------------------
                /**
                 * *
                 * Le block ci de dessous vérifie que tous les flux sont bien ajouté au service de collecte.
                 */
                Query query = em.createQuery("SELECT f FROM Flux f WHERE f.active = TRUE");
                List<Flux> lf = query.getResultList();
                for (Iterator<Flux> it = lf.iterator(); it.hasNext();) {
//                    System.out.println("--");
                    Flux flux = it.next();

//                    Map<AbstrTacheSchedule, Future> map = service.getMapTache();
                    List<AbstrTache> taches = service.tacheGereeParLeService;
                    for (int i = 0; i < taches.size(); i++) {
                        AbstrTache abstrTacheSchedule = taches.get(i);
                        
//                    }
//
//                    // On cherche si le flux est déja dans la map
//                    for (Map.Entry<AbstrTacheSchedule, Future> entry : map.entrySet()) {
//                        AbstrTache abstrTacheSchedule = entry.getKey();
                        Future future = abstrTacheSchedule.getFuture();

                        if (abstrTacheSchedule.getClass().equals(TacheRecupCallable.class)) {
                            TacheRecupCallable castTache = (TacheRecupCallable) abstrTacheSchedule;

                            // Si on a bien une tache pour le flux 
                            if (castTache.getFlux().getID().equals(flux.getID())) {
                                
                                
                                it.remove();
                            }
                        }
                    }
                }

                ServiceCollecteur serviceCollecte = ServiceCollecteur.getInstance();
                // On crée des tache schedulé pour chacun des flux restant dans la liste
                for (int i = 0; i < lf.size(); i++) {
                    Flux flux = lf.get(i);
                    try {
                        serviceCollecte.enregistrerFluxAupresDuService(flux);
                    } catch (IncompleteBeanExeption ex) {
                        Logger.getLogger(TacheProducteur.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

//                //------------------------------------------------------------------------------------
//                //                      Vérification des temps d'execution
//                //------------------------------------------------------------------------------------
//                /**
//                 * *
//                 * Le service de collecte est critique. Il faut vérifier que les taches sont executé correctement, cad
//                 * qu'elle ne sont pas blockés, 
//                 */
//                Map<AbstrTacheSchedule, Future> map = service.getMapTache();
//                for (Map.Entry<AbstrTacheSchedule, Future> entry : map.entrySet()) {
//                    AbstrTache abstrTacheSchedule = entry.getKey();
//                    Future future = entry.getValue();
//                    
//                    
//                    //--Si la tache est lancée, on vérifi qu'elle ne dépasse pas son temps d'execution. Si c'est le cas on la relance
//                    if (abstrTacheSchedule.isRunning()) {
//                           if(abstrTacheSchedule.returnExecutionDuration()> abstrTacheSchedule.getMaxExecuteTime()){
//                                  logger.error("La tache " + abstrTacheSchedule+ ". Dépasse son temps d'execuction de " + abstrTacheSchedule.returnExecutionDuration());
//                               try {
//                                   service.relancerTache(abstrTacheSchedule);
//                               } catch (ArgumentIncorrect ex) {
//                                   Logger.getLogger(TacheProducteurServiceCollecte.class.getName()).log(Level.SEVERE, null, ex);
//                               }
//                                  
//                            }
//                    }
//                }
                
                Thread.sleep(10000);
            }

        } catch (InterruptedException e) {
            logger.debug("Interruption de " + this);
        }
    }
}
