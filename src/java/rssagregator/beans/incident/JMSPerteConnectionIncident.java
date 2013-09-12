/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Incident créer lorsque la connection au service JMS est perdue. Tant que ce
 * la connection n'est pas retrouvé cet incident n'est pas clos et à chaque
 * nouvel échec on incremente le compteur nbrRepetition.
 *
 * @author clem
 */
@Entity(name = "i_jmsperteconnectionincident")
public class JMSPerteConnectionIncident extends SynchroIncident {
}
