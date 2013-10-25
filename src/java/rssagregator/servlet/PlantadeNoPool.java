/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.dao.DAOFactory;
import rssagregator.services.ServiceMailNotifier;
import rssagregator.services.TacheAlerteMail;
import rssagregator.services.TacheRecupCallable;

/**
 *
 * @author clem
 */
@WebServlet(name = "PlantadeNoPool", urlPatterns = {"/PlantadeNoPool"})
public class PlantadeNoPool extends HttpServlet {

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
            throws ServletException, IOException, InterruptedException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        ServiceMailNotifier service = ServiceMailNotifier.getInstance();
        
        
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(100);
        
        
        int i =0 ; 
        for(i=0;i<15000;i++){
             TacheAlerteMail t1 = new TacheAlerteMail(service);
             executorService.submit(t1);
//             service.getExecutorService().submit(t1);
        }
        executorService.shutdown();
        
        executorService.awaitTermination(30, TimeUnit.SECONDS);
        
//        try {
//            executorService.awaitTermination(1, TimeUnit.DAYS);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(PlantadeNoPool.class.getName()).log(Level.SEVERE, null, ex);
//        }

        

//        try {
            
//            service.getExecutorService().invokeAll(coll);

//        } catch (InterruptedException ex) {
//            Logger.getLogger(PlantadeNoPool.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        
        
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet PlantadeNoPool</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PlantadeNoPool at " + request.getContextPath() + "</h1>");
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
        try {
            processRequest(request, response);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlantadeNoPool.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlantadeNoPool.class.getName()).log(Level.SEVERE, null, ex);
        }
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
