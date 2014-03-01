/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import rssagregator.beans.Conf;
import rssagregator.beans.UserAccount;
import rssagregator.dao.DAOFactory;

/**
 * C'est un filtre appliqué aux servlet : <ul>
 * <li>Flux</li>
 * <li>Journaux</li>
 * <li>TypeFlux</li>
 * </ul>
 * <p>Elle permet de restreindre l'acces en écriture aux beans en vérifiant que
 * le Service JMS est bien fonctionnel. En effet, il ne faut pas que
 * l'utilisateur puisse effectuée des ajout de flux ou autre lorsque le système
 * de synchronisation n'est pas en place. Le filtre empeche les moficifation
 * dans les cas suivants : <ul>
 * <li>Si le serveur est maitre, qu'il possede des esclaves et que la connection
 * JMS n'est pas active</li>
 * <li>Si l'utilisateur n'est pas administrateur</li>
 * </ul>
 * </p>
 *
 * @author clem
 */
public class FilterBasicBeans implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        //On récupère l'action.
        String action = request.getPathInfo();
        if (action != null) {
            if (action.length() > 0) {
                action = action.substring(1, action.length());
            }

            Conf conf = DAOFactory.getInstance().getDAOConf().getConfCourante();
            HttpSession session = request.getSession();
            UserAccount u = (UserAccount) session.getAttribute("authuser");

            //----------------------------------------------------------------------------------------
            //                  Verif spécifique à la modification des beans
            //----------------------------------------------------------------------------------------

            //Pour les action de modification (add, mod, rem)
            if (action.equals("add") || action.equals("mod") || action.equals("rem")) {
                //On vérifie le statut JMS
//                Boolean statutJMS = ServiceSynchro.getInstance().getStatutConnection();
                // Si le serveur est maitre, qu'il possede des esclaves et que la connection JMS n'est pas active
//                if (conf.getMaster() && conf.getServeurSlave().size() > 0 && !statutJMS) {
//                    request.setAttribute("accesmsg", "<strong>La connection JMS n'est pas active</strong>. Votre action demande que la connection JMS soit active afin de répercuter les éventuelles moficications sur les serveurs esclaves");
//                    request.getRequestDispatcher("/erreurHTML.jsp").forward(request, response);
//                } // Si c'est un serveur esclaves, on refuse des modification par les servlet. Les entitées ne doivent être rajouté que par synchronisation JMS
//                else if (!conf.getMaster()) {
//                    request.setAttribute("accesmsg", "Il s'agit d'un serveur esclave ! Vous ne devez pas ajouter d'entités sur un serveur esclave. Allez sur le serveur maitre pour faire vos modifications. Celles ci seront répercuté par la synchronisation");
//                    response.sendRedirect(request.getContextPath() + "/erreurHTML.jps");
//                } //Si c'est un utilisateur non administrateur On refuse
                
                if (u != null && !u.getAdminstatut()) {
                    request.setAttribute("accesmsg", "Vous n'avez pas la permission");
                    request.getRequestDispatcher("/erreurHTML.jsp").forward(request, response);
                } else { // Si action mod add rem et tout ok on passe
                    chain.doFilter(request, response);
                }
            }
            else{ // Si c'est une action annodine (exemple recherche)
                chain.doFilter(request, response);
            }
        }
        else{ // SI pas d'action On laisse passer
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
