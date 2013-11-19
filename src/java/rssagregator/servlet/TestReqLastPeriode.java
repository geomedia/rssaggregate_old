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
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.exception.ArgumentIncorrect;
import rssagregator.beans.exception.IncompleteBeanExeption;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import rssagregator.dao.DaoItem;

/**
 *
 * @author clem
 */
@WebServlet(name = "TestReqLastPeriode", urlPatterns = {"/TestReqLastPeriode"})
public class TestReqLastPeriode extends HttpServlet {

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


        DaoFlux daof = DAOFactory.getInstance().getDAOFlux();
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        Flux f = (Flux) daof.find(new Long(27202));


        List<Item> lItem;
        try {
            lItem = daoItem.itemCaptureParleFluxDurantlaDernierePeriodeCollecte(f);
            for (int i = 0; i < lItem.size(); i++) {
                Item item = lItem.get(i);
                System.out.println("" + item);
            }



        } catch (ArgumentIncorrect ex) {
            Logger.getLogger(TestReqLastPeriode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IncompleteBeanExeption ex) {
            Logger.getLogger(TestReqLastPeriode.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestReqLastPeriode</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TestReqLastPeriode at " + request.getContextPath() + "</h1>");
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
