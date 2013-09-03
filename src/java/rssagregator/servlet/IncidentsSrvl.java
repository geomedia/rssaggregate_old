/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
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
import rssagregator.beans.form.IncidentForm;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.FluxIncident;
import static rssagregator.servlet.JournauxSrvl.ATT_SERV_NAME;
import rssagregator.utils.ServletTool;

/**
 * Gère les action : <ul>
 * <li>read</li>
 * <li>rechercher</li>
 * </li>list<li/>
 * </li>mod</li>
 * </ul>
 * @author clem
 */
@WebServlet(name = "Incidents", urlPatterns = {"/incidents/*"})
public class IncidentsSrvl extends HttpServlet {

    public static final String ATT_LIST = "listobj";
    public static final String ATT_FORM = "form";
    public static final String ATT_OBJ = "incident";
    public String VUE = "/WEB-INF/incidentHTML.jsp";
    public static final String ATT_SERV_NAME = "incidents";
    

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

        String vue;
        vue = request.getParameter("vue");
        if (vue == null || vue.isEmpty()) {
            vue = "html";
        }

        Integer firstResult = null;
        Integer itPrPage;
        Boolean clos;

        // Un simple attribut pour que le menu brille sur la navigation courante
        request.setAttribute("navmenu", "incident");
        Map<String, String> redirmap = null;

        // récupération de l'action
        String action = ServletTool.configAction(request, "recherche");
        request.setAttribute("srlvtname", ATT_SERV_NAME);
//        String action = request.getParameter("action");
//        if (action == null) {
//            action = "recherche";
//        }
//        request.setAttribute("action", action);


        DAOIncident dao = DAOFactory.getInstance().getDAOIncident();
        IncidentForm form = new IncidentForm();
        request.setAttribute("form", form);
        
        
//        FluxIncident incident = null;
//        String idString = request.getParameter("id");
//        if (idString != null && !idString.equals("")) {
//            Long id = new Long(request.getParameter("id"));
//            request.setAttribute("id", id);
//            incident = (FluxIncident) dao.find(id);
//
////            flux = (Flux) daoFlux.find(id);
//        }


//        if (request.getMethod().equals("POST") && action.equals("mod")) {
//            form.bind(request, incident, FluxIncident.class);
//        }


        //============================================================================================
        //                              GESTION DES ACTIONS
        //============================================================================================
        //----------------------------------ACTION : RECHERCHE
        if (action.equals("list")) {

            try {
                firstResult = new Integer(request.getParameter("firstResult"));
            } catch (Exception e) {
                firstResult = 0;
            }
            dao.setFistResult(firstResult);
            request.setAttribute("firstResult", firstResult);

            try {
                itPrPage = new Integer(request.getParameter("itPrPage"));
            } catch (Exception e) {
                itPrPage = 25;
            }
            dao.setMaxResult(itPrPage);
            request.setAttribute("itPrPage", itPrPage);


            try {
                clos = new Boolean(request.getParameter("clos"));

            } catch (Exception e) {
                clos = false;
            }
            dao.setClos(clos);
            request.setAttribute("clos", clos);

            Integer nbItem = dao.findnbMax();
            request.setAttribute("nbitem", nbItem);

            //recup de la list des incidents
            List<FluxIncident> listAll = dao.findCriteria();
            request.setAttribute(ATT_LIST, listAll);
        } //---------------------------------------------ACTION MODIFICATION----------------------------
        else if (action.equals("mod")) {

            ServletTool.actionMOD(request, ATT_OBJ, ATT_FORM, FluxIncident.class, false);
            //---------------------------------------ACTION : READ-------------------------------------
        } else if (action.equals("read")) {
            ServletTool.actionREAD(request, FluxIncident.class, ATT_OBJ);
        }



        // gestion de la vue et de l'envoie vers la JSP
        if (vue.equals("jsondesc")) {
            System.out.println("JsonDesc");
            VUE = "/WEB-INF/incidentJSONDesc.jsp";
        }

        if (vue.equals("html")) {
            VUE = "/WEB-INF/incidentHTML.jsp";
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
