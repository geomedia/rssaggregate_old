/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.sql.Timestamp;
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
import static rssagregator.beans.form.AbstrForm.ERR_NE_PEUT_ETRE_NULL;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.dao.DAOComportementCollecte;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOGenerique;
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

    protected FluxForm() {
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
            flux.setTypeFlux(typeFlux);


            //-------> Gestion de la date de création du flux
            if (action.equals("add")) {
                flux.setCreated(new Date());
            }

            //------> Dernière date de modification du flux
//            flux.setModified(new Timestamp(n));
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
        if (url == null || url.isEmpty()) {
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
        if (s != null && !s.isEmpty()) {
            if (!s.matches(REG_EXP_HTTP_URL)) {
                erreurs.put("url", new String[]{"URL Incorrect", "Incorrect"});
            } else {
                url = s;
            }
        } else {
            erreurs.put("url", new String[]{"Ne peut être null", "Incorrect"});
        }


        //-------------->Verif de Page HTML
        s = request.getParameter("htmlUrl");
        if (s != null && !s.isEmpty()) {
            if (!s.matches(REG_EXP_HTTP_URL)) {
                erreurs.put("htmlUrl", new String[]{"URL incorrect", "Incorrect"});
            } else {
                htmlUrl = s;
            }

        }

        //-------------> COMPORTEMENT DE COLLECTE
        s = request.getParameter("mediatorFlux");
        if (s != null && !s.isEmpty()) {
            //On va chercher le comportement dans la base de données
            try {
                DAOComportementCollecte dao = DAOFactory.getInstance().getDAOComportementCollecte();
                mediatorFlux = (MediatorCollecteAction) dao.find(new Long(s));
            } catch (Exception e) {
                erreurs.put("mediatorFlux", new String[]{"Le comportement saisi n'a pu être trouvé", "Incorrect"});
            }
        } else {
            erreurs.put("mediatorFlux", new String[]{ERR_NE_PEUT_ETRE_NULL, ERR_NE_PEUT_ETRE_NULL});
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
        else{
            erreurs.put("typeFlux", new String[]{ERR_NE_PEUT_ETRE_NULL, ERR_NE_PEUT_ETRE_NULL});
        }
        

        //----------> NOM DU FLUX
        s = request.getParameter("nom");
        if (s != null && !s.isEmpty()) {
            if (!s.matches("^[a-zA-Z0-9 -]*$")) {
                erreurs.put("nom", new String[]{"N'utilisez que des caractères alpha numéric", "Incorrect"});
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
