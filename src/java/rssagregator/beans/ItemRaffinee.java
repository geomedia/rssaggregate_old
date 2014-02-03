/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.eclipse.persistence.annotations.Index;
import org.joda.time.DateTime;
import rssagregator.utils.ExceptionTool;

/**
 * Entité permettant de stoquer sans doublon.
 *
 * @author clem
 */
@Entity
@Table(name = "itemraffinee")
@XmlRootElement
public class ItemRaffinee implements ContentRSS, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }
    @Index
    @Column(name = "titre", length = 1000)
    private String titre;
    /**
     * *
     * Description de l'item. L'élément est persisté dans la base de données. On stocke ici le contenu RSS de l'élément
     * description. Pour les flux ATOM, on a une déparation entre description et contenu
     */
    @Column(name = "description", columnDefinition = "text")
    private String description;
    /**
     * *
     * Correspond à l'élément contenu d'un flux ATOM. N'est pas présent dans les flux RSS. Ne sera que peu exploiter par
     * le projet géomédia car cette information ne permet pas une analyse comparative (car absente dans la majorité des
     * cas).
     */
    @Column(name = "contenu", columnDefinition = "text")
    private String contenu;
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
    /**
     * *
     * Stockage de l'élément guid de l'item
     */
    @Column(name = "guid", length = 1000)
    private String guid;
    /**
     * *
     * La catégorie. On a choisi de pas créer une nouvelle entitée pour les catégories. Toutes les catégories sont
     * concaténée dans ce champs
     */
    @Column(name = "categorie", length = 1500)
    private String categorie;
    /**
     * *
     * Le hash permettant d'identifier de manière unique l'item. Ce champs ne peut être null et doit être unique dans la
     * base de données.
     */
    @Index
    @Column(name = "hashContenu", unique = true, nullable = false)
    private String hashContenu;
    /**
     * Correspond à l'élément link d'un flux RSS.
     */
    @Column(name = "link", length = 2000)
    private String link;
    
    
    
    @OneToMany(mappedBy = "itemRaffinee")
    private List<Item> itemBrutes = new ArrayList<Item>();
    


    @Override
    public String getTitre() {
        return titre;
    }

    @Override
    public void setTitre(String titre) {
        this.titre = titre;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
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
    public String getGuid() {
        return guid;
    }

    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    @Override
    public String getHashContenu() {
        return hashContenu;
    }

    @Override
    public void setHashContenu(String hashContenu) {
        this.hashContenu = hashContenu;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }

    @XmlTransient
    public List<Item> getItemBrutes() {
        return itemBrutes;
    }

    public void setItemBrutes(List<Item> itemBrutes) {
        this.itemBrutes = itemBrutes;
    }

    /**
     * *
     * Ajoute une item (brute à l'item) après avoir vérifié que l'item n'étati pas déjà contenu. 
     *
     * @param item
     * @return true si un changement a été effectué (ajout de l'ite) sinon false. 
     */
    public synchronized boolean addItem(Item item) {
        ExceptionTool.argumentNonNull(item);
        if (itemBrutes.contains(item)) {
            itemBrutes.add(item);
            return true;
        }
        return false;
    }
    
    /***
     * L'item raffiné parcour l'ensemble des items qui lui sotn rataché et sélectionne le contenu le plus a jour. Cette item sert de base pour templir le contennu des champs de l'item raffiné
     */
    public void miseANiveauduCOntenu(){
       
        
        Item itemRetenu = null;
        if(itemBrutes!= null && !itemBrutes.isEmpty()){
            itemRetenu = itemBrutes.get(0);
        }
        
        DateTime dt = null;
        
        for (Iterator<Item> it = itemBrutes.iterator(); it.hasNext();) {
            Item item = it.next();
            if(item.getDatePub() != null){
                DateTime dtCurrent = new DateTime(item.getDatePub());
                

                DateTime dtitemPre = new DateTime(itemRetenu.getDatePub());
                DateTime dtItemCourrante = new DateTime(item.getDatePub());
                
                // Si l'item courrante a été publié après l'item précédente
                if(dtItemCourrante.isBefore(dtitemPre)){
                    
                    
                }
            }
        }
        
    }
    
    
}
