/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import javax.persistence.Query;
import rssagregator.beans.incident.FluxIncident;

/**
 *
 * @author clem
 */
public class DAOIncident extends AbstrDao {

    private static final String REQ_FIND_ALL_AC_LIMIT = "SELECT i FROM incidentflux i LEFT JOIN i.fluxLie flux";
//        private static final String REQ_FIND = "SELECT i FROM incidentflux i LEFT JOIN i.fluxLie flux WHERE id=:id";

//JOIN item.listFlux flux
    protected DAOIncident(DAOFactory dAOFactory) {
        this.classAssocie = FluxIncident.class;
        this.dAOFactory = dAOFactory;
    }

    public List<FluxIncident> findAllLimit(Long premier, Long nombre) {
        em = dAOFactory.getEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery(REQ_FIND_ALL_AC_LIMIT);
        query.setFirstResult(premier.intValue());
        query.setMaxResults(nombre.intValue());
        

        List<FluxIncident> listResult = query.getResultList();


        System.out.println("ICI");
        int i;
        for (i = 0; i < listResult.size(); i++) {
            System.out.println("Incid id : " + listResult.get(i).getID());
            System.out.println("Incid message : " + listResult.get(i).getMessageEreur());
        }
        System.out.println("FIN");

        return listResult;

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
}
