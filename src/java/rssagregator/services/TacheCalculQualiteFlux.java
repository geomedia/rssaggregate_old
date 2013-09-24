/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.SortedMap;
import java.util.TreeMap;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxPeriodeCaptation;
import rssagregator.beans.Item;
import rssagregator.beans.exception.DonneeInterneCoherente;
import rssagregator.beans.incident.AnomalieCollecte;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;

/**
 * Note sur l'inice de qualité.
 *
 * @author clem
 */
public class TacheCalculQualiteFlux extends AbstrTacheSchedule<TacheCalculQualiteFlux> {

    private Flux flux;
    Float indiceCaptation;
    Integer mediane;
    Integer decile;
    Integer quartile;
    Integer maximum;
    Integer minimum;
    

    public TacheCalculQualiteFlux(Observer s) {
        super(s);
    }

    public TacheCalculQualiteFlux() {
        super();
    }
    
    

    @Override
    public TacheCalculQualiteFlux call() throws DonneeInterneCoherente, Exception {
        try {
            this.exeption = null;
            Long nbrSecondCaptation = flux.returnCaptationDuration();
            // On doit ensuite chercher tous les incident de collecte lié à ce flux. On obtient un nombre d'heure. 
            List<CollecteIncident> listIncid = flux.getIncidentsLie();
            Long timeIncid = new Long(0);
            for (int i = 0; i < listIncid.size(); i++) {
                CollecteIncident collecteIncident = listIncid.get(i);

                // Certaines exeption ne doivent pas être relevée dans le calcul.
                if (collecteIncident.getClass().equals(AnomalieCollecte.class)) {
                    AnomalieCollecte cast = (AnomalieCollecte) collecteIncident;
                    if (cast.getCauseChangementLigneEditoriale() == true || (cast.getCauseChangementLigneEditoriale() == null && cast.getCauseTechniqueSiteJournal() == null)) {
                        continue;
                    }
                }

                if (collecteIncident.getDateDebut() != null && collecteIncident.getDateFin() == null) {
                    DateTime dtDebut = new DateTime(collecteIncident.getDateDebut());
                    DateTime dtFin = new DateTime(collecteIncident.getDateFin());
                    Duration dur = new Duration(dtDebut, dtFin);
                    timeIncid += dur.getStandardSeconds();
                } else if (collecteIncident.getDateDebut() != null && collecteIncident.getDateFin() == null) {
                    DateTime dtDebut = new DateTime(collecteIncident.getDateDebut());
                    DateTime dtFin = new DateTime();
                    Duration dur = new Duration(dtDebut, dtFin);
                    timeIncid += dur.getStandardSeconds();
                }
            }
            if (timeIncid != 0) {
                Float indice = (100 - (timeIncid.floatValue() / nbrSecondCaptation.floatValue()));
                indiceCaptation = indice;
//            flux.setIndiceQualiteCaptation(indice.intValue());
            } else {
                System.out.println("ELSEE ! ");
                indiceCaptation = new Float(100.00);
            }


            SortedMap<Date, Integer> map = new TreeMap<Date, Integer>();

//            List<CompteItemJour> listCompteJour = new ArrayList<CompteItemJour>();


            //Initialisation de la map
            List<FluxPeriodeCaptation> listPeriode = flux.getPeriodeCaptations();
            for (int i = 0; i < listPeriode.size(); i++) {
                FluxPeriodeCaptation fluxPeriodeCaptation = listPeriode.get(i);
                // On boucle sur chaque jour. 
                DateTime dateDebut = new DateTime(fluxPeriodeCaptation.getDateDebut()).withTime(0, 0, 0, 0);
                 DateTime dateFin = null;
                 System.out.println("--");
                if(fluxPeriodeCaptation.getDatefin()==null){
                     dateFin = new DateTime();
                     System.out.println("NEW");
                }
                else {
                dateFin = new DateTime(fluxPeriodeCaptation.getDatefin()).withTime(23, 59, 59, 0);                    
                }

                Duration d = new Duration(dateDebut, dateFin);

                DateTime dtCurosos = new DateTime(dateDebut);
                while (dtCurosos.isBefore(dateFin)) {
                    map.put(dtCurosos.toDate(), 0);
//                    listCompteJour.add(new CompteItemJour(dtCuros.toDate(), 0));
                    dtCurosos = dtCurosos.plusDays(1);
                    System.out.println("---");
                }
            }

            //Récupération de la liste des items. 
            DaoItem dao = DAOFactory.getInstance().getDaoItem();
            dao.initcriteria();
            List<Flux> lf = new ArrayList<Flux>();
            lf.add(flux);
            dao.setWhere_clause_Flux(lf);
            List<Item> listItem = DAOFactory.getInstance().getDaoItem().findCretaria();

            for (int i = 0; i < listItem.size(); i++) {
                Item item = listItem.get(i);
                DateTime dtItem = new DateTime(item.getDateRecup()).withTime(0, 0, 0, 0);

                Integer cpt = map.get(dtItem.toDate());
                if (cpt == null) {
                    throw new DonneeInterneCoherente("Des items ont été capturée en dehors des période de captation !");
                }
                cpt++;
                map.put(dtItem.toDate(), cpt);
            }

            System.out.println("================================");
//            // On affiche les résultat
//            for (Map.Entry<Date, Integer> entry : map.entrySet()) {
//                System.out.println("%%%");
//                Date date = entry.getKey();
//                Integer integer = entry.getValue();
//                System.out.println("DATE : " + date + ". CPT : " + integer);
//            }


            // calcul de la mediane

            Object[] tab = map.entrySet().toArray();
            Collection<Integer> listInt = map.values();
            List<Integer> listou = new ArrayList<Integer>();
            listou.addAll(listInt);
            Collections.sort(listou);
            for (int i = 0; i < listou.size(); i++) {
                Integer integer = listou.get(i);
                System.out.println("INTTT : " + integer);
            }

            // On retouve la médiane
            int medianeId = listou.size() / 2;
            Float quartileId = new Float(0.25 * listou.size());
            Float decileId = new Float(0.75 * listou.size());
            

            


            this.quartile = (int) Math.round(listou.get(quartileId.intValue()));
            this.decile = (int) Math.round(listou.get(decileId.intValue()));
            this.mediane = listou.get(medianeId);
            this.maximum = listou.get(listou.size()-1);
            this.minimum = listou.get(0);
            




        } catch (Exception e) {
            this.exeption = e;
        } finally {
            this.setChanged();
            this.notifyObservers();
            return this;
        }




//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Flux getFlux() {
        return flux;
    }

    public void setFlux(Flux flux) {
        this.flux = flux;
    }

    public Float getIndiceCaptation() {
        return indiceCaptation;
    }

    public void setIndiceCaptation(Float indiceCaptation) {
        this.indiceCaptation = indiceCaptation;
    }

    public Integer getMediane() {
        return mediane;
    }

    public void setMediane(Integer mediane) {
        this.mediane = mediane;
    }

    public Integer getDecile() {
        return decile;
    }

    public void setDecile(Integer decile) {
        this.decile = decile;
    }

    public Integer getQuartile() {
        return quartile;
    }

    public void setQuartile(Integer quartile) {
        this.quartile = quartile;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    public Integer getMinimum() {
        return minimum;
    }

    public void setMinimum(Integer minimum) {
        this.minimum = minimum;
    }
    
    
    
    

    public static void main(String[] args) {
        ServiceCollecteur collecteur = ServiceCollecteur.getInstance();

        Flux f = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(55));
        System.out.println("FLUX  : " + f);
        TacheCalculQualiteFlux calculQualiteFlux = new TacheCalculQualiteFlux(collecteur);
        calculQualiteFlux.setFlux(f);

        collecteur.getExecutorService().submit(calculQualiteFlux);

    }

    public class CompteItemJour {

        Date date;
        Integer compte;

        public CompteItemJour(Date date, Integer compte) {
            this.date = date;
            this.compte = compte;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Integer getCompte() {
            return compte;
        }

        public void setCompte(Integer compte) {
            this.compte = compte;
        }
    }
}
