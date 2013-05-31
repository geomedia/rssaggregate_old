/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author clem
 */
@Entity
@Table(name = "tr_requesteur")
public class AbstrRequesteur implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    /**
     * *
     * par exemple User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:16.0)
     * Gecko/20100101 Firefox/16.0". Pour certain serveur, si l'on ne spécifie
     * pas la request property on peut subir une redirection
     */
    private String[] requestProperty;
    /**
     * *
     * Le timeout de la connection
     */
    private Integer timeOut;
    /**
     * *
     * Après avoir effectué la requête, le requester inscrit le code retour
     * server (200, 404...) dans cette variable
     */
    private Integer httpStatut;
    /**
     * *
     * Le contenu retourné par la requête (HTML, XML...).
     */
    private String HttpResult;
    
    @Transient
    protected InputStream httpInputStream;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String[] getRequestProperty() {
        return requestProperty;
    }

    public void setRequestProperty(String[] requestProperty) {
        this.requestProperty = requestProperty;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public Integer getHttpStatut() {
        return httpStatut;
    }

    public void setHttpStatut(Integer httpStatut) {
        this.httpStatut = httpStatut;
    }

    public String getHttpResult() {
        return HttpResult;
    }

    public void setHttpResult(String HttpResult) {
        this.HttpResult = HttpResult;
    }

    public void requete(String urlArg) throws MalformedURLException, IOException {
    }

    public InputStream getHttpInputStream() {
        return httpInputStream;
    }

    public void setHttpInputStream(InputStream httpInputStream) {
        this.httpInputStream = httpInputStream;
    }
    
    
    public void disconnect(){
    
}

}
