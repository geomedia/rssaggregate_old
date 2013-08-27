/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import rssagregator.dao.DaoJournal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.form.FluxForm;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxType;
import rssagregator.beans.Journal;
import rssagregator.beans.form.DAOGenerique;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.dao.DaoItem;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.ServiceGestionIncident;
import rssagregator.services.ServiceJMS;
import rssagregator.utils.ServletTool;

/**
 *
 * @author clem
 */
@WebServlet(name = "Flux", urlPatterns = {"/flux/*"})
public class FluxSrvl extends HttpServlet {

    public static final String ATT_FORM = "form";
    public static final String ATT_OBJ = "flux";
    public static final String ATT_ACTION = "action";
    public static final String ATT_LISTOBJ = "listflux";
    public String VUE = "/WEB-INF/fluxJsp.jsp";
    Map redirmap = new HashMap<String, String>(); // La redirmap est envoyée à la jsp, il s'agit d'une hash map pouvant contenir les couple clé valeur (url : l'adresse de redirection) ( msg : le message a faire parvenir à l'utilisateur)
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FluxSrvl.class);

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Liste des clause servant à criteria, ces variables seront envoyé dans la dao par la suite
        Journal journalLie = null;
        String order_by = null;
        Boolean order_desc = null;
        Integer firstResult = null;
        Integer itPrPage = null;

        redirmap = null;

        DaoJournal daoJournal = DAOFactory.getInstance().getDaoJournal();
        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();

        List<Object> journals = daoJournal.findall(); // On a besoin de la liste des journaux dans les pages html pour les menus déroulant. Il faut donc un attribut list journaux
        request.setAttribute("listjournaux", journals);

        // Un simple attribut pour que le menu brille sur la navigation courante
        request.setAttribute("navmenu", ATT_OBJ);

        //Il s'agit de la liste des flux à faire parvenir aux JSP. Cette liste sera remplie par la JSP
        List<Flux> flux = new ArrayList<Flux>();
        FluxForm fluxForm = null;

        // récupération de l'action (list, mod del ...). Si aucune action n'est précisée, alors on recherche qui correpond à la page d'acceuil de la recherhc des flux.
        String action = ServletTool.configAction(request, "recherche");

        // On récupère la sortie (html Json. Cette variable sert à configurer la vue
        String vue = request.getParameter("vue");
        if (vue == null) {
            vue = "html";
        }
        request.setAttribute("vue", vue);


//        // On récupère les flux dont l'id est précisé dans les paramettres de requête (paramettre id)
        String idString = request.getParameter("id");
        String[] tabId = request.getParameterValues("id");

        if (tabId != null && tabId.length > 0) {
            System.out.println("Multi id");
            int i;
            for (i = 0; i < tabId.length; i++) {
                try {
                    Long id = new Long(tabId[i]);
                    flux.add((Flux) daoFlux.find(id));
                } catch (Exception e) {
                }
            }
        }

        //Si action mod ou add on crée un fomulaire 
        if (action.equals("mod") || action.equals("add")) {

            fluxForm = new FluxForm();
            request.setAttribute(ATT_FORM, fluxForm);

            // On a besoin de la liste des types de flux et de la liste des comportement
            DAOGenerique dAOGenerique = DAOFactory.getInstance().getDAOGenerique();
            dAOGenerique.setClassAssocie(FluxType.class);
            request.setAttribute("listtypeflux", dAOGenerique.findall());
            request.setAttribute("listcomportement", DAOFactory.getInstance().getDAOComportementCollecte().findall());

//            // Si il y a du post on récupère les données saisies par l'utilisateur pour éviter la resaisie de l'information
            if (request.getMethod().equals("POST")) {
                // On tente de binder un flux avec les données du formulaire. Ceci a pour but d'étudier les erreur du formulaire
                Flux fluxTmp = new Flux();
                fluxForm.bind(request, fluxTmp, Flux.class);
            }
        }
        // Si l'utilisateur à demander la mise à jour manuelle du flux  
        if (action.equals("add")) {
            if (fluxForm.getValide()) {
                Flux fluxnouv = (Flux) fluxForm.bind(request, null, Flux.class);
                try {
                    daoFlux.creer(fluxnouv);
                    //On enregistre le flux auprès des services (JMS et collecteur) puis on on demande au flux de notifier le changement
                    fluxnouv.enregistrerAupresdesService();
                    fluxnouv.forceChangeStatut();
                    fluxnouv.notifyObservers("add");

                    //On met en place la redirection de l'utilisateur
                    ServletTool.redir(request, "flux/mod?id=" + fluxnouv.getID(), "Ajout du Flux effectué", false);
//                    redir
                } catch (Exception ex) {
                    ServletTool.redir(request, "flux/add", "ERREUR LORS DE L'AJOUT DU FLUX. : " + ex.toString(), true);
                    Logger.getLogger(FluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } else if (action.equals("mod")) {
            if (fluxForm.getValide()) {
                DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
                // On bind dans le flux envoyé en paramettre il s'agit forcement du premier flux de la liste des flux envoyé en id
                Flux fluxmod = (Flux) fluxForm.bind(request, flux.get(0), Flux.class);
                ServletTool.redir(request, "flux/mod?id=" + fluxmod.getID(), "Modification du flux effecué.", false);
                try {
                    dao.modifier(fluxmod);
                    
                     //Diffussion de la modification auprès des services
                    fluxmod.enregistrerAupresdesService();
                    fluxmod.notifyObservers("mod");
                    
                } catch (Exception ex) {
                    ServletTool.redir(request, "flux/add", "ERREUR LORS DE La modif DU FLUX. : " + ex.toString(), true);
                    Logger.getLogger(FluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
                }
       
                
//                daoFlux.forceNotifyObserver();
            }
        } else if (action.equals("maj")) {
            try {
                ServiceCollecteur.getInstance().majManuellAll(flux);
            } catch (Exception ex) {
                AbstrIncident incid = ServiceGestionIncident.getInstance().gererIncident(ex, flux);
                ServletTool.redir(request, "flux/maj", "ERREUR LORS DE La récup DU FLUX. : " + ex.toString(), true);
            }

        } // Si l'action est liste, on récupère la liste des flux
        else if (action.equals("list")) {
            // On restreint la liste des flux affiché
            List<Flux> list = null;

            // Restriction en fonction du journal
            try {
                Long idJournal = new Long(request.getParameter("journalid"));
                request.setAttribute("journalid", idJournal);
                journalLie = (Journal) daoJournal.find(idJournal);
            } catch (Exception e) {
                logger.debug(e);
            }
            //On récupère le nombre max d'item
            Integer nbItem = daoFlux.findNbMax(journalLie);
            request.setAttribute("nbitem", nbItem);
            System.out.println("nbitem" + nbItem);


            // On récupère le nombre d'item par page
            try {
                itPrPage = new Integer(request.getParameter("itPrPage"));
            } catch (Exception e) {
                itPrPage = 30;
            }
            request.setAttribute("itPrPage", itPrPage);

            //On restreint les items à trouver dans la recherche
            try {
                firstResult = new Integer(request.getParameter("firstResult"));
                request.setAttribute("firstResult", firstResult);
            } catch (Exception e) {
                firstResult = 0;
                System.out.println("YYY" + request.getParameter("firstResult"));
                System.out.println("" + e);
            }

            list = daoFlux.findCretaria(journalLie, order_by, order_desc, firstResult, itPrPage);

            request.setAttribute(ATT_LISTOBJ, list);
        } else if (action.equals("rem")) {
            // On tente de supprimer. Si une exeption est levée pendant la suppression. On redirige l'utilisateur différement
            try {
                daoFlux.removeall(flux);
                
//                daoFlux.forceNotifyObserver();
                
                ServletTool.redir(request, "flux", "Suppression du flux effecué.", false);
            } catch (Exception e) {
                ServletTool.redir(request, "flux?action=mod&id=", "ERREUR LORS DE LA SUPPRESSION DU FLUX. : " + e.toString(), true);
            }
        }

        if (action.equals("maj")) {
            request.setAttribute(ATT_OBJ, flux);
        } else {
            if (flux != null && flux.size() == 1) {
                request.setAttribute(ATT_OBJ, flux.get(0));
            } else if (flux != null && flux.size() == 0) {
                request.setAttribute(ATT_OBJ, new Flux());
            } else {
                request.setAttribute(ATT_OBJ, flux);
            }
        }

        //------------------------------------------------------------------------
        //              Gestion de la VUE et de la redirection 
        //------------------------------------------------------------------------
        
        if (vue.equals("json")) {
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "inline");
            VUE = "/WEB-INF/fluxJSON.jsp";
        } else if (vue.equals("opml")) {
            response.setContentType("application/xml;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            VUE = "/WEB-INF/fluxOPML.jsp";
        } else if (vue.equals("jsondesc")) {
            VUE = "/WEB-INF/fluxJSONDesc.jsp";
        } 
        else if(vue.equals("fluxXMLsync")){
            VUE ="/WEB-INF/fluxXMLsync.jsp";
            System.out.println("coucou");
        }
        else {
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            VUE = "/WEB-INF/fluxHTML.jsp";
        }
        this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
    }


// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
