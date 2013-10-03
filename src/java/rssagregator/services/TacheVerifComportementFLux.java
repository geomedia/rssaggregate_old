/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;

/**
 *
 * @author clem
 */
public class TacheVerifComportementFLux extends AbstrTacheSchedule<TacheVerifComportementFLux> {

    Flux flux; // Le flux à observer
    Map<Date, Integer> result;     // Hashmap de résult. pour chaque date, on a le nombre d'item
    Boolean anomalie;
    Float seuilMax;
    Float seuilMin;
    Float moy;

    public TacheVerifComportementFLux(Observer s) {
        super(s);
        anomalie = false;
    }

    @Override
    public TacheVerifComportementFLux call() throws Exception {
        try {
//========================>-PREPARATION DES DONNEES-<================================            
            this.exeption = null;
            anomalie = false;
            result = new HashMap<Date, Integer>();
            DateTime dtTimeAjoutFlux = new DateTime(flux.getCreated());

            DaoItem dao = DAOFactory.getInstance().getDaoItem();
            dao.initcriteria();
            List<Flux> lf = new ArrayList<Flux>();
            lf.add(flux);
            dao.setWhere_clause_Flux(lf);

            // calcul de la date 1
            //        //calcul du delay
            DateTime dt2 = new DateTime(); // c'est la date courante
            dt2 = dt2.minusDays(1).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);  // dt2 devient hier à 23h
            DateTime dt1 = dt2.minusDays(7).withTime(0, 0, 0, 0);// d1 00h00:00 00 il y a une semaine 

            dao.setDate1(dt1.toDate());
            dao.setDate2(dt2.toDate());
            dao.setOrder_by(null);

            List<Item> items = dao.findCretaria();
            System.out.println("NBR ITEM : " + items.size());

//==================>-------CALCUL DU NOMBRE D'ITEM PAR JOURS--<======================
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                DateTime dtItem = new DateTime(item.getDateRecup());
                dtItem = dtItem.withTime(0, 0, 0, 0);

                try {
                    Integer nb = result.get(dtItem.toDate());
                    if (nb == null) {
                        result.put(dtItem.toDate(), 1);
                    } else {
                        nb++;
                        result.put(dtItem.toDate(), nb);
                    }
                } catch (Exception e) {
                    System.out.println("ERR : " + e);
                }
            }

            // Si pour certain jour, il n'y a pas eu de capture il faut ajouter au résultat
            DateTime dtIt = new DateTime(dt1); // new DateTime(1960, 01, 01, 01, 01);
            while (dtIt.isBefore(dt2)) {
                Integer nbIt = result.get(dtIt.toDate());
                if (nbIt == null) {
                    result.put(dtIt.toDate(), 0);
                }
                //incrémentation du compteur date
                dtIt = dtIt.plusDays(1);
            }

//=============================>-------RECHERCHE DES ANNOMALIE--<======================
//----Calcul de la moyenne
            float somme = 0;
            float diviseur = 0;
            dtIt = new DateTime(dt1); // new DateTime(1960, 01, 01, 01, 01);
            while (dtIt.isBefore(dt2)) {
                Integer nbJour = result.get(dtIt.toDate());
                if (nbJour != null) {
                    somme += result.get(dtIt.toDate());
                }
                //On cherche à savoir si le jour doit être considéré dans le diviseur
                if (dtIt.isAfter(dtTimeAjoutFlux)) {
                    diviseur++;
                }
                //incrémentation du compteur date
                dtIt = dtIt.plusDays(1);
            }
            moy = new Float(0);
            if (diviseur != 0) {
                moy = somme / diviseur;
            }
//-----> Calcul des SEUILS            
            // On considère comme annormale si pour une date on est à 1/3 au dessus ou dessous de la moyenne
            seuilMax = moy + (1 / 3) * moy;
            seuilMin = moy - (1 / 3) * moy;
            System.out.println("MOYENNE : " + moy);
            System.out.println("SEUIL MAX : " + seuilMax);
            System.out.println("SEUIL MIN : " + seuilMin);

//----->Detection des annomalies
            for (Map.Entry<Date, Integer> entry : result.entrySet()) {
                Date date = entry.getKey();
                DateTime dateDT = new DateTime(date);
                Float val = entry.getValue().floatValue();

                // la date doit t'elle être considée (avant date de création du flux ou non)
                if (dateDT.isAfter(dtTimeAjoutFlux)) {
                    if (val < seuilMin || val > seuilMax) {
                        anomalie = true;
                        System.out.println("ANNOMALIE ! ");
                    }
                }
            }
        } catch (Exception e) {
            this.exeption = e;
        } finally {

            this.setChanged();
            this.notifyObservers();
            return this;
        }
    }

    public Flux getFlux() {
        return flux;
    }

    public void setFlux(Flux flux) {
        this.flux = flux;
    }

    public Map<Date, Integer> getResult() {
        return result;
    }

    public void setResult(Map<Date, Integer> result) {
        this.result = result;
    }

    public Boolean getAnomalie() {
        return anomalie;
    }

    public void setAnomalie(Boolean anomalie) {
        this.anomalie = anomalie;
    }

    public Float getSeuilMax() {
        return seuilMax;
    }

    public void setSeuilMax(Float seuilMax) {
        this.seuilMax = seuilMax;
    }

    public Float getSeuilMin() {
        return seuilMin;
    }

    public void setSeuilMin(Float seuilMin) {
        this.seuilMin = seuilMin;
    }

    public Float getMoy() {
        return moy;
    }

    public void setMoy(Float moy) {
        this.moy = moy;
    }
    
    
    
    

    public static void main(String[] args) {
        ServiceCollecteur c = ServiceCollecteur.getInstance();
        TacheVerifComportementFLux verifComportementFLux = new TacheVerifComportementFLux(c);
        // On récuprère le Flux
        Flux flux = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(601));

        verifComportementFLux.setFlux(flux);
        try {
            verifComportementFLux.call();
        } catch (Exception ex) {
            Logger.getLogger(TacheVerifComportementFLux.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
