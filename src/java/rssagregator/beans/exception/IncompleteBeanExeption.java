/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.exception;

/**
 *  Exception pouvant être levée si un beans ne possède pas toutes les informations nécessaires
 * @author clem
 */
public class IncompleteBeanExeption extends Exception{

    /***
     * Le beans fautif
     */
    Object beans;
    
    
    public IncompleteBeanExeption() {
    }

    public IncompleteBeanExeption(String message) {
        super(message);
    }

    public Object getBeans() {
        return beans;
    }

    public void setBeans(Object beans) {
        this.beans = beans;
    }
    
    
    
}
