/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import javax.persistence.Entity;
import rssagregator.services.ServiceServer;

/**
 * Tous les incidents générés par une tâche gérée par le service {@link ServiceServer} hérite de cette entité, a savoir : <ul>
 * <li>{@link AliveIncident}</li>
 * </ul>
 * @author clem
 */
@Entity(name = "i_server")
public class ServerIncident extends AbstrIncident{
    
}
