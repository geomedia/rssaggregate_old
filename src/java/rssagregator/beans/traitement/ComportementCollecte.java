package rssagregator.beans.traitement;

import com.sun.syndication.io.FeedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.ws.http.HTTPException;
import org.apache.poi.util.Beta;
import org.reflections.Reflections;
import rssagregator.beans.BeanSynchronise;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.form.ComportementCollecteForm;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;

/**
 * <p>Beans Permettant de stocker le comportement de collecte lié a un flux. Dans la mesure du possible, on essaiera de
 * n'avoir qu'un comportement de collecte pour tout le projet Geomedia. Par composistion il est tout de même possible
 * d'associer un comportement particulier a chaque flux.</p>
 *
 *
 * <p>Un comportement de collecte est une entité composé des différents outil de traitement permettant d'effectuer la
 * collecte :
 * <ul>
 * <li>{@link Requester} Pour effectuer la requete sur le serveur distant</li>
 * <li>{@link AbstrParseur} interpréter les données du flux RSS afin de compléter les données d'un Bean</li>
 * <li>{@link AbstrDedoublonneur} Permet de dédoublonner les items afin de savoir si elle doit ou non être de nouveau
 * enregistré dans la base de données</li>
 * <li></li>
 * </ul>
 * </p>
 *
 */
@Entity
@Table(name = "tr_mediatocollecteaction")
//@Cacheable(true)
//@Cache(type = CacheType.FULL, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS, isolation = CacheIsolationType.SHARED)
@XmlRootElement
public class ComportementCollecte implements Serializable, Cloneable, BeanSynchronise {

    @Transient
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ComportementCollecte.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    @Transient
    Boolean persister;
    /**
     * Le nom du comportement. Il est préférable de nommer les médiators afin de les réutiliszzer par la suite. Exemple
     * : "captation des flux devant être parser par X" ou "flux contenant des renvoie génant"
     */
    @Column(name = "nom")
    private String nom;
    /**
     * On laisse la possibilité de décrire plus longuement le comportement de capture
     */
    @Column(name = "description", columnDefinition = "text")
    private String description;
    /**
     * *
     * <p>Cette variable n'est pas encore correctement implémenté et va peut être être abandonnée.</p>
     * Parmis toute les entités, une est considérée comme le comportement de collecte par defaut, ce boolean permet de
     * déterminer ce comportement par défaut.
     */
    @Beta
    @Column(name = "defaut")
    protected Boolean defaut;
    /**
     * *
     * Le parseur propre au médiateur.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private AbstrParseur parseur;
    /**
     * Le requesteur propre au médiateur. C'est l'objet qui permet de formuler des requêtes http
     */
    @OneToOne(cascade = CascadeType.ALL)
    private AbstrRequesteur requesteur;

    
        /**
     * *
     * Pour fonctionner, cette classe a besoin de découvrir par réflexivité l'ensemble des class de type raffineur.
     * Cette découverte est oppérée dans la bloc static a la première instanciation de la classe. Ce bloc norrit le set
     * ci dessous
     */
    @Transient
    private static Set<Class<? extends AbstrRaffineur>> requesteurClass;
    

    /***
     * Bloc static qui permet de découvir l'ensemble des class de type raffineur a l apremière instanciaiton. 
     */
    static {
        
        // Découverte par réflexivité de l'ensemble des class de type requesteur. 
        Reflections reflections = new Reflections("rssagregator.beans.traitement");
        requesteurClass = reflections.getSubTypesOf(AbstrRaffineur.class);
    }
    
    
    /**
     * Le mediatorAReferer flux permet d'assigner un flux un comportement de collecte. Un médiator est une configuration
     * de parseur Raffineur etc.
     *
     * @element-type Flux
     */
//    /***
//     * Liste de flux pour ce médiateur   LA RELATION EST PORTÉ PAR L'ENTITÉ FLUX. UN FLUX POSSEDE 0 OU 1 MEDIATEUR
//     */
    @OneToMany(mappedBy = "mediatorFlux")
    public List<Flux> listeFlux;
    /**
     * *
     * Les médiateur raffineurs associées au médiateur de collecte. les raffineurs vont être très souvent réutilisé d'ou
     * l'emploi d'un médiateur pour les manier
     */
//    @Transient    LE RAFFINAGE NEST PLUS CONSIDÉRÉ COMME UN TRAITEMENT DE COLECTE; IL N'EST DESTINEE QUA FAIRE DES EXPORT EN CSV
//    private List<MediatorTraitementRafinage> rafineurHTML;
    /**
     * *
     * Pour chaque comportement, il est possible d'assovier des raffineurs.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    protected List<AbstrRaffineur> raffineur = new ArrayList<AbstrRaffineur>();
    /**
     * *
     * Dédoublonneur du médiateur.
     */
    @OneToOne(cascade = CascadeType.ALL)
    protected AbstrDedoublonneur dedoubloneur;
    /**
     * *
     * Il est possible de configurer un second dédoublonneur complétant le travail du premier
     */
//    @OneToOne(cascade = CascadeType.ALL)
//    protected AbstrDedoublonneur dedoublonneur2;
    @Transient
    private Integer nbrItemCollecte;
    /**
     * Nombre de secondes séparant deux moissonage du flux
     */
    @Column(name = "periodiciteCollecte")
    private Integer periodiciteCollecte;
    /**
     * *
     * Une liste des items capturé par le comportement de collecte
     */
    @Transient
    protected List<Item> listItem;
//    @Version
////    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
//    private Long lastUpdate;
//
//    public Long getLastUpdate() {
//        return lastUpdate;
//    }
//
//    public void setLastUpdate(Long lastUpdate) {
//        this.lastUpdate = lastUpdate;
//    }
    @Version
    private Timestamp dateUpdate;

    public Timestamp getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(Timestamp dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    /**
     * Le médiator récolte les item les parse et les dédoublonne. DEPRECATED . IL FAUT UTILISER LES VISITOR POUR
     * EFEFCTUER l'ACTION DE COLLECTER
     */
    @Deprecated
    public List<Item> executeActions(Flux flux) throws MalformedURLException, IOException, HTTPException, FeedException, HTTPException, Exception {
// On vérifie si le collecteur est actif. Pour lancer la collecte
        // On commence par récupérer le flux
        InputStream retourInputStream = null;
        ExecutorService executor = null;
        try {
            //-----------------------
            // REQUESTEUR
            //-----------------------

            if (requesteur != null) {
                if (requesteur.timeOut == null) {
                    requesteur.timeOut = 15;
                }
                this.requesteur.requete(flux.getUrl());
//                retourInputStream = this.requesteur.getHttpInputStream();


                if (parseur != null) {
//                    parseur.setInputStream(retourInputStream);
                }
            }
            //-----------------------------
            //  Parseur
            //-----------------------------
            // On parse le retour du serveur. Le parseur doit se comporter comme une thread car il faut limiter le temps d'execution. Très facile avec un runnable.
            executor = Executors.newFixedThreadPool(1);
            listItem = new ArrayList<Item>();
            Future<List<Item>> futurs;

            futurs = executor.submit(parseur);
            if (requesteur == null) {
                try {
                    listItem = futurs.get(12, TimeUnit.SECONDS);
                } catch (Exception e) {
                    logger.debug("ERR", e);
                }

            } else {
                listItem = futurs.get(requesteur.getTimeOut(), TimeUnit.SECONDS);
            }

            this.nbrItemCollecte = listItem.size();

//            this.nbItTrouve = (short) listItem.size();



            // Génrer hash
            if (dedoubloneur != null && dedoubloneur.enable != null && dedoubloneur.enable) {
                this.dedoubloneur.calculHash(listItem);
            }


            // Ajout brut
//            RafineurHTML rafineur = new RafineurHTML();
//            for (int i = 0; i < listItem.size(); i++) {
//                Item item = listItem.get(i);
////                item.genererDonneesBrutes(flux);
////                rafineur.raffiner(item);
//            }






            //-----------------------------
            //  Dedoublonneur
            //-----------------------------

            // calcul des Md5
            if (dedoubloneur != null && dedoubloneur.enable != null && dedoubloneur.enable) {
//                this.dedoubloneur.calculHash(listItem);
                // On lance le premier dédoublonneur. Il est chargé de dédoublonner par rapport aux hash.
//                this.dedoubloneur.setMediatorAReferer(this);
                listItem = this.dedoubloneur.dedoublonne(listItem, flux);
            }

            //Le deuxieme dedoub. Sur ce qu'il reste. Il dédoublonne en comparant titre et description
//            DedoubloneurComparaisonTitre dedoub2 = new DedoubloneurComparaisonTitre();
//            listItem = dedoub2.dedoublonne(listItem, flux);

//            if (dedoublonneur2 != null && dedoublonneur2.getEnable() != null && dedoublonneur2.getEnable()) {
//                dedoublonneur2.setMediatorAReferer(this);
//                listItem = dedoublonneur2.dedoublonne(listItem, flux);
//            }



//            DaoItem daoItem = DAOFactory.getInstance().getDaoItem();


//            for (ListIterator<Item> it = listItem.listIterator(); it.hasNext();) {
//                Item item = it.next();
//                if (item.getID() == null) {
//                    Item itBDD = daoItem.findByHash(item.getHashContenu());
//                    if (itBDD != null) {
//
////                        itBDD.addFlux(flux);
//                        itBDD.verserLesDonneeBruteAutreItem(item);
//                        it.set(itBDD);
//
//                    }
//                }
//            }



        } catch (Exception e) {
            logger.info("erreur lors de lu traitement : " + e);
            throw e; // On remonte l'erreur. Elle sera traité par le service en passant par la tâche
        } finally {
            // Quoi qu'il arrive, il faut fermer la connection. et détruire le pool du parseur. 
            if (requesteur != null) {
                this.requesteur.clore();
            }
            if (executor != null) {
                executor.shutdownNow();
            }
        }





//        //----------------------------------------------
//        //   Enregistrement des résultats  >> C'est maintenant une tâche séparée
//        //----------------------------------------------
//
//        // On enregistre ces nouvelles items
//        int i;
//        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
//        for (i = 0; i < listItem.size(); i++) {
//            //On précise à la nouvelle item qu'elle appartient au flux collecté
//            listItem.get(i).getListFlux().add(flux);
////                if (persit) {
//            try {
//                daoItem.enregistrement(listItem.get(i), flux);
//            } catch (Exception e) {
//                logger.error("Catch d'une errreur dans l'enregistrement d'un item");
//            }
//
////                    this.flux.getLastEmpruntes().add(0, nouvellesItems.get(i).getHashContenu());
////                }
//        }
//
//
//        // On supprime des hash pour éviter l'accumulation. On en laisse 10 en plus du nombre d'item contenues dans le flux.
//        Integer nbr = flux.getMediatorFluxAction().getDedoubloneur().getCompteCapture()[0] + 10;
//        if (nbr > 0 && nbr < flux.getLastEmpruntes().size()) {
//            for (i = nbr; i < flux.getLastEmpruntes().size(); i++) {
//                flux.getLastEmpruntes().remove(i);
//            }
//        }
//
//
//
////        // TODO le fait de réajouté la tache doit aussi être géré par le service de gestion des incidents
////        // Si il s'agit d'une tache schedule, il faut la réajouter au scheduler
////        if (flux.getActive()) {
////            ServiceCollecteur.getInstance().addScheduledCallable(this);
////            ServiceCollecteur.getInstance().addScheduledCallable(null);
////        }
//
//        // On supprimer les items capturée du cache de l'ORM pour éviter l'encombrement
//        for (i = 0; i < listItem.size(); i++) {
//            DAOFactory.getInstance().getEntityManager().detach(listItem.get(i));
//        }

        return listItem;
    }

    /**
     * *
     * Enregistre la liste des items relevées dans la base de données
     *
     * @param flux : le flux pour lequel la liste des items doit être enregistrée
     */
    @Deprecated
    public void persiter(Flux flux) {

        if (listItem.size() > 0) {
            int i;
            DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
            for (i = 0; i < listItem.size(); i++) {
                //On précise à la nouvelle item qu'elle appartient au flux collecté
                listItem.get(i).getListFlux().add(flux);
//                if (persit) {
                try {
                    // La persistance se fait maintenant en passant par le service
//                    ServiceCollecteur.getInstance().ajouterItemAuFlux(flux, listItem.get(i));
//                    daoItem.enregistrement(listItem.get(i), flux);
                } catch (Exception e) {
                    logger.error("Catch d'une errreur dans l'enregistrement d'un item");
                }
//                    this.flux.getLastEmpruntes().add(0, nouvellesItems.get(i).getHashContenu());
//                }
            }


//            // On supprime des hash pour éviter l'accumulation. On en laisse 10 en plus du nombre d'item contenues dans le flux.
////        Integer nbr = flux.getMediatorFluxAction().getDedoubloneur().getCompteCapture()[0] + 10;
//            Integer nbr = this.dedoubloneur.getCompteCapture()[0] + 10;
//            if (nbr > 0 && nbr < ServiceCollecteur.getInstance().getCacheHashFlux().returnLashHash(flux).size()) {
////                = flux.getLastEmpruntes().iterator();
//                Iterator<String> iterator = ServiceCollecteur.getInstance().getCacheHashFlux().returnLashHash(flux).iterator();
//                for (Iterator<Item> it = listItem.iterator(); it.hasNext();) {
//                    Item item = it.next();
//                    it.remove();
//                    
//                }
//            }


            // On supprimer les items capturée du cache de l'ORM pour éviter l'encombrement
            for (i = 0; i < listItem.size(); i++) {
                DAOFactory.getInstance().getEntityManager().detach(listItem.get(i));
            }
        }
    }

    /**
     * Test le résultat du médiator sur le flux envoyé. On utilise maintenant executeAction avec false en persistance
     */
    public void Obsolete_test(Flux flux) {
    }

    /**
     * Retourne un objet mediatorAReferer par default. Il permet de répondre à 95% des flux en se basant sur le parse
     * par defaut de l'API Rome, le connecteur standart.... Cette methode n'est plus maintenue. Déterminer le
     * comportement par défault par du code compilé et non changeable est mal, juste bon pour faire des test!
     */
    @Deprecated
    public static ComportementCollecte getDefaultCollectAction() {
        ComportementCollecte collecteAction = new ComportementCollecte();

        collecteAction.setDefaut(false);
        collecteAction.periodiciteCollecte = 3600;
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
     * Retourne le nom du médiateur, c'est l'utilisateur qui doit nommer le médiator qu'il a créé. Ce champs est ainsi
     * modifiable depuis l'espace d'administration de l'aggrégateur.
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
     * Retourne la description du médiateur, c'est l'utilisateur qui doit décrire chacun des médiator qu'il crée. Ce
     * champs est ainsi modificable depuis l'espace d'administration
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

    /**
     * *
     * Constructeur par défault. Des objets de traitement basique sont crée pour le Comportement.
     */
    public ComportementCollecte() {
        this.dedoubloneur = new Dedoubloneur();
        this.requesteur = new Requester();
        this.dedoubloneur = new Dedoubloneur();
//        this.dedoublonneur2 = new DedoubloneurComparaisonTitre();
        this.parseur = new RomeParse();
    }

    public static ComportementCollecte getDefaultInstance() {

        ComportementCollecte comportementCollecte = new ComportementCollecte();
        comportementCollecte.setDedoubloneur(new Dedoubloneur());
        comportementCollecte.setRequesteur(new Requester());
        comportementCollecte.setParseur(new RomeParse());


        // Instanciation de tous les raffineurs possible en découvrant les classe par reflexivité
        for (Iterator<Class<? extends AbstrRaffineur>> it = ComportementCollecte.requesteurClass.iterator(); it.hasNext();) {
            Class<? extends AbstrRaffineur> class1 = it.next();

            if (!comportementCollecte.possedeUnRaffineurDeType(class1)) {
                try {
                    AbstrRaffineur r = class1.newInstance();
                    r.setActif(true);
                    comportementCollecte.getRaffineur().add(r);
                } catch (InstantiationException ex) {
                    Logger.getLogger(ComportementCollecteForm.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(ComportementCollecteForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
        }
        
        return comportementCollecte;

//        return null;
    }

    public AbstrParseur getParseur() {
        return parseur;
    }

    public void setParseur(AbstrParseur parseur) {
        this.parseur = parseur;
    }

    //    public AbstrDedoublonneur getDedoubloneur() {
    //        return dedoubloneur;
    //    }
    //
    //    public void setDedoubloneur(Dedoubloneur dedoubloneur) {
    //        this.dedoubloneur = dedoubloneur;
    //    }
    public AbstrDedoublonneur getDedoubloneur() {
        return dedoubloneur;
    }

    public void setDedoubloneur(AbstrDedoublonneur dedoubloneur) {
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

    /**
     * *
     * setter pour {@link #periodiciteCollecte}
     *
     * @return
     */
    public Integer getPeriodiciteCollecte() {
        return periodiciteCollecte;
    }

    /**
     * *
     * getter pour {@link #periodiciteCollecte}
     *
     * @return
     */
    public void setPeriodiciteCollecte(Integer periodiciteCollecte) {
        this.periodiciteCollecte = periodiciteCollecte;
    }

    public List<Item> getListItem() {
        return listItem;
    }

    public void setListItem(List<Item> listItem) {
        this.listItem = listItem;
    }

    @XmlTransient
    public List<Flux> getListeFlux() {
        return listeFlux;
    }

    public void setListeFlux(List<Flux> listeFlux) {
        this.listeFlux = listeFlux;
    }

    @Override
    public String toString() {

        if (this.nom != null && !this.nom.isEmpty()) {
            return this.nom;
        } else {

            return "unamed";
        }
    }

//    public AbstrDedoublonneur getDedoublonneur2() {
//        return dedoublonneur2;
//    }
//
//    public void setDedoublonneur2(AbstrDedoublonneur dedoublonneur2) {
//        this.dedoublonneur2 = dedoublonneur2;
//    }
    @Override
    protected ComportementCollecte clone() throws CloneNotSupportedException {
        ComportementCollecte clone = null;

        clone = (ComportementCollecte) super.clone();

        clone.parseur = (AbstrParseur) this.parseur.clone();
        clone.dedoubloneur = (AbstrDedoublonneur) this.dedoubloneur.clone();
//        clone.dedoubloneur.getCompteCapture()[0]=0;
//        clone.dedoubloneur.getCompteCapture()[1]=1;
//        clone.dedoubloneur.getCompteCapture()[2]=0;
//        clone.dedoubloneur.getCompteCapture()[3]=0;
//        clone.dedoubloneur.setDedoubDatePub(true);




        clone.requesteur = (AbstrRequesteur) this.requesteur.clone();
        return clone;
//        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public ComportementCollecte genererClone() throws CloneNotSupportedException {
        return this.clone();
    }

//    @Override
//    /***
//     * Enregistre le Comportement de collecte auprès du service JMS pour assurer sa diffussion sur les serveurs esclaves (la synchronisation)
//     */
//    public void enregistrerAupresdesService() {
//        this.addObserver(ServiceSynchro.getInstance());
//    }
    @Override
    public Boolean synchroImperative() {
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.ID != null ? this.ID.hashCode() : 0);
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
        final ComportementCollecte other = (ComportementCollecte) obj;
        if (this.ID != other.ID && (this.ID == null || !this.ID.equals(other.ID))) {
            return false;
        }
        return true;
    }

    //    public short getNbItTrouve() {
    //        return nbItTrouve;
    //    }
    //
    //    public void setNbItTrouve(short nbItTrouve) {
    //        this.nbItTrouve = nbItTrouve;
    //    }
    //
    //    public short getNbDedoubMemoire() {
    //        return nbDedoubMemoire;
    //    }
    //
    //    public void setNbDedoubMemoire(short nbDedoubMemoire) {
    //        this.nbDedoubMemoire = nbDedoubMemoire;
    //    }
    //
    //    public short getNbDedoubBdd() {
    //        return nbDedoubBdd;
    //    }
    //
    //    public void setNbDedoubBdd(short nbDedoubBdd) {
    //        this.nbDedoubBdd = nbDedoubBdd;
    //    }
    //
    //    public short getNbLiaisonCree() {
    //        return nbLiaisonCree;
    //    }
    //
    //    public void setNbLiaisonCree(short nbLiaisonCree) {
    //        this.nbLiaisonCree = nbLiaisonCree;
    //    }
    //
    //    public short getNbNouvelle() {
    //        return nbNouvelle;
    //    }
    //
    //    public void setNbNouvelle(short nbNouvelle) {
    //        this.nbNouvelle = nbNouvelle;
    //    }
    //
    //    public short getNbDoublonInterneAuflux() {
    //        return nbDoublonInterneAuflux;
    //    }
    //
    //    public void setNbDoublonInterneAuflux(short nbDoublonInterneAuflux) {
    //    }
    //    }
    public List<AbstrRaffineur> getRaffineur() {
        return raffineur;
    }

    public void setRaffineur(List<AbstrRaffineur> raffineur) {
        this.raffineur = raffineur;
    }

    /**
     * *
     * Parcours les raffineurs du comportement et retourne true si on trouve bien un raffineur du type envoyé en
     * argument
     *
     * @param c
     * @return
     */
    public boolean possedeUnRaffineurDeType(Class c) {

        for (int i = 0; i < raffineur.size(); i++) {
            AbstrRaffineur abstrRaffineur = raffineur.get(i);
            if (abstrRaffineur.getClass().equals(c)) {
                return true;
            }
        }
        return false;
    }

    public static Set<Class<? extends AbstrRaffineur>> getRequesteurClass() {
        return requesteurClass;
    }

    public static void setRequesteurClass(Set<Class<? extends AbstrRaffineur>> requesteurClass) {
        ComportementCollecte.requesteurClass = requesteurClass;
    }
    
    
    
}