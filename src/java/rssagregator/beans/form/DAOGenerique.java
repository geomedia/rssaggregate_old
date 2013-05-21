/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import dao.AbstrDao;
import dao.DAOFactory;

/**
 * *
 * Cette DEO permet un simple crud. Elle hérite simplement de la classe
 * abstraite
 *
 * @author clem
 */
public class DAOGenerique extends AbstrDao {

    public DAOGenerique(DAOFactory daof) {
        this.dAOFactory = daof;
    }
}
