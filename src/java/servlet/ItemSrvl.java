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

//            flux = (Flux) daoFlux.find(id);

        }


        if (action.equals("list")) {

            //On récupère le nombre max d'item
            Integer nbItem = daoItem.findNbMax();
            request.setAttribute("nbitem", nbItem);

            // récupération du numérocourant de page
            Integer numPage;
            try {
                numPage = new Integer(request.getParameter("page"));
            } catch (Exception e) {
                numPage = 1;
            }
            request.setAttribute("pageCourante", numPage);


            Integer nbrItemPrPage;
            try {
                nbrItemPrPage = new Integer(request.getParameter("nbrItemPrPage"));
            } catch (Exception e) {
                nbrItemPrPage = 20;
            }
            request.setAttribute("nbrItemPrPage", nbrItemPrPage);

            // Calcul du nombre max de page
            Double maxPage = Math.ceil((nbItem.doubleValue() / nbrItemPrPage.doubleValue()));
            request.setAttribute("maxPage", maxPage.intValue());

            request.setAttribute("nbitemTotal", nbItem);

            // recheche des items à afficher
            Integer itDebut = (numPage * nbrItemPrPage) - nbrItemPrPage;

            System.out.println("lala");

            List<Item> listItem = daoItem.findAllLimit(new Long(itDebut), new Long(nbrItemPrPage));
            request.setAttribute("listItem", listItem);
                System.out.println("NBIT : " + listItem.size());
        }
    


        request.setAttribute(ATT_ITEM, item);





        request.setAttribute("navmenu", "item");
        this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
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
