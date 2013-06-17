/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import rssagregator.dao.AbstrDao;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author clem
 */
public class FluxForm extends AbstrForm{

    public FluxForm(/*AbstrDao dao*/) {
//        super(dao);
    }

    public void check_url(String url) throws Exception {
        if (url == null || url.equals("")) {
            throw new Exception("L'url est vide");
        }
        // doit commencer par http.
        Pattern p = Pattern.compile("[hH][tT]{2}[pP][:][//].*");
        Matcher m = p.matcher(url);
        if (!m.find()) {
            System.out.println("\"Doit commencer par http://\"");
            throw new Exception("Doit commencer par http://");
        }
    }
    
    
    //        public Flux bind(HttpServletRequest request, Flux flux) throws IllegalAccessException, InvocationTargetException {
//               if (flux == null) {
//                flux = new Flux();
//            }
//    
//            
//            return flux;
//        }
    /**
     * sauvegarde le flux dans la base de donnée en utilisant les paramettres
     * envoyés
     *
     * @param request
     * @return
     */
//    public Flux bind(HttpServletRequest request, Flux flux) {
//        // SI flux est null (cas d'un ajout, on crée un nouveau flux
//        if (flux == null) {
//            flux = new Flux();
//        }
//
//        // On hydrate/peuple le beans avec les données du formulaire
//        try {
//            ClemBeanUtils.populate(flux, request);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        try {
//            // On lance les vérifaication
//            erreurs = ClemBeanUtils.check(this, flux);
//
//        } catch (SecurityException ex) {
//            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//
//        if (erreurs.isEmpty()) {
//            resultat = "Traitement effectué";
//            valide = true;
//
//        } else {
//            resultat = "Erreur lors de la validation des données";
//            valide = false;
//        }
//        return flux;
//    }
}
