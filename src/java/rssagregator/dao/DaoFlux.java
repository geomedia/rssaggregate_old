/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.Journal;
import rssagregator.services.ServiceGestionIncident;

/**
 *
 * @author clem
 */
public class DaoFlux extends AbstrDao {

//    public List<Flux> listFlux;
    private static String REQ_FIND_ALL = "SELECT f FROM Flux f";

    /**
     * *
     * Ce constructeur NE DOIT PAS ETRE UTILISE (d'ou le protected), il faut
     * passer par la DAOFactory
     *
     * @param dAOFactory
     */
    protected DaoFlux(DAOFactory dAOFactory) {
        em = dAOFactory.getEntityManager();
        this.classAssocie = Flux.class;
        this.dAOFactory = dAOFactory;
    }

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
     * Supprimer le flux et tous ses objets liées (item, incident). Si une item
     * est encore liée à un autre flux, la liaison est rompu mais l'item n'est
     * pas supprimée Infocollecte...)
     *
     * @param flux
     */
    public void remove(Flux flux) throws IllegalArgumentException, TransactionRequiredException, Exception {
//        em = DAOFactory.getInstance().getEntityManager();


        // On doit suppimer les items liées si il sont orphelin
        List<Item> items = flux.getItem(); //....
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();

        int i;
        for (i = 0; i < items.size(); i++) {
            //Supppression des items qui vont devenir orphelines
            if (items.get(i).getListFlux().size() < 2) {
                daoItem.remove(items.get(i));
            } else { // Sinon on détach le flux

                items.get(i).getListFlux().remove(flux);
                daoItem.modifier(items.get(i));

            }
        }

        // On supprime la liste de flux du flux
        flux.setItem(new ArrayList<Item>());
        em.getTransaction().begin();
        em.remove(em.merge(flux));
        em.getTransaction().commit();

    }

    /**
     * *
     * Permet de récupérer la liste complete des flux. Pour éviter d'éffectuer
     * milles fois la même requête, il est possible de limiter la recherche au
     * cache de l'ORM.
     *
     * @param sql true= parcourir la base, false : juste le cache
     * @return
     */
    public List<Flux> findAllFlux(Boolean sql) {
//        em = dAOFactory.getEntityManager();
        Query query = em.createQuery(REQ_FIND_ALL);
//        Query query = em.createQuery("SELECT f FROM Flux f");
        if (!sql) {
//            query.setHint("eclipselink.cache-usage", "CheckCacheOnly");
//            query.setHint(QueryHints.FETCH, HintValues.FALSE);
            query.setHint("eclipselink.cache-usage", "CheckCacheOnly");
//              query.setHint("eclipselink.join-fetch", "f.zaddress");

        }
//        System.out.println("QUERY : " + query.toString());

        List<Flux> result = query.getResultList();
        return result;
    }

    /**
     * *
     * Charger données (list flux et config) depuis la base de données. Les
     * dernier hash des items sont aussi chargé
     */
    public void chargerDepuisBd() {

        //Chargement de la liste des flux depuis la BDD
        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
        List<Flux> listflux = daoFlux.findAllFlux(Boolean.TRUE);

        int i;

        for (i = 0; i < listflux.size(); i++) {
            Flux fl = (Flux) listflux.get(i);

            // Pour chaque flux, on va charger les 100 dernier hash 
            DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
            List<String> dernierHash = daoItem.findLastHash(fl, 100);
            fl.setLastEmpruntes(dernierHash);
        }
    }

    /**
     * *
     * Ajoute un flux à la liste des flux collecté puis persiste dans la base de
     * donées. La modification est ensuite notifiée aux observeur (le service de
     * collecte collecteurs)
     *
     * @param obj Le flux a créer
     */
    @Override
    public void creer(Object obj) throws Exception {

        // On va remplir la date en force
        Flux fl = (Flux)obj;
        fl.setCreated(new Date());
        
        em.getTransaction().begin();
        em.persist(fl);
        em.getTransaction().commit();

//        forceNotifyObserver();
    }

    /**
     * *
     * Retourne la liste des journaux appartenant au journal envoyé en argument
     *
     * @param j Le journal servant de base à la recherche
     * @return Une liste de flux résultat de la recherche
     */
    public List<Flux> findFluxParJournaux(Journal j) {
        List<Flux> listFlux = findAllFlux(Boolean.FALSE);
        int i;
        List<Flux> retourList = new ArrayList<Flux>();
        for (i = 0; i < listFlux.size(); i++) {

            if (listFlux.get(i).getJournalLie().equals(j)) {
                retourList.add(listFlux.get(i));
            }
        }
        return retourList;
    }

    /**
     * *
     * Enregistre les modifications du flux dans la base de donnée. Notifi
     * l'observer
     *
     * @param flux
     */
    public void modifierFlux(Flux flux) throws IllegalStateException, RollbackException, Exception {
        try {
            if (flux.getID() != null && flux.getID() >= 0) {
//                em = dAOFactory.getEntityManager();
                em.getTransaction().begin();
                em.merge(flux);
                em.getTransaction().commit();
            }

        } catch (RollbackException e) {
            ServiceGestionIncident.getInstance().gererIncident(e, flux);
            System.out.println("EXEPTION BDD");
            throw e;
        }
    }

    /**
     * *
     * Modifi le statut Change de L'observable et notifi les observer.
     */
    public void forceNotifyObserver() {
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * *
     * Trouve le nombre max de flux. Le journal envoyé en argument permet de
     * restreindre le compte au flux lie en journal en question
     *
     * @param j Journal lie, null pour compter tous les flux
     * @return
     */
    public Integer findNbMax(Journal j) {

//        em = DAOFactory.getInstance().getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery cq = cb.createQuery(Flux.class);
        Root root = cq.from(Flux.class);

        //La jointure avec whereclause
        if (j != null) {
            Join join = root.join("journalLie");
            cq.where(cb.equal(join.get("ID"), j.getID()));
        }

        cq.select(cb.count(root));

        Query query = em.createQuery(cq);
        List resu = query.getResultList();

        try {
            Integer retour = new Integer(resu.get(0).toString());
            return retour;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * *
     * Rechercher une liste de flux par criteria (qui peuvent être null pour
     * absence de critères)
     *
     * @param jLie : le flux doit être lié au journal
     * @param order_by : le champs sur lequel doit porter le trie
     * @param order_desc : true pour obtenir un ordre descendant
     * @param fistResult : pour les limit
     * @param maxResult pour les limit
     * @return : une liste de flux
     */
    public List<Flux> findCretaria(Journal jLie, String order_by, Boolean order_desc, Integer fistResult, Integer maxResult) {
//        em = dAOFactory.getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Flux> cq = cb.createQuery(Flux.class);
        Root<Flux> root = cq.from(Flux.class);


        //La jointure avec whereclause
        if (jLie != null) {
            Join joinFlux = root.join("journalLie");
            cq.where(cb.equal(joinFlux.get("ID"), jLie.getID()));
        }

        // Le ORDER BY
        if (order_by != null) {
            if (order_desc) {
                cq.orderBy(cb.desc(root.get(order_by)));
            } else {
                cq.orderBy(cb.asc(root.get(order_by)));
            }
        }

        TypedQuery<Flux> tq = em.createQuery(cq);

        if (fistResult != null && maxResult != null) {
            tq.setMaxResults(maxResult);
            tq.setFirstResult(fistResult);
        }
        return tq.getResultList();
    }

    public List<Flux> findChildren(Flux flu) {
        String req = "SELECT f FROM Flux f WHERE f.parentFlux=:fl";
        Query query = em.createQuery(req);



        query.setParameter("fl", flu);
        List<Flux> listResult = query.getResultList();

        return listResult;


    }
}
