/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.Flux;
import rssagregator.beans.traitement.TacheCallable;

/**
 *
 * @author clem
 */
public class TestScheduler {

    private ScheduledExecutorService poolSchedule;
    private static TestScheduler instance;

    private TestScheduler() {

        this.poolSchedule = new ScheduledThreadPoolExecutor(5);
        
    }

    public static TestScheduler getInstance() {
        if (instance == null) {
            instance = new TestScheduler();
        }
        return instance;
    }
    
    
    public void addTask(TacheCallable callable){
        poolSchedule.schedule( callable, callable.getSleep(), TimeUnit.MILLISECONDS);
    }
    

    public static void main(String[] args) {
        TestScheduler test = TestScheduler.getInstance();

        Flux f1 = new Flux();
        f1.setUrl("URL A");
        TacheCallable t1 = new TacheCallable();
        t1.setSleep(1000);
        t1.setFlux(f1);

        Flux f2 = new Flux();
        f2.setUrl("URL B");
        TacheCallable t2 = new TacheCallable();
        t2.setFlux(f2);
        t2.setSleep(5000);

            ScheduledFuture<String> scheduledFuture= test.poolSchedule.schedule(t2, 0, TimeUnit.DAYS);
        test.poolSchedule.schedule(t1, 0, TimeUnit.DAYS);
        try {
            System.out.println(""+scheduledFuture.get());
        } catch (InterruptedException ex) {
            System.out.println("CAP");
            Logger.getLogger(TestScheduler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            System.out.println("CAP2");
            Logger.getLogger(TestScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("FIN");
        
        
test.poolSchedule.shutdown();

    }
}
