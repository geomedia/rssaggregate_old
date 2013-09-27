/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.exception;

/**
 * Cette execption peut être levée pour signaler une incohérence de la donnée. Exemple : un flux ayant deux période de capatation ouverte.
 * @author clem
 */
public class DonneeInterneCoherente extends Exception{

    public DonneeInterneCoherente(String message) {
        super(message);
    }
    
}
