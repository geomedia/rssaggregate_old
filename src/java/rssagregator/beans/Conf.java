package rssagregator.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Conf implements Serializable {

    public Conf() {
    this.active = true;
    this.serveurSlave = new ArrayList<ServeurSlave>();
    }
    
    
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    
    
    String servname;
    
    String jmsprovider;
    
    /**
     * Nombre de thread de récupération
     */
//    @Column(name = "nbthreadrecup")
    private Integer nbThreadRecup;
    /**
     * Delai au bout duquel les données doivent être stoquées supprimée de la
     * base de données. Si 0 pas de durée. Ce int est un nombre de jours
     */ 
//    @Transient
    private Integer purgeDuration;
    /**
     * Statut du serveur. Peut prendre deux valeurs, 1 pour maitre 0 pour esclave
     */
//    @Transient
    private Boolean master;
    /**
     * Les serveur slaves doivent connaitre l'ip du serveur maitre (seul lui est
     * autorisé à effecter la récup). Il est ainsi indispensable de remplir ce
     * champs dans les serveurs esclaves. Pour le serveru maitre on inscrira
     * simplement localhost. Ce localhost pourra être simplement complété lors
     * de la configuration du serveru en maitre (attribution du paramètre
     * statutServer)
     */
//    @Transient
    private String hostMaster;
    /**
     * *
     * Liste des serveur esclaves utiles au serveur maitre
     */
//    @OneToMany(cascade = CascadeType.ALL)
    private List<ServeurSlave> serveurSlave;
    
    /***
     * Spécifie si le serveur est actif ou non, c'est à dire, si il collecte ou non l'information. 
     */
//    @Column(name = "active")
    private Boolean active;
    
    private String login;
    private String pass;

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
    
    
    
    
    
}