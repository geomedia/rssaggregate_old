/**
 * *
 * Les incidents sont des beans permettant de conserver une trace des erreurs survenues lors de la collecte. Tous les
 * incidents héritent de la classe AbstrIncident. L'incidents le plus utilisé est CollecteIncidents. Lors de la
 * conception nous avons pensée que chaque tâche pouvait donner lieu a des Incidents. (voir notion de tache incidable).
 * Afin de faciliter la gestion future du système de nombreux incidents ont été retirés. L'objectif est au final de na
 * pas submerger l'administrateur d'information qu'il ne sait pas gérer...
 */
package rssagregator.beans.incident;