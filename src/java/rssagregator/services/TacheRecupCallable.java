/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Date;
import java.util.List;
import java.util.Observer;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.Journal;
import rssagregator.beans.exception.CollecteUnactiveFlux;
import rssagregator.beans.exception.UnIncidableException;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.services.crud.AbstrServiceCRUD;
import rssagregator.services.crud.ServiceCRUDFactory;

/**
 * La tâche permettant au {@link Flux} d'être collecté périodiquement. Elle est gérée par le service
 * {@link ServiceCollecteur}
 *
 * @author clem
 */
public class TacheRecupCallable extends TacheImpl<TacheRecupCallable> implements Incidable {

    public TacheRecupCallable(Observer s) {
        super(s);
        annulerTache = false;
    }

    /**
     * *
     *
     * @param flux :Le flux attribuer à la tâche
     * @param s : Le service devant gérer la tâche
     * @param tacheSchedule : Indique si il s'agit d'une tache schedulé (qui doit être réajouté au service en fin de
     * traitement)
     * @param persit :Faut t'il persister dans la base de données
     */
    public TacheRecupCallable(Flux flux, Observer s, Boolean tacheSchedule) {
        this(s);
        this.flux = flux;
        this.tacheSchedule = tacheSchedule;
        this.timeSchedule = flux.getMediatorFlux().getPeriodiciteCollecte();
//        this.persit = persit;
    }
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheRecupCallable.class);
    /**
     * *
     * Les items capturées par la tache
     */
    List<Item> nouvellesItems;
    /**
     * *
     * Le flux de la tache
     */
    public Flux flux;
    /**
     * A chaque récupération, il faut inscrire dans cette variable la date. Cette variable est ainsi modifié au
     * lancement de la méthode run
     */
    public Date DateDerniereRecup;
//    /**
//     * *
//     * Lorsqu'une exeption survient, on stocke sa référence ici
//     */
    public AbstrIncident incident;
    /**
     * *
     * Indique si cette tache est une tache schedulé devant être rajouté à la fin de son traitment dans le pool
     * schedulé. Si false, c'est une tâche manuelle, elle ne sera pas réajouté
     */
    Boolean tacheSchedule;
    /**
     * *
     * Le boolenn persit indique si la tâche doit ou non enregistrer ses données dans la base de données
     */
//    Boolean persit;
    /**
     * *
     * Permet d'annuler la tâche. Lors de son déclanchement, elle ne va rien faire et ne plus être ajouté au scheduler
     */
    Boolean annulerTache;
    /**
     * *
     * Un pointeur permettant de retrouver le comportement utilisé (un clone de celui servant de modèle dans le flux)
     */
    MediatorCollecteAction comportementDuFlux;

//    EntityManager em;
    /**
     * *
     * @return un booleen true si ok false si exeption capturé et géré. mais pas vraiment utilisé dans le reste du
     * programme
     * @throws Exception
     */
//    @Override
//    public synchronized TacheRecupCallable call() throws Exception {
//        this.exeption = null;
//        
//
//        logger.debug("lancement collecte flux  " + flux);
//        
//        try {
////                    Thread.sleep(3000);
//            if (!flux.getActive()) {
//                throw new CollecteUnactiveFlux("Ce flux doit être activé pour être récolté");
//            }
//            
//            if (!annulerTache) {  // Si la tache n'a pas été annulé elle se déroule normalement.
//logger.debug(flux+"--avant em");
//                em = DAOFactory.getInstance().getEntityManager();
//                em.getTransaction().begin();
//                logger.debug(flux+"--après em");
//                
//                if (!em.contains(flux)) { // Si le flux n'est pas contenu dans l'em, on le recharge...
//                    logger.debug("em find");
//                    flux = em.find(Flux.class, flux.getID());
//                    
//                }
//                logger.debug("après if");
//
//                em.lock(this.flux, LockModeType.PESSIMISTIC_WRITE); // On block le flux en ecriture pour toute la durée de la transaction
//                
//                if(flux.getJournalLie()!=null && flux.getJournalLie().getID()!=null){
//                    Journal j = em.find(Journal.class, flux.getJournalLie().getID());
//                    System.out.println("JOURNAL : " + j);
////                    em.lock(j, LockModeType.PESSIMISTIC_WRITE);
//                    em.lock(j, LockModeType.PESSIMISTIC_READ);
//                }
//
//                MediatorCollecteAction cloneComportement = this.flux.getMediatorFlux().genererClone(); //On crée une copie du mediator devant être employé. Cela permet de faire travailler plusieurs flux avec le même modèle de Comportement
//                this.comportementDuFlux = cloneComportement;
//                logger.debug("clonageok");
//                
//                nouvellesItems = cloneComportement.executeActions(this.flux); // On exécute la collecte en utilisant le Comportement de Collecte cloné
//                logger.debug("Action eecuté");
//
//                //On enregistre chaque item trouvé
//                ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
//                for (int i = 0; i < nouvellesItems.size(); i++) {
//                    logger.debug("ajout");
//                    Item item = nouvellesItems.get(i);
//                    
//                    collecteur.ajouterItemAuFlux(flux, item, em, false); // Il faut préciser au collecteur l'em qu'il doit utiliser, on lui donne celui qui block actuellement le flux. Les enregistrements ne sont alors pas encore commités
//                    
//                }
//            }
//            logger.debug("fin try");
//        } catch (Exception e) {
//            logger.debug("ree", e);
//            this.exeption = e; // On copi l'exeption qui vient de survenir dans le blocktry plus haut a l'intérieur de l'objet présent
//        } finally { // Dans tous les cas
//            try {
//                if (em != null && em.isOpen() && em.getTransaction().isActive()) { // Si l'em est actif et possède une transacrion ...
//                    em.getTransaction().commit(); // On commit les ajouts d'item au flux. Ce commit va aussi libérer la ressource flux en écriture
//                    em.close();
//                }
//                // Si le comit passe alors on peut ajouter tous les hash au cache
//                ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
//                if (nouvellesItems != null) {
//                    for (int i = 0; i < nouvellesItems.size(); i++) {
//                        Item item = nouvellesItems.get(i);
//                        collecteur.getCacheHashFlux().addHash(flux, item.getHashContenu());
//                    }
//                }
//
//                // Il faut aussi supprimer des hash si ils sont trop nombreux
//
//                // Il faut retirer des hash du cache 
//                if (this.getExeption() == null) {
//                    try {
//                        if (this.getComportementDuFlux().getDedoubloneur().getCompteCapture() != null) {
//                            Integer nbrItObserve = this.getComportementDuFlux().getDedoubloneur().getCompteCapture()[0];
//                            System.out.println("##############-----> NOMBRE D'item Observé : " + nbrItObserve);
//                            Integer nbrDsCache = collecteur.getCacheHashFlux().returnNbrHash(flux);
//                            if (nbrDsCache != null && nbrItObserve != null) {
//                                Integer nbrItASup = nbrDsCache - nbrItObserve - 100; // On en laisse 100 de marge 
//                                if (nbrItASup > 0) {
//                                    collecteur.getCacheHashFlux().removeXHash(nbrItASup, flux);
//                                }
//                            }
//                        }
//                    } catch (Exception e) {
//                        logger.debug("err", e);
//                    }
//                }
//                
//            } catch (Exception e) {
//                logger.error("Erreur lors du commit des flux", e); // Cette erreur ne devrait pas survenir. On recevra un mail si c'est le cas grace a l'appender de Log4J
//            }
//            setChanged(); // Notification du service (ServiceCollecteur)
//            notifyObservers();
//            return this; // La tache se retourne a l'appelant
//        }
//    }
    /**
     * *
     * Méthode permettant de gérer un incident lorsque la tâche ne s'est pas déroulé correctement. C'est le role du
     * service de lancer cette méthode
     */
    @Override
    public synchronized void gererIncident() throws InstantiationException, IllegalAccessException, UnIncidableException, Exception {
        logger.debug("flux--" + flux + "-- Gestion incident");
        try {
            if (this.exeption != null) { // Avant toute chose on s'assure qu'il y a bien eu une Exception pour la tâche
                CollecteIncident collecteIncident = (CollecteIncident) incident; // Un simple cast

                em = DAOFactory.getInstance().getEntityManager(); // Pour cette transaction, on veut un nouvel entity manager
                em.getTransaction().begin(); // On démarre une transaction

                // On cherche si le flux avait déjà des incidents ouvert
                DAOIncident daoIncident = (DAOIncident) DAOFactory.getInstance().getDaoFromType(CollecteIncident.class);
                daoIncident.setEm(em);
                List<CollecteIncident> listIncidentOuvert = daoIncident.findIncidentOuvert(flux.getID());

                // Si On observe déjà un incident 
                if (listIncidentOuvert != null && listIncidentOuvert.size() == 1) {
                    collecteIncident = listIncidentOuvert.get(0);
//                     collecteIncident = (CollecteIncident) daoIncident.find(collecteIncident.getID());
                    // On block l'incident
                    try {
                        em.lock(collecteIncident, LockModeType.PESSIMISTIC_WRITE); // Si le flux possédait déjà un incident, alors on va le blocker en écriture le temps de la transaction
                    } catch (Exception e) {
                        logger.debug("erreur lors du lock ", e);
                    }

                } else if (listIncidentOuvert != null && listIncidentOuvert.isEmpty()) { // Si il n'y a pas d'incident, il faut en créer un
                    IncidentFactory factory = new IncidentFactory();
                    collecteIncident = (CollecteIncident) factory.createIncidentFromTask(this, this.exeption.toString());
                }

                if (collecteIncident != null) { // Si on a un incident alors on va incrémenter son compteur et ajouter des infos comme le flux responsable
                    Integer repetition = collecteIncident.getNombreTentativeEnEchec();
                    repetition++;
                    collecteIncident.setNombreTentativeEnEchec(repetition);
                    collecteIncident.setFluxLie(flux);

                    this.setIncident(collecteIncident);

                    //-------ENREGISTREMENT ou modification de l'incident-----------

                    ServiceCRUDFactory cRUDFactory = ServiceCRUDFactory.getInstance();
                    AbstrServiceCRUD service = cRUDFactory.getServiceFor(CollecteIncident.class);

                    if (collecteIncident.getID() == null) {
                        service.ajouter(incident, em);
                    } else {
                        System.out.println("else --> oN modif");
                        service.modifier(incident, em);
                    }
                }
            }
        } catch (Exception e) {
//            logger.error("Erreur lors de la génération de l'incident ", e); // Ca ne devrait pas arriver Log4J nous préviendra si c'est le cas
            throw e; // >> C'est le service qui doit afficher l'erreur
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();// On commit l'ajout ou la modification. Ce commit va aussi provoquer la libération de la ressource en écriture
            }
        }
    }

    @Override
    public void fermetureIncident() throws Exception {
        logger.debug("cloture");
        //Si la tâche s'est déroulé correctement
        if (this.exeption == null) {
            // On récupère les incident du flux 
            try {
                em = DAOFactory.getInstance().getEntityManager();
                DAOIncident dao = (DAOIncident) DAOFactory.getInstance().getDaoFromType(CollecteIncident.class);
                dao.setEm(em);
                List<CollecteIncident> listIncid = dao.findIncidentOuvert(flux.getID());


                em.getTransaction().begin();
                ServiceCRUDFactory cRUDFactory = ServiceCRUDFactory.getInstance();
                AbstrServiceCRUD service = cRUDFactory.getServiceFor(CollecteIncident.class);


                for (int i = 0; i < listIncid.size(); i++) {
                    System.out.println("IT");
                    System.out.println("########################");
                    CollecteIncident abstrIncident = listIncid.get(i);
                    // On doit le vérouiller
                    em.lock(abstrIncident, LockModeType.PESSIMISTIC_WRITE);
                    abstrIncident.setDateFin(new Date());

                    service.modifier(abstrIncident, em); // On utilise le service pour modifier le beans

//                            em.merge(abstrIncident);
                }
            } catch (Exception e) {
                logger.debug("BUG A LA FERMETURE ", e);
                throw e;
            } finally {
                if (em != null && em.isOpen() && em.getTransaction().isActive()) {
                    em.getTransaction().commit();
                }
            }
        }
    }

    public List<Item> getNouvellesItems() {
        return nouvellesItems;
    }

    public void setNouvellesItems(List<Item> nouvellesItems) {
        this.nouvellesItems = nouvellesItems;
    }

    public Flux getFlux() {
        return flux;
    }

    public void setFlux(Flux flux) {
        this.flux = flux;
    }

    public Date getDateDerniereRecup() {
        return DateDerniereRecup;
    }

    public void setDateDerniereRecup(Date DateDerniereRecup) {
        this.DateDerniereRecup = DateDerniereRecup;
    }

    public AbstrIncident getIncident() {
        return incident;
    }

    public void setIncident(AbstrIncident incident) {
        this.incident = incident;
    }

    /**
     * *
     * Indique si cette tache est une tache schedulé devant être rajouté à la fin de son traitment dans le pool
     * schedulé. Si false, c'est une tâche manuelle, elle ne sera pas réajouté
     *
     * @return
     */
    public Boolean getTacheSchedule() {
        return tacheSchedule;
    }

    /**
     * *
     * Indique si cette tache est une tache schedulé devant être rajouté à la fin de son traitment dans le pool
     * schedulé. Si false, c'est une tâche manuelle, elle ne sera pas réajouté
     *
     * @param tacheSchedule
     */
    public void setTacheSchedule(Boolean tacheSchedule) {
        this.tacheSchedule = tacheSchedule;
    }

    /**
     * *
     * Permet d'annuler la tâche. Lors de son déclanchement, elle ne va rien faire et ne plus être ajouté au scheduler
     *
     * @return
     */
    public Boolean getAnnulerTache() {
        return annulerTache;
    }

    /**
     * *
     * Permet d'annuler la tâche. Lors de son déclanchement, elle ne va rien faire et ne plus être ajouté au scheduler
     *
     * @param annulerTache
     */
    public void setAnnulerTache(Boolean annulerTache) {
        this.annulerTache = annulerTache;
    }

//    @Override
//    public void fermerLesIncidentOuvert() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public AbstrIncident getIncidenOuvert() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public Class getTypeIncident() {
        return CollecteIncident.class;
    }

    public MediatorCollecteAction getComportementDuFlux() {
        return comportementDuFlux;
    }

    public void setComportementDuFlux(MediatorCollecteAction comportementDuFlux) {
        this.comportementDuFlux = comportementDuFlux;
    }

    @Override
    protected TacheRecupCallable callFinalyse() {


        try {
            if (em != null && em.isOpen() && em.getTransaction().isActive()) { // Si l'em est actif et possède une transacrion ...
                em.getTransaction().commit(); // On commit les ajouts d'item au flux. Ce commit va aussi libérer la ressource flux en écriture
                em.close();
            }


            // Il faut aussi supprimer des hash si ils sont trop nombreux

            // Il faut retirer des hash du cache 
            if (this.getExeption() == null) {

                // Si le comit passe alors on peut ajouter tous les hash au cache
                ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
                if (nouvellesItems != null) {
                    for (int i = 0; i < nouvellesItems.size(); i++) {
                        Item item = nouvellesItems.get(i);
                        collecteur.getCacheHashFlux().addHash(flux, item.getHashContenu());
                    }
                }


                try {
                    if (this.getComportementDuFlux().getDedoubloneur().getCompteCapture() != null) {
                        Integer nbrItObserve = this.getComportementDuFlux().getDedoubloneur().getCompteCapture()[0];
                        Integer nbrDsCache = collecteur.getCacheHashFlux().returnNbrHash(flux);
                        if (nbrDsCache != null && nbrItObserve != null) {
                            Integer nbrItASup = nbrDsCache - nbrItObserve - 100; // On en laisse 100 de marge 
                            if (nbrItASup > 0) {
                                collecteur.getCacheHashFlux().removeXHash(nbrItASup, flux);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.debug("err", e);
                }
            }

        } catch (Exception e) {
            logger.error("Erreur lors du commit des flux", e); // Cette erreur ne devrait pas survenir. On recevra un mail si c'est le cas grace a l'appender de Log4J
        }
        return (TacheRecupCallable) super.callFinalyse();

//            setChanged(); // Notification du service (ServiceCollecteur)
//            notifyObservers();
//            return this; // La tache se retourne a l'appelant
    }

    @Override
    protected void callCorps() throws Exception {

//                    Thread.sleep(3000);
        if (!flux.getActive()) {
            throw new CollecteUnactiveFlux("Ce flux doit être activé pour être récolté");
        }

//            if (!annulerTache) {  // Si la tache n'a pas été annulé elle se déroule normalement.
        em = DAOFactory.getInstance().getEntityManager();
        em.getTransaction().begin();

        this.verrouillerObjectDansLEM(flux, LockModeType.PESSIMISTIC_WRITE);


        if (flux.getJournalLie() != null && flux.getJournalLie().getID() != null) {
            verrouillerObjectDansLEM(flux.getJournalLie(), LockModeType.PESSIMISTIC_READ);
        }


        MediatorCollecteAction cloneComportement = this.flux.getMediatorFlux().genererClone(); //On crée une copie du mediator devant être employé. Cela permet de faire travailler plusieurs flux avec le même modèle de Comportement
        this.comportementDuFlux = cloneComportement;

        nouvellesItems = cloneComportement.executeActions(this.flux); // On exécute la collecte en utilisant le Comportement de Collecte cloné


        //On enregistre chaque item trouvé
        ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
        for (int i = 0; i < nouvellesItems.size(); i++) {

            Item item = nouvellesItems.get(i);
            if (item.getDonneeBrutes().size() > 1) {
                System.out.println("ITEM ID : " + item.getID());
                System.out.println("---> BRUT : " + item.getDonneeBrutes().size());
            }

            collecteur.ajouterItemAuFlux(flux, item, em, false); // Il faut préciser au collecteur l'em qu'il doit utiliser, on lui donne celui qui block actuellement le flux. Les enregistrements ne sont alors pas encore commités

        }

    }
}
