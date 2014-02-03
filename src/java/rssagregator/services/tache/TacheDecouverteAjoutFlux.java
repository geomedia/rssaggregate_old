/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import rssagregator.beans.Flux;
import rssagregator.beans.Journal;
import rssagregator.beans.exception.AucunFluxDecouvert;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.IncidentDecouverteRSS;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.incident.NotificationAjoutFlux;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import rssagregator.services.SemaphoreCentre;
import rssagregator.services.crud.AbstrServiceCRUD;
import rssagregator.services.crud.ServiceCRUDFactory;
import rssagregator.utils.ThreadUtils;

/**
 * Cette tâche permet de parcourir la page présentant les flux RSS d'un journal afin de découvrir les flux. Chacun des
 * flux est ajouté si il n'est pas déjà présent dans la base de données
 *
 * @author clem
 */
public class TacheDecouverteAjoutFlux extends TacheImpl<TacheDecouverteAjoutFlux> implements Incidable {

//    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheDecouverteAjoutFlux.class);
    /**
     * *
     * Le journal que la tâche doit parcourir
     */
    private Journal journal;
    /**
     * *
     * La liste de tous les liens découverts par la tâche
     */
    Set<String> Discoverlinks = new LinkedHashSet<String>(); // La liste des liens
    /**
     * *
     * La liste des liens déterminé comme étant des RSS par la tache.
     */
    Set<String> DiscoverRSSLink = new LinkedHashSet<String>(); // La liste des liens trouvé comme flux RSS
    /**
     * *
     * liste de flux crée par la tache et ajouté dans cette collection.
     */
    List<Flux> listFluxDecouvert = new ArrayList<Flux>();
    /**
     * *
     * Cette thread va ouvrir des sous Thread afin de parcourir chacun des liens de la page RSS du journal. La variable
     * nombredeSousTache permet de déterminer le nombre de sous Thread à ouvrir
     */
    Integer nombredeSousTache;
    /**
     * *
     * Détermine si la tache doit ou non enregistrer les flux découverts dans la base de données.
     */
    Boolean persist;
    /**
     * *
     * Détermine si la tache doit activer les flux lors de leur ajout dans la base de données.
     */
    Boolean activerLesFLux;
    /**
     * *
     * Executeur service propre à la tache permettant de faire des sous requetes
     */
    ExecutorService es;
    /**
     * *
     * Le temps maximal consacré aux requetes.
     */
    short generalRequestTimeOut;

    @Override
    protected void callCorps() throws Exception {


        es = Executors.newFixedThreadPool(nombredeSousTache); // On lance les tache en parallèle pour chaque lien

        // Si la tâche n'est pas annulé on continu le déroulement

        if (journal == null || journal.getUrlHtmlRecapFlux() == null || journal.getUrlHtmlRecapFlux().isEmpty()) {
            throw new Exception("Le journal envoyé ne permet pas d'exécuter la tâche");
        }

        if (nombredeSousTache == null || nombredeSousTache < 1) {
            throw new NullPointerException("La variable nombredeSousTache doit être définie pour faire fonctionner la tâche");
        }

        org.jsoup.Connection jsoupConnection = Jsoup.connect(journal.getUrlHtmlRecapFlux());

        Document doc = jsoupConnection.parser(Parser.htmlParser()).get();

        Elements linkElements = doc.select("a[href]"); // On récupère tous les éléments <a> de la page
        List<DecouverteRSSAPartirDunLien> listSousTache = new ArrayList<DecouverteRSSAPartirDunLien>(); // Une liste de Runnable a executer plus bas

        for (int i = 0; i < linkElements.size(); i++) { // Pour chaque élément
            Element element = linkElements.get(i);
            String l = element.attr("abs:href"); // On récupère la valeur de l'attribut

            if (l != null && !l.isEmpty()) { //Si elle n'est pas null, on l'ajoute aux discoverLink et on crée une tache
                Discoverlinks.add(l);
                DecouverteRSSAPartirDunLien sousTache = new DecouverteRSSAPartirDunLien();
                sousTache.link = l;
                listSousTache.add(sousTache);
            }
        }

        if (listSousTache.isEmpty()) {
            throw new AucunFluxDecouvert();
        }


//        es.invokeAll(listSousTache);

        try {
            es.invokeAll(listSousTache, generalRequestTimeOut, TimeUnit.SECONDS); // On donne 120 secondes pour la découverte         
        } catch (InterruptedException e) {
            ThreadUtils.interruptCheck();
        } catch (Exception e) {
            logger.debug("Ex" + e);
        }


        es.shutdown();

        System.out.println("=======================");
        System.out.println("Fin des requetes");
        System.out.println("=======================");

        //-------> Ajout des ressouces

        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
        AbstrServiceCRUD serviceCRUD = ServiceCRUDFactory.getInstance().getServiceFor(Flux.class);

        IncidentFactory<NotificationAjoutFlux> incidentFactory = new IncidentFactory<NotificationAjoutFlux>();
        NotificationAjoutFlux notificationAjoutFlux = incidentFactory.getIncident(NotificationAjoutFlux.class, "Découverte nouveau flux", null); //On crée une notification
        for (Iterator<DecouverteRSSAPartirDunLien> it = listSousTache.iterator(); it.hasNext();) {
            DecouverteRSSAPartirDunLien sousTache = it.next();
//                System.out.println("--> IT AJOUT" + sousTache);

            if (sousTache.rss) {
                try {
                    Flux f = daoFlux.findWithUrl(sousTache.link);
                    if (f == null) { // Si le flux n'est pas trouvé, on crée un nouveau flux et on l'ajoute à la liste
                        System.out.println("--> Ajout d'un flux");
                        f = new Flux();
                        f.setJournalLie(journal);
                        f.setUrl(sousTache.link);
                        f.setMediatorFlux(journal.getComportementParDefaultDesFlux());
                        f.setCreated(new Date());

                        if (activerLesFLux) { // On active les flux si c'est demandé
                            f.setActive(true);
                        } else {
                            f.setActive(false);
                        }

                        if (sousTache.title != null && !sousTache.title.isEmpty()) { // On récupère le titre du flux
                            f.setNom(sousTache.title);
                        }

                        listFluxDecouvert.add(f);

                        if (persist) { // On persiste en focntion du booleea.
                            serviceCRUD.ajouter(f);
                            notificationAjoutFlux.getFluxAjoute().add(f);
                            notificationAjoutFlux.setJournal(journal);
                            notificationAjoutFlux.setDateFin(new Date());
                        }
                    }
                } catch (Exception e) {
                    logger.debug("erreur lors de l'ajout", e);
                }
            }
        }

        // On enregistre la notification (c'est un genre d'incident.
        if (!notificationAjoutFlux.getFluxAjoute().isEmpty()) {
            AbstrServiceCRUD crudIncid = ServiceCRUDFactory.getInstance().getServiceFor(NotificationAjoutFlux.class);
            crudIncid.ajouter(notificationAjoutFlux);
        }
    }

    /**
     * *
     * Redéclaration de la méthode callFinalyse afin de fermer l'executor service
     *
     * @return
     */
    @Override
    protected TacheDecouverteAjoutFlux callFinalyse() {
        if (es != null && !es.isShutdown()) {     // On ferme l'executor service
            es.shutdownNow();
        }
        return super.callFinalyse();
    }

    /**
     * Get the value of journal
     *
     * @return the value of journal
     */
    public Journal getJournal() {
        return journal;
    }

    /**
     * Set the value of journal
     *
     * @param journal new value of journal
     */
    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    public Integer getNombredeSousTache() {
        return nombredeSousTache;
    }

    public void setNombredeSousTache(Integer nombredeSousTache) {
        this.nombredeSousTache = nombredeSousTache;
    }

    @Override
    public Class getTypeIncident() {
        return IncidentDecouverteRSS.class;
    }

    public Set<String> getDiscoverlinks() {
        return Discoverlinks;
    }

    public void setDiscoverlinks(Set<String> Discoverlinks) {
        this.Discoverlinks = Discoverlinks;
    }

    public Set<String> getDiscoverRSSLink() {
        return DiscoverRSSLink;
    }

    public void setDiscoverRSSLink(Set<String> DiscoverRSSLink) {
        this.DiscoverRSSLink = DiscoverRSSLink;
    }

    public List<Flux> getListFluxDecouvert() {
        return listFluxDecouvert;
    }

    public void setListFluxDecouvert(List<Flux> listFluxDecouvert) {
        this.listFluxDecouvert = listFluxDecouvert;
    }

    public Boolean getPersist() {
        return persist;
    }

    public void setPersist(Boolean persist) {
        this.persist = persist;
    }

    public Boolean getActiverLesFLux() {
        return activerLesFLux;
    }

    public void setActiverLesFLux(Boolean activerLesFLux) {
        this.activerLesFLux = activerLesFLux;
    }

    /**
     * *
     * Lorsque la découverte des flux d'un journal n'a pas été possible, on crée systématiquement un nouvel incident
     *
     * @throws Exception
     */
    @Override
    public void gererIncident() throws Exception {
        if (this.exeption != null) {
            logger.debug("Gestion Incident");

            String msg;
            if (this.exeption.getClass().equals(AucunFluxDecouvert.class)) {
                msg = "La page de récapitulatif des flux RSS du journal est accessible mais aucun flux n'a été découvert sur celle ci";
            } else if (this.exeption.getClass().equals(java.net.UnknownHostException.class)) {
                msg = "Impossible de joindre la page de récapitulatif des flux RSS du journal";
            } else {
                msg = "Erreur inconue : " + this.exeption;
            }

            IncidentFactory<IncidentDecouverteRSS> factory = new IncidentFactory<IncidentDecouverteRSS>();
            IncidentDecouverteRSS incident = factory.getIncident(IncidentDecouverteRSS.class, msg, this.exeption);
            AbstrServiceCRUD serviceCrud = ServiceCRUDFactory.getInstance().getServiceFor(IncidentDecouverteRSS.class);
            serviceCrud.ajouter(incident);
        }
    }

    @Override
    public void fermetureIncident() throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * *
     * Cette sous classe est utilisé par {@link TacheDecouverteAjoutFlux} afin de parcourir les sous liens en parallèle
     */
    public class DecouverteRSSAPartirDunLien implements Callable<Boolean> {

        /**
         * *
         * Le lien qu'il faut parcourir
         */
        protected String link;
        /**
         * *
         * Bool renseigné par la tache. true si c'est un rss; false sinon
         */
        protected boolean rss;
        protected String title;

        /**
         * *
         * Lance le parcours du link afin de vérifier si oui ou non il est un flux RSS. La tache va renseigner la valeur
         * de la variable {@link #rss}
         */
        @Override
        public Boolean call() throws Exception {

            rss = false;
            XmlReader xmlReader = null;

            try {      // On test si c'est un RSS. Pour cela on se connecte au lien prend le contenu et on tente de le parser comme un rss. Si le try marche c'est un flux

                URL url = new URL(link);

                SyndFeedInput feedInput = new SyndFeedInput();
                feedInput.setPreserveWireFeed(true);

                xmlReader = new XmlReader(url);
                SyndFeed syndFeed = feedInput.build(xmlReader);
                title = syndFeed.getTitle();

                rss = true;
            } catch (Exception e) {
            } finally {
                if (xmlReader != null) {
                    try {
                        xmlReader.close();

                    } catch (Exception e) {
                        logger.debug("fermeture", e);
                    }
                }
            }
            logger.debug("Page : " + link + "RSS : " + rss);
            return rss;
        }
    }

    public short getGeneralRequestTimeOut() {
        return generalRequestTimeOut;
    }

    public void setGeneralRequestTimeOut(short generalRequestTimeOut) {
        this.generalRequestTimeOut = generalRequestTimeOut;
    }

    
    /***
     * Cette tache a besoin d'acquerir la sémaphore relative au journal avant de démarrer. 
     * @return 
     */
    @Override
    public Set<Semaphore> returnSemSet() {

        sem.clear();
        if (journal != null) {
//            synchronized (SemaphoreCentre.getinstance()) {
                try {
                    Semaphore s = SemaphoreCentre.getinstance().returnSemaphoreForRessource(journal);
                    sem.add(s);
                } catch (NullPointerException ex) {
                    Logger.getLogger(TacheDecouverteAjoutFlux.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(TacheDecouverteAjoutFlux.class.getName()).log(Level.SEVERE, null, ex);
                }

//            }

        }

        return sem;

    }
}