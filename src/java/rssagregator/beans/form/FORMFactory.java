/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import rssagregator.beans.Conf;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxType;
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
     * @param beansClass
     * @return
     */
    public AbstrForm getForm(Class beansClass) {
        if (CollecteIncident.class.isAssignableFrom(beansClass)) {
            return new IncidentForm();
        } else if (beansClass.equals(Flux.class)) {
            return new FluxForm();
        } else if (beansClass.equals(Journal.class)) {
            return new JournalForm();
        } else if (beansClass.equals(MediatorCollecteAction.class)) {
            return new ComportementCollecteForm();
        } else if (beansClass.equals(FluxType.class)) {
            return new FluxTypeForm();
        } else if (beansClass.equals(UserAccount.class)) {
            return new UserForm();
        }
        else if(beansClass.equals(ServeurSlave.class)){
            return new ServeurSlaveForm();
        }
        else if (AbstrIncident.class.isAssignableFrom(beansClass)) {
            return new IncidentForm();
        }
        
        else if(beansClass.equals(Conf.class)){
            System.out.println("----INSTANCIATION FORM");
            return new ConfForm();
        }
        throw new UnsupportedOperationException("Le beans envoyé en argument n'a pas de formulaire associé dans cette factory");

    }
}
