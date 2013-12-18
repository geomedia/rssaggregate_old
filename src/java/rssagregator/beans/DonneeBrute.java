/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import com.fasterxml.jackson.annotation.JsonFilter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import org.eclipse.persistence.annotations.Index;

/**
 *
 * @author clem
 */
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"hashContenu", "flux"})})
@JsonFilter("serialisePourUtilisateur")
@Entity
public class DonneeBrute implements Serializable, ContentRSS {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    @Index
    @Column(name = "titre", length = 1000)
    private String titre;
    @Column(name = "description", columnDefinition = "text")
    private String description;
    /**
     * *
     * La date de publication de récupére dans le XML
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "datePub")
    private Date datePub;
    /**
     * *
     * La date de récupération de l'article. Cette information est générer par l'aggrégateur lorsqu'il enregistre une
     * nouvelle item.
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "dateRecup")
    private Date dateRecup;
    @Column(name = "link", length = 2000)
    private String link;
    @Column(name = "guid", length = 1000)
    private String guid;
    @Column(name = "contenu", columnDefinition = "text")
    private String contenu;
    @Column(name = "categorie", length = 1500)
    private String categorie;
    /**
     * *
     * Le hash permettant d'identifier de manière unique l'item. Ce champs ne peut être null et doit être unique dans la
     * base de données.
     */
    @Index
    @Column(name = "hashContenu", nullable = false)
    private String hashContenu;
//    @OneToOne(optional = false)
    @ManyToOne
    private Flux flux; // bidirectionnelle le flux a toute les cascades
    /**
     * Dernière modification de l'entite. Permet l'Optimitic Lock
     */
    @Version
    Timestamp modified;
    
    
    @ManyToOne
    Item item;

    /**
     * *
     * @see #modified
     * @return
     */
    public Timestamp getModified() {
        return modified;
    }

    /**
     * *
     * @see #modified
     * @param modified
     */
    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Flux getFlux() {
        return flux;
    }

    public void setFlux(Flux flux) {
        this.flux = flux;
    }

    @Override
    public String getTitre() {
        return titre;
    }

    @Override
    public void setTitre(String titre) {
        this.titre = titre;
    }

    @Override
    public Date getDatePub() {
        return datePub;
    }

    @Override
    public void setDatePub(Date datePub) {
        this.datePub = datePub;
    }

    @Override
    public Date getDateRecup() {
        return dateRecup;
    }

    @Override
    public void setDateRecup(Date dateRecup) {
        this.dateRecup = dateRecup;
    }

    @Override
    public String getHashContenu() {
        return hashContenu;
    }

    @Override
    public void setHashContenu(String hashContenu) {
        this.hashContenu = hashContenu;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    @Override
    public String toString() {
        return "DonneeBrute{" + "titre=" + titre + ", dateRecup=" + dateRecup + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.ID != null ? this.ID.hashCode() : 0);
        hash = 29 * hash + (this.hashContenu != null ? this.hashContenu.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DonneeBrute other = (DonneeBrute) obj;
        if (this.ID != other.ID && (this.ID == null || !this.ID.equals(other.ID))) {
            return false;
        }
        if ((this.hashContenu == null) ? (other.hashContenu != null) : !this.hashContenu.equals(other.hashContenu)) {
            return false;
        }
        return true;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    
    
    
    
    
}
