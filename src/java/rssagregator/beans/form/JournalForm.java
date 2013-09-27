/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.Journal;

/**
 * Classe permettant de valider et binder les données issues d'une requête dans
 * un bean <strong>Journal</strong>
 *
 * @author clem
 */
public class JournalForm extends AbstrForm {
    //--------------------------------------
    // Les variables devant être récupérées
    private String nom;
    private String urlAccueil;
    private String urlHtmlRecapFlux;
    private String langue;
    private String pays;
    private String fuseauHorraire;
    private String information;
    //--------------------------------------

    public JournalForm() {
        super();
    }

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
//        return super.bind(request, objEntre, type); //To change body of generated methods, choose Tools | Templates.

        //Instanciation du journal si l'objet envoyé en argument est null
        if (this.action.equals("add")) {
            try {
                objEntre = type.newInstance();
//                objEntre =  type.newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(JournalForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(JournalForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Journal journal = (Journal) objEntre;
        
        //---------------------------------------------------
        // Bind des valeurs
        journal.setNom(nom);
        journal.setUrlAccueil(urlAccueil);
        journal.setUrlHtmlRecapFlux(urlHtmlRecapFlux);
        journal.setLangue(langue);
        journal.setPays(pays);
        journal.setFuseauHorraire(fuseauHorraire);
        journal.setInformation(information);
        //---------------------------------------------------
        return journal;
    }

    @Deprecated
    public void check_nom(String nom) throws Exception {
        if (nom == null || nom.length() == 0) {
            throw new Exception("Ne peut être null");
        }
    }

    @Deprecated
    public void check_langue(String nom) throws Exception {
        if (nom == null || nom.length() == 0) {
            throw new Exception("Ne peut être null");
        }
    }

    @Override
    public Boolean validate(HttpServletRequest request) {
        //Bind du nom
        String s = request.getParameter("nom");
        if (s != null && !s.isEmpty()) {
            nom = s;
        }


        s = request.getParameter("urlAccueil");
        if (s != null && !s.isEmpty()) {
            urlAccueil = s;
        }

        s = request.getParameter("urlHtmlRecapFlux");
        if (s != null && !s.isEmpty()) {
            urlHtmlRecapFlux = s;
        }

        s = request.getParameter("langue");
        if (s != null && !s.isEmpty()) {
            langue = s;
        }

        s = request.getParameter("pays");
        if (s != null && !s.isEmpty()) {
            pays = s;
        }

        s = request.getParameter("fuseauHorraire");
        if (s != null && !s.isEmpty()) {
            fuseauHorraire = s;
        }

        s = request.getParameter("information");
        if (s != null && !s.isEmpty()) {
            information = s;
//            journal.setInformation(s);
        }
        valide = erreurs.isEmpty();
        return valide;
    }
}
