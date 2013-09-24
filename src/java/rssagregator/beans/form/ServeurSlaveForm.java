/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.HashMap;
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

    
    
    private String servHost;
    private String login;
    private String pass;

    public ServeurSlaveForm() {
    super();
    }
    
    
    
    
    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
        
        ServeurSlave serveurSlave = (ServeurSlave) objEntre;
        
        if(serveurSlave==null){
            serveurSlave = new ServeurSlave();
        }
        
        
        
        if (valide) {
            serveurSlave.setHost(servHost);
            serveurSlave.setLogin(login);
            serveurSlave.setPass(pass);
        }
        
        
        // Bind manuel des paramettres
        serveurSlave.setHost(request.getParameter("servHost"));
        
        
        serveurSlave.setPass(request.getParameter("pass"));
        serveurSlave.setLogin("login");
        
        //         On lance les vérifaication
//        try {
//       
//            ClemBeanUtils.check(this, serveurSlave);
//        } catch (SecurityException ex) {
//            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
//        if (erreurs.isEmpty()) {
//            resultat = "Traitement effectué";
//            valide = true;
//
//        } else {
//            resultat = "Erreur lors de la validation des données";
//            valide = false;
//        }
        
        return serveurSlave;
    }

    @Override
    public Boolean validate(HttpServletRequest request) {
        this.erreurs = new HashMap<String, String[]>();
        String s;
        s = request.getParameter("servHost");
        if (s!=null) {
            this.servHost = s;
        }
        
        s = request.getParameter("login");
        if (s != null) {
            this.login = s;
        }
        
        
         s = request.getParameter("pass");
         if (s!=null) {
            this.pass = s;
        }
         

         if (erreurs.isEmpty()) {
            return true;
        }
         else{
             return false;
         }
         
    }
}
