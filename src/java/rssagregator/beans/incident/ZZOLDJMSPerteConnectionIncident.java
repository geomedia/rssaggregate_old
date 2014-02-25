/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import javax.persistence.Entity;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import rssagregator.services.tache.ZZOLDTacheLancerConnectionJMS;

/**
 * <strong>/!\ N'est plus utilisé. La synchronisation est retirée des objectifs du projet</strong>
 * <p>Incident générée par le service {@link ServerIncident} lorsque la tâche
 * {@link ZZOLDTacheLancerConnectionJMS} ne parvient pas à étéblir la connection
 * JMS</p>
 * <p>L'incident est créé lorsque la connection au service JMS est perdue. Tant
 * que ce la connection n'est pas retrouvé cet incident n'est pas clos et à
 * chaque nouvel échec on incremente le compteur nbrRepetition.<p>
 *
 * @author clem
 * @deprecated 
 */
//@Entity(name = "i_jmsperteconnectionincident")
public class ZZOLDJMSPerteConnectionIncident extends SynchroIncident {
//
//    
//    /***
//     * Une perte de connection JMS ne doit être notifié par mail que si elle dure depuis 20 minutes
//     * @return 
//     */
//    @Override
//    public Boolean doitEtreNotifieParMail() {
//        DateTime dtCurrent = new DateTime();
//        DateTime dtDebut = new DateTime(this.dateDebut);
//        Duration dur = new Duration(dtDebut, dtCurrent);
//        if(dur.getStandardMinutes()>20){
//            return true;
//        }
//        else{
//            return false;
//        }
//        
////        return super.doitEtreNotifieParMail(); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//    
}
