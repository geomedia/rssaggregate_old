/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author clem
 */
@WebServlet(name = "TESTRecapActiviteGenerale", urlPatterns = {"/TESTrecapActiviteGenerale"})
public class TESTRecapActiviteGeneraleSrvl extends HttpServlet {

    public static final String VUE = "/WEB-INF/TESTrecapactivitegeneralejsp.jsp";

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
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        request.setAttribute("navmenu", "recap");
        Map<String, String> redirmap = null;

        // récupération de l'action
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        request.setAttribute("action", action);
        if (action.equals("test")) {
            response.setContentType("Content-type: text/json");

            JSONObject jsonObjc = new JSONObject();
            jsonObjc.put(30, 10);


            Double rnd = Math.random() * 100;
            Integer x = rnd.intValue();
            Date dateDebut = new Date();
            Long time = dateDebut.getTime();
            time = time/1000;
            time = time*1000;
            x = time.intValue();
     


            rnd = Math.random() * 100;
            Integer y = rnd.intValue();


            JSONArray jsona = new JSONArray();
            jsona.add(time);
            jsona.add(y);


            response.setContentType("Content-type: text/json");
            PrintWriter out = response.getWriter();

            try {
//                out.println();
                out.println(jsona.toJSONString());
                System.out.println(jsona.toString());
            } catch (Exception e) {
            }


//            this.getServletContext().getRequestDispatcher("/WEB-INF/jsonrecapjsp.jsp").forward(request, response);

        } else {
            this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
        }



    }
    public static void main(String[] args) {
               Date dateDebut = new Date();
               System.out.println(dateDebut.getTime());
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
