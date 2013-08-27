/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import java.io.File;
import java.util.Properties;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author clem
 */
public class PropertyLoader {

    /**
     * Charge la liste des propriétés contenu dans le fichier spécifié
     *
     * @param filename le fichier contenant les propriétés
     * @return un objet Properties contenant les propriétés du fichier
     */
    public static Properties load(String filename) throws IOException, FileNotFoundException {
        Properties properties = new Properties();
        InputStream is = PropertyLoader.class.getClassLoader().getResourceAsStream(filename);
        properties.load(is);

        is.close();
        return properties;

    }

    public static void save(Properties prop, String filename, String comment) throws URISyntaxException, FileNotFoundException, IOException {

        URL resourceUrl =  PropertyLoader.class.getClassLoader().getResource(filename);
        System.out.println("URL DU FICHIER PROP : "+resourceUrl.toURI());
//        URL truc = PropertyLoader.class.getClassLoader().getResourceAsStream(filename).;
        
        File file = new File(resourceUrl.toURI());
        System.out.println("LA PATH : "+file.getAbsolutePath());
        OutputStream os = new FileOutputStream(file);
        prop.store(os, "lalala");
        os.close();

    }

    public static String loadProperti(String filename, String prop) throws IOException {
        Properties p = load(filename);
        String sp = p.getProperty(prop);
        return sp;
    }
    
    

}
