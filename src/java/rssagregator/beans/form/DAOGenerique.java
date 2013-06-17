/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import rssagregator.dao.AbstrDao;
import rssagregator.dao.DAOFactory;

/**
 * *
 * Cette DEO permet un simple crud. Elle hérite simplement de la classe
 * abstraite
 *
 * @author clem
 */
public class DAOGenerique extends AbstrDao {

    public DAOGenerique(DAOFactory daof) {
        em = daof.getEntityManager();
        this.dAOFactory = daof;
    }
}
