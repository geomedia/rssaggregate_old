/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.joda.time.DateTime;
import rssagregator.beans.Flux;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.AnomalieCollecte;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;

/**
 *
 * @author clem
 */
public class ServiceCrudIncident extends ServiceCRUDBeansBasique {

    public void ajouterIncidentdeCollecte(CollecteIncident incid, Flux f, EntityManager em, Boolean persist) {



        if (em == null) {
            em = DAOFactory.getInstance().getEntityManager();
            em.getTransaction().begin();
        }

        try {
            if (incid.getClass().equals(AnomalieCollecte.class)) {
                
                System.out.println("-------------");
                System.out.println("AJOUT d'un incident");

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
                        System.out.println(" trouve");
                    }
                }
                if (!trouve) {
                    System.out.println("CREATION");
                    em.persist(incid);
                }

            }
        } catch (Exception e) {
        } finally {
            if (persist) {
                em.getTransaction().commit();
            }
        }






        // On commence par rechercher si un incident de type similaire s'est déjà produit


        // On Ajout ou l'on modifie


    }
    
    
    
    public void cloreIncidents(List<AbstrIncident> indid, EntityManager em, Boolean persist) throws Exception{
        
        
        if (em == null){
            em =DAOFactory.getInstance().getEntityManager();
        }
        
        
        if(!em.isJoinedToTransaction()){
            em.getTransaction().begin();
        }
        
        for (int i = 0; i < indid.size(); i++) {

            AbstrIncident abstrIncident = indid.get(i);
                        System.out.println("ICID ID " + abstrIncident.getID());
            abstrIncident.setDateFin(new Date());
            em.merge(abstrIncident);
            System.out.println("MERGE");
            
        }
        
        if(persist){
            System.out.println("PERSITE");
            em.getTransaction().commit();
        }
        
        
    }
}
