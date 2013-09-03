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
import rssagregator.beans.UserAccount;

/**
 *
 * @author clem
 */
public class FilterAdminOnly implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        UserAccount u = (UserAccount) session.getAttribute("authuser");

        if (u != null && !u.getAdminstatut()) {
            request.setAttribute("accesmsg", "Vous n'avez pas la permission");
            request.getRequestDispatcher("/erreurHTML.jsp").forward(request, response);
        }
        else{
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
