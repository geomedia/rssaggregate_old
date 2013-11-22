/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import org.eclipse.persistence.annotations.Index;

/**
 *
 * @author clem
 */
@Entity
public class DonneeBrute {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
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
    /**
     * *
     * Le hash permettant d'identifier de manière unique l'item. Ce champs ne peut être null et doit être unique dans la
     * base de données.
     */
    @Index
    @Column(name = "hashContenu", unique = true, nullable = false)
    private String hashContenu;
////    @ManyToOne(optional = false)
//    @ManyToOne
//    private Item item; // Bidirectionnelle l'item a toute les cascades 
    @OneToOne(optional = false)
    private Flux flux; // bidirectionnelle le flux a toute les cascades

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

//    public Item getItem() {
//        return item;
//    }
//
//    public void setItem(Item item) {
//        this.item = item;
//    }
    public Flux getFlux() {
        return flux;
    }

    public void setFlux(Flux flux) {
        this.flux = flux;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Date getDatePub() {
        return datePub;
    }

    public void setDatePub(Date datePub) {
        this.datePub = datePub;
    }

    public Date getDateRecup() {
        return dateRecup;
    }

    public void setDateRecup(Date dateRecup) {
        this.dateRecup = dateRecup;
    }

    public String getHashContenu() {
        return hashContenu;
    }

    public void setHashContenu(String hashContenu) {
        this.hashContenu = hashContenu;
    }
}
