/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.util.Observable;

/**
 *
 * @author clem
 */
public abstract class AbstrObservableBeans extends Observable{
    
    /***
     * Cette méthode abstraite doit être redéclaré dans tout les beans devant s'enregistrer auprès de service. Elle permet, à son lancement d'enregistrer le beans auprès des services comme JMS ou collecteur ou mail. Elle doit être lancée après l'instanciation du beans mais pas dans le constructeur car certaine api ne veullent pas notamment l'ORM
     */
    public abstract void enregistrerAupresdesService();
    
        /**
     * *
     * Permet de fixer le boolean de flux (qui est un beans observable) à true.
     * Indispensable pour que le beans ne notifie correctement à ses Observer
     */
    public void forceChangeStatut() {
        this.setChanged();
    }
    
}
