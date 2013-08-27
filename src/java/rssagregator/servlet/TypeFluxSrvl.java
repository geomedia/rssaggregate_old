/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import rssagregator.dao.DAOFactory;
import java.io.IOException;
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
import rssagregator.beans.FluxType;
import rssagregator.beans.form.DAOGenerique;
import rssagregator.beans.form.FluxTypeForm;
import rssagregator.utils.ServletTool;

/**
 * Servlet permettant d'intéragir avec les type de flux. Elle utilise une dao
 * gégérique car le beans type flux est assez élémentaire. Les modification
 * effectuée par l'utilisateur sont ensuite répercuté grace au service JMS après
 * enregistrement dans la base de données. Elle coordonne les action : <ul>
 * <li>add : Ajoute un type de flux</li>
 * <li>mod : Modifi un type de flux existant</li>
 * <li>rem : Supprime un type de flux</li>
 * <li>recherche : Permet de dresser la liste des type de flux</li>
 * </ul>
 * <p>Une vue unique est utilisée par cette servlet pour générer une page HTML permettant à l'utilisateur d'éffectuer ses action : /WEB-INF/typefluxjsp.jsp</p>
 *
 * @author clem
 */
@WebServlet(name = "TypeFluxSrvl", urlPatterns = {"/TypeFluxSrvl/*"})
public class TypeFluxSrvl extends HttpServlet {

    public static final String VUE = "/WEB-INF/typefluxjsp.jsp";
    public static final String ATT_FORM = "form";
    public static final String ATT_BEAN = "obj";
    public static final String ATT_LIST_OBJ = "list";

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
        request.setAttribute("navmenu", "config");
        String action = ServletTool.configAction(request, "recherche");      // récupération de l'action

        DAOGenerique dao = DAOFactory.getInstance().getDAOGenerique();
        dao.setClassAssocie(FluxType.class);
        FluxType fluxType = null;
        FluxTypeForm form = new FluxTypeForm();

        // On récupère le flux dans la base de donnée si il est précisé
        String idString = request.getParameter("id");
        if (idString != null && !idString.equals("")) {
            Long id = new Long(idString);
            request.setAttribute("id", id);
            fluxType = (FluxType) dao.find(id);
        }

        // Si il y a du post on récupère les données saisies par l'utilisateur pour éviter la resaisie de l'information
        if (request.getMethod().equals("POST")) {
            fluxType = (FluxType) form.bind(request, fluxType, FluxType.class);
        }


        request.setAttribute(ATT_FORM, form);
        request.setAttribute(ATT_BEAN, fluxType);


        /**
         * *===================================================================================================================
         * || . . . . . . . . . . . . . . . . . . . . . . .GESTION DES ACTIONS.
         * . . . . . . . . . . . . . . . . . . . . . . . ||
         *///===================================================================================================================
        //------------------------------------------------SUPPRESSION------------------------------------------------------------
        if (action.equals("rem")) {
            try {
                dao.remove(fluxType);
                fluxType.enregistrerAupresdesService();
                fluxType.forceChangeStatut();
                fluxType.notifyObservers(action);
                ServletTool.redir(request, "TypeFluxSrvl/recherche", "Le type de Flux a été supprimé. Vous allez être redigigé vers la liste des TypeFlux", Boolean.FALSE);
            } catch (Exception ex) {
                Logger.getLogger(TypeFluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (form.getValide()) {
            //----------------------------------------AJOUT----------------------------------------------------------------------
            if (action.equals("add")) {
                try {
                    dao.creer(fluxType);
                    fluxType.enregistrerAupresdesService();
                    fluxType.forceChangeStatut();
                    fluxType.notifyObservers(action);
                    ServletTool.redir(request, "TypeFluxSrvl/mod?id=" + fluxType.getID(), "Le type de Flux a été ajouté.", Boolean.FALSE);
                } catch (Exception ex) {
                    Logger.getLogger(TypeFluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
                }

                //------------------------------------ACTION : MODIFICATION------------------------------------------------------------
            } else if (action.equals("mod")) {
                try {
                    dao.modifier(fluxType);
                    fluxType.enregistrerAupresdesService();
                    fluxType.forceChangeStatut();
                    fluxType.notifyObservers(action);
                    ServletTool.redir(request, "TypeFluxSrvl/mod?id=" + fluxType.getID(), "Modification effectée", Boolean.FALSE);
                } catch (Exception ex) {
                    ServletTool.redir(request, "TypeFluxSrvl/mod", "ERREUR LORS DE L'AJOUT DU TYPE DE FLUX. : " + ex.toString(), Boolean.TRUE);
                    Logger.getLogger(TypeFluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
                }
            } //---------------------------------------ACTION RECHERCHE---------------------------------------------------------------------
            else if (action.equals("recherche")) {
                List<Object> list = dao.findall();
                request.setAttribute(ATT_LIST_OBJ, list);
            }
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
