/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TransactionRequiredException;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.Journal;
import rssagregator.beans.exception.DonneeInterneCoherente;
import rssagregator.beans.exception.IncompleteBeanExeption;
import rssagregator.beans.traitement.VisitorHTTP;
import rssagregator.dao.CacheHashFlux;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import rssagregator.dao.DaoItem;
import rssagregator.dao.DaoJournal;
import rssagregator.services.tache.TacheDecouverteAjoutFlux;
import rssagregator.services.tache.TacheFactory;
import rssagregator.services.tache.TacheRecupCallable;
import rssagregator.services.tache.AbstrTache;
import rssagregator.services.tache.TacheRaffiner2;

/**
 * Cette classe permet d'instancier le service de collecte du projet. Elle est organisée autours de deux objets
 * priomordiaux : le pool de tache schedulé qui permet de lancer périodiquement les tache lié aux flux ; et le pool de
 * tache manuelle qui permet annectodiquement de lancer la mise à jour des flux
 *
 * @author clem
 */
public class ServiceCollecteur extends ServiceImpl {

//    ListeFluxCollecte fluxCollecte; On le récupère maintenant directement depuis le singleton de collecte
    /**
     * *
     * Instance du singleton récupérable par {@link #getInstance() }
     */
    private static ServiceCollecteur instance = new ServiceCollecteur();
    /**
     * *
     * Le cache du service de collecte parmettant de dédoublonner la majeur partie des items sans faire appel à la base
     * de données
     */
    private CacheHashFlux cacheHashFlux = CacheHashFlux.getInstance();
//    private CallableCollecteSubmiter collecteSubmiter = new CallableCollecteSubmiter();  --> N'est plus utilisé au profit du producteur consomateur de tache
    /**
     * *
     * Un pool dédié au raffinage des item;
     */
    private ExecutorService executorServiceRaffinage = Executors.newSingleThreadExecutor();

    /**
     * *
     * Cette map permet au service de retrouver les les tâche lié au flux. la clé de la map est l'id du flux (un
     * {@link Long}) la valeur est la tache récoltant le flux. C'est au service de maintenir cette map
     */
//    private Map<Long, List<AbstrTacheSchedule>> mapFluxTache = new HashMap<Long, List<AbstrTacheSchedule>>();
//    private Map<Long, TacheDecouverteAjoutFlux> mapJournalTache = new HashMap<Long, TacheDecouverteAjoutFlux>();
    /**
     * *
     * Constructeur du singleton
     */
    private ServiceCollecteur() {
        super();
        tacheProducteur = new TacheProducteurServiceCollecte(this); // Le service de collecte possède un tache producteur spécifique lui permettant de vérifier la collecte des flux péridiquement
        try {
            ThreadFactoryPrioitaire factoryPrioitaire = new ThreadFactoryPrioitaire();
            // Le nombre de thread doit être relevé dans la conf. 
            poolPrioritaire = Executors.newFixedThreadPool(5);

        } catch (ArithmeticException e) {
            logger.error("Impossible de charger le nombre de Thread pour ce service. Vérifier la conf", e);
        } catch (Exception e) {
            logger.error("Erreur lors de l'instanciation du service", e);
        }
    }

    /**
     * *
     * Ce service est in singleton.
     *
     * @return
     */
    public static ServiceCollecteur getInstance() {
        if (ServiceCollecteur.instance == null) {
            ServiceCollecteur.instance = new ServiceCollecteur();
        }
        return ServiceCollecteur.instance;
    }
    /**
     * *
     * Le pool de thread permettant de lancer des récupération de flux en passant devant le pool schedulé
     */
    private ExecutorService poolPrioritaire;

    /**
     * *
     * Tâche permettant de lancer la collecte d'un nouveau flux. Si le flux était déjà enregistré. Sa tache est détruite
     * et recrée
     *
     * @param f : le flux qu'il faut enregistrer auprès du collecteur
     */
    public synchronized void enregistrerFluxAupresDuService(Flux f) throws IncompleteBeanExeption {
        if (f == null) {
            throw new NullPointerException("le flux est null");
        }
        if (f.getID() == null) {
            throw new IncompleteBeanExeption("Il n'est pas possible d'enregistrer un flux à L'ID NULL");
        }

        if (f.getMediatorFlux() == null) {
            throw new IncompleteBeanExeption("Il n'est pas possible d'ajouter un flux ne possédant pas de Comportement de Collecte");
        }

        if (f.getMediatorFlux().getPeriodiciteCollecte() == null) {
            throw new IncompleteBeanExeption("Le comportement de collecte du flux ne permet pas de savoir la période de schedulation");
        }


        if (f.getActive()) {
            //On regarde si le flux est déjà enregistré dans la map. Il faut le supprimer si il est trouvé. On replacera ensuite de nouvelles taches

            cancelAndRemoveTaskFromAssociedWithBeans(f); // On supprime les taches qui aurait pu être associé au flux

            // Création des taches associées au flux
            TacheRecupCallable tache = (TacheRecupCallable) TacheFactory.getInstance().getNewTask(TacheRecupCallable.class, Boolean.TRUE);
            tache.setFlux(f);
            tache.setTimeSchedule(f.getMediatorFlux().getPeriodiciteCollecte());  // On définit son temsp de schedulation en fonction des paramettre de son comportement de collecte

            logger.debug("Enregistrement du flux " + f);
            this.tacheProducteur.produire(tache);
        }
    }

    /**
     * *
     * Enregistre un journal auprès du service de collecte. Le service de collecte va créer un tache périodique de
     * découverte des flux RSS pour le journal si celui ci est configuré pour
     *
     * @param j
     * @throws IncompleteBeanExeption
     */
    public synchronized void enregistrerJournalAupresduService(Journal j) throws IncompleteBeanExeption {
        if (j == null) {
            throw new NullPointerException("Le journal est null");
        }

        if (j.getID() == null) {
            throw new IncompleteBeanExeption("Le journal n'a pas d'ID");
        }

        // .....


        if (j.getAutoUpdateFlux()) { // Si le journal est configuré pour autodécouvrir ses flux.
            
            if(j.getPeriodiciteDecouverte() == null){
                throw new IncompleteBeanExeption("Le journal n'a pas de variable int PeriodiciteDecouverte");
            }
 
           AbstrTache entry = retriveTaskJournalDiscover(j);
            if (entry != null) {
                try {
                    entry.getFuture().cancel(true);
                } catch (Exception e) {
                    logger.debug("annulation de la tache de découverte des flux " + e);
                }
            }


            TacheDecouverteAjoutFlux tache = (TacheDecouverteAjoutFlux) TacheFactory.getInstance().getNewTask(TacheDecouverteAjoutFlux.class, true);

            tache.setJournal(j);
            tache.setPersist(true);
            tache.setActiverLesFLux(j.getActiverFluxDecouvert());
            tache.setNombredeSousTache(30); // TODO : doit être tiré du beans Journal
            tache.setSchedule(Boolean.TRUE);
            tache.setTimeSchedule(j.getPeriodiciteDecouverte());

            byte byt = 1;
            tache.setTypeSchedule(byt);



            this.tacheProducteur.produire(tache);
        }
    }

    public synchronized void retirerJournalDuService(Journal j) throws IncompleteBeanExeption {
        if (j == null) {
            throw new NullPointerException("Impossible de désactiver un journal null");
        }
        if (j.getID() == null) {
            throw new IncompleteBeanExeption("Le journal n'a pas d'ID");
        }

        // On annule l atache

        AbstrTache entry = retriveTaskJournalDiscover(j);
        if (entry != null) {
            // Annulation de la tache
            try {
                entry.getFuture().cancel(true);
            } catch (Exception e) {
                logger.debug("annulation", e);
            }

            // Suppression de la tache a l'intérieur de la liste des tache du service
            synchronized (tacheGereeParLeService) {
                tacheGereeParLeService.remove(entry);
//                mapTache.remove(entry.getKey());
            }


        }
    }

    /**
     * *
     * annule la tache pour le flux envoyé et supprime le flux de la {@link #mapFluxTache}. En bref, le flux est retirée
     * du service
     *
     * @param f
     * @throws IncompleteBeanExeption
     */
    public synchronized void retirerFluxDuService(Flux f) throws IncompleteBeanExeption {
        if (f == null) {
            throw new NullPointerException("le flux est null");
        }
        if (f.getID() == null) {
            throw new IncompleteBeanExeption("Il n'est pas possible d'enregistrer un flux à L'ID NULL");
        }

        // On annule la tache

//        List<AbstrTacheSchedule> listtacheDuFLux = this.mapFluxTache.get(f.getID());
//        if (listtacheDuFLux != null) {
//            for (int i = 0; i < listtacheDuFLux.size(); i++) {
//                AbstrTache abstrTacheSchedule = listtacheDuFLux.get(i);
//                try {
//                    abstrTacheSchedule.annuler();
//                } catch (Exception ex) {
//                    Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }


//            List< Entry<AbstrTacheSchedule, Future>> tacheDuBeans = retriveAllForBeans(f);


        cancelAndRemoveTaskFromAssociedWithBeans(f);
//            for (int i = 0; i < tacheDuBeans.size(); i++) {
//                Entry<AbstrTacheSchedule, Future> entry1 = tacheDuBeans.get(i);
//                AbstrTache tache = entry1.getKey();
//                Future futur = entry1.getValue();
//                  synchronized(mapTache){
//                      
//                      // On annule la tache
//                      try {
//                          futur.cancel(true);
//                      } catch (Exception e) {
//                      }
//                      
//                      // On supprime la tache
//                           mapTache.remove(tache);
//                        logger.debug("suppression de la tache");
//                    }
//            }


        try { // Suppression du cache associé au flux
            this.cacheHashFlux.removeFlux(f);
        } catch (Exception e) {
            logger.debug("Erreur : ", e);
        }

    }

    /**
     * *
     * Parcours la {@link #mapTache} du service Collecte et retrouve la tache correspondant au flux. Renvoi null si on
     * ne trouve pas de tache pour le flux.
     *
     * @param f
     * @return une entry avec la tache et son futur. ou null.
     */
//    public Entry<AbstrTacheSchedule, Future> retriveTaskCollecte(Flux f) throws IncompleteBeanExeption {
//
//        if (f == null) {
//            throw new NullPointerException("Le flux envoyé en arguement est null");
//        }
//        if (f.getID() == null) {
//            throw new IncompleteBeanExeption("Le flux envoyé en arguement n'a pas d'ID");
//        }
//
//
//        for (Entry<AbstrTacheSchedule, Future> entry : mapTache.entrySet()) {
//            AbstrTache abstrTacheSchedule = entry.getKey();
//            Future future = entry.getValue();
//
//            // Si c'est une tache de récup
//            if (abstrTacheSchedule.getClass().equals(TacheRecupCallable.class)) {
//                TacheRecupCallable tacherecup = (TacheRecupCallable) abstrTacheSchedule;
//                if (tacherecup.getFlux().getID().equals(f.getID())) {
//                    return entry;
//                }
//            }
//        }
//        return null;
//    }
    /**
     * **
     * Retrouve la tache de découverte de flux associé au journal envoyé en argument parmis la map des tâches propres au
     * service.
     *
     * @param j le journal pour lequel il faut effectuer la recherche.
     * @return Une entry comprenant le callable ainsi que le future
     * @throws IncompleteBeanExeption Si le journal envoyé en argumetn n'a pas d'id
     */
    public AbstrTache retriveTaskJournalDiscover(Journal j) throws IncompleteBeanExeption {

        if (j == null) {
            throw new NullPointerException("Le journal est null");
        }
        if (j.getID() == null) {
            throw new IncompleteBeanExeption("le journal envoyé n'a pas d'id");
        }

        for (int i = 0; i < tacheGereeParLeService.size(); i++) {
            AbstrTache abstrTacheSchedule = tacheGereeParLeService.get(i);
//            
            Future future = abstrTacheSchedule.getFuture();

            if (abstrTacheSchedule.getClass().equals(TacheDecouverteAjoutFlux.class)) {
                TacheDecouverteAjoutFlux cast = (TacheDecouverteAjoutFlux) abstrTacheSchedule;
                if (cast.getJournal().getID().equals(j.getID())) {
                    return abstrTacheSchedule;
                }
            }
        }
        return null;
    }

    /**
     * *
     * Stope le service de collecte en fermant proprement les deux pool de tâches de collecte
     */
//    public void stopCollecte() {
//        // Fermeture du scheduler
//        this.executorService.shutdownNow();
//        this.poolPrioritaire.shutdownNow();
//    }
    /**
     * *
     * Cette méthode n'est maintenant plus utilisée au profit de majManuellAll()
     *
     * @param flux
     * @throws Exception
     * @deprecated
     */
    @Deprecated
    public void majManuelle(Flux flux) throws Exception {


        TacheRecupCallable task = (TacheRecupCallable) TacheFactory.getInstance().getNewTask(TacheRecupCallable.class, false);
        task.setFlux(flux);
//        TacheRecupCallable task = new TacheRecupCallable(flux, this, false);


        Future<TacheRecupCallable> t = this.poolPrioritaire.submit(task);

        t.get(30, TimeUnit.SECONDS);
        // A la fin de la tache, il faut rafraichir le context objet et la base de donnée.
//            DAOFactory.getInstance().getEntityManager().refresh(flux);
    }

    /**
     * *
     * Cette méthode lance la mise à jour manuelle de chacun des flux envoyés en parametres. Attends les résultat et
     * renvoi la liste des tâche executées pour la récupération.
     *
     * @param listFlux Liste de flux pour lequels il faut lancer une mise à jour manuelle
     * @throws Exception
     */
//    public List<TacheRecupCallable> majManuellAll(List<Flux> listFlux) throws Exception {
////        int i;
////
////        // Construction de la liste des tâches a soumettre
////        List<TacheRecupListFlux> listeRecupListFluxs = new ArrayList<TacheRecupListFlux>();
////        TacheRecupListFlux recupDesSansJournaux = new TacheRecupListFlux();
////
////        List<TacheRecupCallable> taches = new ArrayList<TacheRecupCallable>();
////        for (i = 0; i < listFlux.size(); i++) {
////
////            Flux f = listFlux.get(i);
////
////
////            TacheRecupCallable task = (TacheRecupCallable) TacheFactory.getInstance().getNewTask(TacheRecupCallable.class, Boolean.FALSE);
////            task.setFlux(listFlux.get(i));
////            taches.add(task);
////
////            // Si le flux n'a pas de journal
////            if (f.getJournalLie() == null) {
////                recupDesSansJournaux.getTaches().add(task);
////
////            } else {
////                boolean trouve = false;
////                for (int j = 0; j < listeRecupListFluxs.size(); j++) {
////                    TacheRecupListFlux tacheRecupList = listeRecupListFluxs.get(j);
////                    if (tacheRecupList.getJournalId().equals(f.getJournalLie().getID())) {
////                        trouve = true;
////                        tacheRecupList.getTaches().add(task);
////                    }
////                }
////
////                if (!trouve) {
////                    TacheRecupListFlux trlf = new TacheRecupListFlux();
////                    trlf.getTaches().add(task);
////                    listeRecupListFluxs.add(trlf);
////                }
////            }
////
//////            taches.add(task);
////        }
////
////        if (!recupDesSansJournaux.getTaches().isEmpty()) {
////            listeRecupListFluxs.add(recupDesSansJournaux);
////        }
////
////        ExecutorService es = Executors.newFixedThreadPool(10);
////
////        // On execute
////
////        for (int j = 0; j < listeRecupListFluxs.size(); j++) {
////            TacheRecupListFlux tacheJournal = listeRecupListFluxs.get(j);
////            es.submit(tacheJournal);
////
////        }
////
////
////        es.shutdown();
////
////
////
//////
//////        // Soumission des tâche dans le pool prioritairez
//////        List<Future> lf = new ArrayList<Future>();
//////        for (int j = 0; j < taches.size(); j++) {
//////            TacheRecupCallable tacheRecupCallable = taches.get(j);
//////            Future fut = poolPrioritaire.submit(tacheRecupCallable);
//////            lf.add(fut);
//////            addTask(tacheRecupCallable, fut);
//////        }
//////
//////
//////        // Attente des résultats
//////        for (int j = 0; j < lf.size(); j++) { // Il faut attendre la terminaison de toutes les tache envoyé
//////            Future future = lf.get(j);
//////            future.get();
//////        }
////
////        return taches;
//        return null;
//    }
    /**
     * *
     * Cette méthode lance la mise à jour manuelle de chacun des flux envoyés en parametres. Attends les résultat et
     * renvoi la liste des tâche executées pour la récupération.
     *
     * @param listFlux Liste de flux pour lequels il faut lancer une mise à jour manuelle
     * @throws Exception
     */
    public List<TacheRecupCallable> majManuellAll(List<Flux> listFlux) throws Exception {
        int i;

        // Construction de la liste des tâches a soumettre
        List<TacheRecupCallable> taches = new ArrayList<TacheRecupCallable>();
        for (i = 0; i < listFlux.size(); i++) {

            TacheRecupCallable task = (TacheRecupCallable) TacheFactory.getInstance().getNewTask(TacheRecupCallable.class, Boolean.FALSE);

            task.setFlux(listFlux.get(i));
            taches.add(task);
        }

        // Soumission des tâche dans le pool prioritaire
        for (int j = 0; j < taches.size(); j++) {
            TacheRecupCallable tacheRecupCallable = taches.get(j);
            tacheProducteur.produireMaintenant(tacheRecupCallable);
        }

        // On doit attendre que les tache disparaisse de la queue
        List<TacheRecupCallable> remainTask = new ArrayList<TacheRecupCallable>();
        remainTask.addAll(taches);

        Thread.sleep(1000);
        while (!remainTask.isEmpty()) {

            for (Iterator<TacheRecupCallable> it = remainTask.iterator(); it.hasNext();) {
                TacheRecupCallable tacheRecupCallable = it.next();

                if (tacheRecupCallable.getFuture() != null) {

                    Future fut = tacheRecupCallable.getFuture();
                    if (fut.isDone()) {
                        it.remove();
                    }
                }
            }
            Thread.sleep(500);
        }



//        // Attente des résultats
//        for (int j = 0; j < lf.size(); j++) { // Il faut attendre la terminaison de toutes les tache envoyé
//            Future future = lf.get(j);
//            try {
//                future.get();
//            } catch (Exception e) {
//                logger.debug("Erreur lors de la récupération manuelle du flux");
//
//            }
//
//        }

        return taches;
    }

    @Override
    public void update(Observable o, Object arg) {


        super.update(o, arg); //To change body of generated methods, choose Tools | Templates.

        // A la fin d'un tache récup on lance des tache de rafinage

//        if (o.getClass().equals(TacheRecupCallable.class)) { // Cette partie est désactivé pour le moement
//
//            TacheRecupCallable recupCallable = (TacheRecupCallable) o;
//            List<Item> listItem = recupCallable.getNouvellesItems();
//
//
////            List<Future> listFuture = new ArrayList<Future>();
//            for (int i = 0; i < listItem.size(); i++) {
//
//                Item item = listItem.get(i);
//                
//                TacheRaffiner2 raffiner2 = (TacheRaffiner2) TacheFactory.getInstance().getNewTask(TacheRaffiner2.class, false);
//                raffiner2.setComportementCollecte(recupCallable.getFlux().getMediatorFlux());
//                raffiner2.setItem(item);
//                raffiner2.setSchedule(false);
//                tacheProducteur.produireMaintenant(raffiner2);
//                
//            }
//        }
    }

    /**
     * *
     * Permet d'ajouter un callable au pool schedulé. La méthode scheduleAtFixedRate ne permet pas d'ajouter des
     * Callable, seulement des runnable. Pour cette raison, les renable doivent se réajouter en fin de tache pour avoir
     * un effet scheduleAtFixedRate
     *
     * @param t Le RUNNABLE qui doit être ajouté au pool
     */
//    public void addScheduledCallable(TacheRecupCallable t) {
////        this.poolSchedule.schedule(t, t.getFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
//        this.executorService.schedule(t, t.getFlux().getMediatorFlux().getPeriodiciteCollecte(), TimeUnit.SECONDS);
//    }
    /**
     * *
     * Retoune le pool prioritaire du service. Il s'agit du pool pour lancer des collectes manuelle. Celles ci sont
     * lancée avec une priorité suppréieure au pool schedulé
     *
     * @return
     */
    public ExecutorService getPoolPrioritaire() {
        return poolPrioritaire;
    }

    /**
     * *
     * Définir le pool prioritaire
     *
     * @param poolPrioritaire
     */
    public void setPoolPrioritaire(ExecutorService poolPrioritaire) {
        this.poolPrioritaire = poolPrioritaire;
    }

    @Override
    public void lancerService() {

        super.lancerService();
//        antiDeadBlock.setService(this);
//        this.executorService.submit(antiDeadBlock);



        // On charge le cache. Cela peut prendre du temps (il faut parcourir tous les flux et récupérer les dernière items...),  on va donc executer comme une thread pour ne pas ralentir le démarrage de l'application.
        Runnable chargement = new Runnable() {
            @Override
            public void run() {
                System.out.println("---> Debut de chargement des flux dans le cache des hash");
                cacheHashFlux.ChargerLesHashdesFluxdepuisBDD(); // Au démarrage du service, il faut charger les hash pour tout les flux dans le cache
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                System.out.println("---> FIN de chargement des flux dans le cache des hash");
            }
        };

        executorServiceAdministratif.submit(chargement);





        //---------------TACHES DE COLLECTE--------------

//        List<Flux> listf = DAOFactory.getInstance().getDAOFlux().findAllFlux(Boolean.TRUE);
//        for (int i = 0; i < listf.size(); i++) {
//            Flux flux = listf.get(i);
//            try {
//                enregistrerFluxAupresDuService(flux);
//            } catch (IncompleteBeanExeption ex) {
//                logger.error("erreur lors de l'enregistrement du flux ");
//                Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }


        // TODO : Ce serait bon de faire porter l'action par le producteur de tache du flux.
        // On doit récupérer les journaux permettant un ajout périodique
        DaoJournal daoJournal = DAOFactory.getInstance().getDaoJournal();
        List<Journal> journaux = daoJournal.findall();
        for (int i = 0; i < journaux.size(); i++) {
            Journal journal = journaux.get(i);

            if (journal.getAutoUpdateFlux() != null && journal.getAutoUpdateFlux()) {
                try {
                    enregistrerJournalAupresduService(journal);
                } catch (IncompleteBeanExeption ex) {
                    Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

//        if (collecteSubmiter.service == null) {
//            collecteSubmiter.setService(this);
//        }
//        executorService.submit(collecteSubmiter);


        //----------------TACHE TacheVerifComportementFluxGeneral
//        TacheVerifComportementFluxGeneral comportementFluxGeneral = new TacheVerifComportementFluxGeneral(this);
//        DateTime dtCurrent = new DateTime();
//        DateTime next = dtCurrent.plusDays(1).withHourOfDay(2);// withDayOfWeek(DateTimeConstants.SUNDAY);
//        Duration dur = new Duration(dtCurrent, next);
//        this.executorService.schedule(comportementFluxGeneral, dur.getStandardSeconds(), TimeUnit.SECONDS);

    }

    /**
     * *
     * Cette méthode permet d'ajouter un item à un flux. Si l'item est déja présente dans la base de donnée, le service
     * crée un liaison vers cette item. Sinon il la crée. La méthode est synchronisé afin que plusieurs thread
     * n'ajoutent pas en même temps des items.
     *
     * @param flux Le flux pour lequel il faut ajouter une item
     * @param item L'item devant être ajouté
     */
    public synchronized void ajouterItemAuFlux(Flux flux, Item item, EntityManager em, Boolean commiter, VisitorHTTP visitor/* ComportementCollecte comportementCollecte*/) {
        DaoItem dao = DAOFactory.getInstance().getDaoItem();

//        Semaphore sem = null;
        if (em == null) {
            em = DAOFactory.getInstance().getEntityManager();
            em.getTransaction().begin();

        }
        dao.setEm(em);

        // On test si l'item est nouvelle
        Boolean itemEstNouvelle = true;
//        Boolean err = false;
        if (item.getID() != null) { // Une item possédant un ID n'est pas nouvelle, il faut alors changer le booleean
            itemEstNouvelle = false;
        }

        // Si elle n'est pas nouvelle, il faut le blocker dans l'em
        if (!itemEstNouvelle) {

            if (!em.contains(item)) {
                item = em.find(Item.class, item.getID(), LockModeType.PESSIMISTIC_WRITE);
            } else {
                em.lock(item, LockModeType.PESSIMISTIC_WRITE);
            }


//            //Acquisition de la sem
//            try {
//                sem = SemaphoreCentre.getinstance().returnSemaphoreForRessource(item);
//                sem.acquire();
//            } catch (NullPointerException ex) {
//                Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IllegalAccessException ex) {
//                Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
//            }


        }


        // On ajouter l'item au flux si ce n'est pas déjà le cas
        List<Flux> lf = item.getListFlux();
        Boolean ajouter = true;
        for (int i = 0; i < lf.size(); i++) {
            Flux flux1 = lf.get(i);
            if (flux1.getID().equals(flux.getID())) {
                ajouter = false;
            }
        }
        if (ajouter) {
            item.getListFlux().add(flux);
        }



        // Si l'item est nouvelle, on cherche si si n'existe pas déjà dans la base de données une item possédant ce hash. 

//        if(itemEstNouvelle){
//            try {
//                Item itBdd = dao.findByHash(item.getHashContenu());
//                if(itBdd!= null){
//                    itBdd.verserLesDonneeBruteAutreItem(item);
//                    item = itBdd;
//                    itemEstNouvelle = false;
//                    item.addFlux(flux);
//                }
//                
//            } catch (Exception e) {
//            
//            logger.debug("err", e);
//            }
//            
//        }

        //
        if (itemEstNouvelle) {  // Si l'item est nouvelle, on va effectuer une création dans la base de données
            try {
                em.persist(item);
//                if (comportementCollecte != null) {
                if (visitor != null) {
                    short nb = visitor.getNbNouvelle();
                    nb++;
                    visitor.setNbNouvelle(nb);
                }

            } catch (Exception ex) {
                logger.debug("erreur lors de l'ajout", ex);
            }
        } else {
            if (visitor != null) {
                short nb = visitor.getNbLiaisonCree();
                nb++;
                visitor.setNbLiaisonCree(nb);
            }
        }

        // Si l'item n'est pas nouvelle. 
        if (!itemEstNouvelle) {
            try {

                em.merge(item);

            } catch (Exception e) {
//                err = true;
                logger.debug("err", e);
            }

        }



        // Si le traitement s'est bien déroulé
        try {
            if (commiter) { // Si aucun em n'a été précisé alors c'est de la responsabilité du service de demander le comit. Sinon c'est l'appelan qui doit se débrouiller avec son em
                em.getTransaction().commit();
                // On ajoute le hash au cache. Cette action ne sera réalisé que si le comit s'est bien déroulé
                cacheHashFlux.addHash(flux, item.getHashContenu());
            }

        } catch (Exception e) {
            logger.error("erreur lors du commit", e);
        }
        // Il faudra trouver qqchose en cas a nouveau d'erreur exemple la base de de donnée ne répond pas.

    }

    /**
     * *
     * Méthode permettant de supprimer le flux. L'ensemble des items du flux sont parcourues. Si les items sont seules,
     * elle sont supprimées. Si elles appartiennent à un autre flux, on retire le flux de l'item, puis on modifie
     * l'item.
     *
     * @param flux
     */
    @Deprecated //----> C'est maintenant dans le service CRUD
    public void removeFluxWithItem(Flux flux) throws Exception {

        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        daoItem.beginTransaction();
        Boolean err = false;


        List<Item> items = daoItem.itemLieAuFlux(flux);

        int i;
        for (i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            //Supppression des items qui vont devenir orphelines
            if (item.getListFlux().size() < 2) {
                // On supprimer la relation 
                item.getListFlux().clear();
                try {
                    daoItem.modifier(item);
                    daoItem.remove(item);
                } catch (Exception e) {
                    err = true;
                    logger.debug("Erreur lors de la suppression", e);
                }

            } else { // Sinon on détach le flux
                item.getListFlux().remove(flux);

                try {
                    daoItem.modifier(item);
                } catch (Exception ex) {
                    err = true;
                    logger.debug("Erreur lors de la modification", ex);
                }
            }
        }
        // On va supprimer le flux si la procédure de suppression des items s'est déroulée correctement
        flux.setItem(new ArrayList<Item>());

        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
        daoFlux.beginTransaction();

        try {
            daoFlux.remove(flux);
        } catch (IllegalArgumentException ex) {
            err = true;
            Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransactionRequiredException ex) {
            err = true;
            Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            err = true;
            Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Tous le monde est OK, Alors on commit
        if (!err) {
            try {
                daoItem.commit();
            } catch (Exception e) {
                logger.debug("Erreur lors du comit de l'item", e);
            }

            try {
                daoFlux.commit();
                cacheHashFlux.removeFlux(flux);
            } catch (Exception e) {
                logger.debug("erreur", e);
            }

        } else {
            throw new Exception("Erreur lors de la suppression");
        }
    }

    /**
     * *
     * Permet de lancer la découverte des flux RSS d'un journal
     *
     * @param journal
     * @param persisterAjout précise si les flux découverts doivent être persisté
     * @param activerFlux précise si les flux découvert doivent être activé (il faut déjà qu'il soient persisté
     * @throws IncompleteBeanExeption Si le journal est null n'a pas d'ID ou si il ne possède pas de champs
     * {@link Journal#urlHtmlRecapFlux}
     */
    public Future<TacheDecouverteAjoutFlux> decouverteFluxJournal(Journal journal, Boolean persisterAjout, Boolean activerFlux, short timeOut, short nbThread) throws IncompleteBeanExeption, DonneeInterneCoherente {
        if (journal == null) {
            throw new NullPointerException("Le journal est null");
        }
        if (journal.getID() == null) {
            throw new IncompleteBeanExeption("Le beans n'a pas d'ID");
        }
        if (journal.getUrlHtmlRecapFlux() == null || journal.getUrlHtmlRecapFlux().isEmpty()) {
            throw new IncompleteBeanExeption("Le journal ne possède pas de champs URLHTMLRECAP. Impossible de découvrir les flux");
        }

        if (!persisterAjout && activerFlux) {
            throw new DonneeInterneCoherente("Il est impossible d'activer des flux non persisté");
        }



//        TacheDecouverteAjoutFlux tache = new TacheDecouverteAjoutFlux();
        TacheDecouverteAjoutFlux tache = (TacheDecouverteAjoutFlux) TacheFactory.getInstance().getNewTask(TacheDecouverteAjoutFlux.class, false);
        
        tache.addObserver(this);
        tache.setJournal(journal);
        tache.setNombredeSousTache(new Integer(nbThread));
        tache.setActiverLesFLux(activerFlux);
        tache.setPersist(persisterAjout);
        tache.setGeneralRequestTimeOut(timeOut);
        
        tacheProducteur.produireMaintenant(tache);
        
  
        synchronized(tache){
            try {
                tache.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServiceCollecteur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
        return tache.getFuture();
        
        
        
//        Future<TacheDecouverteAjoutFlux> fut = submit(tache);
//        return fut;

    }

    @Override
    public void stopService() throws SecurityException, RuntimeException {
        if (this.poolPrioritaire != null) {
            try {
                this.poolPrioritaire.shutdownNow();
            } catch (Exception e) {
                logger.error("Erreur lors de la fermeture du pool prioritaire", e);
            }
        }

        if (this.executorServiceRaffinage != null) {
            try {
                this.executorServiceRaffinage.shutdownNow();
            } catch (Exception e) {
                logger.info("Erreur lors de la fermeture du pool de rafinage ", e);
            }
        }

        super.stopService();
    }

    public CacheHashFlux getCacheHashFlux() {
        return cacheHashFlux;
    }

    public void setCacheHashFlux(CacheHashFlux cacheHashFlux) {
        this.cacheHashFlux = cacheHashFlux;
    }
}
