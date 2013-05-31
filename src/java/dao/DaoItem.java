/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Parameter;
import javax.persistence.Query;
import org.eclipse.persistence.internal.jpa.querydef.ParameterExpressionImpl;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.services.ListeFluxCollecteEtConfigConrante;
import sun.util.logging.resources.logging;

/**
 *
 * @author clem 
 * 
 *  
 * 
 */
public class DaoItem extends AbstrDao {

    protected DaoItem(DAOFactory daof) {
        this.dAOFactory = daof;
        this.classAssocie = Item.class;
    }
    private static final String REQ_FIND_BY_HASH = "SELECT i FROM Item i where i.hashContenu=:hash";
    private static final String REQ_FIND_BY_HASH_AND_FLUX = "SELECT item FROM Item item JOIN item.listFlux flux where item.hashContenu IN (:hash) AND flux.ID=:fluxid";
//    private static final String REQ_FIND_ALL_AC_LIMIT = "SELECT item FROM Item LIMIT :prem, :nbr";
//    private static final String REQ_FIND_ALL_AC_LIMIT = "SELECT item FROM Item item JOIN item.listFlux flux";
    private static final String REQ_FIND_ALL_AC_LIMIT = "SELECT item FROM Item item";
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
    
    
    public List<Item> findAllLimit(Long premier, Long nombre){
        
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
    

    public static void main(String[] args) {
        DaoItem daoItem = new DaoItem(DAOFactory.getInstance());
        Item r = daoItem.findByHash("zz");
        System.out.println("result : " + r.getTitre());
    }

    
    /***
     * Trouve les items possédant un hash présent dans la liste de hash envoyé en paramètre tout en étant lié au flux précisé en paramètre
     * @param hashContenu : List des items. On va utiliser leur hash pou effectuer la recerche.
     * @param flux : Flux devant être lié aux items
     * @return List de flux possédant un hash dans la liste et étant lié au flux sélectioné.
     */
    public List<Item> findHashFlux(List<Item> hashContenu, Flux flux) { 
        em = dAOFactory.getEntityManager();
//        em.getTransaction().begin();

        // Constuction de la liste des hash
        int i;
        String hashParamSQL = "";
        for (i = 0; i < hashContenu.size(); i++) {
            hashParamSQL += "'"+hashContenu.get(i).getHashContenu()+"', ";
        }
        if (hashParamSQL.length() > 2) {
            hashParamSQL = hashParamSQL.substring(0, hashParamSQL.length() - 2);
        }
// TODO : C'est laid de faire des requete mon préparée en plein milieu du code. Mais on n'arive pas a préparer une requete basée su une liste de string
//        Query query = em.createQuery("SELECT item FROM Item item JOIN item.listFlux flux where item.hashContenu IN ("+hashParamSQL+") AND flux.ID=:fluxid");
        Query query = em.createQuery("SELECT item FROM Item item LEFT JOIN fetch item.listFlux WHERE item.hashContenu IN ("+hashParamSQL+")");
 //LEFT JOIN FETCH item.listFlux

         List<Item> resuList;
        resuList = query.getResultList();
        return resuList;
    }
/***
 * Retourne le nombre total d'item dans la base de données
 * @return 
 */
    public Integer findNbMax() {
                em = dAOFactory.getEntityManager();
//        em.getTransaction().begin();
      Query query =  em.createQuery(REQ_COUNT_ALL);
        Object result = query.getSingleResult();
//        em.close();
        return  new Integer(result.toString());
        
    }
/***
 * Cette méthode est utilisée au démarrage de l'application pour précharger les derniers hash des flux.
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
        
        List<String> resu =  query.getResultList();
        
        return resu;
//        int j;
//         
//        for (j=0; j<resu.size(); j++){
//            System.out.println("hash depart : " + resu.get(j));
//            
//        }        
        
    }
}
