/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import rssagregator.beans.Flux;
import rssagregator.beans.FluxType;
import rssagregator.beans.Journal;
import rssagregator.beans.UserAccount;
import rssagregator.beans.incident.FluxIncident;
import rssagregator.beans.traitement.MediatorCollecteAction;

/**
 *
 * @author clem
 */
public class FORMFactory {
    
    private static FORMFactory instance = new FORMFactory();

    private FORMFactory() {
    }
    
    public static FORMFactory getInstance(){
        if(instance==null){
            instance = new FORMFactory();
        }
        return instance;
    }
    
    
    
    /***
     * Retourne un formulaire de gestion pour le beans envoyé en argument
     * @param beansClass
     * @return 
     */
    public AbstrForm getForm(Class beansClass){
        if(FluxIncident.class.isAssignableFrom(beansClass)){
            return new IncidentForm();
        }
        else if(beansClass.equals(Flux.class)){
            return new FluxForm();
        }
        else if(beansClass.equals(Journal.class)){
            return new JournalForm();
        }
        else if(beansClass.equals(MediatorCollecteAction.class)){
            return new ComportementCollecteForm();
        }
        else if(beansClass.equals(FluxType.class)){
            return new FluxTypeForm();
        }
        else if(beansClass.equals(UserAccount.class)){
            return new UserForm();
        }
        
        return null;
    }
    
    
}
