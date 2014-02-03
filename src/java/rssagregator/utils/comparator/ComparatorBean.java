/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils.comparator;

import java.util.Comparator;
import rssagregator.beans.Bean;

/**
 * Permet de comparer des been en fonction de leur ID. Pratique pour trier une liste d'item ou de flux en fonction de
 * l'id par exemple
 *
 * @author clem
 */
public class ComparatorBean implements Comparator<Bean> {

    @Override
    public int compare(Bean o1, Bean o2) {

        // Si les deux ont des id null il sont equau
        if (o1.getID() == null && o2.getID() == null) {
            return 0;
        }


        if (o1.getID() != null && o2.getID() == null) {
            return 1;
        }


        if (o1.getID() == null && o2.getID() != null) {
            return -1;
        }


        return o1.getID().compareTo(o2.getID());

    }
}
