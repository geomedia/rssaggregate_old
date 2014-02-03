/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import rssagregator.beans.traitement.ComportementCollecte;

/**
 * <p>Tout beans ({@link Flux}, {@link Journal}, {@link FluxType}, {@link ComportementCollecte}) devant être
 * synchronisé sur les autres serveur doit implémenter cette interface. </p>
 * <p>Les DAO lors de l'enregistrement, la modification ou la suppression d'une entité vérifient si le beans implémente
 * cet interface afin de diffuser les modifications. Si la diffusion n'est pas possible, les dao Rollback le changement
 * d'état du bean</p>
 *
 * @author clem
 */
public interface BeanSynchronise {

    /**
     * *
     * Methode permettant au beans d'informer le JMS si oui ou non il doit être synchronisé. La redéfinition peut
     * simplement retourner true ou false, mais elle peut aussi faire l'objet d'un calcul plus complexe. Pour Le bean
     * {@link UserAccount}, il ne faut par exemple pas synchroniser le compte root mais juste les compte normaux crée
     * par l'interface.
     *
     * @return
     */
    public Boolean synchroImperative();
}
