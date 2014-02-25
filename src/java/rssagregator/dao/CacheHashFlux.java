/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.services.ServiceCollecteur;
import rssagregator.utils.ThreadUtils;

/**
 * Un singleton permettant de concerver les hash des flux. Il est utilisé par le dédoublonneur
 *
 * @author clem
 */
public class CacheHashFlux {

    static CacheHashFlux instance = new CacheHashFlux();
    Map<Flux, Set<String>> cacheHash = new HashMap<Flux, Set<String>>(); // Le type Lincked hash map permet de concerver l'ordre d'ajout. Utile quand on veut supprimer les x premier (mais peut être mauvais en terme de capacité
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    protected CacheHashFlux() {
    }

    public static CacheHashFlux getInstance() {
        if (instance == null) {
            instance = new CacheHashFlux();
        }
        return instance;
    }

    /**
     * *
     * Cette méthode doit être lancée au démarrage du {@link ServiceCollecteur}. Elle permet d'initialiser le cache des
     * flux avec les données récupérée dans la base de données
     */
    public synchronized void ChargerLesHashdesFluxdepuisBDD() {
        logger.debug("Chargement");
        EntityManager em = DAOFactory.getInstance().getEntityManager();
        try {
            DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux(false);
            daoFlux.setEm(em);
            DaoItem daoItem = DAOFactory.getInstance().getDaoItem(false);
            daoItem.setEm(em);

            List<Flux> listflux = daoFlux.findall();
            for (int i = 0; i < listflux.size(); i++) {
                ThreadUtils.interruptCheck();
                Flux flux = listflux.get(i);
                // Construction de la requete
                Query query = em.createQuery("SELECT item.hashContenu FROM Item item JOIN item.listFlux fl WHERE fl.ID=:idfl ORDER BY item.ID DESC");
                query.setParameter("idfl", flux.getID());
                query.setFirstResult(0);
                query.setMaxResults(500);

                List<String> listResu = query.getResultList();

                Set<String> setHash = new LinkedHashSet<String>();
                setHash.addAll(listResu);
                addAll(flux, setHash);
//                em.detach(flux);
            }

        } catch (Exception e) {
            logger.error("Erreur lors du chargement des hash dans le cache", e);
        } finally {
            if (em != null && em.isOpen()) {
                try {
                    em.close();
                } catch (Exception e) {
                    logger.error("Erreur lors de la fermetuire de l'em", e);
                }
            }
        }
        logger.debug("fin de chargement");
    }

    /**
     * *
     * Retourne la liste des hash concervés dans le cache pour un flux donnée. Le flux est retrouvé par comparaison de
     * sa clé primaire
     *
     * @param flux Le flux pour lequel il faut récupérer
     * @return Le set des hash. retour null si le n'a pas été trouvé dans le cache
     */
    public synchronized Set<String> returnLashHash(Flux flux) {
        if (flux != null && flux.getID() != null) {
            for (Map.Entry<Flux, Set<String>> entry : cacheHash.entrySet()) {
                Flux flux1 = entry.getKey();
                if (flux1.getID().equals(flux.getID())) {
                    Set<String> string = entry.getValue();
                    return string;
                }
            }
        }
        return null;
    }

    public synchronized Integer returnNbrHash(Flux flux) {
        if (flux != null && flux.getID() != null) {
            for (Map.Entry<Flux, Set<String>> entry : cacheHash.entrySet()) {
                Flux flux1 = entry.getKey();
                if (flux1.getID().equals(flux.getID())) {
                    Set<String> string = entry.getValue();
                    return string.size();
                }
            }
        }
        return null;
    }

    /**
     * *
     * Ajoute un hash en mémoire pour le flux donné en paramètre. Si le flux n'est pas trouvé, il est alors ajouté.
     *
     * @param flux Le flux pour lequel il faut ajouter le hash
     * @param hash Le hash a ajouter. L'ajout ne se fera que si le hash n'est pas null ou empty
     */
    public synchronized void addHash(Flux flux, String hash) {
        if (flux != null && hash != null && flux.getID() != null && flux.getID() > 0 && !hash.isEmpty()) {
            boolean trouve = false;
            for (Map.Entry<Flux, Set<String>> entry : cacheHash.entrySet()) {
                Flux flux1 = entry.getKey();

                if (flux1.getID().equals(flux.getID())) {

                    Set<String> string = entry.getValue();
                    trouve = true;
                    string.add(hash);
                }
            }
            //Si on n'a pas trouvé le flux, il faut l'ajouter
            if (!trouve) {
                Set<String> newHash = new LinkedHashSet<String>();
                newHash.add(hash);
                cacheHash.put(flux, newHash);
            }
        }

    }

    /**
     * *
     * Ajoute les hash envoyé en paramètre pour le flux donnée
     *
     * @param flux : Le flux pour lequel il faut ajouté les hash
     * @param setHash Les hash a ajouter
     */
    public void addAll(Flux flux, Set<String> setHash) {

        if (flux != null && setHash != null && setHash.size() > 0) {
            // On vérifi chacune des valeurs du set On retir ce qui pourrait être null ou empty
            for (Iterator<String> it = setHash.iterator(); it.hasNext();) {
                String string = it.next();
                if (string == null) {
                    it.remove();
                }
                if (string != null && string.isEmpty()) {
                    it.remove();
                }
            }

            boolean trouve = false;
            for (Map.Entry<Flux, Set<String>> entry : cacheHash.entrySet()) {
                Flux flux1 = entry.getKey();
                Set<String> key = entry.getValue();
                if (flux1.getID().equals(flux.getID())) {
                    trouve = true;
                    key.addAll(setHash);
                }
            }
            if (!trouve) {
                this.cacheHash.put(flux, setHash);
            }
        }
    }

    /**
     * *
     * Supprimer le hash envoyé en paramettre pour le flux envoyé
     *
     * @param flux le flux pour lequel il faut supprimer le hash
     * @param hash Le hash qu'il faut supprimer
     * @return
     */
    public synchronized Boolean reomveHash(Flux flux, String hash) {
        for (Map.Entry<Flux, Set<String>> entry : cacheHash.entrySet()) {
            Flux flux1 = entry.getKey();
            Set<String> set = entry.getValue();
            if (flux1.getID().equals(flux.getID())) {
                set.remove(hash);
            }
        }
        return false;
    }

    /**
     * *
     * Supprimer le nombre x de hash pour le flux envoyé en argument. Les hash supprimés sont les plus anciens
     *
     * @param x : le nombre de hash à supprimer
     * @param flux Le flux pour lequel il faut supprimer les x hash
     */
    public synchronized void removeXHash(Integer x, Flux flux) {
        if (x != null && flux != null && flux.getID() != null && flux.getID() >= 0 && x > 0) {
            for (Map.Entry<Flux, Set<String>> entry : cacheHash.entrySet()) {
                Flux flux1 = entry.getKey();
                Set<String> set = entry.getValue();
                if (flux1.getID().equals(flux.getID())) {
                    Iterator it = set.iterator();
                    if (x > set.size()) {
                        x = set.size();
                    }
                    for (int i = 0; i < x; i++) {
                        it.next();
                        it.remove();
                    }
                }
            }
        }
    }

    /**
     * *
     * Supprime le flux donnée du cache
     *
     * @param flux
     * @return true si le flux a été trouvé et supprimé. False si le flux n'a pas été trouvé
     */
    public synchronized Boolean removeFlux(Flux flux) {
        if (flux != null && flux.getID() != null && flux.getID() >= 0) {

            for (Map.Entry<Flux, Set<String>> entry : cacheHash.entrySet()) {
                Flux flux1 = entry.getKey();
                Set<String> set = entry.getValue();
                if (flux1.getID().equals(flux.getID())) {
                    try {
                        cacheHash.remove(flux);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }


            }
            if (cacheHash.containsKey(flux)) {
            }
        }
        return false;
    }

    public static void main(String[] args) {
    }

    public void addAllHashDeLItem(Item itemSemblableBDD, Flux flux) {
        if (itemSemblableBDD == null) {
            throw new NullPointerException("item null");
        }
        if (flux == null) {
            throw new NullPointerException("flux null");
        }


        boolean trouve = false;
        for (Map.Entry<Flux, Set<String>> entry : cacheHash.entrySet()) {
            Flux flux1 = entry.getKey();
            Set<String> set = entry.getValue();
            if (flux1.getID().equals(flux.getID())) {
                trouve = true;
                if (itemSemblableBDD.getHashContenu() != null && !itemSemblableBDD.getHashContenu().isEmpty()) {
                    set.add(itemSemblableBDD.getHashContenu());
                }
//                for (int i = 0; i < itemSemblableBDD.getDonneeBrutes().size(); i++) {
////                    DonneeBrute donneeBrutes = itemSemblableBDD.getDonneeBrutes().get(i);
//                    String str = itemSemblableBDD.getDonneeBrutes().get(i).getHashContenu();
//                    if (str != null && !str.isEmpty()) {
//                        set.add(str);
//                    }
//                }
                break;
            }
        }

        if (!trouve) { // Si le flux n'est pas déjà présent
            Set<String> newSet = new HashSet<String>();
            newSet.add(itemSemblableBDD.getHashContenu());
//            for (int i = 0; i < itemSemblableBDD.getDonneeBrutes().size(); i++) {
//                String str = itemSemblableBDD.getDonneeBrutes().get(i).getHashContenu();
//                if (str != null && !str.isEmpty()) {
//                    newSet.add(str);
//                }
//            }
            this.cacheHash.put(flux, newSet);
        }

    }
}
