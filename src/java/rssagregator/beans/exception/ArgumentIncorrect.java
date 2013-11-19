/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.exception;

/**
 * Exception pourvant être levé par une méthode si un argument envoyée possède une valeur incorrect
 * @author clem
 */
public class ArgumentIncorrect extends Exception{

    public ArgumentIncorrect() {
    }

    public ArgumentIncorrect(String message) {
        super(message);
    }
    
    
    
}
