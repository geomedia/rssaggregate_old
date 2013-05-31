package rssagregator.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import rssagregator.beans.traitement.MediatorCollecteAction;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import rssagregator.beans.incident.FluxIncident;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Une des entités les plus importantes... Il s'agit d'un flux de syndication
 * (RSS ATOM...). Un flux appartient à un journal. Un journal peut posséder
 * plusieurs flux. Un flux possède plusieurs items
 */
@Entity
@Table(name = "flux")
public class Flux extends Bean implements Observer, Serializable {

//    @PersistenceContext(type= PersistenceContextType.EXTENDED)
//private EntityManager em;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    /**
     * URL du flux rss. inclure si possible le protocole (http://). Mais, lors
     * de l'ajout une regexp vérifie si l'url est correcte et la modifie
     */
    @Column(name = "url", length = 2000, nullable = false, unique = true)
    private String url;
    /**
     * Nombre de secondes séparant deux moissonage du flux
     */
    @Column(name = "periodiciteCollecte")
    private Integer periodiciteCollecte;
    /**
     * Permet de déterminer si le flux doit être collecté ou non
     */
    @Column(name = "active")
    private Boolean active;
    /**
     * L'url de la rubrique du flux, il s'agit de la page HTML d'entrée de la
     * rubrique. Cette adresse peut être utilisé pour faire de l'auto discovery.
     */
    @Transient
    private String urlRubrique;
    /**
     * Les dernières empruntes md5 des items du flux. On les garde en mémoire
     * pour faire du dédoublonage sans effectuer de requetes dans la base de
     * données. On ne persiste pas dans la base de donnée (TRANSISIENT NORMAL)
     */
    @Transient
    private List<String> lastEmpruntes;
    /**
     * L'objet Callable qui permet d'être lancé pour effectuer la récupération
     * du flux. Cet objet doit être ajouté dans le pool de thread du service de
     * récupération. Il n'est pas persisté
     */
    @Transient
    private TacheRecup tacheRechup;
    /**
     * Lors du lancement d'une collecte par la tache de récupération, cette
     * liste est mise à jour lorsque à la fin de la collecte, la tache notifie
     * au flux ses résultats. On trouve dans ces résultats des items déja
     * collecte, ils sont marqué du bool item.isNew.
     */
    @Deprecated
    @Transient
    private List<Item> listDernierItemCollecte;
    /**
     *
     * Liste des Item du flux. Permet de matérialiser la relation entre flux et
     * Item
     */
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Item> item;
    /**
     * Un objet flux peut posséder différents incidents. Un incident ne possède
     * qu'un flux.
     *
     * @element-type FluxIncident
     */
//    @OneToMany(mappedBy = "flux", cascade = CascadeType.ALL)
//    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true,fetch = FetchType.LAZY)
    @OneToMany(mappedBy = "fluxLie", cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FluxIncident> incidentsLie;
    /**
     *
     * @element-type InfoCollecte
     */
//Cascade remove : On va utiliser la dao flux pour sauvegarder les infos collecte. 
//    @OneToMany(mappedBy = "flux", cascade = {CascadeType.ALL})
    @Transient
    private List<InfoCollecte> infoCollecteFlux;
    /**
     * Le type du flux (international, a la une etc...). Les types de flux sont
     * des beans. ils sont persisté dans la base de données
     */
//    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH})
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH}, fetch = FetchType.LAZY)
    private FluxType typeFlux;
    /**
     * Un flux peut appratenir à un journal. Un journal peut contenir plusieurs
     * flux
     */
// On veut que le flux ne puisse pas créer de journaux mais simplment se lier. Ce n'est pas à la dao du flux de de créer des journaux.
    @ManyToOne(cascade = {CascadeType.MERGE})
    private Journal journalLie;
    /**
     * Le mediator flux permet d'assigner un flux un comportement de collecte.
     * Un médiator est une configuration de parseur Raffineur etc.
     */
    //TODO : pas encore géré
//    @OneToOne(cascade = CascadeType.MERGE)
    @Transient
    private MediatorCollecteAction MediatorFlux;
    /**
     * On ne persiste pas ce champs
     */
    @Transient
    private Boolean erreurDerniereLevee;

    /**
     * Retourne une la liste des rss autodécouvert. Commence par l'adresse
     * urlRubrique. Si pas de réponse, on remonte vers la racine du site. On
     * enlève une sous répertoire par tentative.
     */
    public void autodiscovery() {
    }

    /**
     * Test la config en récupérant les items à l'url. Le contenu n'est pas
     * persister. Permet à l'admin de s'assurer que la config est bonne, que le
     * parseur est adhéquat
     */
    public void Obsolete_test() {
    }

    /**
     * Le constructeur
     */
    public void Flux() {
        this.item = new ArrayList<Item>();
        this.listDernierItemCollecte = new ArrayList<Item>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPeriodiciteCollecte() {
        return periodiciteCollecte;
    }

    public void setPeriodiciteCollecte(Integer periodiciteCollecte) {
        this.periodiciteCollecte = periodiciteCollecte;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getUrlRubrique() {
        return urlRubrique;
    }

    public void setUrlRubrique(String urlRubrique) {
        this.urlRubrique = urlRubrique;
    }

    public List<String> getLastEmpruntes() {
        return lastEmpruntes;
    }

    public void setLastEmpruntes(List<String> lastEmpruntes) {
        this.lastEmpruntes = lastEmpruntes;
    }

    public TacheRecup getTacheRechup() {
        return tacheRechup;
    }

    public void setTacheRechup(TacheRecup tacheRechup) {
        this.tacheRechup = tacheRechup;
    }

    public List<Item> getListDernierItemCollecte() {
        return listDernierItemCollecte;
    }

    public void setListDernierItemCollecte(List<Item> listDernierItemCollecte) {
        this.listDernierItemCollecte = listDernierItemCollecte;
    }

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> items) {
        this.item = items;
    }

    public List<InfoCollecte> getInfoCollecteFlux() {
        return infoCollecteFlux;
    }

    public void setInfoCollecteFlux(List<InfoCollecte> infoCollecteFlux) {
        this.infoCollecteFlux = infoCollecteFlux;
    }

    public FluxType getTypeFlux() {
        return typeFlux;
    }

    public void setTypeFlux(FluxType typeFlux) {
        this.typeFlux = typeFlux;
    }

    public Journal getJournalLie() {
        return journalLie;
    }

    public void setJournalLie(Journal journalLie) {
        this.journalLie = journalLie;
    }

    public MediatorCollecteAction getMediatorFlux() {
        return MediatorFlux;
    }

    public void setMediatorFlux(MediatorCollecteAction MediatorFlux) {
        this.MediatorFlux = MediatorFlux;
    }

    public Flux() {
        this.item = new ArrayList<Item>();
        this.lastEmpruntes = new ArrayList<String>();

        this.MediatorFlux = MediatorCollecteAction.getDefaultCollectAction();
        this.incidentsLie = new ArrayList<FluxIncident>();
    }

    @Override
    public void update(Observable o, Object arg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * *
     * Créer une nouvelle tache de récupération pour le Flux
     */
    public void createTask() {
        this.tacheRechup = new TacheRecup(this);
    }

    public Boolean getErreurDerniereLevee() {
        return erreurDerniereLevee;
    }

    public void setErreurDerniereLevee(Boolean erreurDerniereLevee) {
        this.erreurDerniereLevee = erreurDerniereLevee;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public List<FluxIncident> getIncidentsLie() {
        return incidentsLie;
    }

    public void setIncidentsLie(List<FluxIncident> incidentsLie) {
        this.incidentsLie = incidentsLie;
    }

    /**
     * *
     * Parcours les incidents et retourne ceux qui ne sont pas clos, cad ceux
     * qui n'ont pas de date de fin
     *
     * @return
     */
    public List<FluxIncident> getIncidentEnCours() {
        List<FluxIncident> iRetour = new ArrayList<FluxIncident>();

        int i;
        for (i = 0; i < this.incidentsLie.size(); i++) {

            if (this.incidentsLie.get(i).getDateFin() == null) {
                iRetour.add(this.incidentsLie.get(i));
            }
        }
        return iRetour;
    }

    /**
     * *
     * Parcours les incident ouvert et retourne le premier incident ouvert du
     * même type que la class envoyé en argument.
     *
     * @param c
     * @return L'incident ouvert du même type que Classc ou null si rien n'a été
     * trouvé
     */
    public FluxIncident getIncidentOuverType(Class c) {
//        List<AbstrFluxIncident> listRetour = new ArrayList<AbstrFluxIncident>();

        List<FluxIncident> list = this.getIncidentEnCours();
        int i;
        for (i = 0; i < list.size(); i++) {
            if (list.get(i).getClass().equals(c)) {
                return list.get(i);
//                listRetour.add(list.get(i));
            }
        }
        return null;
    }

    /**
     * *
     * Parcours les incidents du flux et inscrit la date courante en date de fin
     * d'un evenuel incident ouvert. Cette méthode est éxecuté pour chaque flux
     * losque la levée s'est déroulé avec succes
     */
    public void fermerLesIncidentOuvert() {
        int i;
        List<FluxIncident> incidentOuvert = this.getIncidentEnCours();

        for (i = 0; i < incidentOuvert.size(); i++) {
            // On vérifi quand même que la date de fin est bien null
            if (incidentOuvert.get(i).getDateFin() == null) {
                incidentOuvert.get(i).setDateFin(new Date());
            }
        }
    }

    @Override
    /***
     * Retourne le nom du journal ainsi que le type du flux. Si ces variables ne sont pas définient, on retourne l'url.
     */
    public String toString() {
//        return "zouzou";
        String nomRetour = "";
        if (this.getJournalLie() != null && this.getTypeFlux().getDenomination() != null) {
            nomRetour += this.getJournalLie().getNom() + " - " + this.getTypeFlux().getDenomination();
        } else {
            nomRetour = this.getUrl();
        }
        return nomRetour;

    }
}