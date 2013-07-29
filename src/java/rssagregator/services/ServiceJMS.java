///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package rssagregator.services;
//
//import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.jms.Message;
//import javax.jms.MessageConsumer;
//import javax.jms.MessageListener;
//import javax.jms.Session;
//import javax.jms.Topic;
//import javax.jms.TopicConnection;
//import org.apache.activemq.ActiveMQConnectionFactory;
//import rssagregator.utils.PropertyLoader;
//import rssagregator.utils.XMLTool;
//
///**
// *
// * @author clem
// */
//public class ServiceJMS implements MessageListener {
//
//    String servname;
//    String urlJmx;
//    ActiveMQConnectionFactory connectionFactory;
//    MessageConsumer consumer = null;
//    protected Topic queue;
//    protected String queueName = "jms/topic/MyTopicClem";
//    protected String url = "tcp://172.17.201.17:61616";
//    protected int ackMode = Session.AUTO_ACKNOWLEDGE;
//    TopicConnection connection;
//    Session session;
//    private static ServiceJMS instance;
//
//    private ServiceJMS() {
//    }
//
//    
//    public static ServiceJMS getInstance() {
//        if (instance == null) {
//            instance = new ServiceJMS();
//        }
//        return instance;
//    }
//
//    public void startService() throws IOException {
//        //On lit les propriétés du serveur. 
//        servname = PropertyLoader.loadProperti("conf.properties", "servname");
//        urlJmx = PropertyLoader.loadProperti("conf.properties", "jmsprovider");
//
//    }
//
//    public void diffuser(Object bean, String action) {
//    }
//
//    @Override
//    public void onMessage(Message msg) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    public static void main(String[] args) {
//        try {
//            ServiceJMS jMS = ServiceJMS.getInstance();
//            jMS.startService();
//            
//        } catch (IOException ex) {
//            Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//}
