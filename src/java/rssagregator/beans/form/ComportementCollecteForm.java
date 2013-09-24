/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.traitement.Dedoubloneur;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.beans.traitement.Requester;
import rssagregator.beans.traitement.RomeParse;

/**
 *
 * @author clem
 */
public class ComportementCollecteForm extends AbstrForm {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ComportementCollecteForm.class);
    private Integer requester_time_out;
    private Integer periodiciteCollecte;
    private List<String[]> RequestProperty = new ArrayList<String[]>();
    private Boolean DeboubTitle;
    private Boolean DeboudDesc;
    private Boolean dedouGUID;
    private Boolean dedoubLink;
    private Boolean dedoubDatePub;
    private Boolean dedoubCategory;
    private String comportement_nom;
    private String comportement_desc;
    private Boolean defaut;

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
        MediatorCollecteAction collecte = (MediatorCollecteAction) objEntre;
        if (collecte == null) {
            collecte = new MediatorCollecteAction();
            if (collecte.getRequesteur() == null) {
                collecte.setRequesteur(new Requester());
            }
            if (collecte.getDedoubloneur() == null) {
                collecte.setDedoubloneur(new Dedoubloneur());
            }

            if (collecte.getParseur() == null) {
                collecte.setParseur(new RomeParse());
            }
        }

        if (valide) {
            collecte.getRequesteur().setTimeOut(requester_time_out);
            collecte.setPeriodiciteCollecte(periodiciteCollecte);

            collecte.getRequesteur().setRequestProperty(null);
            for (int i = 0; i < RequestProperty.size(); i++) {
                String[] strings = RequestProperty.get(i);
                collecte.getRequesteur().addRequestProperty(strings[0], strings[1]);
            }

            collecte.getDedoubloneur().setDeboubTitle(DeboubTitle);
            collecte.getDedoubloneur().setDeboudDesc(DeboudDesc);
            collecte.getDedoubloneur().setDedouGUID(dedouGUID);
            collecte.getDedoubloneur().setDedoubLink(dedoubLink);
            collecte.getDedoubloneur().setDedoubDatePub(dedoubDatePub);
            collecte.getDedoubloneur().setDedoubCategory(dedoubCategory);

            collecte.setNom(comportement_nom);
            collecte.setDescription(comportement_desc);
            collecte.setDefaut(defaut);
        }
        return collecte;
    }

    @Override
    public Boolean validate(HttpServletRequest request) {
        // recup du time out
        String s;
                erreurs = new HashMap<String, String[]>();

        s = request.getParameter("requester_time_out");
//            collecte.getRequesteur().setTimeOut(PARAM_timeout);
        try {
            requester_time_out = new Integer(s);
        } catch (Exception e) {
        }


        // Récup de la périodicité de collecte
        try {
            s = request.getParameter("periodiciteCollecte");
            periodiciteCollecte = new Integer(s);
        } catch (Exception e) {
        }


        String[] cle = request.getParameterValues("requestPropertyCle");
        String[] valeur = request.getParameterValues("requestPropertyValue");
        int i;

        if (cle != null && valeur != null && cle.length == valeur.length) {
            for (i = 0; i < cle.length; i++) {
                if (!cle[i].trim().isEmpty() && !valeur[i].trim().isEmpty()) {
                    RequestProperty.add(new String[]{cle[i], valeur[i]});
                }
            }
        }
//        //Gestion du request properti
//        s = request.getParameter("requestProperty");
//        request.getpa
//        collecte.getRequesteur().set



        // dedoub_titre
        String dedoub_titre = request.getParameter("dedoub_titre");
        if (dedoub_titre == null || dedoub_titre.isEmpty()) {
            DeboubTitle = false;
//            collecte.getDedoubloneur().setDeboubTitle(false);
        } else {
            DeboubTitle = true;
//            collecte.getDedoubloneur().setDeboubTitle(true);
        }



        // dedoub sur la description
        s = request.getParameter("dedoub_description");
        if (s == null || s.isEmpty()) {
            DeboudDesc = false;
//            collecte.getDedoubloneur().setDeboudDesc(false);ss
        } else {
            DeboudDesc = true;
//            collecte.getDedoubloneur().setDeboudDesc(true);
        }


        // dedoub sur le guid
        s = request.getParameter("dedouGUID");
        if (s == null || s.isEmpty()) {
            dedouGUID = false;

        } else {
            dedouGUID = true;
//            collecte.getDedoubloneur().setDedouGUID(true);
        }


        s = request.getParameter("dedoubLink");
        if (s == null || s.isEmpty()) {
            dedoubLink = false;
//            collecte.getDedoubloneur().setDedoubLink(false);
        } else {
            dedoubLink = true;

        }

        s = request.getParameter("dedoubDatePub");
        if (s == null || s.isEmpty()) {
            dedoubDatePub = false;
//            collecte.getDedoubloneur().setDedoubDatePub(false);
        } else {
            dedoubDatePub = true;

        }

        s = request.getParameter("dedoubCategory");
        if (s == null || s.isEmpty()) {
            dedoubCategory = false;

        } else {
            dedoubCategory = true;
        }


        // Les paramettre propre au mediateur Comportement collect

        s = request.getParameter("comportement_nom");
        if (s != null) {
            comportement_nom = s;
        }

        s = request.getParameter("comportement_desc");
        if (s != null) {
            comportement_desc = s;
            System.out.println("comportement_desc = " + comportement_desc);
        }
        else{
            System.out.println("comportement_desc = " + comportement_desc);
        }


        // comportement par défaut
        s = request.getParameter("defaut");
        if (s == null || s.isEmpty()) {
            defaut = false;
        } else {
            defaut = true;
        }

        if (erreurs.isEmpty()) {
            this.setValide(true);
        } else {
            this.setValide(false);
        }
        System.out.println("=====================================");
        return valide;
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
