/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.TransactionRequiredException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import rssagregator.beans.Flux;
import rssagregator.beans.Journal;
import rssagregator.beans.traitement.ComportementCollecte;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import rssagregator.services.ServiceCollecteur;

/**
 *
 * @author clem
 */
@WebServlet(name = "Simpleadd", urlPatterns = {"/Simpleadd"})
public class Simpleadd extends HttpServlet {

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

        request.setAttribute("listcompo", DAOFactory.getInstance().getDAOComportementCollecte().findall());
        request.setAttribute("listjournaux", DAOFactory.getInstance().getDaoJournal().findall());

        System.out.println("SIZE : " + DAOFactory.getInstance().getDAOComportementCollecte().findall().size());


        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        request.setAttribute("action", action);


        if (action.equals("addcompo")) {
            ComportementCollecte action1 = ComportementCollecte.getDefaultCollectAction();
            try {
                DAOFactory.getInstance().getDAOComportementCollecte().creer(action1);
            } catch (Exception ex) {
                Logger.getLogger(Simpleadd.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


        if (action.equals("addjournal")) {
            Journal j = new Journal();
            j.setNom("Sans Nom");
            try {
                DAOFactory.getInstance().getDaoJournal().creer(j);
            } catch (Exception ex) {
                Logger.getLogger(Simpleadd.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (action.equals("vider")) {


            List<Flux> listflux = DAOFactory.getInstance().getDAOFlux().findAllFlux(Boolean.TRUE);
            int i;
            for (i = 0; i < listflux.size(); i++) {
                try {
                    DAOFactory.getInstance().getDAOFlux().remove(listflux.get(i));
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(Simpleadd.class.getName()).log(Level.SEVERE, null, ex);
                } catch (TransactionRequiredException ex) {
                    Logger.getLogger(Simpleadd.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(Simpleadd.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            List<Object> listj = DAOFactory.getInstance().getDaoJournal().findall();
            for (i = 0; i < listj.size(); i++) {
                try {
                    DAOFactory.getInstance().getDaoJournal().remove(listj.get(i));
                } catch (Exception ex) {
                    Logger.getLogger(Simpleadd.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            List<Object> listcompo = DAOFactory.getInstance().getDAOComportementCollecte().findall();
            for (i = 0; i < listcompo.size(); i++) {
                try {
                    DAOFactory.getInstance().getDAOComportementCollecte().remove(listcompo.get(i));
                } catch (Exception ex) {
                    Logger.getLogger(Simpleadd.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

//            DAOFactory.getInstance().getDAOFlux().forceNotifyObserver();
            ServiceCollecteur.getInstance().update(null, "reload all");
        }
        if (action.equals("recolte")) {
            ServiceCollecteur collecte = ServiceCollecteur.getInstance();

//                collecte.getPoolSchedule().shutdownNow();

            List<Flux> listFlux = DAOFactory.getInstance().getDAOFlux().findAllFlux(Boolean.TRUE);

            DateTime dtDebut = new DateTime();
            try {
                collecte.majManuellAll(listFlux);

            } catch (Exception ex) {
                Logger.getLogger(Simpleadd.class.getName()).log(Level.SEVERE, null, ex);
            }

            DateTime dtFIN = new DateTime();
            Interval interval = new Interval(dtDebut, dtFIN);
            System.out.println("Temps d'exe : " + interval.toDuration().getStandardSeconds());

        }


        if (request.getMethod().equals("POST")) {


            // On récupère le journal 
            String s = request.getParameter("journal");
            Journal j = null;
            try {
                Long idj = new Long(s);
                j = (Journal) DAOFactory.getInstance().getDaoJournal().find(idj);
            } catch (Exception e) {
            }


            // On récupère le comportement de capture
            ComportementCollecte compo = null;
            String s2 = request.getParameter("comportement");
            try {
                Long idc = new Long(s2);
                compo = (ComportementCollecte) DAOFactory.getInstance().getDAOComportementCollecte().find(idc);

            } catch (Exception e) {
            }


            // Periodicité
            String s3 = request.getParameter("periodicite");


            

            int i;
            String tatdeFlux = request.getParameter("txt");
            System.out.println(tatdeFlux);

            StringTokenizer st = new StringTokenizer(tatdeFlux);
            DaoFlux dao = DAOFactory.getInstance().getDAOFlux();

            while (st.hasMoreElements()) {
                try {
                    Flux fl = new Flux(st.nextToken());
                    fl.setActive(Boolean.TRUE);
//                    fl.setPeriodiciteCollecte(60);

                    if (j != null) {
                        fl.setJournalLie(j);
                    }
                    fl.setMediatorFlux(compo);

                    
//                    fl.setPeriodiciteCollecte(new Integer(s3));

                    try {
                        dao.creer(fl);
                    } catch (Exception e) {
                    }


                } catch (Exception ex) {
                    Logger.getLogger(Simpleadd.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("err");
                }

            }
            
//            dao.forceNotifyObserver();
        }

        this.getServletContext().getRequestDispatcher("/WEB-INF/simpleadd.jsp").forward(request, response);
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
