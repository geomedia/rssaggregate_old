package rssagregator.services.tache;

///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package rssagregator.services;
//
//import java.util.List;
//import java.util.Observer;
//import rssagregator.beans.Flux;
//import rssagregator.dao.DAOFactory;
//import rssagregator.dao.DaoFlux;
//
///**
// * Pour chaque flux la tache de vérification
// *
// * @{@link TacheVerifComportementFLux} est lancée. Il s'agit de vérifier pour chacun si on n'observe pas une chute ou
// * hausse significative du nombre d'items capturée. Cette tâche est pensée pour être exécuté tous les jours a une heure
// * fixe (définition dans le xml servicedef.xml)
// * Remplacé par la  {@link TacheCalculQualiteFlux}
// * @author clem
// */
//@Deprecated
//public class TacheVerifComportementFluxGeneral extends AbstrTacheSchedule<TacheVerifComportementFluxGeneral> {
//
//    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheVerifComportementFluxGeneral.class);
//
//    public TacheVerifComportementFluxGeneral(Observer s) {
//        super(s);
//    }
//
//    public TacheVerifComportementFluxGeneral() {
//        super();
//    }
//
//    @Override
//    public TacheVerifComportementFluxGeneral call() throws Exception {
//        logger.debug("---------------------------------------------------------");
//        logger.debug("Execution de la tache TacheVerifComportementFluxGeneral");
//
//        try {
//            //On va chercher la liste des flux devant subir la vérification. Il faut qu'ils aient la variable comportement stable et qu'il soit actif
//            DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
//            ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
//
////            dao.findCretaria(null, null, schedule, nbrTentative, nbrTentative)
////            List<Flux> listFl = dao.findCretaria(null, null, null, null, null, true, true);
//            List<Flux> listFl = dao.findall();
//
//            for (int i = 0; i < listFl.size(); i++) {
//                Flux flux = listFl.get(i);
//                if (flux.getActive()) {
//
//                    logger.debug("Iteration pour le flux : " + flux);
//                    TacheVerifComportementFLux tacheVerif = new TacheVerifComportementFLux(collecteur);
//                    tacheVerif.setFlux(flux);
//                    tacheVerif.setSchedule(false);
//                    collecteur.getExecutorService().submit(tacheVerif);
//                }
//            }
//            logger.debug("---------------------------------------------------------");
//        } catch (Exception e) {
//            this.exeption = e;
//            logger.debug("erreur lors de l'exe", e);
//            logger.debug("---------------------------------------------------------");
//        } finally {
//            this.setChanged();
//            this.notifyObservers();
//            return this;
//        }
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    public static void main(String[] args) {
//        //Test de la sélection des flux avec la DAO
//        DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
//        List<Flux> li = dao.findCretaria(null, null, null, null, null, true, true);
//        for (int i = 0; i < li.size(); i++) {
//            Flux flux = li.get(i);
//        }
//    }
//}
