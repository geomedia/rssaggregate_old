/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *  permet de lancer la collecte d'une liste de {@link TacheRecupCallable} les uns à la suite des autres. Permet par exemple de récupérer tous les flux d'un journal.
 * @author clem
 */
public class TacheRecupListFlux extends TacheImpl<TacheRecupListFlux> {

    List<TacheRecupCallable> taches = new ArrayList<TacheRecupCallable>();
   private ExecutorService es;
    
    Long journalId;
    

    @Override
    protected void callCorps() throws InterruptedException, Exception {

        es = Executors.newSingleThreadExecutor();

        

        for (int i = 0; i < taches.size(); i++) {
            TacheRecupCallable object = taches.get(i);
            es.submit(object);
        }

    }

    public List<TacheRecupCallable> getTaches() {
        return taches;
    }

    public void setTaches(List<TacheRecupCallable> taches) {
        this.taches = taches;
    }

    @Override
    protected TacheRecupListFlux callFinalyse() {

        try {
            es.shutdownNow();
        } catch (Exception e) {
            
        }

        return super.callFinalyse(); //To change body of generated methods, choose Tools | Templates.


    }

    public Long getJournalId() {
        return journalId;
    }

    public void setJournalId(Long journalId) {
        this.journalId = journalId;
    }

    
    
}
