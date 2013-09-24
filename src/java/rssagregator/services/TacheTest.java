/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.ArrayList;
import java.util.List;
import rssagregator.beans.Item;

/**
 *
 * @author clem
 */
public class TacheTest extends AbstrTacheSchedule<TacheTest> {
    
    private List<Item> item;
    
    public TacheTest(AbstrService executorService) {
        super(executorService);
    }
    
    @Override
    public TacheTest call() throws Exception {
        try {
            Thread.sleep(3000);
        } 
        catch (Exception e) {
            this.exeption = e;
        } finally {
            this.setChanged();
            this.notifyObservers();
            return this;
        }
    }
    
    public List<Item> getItem() {
        return item;
    }
    
    public void setItem(List<Item> item) {
        this.item = item;
    }
}
