/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import com.sun.syndication.io.FeedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.tomcat.util.buf.HexUtils;
import rssagregator.beans.Item;

/**
 *
 * @author clem
 */
@Entity
@Table(name = "tr_parseur")
public class AbstrParseur implements IfsParseur, Serializable {

    protected String forceEncoding;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    /**
     * *
     * Calcul les hash pour la list des items envoyés en paramètre
     *
     * @param listItem
     */
    protected static void calculHash(List<Item> listItem) throws NoSuchAlgorithmException {
        int i;
        String concat;
        for (i = 0; i < listItem.size(); i++) {
                Item item = listItem.get(i);
                concat = item.getTitre() + item.getDescription();
                MessageDigest digest = MessageDigest.getInstance("MD5");

                digest.reset();
                
                byte[] hash = digest.digest(concat.getBytes());
                String hashString = new String(HexUtils.toHexString(hash));
                item.setHashContenu(hashString);
        }
    }

    @Override
    public void testParse() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    public List<Item> execute(InputStream xml) throws IOException, IllegalArgumentException, FeedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }
}
