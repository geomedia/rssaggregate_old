package rssagregator.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import rssagregator.services.ServiceCollecteur;


public class Conf extends AbstrObservableBeans implements Serializable {

    public Conf() {
    this.active = true;
    this.serveurSlave = new ArrayList<ServeurSlave>();
    }
    
    
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    
    /***
     * Le nom du serveur. Permet la configuration dans le serveur JMS
     */
    String servname;
    
    /***
     * L'host du provider JMS pour la synchronisation
     */
    String jmsprovider;
    
    /**
     * Nombre de thread de récupération
     */
    private Integer nbThreadRecup;
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
     * Le jour pour lequel la synchro doit être effectuée. Cette variable prend les valeures; lu, ma, me, je,ve, sa ,di
     */
    private String jourSync;
    
    /***
     * L'heure de la synchro . de 00 à 23
     */
    private Integer heureSync;
    
    /***
     * L'adresse http du server exemple http://ip/RSSAgregade
     */
    private String servurl;
    
    
    /***
     * Spécifie si le serveur est actif ou non, c'est à dire, si il collecte ou non l'information. 
     */
//    @Column(name = "active")
    private Boolean active;
    
    private String login;
    private String pass;
    
    
    /***
     * Il s'agit de la path contenant les fichiers de conf du serveur
     */
    private String varpath;

    public void verifConf() {
    }

    public Integer getNbThreadRecup() {
        return nbThreadRecup;
    }

    public void setNbThreadRecup(Integer nbThreadRecup) {
        this.nbThreadRecup = nbThreadRecup;
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

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void forceNotifyObserver() {
        this.setChanged();
        this.notifyObservers();
    }

    public String getJourSync() {
        return jourSync;
    }

    public void setJourSync(String jourSync) {
        this.jourSync = jourSync;
    }

    public Integer getHeureSync() {
        return heureSync;
    }

    public void setHeureSync(Integer heureSync) {
        this.heureSync = heureSync;
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
     * Retourne le nombre de seconde avant la prochaine synchronisation du serveur maitre 
     * @return 
     */
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
    @Override
    public void enregistrerAupresdesService() {
        this.addObserver(ServiceCollecteur.getInstance());
    }

    
}