/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import rssagregator.utils.XMLTool;

/**
 *
 * @author clem
 */
public class StillAlivePOJO {
//    File f;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(StillAlivePOJO.class);
    List<Date[]> alive;
    
    /**
     * 
     */

    

    public StillAlivePOJO() {
        alive = new ArrayList<Date[]>();
    }

    public static StillAlivePOJO load(File f) throws FileNotFoundException {
        //On charge le fichier
//        FileReader fileReader = new FileReader(f);
//        BufferedReader br = new BufferedReader(new FileReader(f));
        StillAlivePOJO pojo = (StillAlivePOJO) XMLTool.unSerialize(new FileInputStream(f));
        

        return pojo;


    }

    public void write(File f) throws IOException {

        String xml = XMLTool.serialise(this);
        //enregistrement
        OutputStreamWriter osw = new FileWriter(f);
        osw.write(xml, 0, xml.length());
        osw.flush();
        osw.close();

    }

    /***
     * 
     * @param maxNbrSeconde : Le nombre de seconde maximum qui est considéré comme normal entre la dernière date de check et la date actuelle
     * @return True si le la durée entre la date actuelle et la dernière vérification est inférieur au maxNbrSeconde; false sinon
     */
    public Boolean check(Integer maxNbrSeconde) {
        Boolean retour = true;
        // On récupère le dernier still alive
        
        if (alive!= null && alive.size() > 0) {
            Date[] lastDate = alive.get(alive.size()-1);
            DateTime dt = new DateTime(lastDate[1]);
            DateTime dtCurrent = new DateTime();
            //Si la date courante moins 6 minutes est bien avant la derniere date constaté dans le still alive
            if (dtCurrent.minusSeconds(maxNbrSeconde).isBefore(dt)) {
                //On remplace la deuxieme date par la date courante
                lastDate[1] = new Date();
                retour = false;
                
            } else { // Sinon il y a bien eu rupture du service
                //On ajoute un nouveau tableau de date
                Date[] ndate = new Date[]{new Date(), new Date()};
                this.alive.add(ndate);
                
                retour = true;
            }
        }
        else if(alive!=null && alive.isEmpty()){
            Date[] nDate = new Date[]{new Date(), new Date()};
            alive.add(nDate);
            retour = false;
        }

        return retour;
    }

    public List<Date[]> getAlive() {
        return alive;
    }

    public void setAlive(List<Date[]> alive) {
        this.alive = alive;
    }



//    public static void main(String[] args) throws IOException {
////        StillAlivePOJO alivePOJO = new StillAlivePOJO();
////        alivePOJO.getAlive().add(new Date[]{new Date(), new Date()});
//        File fi = new File("/home/clem/pojo");
////        alivePOJO.write(fi);
//
//        StillAlivePOJO alivePOJO2 =StillAlivePOJO.load(fi);
//        alivePOJO2.check();
//        alivePOJO2.write(fi);
//
//    }
}
