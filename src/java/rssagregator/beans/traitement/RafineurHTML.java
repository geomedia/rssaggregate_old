package rssagregator.beans.traitement;

import java.util.Vector;
import org.jsoup.Jsoup;
import rssagregator.beans.Item;

public class RafineurHTML extends RaffineurImpl {

    private Boolean removeHtmlEntities;
    public Boolean removeHtmlCode;
    public Vector myMediatorCollecteAction;

    public String execution(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void raffiner(Item i) {


        if (i.getDescription() != null && !i.getDescription().isEmpty()) {
            String txt = Jsoup.parse(i.getDescription()).text();

            i.setDescription(txt);


            i.setDescription(txt);

        }

    }
}