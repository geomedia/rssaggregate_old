/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.exception;

/**
 * Exception levée si aucun mail d'administrateur n'est trouvé lors d'une tache d'envoie de mail
 * @author clem
 */
public class AucunMailAdministateur extends ConfigurationServeurIncomplete{

    public AucunMailAdministateur() {
    }

    public AucunMailAdministateur(String message) {
        super(message);
    }
    
}
