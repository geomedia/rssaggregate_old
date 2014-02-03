/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import java.io.Serializable;
import javax.persistence.Entity;
import rssagregator.services.ServiceSynchro;

/**
 * <strong>/!\ N'est plus utilisé. La synchronisation est retirée des objectifs du projet</strong>
 * 
 * <p>Tous les incidents des tâches gérée par le service {@link ServiceSynchro}
 * héritent de cette classe à savoir : <ul>
 * <li>{@link JMSDiffusionIncident }</li>
 * <li>{@link JMSPerteConnectionIncident}</li>
 * </ul>
 * </p>
 *
 * @author clem
 * @deprecated 
 */
//@Entity(name = "synchroincident")
@Entity(name = "i_synchro")
//@MappedSuperclass
//@Inheritance(strategy = InheritanceType.JOINED)
public class SynchroIncident extends AbstrIncident implements Serializable {
}
