package rssagregator.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.eclipse.persistence.annotations.CascadeOnDelete;

/**
 * Les types de flux sont associé aux flux. Un type de flux corresponds aux
 * catégorie génériques des journaux : internationnal, politique, a la une,
 * environnement... Les type de flux doivent être enregistré auprès du service
 * JMS. Pour cela un utilise le pattern observer. Le beans est ainsi un
 * observable qui a pour observer le service JMS
 */
@Entity
@Cacheable(value = false)
public class FluxType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    
    /***
     * Le nom du type de flux. Exemple international, à la une...
     */
    @Column(name = "denomination")
    private String denomination;
    
     
    /***
     * Une description informative de ce qu'est ce type de flux
     */
    @Column(name = "description")
    private String description;
    
    
    /**
     * Le type du flux (international, a la une etc...). Les types de flux sont
     * des beans. ils sont persisté dans la base de données
     *
     * @element-type Flux
     */
//    @OneToMany(mappedBy = "typeFlux", cascade = {CascadeType.MERGE})
//    @Transient
    @CascadeOnDelete
    @OneToMany(mappedBy = "typeFlux", cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    private List<Flux> fluxLie;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public List<Flux> getFluxLie() {
        return fluxLie;
    }

    public void setFluxLie(List<Flux> fluxLie) {
        this.fluxLie = fluxLie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
    

    public FluxType() {
        this.fluxLie = new ArrayList<Flux>();
    }

//    @Override
    /**
     * *
     * Ajoute le service JMS comme observateur au beans /!\ La diffusion des changement est maintenant confié aux DAO.
     */
//    public void enregistrerAupresdesService() {
//        this.addObserver(ServiceSynchro.getInstance());
//    }

    @Override
    public String toString() {
        if (this.denomination != null && !this.denomination.isEmpty()) {
            return this.denomination;
        }
        else{
            return "??";
        }
    }
}