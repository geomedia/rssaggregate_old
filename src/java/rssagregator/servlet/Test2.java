/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.Flux;
import rssagregator.beans.Journal;
import rssagregator.dao.DAOFactory;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.ServiceSynchro;
import rssagregator.services.TacheCalculQualiteFlux;

/**
 *
 * @author clem
 */
@WebServlet(name = "Test2", urlPatterns = {"/Test2"})
public class Test2 extends HttpServlet {

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
        
        Journal j = new Journal();
        j.setNom("Le Syncro Journal");
        try {
            ServiceSynchro.getInstance().diffuser(j, "add");
        } catch (JMSException ex) {
            Logger.getLogger(Test2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
//           ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
//        
//        Flux f = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(55));
//        
//        
//        
//        
//        
//        System.out.println("FLUX  : " + f);
//        TacheCalculQualiteFlux calculQualiteFlux = new TacheCalculQualiteFlux(collecteur);
//        calculQualiteFlux.setFlux(f);
//        
//        collecteur.getExecutorService().submit(calculQualiteFlux);
        
        
//        ServiceSynchro jMS = ServiceSynchro.getInstance();
//        TacheTest tache = new TacheTest(jMS);
//        try {
//            tache.call();
//        } catch (Exception ex) {
//            Logger.getLogger(Test2.class.getName()).log(Level.SEVERE, null, ex);
//        }
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Test2</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Test2 at " + request.getContextPath() + "</h1>");
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
