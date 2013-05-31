package rssagregator.beans.traitement;

import com.sun.syndication.io.FeedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.ws.http.HTTPException;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.services.ListeFluxCollecteEtConfigConrante;

/**
 * Cette classe gère les relations entre un ou plusieurs flux et les differents
 * objets de traitement(parseurs, raffinneurs...). Il est ainsi possible de
 * créer plusieurs comportement de collecte et de les concerver (ces
 * comportements sont persistés dans la base de données). Un flux n'est associé
 * qu'à un comportement de collecte. Il est possible d'obtenir le comportement
 * par defaut, celui ci est écrit en dur dans le code. La méthode static
 * getDefaultCollectAction retourne une instance de ce mediateur par défault.
 * Celui ci doit permettre de collecter sans préciser de configuration la
 * majorité des flux. Si aucun médiator n'est associé à un flux c'est par le
 * biais de cette méthode qu'on va obtenir le comportement par défault
 */
@Entity
@Table(name = "tr_mediatocollecteaction")
public class MediatorCollecteAction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    /**
     * Le nom du comportement. Il est préférable de nommer les médiators afin de
     * les réutiliszzer par la suite. Exemple : "captation des flux devant être
     * parser par X" ou "flux contenant des renvoie génant"
     */
    private String nom;
    /**
     * On laisse la possibilité de décrire plus longuement le comportement de
     * capture
     */
    private String description;
    /**
     * *
     * Le parseur propre au médiateur.
     */
    @OneToOne
    private AbstrParseur parseur;
    /**
     * Le requesteur propre au médiateur. C'est l'objet qui permet de formuler
     * des requêtes http
     */
    @OneToOne
    private AbstrRequesteur requesteur;
    /**
     * Le mediator flux permet d'assigner un flux un comportement de collecte.
     * Un médiator est une configuration de parseur Raffineur etc.
     *
     * @element-type Flux
     */
//    /***
//     * Liste de flux pour ce médiateur   LA RELATION EST PORTÉ PAR L'ENTITÉ FLUX. UN FLUX POSSEDE 0 OU 1 MEDIATEUR
//     */
//    public List<Flux> listeFlux;
    /**
     * *
     * Les médiateur raffineurs associées au médiateur de collecte. les
     * raffineurs vont être très souvent réutilisé d'ou l'emploi d'un médiateur
     * pour les manier
     */
    @Transient
    private List<MediatorTraitementRafinage> rafineurHTML;
    /**
     * *
     * Nettoyeur, une classe pas encore certaine.
     */
    @Transient
    private Nettoyeur myNettoyeur;
    /**
     * *
     * Dédoublonneur du médiateur.
     */
    @Transient
    private AbstrDedoublonneur dedoubloneur;
    
    @Transient
    private Integer nbrItemCollecte;

    /**
     * Le médiator utilise tout les objets de service pour effectuer l'action
     * sur le flux. Le traitement va produire les Items rafinées pour toutes les
     * items du flux Le boolleen permet de préciser si il faut persister ou non
     * les traitements effectués. le fait de ne pas persister permet de tester
     * l'action d'un médiateur. Rien n'est persisté dans la base de données. La
     * liste des hash n'est pas mise à jour. Seul la listDernierItemCollecte est
     * mis à jours afin que coté JEE on puisse présenter des résultats.
     */
    public List<Item> executeActions(Flux flux) throws MalformedURLException, IOException, HTTPException, FeedException, HTTPException, Exception {
// On vérifie si le collecteur est actif. Pour lancer la collecte
        // On commence par récupérer le flux
        
        
        this.requesteur.requete(flux.getUrl());
      

//        String retourHTTP = this.requesteur.getHttpResult();
        
        InputStream retourInputStream = this.requesteur.getHttpInputStream();
        
      
        
        // On parse le retour du serveur 
        List<Item> listItem = parseur.execute(retourInputStream);
        this.nbrItemCollecte = listItem.size();

        // On dédoublonne
        //TODO : Il faut dédoublonner dans le médiator collecte action. La liste retounée corespond aux items devant être lié au flux. Il peut s'agir d'item nouvelles ou d'item déjà enregistrée mais pas encore lié au flux traité
        listItem = this.dedoubloneur.dedoublonne(listItem, flux);


        
        this.requesteur.disconnect();
        return listItem;
    }

    /**
     * Test le résultat du médiator sur le flux envoyé. On utilise maintenant
     * executeAction avec false en persistance
     */
    public void Obsolete_test(Flux flux) {
    }

    /**
     * Retourne un objet mediator par default. Il permet de répondre à 95% des
     * flux en se basant sur le parse par defaut de l'API Rome, le connecteur
     * standart....
     */
    public static MediatorCollecteAction getDefaultCollectAction() {
        MediatorCollecteAction collecteAction = new MediatorCollecteAction();

        collecteAction.requesteur = Requester.getDefaulfInstance();
        collecteAction.parseur = RomeParse.getDefaultInstance();
        collecteAction.dedoubloneur = new Dedoubloneur();
        return collecteAction;
    }

    /**
     * *
     * Retourne le nom du médiateur, c'est l'utilisateur qui doit nommer le
     * médiator qu'il a créé. Ce champs est ainsi modifiable depuis l'espace
     * d'administration de l'aggrégateur.
     *
     * @return
     */
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * *
     * Retourne la description du médiateur, c'est l'utilisateur qui doit
     * décrire chacun des médiator qu'il crée. Ce champs est ainsi modificable
     * depuis l'espace d'administration
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AbstrRequesteur getRequesteur() {
        return requesteur;
    }

    public void setRequesteur(AbstrRequesteur requesteur) {
        this.requesteur = requesteur;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public MediatorCollecteAction() {
    }

    public IfsParseur getParseur() {
        return parseur;
    }

    public void setParseur(AbstrParseur parseur) {
        this.parseur = parseur;
    }

    public List<MediatorTraitementRafinage> getRafineurHTML() {
        return rafineurHTML;
    }

    public void setRafineurHTML(List<MediatorTraitementRafinage> rafineurHTML) {
        this.rafineurHTML = rafineurHTML;
    }

    public Nettoyeur getMyNettoyeur() {
        return myNettoyeur;
    }

    public void setMyNettoyeur(Nettoyeur myNettoyeur) {
        this.myNettoyeur = myNettoyeur;
    }

    public AbstrDedoublonneur getDedoubloneur() {
        return dedoubloneur;
    }

    public void setDedoubloneur(Dedoubloneur dedoubloneur) {
        this.dedoubloneur = dedoubloneur;
    }

    public Integer getNbrItemCollecte() {
        return nbrItemCollecte;
    }

    public void setNbrItemCollecte(Integer nbrItemCollecte) {
        this.nbrItemCollecte = nbrItemCollecte;
    }
    
    
    
}