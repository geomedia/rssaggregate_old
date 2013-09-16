/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

/**
 *Tout beans (flux, journal, type, comportement) devant être synchronisé sur les autres serveur doit implémenter cette interface
 * @author clem
 */
public interface BeanSynchronise {
    /***
     * Methode permettant au beans d'informer le JMS si oui ou non il doit être synchronisé
     * @return 
     */
    public Boolean synchroImperative();
}
