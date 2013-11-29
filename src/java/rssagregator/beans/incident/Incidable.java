/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import rssagregator.beans.exception.UnIncidableException;
import rssagregator.services.tache.TacheRecupCallable;

/**
 * Interface permettant de définir les tâches pouvant générer des incidents, on parle alors de tâche incidable.
 * L'implémentation de cette interface force à implémenter des méthodes permettant de gérer les incidents de la tâche.
 *
 * @author clem
 */
public interface Incidable {

//    /**
//     * *
//     * Ferme tous les incidents ouvert pour l'entitée
//     */
//    @Deprecated
//    public void fermerLesIncidentOuvert();
//
//    /**
//     * *
//     * Retourne la liste des incident ouvert pour l'entitée.
//     *
//     * @return
//     */
//    public AbstrIncident getIncidenOuvert();
    /**
     * *
     * Retourne le type d'incident permettant de gérer les incidents de la tâche. Exemple pour la tâche
     * {@link TacheRecupCallable}, cette méthode va renvoyer la class {@link CollecteIncident}.
     *
     * @return
     */
    public Class getTypeIncident();

    /**
     * *
     * Cette méthode doit être déclanché par le service associé à la tâche afin de gérer in Incident suite a l'échec de
     * l'appel de la Tache
     *
     * @throws Exception : Une exception peut être levée
     */
    public void gererIncident() throws Exception;

    
    /***
     * Cette méthode doit être exécuté par le service gérant la tâche en cas de réussite de la tâche. Elle permet de clore les incident ouverts.
     * @throws Exception 
     */
    public void fermetureIncident() throws Exception;
}
