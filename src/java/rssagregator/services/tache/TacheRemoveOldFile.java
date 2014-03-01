/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Cette tache a pour but de parcourir les fichier du répertoire web/upload et de supprimer les rep ^EXPORT-- trop vieu. L'utilisateur a deux jours pour récupérer ses fichiers.
 *
 * @author clem
 */
public class TacheRemoveOldFile extends TacheImpl<Object> {

    /***
     * Nombre d'heure max de conservation des fichiers dans le répertoire 
     */
    private Integer nbHours;
    
    @Override
    protected void callCorps() throws InterruptedException, Exception {

        
        String webdir = System.getProperty("webdir") + "upload/";
        File upDir = new File(webdir);
        File sousDir[] = upDir.listFiles();

        DateTime dtCurrent = new DateTime();
        List<File> listFiles = Arrays.asList(sousDir);
        for (Iterator<File> it = listFiles.iterator(); it.hasNext();) {
            File file = it.next();

            if (file.getName().startsWith("EXPORT--")) {

                long date = file.lastModified();
                DateTime dt = new DateTime(date);

                Duration dur = new Duration(dt, dtCurrent);
                if (dur.getStandardDays()> nbHours) {
                    logger.info("Suppression du répertoire " + file);
                    file.delete();
                }
            }
        }
    }

    public Integer getNbHours() {
        return nbHours;
    }

    public void setNbHours(Integer nbHours) {
        this.nbHours = nbHours;
    }
    
    
    
}
