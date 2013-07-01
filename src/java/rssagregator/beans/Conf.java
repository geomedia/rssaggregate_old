package rssagregator.beans;

import java.io.Serializable;
import java.sql.Time;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class Conf implements Serializable {

    public Conf() {
    this.active = true;
    }
    
    

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    /**
     * Nombre de thread de récupération
     */
    @Column(name = "nbthreadrecup")
    private Integer nbThreadRecup;
    /**
     * Delai au bout duquel les données doivent être stoquées supprimée de la
     * base de données. Si 0 pas de durée. Ce paramettre est utilisée pouir ne
     * pas encombrer les serveurs de secours de données innutiles.
     */ 
    @Transient
    private Time purgeDuration;
    /**
     * Statut du serveur. Peut prendre deux valeurs, maitre ou esclave
     */
    @Transient
    private String statutServeur;
    /**
     * Les serveur slaves doivent connaitre l'ip du serveur maitre (seul lui est
     * autorisé à effecter la récup). Il est ainsi indispensable de remplir ce
     * champs dans les serveurs esclaves. Pour le serveru maitre on inscrira
     * simplement localhost. Ce localhost pourra être simplement complété lors
     * de la configuration du serveru en maitre (attribution du paramètre
     * statutServer)
     */
    @Transient
    private String hostMaster;
    /**
     * *
     * Liste des serveur esclaves utiles au serveur maitre
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<ServeurSlave> ServeurSlave;
    
    /***
     * Spécifie si le serveur est actif ou non, c'est à dire, si il collecte ou non l'information. 
     */
    @Column(name = "active")
    private Boolean active;
    

    public void verifConf() {
    }

    public Integer getNbThreadRecup() {
        return nbThreadRecup;
    }

    public void setNbThreadRecup(Integer nbThreadRecup) {
        this.nbThreadRecup = nbThreadRecup;
    }

    public Time getPurgeDuration() {
        return purgeDuration;
    }

    public void setPurgeDuration(Time purgeDuration) {
        this.purgeDuration = purgeDuration;
    }

    public String getStatutServeur() {
        return statutServeur;
    }

    public void setStatutServeur(String statutServeur) {
        this.statutServeur = statutServeur;
    }

    public String getHostMaster() {
        return hostMaster;
    }

    public void setHostMaster(String hostMaster) {
        this.hostMaster = hostMaster;
    }

    public List<ServeurSlave> getServeurSlave() {
        return ServeurSlave;
    }

    public void setServeurSlave(List<ServeurSlave> ServeurSlave) {
        this.ServeurSlave = ServeurSlave;
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
    
    
    
    
}