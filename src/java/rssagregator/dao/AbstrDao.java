/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import rssagregator.beans.Journal;

/**
 * <p>Toutes les DAO doivent hériter de cette class abstraite. Elle définit les actions de base (modifier, créer,
 * find...) pouvant être redéfinit dans les DAO spécialisée pour la gestion d'une entitée particulière.</p>
 * <p>Avec le développement de la couche service, la couche dao a perdu de son importance. Les simples crud peuvent être
 * effectué directement depuis la couche service en utilisant un EntityManager de Eclipse Link. Seul les méthodes
 * permettant de recherc</p>
 *
 * @author clem
 */
public abstract class AbstrDao<T> {

    /**
     * *
     * Entity manager utilisé par la dao
     */
    protected EntityManager em;
    /**
     * *
     * EMF utilisé pour obtenir la dao. Normalement cet EMF provient de la {@link DAOFactory}
     */
    protected EntityManagerFactory emf;
    /**
     * *
     * Un objet perso permettant de générer des requete criteria. Les formulaire doivent permettrent de générer ces
     * objet (le plus souvent en interprétant les paramettres envoyé par JQgrid) en fonction de la requete utilisateur.
     * Les dao parcours ces objets pour effectuer des requêtes en se basant sur critéria.
     */
    protected SearchFiltersList criteriaSearchFilters = new SearchFiltersList();
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
    protected Class classAssocie;
    /**
     * *
     * Le logger de la dao
     */
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    /**
     * *
     * Permet de créer l'entité envoyé en argument. Si le beans est un {@link BeanSynchronise}. L'initialisation de la
     * transaction et le commit n'est pas génée par la méthode. C'est le role de la couche service de gérer la
     * transaction.
     *
     * @param obj Le beans devant être persisté
     * @throws Exception
     */
    public void creer(Object obj) throws Exception {
        em.persist(obj);
    }

    /**
     * *
     * Méthode interne à la dao utilisé par les méthodes findCriteria et cptCriteria. C'est cette méthode qui construit
     * la requete en observant les paramettres transmis à la dao. L'objet {@link AbstrDao#criteriaSearchFilters} est
     * parcouru.
     *
     * @param count
     * @return
     */
    protected TypedQuery gestionCriteria(Boolean count) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> listWhere = new ArrayList<Predicate>();


        CriteriaQuery cq = cb.createQuery(classAssocie);
        cq.distinct(true);
        Root root = cq.from(classAssocie);
        if (criteriaSearchFilters != null && !criteriaSearchFilters.getFilters().isEmpty()) {

            for (int i = 0; i < criteriaSearchFilters.getFilters().size(); i++) {


                SearchFilter searchFilter = criteriaSearchFilters.getFilters().get(i);
                if (searchFilter.getData() == null || searchFilter.getField() == null || searchFilter.getOp() == null || searchFilter.getType() == null) {
                    continue;
                }

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
//                                root.fetch(searchFilter.getField());


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
                    if (searchFilter.getType().equals(Date.class)) {
                        Date d = (Date) searchFilter.getData();
                        listWhere.add(cb.and(cb.greaterThan(root.get(searchFilter.getField()), d)));
                    }
                } else if (searchFilter.getOp().equals("lt")) {
                    if (searchFilter.getType().equals(Date.class)) {
                        Date d = (Date) searchFilter.getData();
                        listWhere.add(cb.and(cb.lessThan(root.get(searchFilter.getField()), d)));
                    }
                } else if (searchFilter.getOp().equals("inn")) {
                    listWhere.add(cb.and(root.get(searchFilter.getField()).isNotNull()));
                } else if (searchFilter.getOp().equals("isn")) {
                    listWhere.add(cb.and(root.get(searchFilter.getField()).isNull()));
                } else {
                }
            }
        }


        //---------Gestion de l'ordre.
        if (!count) {
            if (criteriaSearchFilters.criteriaSidx != null && !criteriaSearchFilters.criteriaSidx.isEmpty() && !criteriaSearchFilters.criteriaSidx.equals("invid") && criteriaSearchFilters.criteriaSord != null && (criteriaSearchFilters.criteriaSord.toLowerCase().equals("asc") || criteriaSearchFilters.criteriaSord.toLowerCase().equals("desc"))) {
                if (criteriaSearchFilters.criteriaSord.toLowerCase().equals("desc")) {
                    cq.orderBy(cb.desc(root.get(criteriaSearchFilters.criteriaSidx)));
                } else if (criteriaSearchFilters.criteriaSord.toLowerCase().equals("asc")) {
                    cq.orderBy(cb.asc(root.get(criteriaSearchFilters.criteriaSidx)));
                }
            }
        }

        criteriaTraitementDeschampsSpecifique(cq, cb, root, listWhere);

        // On applique les wheres
        int i;
        if (listWhere.size() == 1) {

            cq.where(listWhere.get(0));
        } else if (listWhere.size() > 1) {
            Predicate pr = cb.and(listWhere.get(0));
            for (i = 1; i < listWhere.size(); i++) {
                pr = cb.and(pr, listWhere.get(i));
            }
            cq.where(pr);
        }

        if (count) {
//              cq.distinct(true);
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
     * Les traitement spécifique de la méthode de recherche peuvent ainsi être complété par ce biais. Permet de rendre
     * compatible avec un ancien système ou de gérer l'ajout de clause critéria sans passer par
     * {@link AbstrDao#criteriaSearchFilters}. Rappellons que le criteriaSearchFilters est une création perso pas un
     * framework validé a toute épreuse. En passant par cette méthode on peut ainsi faire face a des cas particulier pas
     * prévu dans la gestion du criteriaSearchFilters.
     *
     * @param cq
     * @param cb
     * @param root
     * @param listWhere
     */
    public void criteriaTraitementDeschampsSpecifique(CriteriaQuery cq, CriteriaBuilder cb, Root root, List<Predicate> listWhere) {
    }

    /**
     * *
     * Lance une recherche en utilisant critéria. Il faut au préalable avoir fixé les critères dans la dao (voir l'objet
     * {@link AbstrDao#criteriaSearchFilters}.
     *
     * @return
     */
    public List<T> findCriteria() {
        TypedQuery<T> tq = gestionCriteria(false);

        if (criteriaSearchFilters.criteriaStartRow != null && criteriaSearchFilters.criteriaRow != null) {
            tq.setMaxResults(criteriaSearchFilters.criteriaRow);
            tq.setFirstResult(criteriaSearchFilters.criteriaStartRow);
        } else {
        }

        List<T> listResu = tq.getResultList();

        return listResu;
    }

    public Integer cptCriteria() {
        TypedQuery<T> tq = gestionCriteria(true);
        List resu = tq.getResultList();
        try {
            Integer retour = new Integer(resu.get(0).toString());
            return retour;
        } catch (Exception e) {
            logger.debug("erreur lors du compte", e);
            return null;
        }
    }

    /**
     * *
     * Purge tout les critères criteria. Peut être redéfinit dans les dao filles
     */
    public void initcriteria() {
        criteriaSearchFilters = new SearchFiltersList();
    }

    /**
     * *
     * Permet la mofification de l'entitée envoyée en argument.
     *
     * @param obj
     * @throws Exception
     */
    public void modifier(Object obj) throws Exception {
        em.merge(obj);

    }

    /**
     * *
     * Retrouver un objet à patir de son id.
     *
     * @param id
     * @return
     */
    public Object find(Long id) {
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
        em.remove(em.merge(obj));
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
            logger.error("erreur lors de l'execution de la methode findAll : ", ex);
        } catch (IllegalArgumentException ex) {
            logger.error("erreur lors de l'execution de la methode findAll : ", ex);
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

    /**
     * *
     * Démarre une transaction pour l'em de la dao.
     */
    public void beginTransaction() {
//        tr = em.getTransaction();
        em.getTransaction().begin();
    }

    /**
     * *
     * si l'em associé a la dao possède une transaction, on la rollback
     */
    public void roolbackTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    /**
     * *
     * Provoque la fermeture de l'Em de la dao
     */
    public void closeEm() {
        if (em != null) {
            if (em.isOpen()) {
                try {
                    em.close();
                    logger.debug("fermeture d'un em par finalyse");
                } catch (Exception e) {
                }
            }
        }
    }
}