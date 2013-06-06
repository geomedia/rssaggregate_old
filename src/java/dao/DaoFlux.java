/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.lang.reflect.Method;
import java.util.ArrayList;
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

/**
 *
 * @author clem
 */
public class DaoFlux extends AbstrDao {

    public List<Flux> listFlux;

//    private static String REQ_FIND_ALL="SELECT f FROM Flux f";
//    private String REQ_FIND_ALL_LIMIT="SELECT f FROM Flux f";
    protected DaoFlux(DAOFactory dAOFactory) {
        this.classAssocie = Flux.class;
        this.dAOFactory = dAOFactory;
        this.listFlux = new ArrayList<Flux>();


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
    public void remove(Flux flux) throws IllegalArgumentException, TransactionRequiredException, Exception {
        em = DAOFactory.getInstance().getEntityManager();

        // On doit suppimer les items liées si il sont orphelin
        List<Item> items = flux.getItem(); //....
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();

        int i;
        for (i = 0; i < items.size(); i++) {
            //Supppression des items qui vont devenir orphelines
            if (items.get(i).getListFlux().size() < 2) {
                daoItem.remove(items.get(i));
                System.out.println("SUPRESSION D'une item");
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
        listFlux.remove(flux);
//        forceChange(); // La notification est effectué par la servlet
//        notifyObservers();
    }
    
    
//     public void removeFlux(Flux flux) throws IllegalArgumentException, TransactionRequiredException, Exception {
//
//        try {
//            DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
//
//            dao.remove(flux);
//            listFlux.remove(flux);
//            forceChange();
//            notifyObservers();
//
//        } catch (Exception e) {
//            // On réenvoie l'exeption, c'est à la servlet d'informer et rediriger l'utilisateur
//            throw e;
//        }
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
    //    }
    //    }
    public List<Flux> getListFlux() {
        return listFlux;
    }

    public void setListFlux(List<Flux> listFlux) {
        this.listFlux = listFlux;
    }

    /**
     * *
     * Charger données (list flux et config) depuis la base de données.
     */
    public void chargerDepuisBd() {

//        //Chargement de la config depuis la base de donnée
//        DAOGenerique dao = DAOFactory.getInstance().getDAOGenerique();
//        dao.setClassAssocie(Conf.class);
//        List<Object> confs = dao.findall();
//        if (confs != null && confs.size() > 0) {
//            Conf conf = (Conf) confs.get(0);
//            this.confCourante = conf;
//        } else {
//            Conf defaultconf = new Conf();
//            defaultconf.setActive(Boolean.TRUE);
//            defaultconf.setNbThreadRecup(5);
//            this.confCourante = defaultconf;
//            dao.creer(confCourante);
//        }

        //Chargement de la liste des flux depuis la BDD

        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
        List<Object> listflux = daoFlux.findall();
        int i;

        for (i = 0; i < listflux.size(); i++) {
            Flux fl = (Flux) listflux.get(i);
            fl.setPeriodiciteCollecte(60);
            this.listFlux.add(fl);

            // Pour chaque flux, on va charger les 100 dernier hash 
            DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
            List<String> dernierHash = daoItem.findLastHash(fl, 100);
            fl.setLastEmpruntes(dernierHash);
        }
    }

    /**
     * *
     * Supprime le flux de la liste des flux collecté en mémoire, et persiste
     * dans la base de données, la modification est notifié aux observeur (le
     * Collecteur)
     *
     * @param flux
     */
   

    /**
     * *
     * Ajoute un flux à la liste des flux collecté puis persiste dans la base de
     * donées. La modification est ensuite notifiée aux observeur (le service de
     * collecte collecteurs)
     *
     * @param f
     */
    public void addFlux(Flux f) throws Exception{
        DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
                
        dao.creer(f);

//        System.out.println("Nombre d'observateur : " + this.countObservers());
        listFlux.add(f);
        forceChange();
        notifyObservers();
    }

    /**
     * *
     * Parcours les flux en mémoire et retourne le flux
     *
     * @return Le flux demandé ou null si il n'a pas été trouvé
     */
    public Flux getflux(Long id) {
        int i = 0;
        while (i < listFlux.size()) {
            if (listFlux.get(i).getID().equals(id)) {
                return listFlux.get(i);
            }
            i++;
        }
        return null;
    }

    /**
     * *
     * Retourne la liste des journaux appartenant au journal envoyé en argument
     *
     * @param j Le journal servant de base à la recherche
     * @return Une liste de flux résultat de la recherche
     */
    public List<Flux> findFluxParJournaux(Journal j) {

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
     * Enregistre les modification du flux dans la base de donnée. Notifi l'observer
     *
     * @param flux
     */
    public void modifierFlux(Flux flux)  throws IllegalStateException , RollbackException, Exception{
        System.out.println("");
        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
        
        
 
            if (flux.getID() != null && flux.getID() >= 0) {
                em = dAOFactory.getEntityManager();
                em.getTransaction().begin();
                em.merge(flux);
                em.getTransaction().commit();
               
        
                System.out.println("FIN DE SAUVEGARDE FLUX");
            }
        
//        daoFlux.modifier(flux);

        forceChange();

        notifyObservers();
    }

    /**
     * *
     * Modifi le statut Change de L'observable.
     */
    public void forceChange() {
        this.setChanged();
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

        em = DAOFactory.getInstance().getEntityManager();
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

    public List<Flux> findCretaria(Journal jLie, String order_by, Boolean order_desc, Integer fistResult, Integer maxResult) {
        em = dAOFactory.getEntityManager();

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
                System.out.println("DESC");
                cq.orderBy(cb.desc(root.get(order_by)));
            } else {
                System.out.println("ASC");
                cq.orderBy(cb.asc(root.get(order_by)));
            }
        }

        TypedQuery<Flux> tq = em.createQuery(cq);

        if (fistResult != null && maxResult != null) {
            tq.setMaxResults(maxResult);
            tq.setFirstResult(fistResult);
            System.out.println("LALA");
        }
        System.out.println("fistResult : " + fistResult);
        System.out.println("maxResult : " + maxResult);

        return tq.getResultList();
    }
}
