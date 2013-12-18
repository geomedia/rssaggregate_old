/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.UnavailableException;
import rssagregator.beans.exception.RessourceIntrouvable;

/**
 * Une classe posédant des méthode static dédiée au maniement de fichiers properties.
 *
 * @author clem
 */
public class PropertyLoader {

    private static org.apache.log4j.Logger logger2 = org.apache.log4j.Logger.getLogger(PropertyLoader.class);

    /**
     * Charge le fichier properties QUI EST CONTENU DANS LE PROJET
     *
     * @param filename le fichier contenant les propriétés
     * @return un objet Properties contenant les propriétés du fichier
     */
    public static Properties load(String filename) throws IOException, FileNotFoundException {
        Properties properties = new Properties();
        InputStream is = PropertyLoader.class.getClassLoader().getResourceAsStream(filename);
        properties.load(is);
        Properties p = new Properties();
        is.close();
        return properties;
    }

    /**
     * *
     * Charge un fichier properties depuis un emplacement sur le disque dur (exemple :
     * /var/lib/RSSAgregate/conf.properties)
     *
     * @param filename : ler path et nom de fichier exemple "/var/lib/RSSAgregate/conf.properties"
     * @return L'objet properties ou null si il n'a pas été trouvé"
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Properties loadFromFile(String filename) {
        FileInputStream fis = null;
        Properties p = null;
        try {
            p = new Properties();
            fis = new FileInputStream(filename);
            p.load(fis);

        } catch (Exception e) {
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(PropertyLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return p;
    }

    /**
     * *
     * Sauvegarde l'objet properties dans un fichier properties propre au projet NE MARCHE PAS VRAIMENT, après
     * redémarage on ne retrouve pas les valeur et le fichier est retourné a son état initiale. On préfère utiliser la
     * méthode saveToFile pour sauvegarder dans un répertoire a part / usr/lib. Le fichier conf.properties notamment
     *
     * @param prop
     * @param filename
     * @param comment
     * @throws URISyntaxException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void save(Properties prop, String filename, String comment) throws URISyntaxException, FileNotFoundException, IOException {

        URL resourceUrl = PropertyLoader.class.getClassLoader().getResource(filename);
        System.out.println("URL DU FICHIER PROP : " + resourceUrl.toURI());
//        URL truc = PropertyLoader.class.getClassLoader().getResourceAsStream(filename).;

        File file = new File(resourceUrl.toURI());
        System.out.println("LA PATH : " + file.getAbsolutePath());
        OutputStream os = new FileOutputStream(file);
        prop.store(os, "lalala");
        os.close();
    }

    /**
     * *
     * Sauvegarde l'objet properties dans un fichier properties correspondant aux références envoyées en paramettre
     *
     * @param prop : l'objet properties à sauvegarder
     * @param fileDestination : le fichier destination
     */
    public static void saveToFile(Properties prop, String fileDestination, String comment) throws FileNotFoundException, IOException {
        OutputStreamWriter osw = new FileWriter(fileDestination);
        prop.store(osw, comment);
    }

    /**
     * *
     * Charge une propriété a partir du fichier properties demandé
     *
     * @param filename
     * @param prop
     * @return
     * @throws IOException
     */
    public static String loadProperti(String filename, String prop) throws IOException {
        Properties p = load(filename);
        String sp = p.getProperty(prop);
        return sp;
    }

    
    
    /***
     * Charge une ressource depuis le context tomcat. Renvoie null si rien trouvé et emmet des erreurs avec log4j
     * @param var  le nom de la ressource (à configurer dans context.xml)
     * @return la ressource ou null
     */
    public static Object loadFromContext(String var) {
        try {
            Context initialCtx = new InitialContext();
            Context localCtx = (Context) initialCtx.lookup("java:comp/env");
            Object o = localCtx.lookup(var);
            if (o == null) {
                throw new RessourceIntrouvable("Impossible de trouver la ressource " + var + " dans le context");
            } else {
                return o;
            }

        } catch (NamingException e) {
            logger2.error("Impossible de trouver la ressource dans le context" + var, e);
        } catch (RessourceIntrouvable e) {
            logger2.error("Impossible de trouver la ressource dans le context" + var, e);
        }
        return null;
    }
    
    /***
     * Retoune le répertoire contenant les fichiers de conf ainsi que les log... en dehors du class path de l'application. Un point unique pour trouver cette ressource. 
     * @return string exemple /var/lib/RSSAgregate
     */
    public static String returnConfPath(){
        
        String ress = System.getProperty("confpath");
        return ress;
    }
    

    /**
     * *
     * Un main pour tester
     *
     * @param args
     */
    public static void main(String[] args) {

        Properties pp = new Properties();
        pp.setProperty("#C'est mon commentaire ", "");

        pp.setProperty("name", "Bobizz");
        try {
            OutputStreamWriter osw = new FileWriter("/var/lib/RSSAgregate/conf.properties");

            pp.store(osw, null);
        } catch (IOException ex) {
            Logger.getLogger(PropertyLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        Properties p = new Properties();
        FileInputStream fis;
        try {
            fis = new FileInputStream("/var/lib/RSSAgregate/conf.properties");
            p.load(fis);
            System.out.println("name : " + p.getProperty("name"));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(PropertyLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PropertyLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
