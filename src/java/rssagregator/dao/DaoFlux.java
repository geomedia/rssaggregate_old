/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxType;
import rssagregator.beans.Item;
import rssagregator.beans.Journal;
import rssagregator.beans.traitement.MediatorCollecteAction;

/**
 *
 * @author clem
 */
public class DaoFlux extends AbstrDao {

//    public List<Flux> listFlux;
    private static String REQ_FIND_ALL = "SELECT f FROM Flux f";
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DaoFlux.class);

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

//        em.refresh(flux);
//        
//        System.out.println("=====================");
//        List<FluxIncident> listI = flux.getIncidentsLie();
//        for (int i = 0; i < listI.size(); i++) {
//            CollecteIncident fluxIncident = listI.get(i);
//            System.out.println("INCIDEBNT : " + fluxIncident);
//        }
//        System.out.println("=====================");


        // On doit suppimer les items liées si il sont orphelin. Une cascade classique de l'ORM ne peut convenir 
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        List<Item> items = daoItem.findByFlux(flux.getID());

        int i;
        for (i = 0; i < items.size(); i++) {
            //Supppression des items qui vont devenir orphelines
            if (items.get(i).getListFlux().size() < 2) {

                // On supprimer la relation 
                items.get(i).getListFlux().clear();
                daoItem.modifier(items.get(i));
                daoItem.remove(items.get(i));
            } else { // Sinon on détach le flux
                items.get(i).getListFlux().remove(flux);
                daoItem.modifier(items.get(i));
            }
        }

        // On supprime la liste de flux du flux
        flux.setItem(new ArrayList<Item>());

        //On supprime le flux
        super.remove(flux);
//        em.getTransaction().begin();
//        em.remove(em.merge(flux));
//        em.getTransaction().commit();

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
        Query query = em.createQuery(REQ_FIND_ALL);
        if (!sql) {
            query.setHint("eclipselink.cache-usage", "CheckCacheOnly");

        }
        List<Flux> result = query.getResultList();
        return result;
    }

    /**
     * *
     * Charger les flux depuis la base de données. Les dernier hash des items
     * sont aussi chargé pour résidé en mémoire
     */
    public void chargerDepuisBd() {
        logger.info("Chargement des flux depuis la base de données");

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
            
            //On enregistre le flux à ses services
            fl.enregistrerAupresdesService();


            //On doit également charger les incident en cours pour les flux
//            DAOIncident dAOIncident = DAOFactory.getInstance().getDAOIncident();
//            fl.setIncidentEnCours(dAOIncident.findIncidentOuvert(fl.getID()));
        }
    }

    /**
     * *
     * Ajoute un flux à la liste des flux collecté puis persiste dans la base de
     * donées. Pour chacun des objet lié au flux (journal comportement, type),
     * on va vérifier que l'objet n'est pas déjà présent dans la base de donnée
     * pour éviter la double créeation par la cascade persit. A la fin de
     * l'enregistrement, on met le change statut du bean flux a true afin qu'il
     * puisse si besoin est être notifié aux observeur (le service de collecte).
     *
     * @param obj Le flux a créer
     */
    @Override
    public void creer(Object obj) throws Exception {

        // On va remplir la date en force
        Flux fl = (Flux) obj;
        fl.setCreated(new Date());


        /**
         * *
         * La cascade persist nous oblige à vérifier que chacun des objet lié
         * n'est pas déjà existant. Si il est existant, il faut retrouver ces
         * objet et les mettre dans le flux avant de persister. Si on ne le fait
         * pas, l'ORM va chercher à créer 2x la même chose et provoquer une
         * erreur *
         */
        MediatorCollecteAction m = fl.getMediatorFlux();
        if (m != null) {
            //Si le médiator n'est pas contenu dans l'em (parce que si il est dans l'em pas de problème, l'ORM se s'emmelera pas les pattes !)
            if (!em.contains(m)) {
                //On va le chercher dans la base de données
                MediatorCollecteAction mContenu = em.find(MediatorCollecteAction.class, m.getID());
                if (mContenu != null) {
                    fl.setMediatorFlux(mContenu);
                }
            }
        }

        //Même traitement pour les journaux
        Journal j = fl.getJournalLie();
        if (j != null) {
            if (!em.contains(j)) {
                Journal jContenu = em.find(Journal.class, j.getID());
                if (jContenu != null) {
                    fl.setJournalLie(jContenu);
                }
            }
        }

        //Même traitement pour les Type de flux
        FluxType ft = fl.getTypeFlux();
        if (ft != null) {
            if (!em.contains(ft)) {
                FluxType ftContenu = em.find(FluxType.class, ft.getID());
                if (ftContenu != null) {
                    fl.setTypeFlux(ftContenu);
                }
            }
        }
        
        System.out.println("PERIODE : " + fl.getPeriodeCaptations().size());
        super.creer(fl);


//        em.getTransaction().begin();
//        em.persist(fl);
//        em.getTransaction().commit();
//        fl.addObserver(ServiceCollecteur.getInstance());
//        fl.forceChangeStatut();
//        fl.notifyObservers();
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

    @Override
    /**
     * *
     * Redéclaration de la méthode modifié. En plus de l'eefet classique de la
     * méthode modifié, cette méthode bloque la DaoFlux. Elle change aussi le
     * changeStatut du beans modifié (un flux est un observable)
     */
    public synchronized void modifier(Object obj) throws Exception {

        Flux flux = (Flux) obj;
        super.modifier(flux);

//        try {
//            if (flux.getID() != null && flux.getID() >= 0) {
////                em = dAOFactory.getEntityManager();
//                em.getTransaction().begin();
//                em.merge(flux);
//                em.getTransaction().commit();
//                flux.forceChangeStatut();
//            }
//
//        } catch (RollbackException e) {
//            ServiceGestionIncident.getInstance().gererIncident(e, flux);
//            System.out.println("EXEPTION BDD");
//            throw e;
//        }
    }

    /**
     * *
     * Enregistre les modifications du flux dans la base de donnée. SI Erreur,
     * on consigne en utilisant le service de gestion des incidents l'observer
     *
     * @param flux
     */
//    @Deprecated
//    public synchronized void modifierFluxZ(Flux flux) throws IllegalStateException, RollbackException, Exception {
//
//        
//        try {
//            if (flux.getID() != null && flux.getID() >= 0) {
////                em = dAOFactory.getEntityManager();
//                em.getTransaction().begin();
//                em.merge(flux);
//                em.getTransaction().commit();
//                flux.forceChangeStatut();
//            }
//
//        } catch (RollbackException e) {
//            ServiceGestionIncident.getInstance().gererIncident(e, flux);
//            System.out.println("EXEPTION BDD");
//            throw e;
//        }
//    }

    /**
     * *
     * Modifi le statut Change de L'observable et notifi les observer.
     */
//    public void forceNotifyObserver() {
//        this.setChanged();
//        this.notifyObservers();
//    }
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
     * @param stable limiter aux flux stable
     * @param actif limiter aux flux actif
     * @return : une liste de flux
     */
    public List<Flux> findCretaria(Journal jLie, String order_by, Boolean order_desc, Integer fistResult, Integer maxResult, Boolean stable, Boolean actif) {
//        em = dAOFactory.getEntityManager();
        List<Predicate> listWhere = new ArrayList<Predicate>();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Flux> cq = cb.createQuery(Flux.class);
        Root<Flux> root = cq.from(Flux.class);


        //La jointure avec whereclause
        if (jLie != null) {
            Join joinFlux = root.join("journalLie");
//            cq.where(cb.equal(joinFlux.get("ID"), jLie.getID()));
            listWhere.add(cb.and(cb.equal(joinFlux.get("ID"), jLie.getID())));
        }

        // Le ORDER BY
        if (order_by != null) {
            if (order_desc) {
                cq.orderBy(cb.desc(root.get(order_by)));
            } else {
                cq.orderBy(cb.asc(root.get(order_by)));
            }
        }

        //STABILITE
        if (stable != null && stable) {
            listWhere.add(cb.and(cb.equal(root.get("estStable"), true)));
//            cq.where(cb.equal(root.get("estStable"), true));
        }

        // WHERE CLAUSE ACTIF
        if (actif != null && actif) {
            listWhere.add(cb.and(cb.equal(root.get("active"), true)));
        }


        // On applique les wheres
        if (listWhere.size() == 1) {

            cq.where(listWhere.get(0));
        } else if (listWhere.size() > 1) {
            Predicate pr = cb.and(listWhere.get(0));
            for (int i = 1; i < listWhere.size(); i++) {
                pr = cb.and(pr, listWhere.get(i));
            }
            cq.where(pr);
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

    public void removeall(List<Flux> flux) throws IllegalArgumentException, TransactionRequiredException, Exception {
        int i;
        for (i = 0; i < flux.size(); i++) {
//            try {
            remove(flux.get(i));
//            } catch (IllegalArgumentException ex) {
//                Logger.getLogger(DaoFlux.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (TransactionRequiredException ex) {
//                Logger.getLogger(DaoFlux.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (Exception ex) {
//                Logger.getLogger(DaoFlux.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
    }
}
