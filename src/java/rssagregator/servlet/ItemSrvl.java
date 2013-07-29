/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.form.ItemForm;

/**
 *
 * @author clem
 */
@WebServlet(name = "Item", urlPatterns = {"/item"})
public class ItemSrvl extends HttpServlet {

    public String VUE = null;
    public static final String ATT_ITEM = "item";

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


        System.out.println("coucou");
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "recherche";
        }
        request.setAttribute("action", action);

        //récupération de la vue
        String vue = request.getParameter("vue");
        if (vue == null) {
            vue = "html";
        }


        // On récupère le type de sélection.
        String type = request.getParameter("type");


        //DAO
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        daoItem.initcriteria();
        ItemForm form = new ItemForm();


        Item item = null;

        // On récupère l'item si un id est demandé dans le get
        String idString = request.getParameter("id");
        if (idString != null && !idString.equals("")) {
            Long id = new Long(request.getParameter("id"));
            request.setAttribute("id", id);
            item = (Item) daoItem.find(id);
        }


        //Action liste demander en ajax pour retour JSON
        if (action.equals("list")) {

            /**
             * Entrée des parametres pour compléter les vues
             */
            // On récupère le premier et dernier résult pour former des limites de requêtes.
                Integer nbrItemPrPage;
                try {
                    nbrItemPrPage = new Integer(request.getParameter("itPrPage"));
                } catch (Exception e) {
                    nbrItemPrPage = 20;
                }
                request.setAttribute("itPrPage", nbrItemPrPage);
                daoItem.setMaxResult(nbrItemPrPage);

                //Récupération du firs result
                Integer firsResult;
                try {
                    firsResult = new Integer(request.getParameter("firstResult"));
                    daoItem.setFistResult(firsResult);
                    request.setAttribute("firstResult", firsResult);
                    System.out.println("FIRST result  : " + firsResult);

                } catch (Exception e) {
                    firsResult = 0;
                    System.out.println("ERR first");
                }
                request.setAttribute("firsResult", firsResult);
                daoItem.setFistResult(firsResult);

           

            // SI on doit restreindre la sélection à un flux 
            try {
                String[] tabIdFluxString = request.getParameterValues("fluxSelection2");
                List<Flux> listFluxEntites = new ArrayList<Flux>();
                
                int i;
                for(i=0;i<tabIdFluxString.length;i++){
                    Flux f = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(tabIdFluxString[i]));
                    listFluxEntites.add(f);
                }
                daoItem.setWhere_clause_Flux(listFluxEntites);
                
            } catch (Exception e) {
                System.out.println("ERRRRRRRR" + e);
                daoItem.setWhere_clause_Flux(null);
            }

            //Selection de l'ordre
            try {
                String s = request.getParameter("order");
                String desc = request.getParameter("desc");
                if (!s.equals("")) {
                    if (s.equals("dateRecup") || s.equals("datePub") || s.equals("listFlux")) {
                        daoItem.setOrder_by(s);
                        if (desc.equals("true")) {
                            daoItem.setOrder_desc(Boolean.TRUE);
                        }
                    }
                }
            } catch (Exception e) {
            }

            // Récupération des date limites
            try {
                String d1 = request.getParameter("date1");
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
                DateTime dateTime = fmt.parseDateTime(d1);
                daoItem.setDate1(dateTime.toDate());
            } catch (Exception e) {
            }


            try {
                String d2 = request.getParameter("date2");
                DateTimeFormatter fmt2 = DateTimeFormat.forPattern("dd/MM/yyyy");
                DateTime dateTime2 = fmt2.parseDateTime(d2);
                daoItem.setDate2(dateTime2.toDate());
            } catch (Exception e) {
            }


            //On récupère le nombre max d'item
            Integer nbItem = daoItem.findNbMax();
            request.setAttribute("nbitem", nbItem);
            

            //En fonction de la sélection demander on formule la bonne recherche
            List<Item> listItem;
            
            // si la vue est csv il faut enlever les limites
            if(!vue.equals("html") ^ vue.equals("jsondesc")){ // en java le ^ est un XOR
                daoItem.setFistResult(null);
                daoItem.setMaxResult(null);
            }
            
            listItem = daoItem.findCretaria();
            request.setAttribute("listItem", listItem);
        }
        
        // Action recherche, correspond à la demande de la page permettant de lister les flux. Il est nécessaire de fournir les paramettres permettant de construire les menus déroulant dans la JSP
        if(action.equals("recherche")){
                 // On récupère la liste des flux utile à la génération du menu déroulant
                request.setAttribute("listflux", DAOFactory.getInstance().getDAOFlux().findAllFlux(false));

                //List des journaux
                request.setAttribute("listJournaux", DAOFactory.getInstance().getDaoJournal().findall());
        }
        
        request.setAttribute(ATT_ITEM, item);
        request.setAttribute("navmenu", "item");


        //utilisation de la vue en fonction des paramettres envoyé par l'utilisateur.
        if (vue.equals("html")) {
            VUE = "/WEB-INF/itemHTML.jsp";
        }
        if (vue.equals("csv")) {
            response.setHeader("Content-Disposition", "attachment; filename = items-export.csv");
            VUE = "/WEB-INF/itemCSV.jsp";
        } else if (vue.equals("csvexpert")) {
            response.setHeader("Content-Disposition", "attachment; filename = items-export.csv");
            VUE = "/WEB-INF/itemexpertCSV.jsp";
        }
        else if(vue.equals("jsondesc")){
            System.out.println("ZOUZou");
            VUE = "/WEB-INF/itemJSONDesc.jsp";
        }
        else if (vue.equals("xls")) {
            response.setHeader("Content-Disposition", "attachment; filename = itemss-export.xls");
            VUE = "/WEB-INF/itemXLS.jsp";
        }
        
        this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
    }

    public static String getParam(String param, HttpServletRequest request) {

        String s = request.getParameter(param);
        if (s != null && !s.equals("")) {
            System.out.println("laa");
            return s;

        }
        return "";
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
