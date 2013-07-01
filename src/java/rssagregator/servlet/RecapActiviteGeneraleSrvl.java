/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import rssagregator.beans.Flux;
import rssagregator.beans.RecapActivite;
import rssagregator.beans.form.RecapActiviteForm;

/**
 *
 * @author clem
 */
@WebServlet(name = "RecapActiviteGenerale", urlPatterns = {"/recapActiviteGenerale"})
public class RecapActiviteGeneraleSrvl extends HttpServlet {

    public static final String VUE = "/WEB-INF/recapactivitegeneralejsp.jsp";

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

        request.setAttribute("navmenu", "recap");
        Map<String, String> redirmap = null;


        // récupération de l'action
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        request.setAttribute("action", action);
    
      if (action.equals("json")) {
            response.setContentType("Content-type: text/json");

            // On recupère le recap de la session
            HttpSession session = request.getSession();
            RecapActivite recap = (RecapActivite) session.getAttribute("recapActivite");

            //On construit le json
            int i;

            PrintWriter out = response.getWriter();

            try {
                out.println(recap.getJson());
            } catch (Exception e) {
                  Logger.getLogger(RecapActiviteGeneraleSrvl.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            //Capture de la liste des flux pour créer le menu

//            DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
//            List<Flux> list = daoFlux.findAllFlux(false);
//            request.setAttribute("listFlux", list);
                   request.setAttribute("listFlux", DAOFactory.getInstance().getDAOFlux().findAllFlux(false));
            
            // Liste des journaux
                    request.setAttribute("listJournaux", DAOFactory.getInstance().getDaoJournal().findall());
            
            
            

            RecapActivite recapActivite = new RecapActivite();
            RecapActiviteForm form = new RecapActiviteForm();

            request.setAttribute("obj", recapActivite);
            request.setAttribute("form", form);

            // Il faut construire le parametre que le javascript va demander

            if (request.getMethod().equals("POST")) {

                recapActivite = (RecapActivite) form.bind(request, recapActivite, RecapActivite.class);
                //On passe l'objet par le biai de la session
                HttpSession session = request.getSession();
                session.setAttribute("recapActivite", recapActivite);

                request.setAttribute("recapActivite", recapActivite);
            }

            // récupération des argument
            request.setAttribute("obj", recapActivite);
            request.setAttribute("form", form);
            this.getServletContext().getRequestDispatcher(VUE).forward(request, response);


            // mise en forme les objets à disposer dans le graphique

        }
    }

    public static void main(String[] args) {
        Date dateDebut = new Date();
        System.out.println(dateDebut.getTime());
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

//    public JSONArray jsonGraphEncode(List<Item> items, RecapActivite recap) {
//        // On commence par trier la list des items par date;
//        Collections.sort(items);
//
//        DateTime start = new DateTime(recap.getDate1());
//        DateTime end = new DateTime(recap.getDate2());
//
//// period of 1 year and 7 days
//
//        Days days = Days.daysBetween(start, end);
//
//        int i;
//        // On initialise la liste des résultat
//        Integer[] tabRetour = new Integer[days.getDays()];
//
//        // Initialisation du tableau à 0
//        for (i = 0; i < tabRetour.length; i++) {
//            tabRetour[i] = 0;
//        }
//
//        // Calcul par jour
//        for(i=0;i<items.size();i++){
//            DateTime d = new DateTime(items.get(i).getDateRecup());
//            Period p = new Period(start, d);
//            tabRetour[p.getDays()]++;
//        }
//
//// Formation du tableau Json
//        JSONArray jsona2 = new JSONArray();
//        for (i = 0; i < tabRetour.length; i++) {
//            jsona2.add(tabRetour[i]);
//        }
//
//        return jsona2;
//    }
}
