/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@WebServlet(name = "Simpleadd", urlPatterns = {"/Simpleadd"})
public class Simpleadd extends HttpServlet {

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
        
        
        if(request.getMethod().equals("POST")){
            
            int i;
            String tatdeFlux = request.getParameter("txt");
            System.out.println(tatdeFlux);
            
            StringTokenizer st = new StringTokenizer(tatdeFlux);
            DaoFlux dao = DAOFactory.getInstance().getDAOFlux();
           
            
            while(st.hasMoreElements()){
                try {
                    Flux fl = new Flux(st.nextToken());
                    fl.setActive(Boolean.TRUE);
                    fl.setPeriodiciteCollecte(60);
                    dao.creer(fl);
                    
                } catch (Exception ex) {
                    Logger.getLogger(Simpleadd.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("err");
                }
                
            }
            dao.forceNotifyObserver();
        }

        this.getServletContext().getRequestDispatcher("/WEB-INF/simpleadd.jsp").forward(request, response);
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
