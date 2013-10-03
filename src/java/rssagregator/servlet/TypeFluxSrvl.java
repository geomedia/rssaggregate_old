/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.FluxType;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOGenerique;
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
 * <p>Une vue unique est utilisée par cette servlet pour générer une page HTML
 * permettant à l'utilisateur d'éffectuer ses action :
 * /WEB-INF/typefluxjsp.jsp</p>
 *
 * @author clem
 */
@WebServlet(name = "TypeFluxSrvl", urlPatterns = {"/TypeFluxSrvl/*"})
public class TypeFluxSrvl extends HttpServlet {

    public static final String VUE = "/WEB-INF/typefluxjsp.jsp";
    public static final String ATT_FORM_JSP = "form";
    public static final String ATT_BEAN_JSP = "bean";
    public static final String ATT_LIST_OBJ = "list";
    public static final String ATT_SRVLT_NAME = "TypeFluxSrvl";

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
        request.setAttribute("navmenu", "typeflux");
        String action = ServletTool.configAction(request, "recherche");      // récupération de l'action

        DAOGenerique dao = DAOFactory.getInstance().getDAOGenerique();
        dao.setClassAssocie(FluxType.class);

        request.setAttribute("srlvtname", ATT_SRVLT_NAME);



        /**
         * *===================================================================================================================
         * || . . . . . . . . . . . . . . . . . . . . . . .GESTION DES ACTIONS.
         *///===================================================================================================================
        //------------------------------------------------SUPPRESSION------------------------------------------------------------
        if (action.equals("rem")) {
            ServletTool.actionREM(request, FluxType.class, Boolean.TRUE);

        }

//        if (form.getValide()) {
        //----------------------------------------AJOUT----------------------------------------------------------------------
        if (action.equals("add")) {
            ServletTool.actionADD(request, ATT_BEAN_JSP, ATT_FORM_JSP, FluxType.class, Boolean.TRUE);


            //------------------------------------ACTION : MODIFICATION------------------------------------------------------------
        } else if (action.equals("mod")) {
            ServletTool.actionMOD(request, ATT_BEAN_JSP, ATT_FORM_JSP, FluxType.class, Boolean.TRUE);

        } //---------------------------------------ACTION RECHERCHE---------------------------------------------------------------------
        else if (action.equals("recherche")) {
            List<Object> list = dao.findall();
            request.setAttribute(ATT_LIST_OBJ, list);
        } else if (action.equals("read")) {
            ServletTool.actionREAD(request, FluxType.class, ATT_BEAN_JSP);
        }

//        }
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
