/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

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
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.incident.FluxIncident;

/**
 *
 * @author clem
 */
public class DAOIncident extends AbstrDao {

    Boolean clos;
    Integer fistResult;
    Integer maxResult;
    private static final String REQ_FIND_ALL_AC_LIMIT = "SELECT i FROM incidentflux i LEFT JOIN i.fluxLie flux ORDER BY i.dateFin DESC";
//        private static final String REQ_FIND = "SELECT i FROM incidentflux i LEFT JOIN i.fluxLie flux WHERE id=:id";

//JOIN item.listFlux flux
    protected DAOIncident(DAOFactory dAOFactory) {
        this.classAssocie = FluxIncident.class;
        this.dAOFactory = dAOFactory;
        em = dAOFactory.getEntityManager();
    }

    public List<FluxIncident> findAllLimit(Long premier, Long nombre) {
        em = dAOFactory.getEntityManager();
//        em.getTransaction().begin();
        Query query = em.createQuery(REQ_FIND_ALL_AC_LIMIT);
        query.setFirstResult(premier.intValue());
        query.setMaxResults(nombre.intValue());
        List<FluxIncident> listResult = query.getResultList();

        return listResult;

    }

    public List<FluxIncident> findCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<FluxIncident> cq = cb.createQuery(FluxIncident.class);
        Root<FluxIncident> root = cq.from(FluxIncident.class);
        List<Predicate> listWhere = new ArrayList<Predicate>();


        if (clos) {
            listWhere.add(cb.isNotNull(root.get("dateFin")));
        } else {
            listWhere.add(cb.isNull(root.get("dateFin")));
        }



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

        TypedQuery<FluxIncident> tq = em.createQuery(cq);
        if (fistResult != null && maxResult != null) {
            tq.setMaxResults(maxResult);
            tq.setFirstResult(fistResult);
        }



        return tq.getResultList();
    }

    /**
     * *
     * Trouve les incident ouvert pour le flux envoyé en argument
     *
     * @param fluxId : l'id du flux
     * @return
     */
    public List<FluxIncident> findIncidentOuvert(Long fluxId) {
        String req = "SELECT i FROM incidentflux i JOIN i.fluxLie flux WHERE i.ID=:idflux";

        Query query = em.createQuery(req);
        query.setParameter("idflux", fluxId);
        return query.getResultList();
    }

//        @Override
//    public FluxIncident find(Long id) {
//            
//            
//        return super.find(id); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//    
//    
    public Integer findnbMax() {
        
            CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery cq = cb.createQuery(FluxIncident.class);
        Root<FluxIncident> root = cq.from(FluxIncident.class);
        List<Predicate> listWhere = new ArrayList<Predicate>();


        if (clos) {
            listWhere.add(cb.isNotNull(root.get("dateFin")));
        } else {
            listWhere.add(cb.isNull(root.get("dateFin")));
        }



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
        
        



//        return tq.getResultList();
        
        
        
        
        
//        
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//
//        CriteriaQuery cq = cb.createQuery(FluxIncident.class);
//        Root root = cq.from(FluxIncident.class);

        //La jointure avec whereclause
//        if (j != null) {
//            Join join = root.join("journalLie");
//            cq.where(cb.equal(join.get("ID"), j.getID()));
//        }
                 

        cq.select(cb.count(root));

        Query query = em.createQuery(cq);
        List resu = query.getResultList();

        try {
            Integer retour = new Integer(resu.get(0).toString());
            System.out.println("NB MAX : " + retour);
            return retour;
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean getClos() {
        return clos;
    }

    public void setClos(Boolean clos) {
        this.clos = clos;
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
}
