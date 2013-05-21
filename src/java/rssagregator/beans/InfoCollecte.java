package rssagregator.beans;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 * Les objet de type info collecte permette de stoquer les dates d'entrée et
 * d'arrêt de captation d'un flux. Ces information n'ont pas pu être stocké dans
 * la table Flux. En effet, la captation peut être interrompu (manuellement)
 * puis reprise. On comprends que dans ce cas un flux peut avoir plusieurs objet
 * de type InfoCollecte
 */
@Entity
public class InfoCollecte implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    /**
     * Date à laquelle la captation a été démmarrée
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateEntree;
    /**
     * Date d'arrêt de la captation
     */
    private Integer dateSortie;
    /**
     * Information saisie manuellement par l'équipe d'administration du
     * logicielle. Il s'agit d'une synthèse sur la captation de ce flux
     */
    private String note;
    /**
     * *
     * Les informations de collecte noté par les administrateurs. Il s'agit de
     * champs de commentaire datés.
     */
    @ManyToOne
    private Flux flux;

    /**
     * Le constructeur
     */
    public void InfoCollecte() {
    }

    public Date getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(Date dateEntree) {
        this.dateEntree = dateEntree;
    }

    public Integer getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(Integer dateSortie) {
        this.dateSortie = dateSortie;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Flux getFlux() {
        return flux;
    }

    public void setFlux(Flux flux) {
        this.flux = flux;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }
}