/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.Flux;

/**
 *
 * @author clem
 */
@WebServlet(name = "Test", urlPatterns = {"/Test"})
public class Test extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        
        
        // Récupération de la liste des flux par l'objet liste des flux
//        List<Flux> listeFluxMémoire = ListeFluxCollecteEtConfigConrante.getInstance().listFlux;
        List<Flux> listeFluxMémoire = DAOFactory.getInstance().getDAOFlux().findAllFlux(false);
        
        
        // Récupération des flux par la dao
        DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
        List<Object> listeFluxDAO = dao.findall();
        
        
        try {
            EntityManager em = DAOFactory.getInstance().getEntityManager();
            
            
            
            
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Test</title>");            
            out.println("</head>");
            out.println("<body>");
            int i;
            for(i=0; i<listeFluxMémoire.size(); i++){
                out.println("<p>FLUX Memoire : " + listeFluxMémoire.get(i).hashCode()+"</p>");
                out.println("FLUX DAO   : "+ listeFluxDAO.get(i).hashCode());
                
                System.out.println("flux memoire managed : " + em.contains(listeFluxMémoire.get(i)) );
                System.out.println("flux BAO managed : " + em.contains(listeFluxDAO.get(i)) );
                if(listeFluxMémoire.get(i).equals(listeFluxDAO.get(i))){
                    System.out.println("EGAL");
                }
            }
            
            System.out.println("");

            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
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
