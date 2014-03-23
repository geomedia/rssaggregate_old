/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.services.AbstrService;

/**
 *Servlet point d'acces permettant d'obtenir des information sur les services, notamment les taches schedulée. 
 * 
 * @author clem
 */
@WebServlet(name = "ServiceCtrl", urlPatterns = {"/ServiceCtrl"})
public class ServiceCtrl extends HttpServlet {
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceCtrl.class);
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
        
        // On récupère le nom du service.
        String servicename = request.getParameter("servicename");
        Class c = null;
        try {
        c = Class.forName("rssagregator.services." + servicename);            
        } catch (Exception e) {
            logger.debug("Impossible de récupérer la class " , e);
        }
        
        if(c != null){
            try {
                // Récupération de l'instance
                Method methode = c.getMethod("getInstance");
                AbstrService instance = (AbstrService) methode.invoke(c);
                request.setAttribute("service", instance);
                
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(ServiceCtrl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(ServiceCtrl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ServiceCtrl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ServiceCtrl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(ServiceCtrl.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        this.getServletContext().getRequestDispatcher("/WEB-INF/serviceCtrl.jsp").forward(request, response);
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
