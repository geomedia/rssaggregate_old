package rssagregator.beans.incident;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import rssagregator.beans.Flux;

/** 
 *  Les erreurs de captation sont consigné dans des objets redéfinissant cette classe abstraite. Il peut s'agir d'erreur de parsage, d'erreur http (404, site indisponible etc...)
 */
@Entity
public abstract class AbstrFluxIncident extends AbstrIncident {

  /** 
   *  Un objet flux peut posséder différents incidents. Un incident ne possède qu'un flux. 
   */
  
 
//  private Flux flux;


}