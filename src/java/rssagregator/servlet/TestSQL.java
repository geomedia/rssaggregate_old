/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.Entity;
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
@WebServlet(name = "TestSQL", urlPatterns = {"/TestSQL"})
public class TestSQL extends HttpServlet {

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

        EntityManager em = DAOFactory.getInstance().getEntityManager();

//               Query q = em.createQuery("SELECT i FROM Item i LEFT JOIN i.doublon d WHERE d.ID IS NULL");
//               
//               List list = q.getResultList();
//               for (int i = 0; i < list.size(); i++) {
//            Object object = list.get(i);
//                   System.out.println(""+object);
//        }

        Query query = em.createQuery("SELECT item.hashContenu FROM Item item JOIN item.listFlux fl WHERE fl.ID=:idfl ORDER BY item.ID DESC");
        query.setParameter("idfl", new Long(1537));
        query.setFirstResult(0);
        query.setMaxResults(500);

        List resu = query.getResultList();
        for (int i = 0; i < resu.size(); i++) {
            Object object = resu.get(i);
            System.out.println(object);
            System.out.println("TYPE" + object.getClass());

        }




        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestSQL</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TestSQL at " + request.getContextPath() + "</h1>");
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
