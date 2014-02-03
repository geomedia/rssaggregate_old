/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import rssagregator.beans.exception.HasChildren;
import rssagregator.beans.traitement.ComportementCollecte;
import rssagregator.dao.DAOComportementCollecte;
import rssagregator.dao.DAOFactory;
import rssagregator.services.ServiceSynchro;

/**
 *
 * @author clem
 */
public class ServiceCRUDComportement extends ServiceCRUDBeansSynchro {

    /**
     * *
     * Supprimer un comportement. Le service vérifie au préalable que le comportement n'est lié a aucun flux. Il est en
     * effet impossible de supprimer un comportement si il est encore en service. Cela laisserait les flux orphelin de
     * leur comportement. Il est aussi impensable de supprimer tous les flux liés au comportement. L'administrateur
     * devra ainsi lier les flux à un autre comportement avant de la supprimer
     *
     * @param obj
     * @throws Exception
     */
    @Override
    public void supprimer(Object obj) throws HasChildren, Exception {

        if (obj != null) {
            ComportementCollecte comportement = (ComportementCollecte) obj;
            DAOComportementCollecte dao = DAOFactory.getInstance().getDAOComportementCollecte();
            
            
            if(comportement.getListeFlux()!=null && comportement.getListeFlux().size()!=0){
                throw new HasChildren("Le comportement gère encore des flux. Avant de le supprimer vous devez attribuer un autre comportement à tous les flux gérés par ce comportement");
            }
            
            dao.beginTransaction();
            dao.remove(obj);
            ServiceSynchro.getInstance().diffuser(obj, "rem");
            dao.commit();
            
        } else {
            throw new NullPointerException("On cherche a supprimer un comportement null");
        }
    }
}
