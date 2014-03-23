/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.util.Observable;

/**
 * <p>Un beans Observable est un objet métier devant notifier ses changement d'état auprès d'observeur.</p>
 * <p>Ce méchanisme certe intéressant n'est PLUS UTILISE. Aucun Bean n'éttend cette class. Ce sont maintenant les
 * ServiceCRUD qui s'occupe des traitement spécifique lors des changement d'état des beans. Les beans ne se notifie
 * ainsi plus a des observeur. On laisse tout de même ce mechanisme en cas d'extenssion.</p>
 *
 * @author clem
 */
@Deprecated
public abstract class AbstrObservableBeans extends Observable {

    /**
     * *
     * Cette méthode abstraite doit être redéclaré dans tout les beans devant s'enregistrer auprès de service. Elle
     * permet, à son lancement d'enregistrer le beans auprès des services comme JMS ou collecteur ou mail. Elle doit
     * être lancée après l'instanciation du beans mais pas dans le constructeur car certaine api ne veullent pas
     * notamment l'ORM
     */
    public abstract void enregistrerAupresdesService();

    /**
     * *
     * Permet de fixer le boolean de flux (qui est un beans observable) à true. Indispensable pour que le beans ne
     * notifie correctement à ses Observer
     */
    public void forceChangeStatut() {
        this.setChanged();
    }
}
