/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.traitement.Dedoubloneur;
import rssagregator.beans.traitement.DedoubloneurComparaisonTitre;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.beans.traitement.Requester;
import rssagregator.beans.traitement.RomeParse;

/**
 * Le formulaire permettant de valider et binder un beans MediatorCollecteAction
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
    private Boolean secondDedoub;

    protected ComportementCollecteForm() {
    }

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


            collecte.getDedoublonneur2().setEnable(secondDedoub);



            collecte.setNom(comportement_nom);
            collecte.setDescription(comportement_desc);
            collecte.setDefaut(defaut);



        }
        return collecte;
    }

    @Override
    public Boolean validate(HttpServletRequest request) {
        String s;
        erreurs = new HashMap<String, String[]>();

        //-----------------------------------------------------------------------------
        //...................RECUPERATION ET VALIDATION DES DONNEES
        //-----------------------------------------------------------------------------

        //-----------> TIME OUT
        s = request.getParameter("requester_time_out");
        if (s != null && !s.isEmpty()) {
            try {
                requester_time_out = new Integer(s);
            } catch (Exception e) {
                erreurs.put("requester_time_out", new String[]{"N'est pas un numéric", "N'est pas un chiffre"});
            }
        } else {
            erreurs.put("requester_time_out", new String[]{"Ne peut être nul", ""});
        }


        //-----------> PERIODICITE DE COLLECTE 
        s = request.getParameter("periodiciteCollecte");
        if (s != null && !s.isEmpty()) {
            try {
                periodiciteCollecte = new Integer(s);
            } catch (Exception e) {
                erreurs.put("periodiciteCollecte", new String[]{"N'est pas un numéric", ""});
            }
        } else {
            erreurs.put("periodiciteCollecte", new String[]{"Ne peut être nul", ""});
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

        //----------> DEDOUBLONNAGE TITE
        String dedoub_titre = request.getParameter("dedoub_titre");
        if (dedoub_titre == null || dedoub_titre.isEmpty()) {
            DeboubTitle = false;
        } else {
            DeboubTitle = true;
        }

        //---------> DEDOUBLONNAGE DESCRIPTION
        s = request.getParameter("dedoub_description");
        if (s == null || s.isEmpty()) {
            DeboudDesc = false;
        } else {
            DeboudDesc = true;
        }

        //----------> DEDOUBLONNAGE GUUID
        s = request.getParameter("dedouGUID");
        if (s == null || s.isEmpty()) {
            dedouGUID = false;
        } else {
            dedouGUID = true;
        }

        //-------> DEDOUBLONNAGE LINK
        s = request.getParameter("dedoubLink");
        if (s == null || s.isEmpty()) {
            dedoubLink = false;
        } else {
            dedoubLink = true;
        }

        //-------> DEDOUBLONNAGE DATE PUBLICATION
        //Rappel : on ne permet pas le dédoublonnage sur la date de récup car cette date varie par définition a chaque récupération...
        s = request.getParameter("dedoubDatePub");
        if (s == null || s.isEmpty()) {
            dedoubDatePub = false;
        } else {
            dedoubDatePub = true;
        }

        //-------> DEDOUBLONNAGE CATEGORIE
        s = request.getParameter("dedoubCategory");
        if (s == null || s.isEmpty()) {
            dedoubCategory = false;
        } else {
            dedoubCategory = true;
        }

        // Les paramettre propre au mediateur Comportement collect
        //------------> NOM DU COMPORTEMENT
        s = request.getParameter("comportement_nom");
        if (s != null && !s.isEmpty()) {
            System.out.println("--------------------COMPO : " + s);
            comportement_nom = s;
        } else {
            System.out.println("=+=+=+=++===+===");
            erreurs.put("comportement_nom", new String[]{"ne peut être null", "ne peu"});
        }

        //----------> DESCRIPTION DU COMPORTEMENT
        s = request.getParameter("comportement_desc");
        if (s != null) {
            comportement_desc = s;
            System.out.println("comportement_desc = " + comportement_desc);
        } else {
            System.out.println("comportement_desc = " + comportement_desc);
        }

        //----------> COMPORTEMNENT PAR DEFAUT
        s = request.getParameter("defaut");
        if (s == null || s.isEmpty()) {
            defaut = false;
        } else {
            defaut = true;
        }


        s = request.getParameter("secondDedoub");
        if (s == null || s.isEmpty()) {
            secondDedoub = false;
        } else {
            secondDedoub = true;
        }



        //---------------------------------------------------------------
        //---------------------------------------------------------------
        if (erreurs.isEmpty()) {
            this.setValide(true);
        } else {
            this.setValide(false);
        }
        return valide;
    }
}
