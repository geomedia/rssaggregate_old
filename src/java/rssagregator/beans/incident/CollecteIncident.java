package rssagregator.beans.incident;

import java.io.Serializable;
import javax.mail.search.DateTerm;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import rssagregator.beans.Flux;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.tache.TacheRecupCallable;
import rssagregator.utils.ExceptionTool;

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
     * Une erreur de collecte ne doit être notifiée que si elle a subit 3 répétitions et que la durée soit > a 3 heures. Il faut en effet éviter de notifier dès le premier échec.
     * @return 
     */
    @Override
    public Boolean doitEtreNotifieParMail() {
        ExceptionTool.argumentNonNull(this.getDateDebut());
        
        if(this.nombreTentativeEnEchec>2){
            // Il faut aussi que l'évènement ait plus de 3 heure
            DateTime dtNow = new DateTime();
            DateTime dt = new DateTime(this.dateDebut);
            Duration dut = new Duration(dtNow, dt);
            if(dut.getStandardHours()>=3){
                System.out.println("Je doit être notifie");
                return true;
            }
            return false;

        }
        else{
            return false;
        }
    }

    @Override
    public String toString() {
        return "Incident de collecte FLux : "+fluxLie;
    }


    
    
    
    
}