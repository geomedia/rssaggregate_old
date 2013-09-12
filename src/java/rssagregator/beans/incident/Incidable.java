/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

/**
 *  Interface pour forcer d'autre classe à implémenter des méthodes de gestion d'incident
 * @author clem
 */
public interface Incidable {
    /***
     * Ferme tous les incidents ouvert pour l'entitée
     */
    public void fermerLesIncidentOuvert();
    
    /***
     * Retourne la liste des incident ouvert pour l'entitée.
     * @return 
     */
    public AbstrIncident getIncidenOuvert();
    
    /***
     * Retourne le type d'incident permettant de gérer les incidents de la tâche.
     * @return 
     */
    public Class getTypeIncident();
}
