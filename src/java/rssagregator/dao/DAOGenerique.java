/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

/**
 * *
 * Cette DAO implément AbstrDAO sans redéfinir aucune méthode. Elle permet de
 * gérer des entitée simples.
 *
 * @author clem
 */
public class DAOGenerique extends AbstrDao {

    public DAOGenerique(DAOFactory daof) {
        em = daof.getEntityManager();
        this.dAOFactory = daof;
    }
}
