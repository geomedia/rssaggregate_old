package rssagregator.beans.traitement;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.naming.TimeLimitExceededException;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.xml.ws.http.HTTPException;


/*
 */
@Entity
public class Requester extends AbstrRequesteur {

    @Transient
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Requester.class);
    private static String description = "Requester par défault";
//    private InputStream httpInputStream;
    @Transient
    private HttpURLConnection conn;

    public Requester() {
    }

    /**
     * On envoie une url. Le serveur renvoie du XML. En cas d'erreur, il leve
     * une exeption contenant l'erreur http
     */
    @Override
    public void requete(String urlArg) throws MalformedURLException, HTTPException, IOException, TimeLimitExceededException, Exception {
        URL url = new URL(urlArg);


        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");


        if (timeOut != null) {
            conn.setConnectTimeout(timeOut * 1000);
        } else {
            conn.setConnectTimeout(12000);
        }

        int i;

        if (requestProperty != null) {
            for (i = 0; i < requestProperty.length; i += 2) {
                conn.setRequestProperty(this.requestProperty[i][0], this.requestProperty[i][1]);
            }
        }

//        if (this.requestProperty != null && requestProperty.length == 2) {
//            conn.setRequestProperty(this.requestProperty[0], this.requestProperty[1]);
//        }
        conn.setInstanceFollowRedirects(true);
        conn.connect();


        this.httpStatut = conn.getResponseCode();
//        System.out.println("CODE : " + httpStatut);
        if (httpStatut != 200) {
            logger.info("Erreur HTTP : " + httpStatut + ". " + urlArg);
            throw new HTTPException(httpStatut);
        }
        this.httpInputStream = conn.getInputStream();
    }

    /**
     * *
     * Retourne un connecteur générique
     */
    public static AbstrRequesteur getDefaulfInstance() {

        Requester r = new Requester();
        r.requestProperty = new String[1][2];
        r.requestProperty[0][0] = "User-Agent";
        r.requestProperty[0][1] = "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:16.0) Gecko/20100101 Firefox/16.0";

        r.timeOut = 12;

        return r;
    }

    @Override
    public void disconnect() {
        conn.disconnect();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (conn != null) {
                if (conn.getInputStream() != null) {
                    conn.getInputStream().close();
                }
                conn.disconnect();
            }
        } finally {
            super.finalize();
        }
    }
}