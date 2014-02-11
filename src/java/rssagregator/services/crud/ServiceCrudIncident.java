/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.joda.time.DateTime;
import rssagregator.beans.Flux;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.AnomalieCollecte;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.beans.incident.IncidentFactory;
import rssagregator.beans.incident.PeriodeAnormale;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;

/**
 *
 * @author clem
 */
public class ServiceCrudIncident extends ServiceCRUDBeansBasique {

    /**
     * *
     * Ajoute une anomalie de collecte pour le flux envoye en argument à la date envoyé avec le nombre. Si le flux
     * possède déjà des anomalie de collecte, le méthode va les rechercher. Si on trouve des anomalie pour le jour
     * précédent alors une nouvelle période est ajoute pour l'anomalie trouvé. Si on ne peut trouvé d'nomalie jointive a
     * la date envoyé en argument , alors on va cré"er une nouvelle anomalie de collecte avec une période.
     *
     * @param flux : le flux pour lequel il faut ajouter une anomalie de collecte
     * @param d : le date de l'anomalie
     * @param nbrItem : le nombre d'item collecté constaté a consigner dans la période
     * @param em : Si le transaction doit être effectué" avec un Entity manage déja ouvert. Permet de gérer l'action
     * dans une transaction déjà ouverte
     * @param commit : Détermine si le commit est géré ou non en fin de méthode;
     */
    public void ajouterAnomaliePourJour(Flux flux, Date d, Short nbrItem, EntityManager em, boolean commit) {

        if (em == null) {
            em = DAOFactory.getInstance().getEntityManager();
            em.getTransaction().begin();
        }

        // On commence par rechercher les anomalie du flux

        // Le service va chercher les anomalie de collecte pour l'incident envoyé

        Query query = em.createQuery("SELECT i FROM i_anomaliecollecte i JOIN FETCH i.fluxLie f, i.periodeAnormale p WHERE f.ID=:fid");
        query.setParameter("fid", flux.getID());
        
        List<AnomalieCollecte> anomalie = query.getResultList();// daof.findOpenCollecteIncident(flux, AnomalieCollecte.class, null); 
        // On trie les résultat, c'était peut être possible en SQL mais bon...
        Collections.sort(anomalie);

        PeriodeAnormale p = new PeriodeAnormale();
        p.setDateAnomalie(d);
        p.setNbrItemCollecte(nbrItem);

        boolean merge = false;
        for (int i = 0; i < anomalie.size(); i++) {
            AnomalieCollecte anomalieCollecte = anomalie.get(i);
            if (anomalieCollecte.contientDate(d)) {
                merge = true;
                break;
            } else {
                boolean ajoute = anomalieCollecte.addPeriode(p);
                if (ajoute) {
                    merge = true;
                    
                    // récupération de la date de fin de l'anomalie.
                    
                    
                    
                    anomalieCollecte.setDateFin(new DateTime(d).withEarlierOffsetAtOverlap().toDate()); // On change la date de fin de l'incident par 
                    em.merge(anomalieCollecte);
                    break;
                }
            }
        }

        IncidentFactory<AnomalieCollecte> factory = new IncidentFactory<AnomalieCollecte>();

        // La la période n'a pas pu être ajouté aux anomalie existante. Alors on crée une nouvelle anomalie
        if (!merge) {
            AnomalieCollecte anomalieCollecte = factory.getIncident(AnomalieCollecte.class, "Nombre d'item anormale pour ce jour", null);
            anomalieCollecte.addPeriode(p);
            anomalieCollecte.setFluxLie(flux);
            anomalieCollecte.setDateDebut(new DateTime(d).withTimeAtStartOfDay().toDate()); // définition de la date de début et de fin de l'incident en fonction de la date anormale constatée
            anomalieCollecte.setDateFin(new DateTime(d).withEarlierOffsetAtOverlap().toDate());
            em.persist(anomalieCollecte);
        }

        if (commit) {
            em.getTransaction().commit();
        }
    }

    public void ajouterIncidentdeCollecte(CollecteIncident incid, Flux f, EntityManager em, Boolean persist) {



        if (em == null) {
            em = DAOFactory.getInstance().getEntityManager();
            em.getTransaction().begin();
        }

        try {
            if (incid.getClass().equals(AnomalieCollecte.class)) {


                // Le service va chercher les anomalie de collecte pour l'incident envoyé
                DAOIncident<AnomalieCollecte> daof = (DAOIncident<AnomalieCollecte>) DAOFactory.getInstance().getDaoFromType(AnomalieCollecte.class);


                // Si on ne trouve pas d'anomalie pour le jour de l'anomalie envoyé en argument. On enregistre une nouvel anomalie
                List<AnomalieCollecte> anomalie = daof.findOpenCollecteIncident(f, AnomalieCollecte.class, null);
                boolean trouve = false;
                for (int i = 0; i < anomalie.size(); i++) {
                    AnomalieCollecte anomalieCollecte = anomalie.get(i);



                    DateTime dtDebutAnomalieBDD = new DateTime(anomalieCollecte.getDateDebut()).withTimeAtStartOfDay();
                    DateTime dtDebutNouvelAnomalie = new DateTime(incid.getDateDebut()).withTimeAtStartOfDay();
                    if (dtDebutAnomalieBDD.isEqual(dtDebutNouvelAnomalie)) {
                        trouve = true;
                    }
                }
                if (!trouve) {
                    em.persist(incid);
                }

            }
        } catch (Exception e) {
        } finally {
            if (persist) {
                em.getTransaction().commit();
            }
        }

    }

    /**
     * *
     * Complete la date de fin de tout les incidents envoyés en arguement.
     *
     * @param indid
     * @param em
     * @param persist
     * @throws Exception
     */
    public void cloreIncidents(List<AbstrIncident> indid, EntityManager em, Boolean persist) throws Exception {

        if (em == null) {
            em = DAOFactory.getInstance().getEntityManager();
        }

        if (!em.isJoinedToTransaction()) {
            em.getTransaction().begin();
        }

        for (int i = 0; i < indid.size(); i++) {

            AbstrIncident abstrIncident = indid.get(i);

            if (abstrIncident.getDateFin() == null) {
                abstrIncident.setDateFin(new Date());
                em.merge(abstrIncident);
            }


        }
        if (persist) {
            em.getTransaction().commit();
        }

    }
}
