package rssagregator.beans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.poi.util.Beta;
import org.eclipse.persistence.annotations.Index;
import rssagregator.beans.exception.IncompleteBeanExeption;
import rssagregator.services.ServiceSynchro;
import rssagregator.utils.ExceptionTool;

/**
 * *
 * <p>Entitée primordiale pour le projet GEOMEDIA. Une item corresponds à la synthèse d'un article disponible dans un
 * flux RSS. Un {@link Flux} peut posséder plusieurs items. Une même item peut de même posséder plusieurs Flux. En
 * effet, il est courrant qu'un flux "A la Une" diffuse des items similaire au flux "internationale", une même
 * information peut ainsi être trouvé dans des flux différent d'où cette relation de N à N</p>
 *
 * @author clem
 * @version 0.1
 */
@Entity
@Table(name = "item")
//@Cacheable(value = true)
//@Cache(type = CacheType.CACHE, coordinationType = CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES, isolation = CacheIsolationType.SHARED, shared = true)
@XmlRootElement
public class Item extends Bean implements Serializable, Comparable<Item>, ContentRSS {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    /**
     * *
     * Titre de l'item. Element persisté dans la base de données
     */
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
    /**
     * Cette variable n'est pas encore implémenté.
     * <ul>
     * <li>0 = nouveau pas encore de sync</li>
     * <li>1 = synch effectué </li>
     * <li>2 = item sur le maitre récupéré du serv esclave</li>
     * <li>3 : L'item ne doit pas être synchronisée sur le serveur maitre. Ce cas provient lors de la synchrnonisation
     * de la modification d'un comportement. Le service {@link ServiceSynchro} désactive alors les items qu'il aurait pu
     * récolter durant le laps de temps entre l'émission de la modification du comportement et la répercution de ce
     * comportement sur l'esclave.</li>
     * </ul>
     */
    @Beta
    private Byte syncStatut;
    /**
     * *
     * Les flux auxquelles appartiennent l'item.
     */
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Flux.class)
    private List<Flux> listFlux = new LinkedList<Flux>();
    @ManyToOne
    private ItemRaffinee itemRaffinee;
    
    
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "")
//    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
//    private List<DonneeBrute> donneeBrutes = new ArrayList<DonneeBrute>();
//
//    @XmlTransient
//    public List<DonneeBrute> getDonneeBrutes() {
//        return donneeBrutes;
//    }
//
//    public void setDonneeBrutes(List<DonneeBrute> donneeBrutes) {
//        this.donneeBrutes = donneeBrutes;
//    }

    /**
     * *
     * Constructeur vide. Initialise la liste des flux avec une {@link LinkedList}
     */
    public Item() {
    }

    /**
     * *
     * @see #titre
     * @return
     */
    @Override
    public String getTitre() {
        return titre;
    }

    /**
     * *
     * @see #titre
     * @return
     */
    @Override
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /**
     * *
     * @see #description
     * @return
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * *
     * @see #description
     * @return
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * *
     * @see #datePub
     * @return
     */
    @Override
    public Date getDatePub() {
        return datePub;
    }

    /**
     * *
     * @see #datePub
     * @return
     */
    @Override
    public void setDatePub(Date datePub) {
        this.datePub = datePub;
    }

    /**
     * *
     * @see #dateRecup
     * @return
     */
    @Override
    public Date getDateRecup() {
        return dateRecup;
    }

    /**
     * *
     * @see #dateRecup
     * @return
     */
    @Override
    public void setDateRecup(Date dateRecup) {
        this.dateRecup = dateRecup;
    }

    /**
     * *
     * @see #guid
     * @return
     */
    @Override
    public String getGuid() {
        return guid;
    }

    /**
     * *
     * @see #guid
     * @return
     */
    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * *
     * @see #categorie
     * @return
     */
    public String getCategorie() {
        return categorie;
    }

    /**
     * *
     * @see #categorie
     * @return
     */
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    /**
     * *
     * @see hashContenu
     * @return
     */
    @Override
    public String getHashContenu() {
        return hashContenu;
    }

    /**
     * @see hashContenu
     * @return
     */
    @Override
    public void setHashContenu(String hashContenu) {
        this.hashContenu = hashContenu;
    }

    /**
     * *
     * @see syncStatut
     * @return
     */
    public Byte getSyncStatut() {
        return syncStatut;
    }

    /**
     * *
     * @see syncStatut
     * @return
     */
    public void setSyncStatut(Byte syncStatut) {
        this.syncStatut = syncStatut;
    }

    /**
     * *
     * @see #listFlux
     * @return
     */
    @XmlTransient
    public List<Flux> getListFlux() {
        return listFlux;
    }

    /**
     * *
     * @see #listFlux
     * @return
     */
    public void setListFlux(List<Flux> listFlux) {
        this.listFlux = listFlux;
    }

    /**
     * *
     * @see #link
     * @param link
     */
    @Override
    public String getLink() {
        return link;
    }

    /**
     * *
     * @see #link
     * @param link
     */
    @Override
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * *
     * @see #contenu
     * @return
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * *
     * @see #contenu
     * @return
     */
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    /***
     * @see #ID
     * @return 
     */
    @Override
    public Long getID() {
        return ID;
    }

    /***
     * @see #ID
     * @return 
     */
    @Override
    public void setID(Long ID) {
        this.ID = ID;
    }
    
        /***
     * Dernière modification de l'entite. Permet l'Optimitic Lock
     */
        @Version
    Timestamp modified;

        /***
         * @see #modified
         * @return 
         */
    public Timestamp getModified() {
        return modified;
    }

    /***
     * @see #modified
     * @param modified 
     */
    public void setModified(Timestamp modified) {
        this.modified = modified;
    }
    
    

    /**
     * *
     * la comparaison est basée sur les date de publication. La redéfinition de cette méthode permet de classer les
     * items par date en utilisant un objet de type {@link Comparator} qui permet de trier des List.
     *
     * @param o Item à comparer
     * @return <ul><li>1 : la date de l'item envoyé est supérieur à l'item courante</li><li>0 : Les date de récupération
     * sont similaire</li><li>La date de l'item envoyé est supérieur à l'item courante</li></ul>
     */
    @Override
    public int compareTo(Item o) {
        if (this.getDateRecup().before(o.getDateRecup())) {
            return 1;
        } else if (this.getDateRecup().equals(o.getDateRecup())) {
            return 0;
        } else {
            return -1;
        }
    }

    /***
     * Copi les données de l'item dans une Entitée de type @{@link  DonneeBrute}. Cette nouvelle donnée brute est ensuite ajouté à l'item
     * 
     * @param f
     * @throws IncompleteBeanExeption 
     */
//    public void genererDonneesBrutes(Flux f) throws IncompleteBeanExeption {
//
//        if (f == null) {
//            throw new NullPointerException("impossible de verser avec un flux null");
//        }
//
//        if (f.getID() == null) {
//            throw new IncompleteBeanExeption("Le flux envoyé n'a pas d'id");
//        }
//
//
//        List<DonneeBrute> donneesbrutes = this.donneeBrutes;
//        boolean absente = true;
//        for (int i = 0; i < donneesbrutes.size(); i++) {
//            DonneeBrute donneeBrute = donneesbrutes.get(i);
//
//            if (donneeBrute.getFlux().getID().equals(f.getID()) && donneeBrute.getHashContenu().equals(this.hashContenu)) {
//                absente = false;
//            }
//
//        }
//
//        if (absente) {
//            DonneeBrute newDonneeBrute = new DonneeBrute();
//            newDonneeBrute.setDescription(description);
//            newDonneeBrute.setLink(link);
//            newDonneeBrute.setGuid(guid);
//            newDonneeBrute.setFlux(f);
//            newDonneeBrute.setHashContenu(hashContenu);
//            newDonneeBrute.setTitre(titre);
//            newDonneeBrute.setDatePub(datePub);
//            newDonneeBrute.setDateRecup(dateRecup);
//            newDonneeBrute.setHashContenu(hashContenu);
//            newDonneeBrute.setCategorie(categorie);
//            newDonneeBrute.setContenu(contenu);
//            newDonneeBrute.setItem(this);
//
////            newDonneeBrute.setItem(this);
//            this.donneeBrutes.add(newDonneeBrute);
//        }
//    }

    /**
     * *
     * Les donnée brutes de l'item envoyée en argument sont ajouté si nécessaire (comparaison des hash) aux données brutes de l'item courante
     *
     * @param i l'item pour laquelle on doit récupérer les données brutes
     * @return true si un versement qqchose a pu être versé. sinon false.
     */
//    public Boolean verserLesDonneeBruteAutreItem(Item i) {
//
//
//        boolean versement = false;
//        List<DonneeBrute> listDonneebruteAutreItem = i.donneeBrutes;
//        
//        
//        
//      versement =  this.donneeBrutes.addAll(i.donneeBrutes);
//      
//        for (int j = 0; j < listDonneebruteAutreItem.size(); j++) {
//            DonneeBrute donneeBrute = listDonneebruteAutreItem.get(j);
//            donneeBrute.setItem(this);
//            
//        }
//      
//
////        for (int j = 0; j < listDonneebruteAutreItem.size(); j++) {
////            DonneeBrute donneeBrute = listDonneebruteAutreItem.get(j);
////        }
//      
//      
//        
////        for (int j = 0; j < listDonneebruteAutreItem.size(); j++) {
////            DonneeBrute donneeBruteAutre = listDonneebruteAutreItem.get(j);
////            List<DonneeBrute> listdonneBrutThis = this.donneeBrutes;
////            boolean trouve = false;
////            
////            for (int k = 0; k < listdonneBrutThis.size(); k++) {
////                DonneeBrute donneeBruteThis = listdonneBrutThis.get(k);
////                if (donneeBruteAutre.getFlux().getID().equals(donneeBruteThis.getFlux().getID())) {
////                    trouve = true;
////                }
////            }
////            if (!trouve) {
////                this.donneeBrutes.add(donneeBruteAutre);
////                versement = true;
//////                System.out.println("===========VERSEMENT==============");
//////                System.out.println("NOMBRE DONNEE BRUT POUR ITEM : " + this.donneeBrutes.size());
//////                for (int k = 0; k < this.donneeBrutes.size(); k++) {
//////                    DonneeBrute donneeBrute = this.donneeBrutes.get(k);
////////                    System.out.println("DESC : " + donneeBrute.getDescription());
//////                    
//////                }
////
////            }
////        }
//
//
//        if (versement) {
//            return true; // Si des données brutes on été versées
//        } else {
//            return false; // si rien n'a été versé.
//        }
//
//    }

    /**
     * *
     * Ajoute un flux si nécessaire à la liste de l'item
     *
     * @param f
     */
    public void addFlux(Flux f) throws IncompleteBeanExeption {

        if (f == null) {
            throw new NullPointerException("Impossible d'ajouter un flux null");
        }
        if (f.getID() == null) {
            throw new IncompleteBeanExeption("Le flux n'a pas d'id");
        }

        List<Flux> flThis = this.getListFlux();
        boolean present = false;
        for (int i = 0; i < flThis.size(); i++) {
            Flux flux = flThis.get(i);
            if (flux.getID().equals(f.getID())) {
                present = true;
            }
        }
        if (!present) {
            listFlux.add(f);
        }

    }
    
    /***
     * Détermine si l'item appartien au flux envoyé en argument par comparaison des ID FLUX
     * @param f
     * @return 
     */
    public boolean appartientAuFlux(Flux f) throws NullPointerException, IllegalAccessException{
        ExceptionTool.argumentNonNull(f);
        ExceptionTool.checkNonNullField(f, "ID");
        
        for (int i = 0; i < listFlux.size(); i++) {
            Flux flux = listFlux.get(i);
            if(f.getID().equals(flux.getID())){
                return true;
            }
            
        }
        return false;
    }

    public ItemRaffinee getItemRaffinee() {
        return itemRaffinee;
    }

    public void setItemRaffinee(ItemRaffinee itemRaffinee) {
        this.itemRaffinee = itemRaffinee;
    }

    @Override
    public String getReadURL() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
}