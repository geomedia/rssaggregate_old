package rssagregator.beans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.poi.util.Beta;
import rssagregator.beans.traitement.ComportementCollecte;
import rssagregator.dao.DAOFactory;
import rssagregator.services.tache.TacheDecouverteAjoutFlux;

/**
 * Un journal : Le monde, le Figaro... Un journal peut contenir plusieurs {@link Flux}. Les journaux sont synchronisés
 * du serveur maitre vers les serveur esclave d'ou l'implémentation de l'interface {@link BeanSynchronise}
 */
@Entity
//@Customizer(JournalEntityLisner.class) 
//@Cacheable(value = false)
@XmlRootElement
public class Journal extends Bean implements Serializable, BeanSynchronise {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
//    @Version
//    private Integer version;
    /**
     * Le nom du journal. Par exemple : "Le monde", "Libération"
     */
    @Column(name = "nom", unique = true)
    private String nom;
    /**
     * <p><strong>/!\ L'implémentation de cette variable est encore aléatoire.<strong> Elle n'est pour l'instant
     * qu'informative et aucun calcul de date n'est effectué depuis celle ci.</p>
     * <p>Le fuseaeu horraire. Cette information est importante pour effectuer des calculs de date. Dans la table item,
     * on enregistre la date de reception. En connaissant le fuseau horraire du journal on peut calculer une nouvelle
     * date afin d'approcher d'estimer la date d'émission. Cette information sur la date doit être comparée à la date
     * d'émission trouvé dans les balises XML.</p>
     */
    @Beta
    @Column(name = "fuseauHorraire")
    private String fuseauHorraire;
    /**
     * La langue d'écriture du journal. C'est une chaine de caractère de quatre lettre correspondant aux code iso des
     * langues.
     */
    @Column(name = "langue", length = 4)
    private String langue;
    /**
     * *
     * Le pays du journal. Ici aussi, on utilise le code iso2
     */
    @Column(name = "pays", length = 3)
    private String pays;
    /**
     * Un journal possède plusieurs flux. La suppression du journal entraine la suppression des flux par cascade.
     */
    @OneToMany(mappedBy = "journalLie", fetch = FetchType.LAZY, cascade = {CascadeType.DETACH})
    private List<Flux> fluxLie = new ArrayList<Flux>();
    /**
     * La page d'accueil du journal. Cette variable est informative. Exemple http://www.lemonde.fr
     */
    @Column(name = "urlAccueil", length = 2000)
    private String urlAccueil;
    /**
     * Il s'agit de la page html permettant de trouver tous les flux RSS. Pour de nombreux journaux, on a une page de ce
     * type.
     */
    @Column(name = "urlHtmlRecapFlux", length = 2000)
    private String urlHtmlRecapFlux;
    /**
     * *
     * Boolean permettant de renseigner si oui ou non la liste des flux appratenant au journal doit être périodiquement
     * mise à jour en se basant sur la tache {@link TacheDecouverteAjoutFlux}
     */
    @Column(name = "autoUpdateFlux")
    private Boolean autoUpdateFlux;
    /**
     * *
     * Détermine par la tache automatique {@link TacheDecouverteAjoutFlux} doivent êter activé ou non
     */
    @Column(name = "activerFluxDecouvert")
    private Boolean activerFluxDecouvert = false;
    
    private Integer periodiciteDecouverte;
    
    /***
     * Code permettant de caractériser la ville d'édition du journal . Voir Timothée
     */
    @Column(name = "codeVille", length = 3 , nullable = false)
    private String codeVille;
    
    
    /***
     * Code renseigner par les chercheur permettant d'abréger le journal dans la base de données géomedia.
     */
    @Column(name = "codeJournal", length = 6, nullable = false)
    private String codeJournal; 
    
    
    /**
     * *
     * Un champs texte permettant aux administrateurs de saisir des informations sur le journal. Ce champs texte permet
     * par exemple de donner un historique du journal ou de rendre compte de son point de vue éditoriale
     */
    @Column(name = "information", columnDefinition = "text")
    private String information;
    /**
     * *
     * Le type du journal : <ul><li>quotidien, </li><li>hebdomadaire</li></ul>
     */
    @Column(name = "typeJournal", nullable = false)
    private String typeJournal;
    /**
     * *
     * Lors de la découverte de flux par la tâche {@link TacheDecouverteAjoutFlux}, il est nécessaire d'associer les
     * {@link Flux} découvert à un {@link ComportementCollecte}. C'est ce comportement qui sera attribué au flux du
     * journal présent
     */
    @OneToOne
    private ComportementCollecte comportementParDefaultDesFlux;
    
    
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
     * Get the value of typeJournal
     *
     * @return the value of typeJournal
     */
    public String getTypeJournal() {
        return typeJournal;
    }

    /**
     * Set the value of typeJournal
     *
     * @param typeJournal new value of typeJournal
     */
    public void setTypeJournal(String typeJournal) {
        this.typeJournal = typeJournal;
    }

    /**
     * *
     * Constructeur
     */
    public Journal() {
    }

    /**
     * *
     * @see #ID
     * @param ID
     */
    public Long getID() {
        return ID;
    }

    /**
     * *
     * @see #ID
     * @param ID
     */
    public void setID(Long ID) {
        this.ID = ID;
    }

    /**
     * *
     * @see #nom
     * @return
     */
    public String getNom() {
        return nom;
    }

    /**
     * *
     * @see #nom
     * @return
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * *
     * @see #fuseauHorraire
     * @return
     */
    public String getFuseauHorraire() {
        return fuseauHorraire;
    }

    /**
     * *
     * @see #fuseauHorraire
     * @param fuseauHorraire
     */
    public void setFuseauHorraire(String fuseauHorraire) {
        this.fuseauHorraire = fuseauHorraire;
    }

    /**
     * *
     * @see #langue
     * @return
     */
    public String getLangue() {
        return langue;
    }

    /**
     * *
     * @see #langue
     * @param langue
     */
    public void setLangue(String langue) {
        this.langue = langue;
    }

    /**
     * *
     * @see #fluxLie
     * @return
     */
    @XmlTransient
    public List<Flux> getFluxLie() {
        return fluxLie;
    }

    /**
     * *
     * @see #fluxLie
     * @return
     */
    public void setFluxLie(List<Flux> fluxLie) {
        this.fluxLie = fluxLie;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getUrlHtmlRecapFlux() {
        return urlHtmlRecapFlux;
    }

    public void setUrlHtmlRecapFlux(String urlHtmlRecapFlux) {
        this.urlHtmlRecapFlux = urlHtmlRecapFlux;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getUrlAccueil() {
        return urlAccueil;
    }

    public void setUrlAccueil(String urlAccueil) {
        this.urlAccueil = urlAccueil;
    }

    /**
     * *
     * @see #comportementParDefaultDesFlux
     * @return
     */
    public ComportementCollecte getComportementParDefaultDesFlux() {
        return comportementParDefaultDesFlux;
    }

    /**
     * *
     * @see #comportementParDefaultDesFlux
     * @return
     */
    public void setComportementParDefaultDesFlux(ComportementCollecte comportementParDefaultDesFlux) {
        this.comportementParDefaultDesFlux = comportementParDefaultDesFlux;
    }

    public Boolean getAutoUpdateFlux() {
        return autoUpdateFlux;
    }

    public void setAutoUpdateFlux(Boolean autoUpdateFlux) {
        this.autoUpdateFlux = autoUpdateFlux;
    }

    public Boolean getActiverFluxDecouvert() {
        return activerFluxDecouvert;
    }

    public void setActiverFluxDecouvert(Boolean activerFluxDecouvert) {
        this.activerFluxDecouvert = activerFluxDecouvert;
    }

    public Integer getPeriodiciteDecouverte() {
        return periodiciteDecouverte;
    }

    public void setPeriodiciteDecouverte(Integer periodiciteDecouverte) {
        this.periodiciteDecouverte = periodiciteDecouverte;
    }

    public String getCodeVille() {
        return codeVille;
    }

    public void setCodeVille(String codeVille) {
        this.codeVille = codeVille;
    }

    public String getCodeJournal() {
        return codeJournal;
    }

    public void setCodeJournal(String codeJournal) {
        this.codeJournal = codeJournal;
    }
    
    
    
    
    


    /**
     * *
     * La redéfinition de cette méthode renvoie toujour true pour un {@link Journal}. Les journaux doivent ainsi
     * systématiquement être synchronisé du serveur maitre vers les serveur esclaves.
     *
     * @return
     */
    @Override
    public Boolean synchroImperative() {
        return true;
    }

    /**
     * Renvoie le nom du journal.
     *
     * @return Le nom du journal. Si il n'existe pas, une chaine de caractère "journal sans nom" avec l'ID du journal si
     * elle existe.
     */
    @Override
    public String toString() {
        
        if(this.nom != null && !this.nom.isEmpty() && this.pays != null && !this.pays.isEmpty()){
            return this.nom+" ("+this.pays+")";
        }
        
        if (this.nom != null && !this.nom.isEmpty()) {
            return this.nom;
        } else if (this.ID != null) {
            return "ID : " + this.ID + "journal sans nom";
        } else {
            return "journal sans nom";
        }
    }

    @Override
    public String getReadURL() {
        
                Conf c = DAOFactory.getInstance().getDAOConf().getConfCourante();
        String url = c.getServurl();
                if (url != null && url.length() > 1) {
            Character ch = url.charAt(url.length() - 1);
            if (!ch.equals(new Character('/'))) {
                url += "/";
            }
        }
                
                String retour = url + "journaux/read?id=" + ID.toString();
                return retour;
                
        
        
    }
}