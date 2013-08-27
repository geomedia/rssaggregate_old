///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package rssagregator.utils;
//
//import rssagregator.dao.DAOFactory;
//import rssagregator.dao.DaoFlux;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import rssagregator.beans.Flux;
//import rssagregator.beans.Journal;
//import rssagregator.dao.DAOConf;
//import rssagregator.dao.DaoJournal;
//import rssagregator.services.ServiceCollecteur;
//
///**
// *
// * @author clem
// */
//public class SetDonnee {
//
//    public static void main(String[] args) {
//
//
//        Journal j_libre = new Journal();
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/9/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/58/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/13/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/53/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/100160/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/17/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/100206/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/44/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/10/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/100226/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/11/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/18/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/12/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/14/"));
//        j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/100197/"));
//
//
//
//
//
//        ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
//        DaoJournal daoJournal = DAOFactory.getInstance().getDaoJournal();
//        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
//        DAOConf dAOConf = DAOFactory.getInstance().getDAOConf();
//
////        dAOConf.chargerDepuisBd();
//
//        daoFlux.addObserver(collecteur);
//        dAOConf.addObserver(collecteur);
//
//
//
//        try {
//            daoJournal.creer(j_libre);
//        } catch (Exception ex) {
//            Logger.getLogger(SetDonnee.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        ServiceCollecteur.getInstance().update(DAOFactory.getInstance().getDAOFlux(), null);
//
//
//
//
//
//
//    }
//}
