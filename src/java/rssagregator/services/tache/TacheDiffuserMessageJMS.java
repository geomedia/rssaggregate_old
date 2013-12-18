/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.Observer;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import rssagregator.beans.Conf;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.JMSDiffusionIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.utils.XMLTool;

/**
 * *
 * Cette tâche est gérée par le service {@link ServiceSynchro}. Elle permet de diffuser un message JMS utilisé pour
 * répercuter les modifications des beans synchronisable sur les serveurs esclaves. Cette tache est incidable
 * (implémentation de {@link Incidable}). En effet en cas d'erreur de diffusion, le service doit générer et persister un
 * incident {@link JMSDiffusionIncident}
 *
 * @author clem
 */
public class TacheDiffuserMessageJMS extends TacheImpl<TacheDiffuserMessageJMS> implements Incidable {

//    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheDiffuserMessageJMS.class);
    /**
     * *
     * Le topic connection devant être utilisé pour diffuser le message.
     */
    TopicConnection connection;

    /**
     * *
     * Le topic sur lequel on doit diffuser le message
     */
    protected Topic topic;
    /**
     * *
     * Le beans devant être diffusé sur les server slaves.
     */
    Object bean;
    /**
     * *
     * L'action devant être diffusé pour le beans (add, mod, rem)
     */
    String action;
    
    
    String beanSerialise;

    public TacheDiffuserMessageJMS(Observer s) {
        super(s);
    }

    @Override
    protected void callCorps() throws Exception {
               if (connection != null) {
                Session sessionDiff = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                MessageProducer producer = sessionDiff.createProducer(topic);
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);
                MapMessage mapMessage = sessionDiff.createMapMessage();

                beanSerialise = XMLTool.serialise(bean);
                mapMessage.setStringProperty("bean", beanSerialise);
                mapMessage.setStringProperty("action", action);

                Conf c = DAOFactory.getInstance().getDAOConf().getConfCourante();

                mapMessage.setStringProperty("sender", c.getServname());
                mapMessage.setJMSCorrelationID(c.getServname());
                producer.send(mapMessage);
                mapMessage.acknowledge();
                sessionDiff.close();
                logger.debug("Diffusion du Beans effectuée");
                throw new Exception("test Exception");
            }
            else{
                throw new JMSException("La connection au service JMS est innactive.");
            }
    }

    @Override
    protected TacheDiffuserMessageJMS callFinalyse() {
        return super.callFinalyse(); //To change body of generated methods, choose Tools | Templates.
    }
    


    /**
     * *
     * Retourne la classe {
     *
     * @JMSDiffusionIncident}
     * @return
     */
    @Override
    public Class getTypeIncident() {
        return JMSDiffusionIncident.class;
    }

    public TopicConnection getConnection() {
        return connection;
    }

    public void setConnection(TopicConnection connection) {
        this.connection = connection;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBeanSerialise() {
        return beanSerialise;
    }

    @Override
    public void gererIncident() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    /***
     * Cette méthode ne fait rien. 
     */
    public void fermetureIncident() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
