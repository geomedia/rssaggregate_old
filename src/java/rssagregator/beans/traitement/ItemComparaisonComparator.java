/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.util.Comparator;
import rssagregator.beans.ContentRSS;
import rssagregator.beans.DoublonDe;

/**
 *
 * @author clem
 */
public class ItemComparaisonComparator implements Comparator<ComparaisonItem> {

    AbstrRaffineur raff;

    public ItemComparaisonComparator(AbstrRaffineur raff) {
        this.raff = raff;
    }

    @Override
    public int compare(ComparaisonItem o1, ComparaisonItem o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null && o2 != null) {
            return -1;
        }
        if (o2 == null && o1 != null) {
            return 1;
        }

        if (o1.getScore() > o2.getScore()) {
            return -1;
        }
        if (o1.getScore() < o2.getScore()) {
            return 1;
        }
        if (o1.getScore().equals(o2.getScore())) {

            DoublonDe d1 = o1.getItemReference().returnDoublonforRaffineur(raff);
            DoublonDe d2 = o2.getItemReference().returnDoublonforRaffineur(raff);
            if (d1 != null && d2 != null) {
                if (d1.isOriginal() && !d2.isOriginal()) {
                    return -1;
                } else if (!d1.isOriginal() && d2.isOriginal()) {
                    return 1;
                }
                else{
                    return 0;
                }
            } else if(d1 != null && d2 == null){
                return -1;
            }
            else if(d1 == null && d2 != null){
                return 1;
            }
            else{
                return 0;
            }

        }

        throw new UnsupportedOperationException("Not supported yet. Le filou il faudrait revoir le test unitaire !"); //To change body of generated methods, choose Tools | Templates.
    }

    public AbstrRaffineur getRaff() {
        return raff;
    }

    public void setRaff(AbstrRaffineur raff) {
        this.raff = raff;
    }
}
