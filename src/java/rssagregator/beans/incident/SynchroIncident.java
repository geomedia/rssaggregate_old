/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

/**
 * Cette entitée permet de stocker les erreurs de synchonisation entre le serveur maitre et les serveur esclaves. Ce type d'entitée possède les sous entitées suivante : <ul>
 * <li>JMSDiffusionIncident</li>
 * <li>JMSPerteConnectionIncident</li>
 * </ul>
 * .... Serveur slave injoignable - pas de temporalité
 * .... Perte connection JMS - temporalité
 * .... Impossible d'envoyer un message JMS - pas de temporalité
 * @author clem
 */
//@Entity(name = "synchroincident")
@Entity(name = "i_synchro")
//@MappedSuperclass
//@Inheritance(strategy = InheritanceType.JOINED)
public class SynchroIncident extends AbstrIncident implements Serializable{

    public SynchroIncident() {
    }
}
