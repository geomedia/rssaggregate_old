/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import java.util.UUID;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author clem
 */
public class FileUtils {
    
    
            /**
     * *
     * Crée une chaine aléatoire avec datage. utile pour crée le nom de fichier des email a sauvegarder dans des
     * fichiers.
     *
     * @param msg
     * @return
     */
           //TODO : on peut factoriser comme une méthode static
    public static String contructMailFileName() {

 
        // On construit le datega
        DateTime dt = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd--kk-mm-s-S");
        String alea = UUID.randomUUID().toString();

        String retour = fmt.print(dt) + "__" + alea;
        return retour;
    }
}
