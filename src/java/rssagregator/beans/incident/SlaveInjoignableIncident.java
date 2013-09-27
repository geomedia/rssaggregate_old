/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import javax.persistence.Entity;
import rssagregator.beans.ServeurSlave;

/**
 *  Lorsqu'on ne parvient pas à récupérer les items d'un serveur esclaves (tache hebdomadaire), un élément de ce type est crée. 
 * @author clem
 */
@Entity(name = "i_slaveinjoignableincident")
public class SlaveInjoignableIncident extends SynchroIncident{
    
    /***
     * Le serveur esclave pour lequel, l'incident s'est produit. rappel, les serveur slave ne sont pas sérialisé dans la base de données. Il sont récupéré dans les fichiers de conf.
     */
    protected ServeurSlave serveurSlave;
}
