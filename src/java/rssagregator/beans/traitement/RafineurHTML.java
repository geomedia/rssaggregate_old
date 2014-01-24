package rssagregator.beans.traitement;

import java.util.Vector;
import org.jsoup.Jsoup;
import rssagregator.beans.ContentRSS;
import rssagregator.beans.Item;
import rssagregator.beans.ItemRaffinee;

public class RafineurHTML extends RaffineurImpl {

    private Boolean removeHtmlEntities;
    public Boolean removeHtmlCode;
    public Vector myMediatorCollecteAction;

    public String execution(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    @Override
    /***
     * Raffine un contenu RSS en supprimant les balise HTML de la description. 
     */
    public void raffinerContenu(ContentRSS i) {
        if (i.getDescription() != null && !i.getDescription().isEmpty()) {
            String txt = Jsoup.parse(i.getDescription()).text();

            i.setDescription(txt);

            i.setDescription(txt);

        }
    }
    
    
    /***
     * Met a jour le contenu de l'item raffine en fonction des items liées a l'item raffinée
     * @param itemRaffinee 
     */
    public void raffine(ItemRaffinee itemRaffinee){
        
        // Il faut déterminer l'item le plus intéressante
        // --> Possib de garder l'item la plus récente.
        //--> Concerver celle qui possède le plus de contenu.
        //
        
        
    }
    
    
}