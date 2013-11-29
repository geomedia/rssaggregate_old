package rssagregator.beans;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * *
 * Entité permettant de stocker les informations relatives aux serveurs esclaves associées au serveur maitre. Cette
 * entité est persisté dans le fichier conf.properties <strong>(PAS DANS LA BASE DE DONNEES)</strong>.
 *
 * @author clem
 */
@Entity
public class ServeurSlave implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    
    /***
     * Host du serveur esclave un DNS ou une adresse IP
     */
    @Column(name = "servHost")
    protected String servHost;
    
    /***
     * URL de l'appligation Agregate sur le serveur esclave. Exemple : http://ip/RSSAgregate. 
     */
    @Column(name = "url")
    protected String url;
    
    /***
     * login root sur le serveur esclave
     */
    @Column(name = "login")
    protected String login;
    
    /***
     * Mot de passe à utiliser pour se connecter au serveur esclave et effectuer la synchronisation.
     */
    @Column(name = "pass")
    protected String pass;
    
    
    /***
     * Un champ informatif permettant aux administrateur de décrire le serveur.
     */
    @Column(name = "description")
    protected String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
    
    
    /**
     * *
     * La date pour laquelle il faut faire la synchronisation ntre le serveur maitre et esclave ---> Maintenant c'est la configuration de la tache qui permet de définir cela
     */
    //    @Temporal(javax.persistence.TemporalType.DATE)
    //    private Date dateRecup;
    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    

    public String getServHost() {
        return servHost;
    }

    public void setServHost(String servHost) {
        this.servHost = servHost;
    }
    

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {

        return login + ":" + pass + "@" + servHost + "  " + url;
    }
}