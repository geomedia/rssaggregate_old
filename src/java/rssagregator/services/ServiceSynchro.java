/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.EOFException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.RollbackException;
import org.apache.activemq.ActiveMQConnectionFactory;
import rssagregator.beans.BeanSynchronise;
import rssagregator.beans.Conf;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxType;
import rssagregator.beans.Item;
import rssagregator.beans.Journal;
import rssagregator.beans.exception.UnIncidableException;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.incident.JMSDiffusionIncident;
import rssagregator.beans.incident.JMSPerteConnectionIncident;
import rssagregator.beans.incident.SynchroIncident;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.dao.DAOComportementCollecte;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOGenerique;
import rssagregator.dao.DAOIncident;
import rssagregator.dao.DaoFlux;
import rssagregator.dao.DaoItem;
import rssagregator.dao.DaoJournal;
import rssagregator.utils.XMLTool;

/**
 * Cette classe utilise le client activeMq en vue de synchroniser les beans
 * ({@link Flux}, {@link MediatorCollecteAction}) entre serveurs du projet GEOMEDIA. La méthode run doit être lancée au
 * démarrage du projet afin de lancer un daemon cherchant à relancer la connection toutes les 30 secondes si le booleen
 * statutConnection est à f
 *
 * @author clem
 */
public class ServiceSynchro extends AbstrService implements MessageListener, Observer, ExceptionListener {

    String servname; //Le nom du serveur il est dans le bean Conf dont les information sont enregistrée dans le fichier conf.properties. Cette valeur est utilisée par JMS comme clientID
    ActiveMQConnectionFactory connectionFactory;
    MessageConsumer consumer = null;
    protected Topic topic;
    protected String queueName = "jms/topic/SynchTopic";
    protected int ackMode = Session.CLIENT_ACKNOWLEDGE;
    TopicConnection connection; // tOPIC ACTIVE mQ
    Session session; // Session ActiveMq
    private static ServiceSynchro instance = new ServiceSynchro(); // L'instance du singleton
//    private JMSExeptionlisner exeptionlisner; // Le lisner permettant d'agir si il y a des exeption JMS
    Boolean statutConnection; // Lorsque la connection JMS est rompu ce boolleen doit être mis à false. Le daemon se charge de la relancer
    Boolean daemmon; // Bolleean permettant de stoper le damon
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceSynchro.class);

    /**
     * *
     * Constructeur permettant de créer le service en s'appuyant sur un ScheduledExecutorService très basique.
     */
    private ServiceSynchro() {
        super();
        statutConnection = false;

        // On récupère le servname 
        this.servname = DAOFactory.getInstance().getDAOConf().getConfCourante().getServname();


//        this(Executors.newSingleThreadScheduledExecutor());
//        this(Executors.newSingleThreadScheduledExecutor());
    }

    private ServiceSynchro(ScheduledExecutorService executorService1) {
        this();
        this.executorService = executorService1;
//        super(executorService1);

        //Le nom du serveur utilisé par JMS doit être retouvé dans la conf
//        servname = DAOFactory.getInstance().getDAOConf().getConfCourante().getServname();

    }

    /**
     * *
     * Pour obtenir une instance du singleton
     *
     * @return
     */
    public static ServiceSynchro getInstance() {
        if (instance == null) {
            instance = new ServiceSynchro();
        }
        return instance;
    }

//    @Override
    /**
     * *
     * Démarre un daemon cherchant à activer la connection si besoin est. La vérification est effectuée toutes les 30
     * secondes
     */
//    public void run() {
//
//
//
//        while (daemmon) {
//            System.out.println("TOUR DU completion service");
//            try {
//
//                List<Item> listNouvItem = completionService.take().get();
//                if (listNouvItem != null) {
//                    System.out.println("Le completion Service a rammené qqchose");
//
//                    for (int i = 0; i < listNouvItem.size(); i++) {
//                        Item item = listNouvItem.get(i);
//
//                    }
//                }
//                //            if (!this.getStatutConnection()) {
//                //                try {
//                //                    openConnection();
//                //                } catch (Exception ex) {
//                //                    logger.info("Erreur lors du démarrage de la connection JMS : " + ex);
//                //                }
//                //            }
//
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (ExecutionException ex) {
//                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//
////            try {
////                Thread.sleep(30000);
////            } catch (InterruptedException ex) {
////                logger.error("Erreur lors du Sleep !! Ne devrait pas arriver");
////            }
//        }
//    }
//    public static void main(String[] args) {
//
//
//        ServiceSynchro serviceJMS = ServiceSynchro.getInstance();
//        TacheTest t = new TacheTest(serviceJMS);
//        serviceJMS.run();
//    }
//    public void test() {
//
//        TacheTest t = new TacheTest(this);
//
//        ServiceSynchro serviceJMS = ServiceSynchro.getInstance();
//        serviceJMS.run();
//
//
//    }
    /**
     * *
     * Ouvre la connection ActiveMq et crée le TOPIC permettant aux esclave de recevoir les instructions du maitre
     * (topic de Sync)
     *
     * @throws IOException
     */
    public void openConnection() throws IOException, NamingException, JMSException {

        InitialContext initCtx = new InitialContext();
        Context envContext = (Context) initCtx.lookup("java:comp/env");
        ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) envContext.lookup("jms/ConnectionFactory");
        connectionFactory.setExceptionListener(this);

        connection = (TopicConnection) connectionFactory.createTopicConnection();
        connection.setClientID(servname);
        connection.setExceptionListener(this);
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

    /**
     * *
     * Lance manuellement le serv
     *
     * @throws IOException
     * @throws NamingException
     * @throws JMSException
     */
//    public void manualStart() throws IOException, NamingException, JMSException {
//        openConnection();
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
        if (diffusionNecessaire(bean, action)) {
            TacheDiffuserMessageJMS tacheDiffuserMessageJMS = new TacheDiffuserMessageJMS(this);
            tacheDiffuserMessageJMS.setAction(action);
            tacheDiffuserMessageJMS.setBean(bean);
            tacheDiffuserMessageJMS.setConnection(connection);
            tacheDiffuserMessageJMS.setTopic(topic);
            tacheDiffuserMessageJMS.setSchedule(false);
            executorService.submit(tacheDiffuserMessageJMS);
        }


//        if (diffusionNecessaire(bean, action)) {
//            logger.debug("Diffussion d'un Beans par JMS");
//            if (connection != null) {
//                Session sessionDiff = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
//                MessageProducer producer = sessionDiff.createProducer(topic);
//
//                producer.setDeliveryMode(DeliveryMode.PERSISTENT);
//                MapMessage mapMessage = sessionDiff.createMapMessage();
//                String beanSerialise = XMLTool.serialise(bean);
//
////                System.out.println("" + beanSerialise);
//
//                mapMessage.setStringProperty("bean", beanSerialise);
//                mapMessage.setStringProperty("action", action);
//                mapMessage.setStringProperty("sender", servname);
//                mapMessage.setJMSCorrelationID(servname);
//
//                producer.send(mapMessage);
//                mapMessage.acknowledge();
//                sessionDiff.close();
//                logger.debug("Diffusion du Beans effectuée");
//            } else {
//                throw new JMSException("La connection au service JMS est innactive.");
//            }
//        }
        //TODO : La diffusion d'un message JMS = une thread ?

    }

    /**
     * Méthode déclanché à la réception d'un message JMS par le serveur. Elle est notamment utilisée pour sédérialisé
     * les beans envoyés par le serveur maitre et les recréer sur les serveur esclaves
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
            Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, e);
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
                                 * CE BLOCK A ETE ABANDONNÉ AU PROFIT DUN CHANGEMENT DE LA CASCADE DU FLUX ET DE
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
                                DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
                                dao.beginTransaction();
                                dao.creer(flux);
                                dao.commit();
                                msg.acknowledge();
                                session.commit();
                                logger.info("ajout d'un flux par Syncronisation id : " + flux.getID().toString());
                            } catch (Exception ex) {
                                logger.error("Erreur lors de la synchronisation du beans : " + ex);
                                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            //----------------------------Modification----------------------------
                        } else if (action.equals("mod")) {
                            logger.debug("Demande de modification pour le flux");
                            // On récupère dans la BDD le flux
//                        Flux FluxBDD = (Flux) DAOFactory.getInstance().getDAOFlux().find(flux.getID());
                            try {
                                DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
                                dao.beginTransaction();
                                dao.modifier(flux);
                                dao.commit();

                                msg.acknowledge();
                                session.commit();
                            } catch (IllegalStateException ex) {
                                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (RollbackException ex) {
                                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
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
                                DAOComportementCollecte dao = DAOFactory.getInstance().getDAOComportementCollecte();
                                dao.beginTransaction();
                                dao.creer(bean);
                                dao.commit();
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (action.equals("mod")) {
                            try {
                                MediatorCollecteAction cast = (MediatorCollecteAction) bean;
                                List<Flux> fluxCollecte = cast.getListeFlux();
                                Timestamp stampModif = cast.getDateUpdate();

                                DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
                                daoItem.initcriteria();
                                daoItem.setWhere_clause_Flux(fluxCollecte);
                                daoItem.setDate1(stampModif);
                                daoItem.setDate2(new Date());
                                List<Item> items = daoItem.findCretaria();
                                for (int i = 0; i < items.size(); i++) {
                                    Item item = items.get(i);
                                    item.setSyncStatut(3);
                                    try {
                                        daoItem.beginTransaction();
                                        daoItem.modifier(item);
                                        daoItem.commit();
                                    } catch (Exception e) {
                                        logger.error("erreur lors de la modification du bean : " + e);
                                    }

                                }





                                DAOComportementCollecte dao = DAOFactory.getInstance().getDAOComportementCollecte();
                                dao.beginTransaction();
                                dao.modifier(bean);
                                dao.commit();
                                
                                logger.debug("On tente de modifier");
                                msg.acknowledge();
                                session.commit();

                            } catch (Exception ex) {
                                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (action.equals("rem")) {
                            try {
                                DAOFactory.getInstance().getDAOComportementCollecte().remove(bean);
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } //========================================BEAN : TYPE FLUX====================================
                    else if (bean instanceof FluxType) {
                        DAOGenerique dao = DAOFactory.getInstance().getDAOGenerique();
                        if (action.equals("add")) {
                            try {
                                dao.beginTransaction();
                                dao.creer(bean);
                                dao.commit();
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (action.equals("mod")) {
                            try {
                                dao.beginTransaction();
                                dao.modifier(bean);
                                dao.commit();
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        } else if (action.equals("rem")) {
                            try {
                                dao.remove(bean);
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } //==================================BEAN : JOURNAUX================================================
                    else if (bean instanceof Journal) {
                        DaoJournal dao = DAOFactory.getInstance().getDaoJournal();
                        if (action.equals("add")) {
                            try {
                                dao.beginTransaction();
                                dao.creer(bean);
                                dao.commit();
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (action.equals("mod")) {
                            try {
                                dao.beginTransaction();
                                dao.modifier(bean);
                                dao.commit();
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (action.equals("rem")) {
                            try {
                                dao.remove(bean);
                                msg.acknowledge();
                                session.commit();
                            } catch (Exception ex) {
                                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
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
     * Le statut de de la liaison avec le serveur JMS configurée pour le serveur **
     */
    public void setStatutConnection(Boolean statutConnection) {
        this.statutConnection = statutConnection;
    }

    /**
     * *
     * Les beans se notifient lorsqu'ils changens auprès du service JMS. Celui ci lance alors la diffusion
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        Conf conf = DAOFactory.getInstance().getDAOConf().getConfCourante();


        // SI il s'agit d'un beans devant être diffusé qui précise bien l'action devant être diffusée par un string (add mod rem...)
//        if ((o instanceof Flux || o instanceof MediatorCollecteAction) && arg instanceof String) {
//            try {
//                diffuser(o, (String) arg);
//            } catch (JMSException ex) {
//                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IOException ex) {
//                Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        // Ce sont maintenant les dao qui demande la diffusion. Les flux ne sont plus inscrit au ServiceSynchro

        // ===================================================================================
        //........................Gestion des tâche ne notifiant auprès du service
        // ===================================================================================
        if (o instanceof AbstrTacheSchedule) {
            //--------Une tache de test qui ne sert a rien...
//            if (o.getClass().equals(TacheTest.class)) {
//                System.out.println(" Une Tache test bien de se notifier");

                //--------La tâche de vérification de la connection JMS
//            } 
        if (o.getClass().equals(TacheLancerConnectionJMS.class)) {
                TacheLancerConnectionJMS t = (TacheLancerConnectionJMS) o;
                // Si on a un échec
                if(t.getExeption()!=null){
                    try {
                        t.gererIncident();
                    } catch (Exception ex) {
                        Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else{
                    try {
                        t.fermetureIncident();
                    } catch (Exception ex) {
                        Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (t.schedule) {
                    schedule(t);
//                    executorService.schedule(t, 30, TimeUnit.SECONDS);
                }
                //Si la tache s'est termée correctement 


            } //--------------TACHE DE RECUPERATION DES ITEMS SUR LE SERVEUR ESCLAVE
            else if (o.getClass().equals(TacheSynchroHebdomadaire.class)) {
                logger.debug("Notification d'une tache de récup esclave");
                TacheSynchroHebdomadaire t = (TacheSynchroHebdomadaire) o;

                if (t.getSchedule()) {
                    schedule(t);
                }
//                if (t.getExeption() != null) {
//                    logger.debug("exeption durant la synchro");
//                }
            } //---------------TACHE DE RÉCUPÉRATION DES ITEMS SUR SERVEUR ESCLAVE
            else if (o.getClass().equals(TacheSynchroRecupItem.class)) {
                //Cette tâche n'est jamais schedulé elle est utilisé par TacheSynchroHebdomadaire qui elle est schedulée
                TacheSynchroRecupItem t = (TacheSynchroRecupItem) o;
//                if (t.getExeption() != null) {
////                    gererIncident(t);
//                    //On doit créer un nouveau type d'exeption
//                }
            }

//            gererIncident((AbstrTacheSchedule) o);
        }
    }

    /**
     * *
     * Méthode permettant de tranformer les exceptions survenues lors de la récupération de taches de synchronisation en
     * incident (des beans persistés dans la base de données)
     *
     * @param tache
     */
    @Override
    protected void gererIncident(AbstrTacheSchedule tache) {

        DAOIncident dao = (DAOIncident) DAOFactory.getInstance().getDAOFromTask(tache);
        logger.debug("Gestion");
        //SI il y a eu des exception lors du traitement de la tache
        if (tache.getExeption() != null) {

            logger.debug("gestion d'une erreur pour une tâche de type : " + tache.getClass());
            SynchroIncident si = null;

            //================================================================================================
            //                      INSTANCIATION OU RECUPERATION D'INCIDENT
            //================================================================================================

            //Pour les incident de connection JMS il faut soit créer soit récupérer et incrémenter le compteur
            if (tache.getClass().equals(TacheLancerConnectionJMS.class)) {
                //On remarde si on a déjà un incident 
//                DAOIncident<JMSPerteConnectionIncident> dao = (DAOIncident<JMSPerteConnectionIncident>) DAOFactory.getInstance().getDaoFromType(JMSPerteConnectionIncident.class);
                dao.setClos(false);
                List<JMSPerteConnectionIncident> list = dao.findCriteria(JMSPerteConnectionIncident.class);
                if (list.size() > 0) {
                    si = list.get(0);
                    Integer nb = si.getNombreTentativeEnEchec();
                    nb++;
                    si.setNombreTentativeEnEchec(nb);
                    logger.debug("incrementation de l'incident JMS");
                } else {
                    IncidentFactory<JMSPerteConnectionIncident> factory = new IncidentFactory<JMSPerteConnectionIncident>();
                    si = factory.getIncident(JMSPerteConnectionIncident.class, null, tache.getExeption());
                    logger.debug("nouvel incident JMS");
                }
            }

            if (si == null) {
                IncidentFactory factory = new IncidentFactory();
                try {
                    si = (SynchroIncident) factory.createIncidentFromTask(tache, "blabla");
                } catch (InstantiationException ex) {
                    Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnIncidableException ex) {
                    Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                }
            }


            //=================================================================================================
            // ..................... GESTION DES INCIDENTS
            //=================================================================================================

            //---------------Gestion des incident de TacheSynchroRecupItem
            if (tache.getClass().equals(TacheSynchroRecupItem.class)) {
                TacheSynchroRecupItem cast = (TacheSynchroRecupItem) tache;

                si.setMessageEreur("Erreur lors de la récupération des items du serveur : " + cast.getServeurSlave().toString());
                //----------------------GESTION DE LA PERTE DE CONNECTION JMS
            } else if (tache.getClass().equals(TacheLancerConnectionJMS.class)) {
//            TacheLancerConnectionJMS cast = (TacheLancerConnectionJMS)tache;
                si.setMessageEreur("Erreur de connection au serveur JMS");
            } //-----------> ERREUR DE DIFFUSION DE MESSAGE JMS
            else if (tache.getClass().equals(TacheDiffuserMessageJMS.class)) {
                JMSDiffusionIncident cast = (JMSDiffusionIncident) si;
                TacheDiffuserMessageJMS castTache = (TacheDiffuserMessageJMS) tache;

                si.setMessageEreur("Erreur lors de l'envoie du message JMS");
                cast.setAction(castTache.getAction());
                cast.setMsgSerialise(castTache.getBeanSerialise());

            }


            //=================================================================================================
            //...............................Enregistrment de l'incident
            //=================================================================================================

//            DAOIncident dao = (DAOIncident) DAOFactory.getInstance().getDaoFromType(SynchroIncident.class);
            if (si != null) {
                try {
                    dao.beginTransaction();
                    dao.creer(si);
                    dao.commit();
                } catch (Exception ex) {
                    Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } //=================================================================================================
        //.........................Terminaison correct des TACHE et FERMETURE DE L'INCIDENT
        //=================================================================================================
        else {
            //------------Fermeture des incident de connection JMS si la connection a été retrouvée
            if (tache.getClass().equals(TacheLancerConnectionJMS.class)) {
//                DAOIncident<JMSPerteConnectionIncident> dao = (DAOIncident<JMSPerteConnectionIncident>) DAOFactory.getInstance().getDaoFromType(JMSPerteConnectionIncident.class);
                dao.setClos(false);
                List<JMSPerteConnectionIncident> l = dao.findCriteria(JMSPerteConnectionIncident.class);
                for (int i = 0; i < l.size(); i++) {
                    JMSPerteConnectionIncident jMSPerteConnectionIncident = l.get(i);
                    jMSPerteConnectionIncident.setDateFin(new Date());
                    try {
                        logger.debug("fermeture de l'incident");
                        dao.beginTransaction();
                        dao.modifier(jMSPerteConnectionIncident);
                        dao.commit();
                    } catch (Exception ex) {
                        Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                // Il faut trouver tous les Incident correspondant à une erreur de connection JMS
                //  --Soit on créer un sous type d'incident Synchro
                //----Soit on trouve une requete qui permet de retrouver les erreurs JMS

            }
        }
    }

    public static void main(String[] args) {
        ServiceSynchro serviceSynchro = ServiceSynchro.getInstance();
        TacheLancerConnectionJMS t = new TacheLancerConnectionJMS(serviceSynchro);
        serviceSynchro.executorService.submit(t);
    }

    @Override
    public void onException(JMSException jmse) {

        if (jmse.getCause() instanceof EOFException) {
            // On met le booleean du service à false. Le daemon se chargera de relancer le service
            ServiceSynchro.getInstance().setStatutConnection(false);
        }
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public void instancierTaches() {
//        //------------Lancement des tâches
//        TacheLancerConnectionJMS connectionJMS = new TacheLancerConnectionJMS(this);
//        connectionJMS.setSchedule(true);
//        executorService.submit(connectionJMS);
//
//
//        TacheSynchroRecupItem recupItem = new TacheSynchroRecupItem(this);
//        recupItem.setSchedule(true);
//        executorService.submit(recupItem);
//    }
    @Override
    public void stopService() throws SecurityException, RuntimeException {

        super.stopService();
        daemmon = false;
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException ex) {
            Logger.getLogger(ServiceSynchro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * *
     * Methode permettant de savoir si d'après la config, un beans crée modifie ou supprimé doit être diffusé. Il doit
     * être diffusé si le serveur est maitre et qu'il possède des esclaves. La méthode synchroImperative des beans
     * synchronisable est employé pour faire dire au beans si il doit ou non être synchronisé. On sait par exemple que
     * le compte root est un bean Useraccount ne devant pas être synchronisé.
     *
     * @return
     */
    public Boolean diffusionNecessaire(Object bean, String action) {
        Conf c = DAOFactory.getInstance().getDAOConf().getConfCourante();
        System.out.println("C : " + c);

        // Le contexte demande t'il la synchronisation (Serveur maitre en possession d'esclave)
        if (c.getMaster() && c.getServeurSlave().size() > 0) {
            if (BeanSynchronise.class.isAssignableFrom(bean.getClass())) {
                BeanSynchronise b = (BeanSynchronise) bean;
                return b.synchroImperative();
            }
        } else {
            return false;
        }

        return true;
    }
}
