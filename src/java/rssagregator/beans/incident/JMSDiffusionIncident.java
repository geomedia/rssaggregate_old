/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import javax.persistence.Entity;

/**
 *
 * @author clem
 */
@Entity(name = "i_jmsdiffusionincident")
public class JMSDiffusionIncident extends SynchroIncident{
    
    /***
     * L'action qui n' pu être diffusée
     */
    protected String action;
    
    /***
     * Le corps du message JMS qui n'a pu être envoyé
     */
    protected String msgSerialise;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMsgSerialise() {
        return msgSerialise;
    }

    public void setMsgSerialise(String msgSerialise) {
        this.msgSerialise = msgSerialise;
    }
    
    
}
