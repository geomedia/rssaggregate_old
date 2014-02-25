/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import rssagregator.beans.traitement.AbstrRaffineur;

/**
 *
 * @author clem
 */
@Entity
public class DoublonDe implements Serializable {
    @ManyToOne
    private Item itemDoublon;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    /**
     * *
     *
     */

    /**
     *  Référence de l'item référence : le maitre. Dans cette relation une item est doublon de celle la.
     */
    @OneToOne
    Item itemRef;
    
    
    
    /***
     * Un dédouplonnage est obtenu avec un {@link AbstrRaffineur} Lien vers celui ci.
     */
    @OneToOne
    AbstrRaffineur raffineurEmploye;
    
    
//    /**
//     * Le numéro de l'id
//     */
//    long refRaffineurEmploye;

    public Item getItemRef() {
        return itemRef;
    }

    public void setItemRef(Item itemRef) {
        this.itemRef = itemRef;
    }

//    public long getRefRaffineurEmploye() {
//        return refRaffineurEmploye;
//    }
//
//    public void setRefRaffineurEmploye(long refRaffineurEmploye) {
//        this.refRaffineurEmploye = refRaffineurEmploye;
//    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    //    @Override
    //    public int hashCode() {
    //        int hash = 7;
    //        hash = 37 * hash + (this.itemRef != null ? this.itemRef.hashCode() : 0);
    //        hash = 37 * hash + (int) (this.refRaffineurEmploye ^ (this.refRaffineurEmploye >>> 32));
    //        return hash;
    //    }
    //
    //    @Override
    //    public boolean equals(Object obj) {
    //        if (obj == null) {
    //            return false;
    //        }
    //        if (getClass() != obj.getClass()) {
    //            return false;
    //        }
    //        final DoublonDe other = (DoublonDe) obj;
    //        if (this.itemRef != other.itemRef && (this.itemRef == null || !this.itemRef.equals(other.itemRef))) {
    //            return false;
    //        }
    //        if (this.refRaffineurEmploye != other.refRaffineurEmploye) {
    //            return false;
    //        }
    //        return true;
    //    }
    //    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.itemRef != null ? this.itemRef.hashCode() : 0);
        hash = 61 * hash + (this.raffineurEmploye != null ? this.raffineurEmploye.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DoublonDe other = (DoublonDe) obj;
        if (this.itemRef != other.itemRef && (this.itemRef == null || !this.itemRef.equals(other.itemRef))) {
            return false;
        }
        if (this.raffineurEmploye != other.raffineurEmploye && (this.raffineurEmploye == null || !this.raffineurEmploye.equals(other.raffineurEmploye))) {
            return false;
        }
        return true;
    }
    
    
    
    

//    public Item getItemDoublon() {
//        return itemDoublon;
//    }
//
//    public void setItemDoublon(Item itemDoublon) {
//        this.itemDoublon = itemDoublon;
//    }

    public AbstrRaffineur getRaffineurEmploye() {
        return raffineurEmploye;
    }

    public void setRaffineurEmploye(AbstrRaffineur raffineurEmploye) {
        this.raffineurEmploye = raffineurEmploye;
    }

    public Item getItemDoublon() {
        return itemDoublon;
    }

    public void setItemDoublon(Item itemDoublon) {
        this.itemDoublon = itemDoublon;
    }
    
    /***
     * 
     * @return true si la {@link #itemDoublon} et {@link #itemRef} sont les meme
     */
    public boolean isOriginal(){
        
        if(itemDoublon.getID().equals(itemRef.getID())){
            return true;
        }
        else{
            return false;
        }
        
        
    }
    
    
}
