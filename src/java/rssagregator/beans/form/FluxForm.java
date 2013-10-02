/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import rssagregator.dao.DAOGenerique;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxType;
import rssagregator.beans.Journal;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.dao.DAOComportementCollecte;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import rssagregator.dao.DaoJournal;

/**
 * Formulaire permettant de valider et binder des données envoyée par l'utilisateur dans un beans de type Flux
 *
 * @author clem
 */
public class FluxForm extends AbstrForm {
    //------------------------------------------------------
    //Liste des variables a nourrir à partir de la requête

    String url = null;
    MediatorCollecteAction mediatorFlux = null;
    Boolean active = false;
    String htmlUrl = null;
    Journal journalLie = null;
    FluxType typeFlux = null;
    String nom = null;
    Flux parentFlux = null;
    String infoCollecte = null;
    Boolean estStable = null;
    //---------------------------------------------------

    public FluxForm() {
    }

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
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

            // Si on observe un changement de comportement.
            boolean compoHasChange = false;
            if (flux.getMediatorFlux() != null && mediatorFlux != null) {

                if (!flux.getMediatorFlux().equals(mediatorFlux)) {
                    flux.changerComportement(mediatorFlux);
                    compoHasChange = true;
                    System.out.println("COMPO CHANGEAA");
//                    System.out.println("1 parseur : " + System.identityHashCode(flux.getMediatorFlux().getParseur()));
//                    System.out.println("2 parseur: " + System.identityHashCode(mediatorFlux.getParseur()));
//
//                    System.out.println("1 media : " + System.identityHashCode(flux.getMediatorFlux()));
//                    System.out.println("2 media: " + System.identityHashCode(mediatorFlux));
                }
            }


            this.valide = true;
            flux.setActive(active);
            flux.setUrl(url);
            flux.setHtmlUrl(htmlUrl);
            flux.setMediatorFlux(mediatorFlux);
            flux.setJournalLie(journalLie);
            flux.setNom(nom);
            flux.setParentFlux(parentFlux);
            flux.setInfoCollecte(infoCollecte);
            flux.setEstStable(estStable);

            //Depuis que l'on a retirer journaux et type flux du cache cette opération n'est pas utile
//            if (journalLie != null) {
//                journalLie.getFluxLie().add(flux);
//            }
//            else{
//                flux.getJournalLie().getFluxLie().remove(flux);
////                journalLie.getFluxLie().remove(flux);
//            }

//            flux.setTypeFlux(typeFlux);
//            if (typeFlux != null) {
//                typeFlux.getFluxLie().add(flux);
//            } 

            //================================================================================================
            //.................AJOUT DE DONNES NE PROVENANT PAS DE LA REQUETE
            //================================================================================================
            //Il faut ajouter la date de création du flux ainsi que gérer les période de collecte en fonction de l'activation ou non du flux

            String actFlux = request.getParameter("active");
            if (actFlux != null && !actFlux.isEmpty() && !compoHasChange) {
                flux.activation(true);
            }
            if (actFlux == null && !compoHasChange) { // Si c'est une demande de désactivation du flux
                flux.activation(false);
            }


//            if (action.equals("add")) {
//                String actFlux = request.getParameter("active");
//                if (actFlux != null && !actFlux.isEmpty()) {
//                    List<FluxPeriodeCaptation> l = new ArrayList<FluxPeriodeCaptation>();
//                    FluxPeriodeCaptation p = new FluxPeriodeCaptation();
//                    p.setDateDebut(new Date());
//                    p.setFlux(flux);
//                    l.add(p);
//                    flux.setPeriodeCaptations(l);
//                }
//            } else { // Si c'est une action modif
//                String actFlux = request.getParameter("active");
////Si c'est une demande d'activation du flux
//                if (actFlux != null && !actFlux.isEmpty()) {
//                    flux.activation(true);
//                }
//                if (actFlux == null) { // Si c'est une demande de désactivation du flux
//                    flux.activation(false);
//                }
//            }

            //-------> Gestion de la date de création du flux
            if (action.equals("add")) {
                flux.setCreated(new Date());
            }

            //------> Dernière date de modification du flux
            flux.setModified(new Date());


        }
        return objEntre;
    }

    /**
     * *
     * Ces méthode ne sont plus utilisée. La vérification doit maintenant être éffectuée directement dans le corps de la
     * méthode validate
     *
     * @param url
     * @throws Exception
     * @deprecated
     */
    @Deprecated
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

    /**
     * *
     * Ces méthode ne sont plus utilisée. La vérification doit maintenant être éffectuée directement dans le corps de la
     * méthode validate
     *
     * @param url
     * @throws Exception
     */
    @Deprecated
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

        //------------->Verification de l'url
        s = request.getParameter("url");
        if (s != null && !s.isEmpty() && s.matches("[hH][tT]{2}[pP][:][//].*")) {
            url = s;
        } else {
            erreurs.put("url", new String[]{"Incorrect", "Incorrect"});
        }


        //-------------->Verif de Page HTML
        s = request.getParameter("htmlUrl");
        if (s != null && !s.isEmpty()) {
            htmlUrl = s;
        }

        //-------------> COMPORTEMENT DE COLLECTE
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


        //----------->JOURNAL LIE 
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

        // ---------> TYPE DE FLUX
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

        //----------> NOM DU FLUX
        s = request.getParameter("nom");
        if (s != null) {
            if (s.isEmpty()) {
            } else {
                nom = s;
            }
        }

        //-----------> FLUX PARENT
        s = request.getParameter("parentFlux");
        if (s != null) {
            try {
                DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
                parentFlux = (Flux) dao.find(new Long(s));
            } catch (Exception e) {
            }
        }

        //------> INFORMATION DE COLLECTE
        s = request.getParameter("infoCollecte");
        if (s != null) {
            infoCollecte = s;
        }


        //------> EST STABLE
        s = request.getParameter("estStable");
        estStable = false;
        if (s != null) {
            this.estStable = true;
        }

        if (erreurs.isEmpty()) {
            this.valide = true;
        } else {
            this.valide = false;
        }
        return this.valide;
    }
}
