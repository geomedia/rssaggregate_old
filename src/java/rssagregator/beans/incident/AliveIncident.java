/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import javax.persistence.Entity;
import rssagregator.services.ServiceServer;
import rssagregator.services.tache.TacheStillAlive;

/**
 * <p>Incident lié à la tâche <strong>{@link TacheStillAlive}</strong>. </p>
 * <p>Tâche gérée par le service <strong>{@link ServiceServer}</strong></p>
 * <p>Permet de stocker l'information relative a une innactivitée du serveur
 * (durée relevée par la tache still alive en fonction de l'absence d'écriture
 * dans le fichier still Alive). La date de début de l'incident est utilisée
 * pour stocker le début d'innativité du serveur. La date fin correspond à la
 * reprise d'activité</p>.
 * <p>Cet incident à la particularité de devoir forcer sa notification pour être
 * repérer par la tache Alerte mail. Pour cela, le service doit modifier la
 * variable notificationImperative</p>
 *
 * @author clem
 */
@Entity(name = "i_alive")
public class AliveIncident extends ServerIncident {
}
