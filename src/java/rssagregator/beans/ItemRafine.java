package rssagregator.beans;

import java.util.Date;
// TODO : La liste des champs n'est pas complete. On a supprimer l'héritage entre Item et ItemRaffine

/***
 * <strong>N'EST PLUS UTILISÉ</strong> Le raffinage des items a été sorti de l'aggrégateur.
 * @author clem
 */
@Deprecated
public class ItemRafine {
/***
 * Titre de l'item. Element persisté dans la base de données
 */
  public String titre;

  /***
   * Description de l'item. L'élément est persisté dans la base de données. On stocke ici le contenu RSS de l'élément description. Pour les flux ATOM, on a une déparation entre description et contenu
   */
  public String description;
  
  /***
   * La date de publication de récupére dans le XML
   */
  public Date datePub;
  
  
  /***
   * La date de récupération de l'article
   */
  public Date dateRecup;
  
  /***
   * Stockage de l'élément guid de l'item
   */
  public String guid;
  
  /***
   * La catégorie. On a choisi dene pas créer une nouvelle entitée pour les catégories. A CONFIRMER
   */
  public String categorie;
  
  

  /***
   * L'item (donnée brute) rattaché au flux  raffiné.
   */
  public Item myItem;

  public void ItemRafine() {
  }

}