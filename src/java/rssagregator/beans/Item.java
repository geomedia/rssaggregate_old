package rssagregator.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.apache.poi.util.Beta;
import org.eclipse.persistence.annotations.Index;
import rssagregator.beans.exception.IncompleteBeanExeption;
import rssagregator.services.ServiceSynchro;

/**
 * *
 * <p>Entitée primordiale pour le projet GEOMEDIA. Une item corresponds à la synthèse d'un article disponible dans un
 * flux RSS. Un {@link Flux} peut posséder plusieurs items. Une même item peut de même posséder plusieurs Flux. En
 * effet, il est courrant qu'un flux "A la Une" diffuse des items similaire au flux "internationale", une même
 * information peut ainsi être trouvé dans des flux différent d'où cette relation de N à N<p>
 *
 * @author clem
 * @version 0.1
 */
@Entity
@Table(name = "item")
//@Cacheable(value = true)
//@Cache(type = CacheType.CACHE, coordinationType = CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES, isolation = CacheIsolationType.SHARED, shared = true)
public class Item implements Serializable, Comparable<Item>, ContentRSS {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
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
    private Integer syncStatut;
//    /**
//     * Lorsque la levée du flux est en echec, ce champs est à true. Ce champs permet de savoir si un flux est
//     * nouvellement en erreur. Si le champs passe de true à false, il faut clore les incidents en cours.
//     */
//    private Boolean erreurDerniereLevee;
    /**
     * Booleen permettant de savoir si l'items est nouvelle ou non dans le flux. Il est obtenu par l'objet de
     * dédoublonnage. Lorsque l'admin test un flux. Il faut lui présenter d'une couleur différente les flux capté et les
     * flux déjà présent dans la base. C'est à cela que sert ce boollen
     */
    @Deprecated
    private Boolean isNew;
    /**
     * *
     * Les flux auxquelles appartiennent l'item.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.MERGE}, targetEntity = Flux.class)
    private List<Flux> listFlux;
    
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<DonneeBrute> donneeBrutes = new ArrayList<DonneeBrute>();

    public List<DonneeBrute> getDonneeBrutes() {
        return donneeBrutes;
    }

    public void setDonneeBrutes(List<DonneeBrute> donneeBrutes) {
        this.donneeBrutes = donneeBrutes;
    }
    
    
    

//    @Transient
//    private Boolean nonPresentDansBDD;
//
//    public Boolean getNonPresentDansBDD() {
//        
//        return nonPresentDansBDD;
//    }
//
//    public void setNonPresentDansBDD(Boolean nonPresentDansBDD) {
//        this.nonPresentDansBDD = nonPresentDansBDD;
//    }
    /**
     * *
     * Constructeur vide. Initialise la liste des flux avec une {@link LinkedList}
     */
    public Item() {
        this.listFlux = new LinkedList<Flux>();
    }

    /**
     * *
     * Pour générer la clé unique, on concatene les champs titre, daterecup, description lien et ont hash en md5
     */
//    public void genererCleUnique() {
//    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getHashContenu() {
        return hashContenu;
    }

    public void setHashContenu(String hashContenu) {
        this.hashContenu = hashContenu;
    }

    public Integer getSyncStatut() {
        return syncStatut;
    }

    public void setSyncStatut(Integer syncStatut) {
        this.syncStatut = syncStatut;
    }

//    public Boolean getErreurDerniereLevee() {
//        return erreurDerniereLevee;
//    }
//
//    public void setErreurDerniereLevee(Boolean erreurDerniereLevee) {
//        this.erreurDerniereLevee = erreurDerniereLevee;
//    }
    /**
     * *
     * Pour tester si c'est nouveau on va utiliser l'ID
     *
     * @return
     * @deprecated
     */
    @Deprecated
    public Boolean getIsNew() {
        return isNew;
    }

    /**
     * *
     *     * Pour tester si c'est nouveau on va utiliser l'ID
     *
     * @param isNew
     */
    @Deprecated
    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public List<Flux> getListFlux() {
        return listFlux;
    }

    public void setListFlux(List<Flux> listFlux) {
        this.listFlux = listFlux;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
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

    public void genererDonneesBrutes(Flux f) throws IncompleteBeanExeption {
        
        if(f==null){
            throw new NullPointerException("impossible de verser avec un flux null");
        }
         
        if(f.getID()==null){
            throw new IncompleteBeanExeption("Le flux envoyé n'a pas d'id");
        }
        
        
        List<DonneeBrute> donneesbrutes = this.donneeBrutes;
        boolean absente = true;
        for (int i = 0; i < donneesbrutes.size(); i++) {
            DonneeBrute donneeBrute = donneesbrutes.get(i);

            if (donneeBrute.getFlux().getID().equals(f.getID()) && donneeBrute.getHashContenu().equals(this.hashContenu)) {
                absente = false;
            }

        }

        if (absente) {
            DonneeBrute newDonneeBrute = new DonneeBrute();
            newDonneeBrute.setDescription(description);
            newDonneeBrute.setLink(link);
            newDonneeBrute.setGuid(guid);
            newDonneeBrute.setFlux(f);
            newDonneeBrute.setHashContenu(hashContenu);
            newDonneeBrute.setTitre(titre);
            newDonneeBrute.setDatePub(datePub);
            newDonneeBrute.setDateRecup(dateRecup);
            newDonneeBrute.setHashContenu(hashContenu);
            newDonneeBrute.setCategorie(categorie);
            newDonneeBrute.setContenu(contenu);
                   
//            newDonneeBrute.setItem(this);
            this.donneeBrutes.add(newDonneeBrute);
        }
    }
    
    /***
     * Les donnée brutes de l'item envoyée en argument sont ajouté si nécessaire aux données brutes de l'item courante
     * @param i 
     * @return true si un versement qqchose a pu être versé. sinon false. 
     */
    public Boolean verserLesDonneeBruteAutreItem(Item i){
        
        
        boolean versement = false;
        List<DonneeBrute> listDonneebruteAutreItem = i.donneeBrutes;
        for (int j = 0; j < listDonneebruteAutreItem.size(); j++) {
            DonneeBrute donneeBruteAutre = listDonneebruteAutreItem.get(j);
            List<DonneeBrute> listdonneBrutThis = this.donneeBrutes;
            boolean trouve = false;
            for (int k = 0; k < listdonneBrutThis.size(); k++) {
                DonneeBrute donneeBruteThis = listdonneBrutThis.get(k);
                if(donneeBruteAutre.getFlux().getID().equals(donneeBruteThis.getFlux().getID())){
                    trouve = true;
                }
            }
            if(!trouve){
                        this.donneeBrutes.add(donneeBruteAutre);
                        versement = true;
//                System.out.println("===========VERSEMENT==============");
//                System.out.println("NOMBRE DONNEE BRUT POUR ITEM : " + this.donneeBrutes.size());
//                for (int k = 0; k < this.donneeBrutes.size(); k++) {
//                    DonneeBrute donneeBrute = this.donneeBrutes.get(k);
////                    System.out.println("DESC : " + donneeBrute.getDescription());
//                    
//                }
        
            }
        }
        
        
        if(versement){
            return true; // Si des données brutes on été versées
        }
        else{
            return false; // si rien n'a été versé.
        }
        
    }
    
    /***
     * Ajoute un flux si nécessaire à la liste de l'item
     * @param f 
     */
    public void addFlux(Flux f) throws IncompleteBeanExeption{
        
        if(f==null){
            throw new NullPointerException("Impossible d'ajouter un flux null");
        }
        if(f.getID()==null){
            throw new IncompleteBeanExeption("Le flux n'a pas d'id");
        }
        
        List<Flux> flThis = this.getListFlux();
        boolean present = false;
        for (int i = 0; i < flThis.size(); i++) {
            Flux flux = flThis.get(i);
            if(flux.getID().equals(f.getID())){
                present = true;
            }
        }
        if(!present){
            listFlux.add(f);
        }
        
    }
}