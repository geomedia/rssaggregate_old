/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.Flux;

/**
 *
 * @author clem
 */
public class SetDonnee {

    public static void main(String[] args) {

        Flux fl1 = new Flux();
        fl1.setUrl("http://localhost/spip/spip.php?page=backend");
        fl1.setPeriodiciteCollecte(600);
        fl1.setActive(Boolean.TRUE);


        Flux fl2 = new Flux();
        fl2.setUrl("http://localhost/spip/spip.php?page=backend&id_rubrique=1");
        fl2.setPeriodiciteCollecte(600);
        fl2.setActive(false);

        Flux fl3 = new Flux();
        fl3.setUrl("http://localhost/spip/spip.php?page=backend&id_rubrique=1");
        fl3.setPeriodiciteCollecte(600);
        fl3.setActive(false);
 
        DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
        try {
            dao.creer(fl1);
        } catch (Exception ex) {
            Logger.getLogger(SetDonnee.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            dao.creer(fl2);
        } catch (Exception ex) {
            Logger.getLogger(SetDonnee.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            dao.creer(fl3);
            
    //        ListeFluxCollecteEtConfigConrante.getInstance().addFlux(fl1);
    //        ListeFluxCollecteEtConfigConrante.getInstance().addFlux(fl2);
    //        ListeFluxCollecteEtConfigConrante.getInstance().addFlux(fl3);
            
    //        ListeFluxCollecteEtConfigConrante.getInstance().addFlux(fl3);
    //        ListeFluxCollecteEtConfigConrante.getInstance().addFlux(fl3);
        } catch (Exception ex) {
            Logger.getLogger(SetDonnee.class.getName()).log(Level.SEVERE, null, ex);
        }



    }
}
