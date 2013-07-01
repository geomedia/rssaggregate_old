package rssagregator.beans;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
@Entity
public class ServeurSlave implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String servHost;
    
    private String urlServletRecup;
    
    private String login;
    private String pass;

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
    
    
    /***
     * La date pour laquelle il faut faire la synchronisation ntre le serveur maitre et esclave
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateRecup;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrlServletRecup() {
        return urlServletRecup;
    }

    public void setUrlServletRecup(String urlServletRecup) {
        this.urlServletRecup = urlServletRecup;
    }

    public Date getDateRecup() {
        return dateRecup;
    }

    public void setDateRecup(Date dateRecup) {
        this.dateRecup = dateRecup;
    }

    public String getServHost() {
        return servHost;
    }

    public void setServHost(String servHost) {
        this.servHost = servHost;
    }
}