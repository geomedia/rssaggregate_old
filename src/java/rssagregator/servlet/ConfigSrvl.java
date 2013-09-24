/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.BufferedInputStream;
import rssagregator.dao.DAOFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.beans.Conf;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.form.ConfForm;
import rssagregator.dao.DAOConf;
import rssagregator.services.ServiceSynchro;
import rssagregator.services.TacheSynchroHebdomadaire;
import rssagregator.utils.ServletTool;
import rssagregator.utils.XMLTool;

/**
 * La Servlet utilisée pour rediriger les requetes de l'utilisateur relatif à la
 * configuration du serveur. Les action suivantes peuvent être demandée : <ul>
 * <li><strong>mod :</strong> modifier la config</li>
 * <li><strong>importitem : </strong>Action pouvant être lancée si le serveur
 * est maître. Le serveur maitre va alors demander a tous les serveurs esclaves
 * les items qu'ils ont collecté</li>
 * <li><strong>importflux : </strong>permet de lancer la récupération manuelle
 * sur un serveur esclave de la liste des flux sur un serveur maître</li>
 * <li><strong>jmsreload : </strong>permet de relancer une tentative de
 * connection au service JMS. Renvoie un simple message text.</li>
 * </ul @
 *
 *
 * author clem
 */
@WebServlet(name = "Config", urlPatterns = {"/config/*"})
public class ConfigSrvl extends HttpServlet {

    public String VUE = "/WEB-INF/configjsp.jsp";
    public static final String ATT_FORM = "form";
    public static final String ATT_BEANS = "conf";

    /**
     * Processes requests for both HTTP.
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("AAA");

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");


        // Un simple attribut pour que le menu brille sur la navigation courante
        request.setAttribute("navmenu", "config");


        String action = ServletTool.configAction(request, "mod");


        Conf confcourante = null;
        ConfForm form = new ConfForm();
        DAOConf daoConf = DAOFactory.getInstance().getDAOConf();

        confcourante = DAOFactory.getInstance().getDAOConf().getConfCourante();

        System.out.println("ACTION : " + action);

        // Configuration de la vue
        VUE = request.getParameter("vue");
        if (VUE == null || VUE.isEmpty()) {
            VUE = "/WEB-INF/configjsp.jsp";
        }

        /**
         * *======================================================================================
         * ...................................ACTION MOD
         *///=====================================================================================
        if (action.equals("mod")) {
            //Si l'utilisateur à posté on bind
            if (request.getMethod().equals("POST")) {
                confcourante = (Conf) form.bind(request, confcourante, Conf.class);
            }

            request.setAttribute(ATT_FORM, form);
            request.setAttribute(ATT_BEANS, confcourante);

            // SAUVEGARDE SI INFOS 
            if (form.getValide()) {
                try {
                    DAOFactory.getInstance().getDAOConf().modifierConf(confcourante);
                    // Il faut notifier le changement pour recharger le service de reception
//                    confcourante.forceNotifyObserver();
//                DAOFactory.getInstance().getDAOConf().forceNotifyObservers();
                    ServletTool.redir(request, "config/mod", "Modification de la config effectuée", Boolean.FALSE);

                } catch (Exception ex) {
                    Logger.getLogger(ConfigSrvl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        /**
         * *=====================================================================================
         * . ....................................ACTION : IMPORT ITEM
         *///=====================================================================================
        if (action.equals("importitem") && confcourante.getMaster()) {
            // On lance manuellement la tâche de Synchro
//            ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
            
            TacheSynchroHebdomadaire recupItem = new TacheSynchroHebdomadaire(ServiceSynchro.getInstance());
            List<Item> list = null;
            try {
                TacheSynchroHebdomadaire fut = recupItem.call();
                request.setAttribute("tacheGenerale", fut);
                
                
//                list = fut.getItemTrouvees(); // TODO il faudra mettre un delai limite lors qu'on aura fait des test grandeur nature.
//                list = recupItem.call();
                request.setAttribute("listitemtrouve", list);
            } catch (Exception ex) {
                Logger.getLogger(ConfigSrvl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * =====================================================================================
         * ......................... ACTION : IMPORT FLUX
         *////***=================================================================================
        if (action.equals("importflux") && !daoConf.getConfCourante().getMaster()) {

            URL url = new URL("http://" + daoConf.getConfCourante().getHostMaster() + ":8080/RSSAgregate/flux/list?vue=fluxXMLsync");
            URLConnection connection = url.openConnection();
            connection.connect();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            Object serialisation = XMLTool.unSerialize(in);
            List<Flux> listflux = (List<Flux>) serialisation;
            request.setAttribute("listfluximporte", listflux);
            int i;
            for (i = 0; i < listflux.size(); i++) {
                try {
                    DAOFactory.getInstance().getDAOFlux().creer(listflux.get(i));
                } catch (Exception ex) {
                    Logger.getLogger(ConfigSrvl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } /**
         * *=====================================================================================
         * ............................ACTION : RELOAD JMS //
         * ======================================================================================
         */
        else if (action.equals("jmsreload")) {
            String msg = ""; // Il s'agit du message devant informer l'utilisateur sur le relancement du serveur JMS

            try {
                ServiceSynchro.getInstance().openConnection();
                msg = "OK";
            } catch (NamingException ex) {
                Logger.getLogger(ConfigSrvl.class.getName()).log(Level.SEVERE, null, ex);
                msg = "erreur : " + ex;
            } catch (JMSException ex) {
                Logger.getLogger(ConfigSrvl.class.getName()).log(Level.SEVERE, null, ex);
                msg = "erreur : " + ex;
            }
            request.setAttribute("msg", msg);
            VUE = "/WEB-INF/configJMSinfo.jsp"; // C'est une vue retournant un message texte comprennant le message en paramettre plus haut.
        }
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
