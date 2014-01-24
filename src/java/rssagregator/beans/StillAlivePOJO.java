/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import rssagregator.services.tache.TacheStillAlive;

/**
 * Ce Pojo est un simple objet possedant une liste de tableau à deux dates, il est utilisé par la tâche
 * {@link TacheStillAlive} qui est lancé périodiquement pour effectuer une écriture sur le disque du serveur afin de
 * marquer son fonctionnement. Les méthode load et write permettent de sérialiser désérialiser cet objet dans un JSON.
 * La méthode {@link #check(java.lang.Integer) } permet de vérifier la dernière date enregistré dans le pojo, elle est
 * comparée par rapport a la date courrante. Il est alors possible de savoir si la dernière écriture dans le fichier a
 * dépasser un certain laps de temps
 *
 * @author clem
 */
public class StillAlivePOJO {
//    File f;

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(StillAlivePOJO.class);
    
    /***
     * Une liste de tableau de date. chaque ensemble de deux date correspond à un interval de fonctionnement du serveur.
     */
    List<Date[]> alive = new ArrayList<Date[]>();
    
    
    
    private transient Date debutRupture = null;
    private transient Date finRupture = null;

    /**
     * Constructeur du pojo
     */
    public StillAlivePOJO() {
    }

    /**
     * Charge le POJO depuis le fichier envoyé en argument. Le JSON est désérialiser pour retourner le POJO
     * @param f le fichier qu'il ou le POJO est sérialisé
     * @return Une instance du pojo
     * @throws FileNotFoundException 
     */
    public static StillAlivePOJO load(File f) throws FileNotFoundException {
        //On charge le fichier
//        FileReader fileReader = new FileReader(f);
//        BufferedReader br = new BufferedReader(new FileReader(f));
//        StillAlivePOJO pojo = (StillAlivePOJO) XMLTool.unSerialize(new FileInputStream(f));
        StillAlivePOJO pojo = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            pojo = mapper.readValue(f, StillAlivePOJO.class);
        } catch (IOException ex) {
            Logger.getLogger(StillAlivePOJO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pojo;
    }

    /**
     * *
     * Sérialise le pojo et l'enregistre dans le fichier envoyé en argument
     *
     * @param f le fichier dans lequel il faut écrire
     * @throws IOException
     */
    public void write(File f) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(f, this);
    }

    /**
     * *
     * On parcours les date du pojo afin de vérifier si la dernère n'est pas anormale par rapport au nombre de seconde
     * envoyé en argument.
     *
     * @param maxNbrSeconde : Le nombre de seconde maximum qui est considéré comme normal entre la dernière date de
     * check et la date actuelle
     * @return True si le la durée entre la date actuelle et la dernière vérification est inférieur au maxNbrSeconde;
     * false sinon
     */
    public Boolean check(Integer maxNbrSeconde) {
        Boolean retour = true;
        // On récupère le dernier still alive

        if (alive != null && alive.size() > 0) {
            Date[] lastDate = alive.get(alive.size() - 1);
            
            
            DateTime dt = new DateTime(lastDate[1]); // Il s'agit de la dernière date d'écriture du fichier
            DateTime dtCurrent = new DateTime();
            //Si la date courante moins 6 minutes est bien avant la derniere date constaté dans le still alive
            if (dtCurrent.minusSeconds(maxNbrSeconde).isBefore(dt)) {
                //On remplace la deuxieme date par la date courante
                lastDate[1] = new Date();
                retour = false;

            } else { // Sinon il y a bien eu rupture du service
                
                
                debutRupture = dt.toDate();
                finRupture = dtCurrent.toDate();
                
                //On ajoute un nouveau tableau de date
                Date[] ndate = new Date[]{new Date(), new Date()};
                
                this.alive.add(ndate);

                retour = true;
            }
        } else if (alive != null && alive.isEmpty()) {
            Date[] nDate = new Date[]{new Date(), new Date()};
            alive.add(nDate);
            retour = false;
        }

        return retour;
    }

    /***
     * @see #alive
     * @return 
     */
    public List<Date[]> getAlive() {
        return alive;
    }
/***
 * @see #alive
 * @param alive 
 */
    public void setAlive(List<Date[]> alive) {
        this.alive = alive;
    }

    public Date getDebutRupture() {
        return debutRupture;
    }

    public void setDebutRupture(Date debutRupture) {
        this.debutRupture = debutRupture;
    }

    public Date getFinRupture() {
        return finRupture;
    }

    public void setFinRupture(Date finRupture) {
        this.finRupture = finRupture;
    }
    
    
    
}
