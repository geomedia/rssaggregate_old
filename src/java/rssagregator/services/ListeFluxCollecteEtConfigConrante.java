package rssagregator.services;

import dao.AbstrDao;
import dao.DAOFactory;
import dao.DaoFlux;
import dao.DaoItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import javax.persistence.LockModeType;
import rssagregator.beans.Conf;
import rssagregator.beans.Flux;
import rssagregator.beans.form.DAOGenerique;

/**
 * Cette classe permet de garder en mémoire la liste des flux collecté ainsi que
 * la configuration courante. De nombreux services doivent accéder à ces
 * information. Lors de l'ajout ou de la supression d'un flux ou du changement
 * d'information su la config. Cet objet observable notifie à ses observeur le
 * changement.
 */
public class ListeFluxCollecteEtConfigConrante extends Observable {

    /**
     * *
     * Instanciation au démarrage pour le singleton
     */
    private static ListeFluxCollecteEtConfigConrante instance = new ListeFluxCollecteEtConfigConrante();
    /**
     * Liste contenant l'ensemble des flux collecté. C'est la source
     * d'information pour l'ensemble des services ayant besoin de la liste des
     * flux collectés. Cette liste est récupérée par les service par le biais du
     * pattern observer. La classe ListeFluxCollecte implemente ainsi le pattern
     * Observable.
     */
    public List<Flux> listFlux;
    private Conf confCourante;

    /**
     * *
     * La liste est un singleton
     */
    private ListeFluxCollecteEtConfigConrante() {
        this.listFlux = new ArrayList<Flux>();
    }

    public static ListeFluxCollecteEtConfigConrante getInstance() {
        if (ListeFluxCollecteEtConfigConrante.instance == null) {
            ListeFluxCollecteEtConfigConrante.instance = new ListeFluxCollecteEtConfigConrante();
        }
        return ListeFluxCollecteEtConfigConrante.instance;
    }

    public Conf getConfCourante() {
        return confCourante;
    }

    public void setConfCourante(Conf confCourante) {
        this.confCourante = confCourante;
    }

    public List<Flux> getListFlux() {
        return listFlux;
    }

    public void setListFlux(List<Flux> listFlux) {
        this.listFlux = listFlux;
    }
//    private Integer forceChange = 0;

    /**
     * *
     * Modifi le statut Change de L'observable.
     */
    public void forceChange() {
        this.setChanged();
    }

    /**
     * *
     * Charger données (list flux et config) depuis la base de données.
     */
    public void chargerDepuisBd() {

        //Chargement de la config depuis la base de donnée
        DAOGenerique dao = DAOFactory.getInstance().getDAOGenerique();
        dao.setClassAssocie(Conf.class);
        List<Object> confs = dao.findall();
        if (confs != null && confs.size() > 0) {
            Conf conf = (Conf) confs.get(0);
            this.confCourante = conf;
        } else {
            Conf defaultconf = new Conf();
            defaultconf.setActive(Boolean.TRUE);
            defaultconf.setNbThreadRecup(5);
            this.confCourante = defaultconf;
            dao.creer(confCourante);
        }

        //Chargement de la liste des flux depuis la BDD

        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
        List<Object> listflux = daoFlux.findall();
        int i;

        for (i = 0; i < listflux.size(); i++) {
            Flux fl = (Flux) listflux.get(i);
            fl.setPeriodiciteCollecte(60);
            this.listFlux.add(fl);
            
            // Pour chaque flux, on va charger les 100 dernier hash 
            DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
            List<String> dernierHash = daoItem.findLastHash(fl, 100);
            fl.setLastEmpruntes(dernierHash);
            
             
            
        }
    }

    /**
     * *
     * Supprime le flux de la liste des flux collecté en mémoire, et persiste
     * dans la base de données, la modification est notifié aux observeur (le
     * Collecteur)
     *
     * @param flux
     */
    public void removeFlux(Flux flux) {


        System.out.println("");

        System.out.println("Nombre de flux dans la liste de ref : " + listFlux.size());
        listFlux.remove(flux);
//            listFlux.remove(0);
        System.out.println("");






        DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
//        Flux fl = (Flux) dao.find(flux.getID());
 
        dao.remove(flux);
        forceChange();

        notifyObservers();

        System.out.println("Nombre de flux dans la liste de ref : " + listFlux.size());

    }

    /**
     * *
     * Ajoute un flux à la liste des flux collecté puis persiste dans la base de
     * donées. La modification est ensuite notifiée aux observeur (le service de
     * collecte collecteurs)
     *
     * @param f
     */
    public void addFlux(Flux f) {
        DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
        dao.creer(f);

 
//        System.out.println("Nombre d'observateur : " + this.countObservers());
        listFlux.add(f);
        
        
       


        forceChange();
        notifyObservers();
    }

    /**
     * *
     * Parcours les flux en mémoire et retourne le flux
     *
     * @return Le flux demandé ou null si il n'a pas été trouvé
     */
    public Flux getflux(Long id) {
        int i = 0;
        while (i < listFlux.size()) {
            if (listFlux.get(i).getID().equals(id)) {
                return listFlux.get(i);
            }
            i++;
        }
        return null;
    }

    /**
     * *
     * Enregistre les modification du flux dans la base de donnée.
     *
     * @param flux
     */
    public void modifierFlux(Flux flux) {

        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
        daoFlux.modifier(flux);


        // Il faut recharger le collecteur. En effet, si la périodicité de collecte du flux a été modifié, il est utile de recréer le pool de thread schedulé.

        forceChange();

        notifyObservers();
    }

    /**
     * *
     * Enregistre la conf courante dans la base de donnée en utilisant ue DAO
     * générique; la mofification est ensuite motifiée aux observeurs.
     *
     * @param conf
     */
    public void modifierConf(Conf conf) {
        AbstrDao dao = DAOFactory.getInstance().getDAOGenerique();
        dao.setClassAssocie(Conf.class);

        dao.modifier(conf);
        forceChange();

        notifyObservers();
    }
}