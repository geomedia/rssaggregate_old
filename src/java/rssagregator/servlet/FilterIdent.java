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
 * Filtre qui oblige l'utilisateur a s'identifier POUR toute les page du site EXEPTE le contenu dans /ress
 *
 * @author clem
 */
public class FilterIdent implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        Boolean ok = false;

        //--------------------URL DE LIBRE PASSAGE
        //On admet un passage sans authentification pou le répertoire /ress
        String servString = req.getServletPath();
        if (servString.matches("^/ress/+.*")) {
            ok = true;
        } else {
            ok = false;
        }

        //-------------------TEST POUR LES URL NON LIBRE DE PASSAGE
        //Si ce n'est pas une url de libre passage
        if (!ok) {
            try {
                HttpSession session = req.getSession();
                UserAccount u = (UserAccount) session.getAttribute("authuser");
                if (u == null) {
                    ok = false;
                }
                else{
                    ok = true;
                }
            } catch (Exception e) {
                ok = false;
            }
        }

        //------------------TRATEMENT DE LA REDIRECTION
        if (ok) { // Si le passage est ok (url de libre passage ou authentification ok
            System.out.println("Passage ok");
            chain.doFilter(request, response);
        } else { // Sinon on redirige l'utilisateur vers la page d'identification. On ajoutye un attribut url demande
            System.out.println("DDE IDENT");
            request.setAttribute("askurl", req.getRequestURI());
            request.getRequestDispatcher("/ident").forward(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
