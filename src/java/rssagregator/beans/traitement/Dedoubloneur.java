package rssagregator.beans.traitement;

public class Dedoubloneur extends AbstrDedoublonneur implements IfsObjetDeTraitement {

    private static String description ="le dédoublonneur permet .....";

    @Override
    public String getDescription() {
        return description;
    }
    
}