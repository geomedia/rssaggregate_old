/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.incident.FluxIncident;

/**
 *
 * @author clem
 */
public class IncidentForm extends AbstrForm{

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
        
        this.erreurs = new HashMap<String, String[]>();
               //TODO : Le bind ne fonctionne pas pour les chams hérité, exeptionnellement on va faire ca à la main
        FluxIncident incident = (FluxIncident) objEntre;
        
        String s;
        s= request.getParameter("noteIndicent");
        if(s!=null){
            incident.setNoteIndicent(s);
        }
        
        if(this.erreurs.isEmpty()){
        this.valide=true;            
        }
        else{
            this.valide = false;
        }
        return incident;
    }
}
