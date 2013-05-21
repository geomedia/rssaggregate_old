/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import rssagregator.beans.Flux;

/**
 *
 * @author clem
 */
public class DaoFlux extends AbstrDao{

//    private static String REQ_FIND_ALL="SELECT f FROM Flux f";
//    private String REQ_FIND_ALL_LIMIT="SELECT f FROM Flux f";

    protected DaoFlux(DAOFactory dAOFactory) {
        this.classAssocie = Flux.class;
        this.dAOFactory = dAOFactory;
    }



    /**
     * Enregistre le flux comme nouveau dans la base de donnée Cette méthode est maintenant dans la classe abstraite
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

//    /**
//     * *
//     * Supprimer le flux et tous ses objets liées (item, incident,
//     * Infocollecte...)
//     *
//     * @param flux
//     */
//    public void remove(Flux flux) {
//        initEntityManager();
//        em.getTransaction().begin();
//        System.out.println("");
//        em.remove(em.merge(flux));
//        em.getTransaction().commit();
//    }

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
