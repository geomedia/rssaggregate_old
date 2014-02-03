/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Observable;
import rssagregator.services.tache.AbstrTache;

/**
 *
 * @author clem
 */
public class ServiceImpl extends AbstrService {

//    @Override
//    protected void gererIncident(AbstrTache tache) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof AbstrTache) {

            AbstrTache castTache = (AbstrTache) o;

//            logger.debug("Notification de la tache");

            // Si c'est une tache schedulé et pas annuler un demande la gestion du producteur de tache
            if (castTache.getSchedule() && !castTache.getAnnuler()) {
                this.tacheProducteur.produire(castTache);
            }
        }
    }
}
