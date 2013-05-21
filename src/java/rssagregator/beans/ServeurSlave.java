package rssagregator.beans;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
@Entity
public class ServeurSlave implements Serializable{
    @Id
    private Long id;

    private String servHost;
    private String urlServletRecup;
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