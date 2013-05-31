package rssagregator.beans.traitement;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.XmlReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import rssagregator.beans.Item;


/*
 */
public class Requester extends AbstrRequesteur {

    private static String description = "Requester par défault";
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
    public Integer timeOut;
    /**
     * *
     * Après avoir effectué la requête, le requester inscrit le code retour
     * server (200, 404...) dans cette variable
     */
    private Integer httpStatut;
    /**
     * *
     * Le contenu retourné par la requête (HTML, XML...). Maintenant on préfère manier un input stream. C'est au parseru de détercter l'encodage
     */
    @Deprecated
    private String HttpResult;
//    private InputStream httpInputStream;
    private HttpURLConnection conn;

    /**
     * *
     * Constructeur vide. Il faut passer par les méthodes de construction pour
     * obtenir des requester préconfiguré. On passera peut être ce constructeur
     * en privé à voir si ca pose un problème pour faire un beans
     */
    public Requester() {
    }

    /**
     * On envoie une url. Le serveur renvoie du XML. En cas d'erreur, il leve
     * une exeption contenant l'erreur http
     */
    @Override
    public void requete(String urlArg) throws MalformedURLException, IOException {

        URL url = new URL(urlArg);

        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (this.requestProperty != null && requestProperty.length == 2) {
            conn.setRequestProperty(this.requestProperty[0], this.requestProperty[1]);
        }

        conn.setInstanceFollowRedirects(true);

        conn.connect();
        this.httpStatut = conn.getResponseCode();



        this.httpInputStream = conn.getInputStream();
        




    }
    public static void main(String[] args) {
        Requester r = new Requester();
        try {
            r.requete("http://www.google.free");
            
        } catch (MalformedURLException ex) {
           
            Logger.getLogger(Requester.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(UnknownHostException e){
            System.out.println("fin");
        }
        catch (IOException ex) {
             
            Logger.getLogger(Requester.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }

    /**
     * *
     * Retourne un connecteur générique
     */
    public static AbstrRequesteur getDefaulfInstance() {

        Requester r = new Requester();
        r.requestProperty = new String[]{"User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:16.0) Gecko/20100101 Firefox/16.0"};
        r.timeOut = 10;

        return r;
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




    @Override
    public void disconnect(){
    conn.disconnect();
    
        System.out.println(">>>>>>>>>>>>>>> DECONNECTION ");
}

    @Override
    protected void finalize() throws Throwable {
        try {
            conn.getInputStream().close();
            conn.disconnect();
            System.out.println("FERMETURE PAR FINALYSEE");
        } finally {
            super.finalize();
        }
    }

  
}