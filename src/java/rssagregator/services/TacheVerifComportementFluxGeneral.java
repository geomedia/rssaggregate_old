/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.List;
import java.util.Observer;
import rssagregator.beans.Flux;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;

/**
 * Cette tâche est lancée tous les jours à à 02h. Chaque flux La tache de
 * vérification est lancée. Il s'agit de vérifier pour chacun si on n'observe pas une chute ou hausse significative du nombre d'items capturée.
 *
 * @author clem
 */
public class TacheVerifComportementFluxGeneral extends AbstrTacheSchedule<TacheVerifComportementFluxGeneral> {

    public TacheVerifComportementFluxGeneral(Observer s) {
        super(s);
    }

    public TacheVerifComportementFluxGeneral() {
    super();
    }

    
    
    @Override
    public TacheVerifComportementFluxGeneral call() throws Exception {

        try {
            //On va chercher la liste des flux devant subir la vérification. Il faut qu'ils aient la variable comportement stable et qu'il soit actif
            DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
            ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
            
//            dao.findCretaria(null, null, schedule, nbrTentative, nbrTentative)
            List<Flux> listFl = dao.findCretaria(null, null, null, null, null, true, true);
            for (int i = 0; i < listFl.size(); i++) {
                Flux flux = listFl.get(i);
                TacheVerifComportementFLux tacheVerif = new TacheVerifComportementFLux(collecteur);
                tacheVerif.setFlux(flux);
                tacheVerif.setSchedule(false);
                collecteur.getExecutorService().submit(tacheVerif);
            }
        } catch (Exception e) {
            this.exeption = e;
        } finally {
            this.setChanged();
            this.notifyObservers();
            return this;
        }
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args) {
        //Test de la sélection des flux avec la DAO
        DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
        List<Flux> li = dao.findCretaria(null, null, null, null, null, true, true);
        for (int i = 0; i < li.size(); i++) {
            Flux flux = li.get(i);
            System.out.println("flux : " + flux);
        }
    }
}
