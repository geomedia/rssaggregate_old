/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.Conf;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.form.ConfForm;
import rssagregator.services.ServiceCollecteur;
import rssagregator.utils.ServletTool;

/**
 *
 * @author clem
 */
@WebServlet(name = "Config", urlPatterns = {"/config/*"})
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


        String action = ServletTool.configAction(request, "mod");
        

        Conf confGenerale = null;
        ConfForm form = new ConfForm();

        confGenerale = DAOFactory.getInstance().getDAOConf().getConfCourante();

        System.out.println("ACTION : " + action);
//Si l'utilisateur à posté on bind
        if (request.getMethod().equals("POST") && action.equals("mod")) {
            confGenerale = (Conf) form.bind(request, confGenerale, Conf.class);
        }

        request.setAttribute(ATT_FORM, form);
        request.setAttribute(ATT_BEANS, confGenerale);

        // SAUVEGARDE SI INFOS 
        if (form.getValide()){
            try {
                DAOFactory.getInstance().getDAOConf().modifierConf(confGenerale);
                            // Il faut notifier le changement pour recharger le service de reception
                DAOFactory.getInstance().getDAOConf().forceNotifyObservers();
                ServletTool.redir(request, "config/mod", "Modification de la config effectuée", Boolean.FALSE);
    
            } catch (Exception ex) {
                Logger.getLogger(ConfigSrvl.class.getName()).log(Level.SEVERE, null, ex);
            }
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
