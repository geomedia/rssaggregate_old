/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import rssagregator.beans.Conf;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxType;
import rssagregator.beans.Item;
import rssagregator.beans.Journal;
import rssagregator.beans.ServeurSlave;
import rssagregator.beans.UserAccount;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.beans.traitement.MediatorCollecteAction;

/**
 * Une factory de formulaire. Permet notamment de simplifier l'instanciation des
 * méthode générique de la Classe ServletTool.
 *
 * @author clem
 */
public class FORMFactory {

    /**
     * *
     * Instance du singleton
     */
    private static FORMFactory instance = new FORMFactory();

    /**
     * *
     * Constructeur privé car c'est un singleton
     */
    private FORMFactory() {
    }

    /**
     * *
     * Obtention de l'instance unique de ce singleton
     *
     * @return
     */
    public static FORMFactory getInstance() {
        if (instance == null) {
            instance = new FORMFactory();
        }
        return instance;
    }

    /**
     * Retourne un formulaire de gestion pour le beans envoyé en argument
     *
     * @param beansClass : la class du beans a traiter.
     * @param action L'action demandé par l'utilisateur "add", "mod... Ce paramettre influ sur les traitements opéré par le formulaire.
     * @return Le formulaire correspondant au beans envoyé en argument et configuré pour l'action demandée.
     */
    public AbstrForm getForm(Class beansClass, String action) throws Exception {
        
        if(beansClass == null){
            throw new Exception("Il est impossible d'instancier un formulaire si on ne précise pas la class du bean");
        }
        
        AbstrForm form = null;
        
        if (CollecteIncident.class.isAssignableFrom(beansClass)) {
            form =  new IncidentForm();
        } else if (beansClass.equals(Flux.class)) {
            form =  new FluxForm();
        } else if (beansClass.equals(Journal.class)) {
            form =  new JournalForm();
        } else if (beansClass.equals(MediatorCollecteAction.class)) {
            form =  new ComportementCollecteForm();
        } else if (beansClass.equals(FluxType.class)) {
            form =  new FluxTypeForm();
        } else if (beansClass.equals(UserAccount.class)) {
            form =  new UserForm();
        }
        else if(beansClass.equals(ServeurSlave.class)){
            form =  new ServeurSlaveForm();
        }
        else if (AbstrIncident.class.isAssignableFrom(beansClass)) {
            form =  new IncidentForm();
        }
        
        else if(beansClass.equals(Conf.class)){
            form =  new ConfForm();
        }
        else if (beansClass.equals(Item.class)){
            form =  new ItemForm();
        }
        
        if(form != null){
            if(action!=null && !action.isEmpty()){
                form.setAction(action);
            }
            form.beanClass = beansClass;
            
            return form;
        }
        else{
             throw new UnsupportedOperationException("Le beans envoyé en argument n'a pas de formulaire associé dans cette factory");
        }
    }
}
