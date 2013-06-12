/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.eclipse.persistence.internal.jpa.querydef.CompoundExpressionImpl;
import org.eclipse.persistence.internal.jpa.querydef.PredicateImpl;
import org.eclipse.persistence.internal.oxm.schema.model.Restriction;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;

/**
 *
 * @author clem
 *
 *
 *
 */
public class DaoItem extends AbstrDao {

    Flux where_clause_flux = null;
    String order_by;
    Boolean order_desc;
    Integer fistResult;
    Integer maxResult;
    Date date1;
    Date date2;

    protected DaoItem(DAOFactory daof) {
        this.dAOFactory = daof;
        this.classAssocie = Item.class;

        order_desc = false;
    }
    private static final String REQ_FIND_BY_HASH = "SELECT i FROM Item i where i.hashContenu=:hash";
    private static final String REQ_FIND_BY_HASH_AND_FLUX = "SELECT item FROM Item item JOIN item.listFlux flux where item.hashContenu IN (:hash) AND flux.ID=:fluxid";
//    private static final String REQ_FIND_ALL_AC_LIMIT = "SELECT item FROM Item LIMIT :prem, :nbr";
//    private static final String REQ_FIND_ALL_AC_LIMIT = "SELECT item FROM Item item JOIN item.listFlux flux";
    private static final String REQ_FIND_ALL_AC_LIMIT = "SELECT item FROM Item item ORDER BY item.dateRecup DESC";
    private static final String REQ_COUNT_ALL = "SELECT count(item.ID) FROM Item item";
    private static final String REQ_FIND_HASH = "SELECT item.hashContenu FROM Item item JOIN item.listFlux fl WHERE fl.ID=:idfl ORDER BY item.ID DESC";

    /**
     * *
     * Permet de trouver un item à partir de son hash
     *
     * @param hash
     */
    public Item findByHash(String hash) {
        em = dAOFactory.getEntityManager();
//        em.getTransaction().begin();
        Query query = em.createQuery(REQ_FIND_BY_HASH);
        query.setParameter("hash", hash);
        Item result = (Item) query.getSingleResult();
//        em.close();
        return result;
    }

    public List<Item> findCretaria() {

        em = dAOFactory.getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> listWhere = new ArrayList<Predicate>();

        CriteriaQuery<Item> cq = cb.createQuery(Item.class);
        Root<Item> root = cq.from(Item.class);

        //La jointure avec whereclause
        if (where_clause_flux != null) {
            Join joinFlux = root.join("listFlux");

            listWhere.add(cb.equal(joinFlux.get("ID"), where_clause_flux.getID()));
//            cq.where(cb.equal(joinFlux.get("ID"), where_clause_flux.getID()));
        }

        if (date1 != null && date2 != null) {

//            cq.where(cb.between(root.<Date>get("dateRecup"), date1, date2));
//            cq.where(cb.and(cb.between(root.<Date>get("dateRecup"), date1, date2)));
            listWhere.add(cb.and(cb.between(root.<Date>get("dateRecup"), date1, date2)));


//            cq.where(cb.and(restrictions))
        }


//        
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

        
        // On applique les wheres
          int i;
            if (listWhere.size() == 1) {
                cq.where(listWhere.get(0));
            } else if(listWhere.size()>1) {
                Predicate pr= cb.and(listWhere.get(0));
                for (i = 1; i < listWhere.size(); i++) {
                    pr=cb.and(pr, listWhere.get(i));
                }
                cq.where(pr);
            }
        

        
        // application de la limite
        TypedQuery<Item> tq = em.createQuery(cq);

        if (fistResult != null && maxResult != null) {
            tq.setMaxResults(maxResult);
            tq.setFirstResult(fistResult);
            System.out.println("OUI >>");
        }
        return tq.getResultList();
    }

    public static void main(String[] args) {

        System.out.println("dd");

        DaoItem dao = DAOFactory.getInstance().getDaoItem();



        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTime dateTime = fmt.parseDateTime("01/01/2014");
        Date date1 = dateTime.toDate();


        dateTime = fmt.parseDateTime("01/01/2015");
        Date date2 = dateTime.toDate();


        dao.setDate1(date1);
        dao.setDate2(date2);

        List<Item> resu = dao.findCretaria();
        System.out.println("nbr : " + resu.size());
    }

    /**
     * *
     * Retourne le nombre total d'item dans la base de données. Si une jointure
     * est demandé (voir les where clause criteria de cette dao), le count sera
     * restreint aux items joins au flux
     *
     * @return
     */
    public Integer findNbMax() {

        em = DAOFactory.getInstance().getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery cq = cb.createQuery(Item.class);
        Root root = cq.from(Item.class);

        //La jointure avec whereclause
        if (where_clause_flux != null) {
            Join joinFlux = root.join("listFlux");
            cq.where(cb.equal(joinFlux.get("ID"), where_clause_flux.getID()));

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


// ANCIENNE VERSION AVANT CRITERIA
//               TypedQuery<Item> tq = em.createQuery(cq);
//               tq.
//
//
//        em = dAOFactory.getEntityManager();
////        em.getTransaction().begin();
//        Query query = em.createQuery(REQ_COUNT_ALL);
//        Object result = query.getSingleResult();
////        em.close();
//        return new Integer(result.toString());


    }

    public List<Item> findAllLimit(Long premier, Long nombre) {

        em = dAOFactory.getEntityManager();
//        em.getTransaction().begin();

        Query query = em.createQuery(REQ_FIND_ALL_AC_LIMIT);
        query.setFirstResult(premier.intValue());
        query.setMaxResults(nombre.intValue());

//        query.setParameter("prem", premier);
//        query.setParameter("nbr", nombre);


        List<Item> listResult = query.getResultList();
//        em.close();
        return listResult;
    }

    /**
     * *
     * Trouve les items possédant un hash présent dans la liste de hash envoyé
     * en paramètre tout en étant lié au flux précisé en paramètre
     *
     * @param hashContenu : List des items. On va utiliser leur hash pou
     * effectuer la recerche.
     * @param flux : Flux devant être lié aux items
     * @return List de flux possédant un hash dans la liste et étant lié au flux
     * sélectioné.
     */
    public List<Item> findHashFlux(List<Item> hashContenu, Flux flux) {
        em = dAOFactory.getEntityManager();
//        em.getTransaction().begin();

        // Constuction de la liste des hash
        int i;
        String hashParamSQL = "";
        for (i = 0; i < hashContenu.size(); i++) {
            hashParamSQL += "'" + hashContenu.get(i).getHashContenu() + "', ";
        }
        if (hashParamSQL.length() > 2) {
            hashParamSQL = hashParamSQL.substring(0, hashParamSQL.length() - 2);
        }
// TODO : C'est laid de faire des requete mon préparée en plein milieu du code. Mais on n'arive pas a préparer une requete basée su une liste de string
//        Query query = em.createQuery("SELECT item FROM Item item JOIN item.listFlux flux where item.hashContenu IN ("+hashParamSQL+") AND flux.ID=:fluxid");
        Query query = em.createQuery("SELECT item FROM Item item LEFT JOIN fetch item.listFlux WHERE item.hashContenu IN (" + hashParamSQL + ")");
        //LEFT JOIN FETCH item.listFlux

        List<Item> resuList;
        resuList = query.getResultList();
        return resuList;
    }

    /**
     * *
     * Cette méthode est utilisée au démarrage de l'application pour précharger
     * les derniers hash des flux.
     *
     * @param fl
     * @param i
     */
    public List<String> findLastHash(Flux fl, int i) {

        em = dAOFactory.getEntityManager();
        Query query = em.createQuery(REQ_FIND_HASH);
        query.setParameter("idfl", fl.getID());
//        query.setParameter("lim", i);
        query.setFirstResult(0);
        query.setMaxResults(i);

        List<String> resu = query.getResultList();

        return resu;
//        int j;
//         
//        for (j=0; j<resu.size(); j++){
//            System.out.println("hash depart : " + resu.get(j));
//            
//        }        

    }

    public Flux getWhere_clause_flux() {
        return where_clause_flux;
    }

    public void setWhere_clause_flux(Flux where_clause_flux) {
        this.where_clause_flux = where_clause_flux;
    }

    public String getOrder_by() {
        return order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public Boolean getOrder_desc() {
        return order_desc;
    }

    public void setOrder_desc(Boolean order_desc) {
        this.order_desc = order_desc;
    }

    public Integer getFistResult() {
        return fistResult;
    }

    public void setFistResult(Integer fistResult) {
        this.fistResult = fistResult;
    }

    public Integer getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(Integer maxResult) {
        this.maxResult = maxResult;
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public Date getDate2() {
        return date2;
    }

    public void setDate2(Date date2) {
        this.date2 = date2;
    }
}
