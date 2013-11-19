///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package rssagregator.services;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.Observer;
//import org.joda.time.DateTime;
//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.format.DateTimeFormatter;
//import rssagregator.beans.Flux;
//import rssagregator.beans.Item;
//import rssagregator.beans.POJOCompteItem;
//import rssagregator.beans.incident.AnomalieCollecte;
//import rssagregator.beans.incident.IncidentFactory;
//import rssagregator.dao.DAOFactory;
//import rssagregator.dao.DaoItem;
//import rssagregator.services.crud.AbstrServiceCRUD;
//import rssagregator.services.crud.ServiceCRUDFactory;
//
///**
// * Tâche permettant de vérifier le comportement d'un flux. Il s'agit de récupérer les items sur une période
// * représentative et de compter les items par jours. On vérifie ensuite si il y a des rupture (+ ou- 1/3 ) dans le
// * nombre de capture jour
// *  ---> REMPLACER PAR la {@link TacheCalculQualiteFlux}
// * @author clem
// */
//@Deprecated
//public class TacheVerifComportementFLux extends AbstrTacheSchedule<TacheVerifComportementFLux> {
//
//    Flux flux; // Le flux à observer
////    Map<Date, Integer> result;     // Hashmap de résult. pour chaque date, on a le nombre d'item
//    Float seuilMax;
//    Float seuilMin;
//    Float moy;
//
//    public TacheVerifComportementFLux(Observer s) {
//        super(s);
////        anomalie = false;
//    }
//
//    @Override
//    public TacheVerifComportementFLux call() throws Exception {
//        try {
////========================>-PREPARATION DES DONNEES-<================================            
//            this.exeption = null;
//
//            //-----> Récolte des items sur la période
//
//            DaoItem dao = DAOFactory.getInstance().getDaoItem();
//            dao.initcriteria();
//            List<Flux> lf = new ArrayList<Flux>();
//            lf.add(flux);
//            dao.setWhere_clause_Flux(lf);
//
//            // calcul de la date 1
//            DateTime dt2 = new DateTime(); // c'est la date courante
//            dt2 = dt2.minusDays(1).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);  // dt2 devient hier à 23h
//            DateTime dt1 = dt2.minusDays(7).withTime(0, 0, 0, 0);// d1 00h00:00 00 il y a une semaine 
//
//            dao.setDate1(dt1.toDate());
//            dao.setDate2(dt2.toDate());
//            dao.setOrder_by(null);
//
//            List<Item> items = dao.findCretaria();
//
////==================>-------CALCUL DU NOMBRE D'ITEM PAR JOURS--<======================
////            POJOCompteurFluxItem compteurFluxItem = new POJOCompteurFluxItem();
//            POJOCompteItem cpt = new POJOCompteItem();
//
//            cpt.setItems(items);
//            cpt.setDate1(null);
//
//
//            cpt.setFlux(flux);
//            cpt.setItems(items);
//            cpt.compte();
//            cpt.setDate1(dt1.toDate());
//            cpt.setDate2(dt2.toDate());
//
//            cpt.compte();
//            cpt.calculerMoyenne(dt1.toDate(), dt2.toDate());
//            Map<Date, Integer> mapAnomalie = cpt.detecterAnomalieParrapportAuSeuil(33);
//
//
//            //---------------------CREATION DE L'Anomalie et persistance
//            if (mapAnomalie.size() > 0) {
//                System.out.println("---------> ON A UNE ANOMALIE");
//                IncidentFactory<AnomalieCollecte> factory = new IncidentFactory<AnomalieCollecte>();
//                AbstrServiceCRUD serviceCrud = ServiceCRUDFactory.getInstance().getServiceFor(AnomalieCollecte.class);
//
//                for (Map.Entry<Date, Integer> entry : mapAnomalie.entrySet()) {
//                    Date date = entry.getKey();
//                    Integer nbtConstate = entry.getValue();
//                    AnomalieCollecte anomalieIncident = factory.getIncident(AnomalieCollecte.class, "", null);
//                    anomalieIncident.setDateDebut(date);
//                    anomalieIncident.setNombreCaptureConstate(nbtConstate);
//                    anomalieIncident.setDateDebut(new Date());
//                    anomalieIncident.setFluxLie(flux);
//                    anomalieIncident.setMessageEreur(null);
//                    serviceCrud.ajouter(anomalieIncident);
//                }
//            }
//
//        } catch (Exception e) {
//            this.exeption = e;
//        } finally {
//
//            this.setChanged();
//            this.notifyObservers();
//            return this;
//        }
//    }
//    
//    
//
//    public Flux getFlux() {
//        return flux;
//    }
//
//    public void setFlux(Flux flux) {
//        this.flux = flux;
//    }
//
//    public Float getSeuilMax() {
//        return seuilMax;
//    }
//
//    public void setSeuilMax(Float seuilMax) {
//        this.seuilMax = seuilMax;
//    }
//
//    public Float getSeuilMin() {
//        return seuilMin;
//    }
//
//    public void setSeuilMin(Float seuilMin) {
//        this.seuilMin = seuilMin;
//    }
//
//    public Float getMoy() {
//        return moy;
//    }
//
//    public void setMoy(Float moy) {
//        this.moy = moy;
//    }
//
//    
//    //TODO : Basculer ce code dans une class template
//    /***
//     * Cette méthode permet de mettre en forme l'anomalie de capture. Ce n'est pas très MVC mais les JSP ne fonctionne pas en dehors du contexte servlet request...
//     * @param anomalieCollecte
//     * @param moyenne
//     * @param seuil
//     * @return 
//     */
//    private String miseEnFormeMessage(AnomalieCollecte anomalieCollecte, Float moyenne, Integer seuil) {
//
//        if(anomalieCollecte==null){
//            throw new NullPointerException("Impossible de mettre en forme une anomalie null");
//        }
//        if(moyenne == null){
//            throw new NullPointerException("Impossible de mettre en forme avec une moyenne null");
//        }
//        
//        String message = "<p>";
//        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
//
//        message+="<h1>Annomalie de capture présumée</h1>";
//        message+="<p>FLUX : " + anomalieCollecte.getFluxLie()+"</p>";
//        message += "<p> Le : " + fmt.print(new DateTime(anomalieCollecte.getDateDebut()));
//        message += anomalieCollecte.getNombreCaptureConstate() + " items ont été capturés. La semaine précédante la moyenne des captures s'élevait à " + moyenne + ". Le seuil de tolérance maximale pour ce flux est de" + seuil + " </p>";
//        message += "<ul>";
//        message += "<li>Seuil maximal de capture attendu : "+(moyenne + moyenne * seuil/100)+"</li>";
//        message += "<li>Seuil minimal de capture attendu : "+(moyenne - moyenne * seuil/100)+"</li>";
//        message += "<li>Nombre moyen de capture jours sur la pédiode"+moyenne+"</li>";
//        message += "<li>Nombre d'item pour la journée considérée"+anomalieCollecte.getNombreCaptureConstate()+"</li>";
//        message += "</ul>";
//        message += "</p>";
//        return message;
//    }
//
////    /**
////     * *
////     * Permet de construire le message de l'incident à partir de la tache. Le message est au format html
////     *
////     * @param task
////     */
////    public void feedMessageFromTask(TacheVerifComportementFLux task) {
////
////        if (task != null && task.getResult() != null) {
////            this.messageEreur = "<ul>";
////            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
////            for (Map.Entry<Date, Integer> entry : task.getResult().entrySet()) {
////                Date date = entry.getKey();
////                DateTime dateDt = new DateTime(date);
////                Integer val = entry.getValue();
////
////                if (date != null && val != null) {
////                    messageEreur += fmt.print(dateDt) + ". Nombre d'item : " + val.toString();
////                }
////            }
////            messageEreur += "</ul>";
////            messageEreur = "<p>Moyenne attendu : " + task.getMoy() + "</p>";
////            messageEreur = "<p>Seuil min attendu : " + task.getSeuilMax() + "</p>";
////            messageEreur = "<p>Seuil max attendu : " + task.getSeuilMax() + "</p>";
////
////        }
////    }
//}
