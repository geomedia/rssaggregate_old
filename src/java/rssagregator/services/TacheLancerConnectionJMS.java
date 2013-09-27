/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.Observer;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.Incidable;
import rssagregator.beans.incident.JMSPerteConnectionIncident;

/**
 * Tâche lancée périodiquement chargé de vérifier l'état de la connection JMS et
 * de relancer celle ci en ca s de besoin.
 *
 * @author clem
 */
public class TacheLancerConnectionJMS extends AbstrTacheSchedule<TacheLancerConnectionJMS> implements Incidable{
protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheLancerConnectionJMS.class);

    /**
     * *
     * Constructeur de la tâche
     *
     * @param s le service devant gérer le retour de la tâche
     */
    public TacheLancerConnectionJMS(Observer s) {
        super(s);
    }

    public TacheLancerConnectionJMS() {
    super();
    }

    
    
    
    /**
     * *
     * Lance la tâche. Cette tâche peut être périodique regarder le statut du
     * boolean périodique pour fixer ce comportement.
     *
     * @return true si la connection à pu être initialisé ou si elle était déjà
     * ok. False en cas d'erreur
     * @throws Exception
     */
    @Override
    public TacheLancerConnectionJMS call() throws Exception {

        this.exeption = null;
        ServiceSynchro serviceJMS = ServiceSynchro.getInstance();
        try {
            logger.debug("Vérification connectionJMS");
            if (!serviceJMS.statutConnection) {
                serviceJMS.openConnection();
            }
        } catch (Exception e) {
            this.exeption = e;
        } finally {
            // A la fin de son execution, la tâche se notifie auprès de son ou ses observer
            this.setChanged();
            this.notifyObservers();
            return this;
        }
    }

//    @Override
//    public void fermerLesIncidentOuvert() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public AbstrIncident getIncidenOuvert() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public Class getTypeIncident() {
        return JMSPerteConnectionIncident.class;
    }


}
