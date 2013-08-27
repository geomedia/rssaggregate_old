/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.Conf;
import rssagregator.beans.ServeurSlave;
import rssagregator.beans.form.DAOGenerique;
import rssagregator.utils.PropertyLoader;

/**
 *
 * @author clem
 */
public class DAOConf extends AbstrDao {

    private Conf confCourante;

    public DAOConf(DAOFactory dAOFactory) {
        em = dAOFactory.getEntityManager();
        this.dAOFactory = dAOFactory;
        this.classAssocie = Conf.class;
    }

    public Conf getConfCourante() {
        return confCourante;
    }

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
     * Charge la configuration du serveur à partir du fichier conf.properties
     *
     * @throws IOException
     * @throws Exception
     */
    public void charger() throws IOException, Exception {
        Conf conf = new Conf();
        Properties prop = PropertyLoader.load("conf.properties");

        // Chargement de la valeur active dans le fichier properties
        String active = prop.getProperty("active");
        if (active.equals("1")) {
            conf.setActive(Boolean.TRUE);
        } else if (active.equals("0")) {
            conf.setActive(Boolean.FALSE);
        } else {
            throw new Exception("Impossible de lire la valeur de active");
        }

        //Chargement du nom du serveur
        String servname = prop.getProperty("servname");
        conf.setServname(servname);

        //Chargement du jmsprovider
        String jmsprovider = prop.getProperty("jmsprovider");
        conf.setJmsprovider(jmsprovider);


        //chargement du login d'administration
        String login = prop.getProperty("login");
        conf.setLogin(login);


        // chargement du pass
        String pass = prop.getProperty("pass");
        conf.setPass(pass);


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
            String slaveserver = PropertyLoader.loadProperti("conf.properties", "slaveserver");
            String[] slavetab = slaveserver.split("; ");
            for (int i = 0; i < slavetab.length; i++) {
                String string = slavetab[i];
                String split[] = string.split(" ");
                ServeurSlave serveurSlave = new ServeurSlave();
                serveurSlave.setHost(split[0]);
                serveurSlave.setLogin(split[1]);
                serveurSlave.setPass(split[2]);
                serveurSlave.setUrl(split[3]);
                conf.getServeurSlave().add(serveurSlave);
            }

            // Chargement du jour de synchronisation
            String jourSync = PropertyLoader.loadProperti("conf.properties", "jourSync");
            conf.setJourSync(jourSync);
            System.out.println("JOUR SYNC : " + jourSync);

            //Chargement de l'heure de synchronisation
            String heureSync = PropertyLoader.loadProperti("conf.properties", "heureSync");
            try {
                conf.setHeureSync(new Integer(heureSync));
                System.out.println("HEURE SYNC CHARGE : " + heureSync);

            } catch (Exception e) {
                throw new Exception("Impossible de charger la valeur heure sync dans le fichier properties");
            }

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

        //-----Chargement du nombre de Thread actives sur le serveur
        String nbThreadRecup = prop.getProperty("nbThreadRecup");
        try {
            Integer nbThreadInteger = new Integer(nbThreadRecup);
            conf.setNbThreadRecup(nbThreadInteger);
        } catch (Exception e) {
            throw new Exception("impossible de charger le nombre de thread dans le fichier properties");
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

        this.confCourante = conf;
    }

    public static void main(String[] args) {



        DAOFactory dAOFactory = DAOFactory.getInstance();
        DAOConf dao = new DAOConf(dAOFactory);
        try {
            dao.charger();

            dao.confCourante.setNbThreadRecup(6);
            dao.modifierConf(dao.confCourante);
        } catch (IOException ex) {
            Logger.getLogger(DAOConf.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DAOConf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * *
     * Charge la config courante depuis la BDD. Cette méthode doit être lancée
     * au démarrage de l'application (servlet start)
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
     * Enregistre la conf dans le fichier properties du serveur
     *
     * @param conf
     * @throws Exception
     */
    public void modifierConf(Conf conf) throws Exception {

        Properties prop = PropertyLoader.load("conf.properties");
        

        //--------------- Enregistrement du statut actif//innactif
        if (conf.getActive()) {
            prop.setProperty("active", "1");
        } else {
            prop.setProperty("active", "0");
        }

        //--------------Nombre de Thread pour la récup des flux
        prop.setProperty("nbThreadRecup", conf.getNbThreadRecup().toString());



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
        String chaine = "";
        for (i = 0; i < conf.getServeurSlave().size(); i++) {
            ServeurSlave slave = conf.getServeurSlave().get(i);
            // On construit la chaine de caractères dans le fichier host;
            chaine += slave.getHost() + " " + slave.getLogin() + " " + slave.getPass() + " " + slave.getUrl() + ";";
        }
        if (chaine.length() > 0) {
            chaine = chaine.substring(0, chaine.length() - 1);
        }

        prop.setProperty("slaveserver", chaine);


        //-------------Durée de purge
        if (conf.getPurgeDuration() != null) {
            prop.setProperty("purgeDuration", conf.getPurgeDuration().toString());
        }


        //-------------------Host du serveur maitre
        if (conf.getHostMaster() != null) {
            prop.setProperty("hostMaster", conf.getHostMaster());
        }

        //---------------JOUR de synchronisation
        if (conf.getJourSync() != null) {
            prop.setProperty("jourSync", conf.getJourSync());
        }

        //----------------HEURE de synchronisation
        if (conf.getHeureSync() != null) {
            prop.setProperty("heureSync", conf.getHeureSync().toString());
        }

        PropertyLoader.save(prop, "conf.properties", "LALALA commentaire");
        System.out.println("end");
    }
}
