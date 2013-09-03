/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import rssagregator.beans.UserAccount;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOUser;
import rssagregator.utils.ServletTool;

/**
 *  Cette servlet permet à l'utilisateur de s'identifier et de se déconnecter (détruire sa session)
 * @author clem
 */
@WebServlet(name = "IdentificationSrlvt", urlPatterns = {"/ident/*"})
public class IdentificationSrlvt extends HttpServlet {

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
        
        String action = ServletTool.configAction(request, "login");
        System.out.println("ACTION  : " + action);
        
        DAOUser dao = DAOFactory.getInstance().getDAOUser();
        
        //============================================================================================
        //................................GESTION DES ACTIONS
        //============================================================================================
        
        //---------------------------------------IDENTIFICATION---------------------------------------
        if (action.equals("login")) {
            HttpSession session = request.getSession();
            //Si il y a du post
            if (request.getMethod().equals("POST")) {
                String m = request.getParameter("mail");
                String p = request.getParameter("pass");
                // On cherche dans la base de données l'utilisateur correspondant
                UserAccount u = dao.findPrMail(m);
                if (u != null) {
                    try {
                        if (u.authWithThisPass(p)) {
                            session.setAttribute("authuser", u);
                            ServletTool.redir(request, request.getParameter("askurl"), "Identification réussie", Boolean.FALSE);
                        } else {
                            request.setAttribute("err", "Echec de l'identification");
                            request.setAttribute("askurl", request.getParameter("askurl"));
                        }
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(UserSrlvt.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    request.setAttribute("err", "Echec de l'identification");
                    request.setAttribute("askurl", request.getParameter("askurl"));
                }
            }
        }
        
         //------------------------------------DECONNECTION ---------------------------------------------
        else if(action.equals("logout")){
            HttpSession session = request.getSession();
            session.invalidate();
            System.out.println("context : " + request.getContextPath());
            ServletTool.redir(request, request.getContextPath()+"/index", "Deconnection effectuée avec succès", Boolean.FALSE);
        }
        //====================================================================================================
        //....................................GESTION DE LA VUE
        //====================================================================================================
        
        request.setAttribute("url", request.getRequestURI());
        this.getServletContext().getRequestDispatcher("/WEB-INF/identification.jsp").forward(request, response);
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
