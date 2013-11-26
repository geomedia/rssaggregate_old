/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import org.eclipse.persistence.annotations.Index;

/**
 *
 * @author clem
 */
@Table(uniqueConstraints =  {@UniqueConstraint(columnNames = {"hashContenu", "flux"})})
@Entity
public class DonneeBrute implements Serializable, ContentRSS{

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
    
    
    
    
}
