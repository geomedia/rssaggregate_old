/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import dao.DAOFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.Conf;
import rssagregator.beans.form.ConfForm;
import rssagregator.beans.form.DAOGenerique;
import rssagregator.services.ListeFluxCollecteEtConfigConrante;
import static servlet.JournauxSrvl.VUE;

/**
 *
 * @author clem
 */
@WebServlet(name = "Config", urlPatterns = {"/config"})
public class ConfigSrvl extends HttpServlet {

    public static final String VUE = "/WEB-INF/configjsp.jsp";
    public static final String ATT_FORM = "form";
    public static final String ATT_BEANS = "conf";

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");


        // Un simple attribut pour que le menu brille sur la navigation courante
        request.setAttribute("navmenu", "config");



        Conf confGenerale = null;
//        DAOGenerique dao = DAOFactory.getInstance().getDAOGenerique();
//        dao.setClassAssocie(Conf.class);
        ConfForm form = new ConfForm(/*dao*/);


        // On récupère la config dans la base de donnée
        
//        List<Object> listconf = dao.findall();
//        confGenerale = (Conf) listconf.get(0);

        confGenerale = ListeFluxCollecteEtConfigConrante.getInstance().getConfCourante();


//Si l'utilisateur à posté on bind
        if (request.getMethod().equals("POST")) {
            confGenerale = (Conf) form.bind(request, confGenerale, Conf.class);
        }

        request.setAttribute(ATT_FORM, form);
        request.setAttribute(ATT_BEANS, confGenerale);

        // SAUVEGARDE SI INFOS 
        if (form.getValide()) {
            
//            dao.modifier(confGenerale);
//            ListeFluxCollecteEtConfigConrante.getInstance().setConfCourante(confGenerale);
//            ListeFluxCollecteEtConfigConrante.getInstance().notifyObservers();
            
            ListeFluxCollecteEtConfigConrante.getInstance().modifierConf(confGenerale);
            // Il faut notifier le changement 
            
        }


        this.getServletContext().getRequestDispatcher(VUE).forward(request, response);

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
