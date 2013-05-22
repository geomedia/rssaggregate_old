/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import dao.DAOFactory;
import dao.DAOIncident;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.Flux;
import rssagregator.beans.form.IncidentForm;
import rssagregator.beans.incident.FluxIncident;

/**
 *
 * @author clem
 */
@WebServlet(name = "Incidents", urlPatterns = {"/incidents"})
public class IncidentsSrvl extends HttpServlet {

    public static final String ATT_LIST = "listobj";
    public static final String ATT_FORM = "form";
    public static final String ATT_OBJ = "incident";
    public static final String VUE = "/WEB-INF/incidentJsp.jsp";

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
        request.setAttribute("navmenu", "incident");

        // récupération de l'action
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        request.setAttribute("action", action);


        DAOIncident dao = DAOFactory.getInstance().getDAOIncident();
        IncidentForm form = new IncidentForm();
        FluxIncident incident = null;


        String idString = request.getParameter("id");
        if (idString != null && !idString.equals("")) {
            Long id = new Long(request.getParameter("id"));
            request.setAttribute("id", id);
            incident = (FluxIncident) dao.find(id);
           
            System.out.println("NB FLUX : " + incident.getFluxLie().getUrl());
//            flux = (Flux) daoFlux.find(id);
        }
        

        

        if (request.getMethod().equals("POST")) {
            incident = (FluxIncident) form.bind(request, incident, Flux.class);
        }

        if (action.equals("list")) {
            //recup de la list des incidents
       
            List<FluxIncident> listAll = dao.findAllLimit(new Long(0), new Long(100));
            request.setAttribute(ATT_LIST, listAll);
        }


        request.setAttribute(ATT_FORM, form);
        request.setAttribute(ATT_OBJ, incident);

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
