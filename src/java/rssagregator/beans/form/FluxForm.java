/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.dao.AbstrDao;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxPeriodeCaptation;
import rssagregator.beans.FluxType;
import rssagregator.beans.Journal;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.dao.DAOComportementCollecte;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import rssagregator.dao.DaoJournal;

/**
 *
 * @author clem
 */
public class FluxForm extends AbstrForm {
//----------------Liste des variables a nourrir à partir de la requête

    String url = null;
    MediatorCollecteAction mediatorFlux = null;
    Boolean active = false;
    String htmlUrl = null;
    Journal journalLie = null;
    FluxType typeFlux = null;
    String nom = null;
    Flux parentFlux = null;
    String infoCollecte = null;

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


        if (this.action.equals("add")) {
            try {
                objEntre = type.newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Flux flux = (Flux) objEntre;



        //================================================================================================
        //............................BIND DES DONNES SI AUCUNE ERREUR 
        //================================================================================================
        if (valide) {
            System.out.println("ENREGISTREMENT ");
            this.valide = true;
            flux.setActive(active);
            flux.setUrl(url);
            flux.setHtmlUrl(htmlUrl);
            flux.setMediatorFlux(mediatorFlux);
            flux.setJournalLie(journalLie);
  
            
//            if (journalLie != null) {
//                journalLie.getFluxLie().add(flux);
//            }
//            else{
//                flux.getJournalLie().getFluxLie().remove(flux);
////                journalLie.getFluxLie().remove(flux);
//            }
            
            
            flux.setTypeFlux(typeFlux);
            if (typeFlux != null) {
                typeFlux.getFluxLie().add(flux);
            } 

            flux.setNom(nom); 
            flux.setParentFlux(parentFlux);
            flux.setInfoCollecte(infoCollecte);

            //================================================================================================
            //.................AJOUT DE DONNES NE PROVENANT PAS DE LA REQUETE
            //================================================================================================

            if (action.equals("add")) {
                String actFlux = request.getParameter("active");
                if (actFlux != null && !actFlux.isEmpty()) {
                    List<FluxPeriodeCaptation> l = new ArrayList<FluxPeriodeCaptation>();
                    FluxPeriodeCaptation p = new FluxPeriodeCaptation();
                    p.setDateDebut(new Date());
                    p.setFlux(flux);
                    l.add(p);
                    flux.setPeriodeCaptations(l);
                }
            } else { // Si c'est une action modif
                String actFlux = request.getParameter("active");
//Si c'est une demande d'activation du flux
                if (actFlux != null && !actFlux.isEmpty()) {
                    // Si le flux a déja des période de captation, on récupère la dernière
                    if (!flux.getPeriodeCaptations().isEmpty()) { // Si elle a une date de fin, on ajoute une nouvelle période de captation
                        FluxPeriodeCaptation lastperiode = flux.getPeriodeCaptations().get(flux.getPeriodeCaptations().size() - 1);
                        if (lastperiode.getDatefin() != null) {
                            FluxPeriodeCaptation nPeriode = new FluxPeriodeCaptation();
                            nPeriode.setDateDebut(new Date());
                            nPeriode.setFlux(flux);
                            flux.getPeriodeCaptations().add(nPeriode);
                        }
                    } else if (flux.getPeriodeCaptations().isEmpty()) {
                        FluxPeriodeCaptation nP = new FluxPeriodeCaptation();
                        nP.setDateDebut(new Date());
                        nP.setFlux(flux);
                        flux.getPeriodeCaptations().add(nP);
                    }
                }
                if (actFlux == null) { // Si c'est une demande de désactivation du flux
                    if (!flux.getPeriodeCaptations().isEmpty()) {
                        FluxPeriodeCaptation lastperiode = flux.getPeriodeCaptations().get(flux.getPeriodeCaptations().size() - 1);
                        lastperiode.setDatefin(new Date());
                    }
                }

            }


            if (action.equals("add")) {
                flux.setCreated(new Date());
            }

            flux.setModified(new Date());



        } else {
            for (Map.Entry<String, String[]> entry : erreurs.entrySet()) {
                String string = entry.getKey();
                String[] strings = entry.getValue();
                System.out.println("" + string + " - " + strings[0] + " - " + strings[1]);
            }
        }
//        Flux fl = (Flux) super.bind(request, objEntre, type); //To change body of generated methods, choose Tools | Templates.
        System.out.println("FLUX URL 11 : " + flux.getUrl());
        return objEntre;
    }

    public void check_url(String url) throws Exception {
        System.out.println("---> CHECK URL");
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

    @Override
    public Boolean validate(HttpServletRequest request) {
        erreurs = new HashMap<String, String[]>();
        String s;

        //----Verif de actif
        s = request.getParameter("active");
        if (s != null) {
            active = true;
        } else {
            active = false;
        }

        //-------------------Verification de l'url
        s = request.getParameter("url");
        if (s != null && !s.isEmpty() && s.matches("[hH][tT]{2}[pP][:][//].*")) {
            url = s;
        } else {
            erreurs.put("url", new String[]{"Incorrect", "Incorrect"});
        }


        //--Verif de Page HTML
        s = request.getParameter("htmlUrl");
        if (s != null && !s.isEmpty()) {
            htmlUrl = s;
        }


        //----------------Comportement de collecte
        s = request.getParameter("mediatorFlux");
        if (s != null && !s.isEmpty()) {
            //On va chercher le comportement dans la base de données
            try {
                DAOComportementCollecte dao = DAOFactory.getInstance().getDAOComportementCollecte();
                mediatorFlux = (MediatorCollecteAction) dao.find(new Long(s));
            } catch (Exception e) {
                erreurs.put("mediatorFlux", new String[]{"Incorrect", "Incorrect"});
            }
        } else {
            erreurs.put("mediatorFlux", new String[]{"Incorrect", "Incorrect"});
        }
 

        //-----------JOURNAL
        s = request.getParameter("journalLie");
        if (s != null && !s.isEmpty()) {
            try {

                DaoJournal daoj = DAOFactory.getInstance().getDaoJournal();
                Long id = new Long(s);
                if (id > 0) {
                    journalLie = (Journal) daoj.find(id);
                }

            } catch (Exception e) {
                erreurs.put("journalLie", new String[]{"Incorrect", "Incorrect"});
            }
        }

        // -------Type de flux
        s = request.getParameter("typeFlux");
        if (s != null && !s.isEmpty()) {
            try {
                DAOGenerique dao = DAOFactory.getInstance().getDAOGenerique();
                dao.setClassAssocie(FluxType.class);
                typeFlux = (FluxType) dao.find(new Long(s));
            } catch (Exception e) {
                erreurs.put("typeFlux", new String[]{"Incorrect", "Incorrect"});
            }
        }

        // Nom du flux 

        s = request.getParameter("nom");
        if (s != null) {
            if (s.isEmpty()) {
//                erreurs.put("nom", new String[]{"Incorrect", "Incorrect"});
            } else {
                nom = s;
            }
        }


        // Sous type flux de : 
        s = request.getParameter("parentFlux");
        if (s != null) {
            try {
                DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
                parentFlux = (Flux) dao.find(new Long(s));
            } catch (Exception e) {
            }
        }

        // infoCollecte
        s = request.getParameter("infoCollecte");
        if (s != null) {
            infoCollecte = s;
        }
        
        System.out.println("<-->FIN DE VAZLIDATION--<");

        if (erreurs.isEmpty()) {
            this.valide = true;
        } else {
            this.valide = false;
        }
        return this.valide;
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
