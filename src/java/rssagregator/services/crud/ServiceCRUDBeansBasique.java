/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import rssagregator.dao.AbstrDao;
import rssagregator.dao.DAOFactory;

/**
 *
 * @author clem
 */
public class ServiceCRUDBeansBasique extends AbstrServiceCRUD {

    protected ServiceCRUDBeansBasique() {
    }

    
    
    
    @Override
    public void ajouter(Object obj) throws Exception {
        if (obj != null) {
            //Si le beans n'a pas vocation a être synchronisé on leve une exception

            AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass()); //Récupération de la dao
            dao.beginTransaction();
            dao.creer(obj);
            dao.commit();
        }
    }

    @Override
    public void modifier(Object obj) throws Exception {
        if (obj != null) {

            AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass());
            dao.beginTransaction();
            dao.modifier(obj);
            dao.commit();
        }
    }

    @Override
    public void supprimer(Object obj) throws Exception {
        if (obj != null) {

            AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass());
            dao.beginTransaction();
            dao.remove(obj);
            dao.commit();
        }
    }
}
