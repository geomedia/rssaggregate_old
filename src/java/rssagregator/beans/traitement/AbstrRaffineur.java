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
public abstract class AbstrRaffineur {

    public AbstrRaffineur() {
    }
    
    
    
    public abstract void raffiner(Item i);
}
