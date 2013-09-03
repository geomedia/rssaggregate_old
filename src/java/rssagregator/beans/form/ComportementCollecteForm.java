/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.HashMap;
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

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {


        erreurs = new HashMap<String, String[]>();

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

        // recup du time out
        String s;
        try {
            s = request.getParameter("requester_time_out");
            System.out.println("IIIIIIIIIIIIII : " + s);
//            collecte.getRequesteur().setTimeOut(PARAM_timeout);
            Integer intval = new Integer(s);
            collecte.getRequesteur().setTimeOut(intval);
        } catch (Exception e) {
            logger.error("Erreur lors du bind du paramettre Time Out"+e);
        }


        // Récup de la périodicité de collecte
        try {

            s = request.getParameter("periodiciteCollecte");
            Integer val = new Integer(s);
            collecte.setPeriodiciteCollecte(val);
        } catch (Exception e) {
        }


        String[] cle = request.getParameterValues("requestPropertyCle");
        String[] valeur = request.getParameterValues("requestPropertyValue");
        int i;
        collecte.getRequesteur().setRequestProperty(null);
        if (cle != null && valeur != null && cle.length == valeur.length) {
            for (i = 0; i < cle.length; i++) {
                if (!cle[i].trim().isEmpty() && !valeur[i].trim().isEmpty()) {
                    collecte.getRequesteur().addRequestProperty(cle[i], valeur[i]);
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
            collecte.getDedoubloneur().setDeboubTitle(false);
        } else {
            collecte.getDedoubloneur().setDeboubTitle(true);
        }



        // dedoub sur la description
        s = request.getParameter("dedoub_description");
        if (s == null || s.isEmpty()) {
            collecte.getDedoubloneur().setDeboudDesc(false);
        } else {
            collecte.getDedoubloneur().setDeboudDesc(true);
        }


        // dedoub sur le guid
        s = request.getParameter("dedouGUID");
        if (s == null || s.isEmpty()) {
            collecte.getDedoubloneur().setDedouGUID(false);
        } else {
            collecte.getDedoubloneur().setDedouGUID(true);
        }


        s = request.getParameter("dedoubLink");
        if (s == null || s.isEmpty()) {
            collecte.getDedoubloneur().setDedoubLink(false);
        } else {
            collecte.getDedoubloneur().setDedoubLink(true);
        }

        s = request.getParameter("dedoubDatePub");
        if (s == null || s.isEmpty()) {
            collecte.getDedoubloneur().setDedoubDatePub(false);
        } else {
            collecte.getDedoubloneur().setDedoubDatePub(true);
        }

        s = request.getParameter("dedoubCategory");
        if (s == null || s.isEmpty()) {
            collecte.getDedoubloneur().setDedoubCategory(false);
        } else {
            collecte.getDedoubloneur().setDedoubCategory(true);
        }


        // Les paramettre propre au mediateur Comportement collect
        collecte.setNom(request.getParameter("comportement_nom"));
        collecte.setDescription(request.getParameter("comportement_desc"));

        // comportement par défaut
        s = request.getParameter("defaut");
        if (s == null || s.isEmpty()) {
            collecte.setDefaut(false);
        } else {
            collecte.setDefaut(true);
        }

        if (erreurs.isEmpty()) {
            this.setValide(true);
        }
        else{
            this.setValide(false);
        }

        return collecte;

    }
}
