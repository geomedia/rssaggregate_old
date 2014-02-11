/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import rssagregator.beans.Journal;

/**
 * @author clem
 */
public class DaoJournal extends AbstrDao {

    //------------------------------------------------
    //-----Parametre criteria
    private String criteriaLangue;
    private String criteriaPays;
    private String criteriaTypeJournal;
    Integer fistResult; // Première clause de la limite 
    Integer maxResult; // nombre maximum d'item à retourner
//    private Integer criteriaPage;
//    private Integer criteriaRow;
////    private Integer criteriaSidx;
//    private String criteriaSidx; // La colonne sur laquel il faut ordonner les resultat
//    private String criteriaSord; // le sens de l'ordre asc desc
//    private Integer criteriaStartRow;
//    private List<SearchFilter> criteriaSearchFilter = new ArrayList<SearchFilter>(); // Liste de filtre basée sur les info fournis par la grid JqGrid

    //------------------------------------------------
//    public Journal find(Long id) {
//        initEntityManager();
//        em.getTransaction().begin();
//        Journal resu = em.find(Journal.class, id);
//
//        em.getTransaction().commit();
//        return resu;
//    }
    public static void main(String[] args) {
        // Créaion d'un journal 
        Journal journal = new Journal();
        journal.setNom("truc");
        DaoJournal daoJournal = new DaoJournal(DAOFactory.getInstance());
        try {
            daoJournal.creer(journal);
        } catch (Exception ex) {
            Logger.getLogger(DaoJournal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected DaoJournal(DAOFactory daof) {
//        super();
        this.dAOFactory = daof;
        this.classAssocie = Journal.class;
        em = daof.getEntityManager();
    }

    /**
     * *
     * Retourne la liste des journaux ordonné par titre
     *
     * @return
     */
    public List<Journal> findallOrederByTitre(boolean afficherJournauxSansFlux) {

        Query req;
        if(afficherJournauxSansFlux){
            req = em.createQuery("SELECT j FROM Journal j ORDER BY j.nom");
        }
        else{
//            req = em.createQuery("SELECT j FROM Journal j ORDER BY j.nom");
            req = em.createQuery("SELECT DISTINCT(j) FROM Journal j JOIN j.fluxLie f ORDER BY j.nom");
        }
//        Query req = em.createQuery("SELECT j FROM Journal j ORDER BY j.nom");
        List<Journal> resu = req.getResultList();
        return resu;
    }

    //    public void modifier(Journal journal) {
    //        if (journal.getID() != null && journal.getID() >= 0) {
    //            initEntityManager();
    //            em.getTransaction().begin();
    //            em.merge(journal);
    //            em.getTransaction().commit();
    //        }
    //    }
    //    public List<Object> findall() {
    //
    //        initEntityManager();
    //        em.getTransaction().begin();
    //
    //        Query query = em.createQuery(REQ_FIND_ALL);
    //        List<Object> result = query.getResultList();
    //
    //        return result;
    //    }
    //
    //    public void remove(Object object) {
    //        initEntityManager();
    //        em.getTransaction().begin();
    //        em.remove(em.merge(object));
    //        em.getTransaction().commit();
    //    }
//    @Override
//    public void remove(Object obj) throws Exception {
//
//        // Il faut supprimer tous les flux lié au journal. (Une simple cascade risquerait de laisser des items orphelines car on ne peut pas cascader en delete sur la list des item d'un flux)
//
//        Journal journal = (Journal) obj;
//        List<Flux> listflux = journal.getFluxLie();
//        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
//        int i;
//        for (i = 0; i < listflux.size(); i++) {
//            daoFlux.remove(listflux.get(i));
//            // On désactive le flux pour que l'objet cesse d'être ajouté au collecteur
//            listflux.get(i).setActive(false);
//
//
//
//        }
//        ServiceCollecteur.getInstance().update(null, "reload all");
////        DAOFactory.getInstance().getDAOFlux().forceNotifyObserver();
////        DAOFactory.getInstance().getDAOFlux().notifyObservers();
//
//        journal.setFluxLie(new ArrayList<Flux>());
//        super.remove(obj);
////        em = DAOFactory.getInstance().getEntityManager();
////        em.getTransaction().begin();
////        em.remove(em.merge(obj));
////        em.getTransaction().commit();
////        super.remove(obj); //To change body of generated methods, choose Tools | Templates.
//
//
//    }
    /**
     * *
     * Recherche une liste de jounaux possédant le nom mentionné. Recherche insensible à la casse caractère
     *
     * @param name le nom recherché
     * @return Une liste de journaux. D'après les contrainte ce retour ne doit posséder qu'un élément dans la liste
     */
    public Journal findWithName(String name) throws NonUniqueResultException, NoResultException, Exception {
        String req = "SELECT j FROM Journal j WHERE UPPER(j.nom) like(:name)";
        Query query = em.createQuery(req);
        query.setParameter("name", name.toUpperCase().trim());

        return (Journal) query.getSingleResult();
//        List r = query.getResultList();


//        return r;
    }

    /**
     * *
     * Méthode interne à la dao utilisé par les méthode findCriteria et cptCriteria
     *
     * @param count Précise si il s'agit d'un count. Si oui la query utilise uin count si non elle se prépare a renvoyer
     * une liste de résultat
     * @return
     */
//    @Override
//    protected TypedQuery gestionCriteria(Boolean count) {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        List<Predicate> listWhere = new ArrayList<Predicate>();
//
//        CriteriaQuery cq = cb.createQuery(Journal.class);
//        Root root = cq.from(Journal.class);
//
////        //--------> CRITERE LANGUE
////        if (criteriaLangue != null && !criteriaLangue.isEmpty()) {
////            listWhere.add(cb.and(cb.equal(cb.lower(root.get("langue")), criteriaLangue.toLowerCase())));
////        }
////
////        //--------> CRITERE PAYS
////        if (criteriaPays != null && !criteriaPays.isEmpty()) {
////            listWhere.add(cb.and(cb.equal(cb.lower(root.get("pays")), criteriaPays.toLowerCase())));
////        }
//
//
//        if (criteriaSearchFilter != null && !criteriaSearchFilter.isEmpty()) {
//            for (int i = 0; i < criteriaSearchFilter.size(); i++) {
//                SearchFilter searchFilter = (SearchFilter) criteriaSearchFilter.get(i);
//                
//               
//                if (searchFilter.getOp().equals("eq")) {
//                    listWhere.add(cb.and(cb.equal(cb.lower(root.get(searchFilter.getField())), searchFilter.getData().toLowerCase())));
//                }
//                else if(searchFilter.getOp().equals("cn")){
//                    listWhere.add(cb.and(cb.like(cb.lower(root.get(searchFilter.getField())), "%"+searchFilter.getData().toLowerCase()+"%")));
//                }
//            }
//        }
//
//
//
//        //---------Gestion de l'ordre.
//        if (!count) {
//            if (criteriaSidx != null && !criteriaSidx.isEmpty() && !criteriaSidx.equals("invid") && criteriaSord != null && (criteriaSord.toLowerCase().equals("asc") || criteriaSord.toLowerCase().equals("desc"))) {
//                if (criteriaSord.toLowerCase().equals("desc")) {
//                    cq.orderBy(cb.desc(root.get(criteriaSidx)));
//                } else if (criteriaSord.toLowerCase().equals("asc")) {
//                    cq.orderBy(cb.asc(root.get(criteriaSidx)));
//                }
//            }
//        }
//
//
//
//
//        // On applique les wheres
//        int i;
//        if (listWhere.size() == 1) {
//
//            cq.where(listWhere.get(0));
//        } else if (listWhere.size() > 1) {
//            Predicate pr = cb.and(listWhere.get(0));
//            for (i = 1; i < listWhere.size(); i++) {
//                pr = cb.and(pr, listWhere.get(i));
//            }
//            cq.where(pr);
//        }
//
//        if (count) {
//            cq.select(cb.count(root));
//            TypedQuery tq = em.createQuery(cq);
//            return tq;
//        } else {
//            TypedQuery<Journal> tq = em.createQuery(cq);
//            // Gestion des limites si ce n'est pas un compte de l'ensemble des résultats
//            if (criteriaStartRow != null && criteriaStartRow != null) {
//                tq.setMaxResults(criteriaRow);
//                tq.setFirstResult(criteriaStartRow);
//            }
//            return tq;
//        }
//    }
    /**
     * *
     * Retourne une liste de journaux en fonction des paramettre criteria prédéfini dans la dao.
     *
     * @return
     */
//    @Override
//    public List<Journal> findCriteria() {
//
////        CriteriaQuery<Journal> cq = gestionCriteria();
//
////        CriteriaBuilder cb = em.getCriteriaBuilder();
////        List<Predicate> listWhere = new ArrayList<Predicate>();
////
////        CriteriaQuery<Journal> cq = cb.createQuery(Journal.class);
////        Root<Journal> root = cq.from(Journal.class);
////
////
////        //--------> CRITERE LANGUE
////        if (criteriaLangue != null && !criteriaLangue.isEmpty()) {
////            listWhere.add(cb.and(cb.equal(root.get("langue"), criteriaLangue)));
////        }
////
////        //--------> CRITERE PAYS
////        if (criteriaPays != null && !criteriaPays.isEmpty()) {
////            listWhere.add(cb.and(cb.equal(root.get("pays"), criteriaPays)));
////        }
////
////
////        //---------Gestion de l'ordre.
////        if (criteriaSidx != null && !criteriaSidx.isEmpty() && !criteriaSidx.equals("invid") && criteriaSord != null && (criteriaSord.toLowerCase().equals("asc") || criteriaSord.toLowerCase().equals("desc"))) {
////            if (criteriaSord.toLowerCase().equals("desc")) {
////                cq.orderBy(cb.desc(root.get(criteriaSidx)));
////            } else if (criteriaSord.toLowerCase().equals("asc")) {
////                cq.orderBy(cb.asc(root.get(criteriaSidx)));
////            }
////        }
////
////
////
////        // On applique les wheres
////        int i;
////        if (listWhere.size() == 1) {
////
////            cq.where(listWhere.get(0));
////        } else if (listWhere.size() > 1) {
////            Predicate pr = cb.and(listWhere.get(0));
////            for (i = 1; i < listWhere.size(); i++) {
////                pr = cb.and(pr, listWhere.get(i));
////            }
////            cq.where(pr);
////        }
//
//        // application de la limite
//        TypedQuery<Journal> tq = gestionCriteria(false);
//
//        if (fistResult != null && maxResult != null) {
//            tq.setMaxResults(maxResult);
//            tq.setFirstResult(fistResult);
//        } else {
//        }
//        List<Journal> listResu = tq.getResultList();
//        return listResu;
//    }
//
//    /**
//     * *
//     * Utilise la requête criteria et renvoie le nombre maximum d'items
//     *
//     * @return
//     */
//    @Override
//    public Integer cptCriteria() {
////         CriteriaQuery<Journal> cq = gestionCriteria();
//        TypedQuery<Journal> tq = gestionCriteria(true);
//        List resu = tq.getResultList();
//        try {
//            Integer retour = new Integer(resu.get(0).toString());
//            return retour;
//        } catch (Exception e) {
//            logger.debug("erreur lors du compte", e);
//            return null;
//        }
//    }
    public String getCriteriaLangue() {
        return criteriaLangue;
    }

    public void setCriteriaLangue(String criteriaLangue) {
        this.criteriaLangue = criteriaLangue;
    }

    public String getCriteriaPays() {
        return criteriaPays;
    }

    public void setCriteriaPays(String criteriaPays) {
        this.criteriaPays = criteriaPays;
    }

    public String getCriteriaTypeJournal() {
        return criteriaTypeJournal;
    }

    public void setCriteriaTypeJournal(String criteriaTypeJournal) {
        this.criteriaTypeJournal = criteriaTypeJournal;
    }
}
