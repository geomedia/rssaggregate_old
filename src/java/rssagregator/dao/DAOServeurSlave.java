/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import rssagregator.beans.ServeurSlave;

/**
 *
 * @author clem
 */
public class DAOServeurSlave extends AbstrDao{

 protected DAOServeurSlave(DAOFactory dAOFactory) {
     this.dAOFactory = dAOFactory;
     this.classAssocie = ServeurSlave.class;
     em = dAOFactory.getEntityManager();
 }

    
    
    
    /***
     * Persiste le {@link ServeurSlave} dans la base de données. La référence à ce serveur slave est ensuite ajouté à la liste de serveur slave de la Confcourante
     * @param obj Un objet de type {@link ServeurSlave}
     * @throws Exception 
     */
    @Override
    public void creer(Object obj) throws Exception {
        super.creer(obj); //To change body of generated methods, choose Tools | Templates.
        //Il faut ajouter le serveur à la conf conrante
        dAOFactory.getDAOConf().getConfCourante().getServeurSlave().add((ServeurSlave) obj);
    }

    /***
     * Supprime le serveur slave de la base de données. La référence à ce serveur slave est ensuite supprimé de la confConrante en mémoire.
     * @param obj Un objet de type {@link ServeurSlave}
     * @throws Exception 
     */
    @Override
    public void remove(Object obj) throws Exception {
        super.remove(obj); //To change body of generated methods, choose Tools | Templates.
        // Lors de la suppression d'un serveur esclave la confcourante en mémoire doit être modifiée
        dAOFactory.getDAOConf().getConfCourante().getServeurSlave().remove((ServeurSlave) obj);
    }
    
    
    
    
}
