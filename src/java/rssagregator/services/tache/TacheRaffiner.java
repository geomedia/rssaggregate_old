/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import rssagregator.beans.Item;
import rssagregator.beans.ItemRaffinee;
import rssagregator.beans.traitement.ItemComparator;

/**
 *
 * @author clem
 */
public class TacheRaffiner extends TacheImpl<TacheRaffiner> {

    /**
     * *
     * L'item (brute) que la tâche doit analyser afin de générer une item raffiné ou lié a une item raffiné existante.
     */
    Item item;
    /**
     * *
     * Objet permettant de comparer des item.
     */
    private ItemComparator comparator = new ItemComparator();

    @Override
    protected void callCorps() throws InterruptedException, Exception {
//        super.callCorps(); //To change body of generated methods, choose Tools | Templates.

        if (item != null && item.getID() != null) {

            // On cherche les items rafinnée qui pourraient correspondre
            String titre = item.getTitre();
            String guid = item.getGuid();
            String link = item.getLink();
            String hash = item.getHashContenu();
            initialiserTransaction();

            try {

                
                
                //--------------------------------------------------------------------
                // CONSTRUCTION DE LA REQUETE CRITERIA
                //--------------------------------------------------------------------

                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<ItemRaffinee> cq = cb.createQuery(ItemRaffinee.class);
                Root root = cq.from(ItemRaffinee.class);
                Predicate where = null;

                if (titre != null && !titre.isEmpty()) {
                    if (where == null) {
                        where = cb.like(root.get("titre"), "%" + titre + "%");
                    } else {
                        where = cb.or(where, cb.like(root.get("titre"), "%" + titre + "A%"));
                    }
                    System.out.println("titre" + titre);
                }
                
                if(guid != null && !guid.isEmpty()){
                    if(where == null){
                        where = cb.equal(root.get("guid"), guid);
                    }
                    else{
                        where = cb.or(where, cb.equal(root.get("guid"), guid));
                    }
                }
//                
                if(link != null && !link.isEmpty()){
                    if(where == null){
                        where = cb.equal(root.get("link"), link);
                    }
                    else{
                        where = cb.or(where, cb.equal(root.get("link"), link));
                    }
                }
//                
                if(hash != null && !hash.isEmpty()){
                    if(where == null ){
                        where = cb.equal(root.get("hashContenu"), hash);
                    }
                    else{
                        where = cb.or(where, cb.equal(root.get("hashContenu"), hash));
                    }
                }


                cq.where(where);
                TypedQuery<ItemRaffinee> tq = em.createQuery(cq);
                List<ItemRaffinee> resu = tq.getResultList();
                System.out.println("NB RESULT " + resu.size());
                ItemRaffinee itemRetenu = null;

                
                //-------------------------------------------------------------------
                //Exploitation des résultats
                //-------------------------------------------------------------------
                
                for (int i = 0; i < resu.size(); i++) {
                    ItemRaffinee iteration = resu.get(i);
                    int retour = -5;
                    try {
                        retour = comparator.compare(item, iteration);
                    } catch (Exception e) {
                        logger.debug("err", e);
                    }

                    System.out.println("retour : " + retour);
                    if (retour >= 0) {
                        itemRetenu = resu.get(i);
                        System.out.println("ITEM RETENU : " + itemRetenu);
                    }
                }



                //---------------------------------------------------------------------
                // Enregistrement de l'item rafiné
                //---------------------------------------------------------------------

                if (itemRetenu == null) { // Si on n'a pas trouvé d'item ressemblante ou strictment identique, on crée une itemRafinné a partir de l'item courante 
                    itemRetenu = new ItemRaffinee();
                    itemRetenu.setTitre(item.getTitre());
                    itemRetenu.setCategorie(item.getCategorie());
                    itemRetenu.setDatePub(item.getDatePub());
                    itemRetenu.setDateRecup(item.getDateRecup());
                    itemRetenu.setGuid(item.getGuid());
                    itemRetenu.setHashContenu(item.getHashContenu());
                    itemRetenu.setLink(item.getLink());
                    itemRetenu.addItem(item);
                    em.persist(itemRetenu);
                    logger.debug("persite");
                } else { // SInon on ajoute l'item (brute) courante à l'item rafiné et on modifi l'item raffiné.
                    itemRetenu.addItem(item);
                    em.merge(itemRetenu);
                    logger.debug("Merge");
                }
            } catch (Exception e) {
                logger.debug("err", e);
            }
        }
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
