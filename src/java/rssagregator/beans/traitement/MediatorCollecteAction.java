package rssagregator.beans.traitement;

import com.sun.syndication.io.FeedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.ws.http.HTTPException;
import rssagregator.beans.DebugRecapLeveeFlux;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;

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
public class MediatorCollecteAction implements Serializable, Cloneable, Callable<List<Item>> {

    @Transient
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MediatorCollecteAction.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    @Transient
    Boolean persister;
    /**
     * Le nom du comportement. Il est préférable de nommer les médiators afin de
     * les réutiliszzer par la suite. Exemple : "captation des flux devant être
     * parser par X" ou "flux contenant des renvoie génant"
     */
    @Column(name = "nom")
    private String nom;
    /**
     * On laisse la possibilité de décrire plus longuement le comportement de
     * capture
     */
    @Column(name = "description", columnDefinition = "text")
    private String description;
    /**
     * *
     * Parmis toute les entités, une est considérée comme le comportement de
     * collecte par defaut, ce boolean permet de déterminer ce comportement par
     * défaut.
     */
    @Column(name = "defaut")
    protected Boolean defaut;
    /**
     * *
     * Le parseur propre au médiateur.
     */
    @OneToOne(cascade = CascadeType.ALL)
    private AbstrParseur parseur;
    /**
     * Le requesteur propre au médiateur. C'est l'objet qui permet de formuler
     * des requêtes http
     */
    @OneToOne(cascade = CascadeType.ALL)
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
//    @Transient    LE RAFFINAGE NEST PLUS CONSIDÉRÉ COMME UN TRAITEMENT DE COLECTE; IL N'EST DESTINEE QUA FAIRE DES EXPORT EN CSV
//    private List<MediatorTraitementRafinage> rafineurHTML;
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
    @OneToOne(cascade = CascadeType.ALL)
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

        //-----------------------
        // REQUESTEUR
        //-----------------------

        if (requesteur.timeOut == null) {
            requesteur.timeOut = 15;
            System.out.println("");
        }
        this.requesteur.requete(flux.getUrl());
        InputStream retourInputStream = this.requesteur.getHttpInputStream();

        //-----------------------------
        //  Parseur
        //-----------------------------
        // On parse le retour du serveur. Le parseur doit se comporter comme une thread car il faut limiter le temps d'execution. Très facile avec un runnable.
        parseur.setXmlIS(retourInputStream);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<List<Item>> futurs = executor.submit(parseur);
        List<Item> listItem = futurs.get(requesteur.getTimeOut(), TimeUnit.SECONDS);

        this.nbrItemCollecte = listItem.size();

        //-----------------------------
        //  Dedoublonneur
        //-----------------------------

        // calcul des Md5
        this.dedoubloneur.calculHash(listItem);
        listItem = this.dedoubloneur.dedoublonne(listItem, flux);


        //----------------------------------------------
        //   Enregistrement des résultats
        //----------------------------------------------

        // On enregistre ces nouvelles items
        int i;
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        for (i = 0; i < listItem.size(); i++) {
            //On précise à la nouvelle item qu'elle appartient au flux collecté
            listItem.get(i).getListFlux().add(flux);
//                if (persit) {
            try {
                daoItem.enregistrement(listItem.get(i), flux);
            } catch (Exception e) {
                logger.error("Catch d'une errreur dans l'enregistrement d'un item");
            }

//                    this.flux.getLastEmpruntes().add(0, nouvellesItems.get(i).getHashContenu());
//                }
        }


        // On supprime des hash pour éviter l'accumulation. On en laisse 10 en plus du nombre d'item contenues dans le flux.
        Integer nbr = flux.getMediatorFluxAction().getDedoubloneur().getCompteCapture()[0] + 10;
        if (nbr > 0 && nbr < flux.getLastEmpruntes().size()) {
            for (i = nbr; i < flux.getLastEmpruntes().size(); i++) {
                flux.getLastEmpruntes().remove(i);
            }
        }

        //Devra être supprimé à la fin
        DebugRecapLeveeFlux debug = new DebugRecapLeveeFlux();
        debug.setDate(new Date());
        debug.setNbrRecup(listItem.size());
        flux.getDebug().add(debug);


//        // TODO le fait de réajouté la tache doit aussi être géré par le service de gestion des incidents
//        // Si il s'agit d'une tache schedule, il faut la réajouter au scheduler
//        if (flux.getActive()) {
//            ServiceCollecteur.getInstance().addScheduledCallable(this);
//            ServiceCollecteur.getInstance().addScheduledCallable(null);
//        }

        // On supprimer les items capturée du cache de l'ORM pour éviter l'encombrement
        for (i = 0; i < listItem.size(); i++) {
            DAOFactory.getInstance().getEntityManager().detach(listItem.get(i));
        }
        
        

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

        collecteAction.setDefaut(false);

        collecteAction.requesteur = Requester.getDefaulfInstance();
        collecteAction.parseur = RomeParse.getDefaultInstance();


        collecteAction.dedoubloneur = new Dedoubloneur();
        collecteAction.dedoubloneur.setDeboubTitle(Boolean.TRUE);
        collecteAction.dedoubloneur.setDeboudDesc(Boolean.TRUE);
        collecteAction.dedoubloneur.setDedouGUID(Boolean.TRUE);
        collecteAction.dedoubloneur.setDedoubCategory(Boolean.FALSE);
        collecteAction.dedoubloneur.setDedoubDatePub(Boolean.FALSE);
        collecteAction.dedoubloneur.setDedoubLink(Boolean.TRUE);
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

    public Boolean getDefaut() {
        return defaut;
    }

    public void setDefaut(Boolean defaut) {
        this.defaut = defaut;
    }

    @Override
    public String toString() {

        if (this.nom != null && !this.nom.isEmpty()) {
            return this.nom;
        } else {

            return "unamed";
        }
    }

    @Override
    protected MediatorCollecteAction clone() throws CloneNotSupportedException {
        MediatorCollecteAction clone = null;

        clone = (MediatorCollecteAction) super.clone();

        clone.parseur = (AbstrParseur) this.parseur.clone();
        clone.dedoubloneur = (AbstrDedoublonneur) this.dedoubloneur.clone();
//        clone.dedoubloneur.getCompteCapture()[0]=0;
//        clone.dedoubloneur.getCompteCapture()[1]=1;
//        clone.dedoubloneur.getCompteCapture()[2]=0;
//        clone.dedoubloneur.getCompteCapture()[3]=0;
//        clone.dedoubloneur.setDedoubDatePub(true);



        System.out.println("");

        clone.requesteur = (AbstrRequesteur) this.requesteur.clone();
        return clone;
//        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public MediatorCollecteAction genererClone() throws CloneNotSupportedException {
        return this.clone();
    }

    public static void main(String[] args) {
        try {
            MediatorCollecteAction mca = MediatorCollecteAction.getDefaultCollectAction();
            System.out.println("time out 1 : " + mca.getRequesteur().getTimeOut());
            MediatorCollecteAction mca2 = (MediatorCollecteAction) mca.clone();


            System.out.println("time out 1 : " + mca.getRequesteur().getTimeOut());
            System.out.println("time out 1 : " + mca2.getRequesteur().getTimeOut());


        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(MediatorCollecteAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<Item> call() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}