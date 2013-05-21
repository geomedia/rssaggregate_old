package rssagregator.beans.traitement;

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
/**
 * test
 */
public class RomeParse extends AbstrParseur implements IfsObjetDeTraitement, IfsParseur {
    /* {author=clem}*/


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
//        int z =0;
//        char c='a';
//        while((c=(char) reader.read())!=-1){
//            System.out.println(""+c);
//        }
       
        

        SyndFeedInput feedInput = new SyndFeedInput();

        feedInput.setPreserveWireFeed(true);
        feedInput.setXmlHealerOn(true);

        SyndFeed feed = feedInput.build(reader);
        String result = "";

        for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
            // Création d'un nouveau beans Item
            Item new_item = new Item();
            SyndEntry entry = (SyndEntry) i.next();

            if (entry.getTitle() != null) {
                result += "Title : " + entry.getTitle() + "\n";
                new_item.setTitre(entry.getTitle());
            }

            if (entry.getLink() != null) {
                result += "Link : " + entry.getLink() + "\n";
                new_item.setLink(entry.getLink());
            }

            if (entry.getDescription() != null) {
                result += "Dexcription : " + entry.getDescription().getValue() + "\n";
                new_item.setDescription(entry.getDescription().getValue());
            }

            if (entry.getContents() != null) {
                int i2;
                for (i2 = 0; i2 < entry.getContents().size(); i2++) {
                    SyndContentImpl contenu = (SyndContentImpl) entry.getContents().get(i2);
                    result += "Contenu (atom): " + contenu.getValue() + "\n";
                    new_item.setContenu(contenu.getValue());
                }
            }

// Si c'est un feed rss, captation du GUID
            if (entry.getWireEntry() instanceof com.sun.syndication.feed.rss.Item) {
                com.sun.syndication.feed.rss.Item s;
                s = (com.sun.syndication.feed.rss.Item) entry.getWireEntry();
                if(s!= null && s.getGuid()!=null){
                                    result += "GUID : " + s.getGuid().getValue();
                new_item.setGuid(s.getGuid().getValue());
                }

            }

            result += "\n------------------------------------------\n";
//            System.out.println(result);

            listItems.add(new_item);
        }
  
        
        // Calcul des hash
        int i;
        for(i=0; i< listItems.size(); i++){
            try {
                calculHash(listItems);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(RomeParse.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void testParse() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}