/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.incident.CollecteIncident;

/**
 *
 * @author clem
 */ 
public class IncidentForm extends AbstrForm{

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
        
        this.erreurs = new HashMap<String, String[]>();
               //TODO : Le bind ne fonctionne pas pour les chams hérité, exeptionnellement on va faire ca à la main
        CollecteIncident incident = (CollecteIncident) objEntre;
        
        
        //-----------------------Note de l'incident---------------------------------
        String s;
        s= request.getParameter("noteIndicent");
        if(s!=null){
            incident.setNoteIndicent(s);
        }
        
        //------------------------Cloture de l'incident-----------------------------
        //Si le boolean est coché on Met la date de fin à la date courante
        s = request.getParameter("dateFin");
        if(s!=null && !s.isEmpty()){
            incident.setDateFin(new Date());
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
