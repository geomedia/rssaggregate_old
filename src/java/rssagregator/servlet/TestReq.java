/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.Item;
import rssagregator.beans.Journal;
import rssagregator.beans.exception.ArgumentIncorrect;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;
import rssagregator.dao.DaoJournal;

/**
 *
 * @author clem
 */
@WebServlet(name = "TestReq", urlPatterns = {"/TestReq"})
public class TestReq extends HttpServlet {

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
        
        
        DaoJournal daoj = DAOFactory.getInstance().getDaoJournal();
       Journal j = (Journal) daoj.find(new Long(5201));
        
        
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        List<Item> resu;
        try {
            resu = daoItem.findItemPossedantTitreAppartenantAuJournal("Tout sur le nouveau \"Journal Tactile enrichi\"", j);
              for (int i = 0; i < resu.size(); i++) {
            Item item = resu.get(i);
            System.out.println("ITEM : " + item.getTitre());
        }
        } catch (NullPointerException ex) {
            Logger.getLogger(TestReq.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ArgumentIncorrect ex) {
            Logger.getLogger(TestReq.class.getName()).log(Level.SEVERE, null, ex);
        }
      
        
        
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestReq</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TestReq at " + request.getContextPath() + "</h1>");
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
