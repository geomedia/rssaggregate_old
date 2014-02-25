/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.dao.AbstrDao;

/**
 * Le formulaire permettant de valider et binder des donnée tirées de la requête dans un beans <strong>
 * Incident</strong>. Il y a de nombreux type d'incident (par généricité). Mais les seules paramètre modifiable sont
 * tous dans la class AbstrIncident ce sont: <ul>
 * <li>note d'incident </li>
 * <li>date fin : pour clore manuellement un incident</li>
 * </ul>
 * <p>Seul ces paramètres sont gérés par ce formulaire</p>
 *
 * @author clem
 */
public class IncidentForm extends AbstrForm {

    //----------------------------------------
    private String noteIndicent;
    private Date dateFin;

    //----------------------------------------
    protected IncidentForm() {
    }

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
        if (valide) {
            AbstrIncident incident = (AbstrIncident) objEntre;
            incident.setNoteIndicent(noteIndicent);
            
            if (dateFin != null) {
                incident.setDateFin(dateFin);
            }

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

    @Override
    public void parseListeRequete(HttpServletRequest request, AbstrDao dao) throws Exception {
        super.parseListeRequete(request, dao); //To change body of generated methods, choose Tools | Templates
    }
}
