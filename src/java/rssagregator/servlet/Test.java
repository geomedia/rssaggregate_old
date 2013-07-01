/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import rssagregator.beans.Flux;
import rssagregator.beans.Journal;
import rssagregator.dao.DAOConf;
import rssagregator.dao.DaoJournal;
import rssagregator.services.ServiceCollecteur;
import rssagregator.utils.SetDonnee;

/**
 *
 * @author clem
 */
@WebServlet(name = "Test", urlPatterns = {"/Test"})
public class Test extends HttpServlet {
private static Logger logger = Logger.getLogger(Test.class);
    

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
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        
        logger.debug("Je suis un debug");


        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Test</title>");
            out.println("</head>");
            out.println("<body>");


            if (action.equals("setdonnee")) {

                Journal j_libre = new Journal();
                j_libre.setNom("Liberation");
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/9/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/58/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/13/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/53/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/100160/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/17/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/100206/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/44/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/10/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/100226/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/11/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/18/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/12/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/14/"));
                j_libre.getFluxLie().add(new Flux("http://rss.liberation.fr/rss/100197/"));
                
                
                Journal  j_monde = new Journal();
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/une.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/videos.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/afrique.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/services-aux-internautes.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/ameriques.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/argent.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/asie-pacifique.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/crise-financiere.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/culture.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/disparitions.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/documents-wikileaks.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/elections-regionales.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/elections-italiennes.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/emploi.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/enseignement-superieur.xml"));
                j_monde.getFluxLie().add(new Flux("http://www.lemonde.fr/rss/tag/economie.xml"));
               
                
                



//                ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
                DaoJournal daoJournal = DAOFactory.getInstance().getDaoJournal();
//                DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();
//                DAOConf dAOConf = DAOFactory.getInstance().getDAOConf();
//
//                dAOConf.chargerDepuisBd();
//
//                daoFlux.addObserver(collecteur);
//                dAOConf.addObserver(collecteur);
//
//
//
                try {
                    daoJournal.creer(j_libre);
                    daoJournal.creer(j_monde);
                } catch (Exception ex) {
//                    Logger.getLogger(SetDonnee.class.getName()).log(Level.SEVERE, null, ex);
                }
                ServiceCollecteur.getInstance().update(DAOFactory.getInstance().getDAOFlux(), null);

            }
            
            
            if(action.equals("simpleadd")){
      
                
                
                
            }
            
            


            if (action.equals("updateall")) {
                ServiceCollecteur collecte = ServiceCollecteur.getInstance();

//                collecte.getPoolSchedule().shutdownNow();


                List<Flux> listFlux = DAOFactory.getInstance().getDAOFlux().findAllFlux(Boolean.TRUE);


                DateTime dtDebut = new DateTime();
                try {
                    collecte.majManuellAll(listFlux);
                } catch (Exception ex) {
//                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                }

                             DateTime dtFIN = new DateTime();
                Interval interval = new Interval(dtDebut, dtFIN);
                out.println("Temps d'exe : " + interval.toDuration().getStandardSeconds());

            }



            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
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
