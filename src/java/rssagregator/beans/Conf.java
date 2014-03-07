package rssagregator.beans;

import java.io.Serializable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;

/***
 * Bean permettant de stoquer la configuration du serveur. La configuration n'est pas faite dans la base de donnée. Les données proviennent des sources suivante :<ul>
 * <li>conf.properties : fichier de config </li>
 * <li>Base de donnée : les entitées de type {@link ServeurSlave} sont dans la base de données</li>
 * 
 * </ul>
 * 
 * @author clem
 */
public class Conf implements Serializable {

    public Conf() {
    this.active = true;
//    this.serveurSlave = new ArrayList<ServeurSlave>();
    }
    
        
    /***
     * Indique si le serveur est en production. Certain messages d'erreur ne sont affiché que si il est en debug
     */
    Boolean prod;
    

    /***
     * L'adresse http du server exemple http://ip/RSSAgregade
     */
    private String servurl;
    
    
    /***
     * Spécifie si le serveur est actif ou non, c'est à dire, si il collecte ou non l'information. 
     */
    private Boolean active;
    
    
    /***
     * Pour vérifier la cohérence de la configuration
     * @deprecated
     */
    @Deprecated
    public void verifConf() {
        throw new UnsupportedOperationException("Non Implémenté");
    }

    public Boolean getProd() {
        return prod;
    }

    public void setProd(Boolean prod) {
        this.prod = prod;
    }
    
    
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getServurl() {
        return servurl;
    }

    public void setServurl(String servurl) {
        this.servurl = servurl;
    }

    
    /***
     * <p><strong>N'est plus utilisé. C'est maintenant chaque tâche qui contient les information sur sa schedulation</strong></p>
     * Retourne le nombre de seconde avant la prochaine synchronisation du serveur maitre 
     * @return 
     */
    @Deprecated
    public Long getDurationSync(){
        DateTime dtCurrent = new DateTime();
        DateTime next = dtCurrent.withDayOfWeek(DateTimeConstants.SUNDAY);
        Duration dur = new Duration(dtCurrent, next);
        return dur.getStandardSeconds();
    }
    

    
}