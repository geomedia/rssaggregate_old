/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.exception;

/**
 * Peut être levée lorsque par exemple on chercher une ressource JNDI et qu'elle n'est pas trouvée.
 * @author clem
 */
public class RessourceIntrouvable extends Exception{

    public RessourceIntrouvable() {
    }

    public RessourceIntrouvable(String message) {
        super(message);
    }
    
}
