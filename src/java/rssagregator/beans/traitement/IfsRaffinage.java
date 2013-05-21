package rssagregator.beans.traitement;

import java.util.Vector;

public interface IfsRaffinage {



  /** 
   *  effectué le traitement sur la chaine de caractère envoyé.
   *  Renvoi la chaine de caractère traitée.
   */
  public String execution(String string);

}