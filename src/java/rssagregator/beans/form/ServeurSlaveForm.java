/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.ServeurSlave;

/**
 * /!\ N'est plus utilisé dans la mesure ou la synchronisation a été retiré des objectifs du projet.
 * Class permettant de valider et binder les données issues de la requête dans un beans <strong>ServeurSlave</strong>
 *
 * @author clem
 * @deprecated 
 */
public class ServeurSlaveForm extends AbstrForm {

    //------------------------------------------------------
    // Les variables devant être relevée dans la requête
    private String servHost;
    private String login;
    private String pass;
    private String url;
    private String description;

    //------------------------------------------------------
    
    
    protected ServeurSlaveForm() {
        super();
    }
    
    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {

        // Instanciation du serveur slave si l'objet envoyé en arguement est null
        ServeurSlave serveurSlave = (ServeurSlave) objEntre;
        if (serveurSlave == null) {
            serveurSlave = new ServeurSlave();
        }

        // Bind des valeurs
        if (valide) {
            serveurSlave.setServHost(servHost);
            serveurSlave.setLogin(login);
            serveurSlave.setPass(pass);
            serveurSlave.setDescription(description);
            serveurSlave.setUrl(url);
        }


        // Bind manuel des paramettres
//        serveurSlave.setHost(request.getParameter("servHost"));
//        serveurSlave.setPass(request.getParameter("pass"));
//        serveurSlave.setLogin("login");

        return serveurSlave;
    }
    
    @Override
    public Boolean validate(HttpServletRequest request) {
        this.erreurs = new HashMap<String, String[]>();
        String s;
        
        s = request.getParameter("servHost");
        if (s != null) {
            this.servHost = s;
        }
        
        s = request.getParameter("login");
        if (s != null) {
            this.login = s;
        }
        
        s = request.getParameter("pass");
        if (s != null) {
            this.pass = s;
        }
        
        s = request.getParameter("description");
        if (s != null) {
            this.description = s;
        }
        
        s = request.getParameter("url");
        if (s != null) {
            if (s.matches(REG_EXP_HTTP_URL)) {
                this.url = s;
            }
            else{
                erreurs.put("url", new String[]{"URL incorrect", "ne peu"});
            }
        } else {
            erreurs.put("url", new String[]{"ne peut être null", "ne peu"});
        }
        
        if (erreurs.isEmpty()) {
            this.valide = true;
        } else {
            this.valide = false;
        }
        
        return this.valide;
    }
}
