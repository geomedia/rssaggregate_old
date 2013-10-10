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
 * Ce filtre permet d'empêcher les utilisateur non admin de lister les utilisateurs ou de modifier un compte qui n'est
 * pas le leur
 *
 * @author clem
 */
public class FilterUserBean implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        }

        Conf conf = DAOFactory.getInstance().getDAOConf().getConfCourante();
        HttpSession session = request.getSession();
        UserAccount u = (UserAccount) session.getAttribute("authuser");


        // Pour action read et mod
        if (action.equals("read") || action.equals("mod")) {
            System.out.println("--------");
            //Si l'utilisateur n'est pas admin
            if (!u.getAdminstatut()) {
                // récup de l'id demandé
                String idReq = request.getParameter("id");
                if (idReq != null && !idReq.isEmpty()) {
                    Long valId = new Long(idReq);
                    if (valId.equals(u.getID())) {
                        // C'est ok.
                        chain.doFilter(request, response);
                    } else {
                        request.setAttribute("accesmsg", "Vous n'avez pas la permission");
                        request.getRequestDispatcher("/erreurHTML.jsp").forward(request, response);
                    }

                }
            } else { // Si c'est un admin on laisse tout passer
                chain.doFilter(request, response);
            }


        } else {
            if (u.getAdminstatut()) { // On laisse tout passer pour l'admin
                chain.doFilter(request, response);
            } else { // Si ce n'est pas un admin on le block
                request.setAttribute("accesmsg", "Vous n'avez pas la permission");
                request.getRequestDispatcher("/erreurHTML.jsp").forward(request, response);
            }
        }




    }

    @Override
    public void destroy() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
