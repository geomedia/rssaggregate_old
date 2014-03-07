/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.List;
import rssagregator.beans.DoublonDe;
import rssagregator.beans.Item;
import rssagregator.beans.traitement.AbstrRaffineur;
import rssagregator.beans.traitement.ComportementCollecte;

/**
 *
 * @author clem
 */
public class TacheRaffinerPlusieurs extends TacheImpl<TacheRaffinerPlusieurs> {

    private List<Item> itemARaffiner;
    ComportementCollecte comportement;

    @Override
    protected void callCorps() throws InterruptedException, Exception {


        // Init transaction
        initialiserTransaction();
        for (int i = 0; i < itemARaffiner.size(); i++) {

            Item item = itemARaffiner.get(i);
//            TacheRaffiner2.raffiner(item, em, comportement, logger);
            // 
            for (int j = 0; j < comportement.getRaffineur().size(); j++) {
                AbstrRaffineur abstrRaffineur = comportement.getRaffineur().get(j).getClone();

                if (abstrRaffineur.isActif()) { // Si le raffineur est actif on raffine 
                    abstrRaffineur.setItemObserve(item);
                    abstrRaffineur.setFlux(item.getListFlux());
//                    abstrRaffineur.setID(new Long(1));
                    try {
                        abstrRaffineur.rafinerItemBrute(item, em);
                    } catch (Exception e) {
                        logger.debug("deconne de " + abstrRaffineur);
                    }

                    //--- Enregistrement des doublons
                    List<DoublonDe> dls = item.getDoublon();

                    for (int k = 0; k < dls.size(); k++) {
                        DoublonDe doublonDe = dls.get(k);
                        if (doublonDe.getID() == null) {
                            em.persist(doublonDe);
                        } else {
                            em.merge(doublonDe);
                        }
                    }


                }
            }

        }

        // Enregistrement
//        for (int i = 0; i < itemARaffiner.size(); i++) {
////            System.out.println("-- SAVE");
//            Item item = itemARaffiner.get(i);
//            List<DoublonDe> listDoublon = item.getDoublon();
//
//            for (int j = 0; j < listDoublon.size(); j++) {
//                DoublonDe doublonDe = listDoublon.get(j);
//                if (doublonDe.getID() == null) {
//                    em.persist(doublonDe);
//                } else {
//                    em.merge(doublonDe);
//                }
//            }
//        }

    }

    public List<Item> getItemARaffiner() {
        return itemARaffiner;
    }

    public void setItemARaffiner(List<Item> itemARaffiner) {
        this.itemARaffiner = itemARaffiner;
    }

    public ComportementCollecte getComportement() {
        return comportement;
    }

    public void setComportement(ComportementCollecte comportement) {
        this.comportement = comportement;
    }
}
