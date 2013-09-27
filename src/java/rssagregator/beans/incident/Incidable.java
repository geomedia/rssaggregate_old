/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import rssagregator.services.TacheRecupCallable;

/**
 * Interface permettant de définir les tâches pouvant générer des incidents, on
 * parle alors de tâche incidable. L'implémentation de cette interface force à
 * implémenter des méthodes permettant de gérer les incidents de la tâche.
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
     * Retourne le type d'incident permettant de gérer les incidents de la
     * tâche. Exemple pour la tâche {@link TacheRecupCallable}, cette méthode va renvoyer la class {@link CollecteIncident}.
     *
     * @return
     */
    public Class getTypeIncident();
}
