/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

/**
 *  Pour chaque type de beans, un service permet de gérer le crud. Les méthodes sont a redéfinir pour chaque serviceCRUD. 
 * @author clem
 */
public abstract class AbstrServiceCRUD {


    
    protected AbstrServiceCRUD() {
    }
    
    
    /***
     * Ajouter un beans. La méthode doit être redéclarer dans chacun des servicesCRUD
     * @param obj 
     */
    public abstract void ajouter(Object obj)throws Exception;
    
    public abstract void modifier(Object obj) throws Exception;
    
    public abstract void supprimer(Object obj) throws Exception;
    
}
