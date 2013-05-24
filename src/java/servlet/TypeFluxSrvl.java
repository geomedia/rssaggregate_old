/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import dao.DAOFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.FluxType;
import rssagregator.beans.Journal;
import rssagregator.beans.form.DAOGenerique;
import rssagregator.beans.form.FluxTypeForm;
import static servlet.ConfigSrvl.VUE;
import static servlet.JournauxSrvl.ATT_FORM;
import static servlet.JournauxSrvl.ATT_JOURNAL;
import static servlet.JournauxSrvl.ATT_LIST_JOURNAUX;

/**
 *
 * @author clem
 */
@WebServlet(name = "TypeFluxSrvl", urlPatterns = {"/TypeFluxSrvl"})
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
        request.setCharacterEncoding("UTF-8");

        request.setAttribute("navmenu", "config");


        // récupération de l'action
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        request.setAttribute("action", action);


        DAOGenerique dao = DAOFactory.getInstance().getDAOGenerique();
        dao.setClassAssocie(FluxType.class);
        FluxType fluxType = null;
        FluxTypeForm form = new FluxTypeForm();

        //        // On récupère le flux dans la base de donnée si il est précisé
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

        if (action.equals("list")) {
            List<Object> list = dao.findall();
            request.setAttribute(ATT_LIST_OBJ, list);
        }

        if (action.equals("rem")) {
            dao.remove(fluxType);
        }
        
                request.setAttribute(ATT_FORM, form);
        request.setAttribute(ATT_BEAN, fluxType);
        
        
                // SAUVEGARDE SI INFOS 
        if (form.getValide()) {
            if (action.equals("add")) {
                dao.creer(fluxType);
            } else if (action.equals("mod")) {
                System.out.println("");
                dao.modifier(fluxType);
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
