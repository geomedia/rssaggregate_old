/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.ArrayList;
import java.util.List;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;

/**
 *
 * @author clem
 */
public class DaoFlux extends AbstrDao {

//    private static String REQ_FIND_ALL="SELECT f FROM Flux f";
//    private String REQ_FIND_ALL_LIMIT="SELECT f FROM Flux f";
    protected DaoFlux(DAOFactory dAOFactory) {
        this.classAssocie = Flux.class;
        this.dAOFactory = dAOFactory;

    }

    /**
     * Enregistre le flux comme nouveau dans la base de donnée Cette méthode est
     * maintenant dans la classe abstraite
     *
     * @param flux
     */
//    public void creer(Flux flux) {
//        //Il faut initialiser le em
//        initEntityManager();
//        em.getTransaction().begin();
//        em.persist(flux);
//        em.getTransaction().commit();
//    }
//    /**
//     * *
//     * Modifie le flux envoyé
//     *
//     * @param flux
//     */
//    public void modifier(Flux flux) {
//        // Test si le flux possède bien un id
//        if (flux.getID() != null && flux.getID() >= 0) {
//            initEntityManager();
//            em.getTransaction().begin();
//            em.merge(flux);
//            em.getTransaction().commit();
//        } 
//    }
    /**
     * *
     * Supprimer le flux et tous ses objets liées (item, incident,
     * Infocollecte...)
     *
     * @param flux
     */
    public void remove(Flux flux) {
        em = DAOFactory.getInstance().getEntityManager();


        // On doit suppimer les items liées si il sont orphelin
        List<Item> items = flux.getItem(); //....
        //999
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();

        System.out.println("NOMBRE DE d'Item : " + items.size());
        System.out.println("");


//        System.out.println("L'em gere le flux  : " + em.contains(flux));

        int i;
        for (i = 0; i < items.size(); i++) {
//System.out.println("L'em gere l'item : " + em.contains(items.get(i)));

            //Supppression des items qui vont devenir orphelines
            if (items.get(i).getListFlux().size() < 2) {
                daoItem.remove(items.get(i));
                System.out.println("SUPRESSION D'une item");
            } else { // Sinon on détach le flux

                items.get(i).getListFlux().remove(flux);
                daoItem.modifier(items.get(i));

//                int j;
//                List<Flux> flList = items.get(i).getListFlux();
//                for (j = 0; j < flList.size(); j++) {
//                    if (flList.get(j).getID() == flux.getID()) {
//                        items.get(i).getListFlux().remove(flList.get(j));
//                        daoItem.modifier(items.get(i));
//                        System.out.println("IN THE IF");
//                        
//
//                    }
//                }
            }
        }

        // On supprime la liste de flux du flux
        flux.setItem(new ArrayList<Item>());

//        em.merge(flux);
        em.getTransaction().begin();

        em.remove(em.merge(flux));
        em.getTransaction().commit();
    }
//    /**
//     * *
//     * Retrouver un flux à patir de son id.
//     *
//     * @param id
//     * @return
//     */
//    public Flux find(Long id) {
//        initEntityManager();
//        em.getTransaction().begin();
//        Flux resuFlux = em.find(Flux.class, id);
//        em.getTransaction().commit();
//        return resuFlux;
//    }
//        public  List<Object> findall() {
//        initEntityManager();
//        em.getTransaction().begin();
//        
//        System.out.println("JE suis : "+this.getClass().getCanonicalName());
//        Query query = em.createQuery(REQ_FIND_ALL);
//        List<Object> result = query.getResultList();
//        return result;
//    }
//    public List<Flux> findall() {
//        initEntityManager();
//        em.getTransaction().begin();
//        Query query = em.createQuery(REQ_FIND_ALL);
//        List<Flux> result = query.getResultList();
//        return result;
//
//    }
}
