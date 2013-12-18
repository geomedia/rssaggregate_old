/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

/**
 * Interface indiquant que le tache s'applique sur beans
 * @author clem
 */
public interface TacheActionableSurUnBean {
    
    /***
     * retroune le beans sur lequel s'applique la tache
     * @return 
     */
    public Object returnBeanCible();
    
}
