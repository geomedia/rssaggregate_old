/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.Conf;
import rssagregator.beans.UserAccount;
import rssagregator.utils.PropertyLoader;
import rssagregator.utils.ServletTool;

/**
 * La DAO permettant l'intéraction entre un bean de type {@link Conf}. Contrairement aux autres beans, la {@link Conf}
 * n'est pas persisté dans la base de données SQL mais dans un fichier : conf.properties
 *
 * @author clem
 */
public class DAOConf extends AbstrDao {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DAOConf.class);
    /**
     * *
     * La conf est un objet singleton car l'aggrégateur n'en possède qu'un : la conf courante. Cette variable permet de
     * concerver l'instance du singleton.
     */
    private Conf confCourante;

    protected DAOConf(DAOFactory dAOFactory) {
        em = dAOFactory.getEntityManager();
        this.dAOFactory = dAOFactory;
        this.classAssocie = Conf.class;
    }

    /**
     * *
     * Permet de récupérer la confcourante, c'est à dire, celle chargé au démarrage de l'application
     *
     * @return
     */
    public Conf getConfCourante() {
        return confCourante;
    }

    /**
     * *
     * Définition de la conf courante.
     *
     * @param confCourante
     */
    public void setConfCourante(Conf confCourante) {
        this.confCourante = confCourante;
    }

    /**
     * *
     * Modifi le statut Change de L'observable.
     */
//    public void forceNotifyObservers() {
//        this.setChanged();
//        notifyObservers();
//    }
    /**
     * *
     * Charge la configuration du serveur à partir du fichier conf.properties. L'objet conf instancié est placée dans la
     * variable confCourante.
     *
     * @throws IOException
     * @throws Exception
     */
    public void charger() throws IOException, Exception {
        Conf conf = new Conf();
//        Properties prop = PropertyLoader.load("conf.properties");
        String propfile = PropertyLoader.loadProperti("serv.properties", "conf"); // On commence par charger le nom du fichier properties externe à partir du fichier serv.properties
        Properties prop = PropertyLoader.loadFromFile(propfile);

        // Chargement de la valeur active dans le fichier properties
        String active = prop.getProperty("active");
        if (active.equals("1")) {
            conf.setActive(Boolean.TRUE);
        } else if (active.equals("0")) {
            conf.setActive(Boolean.FALSE);
        } else {
            throw new Exception("Impossible de lire la valeur de active");
        }

        //Chargement du varpath (Ou sont normalement les fichier log et properties)
        String varpath = PropertyLoader.loadProperti("serv.properties", "varpath");
        conf.setVarpath(varpath);

        //Chargement du nom du serveur
        String servname = prop.getProperty("servname");
        conf.setServname(servname);

        //Chargement du jmsprovider
        String jmsprovider = prop.getProperty("jmsprovider");
        conf.setJmsprovider(jmsprovider);


        // Chargement du statut (master ou slave)
        String statut = prop.getProperty("master");
        if (statut.equals("1")) {
            conf.setMaster(true);
        } else if (statut.equals("0")) {
            conf.setMaster(false);
        } else {
            throw new Exception("Impossible de charger la valeur master dans le fichier properties");
        }

        // Chargement de la liste des serveur esclave si le serveur est maitre
        if (conf.getMaster()) {
            // ----> Les serveur Slave sont retournés dans la base de données
//            String slaveserver = prop.getProperty("slaveserver"); //PropertyLoader.loadProperti("conf.properties", "slaveserver");
//            String[] slavetab = slaveserver.split("; ");
//            for (int i = 0; i < slavetab.length; i++) {
//                String string = slavetab[i];
//                String split[] = string.split(" ");
//                ServeurSlave serveurSlave = new ServeurSlave();
//                serveurSlave.setServHost(split[0]);
//                serveurSlave.setLogin(split[1]);
//                serveurSlave.setPass(split[2]);
//                serveurSlave.setUrl(split[3]);
//                conf.getServeurSlave().add(serveurSlave);
//            }

        } else { // Si c'est un serveur esclave
            String purgeDuration = prop.getProperty("purgeDuration");
            try {
                Integer val = new Integer(purgeDuration);
                conf.setPurgeDuration(val);
                System.out.println(val);
            } catch (Exception e) {
                throw new Exception("impossible de lire la valeur purgeduration dans le fichier conf.properties");
            }

            String hostMaster = prop.getProperty("hostMaster");
            conf.setHostMaster(hostMaster);
        }

        //----------Récupération de la durée avant purge
        String s = prop.getProperty("purgeDuration");
        try {
            Integer val = new Integer(s);
            conf.setPurgeDuration(Integer.MIN_VALUE);
        } catch (Exception e) {
            throw new Exception("Impossible de charger la durée avant purge dans la configuration");
        }

        //--------------Chargement du host maitre
        s = prop.getProperty("hostMaster");
        conf.setHostMaster(s);


        //-----------Chargement de l'adresse http du server
        s = prop.getProperty("servurl");
        conf.setServurl(s);

        System.out.println("=============================================================");
        System.out.println("CONF COURANTE : " + conf);
        System.out.println("=============================================================");
        this.confCourante = conf;
    }

//    public static void main(String[] args) {
//
//
//
//        DAOFactory dAOFactory = DAOFactory.getInstance();
//        DAOConf dao = new DAOConf(dAOFactory);
//        try {
//            dao.charger();
//
//            dao.confCourante.setNbThreadRecup(6);
//            dao.modifierConf(dao.confCourante);
//        } catch (IOException ex) {
//            Logger.getLogger(DAOConf.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            Logger.getLogger(DAOConf.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    /**
     * *
     * Charge la config courante depuis la BDD. Cette méthode doit être lancée au démarrage de l'application (servlet
     * start)
     */
//    public void chargerDepuisBd() {
//
//        //Chargement de la config depuis la base de donnée
//        DAOGenerique dao = DAOFactory.getInstance().getDAOGenerique();
//        dao.setClassAssocie(Conf.class);
//        List<Object> confs = dao.findall();
//        if (confs != null && confs.size() > 0) {
//            Conf conf = (Conf) confs.get(0);
//            this.confCourante = conf;
//        } else {
//            Conf defaultconf = new Conf();
//            defaultconf.setActive(Boolean.TRUE);
//            defaultconf.setNbThreadRecup(5);
//            this.confCourante = defaultconf;
//            try {
//                dao.creer(confCourante);
//            } catch (Exception ex) {
//                Logger.getLogger(DAOConf.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
    /**
     * *
     * Enregistre la conf dans le fichier properties du serveur.
     *
     * @param conf
     * @throws Exception
     */
    public void modifierConf(Conf conf) throws Exception {
        System.out.println("MOF DE LA CONFF");
//        Properties prop = PropertyLoader.load("conf.properties");
        String propfile = PropertyLoader.loadProperti("serv.properties", "conf");
//        Properties prop = PropertyLoader.loadFromFile(propfile);
        Properties prop = new Properties();

        //-----------------NOM DU SERVER
        if (conf.getServname() != null && !conf.getServname().isEmpty()) {
            prop.setProperty("servname", conf.getServname());
        }

        //--------------- Enregistrement du statut actif//innactif
        if (conf.getActive()) {
            prop.setProperty("active", "1");
        } else {
            prop.setProperty("active", "0");
        }

        //-------------JMX Provider
        prop.setProperty("jmsprovider", conf.getJmsprovider());

        //--------------Master statut
        if (conf.getMaster()) {
            prop.setProperty("master", "1");
        } else {
            prop.setProperty("master", "0");
        }

        //------------------Serveur slave
        int i;
        String chaine = ""; // --------> LES SERVEUR SLAVE SONT RETOURNE DANS LA BASE DE DONNÉES
//        for (i = 0; i < conf.getServeurSlave().size(); i++) {
//            ServeurSlave slave = conf.getServeurSlave().get(i);
//            // On construit la chaine de caractères dans le fichier host;
//            chaine += slave.getServHost() + " " + slave.getLogin() + " " + slave.getPass() + " " + slave.getUrl() + ";";
//        }
//        
//        
//        if (chaine.length() > 0) {
//            chaine = chaine.substring(0, chaine.length() - 1);
//        }
//
//        prop.setProperty("slaveserver", chaine);

        //-------------Durée de purge
        if (conf.getPurgeDuration() != null) {
            prop.setProperty("purgeDuration", conf.getPurgeDuration().toString());
        }

        //-------------------Host du serveur maitre
        if (conf.getHostMaster() != null) {
            prop.setProperty("hostMaster", conf.getHostMaster());
        }

        //-------------adresse HTTP du Serveur
        if (conf.getServurl() != null) {
            prop.setProperty("servurl", conf.getServurl());
        }

//        PropertyLoader.save(prop, propfile, "les commentaire ont disparu reportez vous à la doc du projet dsl !");
        PropertyLoader.saveToFile(prop, propfile, "les commentaire ont disparu reportez vous à la doc du projet dsl !");

//        PropertyLoader.save(prop, "conf.properties", "LALALA commentaire");
    }

    /**
     * *
     * Le compte root est définit dans le fichier conf.properties. Au démarrage de l'application, cette méthode est
     * déclancer pour vérifier qu'un compte root comprenant les paramettres présent dans le fichier conf.properties
     * existe bien dans la base de données. Si il n'existe pas, ce compte root est crée avec les paramètre récupéré dans
     * le fichier conf.properties. SI les paramettres trouvé dans la base de données diffère de ceux trouvé dans le
     * fichier il sont modifié en prennant pour base ce qui est inscrit dans le fichier.
     *
     * @throws IOException : Si on ne trouve pas les fichier properties nécessaire, cette exception est levée.
     * @throws Exception
     */
    public void verifRootAccount() throws IOException, Exception {
        String propfile = PropertyLoader.loadProperti("serv.properties", "conf");
        Properties prop = PropertyLoader.loadFromFile(propfile);
        //--------------Chargement de l'utilisateur root par default
        String m = prop.getProperty("rootuser");
        String p = prop.getProperty("rootpass");
        UserAccount u = new UserAccount();
        u.setMail(m);
        u.setPass(p);
        u.setUsername("root");
        u.setAdminstatut(Boolean.TRUE);
        u.setAdminMail(Boolean.FALSE);
        u.setRootAccount(Boolean.TRUE);


        // On cherche si cet utilisateur est bien dans la base de données
        DAOUser daou = DAOFactory.getInstance().getDAOUser();
        UserAccount uBdd = daou.findPrMail(m);

        // Si, on n'a pas trouvé dans la base de données
        if (uBdd == null) {
            logger.debug("Création du compte root dans la BDD");
            daou.beginTransaction();
            daou.creer(u);
            daou.commit();
        } //Si les deux ne sont pas egaux en conten u
        else if (!uBdd.equals(u)) {
            logger.debug("Mise à jour du compte root dans la BDD");
            uBdd.setMail(m);
            uBdd.setPass(p);
            
            daou.beginTransaction();
            daou.modifier(uBdd);
            daou.commit();
        }
        System.out.println("================================================");
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /***
     * Un main de test
     * @param args 
     */
    public static void main(String[] args) {
        DAOConf d = DAOFactory.getInstance().getDAOConf();
        try {
            d.verifRootAccount();
        } catch (IOException ex) {
            Logger.getLogger(DAOConf.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DAOConf.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    
    /***
     * Il n'y a qu'une conf. La redéfinition de cette méthode permet la compatibilité avec les outils de {@link ServletTool). Cette méthode se contente de renvoyer l'instance de confcourante
     * @param id
     * @return 
     */
    @Override
    public Object find(Long id) {
        System.out.println("FIND CONF : " + this.confCourante);
        return confCourante;
//        return super.find(id); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
