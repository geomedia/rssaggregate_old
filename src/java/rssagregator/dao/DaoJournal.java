/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;
import rssagregator.beans.Flux;
import rssagregator.beans.Journal;
import rssagregator.services.ServiceCollecteur;

/**
 * @author clem
 */
public class DaoJournal extends AbstrDao {

 
    
    

    
//    public Journal find(Long id) {
//        initEntityManager();
//        em.getTransaction().begin();
//        Journal resu = em.find(Journal.class, id);
//
//        em.getTransaction().commit();
//        System.out.println("journal +++ " + resu);
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
    //        System.out.println("JE suis : " + this.getClass().getCanonicalName());
    //        Query query = em.createQuery(REQ_FIND_ALL);
    //        List<Object> result = query.getResultList();
    //
    //        return result;
    //    }
    //
    //    public void remove(Object object) {
    //        initEntityManager();
    //        em.getTransaction().begin();
    //        System.out.println("");
    //        em.remove(em.merge(object));
    //        em.getTransaction().commit();
    //    }

    @Override
    public void remove(Object obj) throws Exception{
        
        // Il faut supprimer tous les flux lié au journal. (Une simple cascade risquerait de laisser des items orphelines car on ne peut pas cascader en delete sur la list des item d'un flux)
        
        Journal journal = (Journal) obj;
        List<Flux> listflux = journal.getFluxLie();
        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
        System.out.println("NOMBRE DE JOURNAUX AU MOMENT DU DELET : " + listflux.size());
        int i;
        for(i=0;i<listflux.size(); i++){
            daoFlux.remove(listflux.get(i));
            // On désactive le flux pour que l'objet cesse d'être ajouté au collecteur
            listflux.get(i).setActive(false);

            System.out.println("SUPPRESSION D'UN FLUX A PARTIR DU JOURNAL");
            
            
        }
        ServiceCollecteur.getInstance().update(null, "reload all");
//        DAOFactory.getInstance().getDAOFlux().forceNotifyObserver();
//        DAOFactory.getInstance().getDAOFlux().notifyObservers();
        
        journal.setFluxLie(new ArrayList<Flux>());
        em = DAOFactory.getInstance().getEntityManager();
        
        em.getTransaction().begin();
        em.remove(em.merge(obj));
        em.getTransaction().commit();
//        super.remove(obj); //To change body of generated methods, choose Tools | Templates.
        
         
    }
    
    
}
