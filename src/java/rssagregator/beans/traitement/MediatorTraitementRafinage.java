package rssagregator.beans.traitement;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;
import rssagregator.beans.Item;

public class MediatorTraitementRafinage {

  public String nomRafinage;

  /** 
   *  permet de préciser sur quelles champs portement les traitements. On peut ainsi ne toucher que le titre ou simplement la description. Pour la persistance pouquoi ne pas simplement sérialiser déserialiser cette liste dans un champs de la based de données. On économise une table. 
   */
  public List<Field> beansItemFields;

    /**
   * 
   * @element-type IfsRaffinage
   */
  public Vector  myIfsRaffinage;
    /**
   * 
   * @element-type MediatorCollecteAction
   */
  public Vector  myMediatorCollecteAction;

  /** 
   *  Parcours les champs de l'item. et applique les traitements d'un ou plusieurs rafineurs. Les résultats sont stoquées dans l'item rafinée
   */
  public void execution(Item i) {
      
  }

}