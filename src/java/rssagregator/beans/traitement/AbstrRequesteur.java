/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import javax.naming.TimeLimitExceededException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.ws.http.HTTPException;

/**
 *
 * @author clem
 */
@Entity
@Table(name = "tr_requesteur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AbstrRequesteur implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    public AbstrRequesteur() {
    }
    /**
     * *
     * par exemple User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:16.0)
     * Gecko/20100101 Firefox/16.0". Pour certain serveur, si l'on ne spécifie
     * pas la request property on peut subir une redirection
     */
    protected String[][] requestProperty;
    /**
     * *
     * Le timeout de la connection
     */
    protected Integer timeOut;
    /**
     * *
     * Après avoir effectué la requête, le requester inscrit le code retour
     * server (200, 404...) dans cette variable
     */
    protected Integer httpStatut;
    /**
     * *
     * Le contenu retourné par la requête (HTML, XML...).
     */
    protected String HttpResult;
    @Transient
    protected InputStream httpInputStream;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String[][] getRequestProperty() {
        return requestProperty;
    }

    public void setRequestProperty(String[][] requestProperty) {
        this.requestProperty = requestProperty;
    }

    public void addRequestProperty(String cle, String val) {
        String newTab[][];
        if (this.requestProperty == null) {
            newTab = new String[1][2];
            System.out.println("lo");
        } else {
            newTab = new String[this.requestProperty.length + 1][2];
            int i;
            for (i = 0; i < this.requestProperty.length; i++) {
                newTab[i][0] = this.requestProperty[i][0];
                newTab[i][1] = this.requestProperty[i][1];
            }
        }
        newTab[newTab.length-1][0] = cle;
        newTab[newTab.length-1][1] = val;
        this.requestProperty = newTab;
    }

    public static void main(String[] args) {
        Requester requester = new Requester();
        requester.addRequestProperty("zozo1", "zaza1");
        requester.addRequestProperty("zozo2", "zaza2");
        requester.addRequestProperty("zozo3", "zaza3");
        
        int i;
        for(i=0;i<requester.requestProperty.length;i++){
            System.out.println("cle : " + requester.requestProperty[i][0]+" val : " + requester.requestProperty[i][1]);
        }
        
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

    public void requete(String urlArg) throws MalformedURLException, HTTPException, IOException, TimeLimitExceededException, Exception {
    }

    public InputStream getHttpInputStream() {
        return httpInputStream;
    }

    public void setHttpInputStream(InputStream httpInputStream) {
        this.httpInputStream = httpInputStream;
    }

    public void disconnect() {
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
