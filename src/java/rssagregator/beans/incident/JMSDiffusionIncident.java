/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import javax.persistence.Column;
import javax.persistence.Entity;
import rssagregator.services.tache.TacheDiffuserMessageJMS;

/**
 * <p>Incident générée par le service
 *
 * @{@link ServiceSynchro} lorsque la tache {@link TacheDiffuserMessageJMS}</p>
 * @author clem
 */
@Entity(name = "i_jmsdiffusionincident")
public class JMSDiffusionIncident extends SynchroIncident {

    /**
     * *
     * Le corps du message JMS qui n'a pu être envoyé. On y trouve du contenu XML
     */
    @Column(name = "msgSerialise", columnDefinition = "text")
    protected String msgSerialise;
    /**
     * *
     * L'actionDiffusion qui n'a pu être diffusée.
     */
    @Column(name = "actionDiffusion", length = 10)
    protected String actionDiffusion;

    public String getAction() {
        return actionDiffusion;
    }

    public void setAction(String action) {
        this.actionDiffusion = action;
    }

    public String getMsgSerialise() {
        return msgSerialise;
    }

    public void setMsgSerialise(String msgSerialise) {
        this.msgSerialise = msgSerialise;
    }
}
