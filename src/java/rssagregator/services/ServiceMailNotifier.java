/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import rssagregator.beans.UserAccount;
import rssagregator.dao.DAOFactory;
import rssagregator.utils.PropertyLoader;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;

/**
 * Le service mail gère la vie des Tache : <ul>
 * <li>TacheEnvoyerMail : Envoie d'un mail </li>
 * <li></li>
 * </ul>
 *
 * @author clem
 */
public class ServiceMailNotifier extends ServiceImpl {

    private static ServiceMailNotifier instance = new ServiceMailNotifier();
//    private ScheduledExecutorService executorService;
    private Properties propertiesMail;
    private final static String MAILER_VERSION = "Java";
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceMailNotifier.class);
    
    

    /**
     * *
     * Constructeur privée. Configure l'objet avec le contenu du fichier properties du répertoire de configuration
     */
    private ServiceMailNotifier() {
        super();
        try {
            //On doit charger le fichier de conf.
            //On commence par rechercher l'emplacement du fichier propertymail dans le fichier propertie du projet.
            
            String varPath = (String) PropertyLoader.returnConfPath()+"mailconf.properties";
            propertiesMail = PropertyLoader.loadFromFile(varPath);
        } catch (Exception ex) {
            Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ServiceMailNotifier getInstance() {
        if (instance == null) {
            instance = new ServiceMailNotifier();
        }
        return instance;
    }

    /**
     * *
     * Permet d'obtenir une instance du service configuré statiquement (les instances normale sont configurée en
     * utilisant le fichier XML servicedef.xml au démarrage de l'appli)
     *
     * @return
     */
    public static ServiceMailNotifier getTestInstance() {
        ServiceMailNotifier service = new ServiceMailNotifier();
        service.executorService = Executors.newScheduledThreadPool(5);

        return service;

    }

    /**
     * *
     * Retourne la liste des adresses mail des admins en se basant sur la dao UserAccount.
     *
     * @return
     */
    public InternetAddress[] returnMailAdmin() throws AddressException {

        List<UserAccount> list = DAOFactory.getInstance().getDAOUser().findUserANotifier();
        logger.debug("recherche de la liste des mail a joindre list size : " + list.size());
        InternetAddress[] retour = new InternetAddress[list.size()];
        for (int i = 0; i < list.size(); i++) {
            UserAccount userAccount = list.get(i);
            retour[i] = new InternetAddress(userAccount.getMail());
            logger.debug("email : " + retour[i].getAddress());
        }
        return retour;
    }

    public Properties getPropertiesMail() {
        return propertiesMail;
    }

    public void setPropertiesMail(Properties propertiesMail) {
        this.propertiesMail = propertiesMail;
    }



    @Override
    public void stopService() throws SecurityException, RuntimeException {
        super.stopService();

    }
    
    
    public void writeMailtoFile(Message message){
        
        
//        String varpath = System.getProperty("confpath");
        String varpath = "/home/clem/testmail";
        varpath+="/mail/";
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(varpath);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServiceMailNotifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
 
        ServiceMailNotifier.getTestInstance().writeMailtoFile(null);
    }
    
}
