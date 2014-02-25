/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import rssagregator.beans.Item;

/**
 *
 * @author clem
 */
public class ComparaisonItem {
    Item itemReference;
    Item itemComparee;
    Float score;

    public Item getItemReference() {
        return itemReference;
    }

    public void setItemReference(Item itemReference) {
        this.itemReference = itemReference;
    }

    public Item getItemComparee() {
        return itemComparee;
    }

    public void setItemComparee(Item itemComparee) {
        this.itemComparee = itemComparee;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }
    
    /***
     * Génère le scrore en se basant sur la méthode evaluerSimilitudeItem du raffineur envoyé en argument
     * @param raff 
     */
    void evaluer(AbstrRaffineur raff){
        score= raff.evaluerSimilitudeItem(itemReference, itemComparee);
    }
    
}
