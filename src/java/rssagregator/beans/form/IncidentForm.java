/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.incident.FluxIncident;

/**
 *
 * @author clem
 */
public class IncidentForm extends AbstrForm{

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
        
               //TODO : Le bind ne fonctionne pas pour les chams hérité, exeptionnellement on va faire ca à la main
        FluxIncident incident = (FluxIncident) objEntre;
        incident.setNoteIndicent((String) request.getParameter("noteIndicent"));
        
        return null;
        
    }
    
    
    
    
}
