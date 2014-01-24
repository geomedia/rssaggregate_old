/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import com.sun.syndication.io.FeedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import rssagregator.beans.Item;

/**
 *
 * @author clem
 */
@Entity
@Table(name = "tr_parseur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@XmlRootElement
public class AbstrParseur implements Serializable, Cloneable,  Callable<List<Item>> {

    protected String forceEncoding;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    
//        @Transient
//    InputStream inputStream;
        
        @Transient
        byte[] contenuAParser;

    public AbstrParseur() {
    }

        


    public void testParse() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    public List<Item> execute(byte[] xml) throws IOException, IllegalArgumentException, FeedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

//    public InputStream getInputStream() {
//        return inputStream;
//    }
//
//    public void setInputStream(InputStream inputStream) {
//        this.inputStream = inputStream;
//    }


    
    

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Item> call() throws Exception {
       return execute(contenuAParser);
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.ID != null ? this.ID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstrParseur other = (AbstrParseur) obj;
        if (this.ID != other.ID && (this.ID == null || !this.ID.equals(other.ID))) {
            return false;
        }
        return true;
    }

    public String getForceEncoding() {
        return forceEncoding;
    }

    public void setForceEncoding(String forceEncoding) {
        this.forceEncoding = forceEncoding;
    }

    public byte[] getContenuAParser() {
        return contenuAParser;
    }

    public void setContenuAParser(byte[] contenuAParser) {
        this.contenuAParser = contenuAParser;
    }
    
    
    
 
    
}
