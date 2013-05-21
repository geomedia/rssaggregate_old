package rssagregator.beans.traitement;

import rssagregator.beans.Item;

/** 
 *  Tous les objets de traitement implementent cette interface. Elle force à avoir les méthode permettant d'executer et d'obtenir un traitement par défault
 */
public interface IfsObjetDeTraitement {



//  /** 
//   *  Execute le traitement sur un BeansItem. 
//   */
//  public void execute(Item beansItem);
    
    
    /**
   * 
   * Toutes les classes objet de traitement doivent être décrite. Ces descriptions sont destinée à enrichir l'interface utilisateur en décrivant l'action de l'objet.
   */

  public String getDescription();
}