/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.persistence.LockModeType;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.Flux;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;

/**
 *
 * @author clem
 */
@WebServlet(name = "TestLock2", urlPatterns = {"/TestLock2"})
public class TestLock2 extends HttpServlet {

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

        DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
        dao.beginTransaction();
        

        Flux f = (Flux) dao.find(new Long(58));
//        f.setUrl("http://lenouveua");
//        dao.getEm().merge(f);
        dao.getEm().lock(f, LockModeType.PESSIMISTIC_READ);
//        dao.commit();
        f.setUrl("http://lock2");
//        f.setActive(false);
        dao.getEm().merge(f);
        
      
        dao.commit();

//        dao.beginTransaction();


        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestLock2</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TestLock2 at " + request.getContextPath() + "</h1>");
            out.println("<h1>Servlet TestLock2 at " + f + "</h1>");

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
