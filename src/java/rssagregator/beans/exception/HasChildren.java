/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.exception;

import rssagregator.beans.Flux;
import rssagregator.beans.traitement.ComportementCollecte;

/**
 * Exception a levée si on tente de supprimer un entité possédant des liens ne pouvant être laissé orphelin (exemple
 * suppression d'un {@link ComportementCollecte} possédant des {@link Flux}
 *
 * @author clem
 */
public class HasChildren extends Exception {

    public HasChildren() {
    }

    public HasChildren(String message) {
        super(message);
    }
}
