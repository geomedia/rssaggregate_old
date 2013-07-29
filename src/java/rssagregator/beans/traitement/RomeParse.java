package rssagregator.beans.traitement;

import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.Item;
import com.sun.syndication.io.XmlReader;
import java.util.Date;
import java.util.concurrent.Callable;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * test
 */
@Entity
public class RomeParse extends AbstrParseur implements Cloneable {
    /* {author=clem}*/

    /***
     * Le input stream provenant du requester
     */
    
    @Transient
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RomeParse.class);
    private final static String description = "Le parseur ROME est trop super...";

    /**
     * *
     * Parse le contenu XML envoyé au format String pour retourner une liste de
     * beans. Elle n'est pas dédoublonné, c'est le travail du mediateur de
     * collecte d'organiser le travail entre les différnts objets de traitmenet
     *
     * @param xmlIS
     * @return
     */
    @Override
    public List<Item> execute(InputStream xmlIS) throws IOException, IllegalArgumentException, FeedException {
        List<Item> listItems = new ArrayList<Item>();


        XmlReader reader = new XmlReader(xmlIS);
                
        SyndFeedInput feedInput = new SyndFeedInput();

        feedInput.setPreserveWireFeed(true);
        feedInput.setXmlHealerOn(true);

        SyndFeed feed = feedInput.build(reader);
//        String result = "";

        for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
            // Création d'un nouveau beans Item
            Item new_item = new Item();

            SyndEntry entry = (SyndEntry) i.next();

            if (entry.getTitle() != null) {
//                result += "Title : " + entry.getTitle() + "\n";
                new_item.setTitre(entry.getTitle());
            }

            if (entry.getLink() != null) {
//                result += "Link : " + entry.getLink() + "\n";
                new_item.setLink(entry.getLink());
            }

            if (entry.getDescription() != null) {
//                result += "Dexcription : " + entry.getDescription().getValue() + "\n";
                new_item.setDescription(entry.getDescription().getValue());
            }

            if (entry.getContents() != null) {
                int i2;
                for (i2 = 0; i2 < entry.getContents().size(); i2++) {
                    SyndContentImpl contenu = (SyndContentImpl) entry.getContents().get(i2);
//                    result += "Contenu (atom): " + contenu.getValue() + "\n";
                    new_item.setContenu(contenu.getValue());
                }
            }

// Si c'est un feed rss, captation du GUID
            if (entry.getWireEntry() instanceof com.sun.syndication.feed.rss.Item) {
                if (entry.getWireEntry() instanceof com.sun.syndication.feed.rss.Item) {
//                    import com.sun.syndication.feed.rss.Item;
                    com.sun.syndication.feed.rss.Item it;
                    it = (com.sun.syndication.feed.rss.Item) entry.getWireEntry();
                    if (it.getGuid() != null) {
//                    result += "GUID : " + s.getGuid().getValue();                      
                        new_item.setGuid(it.getGuid().getValue());
                    }
                }
            }

            // Gesion de la date
            if (entry.getPublishedDate() != null) {
                new_item.setDatePub(entry.getPublishedDate());
            }

            //Concat de toutes les catégories

            if (entry.getCategories() != null && entry.getCategories().size() > 0) {
                int j;
                String concat = "";
                for (j = 0; j < entry.getCategories().size(); j++) {
                    SyndCategoryImpl cat = (SyndCategoryImpl) entry.getCategories().get(j);
                    concat += cat.getName() + "; ";
                }
                concat = concat.substring(0, concat.length() - 2);
                new_item.setCategorie(concat);
            }

            // On inscrit la date de récupération
            Date datecourante = new Date();
            new_item.setDateRecup(datecourante);

//            result += "\n------------------------------------------\n";
//            System.out.println(result);

            listItems.add(new_item);
        }

        return listItems;
    }

    /**
     * *
     * Retourne une instance configurée par défault de ROME
     *
     * @return
     */
    public static RomeParse getDefaultInstance() {
        RomeParse parse = new RomeParse();

        return parse;
    }


    public String getDescription() {
        return description;
    }

    @Override
    public void testParse() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }


    public void run() {
  
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public  List<Item> call() throws Exception {
        return execute(xmlIS);
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}