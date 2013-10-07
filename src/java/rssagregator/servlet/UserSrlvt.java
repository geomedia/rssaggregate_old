/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.UserAccount;
import rssagregator.dao.DAOFactory;
import rssagregator.utils.ServletTool;

/**
 * Servlet permettant de gérer les requetes concernant le CRUD de l'entité UserAccount. action :
 *
 * @author clem
 */
@WebServlet(name = "UserSrlvt", urlPatterns = {"/user/*"})
public class UserSrlvt extends HttpServlet {

    public static final String ATT_FORM_JSP = "form";
    public static final String ATT_BEAN_JSP = "bean";
    public static final String ATT_LIST_OBJ = "list";
    public static final String ATT_SRVLT_NAME = "user";

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
        request.setCharacterEncoding("UTF-8");
        response.setContentType("UTF-8");

        // récupération de l'action.
        String action = ServletTool.configAction(request, "recherche");

        request.setAttribute("srlvtname", ATT_SRVLT_NAME);
        request.setAttribute("navmenu", "user");



        //========================================================================================
        //................................GESTION DES ACTION 
        //========================================================================================

        //----------------------------ACTION : RECHERCHE-------------------------------------------------
        //Cette action permet de lister les utilisateurs.
        if (action.equals("recherche")) {
            // On récupère la liste des Utilisateur. Pour l'instant pas de critères, de toute facon on ne pense pas gérer beaucoup d'utilisateur. Pas de module de recherche a l'intérieur des utilisateur
            List<Object> list = DAOFactory.getInstance().getDAOUser().findall();
            request.setAttribute("list", list);

        } //----------------------------ACTION : MODIFICATION------------------------------------------
        else if (action.equals("mod")) {
            ServletTool.actionMOD(request, ATT_BEAN_JSP, ATT_FORM_JSP, UserAccount.class, Boolean.TRUE);


        } //----------------------------ACTION : AJOUT--------------------------------------------------------
        else if (action.equals("add")) {
            ServletTool.actionADD(request, ATT_BEAN_JSP, ATT_FORM_JSP, UserAccount.class, Boolean.TRUE);

        } //-------------------------------ACTION : READ-----------------------------------------------------
        else if (action.equals("read")) {
            ServletTool.actionREAD(request, UserAccount.class, ATT_BEAN_JSP);

        } //------------------------------------------ACTION : REMOVE------------------------------------------
        else if (action.equals("rem")) {
            ServletTool.actionREM(request, UserAccount.class, Boolean.TRUE);

        } //---------------------------------------IDENTIFICATION---------------------------------------
//        else if (action.equals("ident")) {                    L'IDENTIFICATION ET LE LOGOUT SONT MAINTENANT DANS LA SERVLET IDENTIFICATION..........;
//
//            HttpSession session = request.getSession();
//
//            //Si il y a du post
//            if (request.getMethod().equals("POST")) {
//                String m = request.getParameter("mail");
//                String p = request.getParameter("pass");
//                // On cherche dans la base de données l'utilisateur correspondant
//                UserAccount u = dao.findPrMail(m);
//                if (u != null) {
//                    try {
//                        if (u.authWithThisPass(p)) {
//                            System.out.println("PASS OK");
//                            session.setAttribute("authuser", u);
//                            ServletTool.redir(request, "index", "Identification réussie", Boolean.FALSE);
//                        } else {
//                            request.setAttribute("err", "Echec de l'identification");
//                            System.out.println("BAB PASS");
//                        }
//                    } catch (NoSuchAlgorithmException ex) {
//                        Logger.getLogger(UserSrlvt.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                } else {
//                    request.setAttribute("err", "Echec de l'identification");
//                }
//
//            }
//        } //------------------------------------DECONNECTION ---------------------------------------------
//        else if (action.equals("logout")) {
//
//
//            HttpSession session = request.getSession();
//            session.invalidate();
//
//        }

        //============================================================================================
        //................................GESTION DE LA VUE 
        //============================================================================================
        String vue = request.getParameter("vue");
        String jsp = "/WEB-INF/userHTML.jsp";
        if(vue!=null && !vue.isEmpty()){
            if(vue.equals("jsonform")){
                jsp = "/WEB-INF/jsonform.jsp";
            }
        }

        this.getServletContext().getRequestDispatcher(jsp).forward(request, response);

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
