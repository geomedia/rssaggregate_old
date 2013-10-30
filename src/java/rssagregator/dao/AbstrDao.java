/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.poi.util.Beta;
import rssagregator.beans.BeanSynchronise;
import rssagregator.beans.Flux;
import rssagregator.beans.Journal;
import rssagregator.services.ServiceSynchro;

/**
 * Toutes les DAO doivent hériter de cette class abstraite. Elle définit les actions de base (modifier, créer, find...)
 * pouvant être redéfinit dans les DAO spécialisée pour la gestion d'une entitée particulière.
 *
 * @author clem
 */
public abstract class AbstrDao<T> {

    protected EntityManager em;
    protected EntityManagerFactory emf;
//    protected EntityTransaction tr;
//    protected List<SearchFilter> criteriaSearchFilter = new ArrayList<SearchFilter>();
    protected SearchFiltersList criteriaSearchFilters = new SearchFiltersList();
    protected String criteriaSidx; // La colonne sur laquel il faut ordonner les resultat
    protected String criteriaSord; // le sens de l'ordre asc desc
    protected Integer criteriaStartRow; //Premier enregistrement permettant de construire la limite
    protected Integer criteriaPage;  // Utile pour la dao ?
    protected Integer criteriaRow; // Nombre d'enregistrement pour construire la limite (second paramettre de la limite).
    /**
     * *
     * La persistence Unit définie dans la config d'Eclipse link. Voir le fichier persistence.xml
     */
    protected String PERSISTENCE_UNIT_NAME = "RSSAgregatePU2";
    /**
     * *
     * Instance de la daofactory.
     */
    protected DAOFactory dAOFactory;
    /**
     * *
     * Cette variable peut être utilisée par certaine dao exemple la daoGenerique pour savoir sur quel type d'entité
     * elle doit agir. Cette variable va peut être être supprimée au profit de la généricité pour une implémentation
     * plus standart
     */
    @Beta
    protected Class classAssocie;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AbstrDao.class);

    /**
     * *
     * Permet de créer l'entité envoyé en argument. Si le beans est un {@link BeanSynchronise}, la DAO va chercher à
     * diffuser la création. En cas d'échec de diffusion elle va rollbacker la création afin de ne pas avoir d'entitée
     * crée sur le serveur maître et absente sur les serveurs esclaves.
     *
     * @param obj Le beans devant être persisté
     * @throws Exception
     */
    public void creer(Object obj) throws Exception {
//        EntityTransaction tr = em.getTransaction();
//        tr.begin();
        em.persist(obj);
//        try {  ---> Effectuer la synchro est maintenant dans la couche service
////            if (BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
////                ServiceSynchro.getInstance().diffuser(obj, "add");
////            }
////            tr.commit();
//        } catch (Exception e) {
//            logger.error("Echec de la suppression du beans : " + e);
////            tr.rollback();
//            throw e;
//        }
    }

    /**
     * *
     * Méthode interne à la dao utilisé par les méthode findCriteria et cptCriteria. C'est cette méthode qui construit
     * la requete en observant les paramettres transmis à la dao
     *
     * @param count
     * @return
     */
    protected TypedQuery gestionCriteria(Boolean count) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> listWhere = new ArrayList<Predicate>();
        System.out.println("GESTION CRITERIA");

        CriteriaQuery cq = cb.createQuery(classAssocie);
        Root root = cq.from(classAssocie);
        if (criteriaSearchFilters != null && !criteriaSearchFilters.getFilters().isEmpty()) {
            System.out.println("---> criteria search filter");
            for (int i = 0; i < criteriaSearchFilters.getFilters().size(); i++) {


                SearchFilter searchFilter = criteriaSearchFilters.getFilters().get(i);
                if (searchFilter.getData() == null || searchFilter.getField() == null || searchFilter.getOp() == null || searchFilter.getType() == null) {
                    continue;
                }
                System.out.println("OPERATOR : " + searchFilter.getOp());
                if (searchFilter.getOp().equals("eq")) {
                    if (searchFilter.getType() != null && searchFilter.getType().equals(String.class)) {
                        listWhere.add(cb.and(cb.equal(cb.lower(root.get(searchFilter.getField())), searchFilter.getData().toString().toLowerCase())));
                    } else if (searchFilter.getType() != null && searchFilter.getType().equals(Long.class)) {
                        try {
                            listWhere.add(cb.and(cb.equal((root.get(searchFilter.getField())), new Long(searchFilter.getData().toString()))));
                        } catch (Exception e) {
                        }
                    }


                } else if (searchFilter.getOp().equals("cn")) {
                    if (searchFilter.getType() != null && searchFilter.getType().equals(String.class)) {
                        listWhere.add(cb.and(cb.like(cb.lower(root.get(searchFilter.getField())), "%" + searchFilter.getData().toString().toLowerCase() + "%")));
                    }

                    //----------> OPERATEUR IN
                } else if (searchFilter.getOp().equals("in")) {
                    // ---> Si la data du filtre est une liste
                    if (searchFilter.getType().isAssignableFrom(List.class)) {
                        try {
                            //Il faut savoir si la jointure est nécessaire
                            Class c = classAssocie.getDeclaredField(searchFilter.getField()).getType();
                            // Si le champs concerné dan la classe métier est une liste, alors on va effectuer une jointure
                            if (List.class.isAssignableFrom(c)) {
                                Join joinFlux = root.join(searchFilter.getField());
                                List lf = (List) searchFilter.getData();
                                listWhere.add(joinFlux.in(lf));
                            }
                        } catch (NoSuchFieldException ex) {
                            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SecurityException ex) {
                            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

//                    }
                    //Construction 
//                    Collection<String> coll = new ArrayList<String>();
//                    String[] tabString = searchFilter.getData().toString().split(",");
//                    for (int j = 0; j < tabString.length; j++) {
//                        String string = tabString[j];
//                        coll.add(string);
//                    }
//
//                    listWhere.add(cb.and(root.get(searchFilter.getField()).in(coll)));
                } //Opérateur supérieur a
                else if (searchFilter.getOp().equals("gt")) {
                    System.out.println("-----Opérator gt");
                    if (searchFilter.getType().equals(Date.class)) {
                        System.out.println("----------CRITERE DE DATE GREATER");
                        Date d = (Date) searchFilter.getData();
                        listWhere.add(cb.and(cb.greaterThan(root.get(searchFilter.getField()), d)));
                    }
                } else if (searchFilter.getOp().equals("lt")) {
                    if (searchFilter.getType().equals(Date.class)) {
                        System.out.println("----------CRITERE DE DATE LESS");
                        Date d = (Date) searchFilter.getData();
                        listWhere.add(cb.and(cb.lessThan(root.get(searchFilter.getField()), d)));
                    }
                }
            }
        }


        //---------Gestion de l'ordre.
        if (!count) {
            if (criteriaSidx != null && !criteriaSidx.isEmpty() && !criteriaSidx.equals("invid") && criteriaSord != null && (criteriaSord.toLowerCase().equals("asc") || criteriaSord.toLowerCase().equals("desc"))) {
                System.out.println("--> criteria sidx : " + criteriaSidx);
                if (criteriaSord.toLowerCase().equals("desc")) {
                    cq.orderBy(cb.desc(root.get(criteriaSidx)));
                } else if (criteriaSord.toLowerCase().equals("asc")) {
                    cq.orderBy(cb.asc(root.get(criteriaSidx)));
                }
            }
        }

        criteriaTraitementDeschampsSpecifique(cq, cb, root, listWhere);

        // On applique les wheres
        int i;
        if (listWhere.size() == 1) {
            System.out.println("LIST WHERE = 1");

            cq.where(listWhere.get(0));
        } else if (listWhere.size() > 1) {
            System.out.println("LIST WEHERE " + listWhere.size());
            Predicate pr = cb.and(listWhere.get(0));
            for (i = 1; i < listWhere.size(); i++) {
                pr = cb.and(pr, listWhere.get(i));
            }
            cq.where(pr);
        }



        if (count) {
            cq.select(cb.count(root));
            TypedQuery tq = em.createQuery(cq);
            return tq;
        } else {
            TypedQuery<Journal> tq = em.createQuery(cq);
            return tq;
        }
    }

    /**
     * *
     * Cette méthode est a redéclarer dans les classes fille. Elle permet d'ajouter des clause à la liste des clause.
     * Les traitement spécifique de la méthode de recherche peuvent ainsi être complété par ce biais.
     *
     * @param cq
     * @param cb
     * @param root
     * @param listWhere
     */
    public void criteriaTraitementDeschampsSpecifique(CriteriaQuery cq, CriteriaBuilder cb, Root root, List<Predicate> listWhere) {
        System.out.println("------> C'est Dans La ClAss MEREE :(");
//        return cq; 
    }

    /**
     * *
     * Lance une recherche en utilisant critéria. Il faut au préalable avoir fixé les critères dans la dao. pour cela,
     * il faut utiliser les setters appropriés (
     *
     * @return
     */
    public List<T> findCriteria() {
        TypedQuery<T> tq = gestionCriteria(false);

        if (criteriaStartRow != null && criteriaRow != null) {
            tq.setMaxResults(criteriaRow);
            tq.setFirstResult(criteriaStartRow);
        } else {
        }
        List<T> listResu = tq.getResultList();
        System.out.println("-@@" + tq.getMaxResults());
        System.out.println("--> List resu size : " + listResu.size());
        return listResu;
    }

    public Integer cptCriteria() {
//         CriteriaQuery<Journal> cq = gestionCriteria();
        TypedQuery<T> tq = gestionCriteria(true);
        List resu = tq.getResultList();
        try {
            Integer retour = new Integer(resu.get(0).toString());
            System.out.println("NBR cptCriteria : " + retour);
            return retour;
        } catch (Exception e) {
            logger.debug("erreur lors du compte", e);
            return null;
        }
    }

    public void initcriteria() {
//            throw new UnsupportedOperationException("pas implémenté dans la classe abstraite");
    }

    ;

    /**
     * *
     * Permet la mofification de l'entitée envoyée en argument. Si le beans est un {@link BeanSynchronise}, la DAO va
     * chercher à diffuser la création. En cas d'échec de diffusion elle va rollbacker la création afin de ne pas avoir
     * d'entitée crée sur le serveur maître et absente sur les serveurs esclaves.
     *
     * @param obj
     * @throws Exception
     */
    public void modifier(Object obj) throws Exception {
        // Test si le flux possède bien un id
        // On récupère l'id
        Method getter = obj.getClass().getMethod("getID");
        Object retour = getter.invoke(obj);
        if (retour != null && retour instanceof Long && (Long) retour >= 0) {
//            EntityTransaction tr = em.getTransaction();
//            tr.begin();
            em.merge(obj);
            try {
                // Si il s'agit d'un beans devant être synchronisé On lance la diff
                if (BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
                    ServiceSynchro.getInstance().diffuser(obj, "mod");
                }
//                tr.commit();
                //En cas d'échec de la synchronisation, on rollback la modification.
            } catch (Exception e) {
                logger.error("erreur lors de la modification d'un beans : " + e + "\n trace : " + e.getStackTrace());
//                tr.rollback();
                throw e;
            }
        }
    }

    /**
     * *
     * Retrouver un objet à patir de son id.
     *
     * @param id
     * @return
     */
    public Object find(Long id) {
        System.out.println("FIND ABSTR");
        Class laclass = this.getClassAssocie();
        try {
            Object resu = em.find(laclass, id);
            return resu;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * *
     * Supprimer l'objet envoyé en argument. Cette action utilise le même principe de diffusion de la modification
     * auprès des serveurs esclaves que créer() et modifier().
     *
     * @param obj le bean à supprimé de la base de données
     */
    public void remove(Object obj) throws Exception {
//        EntityTransaction tr = em.getTransaction();
//        tr.begin();

        em.remove(em.merge(obj));
       

//        try {
//            if (BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
//                ServiceSynchro.getInstance().diffuser(obj, "rem");
//            }
//            tr.commit();
//        } catch (Exception e) {
//
////            tr.rollback();
//            logger.error("Erreur lors de la suppression du beans : " + e);
//        }
    }

    /**
     * *
     * Retourne tous les enregistrement pour le type définit par la variable classAssocie de la dao.
     *
     * @return Une liste d'objet ou null si échec de la requête
     */
    public List<Object> findall() {
        try {
            Class classasso = this.getClassAssocie();
            String req = "SELECT f FROM " + classasso.getSimpleName() + " f";
            Query query = em.createQuery(req);
//            query.setHint("eclipselink.cache-usage", "CheckCacheOnly");
            List<Object> result = query.getResultList();
            return result;
        } catch (SecurityException ex) {
            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
            logger.error("erreur lors de l'execution de la methode findAll : " + ex);
        } catch (IllegalArgumentException ex) {
            logger.error("erreur lors de l'execution de la methode findAll : " + ex);
            Logger.getLogger(AbstrDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public AbstrDao() {
    }

    public Class getClassAssocie() {
        return classAssocie;
    }

    public void setClassAssocie(Class classAssocie) {
        this.classAssocie = classAssocie;
    }

    public DAOFactory getdAOFactory() {
        return dAOFactory;
    }

    public void setdAOFactory(DAOFactory dAOFactory) {
        this.dAOFactory = dAOFactory;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

//    public List<SearchFilter> getCriteriaSearchFilter() {
//        return criteriaSearchFilter;
//    }
//
//    public void setCriteriaSearchFilter(List<SearchFilter> criteriaSearchFilter) {
//        this.criteriaSearchFilter = criteriaSearchFilter;
//    }
    public String getCriteriaSidx() {
        return criteriaSidx;
    }

    public void setCriteriaSidx(String criteriaSidx) {
        this.criteriaSidx = criteriaSidx;
    }

    public String getCriteriaSord() {
        return criteriaSord;
    }

    public void setCriteriaSord(String criteriaSord) {
        this.criteriaSord = criteriaSord;
    }

    public Integer getCriteriaStartRow() {
        return criteriaStartRow;
    }

    public void setCriteriaStartRow(Integer criteriaStartRow) {
        this.criteriaStartRow = criteriaStartRow;
    }

    public Integer getCriteriaPage() {
        return criteriaPage;
    }

    public void setCriteriaPage(Integer criteriaPage) {
        this.criteriaPage = criteriaPage;
    }

    public Integer getCriteriaRow() {
        return criteriaRow;
    }

    public void setCriteriaRow(Integer criteriaRow) {
        this.criteriaRow = criteriaRow;
    }

    public SearchFiltersList getCriteriaSearchFilters() {
        return criteriaSearchFilters;
    }

    public void setCriteriaSearchFilters(SearchFiltersList criteriaSearchFilters) {
        this.criteriaSearchFilters = criteriaSearchFilters;
    }

    /**
     * *
     * Commiter les modif de la dao. Au départ, on commitait dans chaque méthode de dao. Mais il faut parfois effectuer
     * plusieurs action et commiter (ou ne pas) a la fin. Le commit doit ainsi être emblobant
     *
     * @throws IllegalStateException
     * @throws RollbackException
     */
    public void commit() throws IllegalStateException, RollbackException {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
//        em.getTransaction().commit();
    }

    public void beginTransaction() {
//        tr = em.getTransaction();
        em.getTransaction().begin();
    }

    public void roolbackTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

//    public EntityTransaction getTr() {
//        return tr;
//    }
//
//    public void setTr(EntityTransaction tr) {
//        this.tr = tr;
//    }
    
    
    
    
    
}
