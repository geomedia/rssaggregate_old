/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Observable;
import rssagregator.services.tache.AbstrTacheSchedule;

/**
 *
 * @author clem
 */
public class ServiceImpl extends AbstrService {

    @Override
    protected void gererIncident(AbstrTacheSchedule tache) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof AbstrTacheSchedule) {

            logger.debug("Notification de la tache");
            // Si c'est une tâche schedule on la reschedule
            AbstrTacheSchedule tacheSchedule = (AbstrTacheSchedule) o;
            if (tacheSchedule.getSchedule()) {
                schedule(tacheSchedule);
            }
        }
    }
}
