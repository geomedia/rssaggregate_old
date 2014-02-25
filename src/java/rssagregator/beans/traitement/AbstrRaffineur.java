/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.reflections.Reflections;
import rssagregator.beans.ContentRSS;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;

/**
 * Les raffineur ont pour tache principale de faire correspondre une {@link Item} à un {@link ItemRaffinee}
 *
 * @author clem
 */
@Entity
@Table(name = "tr_raffineur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstrRaffineur implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    /**
     * *
     * Liste construite par la méthode {@link #findSimilarIteminBDD() }
     */
    @Transient
    List<ComparaisonItem> listComparaison = new ArrayList<ComparaisonItem>();
    /**
     * *
     * L'item sur laquelle porte le travail
     */
    @Transient
    Item itemObserve;
    /**
     * *
     * Le comparator utilisé pour trier la liste;
     */
    @Transient
    Comparator<ComparaisonItem> comparator;
    
    
    /***
     * Pour restraindre le dédoublonnage aux items appartenant au flux
     */
    @Transient
    List<Flux> flux;
    
    /***
     * Détermine si le comportement de collecte est actif
     */
    protected boolean actif;

    

    public AbstrRaffineur() {
    }

    public abstract void raffinerContenu(ContentRSS i);

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    /**
     * *
     * Cette méthode est chargé de lier l'item envoyé en argument a une item raffinées. Si aucune item rafinée ne semble
     * correspondre, elle crée alors une nouvelle item raffinée.
     *
     * @param item l'item qu'il faut raffinée. C'est le plus souvent une item nouvellement capté
     */
    public void rafinerItemBrute(Item item, EntityManager em) {


        this.listComparaison.clear();
        
        // On commence par trouver les similaire
        findSimilarIteminBDD(em);

        // On les trie
        compareSimilarAndSort();

        // On dé"cide. La méthode observe l'item arrivée en premier dans la liste. 
        decide();

    }

    /**
     * *
     * Déclanche une recherche dans la base de données afin de contruire la liste de similitude
     */
    protected abstract void findSimilarIteminBDD(EntityManager em);

    /**
     * *
     * trier la {@link #listComparaison} en utilisant le comparator
     */
    protected abstract void compareSimilarAndSort();

    /**
     * *
     * Méthode chargé d'inspecter la première item dans la liste afin de décider si elle est bien un doublon par rapport
     * à l'item observé
     */
    protected abstract void decide();

    /**
     * *
     * Méthode chargé de renvoyer un float 0.0 si l'item 2 ne ressemble pas du tout à l'item 1. 100.00 Si elle est
     * strictement similaire.
     *
     * @param itemref
     * @param item2
     * @return
     */
    protected abstract float evaluerSimilitudeItem(Item itemref, Item item2);

    public List<ComparaisonItem> getListComparaison() {
        return listComparaison;
    }

    public void setListComparaison(List<ComparaisonItem> listComparaison) {
        this.listComparaison = listComparaison;
    }

    public Item getItemObserve() {
        return itemObserve;
    }

    public void setItemObserve(Item itemObserve) {
        this.itemObserve = itemObserve;
    }

    public Comparator<ComparaisonItem> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<ComparaisonItem> comparator) {
        this.comparator = comparator;
    }

    public static void main(String[] args) {
        Reflections reflections = new Reflections("rssagregator.beans.traitement");
        Set<Class<? extends AbstrRaffineur>> imp = reflections.getSubTypesOf(AbstrRaffineur.class);

        for (Iterator<Class<? extends AbstrRaffineur>> it = imp.iterator(); it.hasNext();) {
            Class<? extends AbstrRaffineur> class1 = it.next();

            System.out.println("CLASS  " + class1);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.ID != null ? this.ID.hashCode() : 0);
        return hash;
    }

    
    
    /***
     * Deux dédoublonneurs sont equaux si ils ont la même ID.
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstrRaffineur other = (AbstrRaffineur) obj;
        if (this.ID != other.ID && (this.ID == null || !this.ID.equals(other.ID))) {
            return false;
        }
        return true;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
    
        @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }
        
        /***
         * Methode publique pour obtenir un clone
         * @return
         * @throws CloneNotSupportedException 
         */
        public AbstrRaffineur getClone() throws CloneNotSupportedException{
            return (AbstrRaffineur) clone();
            
        }

    public List<Flux> getFlux() {
        return flux;
    }

    public void setFlux(List<Flux> flux) {
        this.flux = flux;
    }  
    
}
