/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services.crud;

import rssagregator.beans.Flux;
import rssagregator.beans.FluxType;
import rssagregator.beans.Item;
import rssagregator.beans.Journal;
import rssagregator.beans.ServeurSlave;
import rssagregator.beans.UserAccount;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.traitement.MediatorCollecteAction;

/**
 * Cette factory est chargé de délivrer les instances services crud.
 *
 * @author clem
 */
public class ServiceCRUDFactory {

    private static ServiceCRUDFactory instance = new ServiceCRUDFactory();
    ServiceCRUDFlux serviceCRUDFlux = new ServiceCRUDFlux();
    ServiceCRUDBeansBasique serviceCRUDBeansBasique = new ServiceCRUDBeansBasique();
    ServiceCRUDBeansSynchro serviceCRUDBeansSynchro = new ServiceCRUDBeansSynchro();
    ServiceCRUDComportement serviceCRUDComportement = new ServiceCRUDComportement();
    ServiceCRUDJournal serviceCRUDJournal = new ServiceCRUDJournal();

    private ServiceCRUDFactory() {
   
    }

    public static ServiceCRUDFactory getInstance() {
        if (instance == null) {
            instance = new ServiceCRUDFactory();
        }
        return instance;
    }

    /**
     * *
     * Retourne le service permettant de gérer le beans envoyé en argument
     *
     * @param beans un beans, exemple un flux une item....
     * @return = le service permetant de gérer le beans envoyé. 
     * @throws Si il n'est pas possible de trouver le service approprié pour gérer le beans, on emmet une UnsupportedOperationException
     */
    public AbstrServiceCRUD getServiceFor(Class beans) throws UnsupportedOperationException{
        AbstrServiceCRUD serviceCrud = null;
        if (beans == null) {
            throw new UnsupportedOperationException("Pas de service CRUD pour un beans null");
        } else {
            if (beans.equals(Flux.class)) {
                serviceCrud = serviceCRUDFlux;
            } else if (beans.equals(Journal.class)) {
                serviceCrud = serviceCRUDJournal;
            } else if (beans.equals(FluxType.class)) {
                serviceCrud = serviceCRUDBeansSynchro;
            } else if (beans.equals(UserAccount.class)) {
                serviceCrud = serviceCRUDBeansSynchro;
            } else if (beans.equals(Item.class)) {
                serviceCrud = serviceCRUDBeansBasique;
            } else if (AbstrIncident.class.isAssignableFrom(beans)) {
                serviceCrud = serviceCRUDBeansBasique;
            } else if (beans.equals(MediatorCollecteAction.class)) {
                serviceCrud = serviceCRUDComportement;
            } else if (beans.equals(ServeurSlave.class)) {
                serviceCrud = serviceCRUDBeansBasique;
            }
        }

        if (serviceCrud != null) {
            return serviceCrud;
        } else {
            throw new UnsupportedOperationException("Il faut configurer le service approrié pour le type de beans " + beans.getClass());
        }
    }
}
