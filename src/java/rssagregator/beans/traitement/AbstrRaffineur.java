/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import rssagregator.beans.ContentRSS;

/**
 * <strong>N'est plus utilisé</strong>. Reviendra peut être par la suite
 * @author clem
 * @deprecated 
 */
public abstract class AbstrRaffineur {

    public AbstrRaffineur() {
    }
    
    
    
    public abstract void raffinerContenu(ContentRSS i);
}
