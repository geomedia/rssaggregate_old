/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import dao.DAOFactory;
import dao.DaoItem;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.form.ItemForm;

/**
 *
 * @author clem
 */
@WebServlet(name = "Item", urlPatterns = {"/item"})
public class ItemSrvl extends HttpServlet {

    public static final String VUE = "/WEB-INF/itemjsp.jsp";
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

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        request.setAttribute("action", action);

        // On récupère le type de sélection.
        String type = request.getParameter("type");


        //DAO

        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        ItemForm form = new ItemForm();


        Item item = null;

        // On récupère l'item si un id est demandé dans le get
        String idString = request.getParameter("id");
        if (idString != null && !idString.equals("")) {
            Long id = new Long(request.getParameter("id"));
            request.setAttribute("id", id);
            item = (Item) daoItem.find(id);
        }


        if (action.equals("list")) {

            /**
             * *
             * Entrée des parametres pour compléter la vue
             */
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

            } catch (Exception e) {
                firsResult = 0;
            }
            request.setAttribute("firsResult", nbrItemPrPage);
            daoItem.setFistResult(firsResult);

            // On récupère la liste des flux
//            request.setAttribute("listflux", ListeFluxCollecteEtConfigConrante.getInstance().listFlux);
            request.setAttribute("listflux", DAOFactory.getInstance().getDAOFlux().getListFlux());

            
            
            // SI on doit restreindre la sélection à un flux 
            try {
//                Flux f = ListeFluxCollecteEtConfigConrante.getInstance().getflux(new Long(request.getParameter("id-flux")));
                Flux f = DAOFactory.getInstance().getDAOFlux().getflux(new Long(request.getParameter("id-flux")));
//                        ListeFluxCollecteEtConfigConrante.getInstance().getflux(new Long(request.getParameter("id-flux")));
                daoItem.setWhere_clause_flux(f);

                request.setAttribute("idflux", f.getID().toString());
            } catch (Exception e) {
                daoItem.setWhere_clause_flux(null);
            }

            //Selection de l'ordre
            try {
                String s = request.getParameter("order");
                String desc = request.getParameter("desc");
                if (!s.equals("")) {
                    if (s.equals("dateRecup") || s.equals("datePub")) {
                        daoItem.setOrder_by(s);
                        if (desc.equals("true")) {
                            daoItem.setOrder_desc(Boolean.TRUE);
                        }
                    }
                }
            } catch (Exception e) {
            }


            //On récupère le nombre max d'item
            Integer nbItem = daoItem.findNbMax();
            request.setAttribute("nbitem", nbItem);


            //En fonction de la sélection demander on formule la bonne recherche
            List<Item> listItem;
            listItem = daoItem.findCretaria();
            request.setAttribute("listItem", listItem);
            System.out.println("NBIT : " + listItem.size());
        }
        request.setAttribute(ATT_ITEM, item);

        request.setAttribute("navmenu", "item");
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
