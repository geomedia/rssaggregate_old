/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.Journal;
import rssagregator.beans.exception.DonneeInterneCoherente;
import rssagregator.beans.exception.IncompleteBeanExeption;
import rssagregator.beans.form.AbstrForm;
import rssagregator.beans.form.FORMFactory;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoJournal;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.tache.TacheDecouverteAjoutFlux;
import rssagregator.utils.CodePays;
import rssagregator.utils.ServletTool;

/**
 *
 * @author clem
 */
@WebServlet(name = "Journaux", urlPatterns = {"/journaux/*"})
public class JournauxSrvl extends HttpServlet {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(JournauxSrvl.class);
    public String VUE = "/WEB-INF/journaljsp.jsp";
    public static final String ATT_FORM = "form";
    public static final String ATT_JOURNAL = "bean";
    public static final String ATT_LIST_JOURNAUX = "listjournaux";
    public static final String ATT_SERV_NAME = "journaux"; // Le nom de la servlet. utile pour construire des url dans la vue

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

        request.setAttribute("listLocal", CodePays.getLanMap().entrySet().iterator());
        request.setAttribute("listCountry", CodePays.getCountryMap().entrySet().iterator());

        String[] timeZonetab = TimeZone.getAvailableIDs();
        Arrays.sort(timeZonetab);

        request.setAttribute("fuseau", timeZonetab);
//        

        // Un simple attribut pour que le menu brille sur la navigation courante
        request.setAttribute("navmenu", "journaux");

        // récupération de l'action
        String action = ServletTool.configAction(request, "recherche");
        request.setAttribute("srlvtname", ATT_SERV_NAME);


        DaoJournal daoJournal = DAOFactory.getInstance().getDaoJournal();
        
        Journal journal = null;

        request.setAttribute(ATT_JOURNAL, journal);

        VUE = request.getParameter("vue");
        if (VUE == null || VUE.isEmpty()) {
            VUE = "/WEB-INF/journaljsp.jsp";
        }


        /**
         * *===================================================================================================
         * .....................................GESTION DES ACTIONS
         *///===================================================================================================
        //--------------------------------ACTION RECHERCHE ---------------------------------------------------------
        if (action.equals("recherche")) {
            request.setAttribute("listCountry", CodePays.getCountryMap().entrySet().iterator());




            List<Journal> listJournaux = daoJournal.findCriteria();
            request.setAttribute(ATT_LIST_JOURNAUX, listJournaux);
        } //---------------------------------ACTION LIST --------------------------------------
        else if (action.equals("list")) {
            DaoJournal dao = DAOFactory.getInstance().getDaoJournal();

            AbstrForm form = null;
            try {
                form = FORMFactory.getInstance().getForm(Journal.class, "list");

            } catch (Exception ex) {
                Logger.getLogger(JournauxSrvl.class.getName()).log(Level.SEVERE, null, ex);
            }



            try {
                form.parseListeRequete(request, dao);
            } catch (Exception ex) {
                Logger.getLogger(JournauxSrvl.class.getName()).log(Level.SEVERE, null, ex);
            }


            dao.setCriteriaSearchFilters(form.getFiltersList());



            ServletTool.actionLIST(request, Journal.class, ATT_JOURNAL, dao);
//
//            // On récupère les paramettre demandé par la grid
//            // Parametre page
//
//            //parametre de recherche
//            // On récupère les critères
////            if (request.getParameter("pays") != null && !request.getParameter("pays").isEmpty()) {
////                daoJournal.setCriteriaPays(request.getParameter("pays"));
////            }
//            
//            
//            if(request.getParameter("filters")!= null && !request.getParameter("filters").isEmpty()){
//                String filter = request.getParameter("filters");
//                JSONObject obj = new JSONObject();
//                JSONParser parse = new JSONParser();
//                try {
//                    JSONObject obj2 = (JSONObject) parse.parse(filter);
//                    JSONArray rules = (JSONArray) obj2.get("rules");
//                    for (int i = 0; i < rules.size(); i++) {
//                        JSONObject object = (JSONObject) rules.get(i);
//                       String field = (String) object.get("field");
//                       String op = (String) object.get("op");
//                       String data = (String) object.get("data");
//                        SearchFilter filt = new SearchFilter();
//                        filt.setData(data);
//                        filt.setField(field);
//                        filt.setOp(op);
//                        daoJournal.getCriteriaSearchFilter().add(filt);
//                    }
//
//                    
//                } catch (ParseException ex) {
//                    Logger.getLogger(JournauxSrvl.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//            
//
////            if (request.getParameter("langue") != null && !request.getParameter("langue").isEmpty()) {
////                daoJournal.setCriteriaLangue(request.getParameter("langue"));
////            }
//
//            Map<String, String[]> map =  request.getParameterMap();
//            for (Map.Entry<String, String[]> entry : map.entrySet()) {
//                String string = entry.getKey();
//                String[] strings = entry.getValue();
//            }
//            
//
//            //Compte du nombre total de résultat
//            Integer count = null;
//            try {
//                count = daoJournal.cptCriteria();
//                request.setAttribute("count", count);
//            } catch (Exception e) {
//                logger.debug("err count", e);
//            }
//
//
//            // ROW
//            if (VUE != null && !VUE.equals("csv")) {
//                Integer limit = null;
//                if (request.getParameter("rows") != null && !request.getParameter("rows").isEmpty()) {
//                    try {
//                        limit = new Integer(request.getParameter("rows"));
//                        request.setAttribute("rows", limit);
//                        daoJournal.setCriteriaRow(limit);
//                    } catch (Exception e) {
//                    }
//                } else {
//                }
//
//
//                //-----PAGE
//                Integer page = null;
//                if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
//                    try {
//                        page = new Integer(request.getParameter("page"));
//
//                        request.setAttribute("page", new Integer(request.getParameter("page")));
//                        Integer startRows = limit * page - limit;
//                        daoJournal.setCriteriaStartRow(startRows);
//
//                        Double totalPagedbl;
//                        Integer totalPage = null;
//                        if (count != null && limit != null && count > 0 && limit > 0) {
//                            totalPagedbl = Math.ceil(count.doubleValue() / limit.doubleValue());
//                            totalPage = totalPagedbl.intValue();
//
//                            request.setAttribute("totalPage", totalPage);
//                        } else {
//                            totalPage = 1;
//                            request.setAttribute("totalPage", totalPage);
//                        }
//                    } catch (Exception e) {
//                    }
//                } else {
//                    request.setAttribute("page", new Integer(1));
//                }
//            }
//
//
//            // Traitement de l'ordre 
//            if (request.getParameter("sidx") != null && !request.getParameter("sidx").isEmpty()) {
//                try {
//                    request.setAttribute("sidx", request.getParameter("sidx"));
//                    daoJournal.setCriteriaSidx(request.getParameter("sidx"));
//                } catch (Exception e) {
//                }
//            }
//
//            if (request.getParameter("sord") != null && !request.getParameter("sord").isEmpty()) {
//                daoJournal.setCriteriaSord(request.getParameter("sord"));
//            }
//
//
//            if (request.getParameter("sord") != null && !request.getParameter("sord").isEmpty()) {
//                request.setAttribute("sord", request.getParameter("sord"));
//            }
//
//
//            // On utilise la dao pour effectuer la sélection
//            List<Journal> items = daoJournal.findCriteria();
//            request.setAttribute("items", items);
//
//            Integer records = daoJournal.cptCriteria();
//            request.setAttribute("records", records);




        } else if (action.equals("rem")) {
            ServletTool.actionREM(request, Journal.class, Boolean.TRUE);
//            try {
//                journal = (Journal) daoJournal.find(new Long(request.getParameter("id")));
//                try {
//                    daoJournal.remove(journal);
//                    journal.enregistrerAupresdesService();
//                    journal.forceChangeStatut();
//                    journal.notifyObservers(action);
//                    ServletTool.redir(request, "journaux/recherche", "Suppression effectuée", false);
//                } catch (Exception ex) {
//                    Logger.getLogger(JournauxSrvl.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            } catch (Exception e) {
//                ServletTool.redir(request, "journaux/recherche", "Ce journal n'existe pas", true);
//            }
        }


        //---------------------------------ACTION ADD ---------------------------------------------------
        if (action.equals("add")) {
            request.setAttribute("listcomportement", DAOFactory.getInstance().getDAOComportementCollecte().findall());
            ServletTool.actionADD(request, ATT_JOURNAL, ATT_FORM, Journal.class, Boolean.TRUE);
        } //-----------------------------------ACTION MOD ---------------------------------------------------
        else if (action.equals("mod")) {
             request.setAttribute("listcomportement", DAOFactory.getInstance().getDAOComportementCollecte().findall());
            ServletTool.actionMOD(request, ATT_JOURNAL, ATT_FORM, Journal.class, Boolean.TRUE);
        } //-------------------------------ACTION READ-------------------------------------------------------
        else if (action.equals("read")) {
            ServletTool.actionREAD(request, Journal.class, ATT_JOURNAL);
        }
        //--------------------------------ACTION DISCOVER----------------------------------------------------
        else if(action.equals("discover")){
            //Récup de l'id
            Journal j = (Journal) DAOFactory.getInstance().getDaoJournal().find(new Long(request.getParameter("id")));
  
            try {
                Future<TacheDecouverteAjoutFlux> fut = ServiceCollecteur.getInstance().decouverteFluxJournal(j, ServletTool.getBooleen(request, "persist"), ServletTool.getBooleen(request, "active"), new Short(request.getParameter("timeout")), new Short(request.getParameter("nbThread")));
                
                TacheDecouverteAjoutFlux tache = fut.get(3, TimeUnit.MINUTES);
                request.setAttribute("tacheDecouverte", tache);
                
            } catch (IncompleteBeanExeption ex) {
                Logger.getLogger(JournauxSrvl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DonneeInterneCoherente ex) {
                Logger.getLogger(JournauxSrvl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(JournauxSrvl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(JournauxSrvl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TimeoutException ex) {
                Logger.getLogger(JournauxSrvl.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch(Exception ex){
                logger.debug("Err", ex);
            }
        }


        //-----------------------------------------------------------------------------------------------
        //.....................................GESTION DE LA VUE
        //-----------------------------------------------------------------------------------------------

        if (VUE != null) {
            if (VUE.equals("jsonform")) {
                VUE = "/WEB-INF/jsonform.jsp";
            } else if (VUE.equals("grid")) {
                VUE = "/WEB-INF/journalJsonGrid.jsp";
            } else if (VUE.equals("csv")) {
                VUE = "/WEB-INF/journalCSV.jsp";
            }
        }

// redirection de l'utilisateur
//        if (action.equals("add") && journalForm.getValide()) {
//            response.sendRedirect("journaux?action=mod&id=" + journal.getID());
//        } else {
        this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
//        }
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
