/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

/**
 *
 * @author clem
 */
public class ZZOLDTacheRaffiner extends TacheImpl<ZZOLDTacheRaffiner> {

    
//    private static Semaphore semUnique = new Semaphore(1);
//    
//    
//    
//    /**
//     * *
//     * L'item (brute) que la tâche doit analyser afin de générer une item raffiné ou lié a une item raffiné existante.
//     */
//    Item item;
//    /**
//     * *
//     * Objet permettant de comparer des item.
//     */
//    private ItemComparator comparator = new ItemComparator();
//    
//    /***
//     * Le comportement de collecte qui doit être utilisé pour raffiné l'item. 
//     */
//    ComportementCollecte comportementCollecte;
//    
//
//    @Override
//    protected void callCorps() throws InterruptedException, Exception {
////        super.callCorps(); //To change body of generated methods, choose Tools | Templates.
//
//        if (item != null && item.getID() != null) {
//
//            ExceptionTool.argumentNonNull(item);
//            ExceptionTool.argumentNonNull(comportementCollecte);
//            initialiserTransaction();
//           
//            
//            
//            ItemRaffinee itemRetenu = null; // L'item raffinée que l'on cherche 
//
//            item = em.find(Item.class, item.getID(), LockModeType.PESSIMISTIC_WRITE); // On lock l'item brute par JPA
//
//
//            //-----------------------------------------------------------------------
//            //Recherche d'une item raffinnée possédant le même Hash
//            //-----------------------------------------------------------------------
//            /**
//             * * 
//             * On commence par chercher si on peut trouver dans la base de données une itemsRéffinée possédant le même
//             * hash que l'item observe. Si on la trouve innutile de procéder a un traitement plus complexe.
//             */
//            Query q = em.createQuery("SELECT i FROM ItemRaffinee i WHERE i.hashContenu LIKE(:hash)");
//            q.setParameter("hash", item.getHashContenu());
//            Object result = null;
//            try {
//                result = q.getSingleResult();
//                if (result != null) {
//                    itemRetenu = (ItemRaffinee) result;
//                    em.lock(itemRetenu, LockModeType.PESSIMISTIC_READ);
//                }
//            } catch (Exception e) {
//            }
//
//
//
//
////            try {
//            //--------------------------------------------------------------------
//            //           CONSTRUCTION DE LA REQUETE CRITERIA
//            //--------------------------------------------------------------------
//            if (itemRetenu == null) { // Si on n'a pas déjà trouvé une item raffinnée par son hash alors on cherche des items ressemblante ou strictement semblable par le block ci dessous
//                // On cherche les items rafinnée qui pourraient correspondre
//                String titre = item.getTitre();
//                String guid = item.getGuid();
//                String link = item.getLink();
//                String hash = item.getHashContenu();
//
//                CriteriaBuilder cb = em.getCriteriaBuilder();
//                CriteriaQuery<Item> cq = cb.createQuery(Item.class);
//                Root root = cq.from(Item.class);
//                root.fetch("itemRaffinee");
//
//                Predicate where = null;
//
//                if (titre != null && !titre.isEmpty()) {
//                    if (where == null) {
//                        where = cb.like(root.get("titre"), "%" + titre + "%");
//                    } else {
//                        where = cb.or(where, cb.like(root.get("titre"), "%" + titre + "%"));
//                    }
//                }
//
//                if (guid != null && !guid.isEmpty()) {
//                    if (where == null) {
//                        where = cb.equal(root.get("guid"), guid);
//                    } else {
//                        where = cb.or(where, cb.equal(root.get("guid"), guid));
//                    }
//                }
////                
//                if (link != null && !link.isEmpty()) {
//                    if (where == null) {
//                        where = cb.equal(root.get("link"), link);
//                    } else {
//                        where = cb.or(where, cb.equal(root.get("link"), link));
//                    }
//                }
////                
////                if (hash != null && !hash.isEmpty()) {
////                    if (where == null) {
////                        where = cb.equal(root.get("hashContenu"), hash);
////                    } else {
////                        where = cb.or(where, cb.equal(root.get("hashContenu"), hash));
////                    }
////                }
//
//                cq.where(where);
//               
//                TypedQuery<Item> tq = em.createQuery(cq);
//               
//                List<Item> resu = tq.getResultList();
// 
//
//                //-------------------------------------------------------------------
//                //Exploitation des résultats
//                //-------------------------------------------------------------------
//                /**
//                 * *
//                 * On commence par chercher si simplement il n'existe pas une item raffinée possédant le même hash
//                 */
//                /**
//                 * *
//                 * Dans ce block on parcours chacune des items ressemblante ou similaire trouvé dans la base de données.
//                 * On utilise ensuite le comparator qui détermine si on peut considérer les deux itemsBrute comme
//                 * provenant du même article. Si un résultat concorde et que l'item ressemblante possède déjà une item
//                 * rafiné alors on retient l'item de l'item observé comme item rafiné pour l'item sur laquelle le
//                 * travail de la thread est effectué.
//                 */
//      
//        
//                for (int i = 0; i < resu.size(); i++) {
//
//                    Item iteration = resu.get(i);
//                    int retour = -5;
//                    try {
//                        retour = comparator.compare(item, iteration);
//                    } catch (Exception e) {
//                        logger.debug("err", e);
//                    }
//
//                    if (retour >= 0) { // Si l'item inspecté est ressemblante ou semblable
//                        if (iteration.getItemRaffinee() != null) { // sii l'item ressemblante ou semblable possède une item rafiné
//                            itemRetenu = iteration.getItemRaffinee();
//                            break;
//                        }
//                    }
//                }
//              
//            }
//
//            //---------------------------------------------------------------------
//            // Enregistrement de l'item rafiné
//            //---------------------------------------------------------------------
//            /**
//             * *
//             * Block permettant de lier l'item raffinnée si elle existe à l'item observé. Si on n'a pas trouvé d'item
//             * raffiné, on en crée une
//             */
//            if (itemRetenu == null) { // Si on n'a pas trouvé d'item ressemblante ou strictment identique, on crée une itemRafinné a partir de l'item courante 
//                itemRetenu = new ItemRaffinee();
//                itemRetenu.setTitre(item.getTitre());
//                itemRetenu.setCategorie(item.getCategorie());
//                itemRetenu.setDatePub(item.getDatePub());
//                itemRetenu.setDateRecup(item.getDateRecup());
//                itemRetenu.setGuid(item.getGuid());
//                itemRetenu.setHashContenu(item.getHashContenu());
//                itemRetenu.setLink(item.getLink());
//                itemRetenu.addItem(item);
//                item.setItemRaffinee(itemRetenu);
//
//                itemRetenu.setDescription(item.getDescription());
//                em.merge(item);
//                em.persist(itemRetenu);
//
////                logger.debug("persite");
//            } else { // SInon on ajoute l'item (brute) courante à l'item rafiné et on modifi l'item raffiné.
//
//                // On lock l'item raffiné
//                em.lock(itemRetenu, LockModeType.PESSIMISTIC_WRITE);
//                em.refresh(itemRetenu);
//
//
//                itemRetenu.addItem(item);
//                item.setItemRaffinee(itemRetenu);
//                em.merge(itemRetenu);
//                em.merge(item);
//            }
////            } catch (Exception e) {
////                logger.debug("err", e);
////            }
//        }
//    }
//
//    @Override
//    public Set<Semaphore> returnSemSet() {
//
//        sem.clear();
//        sem.add(semUnique);
//
////        if (item != null) {
////            try {
////                
////                ItemRaffinee pseudoItemRaf = new ItemRaffinee();
////                pseudoItemRaf.setID(new Long(99999)); // On ne doit pouvoir lancer qu'un raffinage en même temps. Pour simuler ce comportement on demande une semaphore pour un beans pour lequel on a fixé l'id.
////                
////                Semaphore s = SemaphoreCentre.getinstance().returnSemaphoreForRessource(pseudoItemRaf);
////                logger.debug("On a une sem pour cette item");
////                sem.add(s);
////
////            } catch (NullPointerException ex) {
////                Logger.getLogger(ZZOLDTacheRaffiner.class.getName()).log(Level.SEVERE, null, ex);
////            } catch (IllegalAccessException ex) {
////                Logger.getLogger(ZZOLDTacheRaffiner.class.getName()).log(Level.SEVERE, null, ex);
////            }
////
////        }
//
//        return sem;
//    }
//
//    public Item getItem() {
//        return item;
//    }
//
//    public void setItem(Item item) {
//        this.item = item;
//    }
//
//    public ComportementCollecte getComportementCollecte() {
//        return comportementCollecte;
//    }
//
//    public void setComportementCollecte(ComportementCollecte comportementCollecte) {
//        this.comportementCollecte = comportementCollecte;
//    }
//
//    
    
    
}
