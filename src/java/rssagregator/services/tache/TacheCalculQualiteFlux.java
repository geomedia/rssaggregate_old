/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.tache;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import org.joda.time.DateTime;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxPeriodeCaptation;
import rssagregator.beans.Item;
import rssagregator.beans.POJOCompteItem;
import rssagregator.beans.incident.AnomalieCollecte;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.dao.DaoFlux;
import rssagregator.dao.DaoItem;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.crud.ServiceCRUDFactory;
import rssagregator.services.crud.ServiceCrudIncident;
import rssagregator.utils.ThreadUtils;

/**
 * <p>Cette tâche effectue différents calculs observant le nombre d'item /jour capturés par un flux. Les calculs sont
 * effectué vis à vis de la dernière {@link FluxPeriodeCaptation} du flux. Cette tache permet de calculer les données
 * permettant d'afficher une box ploat (min quartile mediane décile max). Elle calcul aussi l'indice de qualité de
 * captation.</p>
 * <p> La tache est controlé par le service : {@link ServiceCollecteur}</P>
 *
 * <p>A la fin de la tache on verifie si il n'y a pas de anomalie de collecte</p>
 *
 * @author clem
 */
public class TacheCalculQualiteFlux extends TacheImpl<TacheCalculQualiteFlux> implements TacheActionableSurUnBean {

    private Flux flux;

    //    Float indiceCaptation;
    //    Integer mediane;
    //    Integer decile;
    //    Integer quartile;
    //    Integer maximum;
    //    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheCalculQualiteFlux.class);
    //    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheCalculQualiteFlux.class);
    public TacheCalculQualiteFlux() {
    }

//    public TacheCalculQualiteFlux(Observer s) {
//        super(s);
//    }

    public Flux getFlux() {
        return flux;
    }

    public void setFlux(Flux flux) {
        this.flux = flux;
    }

    @Override
    protected void callCorps() throws Exception {

        initialiserTransaction();

//        verrouillerObjectDansLEM(flux, LockModeType.PESSIMISTIC_WRITE);
        DaoFlux daof = DAOFactory.getInstance().getDAOFlux();
        daof.setEm(em);
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        daoItem.setEm(em);


        // On charge le flux en fetchant les ressources dont on va avoir besoin
        Query q = em.createQuery("SELECT f FROM Flux AS f JOIN FETCH f.incidentsLie, f.periodeCaptations p WHERE f.ID = :idfl");
        q.setParameter("idfl", flux.getID());
        flux = (Flux) q.getSingleResult();


        FluxPeriodeCaptation period = flux.returnDerniereFluxPeriodeCaptation();// daof.findDernierePeriodeCaptation(flux);

        verrouillerObjectDansLEM(period, LockModeType.PESSIMISTIC_WRITE);



        //------------------------------CALCUL DE L'incide de captation

        try {
            Long nbSecCaptation = period.returnCaptationDuration();
            Long nbSecIncid = flux.returnIncidentDurationDurantLaPeride(period);// period.returnIncidentDuration();
            if (nbSecCaptation > 0) {
                Float indice = (100 - (nbSecIncid.floatValue() / nbSecCaptation.floatValue()));
                period.setIndiceQualiteCaptation(indice);
            } else {
                period.setIndiceQualiteCaptation(new Float(100.00));
            }

        } catch (Exception e) {
            logger.error("Erreur lors du calcul de l'indice ", e);

        }
        ThreadUtils.interruptCheck();


        //-----------------Calcul de la moyenne médiane quartiele décile....


        Date date1 = period.getDateDebut();
        Date date2 = null;
        if (period.getDatefin() != null) {
            date2 = new DateTime(period.getDatefin()).minusDays(1).withEarlierOffsetAtOverlap().toDate(); // Pour la date 2, on prend la date de fin de période - 1 jour avec heures la plus tardive    
        } else {
            date2 = new Date();
        }


        List<Item> items = daoItem.itemCaptureParleFluxDurantlaCollecte(flux, period);


        POJOCompteItem compteItem = new POJOCompteItem();
        compteItem.setFlux(flux);
        compteItem.setItems(items);
        compteItem.setDate1(date1);
        compteItem.setDate2(date2);

        compteItem.compte();
        compteItem.calculterBoxPloat();


        period.setStatMax(compteItem.getMax());
        period.setStatMin(compteItem.getMin());
        period.setStatQuartilePremier(compteItem.getQuartilePremier());
        period.setStatQuartileTrois(compteItem.getQuartileTrois());
        period.setStatMedian(compteItem.getMediane());
        period.setStatMoyenne(compteItem.getMoyenne());
        period.setStatEcartType(compteItem.getEcartType());
        period.setStatSommeItemCapture(compteItem.getSomme());


        period.setStatEcartTypeLundi(compteItem.getStatEcartypeDayOfWeek()[0]);
        period.setStatMedLundi(compteItem.getStatMedDayOfWeek()[0].floatValue());
        period.setStatMoyLundi(compteItem.getStatMoyDayOfWeek()[0]);
        
        
        

        period.setStatEcartTypeMardi(compteItem.getStatEcartypeDayOfWeek()[1]);
        period.setStatMedMardi(compteItem.getStatMedDayOfWeek()[1].floatValue());
        period.setStatMoyMardi(compteItem.getStatMoyDayOfWeek()[1]);

        period.setStatEcartTypeMercredi(compteItem.getStatEcartypeDayOfWeek()[2]);
        period.setStatMedMercredi(compteItem.getStatMoyDayOfWeek()[2]);
        period.setStatMoyMercredi(compteItem.getStatMoyDayOfWeek()[2]);

        period.setStatEcartTypeJeudi(compteItem.getStatEcartypeDayOfWeek()[3]);
        period.setStatMedJeudi(compteItem.getStatMedDayOfWeek()[3].floatValue());
        period.setStatMoyJeudi(compteItem.getStatMoyDayOfWeek()[3]);

        period.setStatEcartTypeVendredi(compteItem.getStatEcartypeDayOfWeek()[4]);
        period.setStatMedVendredi(compteItem.getStatMedDayOfWeek()[4].floatValue());
        period.setStatMoyVendredi(compteItem.getStatMoyDayOfWeek()[4]);

        period.setStatEcartTypeSamedi(compteItem.getStatEcartypeDayOfWeek()[5]);
        period.setStatMedSamedi(compteItem.getStatMedDayOfWeek()[5].floatValue());
        period.setStatMoySamedi(compteItem.getStatMoyDayOfWeek()[5]);

        period.setStatEcartTypeDimanche(compteItem.getStatEcartypeDayOfWeek()[6]);
        period.setStatMedDimanche(compteItem.getStatMedDayOfWeek()[6].floatValue());
        period.setStatMoyDimanche(compteItem.getStatMoyDayOfWeek()[6]);




        //--------------Detection des anomalies 
        compteItem.calculerMoyenne(date1, date2);
        Map<Date, Integer> anomalies = compteItem.detecterAnomalieParrapportAuSeuil(33);

        IncidentFactory<AnomalieCollecte> factory = new IncidentFactory<AnomalieCollecte>();
        ServiceCrudIncident serviceCrud = (ServiceCrudIncident) ServiceCRUDFactory.getInstance().getServiceFor(AnomalieCollecte.class);

        DAOIncident<AnomalieCollecte> daoIncident = (DAOIncident<AnomalieCollecte>) DAOFactory.getInstance().getDaoFromType(AnomalieCollecte.class);

        List<AnomalieCollecte> AnomalieDsBSS = daoIncident.findOpenCollecteIncident(flux, AnomalieCollecte.class, null);


        if (false) { // Désactivé pour le moment
            for (Map.Entry<Date, Integer> entry : anomalies.entrySet()) {
                Date date = entry.getKey();
                Integer nbtConstate = entry.getValue();
                AnomalieCollecte anomalieIncident = factory.getIncident(AnomalieCollecte.class, "Le nombre d'item capturé pour ce jour est anormalement haut ou bas", null);
                anomalieIncident.setDateDebut(date);
                anomalieIncident.setNombreCaptureConstate(nbtConstate);
                anomalieIncident.setFluxLie(flux);
                anomalieIncident.setMoyenneDesCapture(compteItem.getMoyenne());
                anomalieIncident.setSeuil(33);
                ThreadUtils.interruptCheck();
                serviceCrud.ajouterIncidentdeCollecte(anomalieIncident, flux, em, false);
            }
        }

        //------------Enregistrement
        em.merge(period); // On modifi la période

    }

    @Override
    public Object returnBeanCible() {
        return flux;
    }

    @Override
    public String toString() {
        return "TacheCalculQualiteFlux{" + "flux=" + flux + '}';
    }
}
