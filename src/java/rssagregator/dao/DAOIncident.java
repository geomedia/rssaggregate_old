/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import rssagregator.beans.Flux;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.beans.incident.NotificationAjoutFlux;
import rssagregator.services.tache.TacheAlerteMail;

/**
 *
 * @author clem
 */
public class DAOIncident<T> extends AbstrDao {

    Boolean clos;
    Integer fistResult;
    Integer maxResult;
    Boolean nullLastNotification;
//    Boolean criteriaNotificationImperative;
    List<Flux> criteriaFluxLie;
    private static final String REQ_FIND_ALL_AC_LIMIT = "SELECT i FROM incidentflux i LEFT JOIN i.fluxLie flux ORDER BY i.dateFin DESC";

    //        private static final String REQ_FIND = "SELECT i FROM incidentflux i LEFT JOIN i.fluxLie flux WHERE id=:id";
//    public DAOIncident() {
//    }
//JOIN item.listFlux flux
    protected DAOIncident(DAOFactory dAOFactory) {

//       this.classAssocie = cc; 


//        this.classAssocie = CollecteIncident.class;
        this.dAOFactory = dAOFactory;
        em = dAOFactory.getEntityManager();
//        nullLastNotification = false;
//        criteriaNotificationImperative = false;
//        clos = false;
        criteriaFluxLie = new ArrayList<Flux>();
    }

    @Deprecated
    public List<T> findAllLimit(Long premier, Long nombre) {
        em = dAOFactory.getEntityManager();
//        em.getTransaction().begin();
        Query query = em.createQuery(REQ_FIND_ALL_AC_LIMIT);
        query.setFirstResult(premier.intValue());
        query.setMaxResults(nombre.intValue());
        List<T> listResult = query.getResultList();

        return listResult;

    }

    /**
     * *
     * Retourne tous les incidents non clos du type envoyé en argument
     *
     * @return
     */
    public List<T> findIncidentNonClos(Class<T> T) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(T);
        Root<T> root = cq.from(T);

        cq.where(cb.isNull(root.get("dateFin")));
        TypedQuery<T> tq = em.createQuery(cq);
        return tq.getResultList();

    }

    @Deprecated
    public List<T> findCriteria(Class<T> T) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<T> cq = cb.createQuery(T);
        Root<T> root = cq.from(T);
        List<Predicate> listWhere = new ArrayList<Predicate>();

        if (clos != null) {
            if (clos) {
                listWhere.add(cb.isNotNull(root.get("dateFin")));
            } else {
                listWhere.add(cb.isNull(root.get("dateFin")));
            }
        }


        if (nullLastNotification != null && nullLastNotification) {
            listWhere.add(cb.isNull(root.get("lastNotification")));
        }

//        if (criteriaNotificationImperative != null && criteriaNotificationImperative) {
//            listWhere.add(cb.and(cb.equal(root.get("notificationImperative"), true)));
//        }

        if (criteriaFluxLie != null && !criteriaFluxLie.isEmpty()) {
            Join joinFlux = root.join("fluxLie");
            listWhere.add(joinFlux.in(criteriaFluxLie));
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

        TypedQuery<T> tq = em.createQuery(cq);
        if (fistResult != null && maxResult != null) {
            tq.setMaxResults(maxResult);
            tq.setFirstResult(fistResult);
        }


        List l = tq.getResultList();
        for (int j = 0; j < l.size(); j++) {
            Object object = l.get(j);
        }
        return l;
    }

    /**
     * *
     * Trouve les incident ouvert pour le flux envoyé en argument
     *
     * @param fluxId : l'id du flux
     * @return
     */
    @Deprecated
    public List<T> findIncidentOuvert(Long fluxId) {
        String req = "SELECT i FROM i_collecteincident i JOIN i.fluxLie flux WHERE flux.ID=:idflux AND i.dateFin IS NULL";
        Query query = em.createQuery(req);
        query.setParameter("idflux", fluxId);
        return query.getResultList();
    }

    /**
     * *
     * Ne prend pas en compte la généricité. Ne permet que de trouver des incident de flux
     *
     * @return
     */
    public List<T> findAllOpenIncident() {
        String req = "SELECT i FROM i_superclass i WHERE i.dateFin IS NULL";
//        String req = "SELECT i FROM incidentflux i";
        Query query = em.createQuery(req);

        List<T> l = query.getResultList();
        return l;
    }

    /**
     * *
     * Retourne les incident de collecte pour le flux F. Les incident sont de la class envoyé en argument.
     *
     * @param f
     * @param T
     * @return
     */
    public List<T> findOpenCollecteIncident(Flux f, Class<T> T, Boolean datefinnull) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<T> cq = cb.createQuery(T);
        Root<T> root = cq.from(T);
        Predicate where = cb.conjunction();

        where = cb.equal(root.get("fluxLie"), f);


        if (datefinnull != null) {
            if (datefinnull) {
                where = cb.and(where, cb.and(root.get("champdate").isNull()));
            } else {
                where = cb.and(where, cb.and(root.get("champdate").isNotNull()));
            }
        }

        cq.where(where);

        TypedQuery<T> tq = em.createQuery(cq);

        return tq.getResultList();
    }

    /**
     * *
     * Retourne la liste des incident devant être notifié, cette requête est utilisée par la tache
     * {@link TacheAlerteMail}. Pour des tâche basique ou les critères de recherche ne sont pas déterminé par le
     * contexte, on préfère ne pas utiliser critéria
     *
     * @return
     */
    public List<T> findIncidentANotifier() {
        String req = "SELECT i FROM i_superclass i WHERE i.lastNotification is null";
        Query q = em.createQuery(req);
        return q.getResultList();
    }

    /**
     * *
     * Retourne toute les NotificationAjoutFlux pour le flux envoyé en argument
     *
     * @param f
     * @return
     */
    public List<NotificationAjoutFlux> findNotificationAjoutFluxFromFlux(Flux f) {
        String req = "SELECT n FROM i_ajoutflux n WHERE n.fluxAjoute IN :id";

        //f WHERE n.fluxAjoute IN :id
        List<Flux> listflux = new ArrayList<Flux>();
        listflux.add(f);

        Query q = em.createQuery(req);
        q.setParameter("id", listflux);

        List resu = q.getResultList();
        return resu;

    }

    public static void main(String[] args) {


        Flux fl = new Flux();
        fl.setID(new Long(300));

        DAOIncident<NotificationAjoutFlux> dao = new DAOIncident<NotificationAjoutFlux>(DAOFactory.getInstance());
        dao.findNotificationAjoutFluxFromFlux(fl);

//        DAOIncident<JMSPerteConnectionIncident> dao = new DAOIncident<JMSPerteConnectionIncident>(DAOFactory.getInstance());
//        dao.setClos(false);
//        List l = dao.findCriteria(JMSPerteConnectionIncident.class);
//        for (int i = 0; i < l.size(); i++) {
//            Object object = l.get(i);
//        }
    }

//        @Override
//    public CollecteIncident find(Long id) {
//            
//            
//        return super.find(id); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//    
//    
    public Integer findnbMax(Class<T> T) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery cq = cb.createQuery(T);
        Root<CollecteIncident> root = cq.from(T);
        List<Predicate> listWhere = new ArrayList<Predicate>();


        if (clos != null) {
            if (clos) {
                listWhere.add(cb.isNotNull(root.get("dateFin")));
            } else {
                listWhere.add(cb.isNull(root.get("dateFin")));
            }
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
//        CriteriaQuery cq = cb.createQuery(CollecteIncident.class);
//        Root root = cq.from(CollecteIncident.class);

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

    public Boolean getNullLastNotification() {
        return nullLastNotification;
    }

    public void setNullLastNotification(Boolean nullLastNotification) {
        this.nullLastNotification = nullLastNotification;
    }

//    public Boolean getCriteriaNotificationImperative() {
//        return criteriaNotificationImperative;
//    }
//
//    public void setCriteriaNotificationImperative(Boolean criteriaNotificationImperative) {
//        this.criteriaNotificationImperative = criteriaNotificationImperative;
//    }

    public List<Flux> getCriteriaFluxLie() {
        return criteriaFluxLie;
    }

    public void setCriteriaFluxLie(List<Flux> criteriaFluxLie) {
        this.criteriaFluxLie = criteriaFluxLie;
    }
}
