
import rssagregator.beans.Flux;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author clem
 */
public class POSTit {
    //TODO : Utiliser postgre à la plac de MySQL
    //TODO : Qui porte la relation item ou flux.; Faire le point sur le lazy load. il n'est pas utile de charger toute les items d'un flux pour une simple mise à jour.
    //TODO : faire le point sur la cascading et autre dans les relation many to many
    
    public static void main(String[] args) {
        
        Double nb1 = 46.0;
        Double nb2 = 20.0;
        
        Double result = Math.ceil(nb1/nb2);
        System.out.println("result double : " + result);
        
        Integer intval = result.intValue();
        System.out.println("en int : " + intval);
        
        System.out.println("ARR : " + Math.ceil((nb1/nb2)));
    }
    
}

