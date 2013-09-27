/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.util.Date;

/**
 * Une classe qui sera retiré du serveur en production.
 * @author clem
 */
public class DebugRecapLeveeFlux {
    
    Date date;
    Integer nbrRecup;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getNbrRecup() {
        return nbrRecup;
    }

    public void setNbrRecup(Integer nbrRecup) {
        this.nbrRecup = nbrRecup;
    }
    
}
