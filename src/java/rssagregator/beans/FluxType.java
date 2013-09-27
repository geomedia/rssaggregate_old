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
 * <p>Les types de flux sont associé aux flux. Un type de flux corresponds aux
 * catégorie génériques des journaux : internationnal, politique, a la une,
 * environnement... </p>
 * <p>Les types flux implémentents l'interface {@link BeanSynchronise} car leur changement d'état doivent être répercuté sur les serveur esclave.</p>
 */

@Entity
@Cacheable(value = false)
public class FluxType implements Serializable, BeanSynchronise {

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
    
        
    /***
     * Les flux liés au TypeFlux
     */
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
    
    
    

    /***
     * Constructeur par défault. Initialise les arrayList
     */
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

    /***
     * Retourne la dénomination du flux ou "??" si cette dénomination n'existe pas.
     * @return chaine de caractère
     */
    @Override
    public String toString() {
        if (this.denomination != null && !this.denomination.isEmpty()) {
            return this.denomination;
        }
        else{
            return "??";
        }
    }

    /***
     * Retourne true car toute modification d'un {@link FluxType} doit systématiquement être répercuté sur les serveur esclaves.
     * @return 
     */
    @Override
    public Boolean synchroImperative() {
        return true;
    }
}