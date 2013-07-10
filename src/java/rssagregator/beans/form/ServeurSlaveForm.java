/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.ClemBeanUtils;
import rssagregator.beans.ServeurSlave;

/**
 *
 * @author clem
 */
public class ServeurSlaveForm extends AbstrForm{

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
        
        ServeurSlave serveurSlave = (ServeurSlave) objEntre;
        
        if(serveurSlave==null){
            serveurSlave = new ServeurSlave();
        }
        
        // Bind manuel des paramettres
        serveurSlave.setServHost(request.getParameter("servHost"));
        
        
        serveurSlave.setPass(request.getParameter("pass"));
        serveurSlave.setLogin("login");
        
        //         On lance les vérifaication
        try {
       
            ClemBeanUtils.check(this, serveurSlave);
        } catch (SecurityException ex) {
            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (erreurs.isEmpty()) {
            resultat = "Traitement effectué";
            valide = true;

        } else {
            resultat = "Erreur lors de la validation des données";
            valide = false;
        }
        
        return serveurSlave;
    }
}
