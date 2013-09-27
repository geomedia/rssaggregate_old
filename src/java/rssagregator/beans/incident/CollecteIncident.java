package rssagregator.beans.incident;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import rssagregator.beans.Flux;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.TacheRecupCallable;

/**
 * <p>Cet incident est généré par le service {@link ServiceCollecteur} à partir des résultats de la tâche {@link TacheRecupCallable}</p>
 * <p>Les erreurs de collecte sont due à une incapacité de la tache à récupérer le contenu (site indisponible, changement d'adresse...)</p>
 */
@Entity(name = "i_collecteincident")
public class CollecteIncident extends AbstrIncident implements Serializable {

    /**
     * Une erreur de collecte est forcement lié à un flux. Un incident ne
     * possède qu'un flux. Un flux peut posséder plusieurs incidents.
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Flux fluxLie;

    public Flux getFluxLie() {
        return fluxLie;
    }

    public void setFluxLie(Flux fluxLie) {
        this.fluxLie = fluxLie;
    }

    
    /***
     * Une erreur de collecte ne doit être notifiée que si elle a subit 2 répétition. Il faut en effet éviter de notifier dès le premier échec.
     * @return 
     */
    @Override
    public Boolean doitEtreNotifieParMail() {
        if(this.nombreTentativeEnEchec>1){
            return true;
        }
        else{
            return false;
        }
//        return super.doitEtreNotifieParMail(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}