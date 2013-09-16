/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import rssagregator.dao.AbstrDao;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxPeriodeCaptation;

/**
 *
 * @author clem
 */
public class FluxForm extends AbstrForm {

    public FluxForm(/*AbstrDao dao*/) {
//        super(dao);
    }

    @Override
    /**
     * *
     * Le bind est redéclarer pour gérer les date d'ajout et de modification du
     * flux.
     */
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
//        boolean isnew = false;
//        if (objEntre == null) {
//            isnew = true;
//        }

        Flux fl = (Flux) super.bind(request, objEntre, type); //To change body of generated methods, choose Tools | Templates.


        //Si le formulaire dit flux innactif


        //==================================================================================================================
        //....................................GESTION DES PERIODE DE CAPTATION
        //==================================================================================================================
        //Si c'est un nouveau flux , On ajoute une période de captation si le flux est actif
        if (addAction) {
            String actFlux = request.getParameter("active");
            if (actFlux != null && !actFlux.isEmpty()) {
                List<FluxPeriodeCaptation> l = new ArrayList<FluxPeriodeCaptation>();
                FluxPeriodeCaptation p = new FluxPeriodeCaptation();
                p.setDateDebut(new Date());
                p.setFlux(fl);
                l.add(p);
                fl.setPeriodeCaptations(l);
            }
        } else { // Si c'est une action modif
            String actFlux = request.getParameter("active");
//Si c'est une demande d'activation du flux
            if (actFlux != null && !actFlux.isEmpty()) {
                // Si le flux a déja des période de captation, on récupère la dernière
                if (!fl.getPeriodeCaptations().isEmpty()) { // Si elle a une date de fin, on ajoute une nouvelle période de captation
                    FluxPeriodeCaptation lastperiode = fl.getPeriodeCaptations().get(fl.getPeriodeCaptations().size() - 1);
                    if (lastperiode.getDatefin() != null) {
                        FluxPeriodeCaptation nPeriode = new FluxPeriodeCaptation();
                        nPeriode.setDateDebut(new Date());
                        nPeriode.setFlux(fl);
                        fl.getPeriodeCaptations().add(nPeriode);
                    }
                } else if (fl.getPeriodeCaptations().isEmpty()) {
                    FluxPeriodeCaptation nP = new FluxPeriodeCaptation();
                    nP.setDateDebut(new Date());
                    nP.setFlux(fl);
                    fl.getPeriodeCaptations().add(nP);
                }
            }
            if (actFlux == null) { // Si c'est une demande de désactivation du flux
                if (!fl.getPeriodeCaptations().isEmpty()) {
                    FluxPeriodeCaptation lastperiode = fl.getPeriodeCaptations().get(fl.getPeriodeCaptations().size() - 1);
                    lastperiode.setDatefin(new Date());
                }
            }

        }


        if (addAction) {
            fl.setCreated(new Date());
        }

        fl.setModified(new Date());
        System.out.println(">>>>>>>>> PERDIODE SIZE A LA FIN DU FORM : " + fl.getPeriodeCaptations().size());
        return fl;
    }

    public void check_url(String url) throws Exception {
        if (url == null || url.equals("")) {
            throw new Exception("L'url est vide");
        }
        // doit commencer par http.
        Pattern p = Pattern.compile("[hH][tT]{2}[pP][:][//].*");
        Matcher m = p.matcher(url);
        if (!m.find()) {
            throw new Exception("Doit commencer par http://");
        }
    }

    public void check_htmlUrl(String url) throws Exception {
//        if (url == null || url.equals("")) {
//            throw new Exception("L'url est vide");
//        }
        // doit commencer par http.
        if (url != null && !url.isEmpty()) {
            Pattern p = Pattern.compile("[hH][tT]{2}[pP][:][//].*");
            Matcher m = p.matcher(url);
            if (!m.find()) {
                throw new Exception("Doit commencer par http://");
            }
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
