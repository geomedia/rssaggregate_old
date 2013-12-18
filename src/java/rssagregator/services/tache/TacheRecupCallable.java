/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.Date;
import java.util.List;
import java.util.Observer;
import javax.persistence.LockModeType;
import rssagregator.beans.DonneeBrute;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.exception.CollecteUnactiveFlux;
import rssagregator.beans.exception.UnIncidableException;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.crud.AbstrServiceCRUD;
import rssagregator.services.crud.ServiceCRUDFactory;
import rssagregator.utils.ThreadUtils;

/**
 * La tâche permettant au {@link Flux} d'être collecté périodiquement. Elle est gérée par le service
 * {@link ServiceCollecteur}
 *
 * @author clem
 */
public class TacheRecupCallable extends TacheImpl<TacheRecupCallable> implements Incidable, TacheActionableSurUnBean {




    
    
    
//    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheRecupCallable.class);
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
     * Le boolenn persit indique si la tâche doit ou non enregistrer ses données dans la base de données
     */
//    Boolean persit;
//    /**
//     * *
//     * Permet d'annuler la tâche. Lors de son déclanchement, elle ne va rien faire et ne plus être ajouté au scheduler
//     */
//    Boolean annulerTache;
    /**
     * *
     * Un pointeur permettant de retrouver le comportement utilisé (un clone de celui servant de modèle dans le flux)
     */
    MediatorCollecteAction comportementDuFlux;

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

                initialiserTransaction();

                // On cherche si le flux avait déjà des incidents ouvert
                DAOIncident daoIncident = (DAOIncident) DAOFactory.getInstance().getDaoFromType(CollecteIncident.class);
                daoIncident.setEm(em);
                List<CollecteIncident> listIncidentOuvert = daoIncident.findIncidentOuvert(flux.getID());

                // Si On observe déjà un incident 
                if (listIncidentOuvert != null && listIncidentOuvert.size() == 1) {
                    collecteIncident = listIncidentOuvert.get(0);
//                     collecteIncident = (CollecteIncident) daoIncident.find(collecteIncident.getID());
                    // On block l'incident

                    verrouillerObjectDansLEM(collecteIncident, LockModeType.PESSIMISTIC_WRITE);

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
                        service.modifier(incident, em);
                    }
                }
            }
        } catch (Exception e) {
//            logger.error("Erreur lors de la génération de l'incident ", e); // Ca ne devrait pas arriver Log4J nous préviendra si c'est le cas
            commitTransaction(false);
            throw e; // >> C'est le service qui doit afficher l'erreur
        } finally {
            commitTransaction(true);
        }
    }

    @Override
    public synchronized void fermetureIncident() throws Exception {
        //Si la tâche s'est déroulé correctement
        if (this.exeption == null) {
            // On récupère les incident du flux 
            try {
                initialiserTransaction();

//                em = DAOFactory.getInstance().getEntityManager();
                DAOIncident dao = (DAOIncident) DAOFactory.getInstance().getDaoFromType(CollecteIncident.class);
                dao.setEm(em);
                List<CollecteIncident> listIncid = dao.findIncidentOuvert(flux.getID());


                ServiceCRUDFactory cRUDFactory = ServiceCRUDFactory.getInstance();
                AbstrServiceCRUD serviceCrud = cRUDFactory.getServiceFor(CollecteIncident.class);


                for (int i = 0; i < listIncid.size(); i++) {
                    CollecteIncident abstrIncident = listIncid.get(i);
                    // On doit le vérouiller
                    em.lock(abstrIncident, LockModeType.PESSIMISTIC_WRITE);
                    abstrIncident.setDateFin(new Date());

                    serviceCrud.modifier(abstrIncident, em); // On utilise le service pour modifier le beans

//                            em.merge(abstrIncident);
                }
            } catch (Exception e) {
                logger.debug("BUG A LA FERMETURE ", e);
                throw e;
            } finally {
                commitTransaction(true);
            }
        }
    }


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
    protected synchronized TacheRecupCallable callFinalyse() {
        try {

            // Il faut aussi supprimer des hash si ils sont trop nombreux

     
            if (this.getExeption() == null) {
                commitTransaction(true);
                // Si le comit passe alors on peut ajouter tous les hash au cache
                ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
                if (nouvellesItems != null) {
                    for (int i = 0; i < nouvellesItems.size(); i++) {
                        Item item = nouvellesItems.get(i);
                        collecteur.getCacheHashFlux().addHash(flux, item.getHashContenu());
                        for (int j = 0; j < item.getDonneeBrutes().size(); j++) {
                            DonneeBrute donneesBrutes = item.getDonneeBrutes().get(j);
                            collecteur.getCacheHashFlux().addHash(flux, donneesBrutes.getHashContenu());

                        }
                    }
                }


                try { // Suppression de hash afin d'éviter l'accumulation en mémoire
                    if (this.comportementDuFlux.getNbItTrouve() > 0) {
                        short nbrItObserve = this.comportementDuFlux.getNbItTrouve();
                        short nbrDsCache = collecteur.getCacheHashFlux().returnNbrHash(flux).shortValue();
                        if (nbrDsCache > (nbrItObserve + 500)) { // Si le nombre d'item dans le cache est supérieur au nombre d'item obs + 500 
                            Integer nbrItASup = nbrDsCache - nbrItObserve - 500; // On en laisse 100 de marge 
                            if (nbrItASup > 0) {
                                collecteur.getCacheHashFlux().removeXHash(nbrItASup, flux);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.debug("err", e);
                }
            }
            else{
                System.out.println("ROOL BACK");
                commitTransaction(false); // Si il y a eu des erreur on roolback
            }

        } catch (Exception e) {
            logger.error("Erreur sur le flux " + flux, e); // Cette erreur ne devrait pas survenir. On recevra un mail si c'est le cas grace a l'appender de Log4J
        }
        return (TacheRecupCallable) super.callFinalyse();

//            setChanged(); // Notification du service (ServiceCollecteur)
//            notifyObservers();
//            return this; // La tache se retourne a l'appelant
    }

    @Override
    protected void callCorps() throws InterruptedException, Exception {    
        
        if (!flux.getActive()) {
            throw new CollecteUnactiveFlux("Ce flux doit être activé pour être récolté");
        }

        initialiserTransaction();
        
  
        
//        Thread.sleep(300000);
        

        this.verrouillerObjectDansLEM(flux, LockModeType.PESSIMISTIC_WRITE);


        // Si le flux appartient a un journal, il faut verrouiller le journal afin d'éviter que plusieurs tache collecte les donnes d'un même journal en meme temps
        if (flux.getJournalLie() != null && flux.getJournalLie().getID() != null) {
            verrouillerObjectDansLEM(flux.getJournalLie(), LockModeType.PESSIMISTIC_READ);
        }
                   

        MediatorCollecteAction cloneComportement = this.flux.getMediatorFlux().genererClone(); //On crée une copie du mediator devant être employé. Cela permet de faire travailler plusieurs flux avec le même modèle de Comportement
        this.comportementDuFlux = cloneComportement;
        
        ThreadUtils.interruptCheck(); // On lance l'execution si la thread n'est pas déjà interrompu

        nouvellesItems = cloneComportement.executeActions(this.flux); // On exécute la collecte en utilisant le Comportement de Collecte cloné

        
        ThreadUtils.interruptCheck(); 
        
        //On enregistre chaque item trouvé
        ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
        for (int i = 0; i < nouvellesItems.size(); i++) {
            Item item = nouvellesItems.get(i);
            collecteur.ajouterItemAuFlux(flux, item, em, false, cloneComportement); // Il faut préciser au collecteur l'em qu'il doit utiliser, on lui donne celui qui block actuellement le flux. Les enregistrements ne sont alors pas encore commités
            System.out.println("Et dans la tache l'item a " + item.getDonneeBrutes().size());
        
        }

        logger.debug("Recup du Flux " + flux.getID() + " " + flux + "\n Découverte : " + cloneComportement.getNbrItemCollecte() + "; Mem Dedoub :" + cloneComportement.getNbDedoubMemoire() + "; dedoubBDD" + cloneComportement.getNbDedoubBdd() + "; interneFlux : " + cloneComportement.getNbDoublonInterneAuflux() + "; Liaison : " + cloneComportement.getNbLiaisonCree() + "; it crée : " + cloneComportement.getNbNouvelle());
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



    @Override
    protected void finalize() throws Throwable {
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object returnBeanCible() {
        return flux;
    }

    @Override
    public String toString() {
        return "TacheRecupCallable{" + "flux=" + flux + ", DateDerniereRecup=" + DateDerniereRecup + ", incident=" + incident + '}';
    }
    
    
    
    
    
    
}
