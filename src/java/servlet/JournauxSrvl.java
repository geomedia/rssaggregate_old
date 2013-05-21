/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import dao.DAOFactory;
import dao.DaoJournal;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.Journal;
import rssagregator.beans.form.JournalForm;

/**
 *
 * @author clem
 */
@WebServlet(name = "Journaux", urlPatterns = {"/journaux"})
public class JournauxSrvl extends HttpServlet {

    public static final String VUE = "/WEB-INF/journaljsp.jsp";
    public static final String ATT_FORM = "form";
    public static final String ATT_JOURNAL = "journal";
    public static final String ATT_LIST_JOURNAUX = "listjournaux";

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

        // Un simple attribut pour que le menu brille sur la navigation courante
        request.setAttribute("navmenu", "journaux");

        // récupération de l'action
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        request.setAttribute("action", action);

        DaoJournal daoJournal = DAOFactory.getInstance().getDaoJournal();
        JournalForm journalForm = new JournalForm(/*daoJournal*/);
        Journal journal = null;

        //        // On récupère le flux dans la base de donnée si il est précisé
        String idString = request.getParameter("id");
        if (idString != null && !idString.equals("")) {
            Long id = new Long(idString);
            request.setAttribute("id", id);
            journal = (Journal) daoJournal.find(id);
        }
       
        
        // Si il y a du post on récupère les données saisies par l'utilisateur pour éviter la resaisie de l'information
        if (request.getMethod().equals("POST")) {
            journal = (Journal) journalForm.bind(request, journal, Journal.class);
        }

        if (action.equals("list")) {
            List<Object> listJournaux = daoJournal.findall();
            request.setAttribute(ATT_LIST_JOURNAUX, listJournaux);
        }
        
        
        if(action.equals("rem")){
            
            daoJournal.remove(journal);
        }
        

        request.setAttribute(ATT_FORM, journalForm);
        request.setAttribute(ATT_JOURNAL, journal);

        // SAUVEGARDE SI INFOS 
        if (journalForm.getValide()) {
            if (action.equals("add")) {
                daoJournal.creer(journal);
            } else if (action.equals("mod")) {
                daoJournal.modifier(journal);
            }
        }


// redirection de l'utilisateur
        if (action.equals("add") && journalForm.getValide()) {
            response.sendRedirect("journaux?action=mod&id=" + journal.getID());
        } else {
            this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
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
