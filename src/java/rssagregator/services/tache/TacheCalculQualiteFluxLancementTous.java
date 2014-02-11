/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.Query;
import rssagregator.beans.Flux;
import rssagregator.dao.DAOFactory;
import rssagregator.services.ServiceCollecteur;

/**
 * Tache Lancant le calcul de qualité des flux pour tous les fluxs. Lancement 1 à 1 dans un SingleThreadExecutor
 *
 * @author clem
 */
public class TacheCalculQualiteFluxLancementTous extends TacheImpl<TacheCalculQualiteFluxLancementTous> {

    @Override
    protected void callCorps() throws InterruptedException, Exception {
        ExecutorService es = Executors.newSingleThreadExecutor();
        try {
            // On récupère la liste des tache calcul qualité. 
            em = DAOFactory.getInstance().getEntityManager();
            Query q = em.createQuery("SELECT f FROM Flux AS f WHERE f.active = TRUE");

            List<Flux> fluxs = q.getResultList();
            for (int i = 0; i < fluxs.size(); i++) {
                Flux flux = fluxs.get(i);

                //Création d'un tache

//            TacheCalculQualiteFlux calculQualiteFlux = new TacheCalculQualiteFlux();
                TacheCalculQualiteFlux calculQualiteFlux = (TacheCalculQualiteFlux) TacheFactory.getInstance().getNewTask(TacheCalculQualiteFlux.class, false);
                calculQualiteFlux.setFlux(flux);
                calculQualiteFlux.setSchedule(false);
                
                
                ServiceCollecteur.getInstance().getTacheProducteur().produireMaintenant(calculQualiteFlux);
                synchronized(calculQualiteFlux){
                    calculQualiteFlux.wait(300*1000);
                }
                
                
//                Future fut = es.submit(calculQualiteFlux);
//                try {
//                    fut.get(300, TimeUnit.SECONDS);
//                } catch (Exception e) {
//                    logger.debug("La tache Calcul qualité flux a mis plus de 120 secondes", e);
//                }

            }
        } catch (Exception e) {
            logger.error("Erreur lors du calcul qualité flux ", e);
        } finally {
            es.shutdownNow();

        }

    }
}
