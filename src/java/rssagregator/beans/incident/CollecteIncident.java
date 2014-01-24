package rssagregator.beans.incident;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;
import rssagregator.beans.Flux;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.tache.TacheRecupCallable;

/**
 * <p>Cet incident est généré par le service {@link ServiceCollecteur} à partir des résultats de la tâche {@link TacheRecupCallable}</p>
 * <p>Les erreurs de collecte sont due à une incapacité de la tache à récupérer le contenu (site indisponible, changement d'adresse...)</p>
 */
@Entity(name = "i_collecteincident")
@XmlRootElement
public class CollecteIncident extends AbstrIncident implements Serializable {

    public CollecteIncident() {
    }

    
    
    
    /**
     * Une erreur de collecte est forcement lié à un flux. Un incident ne
     * possède qu'un flux. Un flux peut posséder plusieurs incidents.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    protected Flux fluxLie;

    public Flux getFluxLie() {
        return fluxLie;
    }

    public void setFluxLie(Flux fluxLie) {
        this.fluxLie = fluxLie;
    }

    public String incidDesc(){
        return "Les incidents de collectes sont blablabla";
    }
    
    /***
     * Une erreur de collecte ne doit être notifiée que si elle a subit 3 répétition. Il faut en effet éviter de notifier dès le premier échec.
     * @return 
     */
    @Override
    public Boolean doitEtreNotifieParMail() {
        if(this.nombreTentativeEnEchec>2){
            return true;
        }
        else{
            return false;
        }
//        return super.doitEtreNotifieParMail(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "Incident de collecte FLux : "+fluxLie;
    }
    
    
    
    
}