/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.form.IncidentForm;
import rssagregator.beans.incident.AbstrIncident;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOIncident;
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
//        String action = request.getParameter("action");
//        if (action == null) {
//            action = "recherche";
//        }
//        request.setAttribute("action", action);


//        DAOIncident dao = DAOFactory.getInstance().getDAOIncident();
        IncidentForm form = new IncidentForm();
        request.setAttribute("form", form);


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
        //                              GESTION DES ACTIONS
        //============================================================================================
        //----------------------------------ACTION : RECHERCHE
        if (action.equals("list")) {
            //Récupération du type 
            Class c = null;
            DAOIncident dao = null;
            try {
                System.out.println("AAA");
                c = Class.forName("rssagregator.beans.incident." + request.getParameter("type"));
                System.out.println("CLASS : " + c);
                dao = (DAOIncident) DAOFactory.getInstance().getDaoFromType(c);
                if (!AbstrIncident.class.isAssignableFrom(c)) {
                    throw new Exception("non");
                }
            } catch (Exception e) {
                System.out.println("ERR" + e);
            }

            System.out.println("DAO : " + dao);
            System.out.println("CLASS : " + c);

            String type = request.getParameter("type");


//            if (type.equals("FluxIncident")) {
//                dao = (DAOIncident<CollecteIncident>) DAOFactory.getInstance().getDaoFromType(CollecteIncident.class);
//                c = CollecteIncident.class;
//
//            } else if (type.equals("ServerIncident")) {
//                System.out.println("SERVEUR INCIDENT");
//                dao = (DAOIncident<MailIncident>) DAOFactory.getInstance().getDaoFromType(MailIncident.class);
//                c = MailIncident.class;
//            } else if (type.equals("SynchroIncident")) {
//                dao = (DAOIncident) DAOFactory.getInstance().getDaoFromType(SynchroIncident.class);
//                c = SynchroIncident.class;
//            }

            try {
                firstResult = new Integer(request.getParameter("firstResult"));
            } catch (Exception e) {
                firstResult = 0;
            }
            dao.setFistResult(firstResult);
            request.setAttribute("firstResult", firstResult);

            try {
                itPrPage = new Integer(request.getParameter("itPrPage"));
            } catch (Exception e) {
                itPrPage = 25;
            }
            dao.setMaxResult(itPrPage);
            request.setAttribute("itPrPage", itPrPage);


            try {
                clos = Boolean.valueOf(request.getParameter("clos"));

            } catch (Exception e) {
                clos = false;
            }
            dao.setClos(clos);
            request.setAttribute("clos", clos);

            Integer nbItem = dao.findnbMax(c);
            request.setAttribute("nbitem", nbItem);

            //recup de la list des incidents
            List<Object> listAll = dao.findCriteria(c);
            System.out.println("TAILLE LISTE : " + listAll.size());
            request.setAttribute(ATT_LIST, listAll);
            //--------------------------------------------ACTION : MOD-------------------------------------
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
                redir(request, ATT_SERV_NAME + "/read?id=" + request.getParameter("id"), "L'entité demandée n'existe pas !", Boolean.TRUE);
            } catch (Exception ex) {
                redir(request, ATT_SERV_NAME + "/read?id=" + request.getParameter("id"), "L'entité demandée n'existe pas !", Boolean.TRUE);
            }
        }
// gestion de la vue et de l'envoie vers la JSP
        if (vue.equals("jsondesc")) {
            System.out.println("JsonDesc");
            VUE = "/WEB-INF/incidentJSONDesc.jsp";
        }

        if (vue.equals("html")) {
            VUE = "/WEB-INF/incidentHTML.jsp";
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
