/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

/**
 * Pour chaque type de beans, un service permet de gérer le crud. Les méthodes sont a redéfinir pour chaque serviceCRUD.
 *
 * @author clem
 */
public abstract class AbstrServiceCRUD {

    protected AbstrServiceCRUD() {
    }

    /**
     * *
     * Ajouter un beans. La méthode doit être redéclarer dans chacun des servicesCRUD
     *
     * @param obj
     */
    public abstract void ajouter(Object obj) throws Exception;

    /**
     * *
     * Effectue un ajout en utilisant l'em envoyé en argument. L'ajout n'est pas commité, c'est a l'appellant de le
     * faire
     *
     * @param obj
     * @param em
     * @throws Exception
     */
    public abstract void ajouter(Object obj, EntityManager em) throws Exception;

    /**
     * *
     * Modifie le beans envoyé en argument. Un {@link EntityManager} est crée pour cette action. La transaction est crée
     * et commité au sein de cette procédure.
     *
     * @param obj
     * @throws Exception
     */
    public abstract void modifier(Object obj) throws Exception;

    /***
     * Modifie le beans envoyé en argument. A la différence de la méthode {@.
     * @param obj
     * @param em
     * @throws Exception 
     */
    public abstract void modifier(Object obj, EntityManager em) throws Exception;

//    public abstract void modifier(Object obj, EntityManager em) throws Exception;
    public abstract void supprimer(Object obj) throws Exception;

    public abstract void supprimer(Object obj, EntityManager em) throws Exception;
    
    public abstract void supprimerList(List objs)throws Exception;
    public abstract void supprimerList(List<Object> objs, EntityManager em)throws Exception;
    
}
