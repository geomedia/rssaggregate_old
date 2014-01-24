/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

/**
 * Interface indiquant que le tache s'applique sur beans . Pour une Collecte : porte sur une Flux. Retrouver journal porte sur un journal...
 * @author clem
 */
public interface TacheActionableSurUnBean {
    
    /***
     * retroune le beans sur lequel s'applique la tache
     * @return 
     */
    public Object returnBeanCible();
    
}
