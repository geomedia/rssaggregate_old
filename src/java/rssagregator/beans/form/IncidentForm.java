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
public class IncidentForm extends AbstrForm {

    private String noteIndicent;
    private Date dateFin;

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
//        this.erreurs = new HashMap<String, String[]>();
        //TODO : Le bind ne fonctionne pas pour les chams hérité, exeptionnellement on va faire ca à la main

        if (valide) {
            CollecteIncident incident = (CollecteIncident) objEntre;
            incident.setNoteIndicent(noteIndicent);
            incident.setDateFin(dateFin);

            return incident;
        }
        return null;
    }

    @Override
    public Boolean validate(HttpServletRequest request) {

        //-----------------------Note de l'incident---------------------------------
        String s;
        s = request.getParameter("noteIndicent");
        if (s != null) {
            noteIndicent = s;
//            incident.setNoteIndicent(s);
        }

        //------------------------Cloture de l'incident-----------------------------
        //Si le boolean est coché on Met la date de fin à la date courante
        s = request.getParameter("dateFin");
        if (s != null && !s.isEmpty()) {
            dateFin = new Date();
//            incident.setDateFin(new Date());
        }

        valide = erreurs.isEmpty();
        return valide;
    }
}
