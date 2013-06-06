/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.util.concurrent.Callable;
import rssagregator.beans.Flux;

/**
 *
 * @author clem
 */
public class TacheCallable implements Callable<Flux>{

    @Override
    public Flux call() throws Exception {
        System.out.println("Tralala");
        return null
    }
    
}
