/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import rssagregator.beans.ContentRSS;
import rssagregator.beans.Item;

/**
 *
 * @author clem
 */
public abstract class AbstrRaffineur {

    public AbstrRaffineur() {
    }
    
    
    
    public abstract void raffinerContenu(ContentRSS i);
}
