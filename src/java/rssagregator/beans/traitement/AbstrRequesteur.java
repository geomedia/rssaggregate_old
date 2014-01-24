/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.concurrent.Callable;
import javax.naming.TimeLimitExceededException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.ws.http.HTTPException;

/**
 * Le requesteur est chargé d'effectuer la requete. Il récupère
 * <ul>
 * <li>{@link #resu} : un tableau d'octet contenant par exemple le docuement xml récupéré</li>
 * <li>{@link #httpStatut} 200, 404 , 500 : code retour HTTP</li>
 * </ul>
 * @author clem
 */
@Entity
@Table(name = "tr_requesteur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@XmlRootElement
public class AbstrRequesteur implements Serializable, Cloneable, Callable<Object> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    @Transient
    protected transient String url;

    public AbstrRequesteur() {
    }
    /**
     * *
     * par exemple User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:16.0) Gecko/20100101 Firefox/16.0". Pour
     * certain serveur, si l'on ne spécifie pas la request property on peut subir une redirection
     */
    protected String[][] requestProperty;
    /**
     * *
     * Le timeout de la connection
     */
    protected Integer timeOut;
    /**
     * *
     * Après avoir effectué la requête, le requester inscrit le code retour server (200, 404...) dans cette variable
     */
    protected Integer httpStatut;
  
    
    /**
     * *
     * Un tableau d'octet contenant le résultat de la requete. C'est le parseur qui doit lire en déterminant l'encodage
     */
    @Transient
    protected byte[] resu;

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
        newTab[newTab.length - 1][0] = cle;
        newTab[newTab.length - 1][1] = val;
        this.requestProperty = newTab;
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


    public void requete(String urlArg) throws MalformedURLException, HTTPException, IOException, TimeLimitExceededException, Exception {
    }


    /***
     * Methode permettant de clore la source utilisé (un fichier une connection a ressource distante...)
     */
    public void clore() {
        
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object call() throws Exception {
        requete(url);
        return null;

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getResu() {
        return resu;
    }

    public void setResu(byte[] resu) {
        this.resu = resu;
    }
    
    
    
}
