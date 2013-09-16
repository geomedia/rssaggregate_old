/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.util.Observable;

/**
 *  Un beans Observable est un objet métier devant notifier ses changement d'état auprès de services (exemple le service de collecte). Liste des beans observable : <ul>
 * <li>Flux : Il doit notifier ses changement d'état auprès du service de collecte.</li>
 * <li>Conf : La conf est enregistré auprès du collecteur. Le collecteur doit en effet se recharger en fonction des paramettre de la conf</li>
 * </ul>
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
