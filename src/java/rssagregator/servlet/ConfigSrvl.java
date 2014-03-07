/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.Conf;
import rssagregator.dao.DAOConf;
import rssagregator.dao.DAOFactory;
import rssagregator.utils.ServletTool;

/**
 * La Servlet utilisée pour rediriger les requetes de l'utilisateur relatif à la
 * configuration du serveur. Les action suivantes peuvent être demandée : <ul>
 * <li><strong>mod :</strong> modifier la config</li>
 * <li><strong>importitem : </strong>Action pouvant être lancée si le serveur
 * est maître. Le serveur maitre va alors demander a tous les serveurs esclaves
 * les items qu'ils ont collecté</li>
 * <li><strong>importflux : </strong>permet de lancer la récupération manuelle
 * sur un serveur esclave de la liste des flux sur un serveur maître</li>
 * <li><strong>jmsreload : </strong>permet de relancer une tentative de
 * connection au service JMS. Renvoie un simple message text.</li>
 * </ul @
 *
 *
 * author clem
 */
@WebServlet(name = "Config", urlPatterns = {"/config/*"})
public class ConfigSrvl extends HttpServlet {

    public String vue = "/WEB-INF/configjsp.jsp";
    public static final String ATT_FORM = "form";
    public static final String ATT_BEANS = "conf";
    public static final String ATT_SERV_NAME = "config";

    /**
     * Processes requests for both HTTP.
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");


        // Un simple attribut pour que le menu brille sur la navigation courante
        request.setAttribute("navmenu", "config");
         request.setAttribute("srlvtname", ATT_SERV_NAME);
        
        String action = ServletTool.configAction(request, "mod");

        Conf confcourante = null;
        confcourante = DAOFactory.getInstance().getDAOConf().getConfCourante();

        // Configuration de la vue
        vue = request.getParameter("vue");
        String jsp = "/WEB-INF/configjsp.jsp";
        if (vue == null || vue.isEmpty()) {
            vue = "/WEB-INF/configjsp.jsp";
            
        }

        /**
         * *======================================================================================
         * ...................................ACTION MOD
         *///=====================================================================================
        if (action.equals("mod")) {
            //Si l'utilisateur à posté on bind
            
            ServletTool.actionMOD(request, ATT_BEANS, ATT_FORM, Conf.class, Boolean.TRUE);
        }
                /**
         * *=====================================================================================
         * . ....................................ACTION : READ
         *///=====================================================================================
        
        else if (action.equals("read")){
            ServletTool.actionREAD(request, Conf.class, ATT_BEANS);
        }

        
        if(vue ==null || vue.isEmpty()){
            jsp = "/WEB-INF/configjsp.jsp";
        }
        else if(vue.equals("jsonform")){
            jsp = "/WEB-INF/jsonform.jsp";
        }

        this.getServletContext().getRequestDispatcher(jsp).forward(request, response);
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
