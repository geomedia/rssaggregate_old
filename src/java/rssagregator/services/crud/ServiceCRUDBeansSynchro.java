/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import rssagregator.beans.BeanSynchronise;
import rssagregator.dao.AbstrDao;
import rssagregator.dao.DAOFactory;
import rssagregator.services.ServiceSynchro;

/**
 * Un certain nombre de beans doivent être synchronisé. Exemple les journaux. Les utilisateur etc. Le traitement des
 * action de CRUD est ainsi différent
 *
 * @author clem
 */
public class ServiceCRUDBeansSynchro extends AbstrServiceCRUD {

    protected ServiceCRUDBeansSynchro() {
    }
    
    

    @Override
    public void ajouter(Object obj) throws Exception {

        if (obj != null) {
            //Si le beans n'a pas vocation a être synchronisé on leve une exception
            if (!BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
                throw new Exception("Ce beans n'est pas synchronisable");
            }

            AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass()); //Récupération de la dao
            dao.beginTransaction();
            dao.creer(obj);
            ServiceSynchro.getInstance().diffuser(obj, "add");
            dao.commit();
        }
    }

    /**
     * *
     * Modifie le beans envoyé en argument. Le beans sera comité dans la base de donnée uniquement si la diffusion a été
     * possible auprès du {@link ServiceSynchro}
     *
     * @param obj
     * @throws Exception
     */
    @Override
    public void modifier(Object obj) throws Exception {
        if (obj != null) {

            if (!BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
                throw new Exception("Ce beans n'est pas synchronisable");
            }

            AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass());
            dao.beginTransaction();
            dao.modifier(obj);
             ServiceSynchro.getInstance().diffuser(obj, "mod");
            dao.commit();
        }
    }

    @Override
    public void supprimer(Object obj) throws Exception {

        if (obj != null) {

            if (!BeanSynchronise.class.isAssignableFrom(obj.getClass())) {
                throw new Exception("Ce beans n'est pas synchronisable");
            }
            AbstrDao dao = DAOFactory.getInstance().getDaoFromType(obj.getClass());
            dao.beginTransaction();
            dao.remove(obj);
            ServiceSynchro.getInstance().diffuser(obj, "rem");
            dao.commit();
        }
    }
}
