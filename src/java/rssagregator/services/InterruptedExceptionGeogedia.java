/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

/**
 * Une execption propre au projet géomédia. Lorsqu'un Callable est interrompu, il doit emmettre ce type d'exception
 * @author clem
 */
public class InterruptedExceptionGeogedia extends InterruptedException{
    AbstrTacheSchedule task;

    public InterruptedExceptionGeogedia(AbstrTacheSchedule task, String s) {
        super(s);
        this.task = task;
    }
    
}
