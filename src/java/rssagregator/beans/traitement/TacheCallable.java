/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.util.concurrent.Callable;
import rssagregator.beans.Flux;
import rssagregator.services.TestScheduler;

/**
 *
 * @author clem
 */
public class TacheCallable implements Callable<String>{
Integer sleep;
Flux flux;
Integer it=0;
    
    @Override
    public String call() throws Exception {
        Thread.sleep(sleep);
        it++;
        System.out.println(it+" Lancement max du flux : " + flux.getUrl());
        
//        Integer errCon = 10/0;
        if(it>0){
        throw new Exception("erreur a la main");            
        }

// A la fin la tache se réajoute dans le scheduler
        TestScheduler.getInstance().addTask(this);
        return "IT : " + it+" retour de la tache : " + flux.getUrl();
    }

    public Integer getSleep() {
        return sleep;
    }

    public void setSleep(Integer sleep) {
        this.sleep = sleep;
    }

    public Flux getFlux() {
        return flux;
    }

    public void setFlux(Flux flux) {
        this.flux = flux;
    }
    
    
    
}
