package rssagregator.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
 * environnement...
 */
@Entity
public class FluxType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    @Column(name = "denomination")
    private String denomination;
    /**
     * Le type du flux (international, a la une etc...). Les types de flux sont
     * des beans. ils sont persisté dans la base de données
     *
     * @element-type Flux
     */
//    @OneToMany(mappedBy = "typeFlux", cascade = {CascadeType.MERGE})
//    @Transient

   @CascadeOnDelete
    @OneToMany(mappedBy = "typeFlux", cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
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

    public FluxType() {
        this.fluxLie = new ArrayList<Flux>();
    }
}