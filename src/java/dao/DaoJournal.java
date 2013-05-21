/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import javax.persistence.Query;
import rssagregator.beans.Journal;

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
        daoJournal.creer(journal);
    }

    protected DaoJournal(DAOFactory daof) {
        super();
        this.dAOFactory = daof;
        this.classAssocie = Journal.class;
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
}
