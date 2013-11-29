/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe permettant de compter le nombre d'Item par jour capturé par Flux. Il faut lui fournir une liste d'item et
 * déclancer le comptage. Les résultats sont ensuite disponible au sein d'objet de Type {@link POJOCompteItem}
 * disponible dans la liste {@link #listCompteItem}
 *
 * @author clem
 */
public class POJOCompteurFluxItem {

    /**
     * *
     * Liste des objet de compte réalisé par la méthode {@link #compter() } en se basant sur la liste des items
     * {@link #listItem}
     */
    List<POJOCompteItem> listCompteItem = new ArrayList<POJOCompteItem>();
    /**
     * *
     * Liste des items pour lesquels il faut réaliser un compte par jour
     */
    List<Item> listItem;
    /**
     * *
     * Début de date du compte. Le compte du nombre d'item jour à une temporalité. Il s'agit de la date de début
     */
    Date date1;
    /**
     * *
     * date de fin cf {@link #date1} pour date début
     */
    Date date2;

    List<Flux> listFlux = new ArrayList<Flux>();
    
    

    public List<POJOCompteItem> getListCompteItem() {
        return listCompteItem;
    }

    public void setListCompteItem(List<POJOCompteItem> listCompteItem) {
        this.listCompteItem = listCompteItem;
    }

    public List<Item> getListItem() {
        return listItem;
    }

    public void setListItem(List<Item> listItem) {
        this.listItem = listItem;
    }

    /**
     * *
     * Parcour l'ensemble des items afin de générer des {@link POJOCompteItem} pour chaque {@link Flux} découvert dans
     * les items. Puis lance le compte pour chaque{@link POJOCompteItem}
     */
    public void compter() {
                    
            for (int j = 0; j < listFlux.size(); j++) {
                Flux flux = listFlux.get(j);
                POJOCompteItem compteItem = new POJOCompteItem();
                compteItem.setDate1(date1);
                compteItem.setDate2(date2);
                compteItem.setItems(listItem);
                compteItem.setFlux(flux);
                this.listCompteItem.add(compteItem);
            }
        

        // Pour chaque compte item on demande le trie et le compte par jour 
        for (int i = 0; i < listCompteItem.size(); i++) {
            POJOCompteItem pOJOCompteItem = listCompteItem.get(i);
            try {
                pOJOCompteItem.compte();
            } catch (Exception ex) {
                Logger.getLogger(POJOCompteurFluxItem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * *
     * getter pour {@link #date1}
     *
     * @return
     */
    public Date getDate1() {
        return date1;
    }

    /**
     * *
     * setter pour {@link #date1}
     *
     * @param date1
     */
    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    /**
     * *
     * getter pour {@link #date2}
     *
     * @return
     */
    public Date getDate2() {
        return date2;
    }

    /**
     * *
     * setter pour {@link #date2}
     *
     * @param date2
     */
    public void setDate2(Date date2) {
        this.date2 = date2;
    }

    public List<Flux> getListFlux() {
        return listFlux;
    }

    public void setListFlux(List<Flux> listFlux) {
        this.listFlux = listFlux;
    }
    
}
