/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.LockModeType;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;

/**
 *
 * @author clem
 */
@WebServlet(name = "TestLockIncid", urlPatterns = {"/TestLockIncid"})
public class TestLockIncid extends HttpServlet {

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
        
        DAOIncident dao = (DAOIncident) DAOFactory.getInstance().getDaoFromType(CollecteIncident.class);
        
        AbstrIncident incid = (AbstrIncident) dao.find(new Long(8755));
        dao.getEm().getTransaction().begin();
        dao.getEm().lock(incid, LockModeType.PESSIMISTIC_WRITE);
        System.out.println("INCID : " + incid);
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestLockIncid.class.getName()).log(Level.SEVERE, null, ex);
        }
        dao.commit();
        
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestLockIncid</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TestLockIncid at " + request.getContextPath() + "</h1>");
            out.println("<h1>Servlet TestLockIncid at " + incid + "</h1>");
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
