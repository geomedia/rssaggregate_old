/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import dao.DAOFactory;
import dao.DaoFlux;
import dao.DaoJournal;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.form.FluxForm;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxType;
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
    public static final String ATT_FLUX = "flux";
    public static final String ATT_LIST_FLUX = "listflux";
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
              
        // Un simple attribut pour que le menu brille sur la navigation courante
        request.setAttribute("navmenu", "flux");

        // récupération de l'action
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        request.setAttribute("action", action);

        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
        FluxForm fluxForm = new FluxForm(/*daoFlux*/);
        Flux flux = null;


//        // On récupère le flux dans la base de donnée si il est précisé
        String idString = request.getParameter("id");
        if (idString != null && !idString.equals("")) {
            Long id = new Long(request.getParameter("id"));
            request.setAttribute("id", id);
            flux = ListeFluxCollecteEtConfigConrante.getInstance().getflux(id);
//            flux = (Flux) daoFlux.find(id); 
        }

        // Si l'utilisateur à demander la mise à jour 
        if (action.equals("maj")) {
            ServiceCollecteur.getInstance().majManuelle(flux);
        }

        // Si il y a du post on récupère les données saisies par l'utilisateur pour éviter la resaisie de l'information
        if (request.getMethod().equals("POST")) {
            flux = (Flux) fluxForm.bind(request, flux, Flux.class);
        }

        if (action.equals("list")) {
            
            request.setAttribute(ATT_LIST_FLUX, ListeFluxCollecteEtConfigConrante.getInstance().getListFlux());
        } else if (action.equals("rem")) {
            //On stop la collecte
            
            
            ListeFluxCollecteEtConfigConrante.getInstance().removeFlux(flux);
            System.out.println("REVOME FIN");
        }


        request.setAttribute(ATT_FORM, fluxForm);
        request.setAttribute(ATT_FLUX, flux);

        // SAUVEGARDE SI INFOS 
        if (fluxForm.getValide()) {
            if (action.equals("add")) {
                ListeFluxCollecteEtConfigConrante.getInstance().addFlux(flux);

            } else if (action.equals("mod")) {

                daoFlux.modifier(flux);
                //La liste des flux doit notifier ses observeur (le collecteur) D'un changement
                ListeFluxCollecteEtConfigConrante.getInstance().forceChange();
                ListeFluxCollecteEtConfigConrante.getInstance().notifyObservers();
            }
        }

        // On a besoin de la liste des journaux pour effectuer la liste des choix
        DaoJournal daoJournal = DAOFactory.getInstance().getDaoJournal();
        List<Object> journals = daoJournal.findall();
        request.setAttribute("listjournaux", journals);


        // On a besoin de la liste des types de flux 
        DAOGenerique dAOGenerique = DAOFactory.getInstance().getDAOGenerique();
        dAOGenerique.setClassAssocie(FluxType.class);
        List<Object> listTypeFlux = dAOGenerique.findall();
        request.setAttribute("listtypeflux", listTypeFlux);


// redirection de l'utilisateur
        if (action.equals("add") && fluxForm.getValide()) {
            response.sendRedirect("flux?action=mod&id=" + flux.getID());
        } else {
            this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
        }
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
