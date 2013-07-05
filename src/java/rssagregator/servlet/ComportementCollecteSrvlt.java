/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.form.ComportementCollecteForm;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.dao.DAOComportementCollecte;
import rssagregator.dao.DAOFactory;

/**
 * Cette servlet permet de configurer des MediatorCollecte, elle configure
 * directement les objets de traitement associé. Contrairement aux autres
 * servlet, elle n'est ainsi pas dédié à la gestion d'un type d'entité unique
 *
 * @author clem
 */
@WebServlet(name = "ComportementCollecteSrvlt", urlPatterns = {"/ComportementCollecte"})
public class ComportementCollecteSrvlt extends HttpServlet {

    public String VUE = "/WEB-INF/comportementcollecteHTML.jsp";
    Map<String, String> redirmap = null;

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

        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");



        redirmap = null;

        ComportementCollecteForm form = new ComportementCollecteForm();

        MediatorCollecteAction obj = null;

        DAOComportementCollecte dao = DAOFactory.getInstance().getDAOComportementCollecte();
        dao.setClassAssocie(MediatorCollecteAction.class);
        request.setAttribute("navmenu", "config");

        // récupération de l'action
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        request.setAttribute("action", action);


        // On récupère la sortie (html Json. Cette variable sert à configurer la vue
        // récupération de l'action
        String vue = request.getParameter("vue");
        if (vue == null) {
            vue = "html";
        }
        request.setAttribute("vue", vue);


        request.setAttribute("redirmap", redirmap);

        if (action.equals("mod") || action.equals("rem")) {
            try {
                obj = (MediatorCollecteAction) dao.find(new Long(request.getParameter("id")));
                System.out.println("" + obj.getNom());
            } catch (Exception e) {
            }
        }


        if (request.getMethod().equals("POST")) {
            obj = (MediatorCollecteAction) form.bind(request, obj, MediatorCollecteAction.class);
        }


        if (action.equals("list")) {
            //On récupère la liste des comportements
            List<Object> listcomportement = dao.findall();
            request.setAttribute("list", listcomportement);
        }

        if (form.getValide()) {
            if (action.equals("add")) {
                try {
                    dao.creer(obj);

                    redir(request, "ComportementCollecte?action=mod&id=" + obj.getID(), "Ajout effectué");
                } catch (Exception ex) {
                    Logger.getLogger(ComportementCollecteSrvlt.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (action.equals("mod")) {
                try {
                    dao.modifier(obj);
                    redir(request, "ComportementCollecte", "Modification effectuée");
                } catch (Exception ex) {
                    Logger.getLogger(ComportementCollecteSrvlt.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        if (action.equals("rem")) {
            try {
                dao.remove(obj);
                redir(request, "ComportementCollecte?action=list", "Suppression effectuée.");
            } catch (Exception ex) {
                Logger.getLogger(ComportementCollecteSrvlt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


        request.setAttribute("comportement", obj);

        this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
    }

    private void redir(HttpServletRequest request, String url, String msg) {
        redirmap = new HashMap<String, String>();
        redirmap.put("url", url);
        redirmap.put("msg", msg);
        request.setAttribute("redirmap", redirmap);
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
