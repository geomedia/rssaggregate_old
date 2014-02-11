/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import org.joda.time.DateTime;

/**
 *
 * @author clem
 */
@Entity
public class PeriodeAnormale implements Serializable, Comparable<PeriodeAnormale>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    /**
     * *
     * La date de l'anomalie
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name = "dateAnomalie", nullable = false)
    Date dateAnomalie;
    /**
     * *
     * Nombre d'item collecte a la date
     */
    @Column(name = "nbrItemCollecte", nullable = false)
    Short nbrItemCollecte;

    public Date getDateAnomalie() {
        return dateAnomalie;
    }

    public void setDateAnomalie(Date dateAnomalie) {
        this.dateAnomalie = dateAnomalie;
    }

    public Short getNbrItemCollecte() {
        return nbrItemCollecte;
    }

    public void setNbrItemCollecte(Short nbrItemCollecte) {
        this.nbrItemCollecte = nbrItemCollecte;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    @Override
    public int compareTo(PeriodeAnormale o) {
        if(this.getDateAnomalie() == null){
            if(o.getDateAnomalie() == null){ // Si les deux sont null lros il sont egaux..
                return 0;
            }
            
            if(o.getDateAnomalie() != null){
                return -1; // Si celui envoé en argument n'est pas null il est supérieur a l'élément null this
            }
        }
        
        if(o.getDateAnomalie()== null){
            if(this.getDateAnomalie()==null){
                return 0;
            }
            else{
                return 1;
            }
        }
        
        // Les période sont des jours 
        DateTime dtCurrent = new DateTime(this.dateAnomalie).withTimeAtStartOfDay();
        DateTime dtObs = new DateTime(o.getDateAnomalie()).withTimeAtStartOfDay();
        
        if(dtCurrent.equals(dtObs)){
            return 0;
        }
        else if(dtCurrent.isBefore(dtObs)){
            return -1;
        }
        else if (dtCurrent.isAfter(dtObs)){
            return 1;
        }
               
        
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
