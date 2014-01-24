/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;

/**
 *
 * Servlet affichant la page d'acceuil. Utilise la index.jsp pour la vue. 
 * Permet la présentation de quelques statitiques ainsi que des incidents non clos. Cette servlet est accessible depuis les pattern "/" et "/index"
 * 
 * @author clem
 */
@WebServlet(name = "IndexSrvlt", urlPatterns = {"/index", ""})
public class IndexSrvlt extends HttpServlet {

    public String VUE = "/WEB-INF/index.jsp";

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

        // Il faut calculer le nombre de flux
        EntityManager em = DAOFactory.getInstance().getEntityManager();
        try {

            Query query = em.createQuery("SELECT COUNT(f) FROM Flux f WHERE f.active = TRUE");
            Object resu = query.getSingleResult();
            request.setAttribute("nbrFlux", resu);

        } catch (Exception e) {
        }

        try {
            Query countJournaux = em.createQuery("SELECT COUNT(DISTINCT j) FROM Journal j JOIN j.fluxLie f WHERE f.active = TRUE");
            Object nbrJournaux = countJournaux.getSingleResult();
            request.setAttribute("nbrJournaux", nbrJournaux);

        } catch (Exception e) {
        }

        // Comptage du nombre de pays
        try {
            Query countPays = em.createQuery("SELECT COUNT(DISTINCT j.pays) FROM Journal j JOIN j.fluxLie f WHERE f.active = TRUE");
            Object nbrPays = countPays.getSingleResult();
            request.setAttribute("nbrPays", nbrPays);

        } catch (Exception e) {
        }


        try {
            DAOIncident dAOIncident = DAOFactory.getInstance().getDAOIncident();
            List<AbstrIncident> listIncidNonClos = dAOIncident.findIncidentNonClos(AbstrIncident.class);
            request.setAttribute("incidList", listIncidNonClos);
            request.setAttribute("nbrIncident", listIncidNonClos.size());

        } catch (Exception e) {
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
