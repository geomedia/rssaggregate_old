package rssagregator.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    this.serveurSlave = new ArrayList<ServeurSlave>();
    }
    
    
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long ID;
    
    /***
     * Le nom du serveur. Permet la configuration dans le serveur JMS
     */
    String servname;
    
    /***
     * L'host du provider JMS pour la synchronisation.
     */
    String jmsprovider;
    
    
    /***
     * Indique si le serveur est en production. Certain messages d'erreur ne sont affiché que si il est en debug
     */
    Boolean prod;
    

    /**
     * Delai au bout duquel les données doivent être stoquées supprimée de la
     * base de données. Si 0 pas de durée. Ce int est un nombre de jours
     */ 
    private Integer purgeDuration;
    /**
     * Statut du serveur. Peut prendre deux valeurs, 1 pour maitre 0 pour esclave
     */
    private Boolean master;
    /**
     * Les serveur slaves doivent connaitre l'ip du serveur maitre (seul lui est
     * autorisé à effecter la récup). Il est ainsi indispensable de remplir ce
     * champs dans les serveurs esclaves. Pour le serveru maitre on inscrira
     * simplement localhost. Ce localhost pourra être simplement complété lors
     * de la configuration du serveru en maitre (attribution du paramètre
     * statutServer)
     */
    private String hostMaster;
    /**
     * *
     * Liste des serveur esclaves utiles au serveur maitre
     */
    private List<ServeurSlave> serveurSlave;

    /***
     * L'adresse http du server exemple http://ip/RSSAgregade
     */
    private String servurl;
    
    
    /***
     * Spécifie si le serveur est actif ou non, c'est à dire, si il collecte ou non l'information. 
     */
    private Boolean active;
    
    
    /***
     * Il s'agit de la path contenant les fichiers de conf du serveur. Exemple /var/lib/RSSAgregate
     */
    private String varpath;

    /***
     * Pour vérifier la cohérence de la configuration
     * @deprecated
     */
    @Deprecated
    public void verifConf() {
        throw new UnsupportedOperationException("Non Implémenté");
    }

    public Integer getPurgeDuration() {
        return purgeDuration;
    }

    public void setPurgeDuration(Integer purgeDuration) {
        this.purgeDuration = purgeDuration;
    }

    public String getHostMaster() {
        return hostMaster;
    }

    public void setHostMaster(String hostMaster) {
        this.hostMaster = hostMaster;
    }

    public List<ServeurSlave> getServeurSlave() {
        return serveurSlave;
    }

    public void setServeurSlave(List<ServeurSlave> ServeurSlave) {
        this.serveurSlave = ServeurSlave;
    }

    public Boolean getProd() {
        return prod;
    }

    public void setProd(Boolean prod) {
        this.prod = prod;
    }
    
    
    

//    public Long getID() {
//        return ID;
//    }
//
//    public void setID(Long ID) {
//        this.ID = ID;
//    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getServname() {
        return servname;
    }

    public void setServname(String servname) {
        this.servname = servname;
    }

    public String getJmsprovider() {
        return jmsprovider;
    }

    public void setJmsprovider(String jmsprovider) {
        this.jmsprovider = jmsprovider;
    }

    public Boolean getMaster() {
        return master;
    }

    public void setMaster(Boolean master) {
        this.master = master;
    }

    public String getServurl() {
        return servurl;
    }

    public void setServurl(String servurl) {
        this.servurl = servurl;
    }

    public String getVarpath() {
        return varpath;
    }

    public void setVarpath(String varpath) {
        this.varpath = varpath;
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

    
    /***
     * Inscrit le beans aux service : <ul>
     * <li>Collecteur</li>
     * </ul>
     */
//    @Override
//    public void enregistrerAupresdesService() {
//        this.addObserver(ServiceCollecteur.getInstance());
//    }

    
}