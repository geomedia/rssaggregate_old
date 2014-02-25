/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.List;
import javax.persistence.Query;
import rssagregator.beans.Item;
import rssagregator.dao.DAOFactory;
import rssagregator.services.ServiceCollecteur;

/**
 * Cette tache a pour mission de raffiner les items ne possédant pas encore de liaison vers une item raffinée.
 * Normalement, les items sont automatiquement raffinées lors de leur création (voir {@link TacheRaffiner}, c'est le
 * service de collecte qui lance cette dernière tache a la création de nouvelle items. Cependant, il peut être
 * nécessaire de reraffiner l'ensemble du corpus, c'est dans ce cas de figure que l'on utilise la présente tache. Son
 * traitement peut être long. Elle va traité les items par block pour éviter de devoir charger d'un coup des milliers
 * d'items en mémoire
 *
 * @author clem
 */
public class TacheRaffinerPeriodique extends TacheImpl<TacheRaffinerPeriodique> {

    @Override
    protected void callCorps() throws InterruptedException, Exception {

        initialiserTransaction();
        // Selection des items ne possédant pas d'item raffinée
        boolean encore = true;
        while (encore) {
//            Query q = em.createQuery("SELECT i FROM Item i WHERE i.itemRaffinee IS NULL");
//            Query q = em.createQuery("SELECT i FROM Item i");
//            Query q = em.createQuery("SELECT i FROM Item i LEFT JOIN i.doublon d WHERE d IS NULL");
             Query q = em.createQuery("SELECT i FROM Item i LEFT JOIN i.doublon d WHERE d.ID IS NULL");
//             Query q = em.createQuery("SELECT i FROM Item i LEFT JOIN i.doublon d, i.listFlux fl, fl.journalLie j WHERE d.ID IS NULL AND j.ID=1756");
            
            
            
            q.setFirstResult(0);
            q.setMaxResults(150);

            List<Item> items = q.getResultList();
            if (items.isEmpty()) {
                encore = false;
                break;
            }
            
            //---------------------
            // Dédoublonnage par tache plusieurs
            //---------------------
            TacheRaffinerPlusieurs raffinerPlusieurs = (TacheRaffinerPlusieurs) TacheFactory.getInstance().getNewTask(TacheRaffinerPlusieurs.class,false);
            raffinerPlusieurs.setItemARaffiner(items);
            raffinerPlusieurs.setComportement(items.get(0).getListFlux().get(0).getMediatorFlux());
                    ServiceCollecteur.getInstance().getTacheProducteur().produire(raffinerPlusieurs);
                    synchronized(raffinerPlusieurs){
                        raffinerPlusieurs.wait(20*1000);
                    }
            
            
            
            
//            for (int i = 0; i < items.size(); i++) {
//                Item item = items.get(i);
//      
//                // On crée une tache rafinée
//                TacheRaffiner2 raffiner = (TacheRaffiner2) TacheFactory.getInstance().getNewTask(TacheRaffiner2.class, false);
//                raffiner.setItem(item);
//                raffiner.setComportementCollecte(item.getListFlux().get(0).getMediatorFlux());
//                ServiceCollecteur.getInstance().getTacheProducteur().produireMaintenant(raffiner);
//
//                //On attend la completion de la tache avant d'en produire une autre
//                synchronized (raffiner) {
//                    raffiner.wait(10000);
//                }
//                em.detach(item); // On sort l'item de l'EM 
//            }

        }

    }
}
