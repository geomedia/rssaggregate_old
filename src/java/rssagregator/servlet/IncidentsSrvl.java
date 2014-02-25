/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.Flux;
import rssagregator.beans.form.AbstrForm;
import rssagregator.beans.form.FORMFactory;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
import rssagregator.dao.DaoFlux;
import rssagregator.services.crud.AbstrServiceCRUD;
import rssagregator.services.crud.ServiceCRUDFactory;
import rssagregator.services.crud.ServiceCrudIncident;
import rssagregator.utils.ServletTool;
import static rssagregator.utils.ServletTool.redir;

/**
 * Gère les action : <ul>
 * <li>read</li>
 * <li>rechercher</li>
 * </li>list<li/>
 * </li>mod</li>
 * </ul>
 *
 * @author clem
 */
@WebServlet(name = "Incidents", urlPatterns = {"/incidents/*"})
public class IncidentsSrvl extends HttpServlet {

    public static final String ATT_LIST = "listobj";
    public static final String ATT_FORM = "form";
    public static final String ATT_OBJ = "bean";
    public String VUE = "/WEB-INF/incidentHTML.jsp";
    public static final String ATT_SERV_NAME = "incidents";
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(IncidentsSrvl.class);

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

        String vue;
        vue = request.getParameter("vue");
        if (vue == null || vue.isEmpty()) {
            vue = "html";
        }

        Integer firstResult = null;
        Integer itPrPage;
        Boolean clos;

        // Un simple attribut pour que le menu brille sur la navigation courante
        request.setAttribute("navmenu", "incident");
        Map<String, String> redirmap = null;

        // récupération de l'action
        String action = ServletTool.configAction(request, "recherche");
        request.setAttribute("srlvtname", ATT_SERV_NAME);


        //============================================================================================
        //                              GESTION RECHERCHE
        //============================================================================================
        if (action.equals("recherche")) {

            // Si les paramettres permettant d'afficher la requete ne sont pas nul 
//            action = "list";

            // Si on trouve des paramettres de présélection dans la requete utilisateur.
            //....
            //Récupération des parametres.

            String[] fluxDde = request.getParameterValues("fluxSelection2");
            List<Flux> fluxSelectionne = new ArrayList<Flux>();
            if (fluxDde != null && fluxDde.length > 0) {
                DaoFlux daof = DAOFactory.getInstance().getDAOFlux();

                for (int i = 0; i < fluxDde.length; i++) {
                    String string = fluxDde[i];
                    Flux f = (Flux) daof.find(new Long(string));
                    fluxSelectionne.add(f);
                }
                request.setAttribute("requestOnStart", true);

            }

            request.setAttribute("fluxsel", fluxSelectionne);
        }
        //==========================================================================================
        //                              ACTION RSS
        //==========================================================================================
        /**
         * *
         * Permet d'afficher les incidents sous forme d'un flux RSS afin d'aider les administrateur a suivre.
         */
        if (action.equals("rssBakend")) {

            DAOIncident dao = DAOFactory.getInstance().getDAOIncident();
            List<AbstrIncident> listIncidNonClos = dao.findIncidentNonClos(AbstrIncident.class);


            request.setAttribute("incids", listIncidNonClos);
            vue = "rss";



        }

        //============================================================================================
        //                              GESTION DES ACTIONS
        //============================================================================================

        //----------------------------------ACTION : LIST
        if (action.equals("list")) {

            try {
                Class c = null;
                DAOIncident dao = null;
                try {
      
                    
                    c = Class.forName("rssagregator.beans.incident." + request.getParameter("type"));
                    dao = (DAOIncident) DAOFactory.getInstance().getDaoFromType(c);
                    if (!AbstrIncident.class.isAssignableFrom(c)) {
                        throw new Exception("non");
                    }
                } catch (Exception e) {
                    logger.debug("Exeption ", e);
                }


                AbstrForm form = FORMFactory.getInstance().getForm(c, "list");
                form.parseListeRequete(request, dao);
                dao.setCriteriaSearchFilters(form.getFiltersList());
                ServletTool.actionLIST(request, c, ATT_OBJ, dao);


            } catch (Exception ex) {
                Logger.getLogger(IncidentsSrvl.class.getName()).log(Level.SEVERE, null, ex);
            }
//            //--------------------------------------------ACTION : MOD-------------------------------------
        } else if (action.equals("mod")) {
            try {
                Class c = Class.forName("rssagregator.beans.incident." + request.getParameter("type"));
                ServletTool.actionMOD(request, ATT_OBJ, ATT_FORM, c, false);
                if (!AbstrIncident.class.isAssignableFrom(c)) {
                    throw new Exception("non");
                }
            } catch (ClassNotFoundException ex) {
                redir(request, ATT_SERV_NAME + "/read?id=" + request.getParameter("id"), "L'entité demandée n'existe pas !", Boolean.TRUE);
            } catch (Exception ex) {
                redir(request, ATT_SERV_NAME + "/read?id=" + request.getParameter("id"), "L'entité demandée n'existe pas !", Boolean.TRUE);
            }



            //---------------------------------------ACTION : READ-------------------------------------
        } else if (action.equals("read")) {


            // Récupération le type d'incident
            String type = request.getParameter("type");
            try {
                Class c = Class.forName("rssagregator.beans.incident." + request.getParameter("type"));
                if (!AbstrIncident.class.isAssignableFrom(c)) {
                    throw new Exception("non");
                }
                ServletTool.actionREAD(request, c, ATT_OBJ);

            } //               Class c = Class.forName("rssagregator.beans.incident.CollecteIncident");
            catch (ClassNotFoundException ex) {
                logger.debug("Exeption ", ex);
                redir(request, ATT_SERV_NAME + "/read?id=" + request.getParameter("id"), "L'entité demandée n'existe pas !", Boolean.TRUE);
            } catch (Exception ex) {
                logger.debug("EXX", ex);
                redir(request, ATT_SERV_NAME + "/read?id=" + request.getParameter("id"), "L'entité demandée n'existe pas !", Boolean.TRUE);
            }
        } //------------------------------------------ACTION CLOSE-----------------------------------------
        /**
         * *
         * Permet de clore une liste d'incident
         */
        else if (action.equals("close")) {
            //On récupère les incident
            List<AbstrIncident> listIncid = new ArrayList<AbstrIncident>();
            List<Long> listId = ServletTool.parseidFromRequest(request, null);

            DAOIncident dao = DAOFactory.getInstance().getDAOIncident();
            dao.setClassAssocie(AbstrIncident.class);
            for (int i = 0; i < listId.size(); i++) {
                Long long1 = listId.get(i);
                AbstrIncident incident = (AbstrIncident) dao.find(long1);
                if (incident != null) {
                    listIncid.add(incident);
                }

            }

            ServiceCrudIncident service = (ServiceCrudIncident) ServiceCRUDFactory.getInstance().getServiceFor(AbstrIncident.class);
            try {
                service.cloreIncidents(listIncid, dao.getEm(), true);
            } catch (Exception ex) {
                Logger.getLogger(IncidentsSrvl.class.getName()).log(Level.SEVERE, null, ex);
            }


        } else if (action.equals("rem")) {

            try {

                List<AbstrIncident> listIncid = new ArrayList<AbstrIncident>();
                List<Long> listId = ServletTool.parseidFromRequest(request, null);
                DAOIncident dao = DAOFactory.getInstance().getDAOIncident();
                dao.setClassAssocie(AbstrIncident.class);
                for (int i = 0; i < listId.size(); i++) {
                    Long long1 = listId.get(i);
                    AbstrIncident incid = (AbstrIncident) dao.find(long1);
                    if (incid != null) {
                        listIncid.add(incid);
                    }
                }

                AbstrServiceCRUD service = null;
                service = ServiceCRUDFactory.getInstance().getServiceFor(AbstrIncident.class);
                try {
                    service.supprimerList(listIncid);
                } catch (Exception ex) {
                    Logger.getLogger(IncidentsSrvl.class.getName()).log(Level.SEVERE, null, ex);
                }
                ServletTool.redir(request, "incidents", "suppression effetuée", false);

            } catch (Exception e) {
                ServletTool.redir(request, "incidents", "Errerur " + e, true);
            }
        }




// gestion de la vue et de l'envoie vers la JSP
        if (vue.equals("jsondesc")) {
            VUE = "/WEB-INF/incidentJSONDesc.jsp";
        } else if (vue.equals("rss")) {
            VUE = "/WEB-INF/incidentRSS.jsp";
        }

        if (vue.equals("html")) {
            VUE = "/WEB-INF/incidentHTML.jsp";
        } else if (vue.equals("grid")) {
            VUE = "/WEB-INF/incidentJSONGrid.jsp";
        }
        this.getServletContext().getRequestDispatcher(VUE).forward(request, response);

    }

//    public static void main(String[] args) throws ClassNotFoundException {
//        Class c = Class.forName("rssagregator.beans.incident.FluxIncident");
//    }
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
