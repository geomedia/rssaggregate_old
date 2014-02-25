/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import javax.persistence.LockModeType;
import javax.xml.ws.http.HTTPException;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.exception.CollecteUnactiveFlux;
import rssagregator.beans.exception.UnIncidableException;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.incident.RecupIncident;
import rssagregator.utils.comparator.ComparatorBean;
import rssagregator.beans.traitement.VisitorHTTP;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.services.SemaphoreCentre;
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
     * Pointe le visiteur utilisé pour effectuer la collecte. Le visitor est l'objet permettant d'effectuer la collecte
     *
     * @see VisitorHTTP
     */
    VisitorHTTP visitorHTTP;

    /**
     * *
     * Méthode permettant de gérer un incident lorsque la tâche ne s'est pas déroulé correctement. C'est le role du
     * service de lancer cette méthode
     */
    @Override
    public synchronized void gererIncident() throws InstantiationException, IllegalAccessException, UnIncidableException, Exception {
        try {
            
//            System.out.println("type " + incident.getClass());
            if (this.exeption != null) { // Avant toute chose on s'assure qu'il y a bien eu une Exception pour la tâche
                RecupIncident collecteIncident = (RecupIncident) incident; // Un simple cast

                initialiserTransaction();

                // On cherche si le flux avait déjà des incidents ouvert
                DAOIncident daoIncident = (DAOIncident) DAOFactory.getInstance().getDaoFromType(RecupIncident.class);
                daoIncident.setEm(em);
                List<RecupIncident> listIncidentOuvert = daoIncident.findIncidentOuvert(flux.getID());

                // Si On observe déjà un incident 


                if (listIncidentOuvert != null && listIncidentOuvert.size() == 1) {
                    collecteIncident = listIncidentOuvert.get(0);
//                     collecteIncident = (CollecteIncident) daoIncident.find(collecteIncident.getID());
                    // On block l'incident

                    verrouillerObjectDansLEM(collecteIncident, LockModeType.PESSIMISTIC_WRITE);

                } else if (listIncidentOuvert != null && listIncidentOuvert.isEmpty()) { // Si il n'y a pas d'incident, il faut en créer un
                    IncidentFactory factory = new IncidentFactory();


                   
                    String msg = "";
                    //Construction du message a destination de l'utilisateur


                    if (exeption.getClass().equals(java.util.concurrent.ExecutionException.class)) {
                        ExecutionException cast = (ExecutionException) exeption;
                        msg = constructionMessageErreurDepuisExc(exeption.getCause());
                    }
                    else {
                        msg = constructionMessageErreurDepuisExc(exeption);
                    }

                    collecteIncident = (RecupIncident) factory.createIncidentFromTask(this, msg);
                    collecteIncident.setNombreTentativeEnEchec(0);
                }

                if (collecteIncident != null) { // Si on a un incident alors on va incrémenter son compteur et ajouter des infos comme le flux responsable
                    Integer repetition = collecteIncident.getNombreTentativeEnEchec();
                    repetition++;
                    collecteIncident.setNombreTentativeEnEchec(repetition);
                    collecteIncident.setFluxLie(flux);

                    this.setIncident(collecteIncident);

                    //-------ENREGISTREMENT ou modification de l'incident-----------

                    ServiceCRUDFactory cRUDFactory = ServiceCRUDFactory.getInstance();
                    AbstrServiceCRUD service = cRUDFactory.getServiceFor(RecupIncident.class);

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
                DAOIncident dao = (DAOIncident) DAOFactory.getInstance().getDaoFromType(RecupIncident.class);
                dao.setEm(em);
                List<RecupIncident> listIncid = dao.findIncidentOuvert(flux.getID());


                ServiceCRUDFactory cRUDFactory = ServiceCRUDFactory.getInstance();
                AbstrServiceCRUD serviceCrud = cRUDFactory.getServiceFor(RecupIncident.class);


                for (int i = 0; i < listIncid.size(); i++) {
                    RecupIncident abstrIncident = listIncid.get(i);
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

    /**
     * *
     * Retourne la classe CollecteIncident
     *
     * @return
     */
    @Override
    public Class getTypeIncident() {
        return RecupIncident.class;
    }

    @Override
    protected synchronized TacheRecupCallable callFinalyse() {
        logger.debug("" + this + " bloc Finalyse");
        try {
            if (this.exeption == null) {
                logger.debug("Exeption null");
                commitTransaction(true);
                // Si le comit passe alors on peut ajouter tous les hash au cache
                ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
                if (nouvellesItems != null) {
                    for (int i = 0; i < nouvellesItems.size(); i++) {
                        Item item = nouvellesItems.get(i);
                        collecteur.getCacheHashFlux().addHash(flux, item.getHashContenu());
                    }
                }


                try { // Suppression de hash afin d'éviter l'accumulation en mémoire

                    if (visitorHTTP != null && visitorHTTP.getNbItTrouve() > 0) {
//                    if (this.comportementDuFlux.getNbItTrouve() > 0) {

                        short nbrItObserve = visitorHTTP.getNbItTrouve();

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
            } else { // Si il y a eu des erreur on roolback
                commitTransaction(false);
            }

        } catch (Exception e) {
            logger.error("Erreur sur le flux " + flux, e); // Cette erreur ne devrait pas survenir. On recevra un mail si c'est le cas grace a l'appender de Log4J
        } finally {
        }
//        flux = null;
        return (TacheRecupCallable) super.callFinalyse();
    }

    @Override
    protected void callCorps() throws InterruptedException, Exception {
        if (!flux.getActive()) {
            throw new CollecteUnactiveFlux("Ce flux doit être activé pour être récolté");
        }

        initialiserTransaction();
////         Si le flux appartient a un journal, il faut verrouiller le journal afin d'éviter que plusieurs tache collecte les donnes d'un même journal en meme temps
//        if (flux.getJournalLie() != null && flux.getJournalLie().getID() != null) {
//            verrouillerObjectDansLEM(flux.getJournalLie(), LockModeType.PESSIMISTIC_WRITE);
//////            sema = SemaphoreCentre.getinstance().returnSemaphoreForRessource(flux.getJournalLie());
//////            sema.acquire();
//        }
//        this.verrouillerObjectDansLEM(flux, LockModeType.PESSIMISTIC_WRITE);

        visitorHTTP = new VisitorHTTP();
        visitorHTTP.visit(flux);

        ThreadUtils.interruptCheck(); // On lance l'execution si la thread n'est pas déjà interrompu
        nouvellesItems = visitorHTTP.getListItem();

        ThreadUtils.interruptCheck();

        //On enregistre chaque item trouvé
        ServiceCollecteur collecteur = ServiceCollecteur.getInstance();

        //Pour éviter les dead lock il faut respecter un ordre dans la facon de poser les verrour (c'est le collecteur qui pose le verrour). On va simplement trier les items par ID

        Collections.sort(nouvellesItems, new ComparatorBean());

        for (int i = 0; i < nouvellesItems.size(); i++) {
            Item item = nouvellesItems.get(i);
            collecteur.ajouterItemAuFlux(flux, item, em, false, visitorHTTP); // Il faut préciser au collecteur l'em qu'il doit utiliser, on lui donne celui qui block actuellement le flux. Les enregistrements ne sont alors pas encore commités
        }
        
    }

    /**
     * *
     *
     * @throws InterruptedException
     * @throws Exception
     */
    @Override
    public void initEmAndLockRessources() throws InterruptedException, Exception {
        super.initEmAndLockRessources(); //To change body of generated methods, choose Tools | Templates.

        initialiserTransaction();
        logger.debug("Init task " + this);

        if (flux != null && flux.getJournalLie() != null) {
            verrouillerObjectDansLEM(flux.getJournalLie(), LockModeType.PESSIMISTIC_WRITE);

        }

        if (flux != null) {
            verrouillerObjectDansLEM(flux, LockModeType.PESSIMISTIC_WRITE);
        }

//        if(sema != null){
//            sema.acquire();
//            logger.debug("Semaphore acquise "+ this );
//        }
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

    /**
     * *
     * Retourne un set de semaphore devant être acquis (dans l'ordre du set) avant de lancer la tache. Il s'agit du
     * journal si il existe puis du flux
     *
     * @return
     */
    @Override
    public Set<Semaphore> returnSemSet() {
        sem.clear(); // On vide la map pour le reconstruire car les semaphore on pu changer
        if (this.flux != null && this.flux.getJournalLie() != null) {
            Semaphore s;

            try {
                s = SemaphoreCentre.getinstance().returnSemaphoreForRessource(this.flux.getJournalLie());
                sem.add(s);
            } catch (Exception ex) {
                logger.debug("Exception  ", ex);
            }

        }

        if (this.flux != null) {
            try {
                Semaphore s = SemaphoreCentre.getinstance().returnSemaphoreForRessource(this.flux);
                sem.add(s);
            } catch (Exception e) {
                logger.debug("Exception", e);
            }

        }
        return sem;
    }

    /**
     * *
     * @see #visitorHTTP
     * @return
     */
    public VisitorHTTP getVisitorHTTP() {
        return visitorHTTP;
    }

    /**
     * *
     * @see #visitorHTTP
     * @return
     */
    public void setVisitorHTTP(VisitorHTTP visitorHTTP) {
        this.visitorHTTP = visitorHTTP;
    }

    /**
     * *
     * Construit un message d'erreur a stocker dans l'incident a partir du type de l'excpetion
     */
    private String constructionMessageErreurDepuisExc(Throwable exception) {
        // Gestion de HTTPExeption
        
        
        if (exception instanceof HTTPException) {
            HTTPException ex = (HTTPException) exception;
            return "HTTPException : Le serveur est joingnable mais retour d'un code erreur : " + ex.getStatusCode();

        } // URL MAL FORMATE
        else if (exception instanceof UnknownHostException) {
            return "UnknownHostException : Il est impossible de joindre l'host du flux. Vérifiez l'adresse dans la fiche du flux. Le serveur n'a pas pu être join...";
        } else if (exception.getClass().equals(ParsingFeedException.class)) {
                    return "ParsingFeedException : Impossible de parser le flux XML. La page demandée est joignable mais le contenu trouvé n'a pas pu être interprété. Vérifiez à la main le contenu trouvable a l'url du flux. Il se peut que le journal ait supprimé le flux pour l'adresse mentionné (obtention d'une page erreur 404 ?).";
        } else if (exception instanceof FeedException) { // Erreur de parsage du flux
            return "FeedException : Impossible de parser le flux XML. Erreur :";
        }
         return "Pas de message pour ce type d'exception, regardez les logs";
        
    }
}
