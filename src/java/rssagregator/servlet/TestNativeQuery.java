/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.dao.DAOFactory;

/**
 *
 * @author clem
 */
@WebServlet(name = "TestNativeQuery", urlPatterns = {"/TestNativeQuery"})
public class TestNativeQuery extends HttpServlet {

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
        
        
        EntityManager em = DAOFactory.getInstance().getEntityManager();
        
        PrintWriter out = response.getWriter();
       
                Query qCount = em.createNativeQuery("SELECT \n" +
"date_trunc('day', item.daterecup) as \"day\", COUNT(*)\n" +
"\n" +
"FROM \n" +
"  public.item, \n" +
"  public.flux, \n" +
"  public.item_flux\n" +
"WHERE \n" +
"  item.id = item_flux.item_id AND\n" +
"  flux.id = item_flux.listflux_id\n" +
"AND flux.id=1674\n" +
"AND item.daterecup BETWEEN '?' and '?'\n" +
"GROUP BY 1\n" +
"ORDER BY 1\n" +
"  ;");
                
                List<Object[]> results = qCount.getResultList();
                for (int i = 0; i < results.size(); i++) {
            Object[] objects = results.get(i);
                    System.out.println(""+objects[0] + " " + objects[1]);
            
                    System.out.println("Type" + objects[0].getClass());
                    System.out.println("Type" + objects[1].getClass());
        }
                
//        List<Object> resuCpt = qCount.getResultList();
//        for (int i = 0; i < resuCpt.size(); i++) {
//            Object integer = resuCpt.get(i);
//            Double cast = (Double) integer;
//            System.out.println("CPT " + integer);
//            System.out.println("Double  " + cast);
//        }
        
        
        
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestNativeQuery</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TestNativeQuery at " + request.getContextPath() + "</h1>");
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
