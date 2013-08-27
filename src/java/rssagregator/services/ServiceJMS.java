/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.RollbackException;
import org.apache.activemq.ActiveMQConnectionFactory;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxType;
import rssagregator.beans.Journal;
import rssagregator.beans.form.DAOGenerique;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoJournal;
import rssagregator.utils.XMLTool;

/**
 * Cette classe utilise le client activeMq en vue de synchroniser les beans
 * (flux, comportement de collecte) entre serveurs du projet GEOMEDIA. La
 * méthode run doit être lancée au démarrage du projet afin de lancer un daemon
 * cherchant à relancer la connection toutes les 30 secondes si le booleen
 * statutConnection est à f
 *
 * @author clem
 */
public class ServiceJMS implements MessageListener, Runnable, Observer {

    String servname; //Le nom du serveur il est dans le bean Conf dont les information sont enregistrée dans le fichier conf.properties. Cette valeur est utilisée par JMS comme clientID
    ActiveMQConnectionFactory connectionFactory;
    MessageConsumer consumer = null;
    protected Topic topic;
    protected String queueName = "jms/topic/SynchTopic";
    protected int ackMode = Session.CLIENT_ACKNOWLEDGE;
    TopicConnection connection; // tOPIC ACTIVE mQ
    Session session; // Session ActiveMq
    private static ServiceJMS instance = new ServiceJMS(); // L'instance du singleton
    private JMSExeptionlisner exeptionlisner; // Le lisner permettant d'agir si il y a des exeption JMS
    Boolean statutConnection; // Lorsque la connection JMS est rompu ce boolleen doit être mis à false. Le daemon se charge de la relancer
    Boolean daemmon; // Bolleean permettant de stoper le damon
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceJMS.class);

    private ServiceJMS() {
        statutConnection = false;
        daemmon = true;
        exeptionlisner = new JMSExeptionlisner();
        servname = DAOFactory.getInstance().getDAOConf().getConfCourante().getServname();
    }

    /**
     * *
     * Pour obtenir une instance du singleton
     *
     * @return
     */
    public static ServiceJMS getInstance() {
        if (instance == null) {
            instance = new ServiceJMS();
        }
        return instance;
    }

    @Override
    /**
     * *
     * Démarre un daemon cherchant à activer la connection si besoin est. La
     * vérification est effectuée toutes les 30 secondes
     */
    public void run() {
        while (daemmon) {
            if (!this.getStatutConnection()) {
                try {
                    startService();
                } catch (Exception ex) {
                    logger.info("Erreur lors du démarrage de la connection JMS : " + ex);
                }
            }
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ex) {
                logger.error("Erreur lors du Sleep !! Ne devrait pas arriver");
            }
        }
    }

    /**
     * *
     * Ouvre la connection ActiveMq et crée le TOPIC permettant aux esclave de
     * recevoir les instructions du maitre (topic de Sync)
     *
     * @throws IOException
     */
    public void startService() throws IOException, NamingException, JMSException {
        logger.info("Tentative de démarrage du Service JMS");

        InitialContext initCtx = new InitialContext();
        Context envContext = (Context) initCtx.lookup("java:comp/env");
        ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) envContext.lookup("jms/ConnectionFactory");
        connectionFactory.setExceptionListener(exeptionlisner);

        connection = (TopicConnection) connectionFactory.createTopicConnection();
        connection.setClientID(servname);
        connection.setExceptionListener(exeptionlisner);
        session = connection.createTopicSession(true, Session.CLIENT_ACKNOWLEDGE);
        topic = session.createTopic(queueName);

        consumer = session.createDurableSubscriber(topic, servname);
        // Si le serveur n'est pas maitre (cad, si il est esclave), il doit ecouter le topic
        if (DAOFactory.getInstance().getDAOConf().getConfCourante().getMaster()) {
            consumer.setMessageListener(this);
        }

        connection.start();
        statutConnection = true;
        logger.info("Démarrage du service JMS effectué");
    }

    /***
     * Lance manuellement le serv
     * @throws IOException
     * @throws NamingException
     * @throws JMSException 
     */
//    public void manualStart() throws IOException, NamingException, JMSException {
//        startService();
//    }

    /**
     * *
     * Permet de diffuser un beans (flux, comportement...) par JMS.
     *
     * @param bean
     * @param action rem, add
     * @throws JMSException
     * @throws IOException
     */
    public void diffuser(Object bean, String action) throws JMSException, IOException {
        logger.debug("Diffussion d'un Beans par JMS");
        if (connection != null) {
            Session sessionDiff = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageProducer producer = sessionDiff.createProducer(topic);

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            MapMessage mapMessage = sessionDiff.createMapMessage();
            String beanSerialise = XMLTool.serialise(bean);

            System.out.println("" + beanSerialise);

            mapMessage.setStringProperty("bean", beanSerialise);
            mapMessage.setStringProperty("action", action);
            mapMessage.setStringProperty("sender", servname);
            mapMessage.setJMSCorrelationID(servname);

            producer.send(mapMessage);
            mapMessage.acknowledge();
            sessionDiff.close();
            logger.debug("Diffusion du Beans effectuée");
        }
    }

    /**
     * Méthode déclanché à la réception d'un message JMS par le serveur. Elle
     * est notamment utilisée pour sédérialisé les beans envoyés par le serveur
     * maitre et les recréer sur les serveur esclaves
     *
     * @param msg Le message reçut
     */
    @Override
    public void onMessage(Message msg) {
        logger.debug("Reception d'un message JMS");

        String sender = "";
        try {
            sender = msg.getStringProperty("sender");
        } catch (JMSException ex) {
            Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
        }
        logger.debug("sender : " + sender);

        // Si le message ne provient pas du serveur lui même
        if (!sender.equals(servname)) {
            logger.debug("Je traite car c'est un serv different");
            if (msg instanceof MapMessage) {
                logger.debug("c'est un mapmessage");
                MapMessage mapmsg = (MapMessage) msg;
                String action = null;
                try {
                    action = mapmsg.getStringProperty("action");
                } catch (Exception e) {
                }
                //==========================================================================
                //                         DESERIALISATION DU BEANS
                //==========================================================================
                Object bean = null;
                try {
                    String beansXML = mapmsg.getStringProperty("bean");
                    bean = XMLTool.unSerialize(beansXML);
                } catch (Exception e) {
                    Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, e);
                }
                logger.debug("beens to string : " + bean.toString());
                logger.debug("action : " + action);

                //==========================================================================
                //          Traitement de l'action a effectuer
                //==========================================================================
                // On doit gérer les entité : flux, comportement, peut être journaux
                if (action != null && bean != null) {
                    logger.debug("dans le if");
                    logger.debug("le beans est de type : " + bean.getClass().getSimpleName());
                    //================================FLUX================================
                    if (bean instanceof Flux) {
                        logger.debug("c'est un flux");
                        logger.debug("Ce message contient un flux");
                        Flux flux = (Flux) bean;
                        if (action.equals("add")) {
                            logger.debug("demande d'ajout pour le flux");
                            try {


                                /**
                                 * *
                                 * CE BLOCK A ETE ABANDONNÉ AU PROFIT DUN
                                 * CHANGEMENT DE LA CASCADE DU FLUX ET DE
                                 * MODIFICATION DANS LA DAO
                                 */
                                // Il faut veiller à vérifier l'existence de tous les objet lié au flux. Si on n'a pas par exemple l'objet de traitement adhéquat, on va alors le créer
                                // Gestion des médiator de collecte
//                                if (flux.getMediatorFlux() != null) {
//                                    MediatorCollecteAction comportemnetBDD = (MediatorCollecteAction) DAOFactory.getInstance().getDAOComportementCollecte().find(flux.getMediatorFlux().getID());
//                                    if (comportemnetBDD == null) {
//                                        DAOFactory.getInstance().getDAOComportementCollecte().creer(flux.getMediatorFlux());
//                                    }
//                                }
//                                //gestion du type de flux
//                                if(flux.getTypeFlux()!=null){
//                                    DAOGenerique dAOGenerique = DAOFactory.getInstance().getDAOGenerique();
//                                    dAOGenerique.setClassAssocie(FluxType.class);
//                                    FluxType type = (FluxType) dAOGenerique.find(flux.getTypeFlux().getID());
//                                    if(type==null){
//                                        logger.debug("Création d'un type de flux");
//                                        dAOGenerique.creer(flux.getTypeFlux());
//                                        
//                                    }
//                                }
//                                //gestion du journal propre au flux
//                                if(flux.getJournalLie()!=null){
//                                    Journal jDistant = flux.getJournalLie();
//                                    Journal jLocal = (Journal) DAOFactory.getInstance().getDaoJournal().find(jDistant.getID());
//                                    if(jLocal == null){
//                                        DAOFactory.getInstance().getDaoJournal().creer(jDistant);
//                                    }
//                                }
                                // pas de panique pour les objet liés. Si ils sont existant dans la base de données la dao va s'occuper de leur création lors de la création du flux. Si il n'existent pas, c'est la dao qui va les créer grace à la cascade persist
                                DAOFactory.getInstance().getDAOFlux().creer(flux);
                                msg.acknowledge();
                                session.commit();
                                logger.info("ajout d'un flux par Syncronisation id : " + flux.getID().toString());
                            } catch (Exception ex) {
                                logger.error("Erreur lors de la synchronisation du beans : " + ex);
                                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            //----------------------------Modification----------------------------
                        } else if (action.equals("mod")) {
                            logger.debug("Demande de modification pour le flux");
                            // On récupère dans la BDD le flux
//                        Flux FluxBDD = (Flux) DAOFactory.getInstance().getDAOFlux().find(flux.getID());
                            try {
                                DAOFactory.getInstance().getDAOFlux().modifier(flux);
                                msg.acknowledge();
                                session.commit();
                            } catch (IllegalStateException ex) {
                                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (RollbackException ex) {
                                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } //----------------------------Suppression----------------------------
                        else if (action.equals("rem")) {
                            logger.debug("Suppression du flux");
                            try {
                                DAOFactory.getInstance().getDAOFlux().remove(flux);
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception e) {
                                logger.error("erreur lors de la supression du flux");
                            }
                        }
                        //====================================BEAN : COMPORTEMENT DE COLLECTE===========================
                    } else if (bean instanceof MediatorCollecteAction) {
                        if (action.equals("add")) {
                            try {
                                DAOFactory.getInstance().getDAOComportementCollecte().creer(bean);
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (action.equals("mod")) {
                            try {
                                DAOFactory.getInstance().getDAOComportementCollecte().modifier(bean);
                                logger.debug("On tente de modifier");
                                msg.acknowledge();
                                session.commit();

                            } catch (Exception ex) {
                                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (action.equals("rem")) {
                            try {
                                DAOFactory.getInstance().getDAOComportementCollecte().remove(bean);
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } //========================================BEAN : TYPE FLUX====================================
                    else if (bean instanceof FluxType) {
                        DAOGenerique dao = DAOFactory.getInstance().getDAOGenerique();
                        if (action.equals("add")) {
                            try {
                                dao.creer(bean);
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (action.equals("mod")) {
                            try {
                                dao.modifier(bean);
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        } else if (action.equals("rem")) {
                            try {
                                dao.remove(bean);
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } //==================================BEAN : JOURNAUX================================================
                    else if (bean instanceof Journal) {
                        DaoJournal dao = DAOFactory.getInstance().getDaoJournal();
                        if (action.equals("add")) {
                            try {
                                dao.creer(bean);
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (action.equals("mod")) {
                            try {
                                dao.modifier(bean);
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (action.equals("rem")) {
                            try {
                                dao.remove(bean);
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }

            //Si le message provient du même client ID que le serveur, alors on l'acquite
        } else {
            System.out.println("C'est mon message");


            try {
                msg.acknowledge();
                session.commit();
            } catch (JMSException ex) {
                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * *
     * Fermeture du Service JMS. Ferme la connection et stoppe le daemon
     * cherchant perpétuellement à la relancer
     */
    public void close() {

        //        try {

        daemmon = false;
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException ex) {
            Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
        }

        //            Thread.currentThread().interrupt();
        //            System.out.println(">>> JMS Interruption de la thread");

        //            connection.close();
        //            System.out.println(">>>>JMS CONNECTION CLOSE");
        //            connection.stop();
        //            
        //            System.out.println(">>>JMS CONNECTION STOP");

        //        } catch (JMSException ex) {
        //            Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
        //        }

    }

    /**
     * *
     * Le statut de de la liaison avec le serveur JMS configurée pour le serveur
     *
     * @return
     */
    public Boolean getStatutConnection() {
        return statutConnection;
    }

    /**
     * *
     * Le statut de de la liaison avec le serveur JMS configurée pour le serveur
     * **
     */
    public void setStatutConnection(Boolean statutConnection) {
        this.statutConnection = statutConnection;
    }

    /**
     * *
     * Les beans se notifient lorsqu'ils changens auprès du service JMS. Celui
     * ci lance alors la diffusion
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {

        // SI il s'agit d'un beans devant être diffusé qui précise bien l'action devant être diffusée par un string (add mod rem...)
        if ((o instanceof Flux || o instanceof MediatorCollecteAction) && arg instanceof String) {
            try {
                diffuser(o, (String) arg);
            } catch (JMSException ex) {
                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ServiceJMS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
