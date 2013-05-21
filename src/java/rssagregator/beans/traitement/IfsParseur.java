package rssagregator.beans.traitement;

import com.sun.syndication.io.FeedException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.persistence.Entity;
import rssagregator.beans.Item;



public interface IfsParseur {

  /** 
   *  On envoies un flux. il parse et renvoie une 
   */
    
  public void testParse();
  
   public List<Item> execute(InputStream xml) throws IOException, IllegalArgumentException, FeedException;


}