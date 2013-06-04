/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import dao.DAOFactory;
import dao.DaoFlux;
import dao.DaoJournal;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import rssagregator.services.ListeFluxCollecteEtConfigConrante;
import rssagregator.services.ServiceCollecteur;

/**
 *
 * @author clem
 */
@WebServlet(name = "Flux", urlPatterns = {"/flux"})
public class FluxSrvl extends HttpServlet {

    public static final String ATT_FORM = "form";
    public static final String ATT_OBJ = "flux";
    public static final String ATT_ACTION = "action";
    public static final String ATT_LISTOBJ = "listflux";
    public static final String VUE = "/WEB-INF/fluxJsp.jsp";

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

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        Map<String, String> redirmap = null;


        DaoJournal daoJournal = DAOFactory.getInstance().getDaoJournal();
        List<Object> journals = daoJournal.findall();
        request.setAttribute("listjournaux", journals);



        // Un simple attribut pour que le menu brille sur la navigation courante
        request.setAttribute("navmenu", ATT_OBJ);
        Flux flux = null;
        FluxForm fluxForm = null;

        // récupération de l'action
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        request.setAttribute("action", action);


//        // On récupère le flux dans la base de donnée si l'id est précisé et qu'on n'a pas de parametre en post
//        if (!request.getMethod().equals("POST")) {
        String idString = request.getParameter("id");
        if (idString != null && !idString.equals("")) {
            Long id = new Long(request.getParameter("id"));
            request.setAttribute("id", id);
            flux = ListeFluxCollecteEtConfigConrante.getInstance().getflux(id);
        }
//        

        //Si action mod ou add on crée un fomulaire 
        if (action.equals("mod") || action.equals("add")) {

            fluxForm = new FluxForm();
//            fluxForm.setErreurs(new HashMap<String, String[]>());
            request.setAttribute(ATT_FORM, fluxForm);


            // On a besoin de la liste des types de flux 
            DAOGenerique dAOGenerique = DAOFactory.getInstance().getDAOGenerique();
            dAOGenerique.setClassAssocie(FluxType.class);
            List<Object> listTypeFlux = dAOGenerique.findall();
            request.setAttribute("listtypeflux", listTypeFlux);

//            // Si il y a du post on récupère les données saisies par l'utilisateur pour éviter la resaisie de l'information
            if (request.getMethod().equals("POST")) {

//                flux = (Flux) fluxForm.bind(request, flux, Flux.class);
                // On tente de binder un flux avec les données du formulaire. Ceci a pour but d'étudier les erreur du formulaire
                Flux fluxTmp = new Flux();
                fluxForm.bind(request, fluxTmp, Flux.class);
            }
        }

        // Si l'utilisateur à demander la mise à jour manuelle du flux  
        if (action.equals("add")) {

            if (fluxForm.getValide()) {
                flux = (Flux) fluxForm.bind(request, flux, Flux.class);
                ListeFluxCollecteEtConfigConrante.getInstance().addFlux(flux);
                redirmap = new HashMap<String, String>();
                redirmap.put("url", "flux?action=mod&id=" + flux.getID());
                redirmap.put("msg", "Ajout du Flux effectué.");
                request.setAttribute("redirmap", redirmap);
            }
        } else if (action.equals("mod")) {
            if (fluxForm.getValide()) {

                redirmap = new HashMap<String, String>();
                redirmap.put("url", "flux?action=mod&id=" + flux.getID());
                redirmap.put("msg", "Modification du flux effecué.");
                request.setAttribute("redirmap", redirmap);


                DaoFlux dao = DAOFactory.getInstance().getDAOFlux();

                flux = (Flux) fluxForm.bind(request, flux, Flux.class);
                dao.modifier(flux);
                //La liste des flux doit notifier ses observeur (le collecteur) D'un changement
                ListeFluxCollecteEtConfigConrante.getInstance().forceChange();
                ListeFluxCollecteEtConfigConrante.getInstance().notifyObservers();
            }
        } else if (action.equals("maj")) {

            ServiceCollecteur.getInstance().majManuelle(flux);
        } // Si l'action est liste, on récupère la liste des flux
        else if (action.equals("list")) {

            // On restreint la liste des flux affiché
            List<Flux> list = null;

            // Restriction en fonction du journal
            try {
                System.out.println("ID du journal : " + request.getParameter("journal-id"));

                Long idJournal = new Long(request.getParameter("journal-id"));

                request.setAttribute("journalid", idJournal);
                Journal journalSelection = (Journal) daoJournal.find(idJournal);
                list = ListeFluxCollecteEtConfigConrante.getInstance().findFluxParJournaux(journalSelection);


            } catch (Exception e) {
            }


            // Si la liste est null, on renvoie tous les flux
            if (list == null) {
                list = ListeFluxCollecteEtConfigConrante.getInstance().getListFlux();
            }


            request.setAttribute(ATT_LISTOBJ, list);
        } else if (action.equals("rem")) {
            // On tente de supprimer. Si une exeption est levée pendant la suppression. On redirige l'utilisateur différement
            try {
                ListeFluxCollecteEtConfigConrante.getInstance().removeFlux(flux);
                //On rediige vers la page de listing des flux.
                redirmap = new HashMap<String, String>();
                redirmap.put("url", "flux");
                redirmap.put("msg", "Suppression du flux effecué.");
                request.setAttribute("redirmap", redirmap);
            } catch (Exception e) { 
                redirmap = new HashMap<String, String>();
                redirmap.put("url", "flux?action=mod&id="+flux.getID());
                redirmap.put("msg", "ERREUR LORS DE LA SUPPRESSION DU FLUX. : " + e.toString());
                request.setAttribute("redirmap", redirmap);
            }

        }

        request.setAttribute(ATT_OBJ, flux);

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
