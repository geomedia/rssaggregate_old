/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
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
            System.out.println("JE SUIS LANC2");
            List<Item> list = new ArrayList<Item>();
            Item it = new Item();
            it.setTitre("toto en vacance");
            list.add(it);

            this.setItem(item);
            System.out.println("Je suis une tache avec une liste d'item dedans");
            return this;
        } catch (Exception e) {

            this.exeption = e;
            return this;

        } finally {
            this.setChanged();
            this.notifyObservers();

        }
    }

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }


}
