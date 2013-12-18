/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
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


//        DAOIncident dao = DAOFactory.getInstance().getDAOIncident();
//        IncidentForm form = FORMFactory.getInstance().getForm(null, action)
////                new IncidentForm();
//        request.setAttribute("form", form);


//        CollecteIncident incident = null;
//        String idString = request.getParameter("id");
//        if (idString != null && !idString.equals("")) {
//            Long id = new Long(request.getParameter("id"));
//            request.setAttribute("id", id);
//            incident = (CollecteIncident) dao.find(id);
//
////            flux = (Flux) daoFlux.find(id);
//        }


//        if (request.getMethod().equals("POST") && action.equals("mod")) {
//            form.bind(request, incident, CollecteIncident.class);
//        }

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


        //============================================================================================
        //                              GESTION DES ACTIONS
        //============================================================================================
        //----------------------------------ACTION : LIST
        //List permet de sélectionner une liste d'incident. Leur affichage est géré par En jSON. Voir la config des vues.
//        if (action.equals("list")) {
//
//            //Récupération du type 
//            Class c = null;
//            DAOIncident dao = null;
//            try {
//                System.out.println("AAA");
//                c = Class.forName("rssagregator.beans.incident." + request.getParameter("type"));
//                System.out.println("CLASS : " + c);
//                dao = (DAOIncident) DAOFactory.getInstance().getDaoFromType(c);
//                if (!AbstrIncident.class.isAssignableFrom(c)) {
//                    System.out.println("PAS ASSIGNABLE");
//                    throw new Exception("non");
//                }
//            } catch (Exception e) {
//
//                System.out.println("ERR" + e);
//            }
//
//            try {
//                firstResult = new Integer(request.getParameter("firstResult"));
//            } catch (Exception e) {
//                firstResult = 0;
//            }
//            dao.setFistResult(firstResult);
//            request.setAttribute("firstResult", firstResult);
//
//            try {
//                itPrPage = new Integer(request.getParameter("itPrPage"));
//            } catch (Exception e) {
//                itPrPage = 25;
//            }
//            dao.setMaxResult(itPrPage);
//            request.setAttribute("itPrPage", itPrPage);
//
//            try {
//                clos = Boolean.valueOf(request.getParameter("clos"));
//
//            } catch (Exception e) {
//                clos = false;
//            }
//            dao.setClos(clos);
//            request.setAttribute("clos", clos);
//
//
//
//            //Criteria Flux lie. N'est valable que pour les incidents de collecte.
//            if (c != null && c.equals(CollecteIncident.class)) {
//                String[] fluxLie = request.getParameterValues("fluxSelection2");
//                System.out.println("###" + fluxLie);
//                System.out.println("---> RESTRICTION FLUX");
//                DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
//                List<Flux> listFluxLie = new ArrayList<Flux>();
//                if (fluxLie != null && fluxLie.length > 0) {
//                    for (int i = 0; i < fluxLie.length; i++) {
//
//                        String strIdFlux = fluxLie[i];
//                        System.out.println("############FLUX : " + strIdFlux);
//                        Flux f = (Flux) daoFlux.find(new Long(strIdFlux));
//                        listFluxLie.add(f);
//                    }
//
//                    if (!listFluxLie.isEmpty()) {
//                        dao.setCriteriaFluxLie(listFluxLie);
//                    }
//                }
//            }
//
//
//            Integer nbItem = dao.findnbMax(c);
//            request.setAttribute("nbitem", nbItem);
//
//            //recup de la list des incidents
//            List<Object> listAll = dao.findCriteria(c);
//
//            System.out.println("°°°° LIST : " + listAll);
//            System.out.println("°°°° LIST : " + listAll.size());
//            System.out.println("TAILLE LISTE : " + listAll.size());
//            request.setAttribute(ATT_LIST, listAll);
//            //--------------------------------------------ACTION : MOD-------------------------------------
//        } 


        if (action.equals("list")) {

            try {
                Class c = null;
                DAOIncident dao = null;
                try {
                    System.out.println("AAA");
                    c = Class.forName("rssagregator.beans.incident." + request.getParameter("type"));
                    System.out.println("CLASS : " + c);
                    dao = (DAOIncident) DAOFactory.getInstance().getDaoFromType(c);
                    System.out.println("LE type : " + c);
                    if (!AbstrIncident.class.isAssignableFrom(c)) {
                        System.out.println("PAS ASSIGNABLE");
                        throw new Exception("non");
                    }
                } catch (Exception e) {

                    System.out.println("ERR" + e);
                }


                AbstrForm form = FORMFactory.getInstance().getForm(c, "list");
                System.out.println("FOMR : " + form);
                System.out.println("DAO : " + dao);
                System.out.println("TYPE : " + c.getName());
                form.parseListeRequete(request, dao);
                dao.setCriteriaSearchFilters(form.getFiltersList());
                ServletTool.actionLIST(request, c, ATT_OBJ, dao);


            } catch (Exception ex) {
                Logger.getLogger(IncidentsSrvl.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (action.equals("mod")) {
            System.out.println("");
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
                    System.out.println("ERREUR CLASS");
                    throw new Exception("non");
                }
                System.out.println("CLASS : " + c);
                ServletTool.actionREAD(request, c, ATT_OBJ);

            } //               Class c = Class.forName("rssagregator.beans.incident.CollecteIncident");
            catch (ClassNotFoundException ex) {
                System.out.println("CLASS not foun");
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
            System.out.println("ID : " + listId.size());

            DAOIncident dao = DAOFactory.getInstance().getDAOIncident();
            dao.setClassAssocie(AbstrIncident.class);
            for (int i = 0; i < listId.size(); i++) {
                Long long1 = listId.get(i);
                System.out.println("ID ds servlet " + long1);
                AbstrIncident incident = (AbstrIncident) dao.find(long1);
                System.out.println("INCID " + incident);
                if (incident != null) {
                    listIncid.add(incident);
                    System.out.println("INCID DS SERVLET : " + incident.getID());
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
            System.out.println("JsonDesc");
            VUE = "/WEB-INF/incidentJSONDesc.jsp";
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
//        System.out.println("Class" + c.toString());
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
