
import com.sun.syndication.feed.opml.Opml;
import com.sun.syndication.feed.opml.Outline;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedInput;
import com.sun.syndication.io.impl.OPML10Generator;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author clem
 */
public class InformaTest {
    public static void main(String[] args) {
        try {
            //read OPML 

            WireFeedInput input = new WireFeedInput();

//            Opml feed = (Opml) input.build( new File("C:\\subscriptions.xml") );
            Opml feed = new Opml();
                        //add new outline         

            List<Outline> outlines = (List<Outline>) feed.getOutlines();

            Outline myOutline = new Outline("RTE GAA", new
            URL("http://www.rte.ie/rss/gaa.xml"), new
            URL("http://www.rte.ie/rss/gaa.xml"));

            outlines.add(myOutline);

            feed.setOutlines(outlines);
            
            OPML10Generator generator = new OPML10Generator();
            Document doc = generator.generate(feed);
            
            XMLOutputter xmlo = new XMLOutputter();
            
            System.out.println(xmlo.outputString(doc));
            
    
            



            
            
//            objWireFeedOutput.output(feed, new File("C:\\subscriptions.xml"));
        }  catch (IOException ex) {
            Logger.getLogger(InformaTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(InformaTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FeedException ex) {
            Logger.getLogger(InformaTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
