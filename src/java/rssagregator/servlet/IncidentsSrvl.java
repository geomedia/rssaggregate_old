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
import rssagregator.beans.Flux;
import rssagregator.beans.form.IncidentForm;
import rssagregator.beans.incident.AbstrIncident;
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
           Map<String, String> redirmap = null;

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
           
//            flux = (Flux) daoFlux.find(id);
        }
        

        

        if (request.getMethod().equals("POST")) {
            form.bind(request, incident, FluxIncident.class);
        }

        if (action.equals("list")) {
            //recup de la list des incidents
       
            List<FluxIncident> listAll = dao.findAllLimit(new Long(0), new Long(100));
            request.setAttribute(ATT_LIST, listAll);
        }
        else if (action.equals("mod")) {
             if (form.getValide()) {
                 try {
                     dao.modifier(incident);
                 } catch (Exception ex) {
                                     redirmap = new HashMap<String, String>();
                redirmap.put("url", "flux?action=add");
                redirmap.put("msg", "ERREUR LORS DE L'AJOUT DU FLUX. : " + ex.toString());
                request.setAttribute("redirmap", redirmap);
                          request.setAttribute("err", "true");
                     Logger.getLogger(IncidentsSrvl.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 
             }
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
