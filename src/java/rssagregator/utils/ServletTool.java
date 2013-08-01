/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

/**
 *  Une série de methode static pouvant être utilisée dans les Servlet du projet. 
 * @author clem
 */
public class ServletTool {
    
    
    
    /***
     * Outil utilisé dans toutes les sevlet pour récupérer l'action demandé par l'utilisateur. La récupération se base sur le path (request.getPathInfo). 
     * @param request la request dans la servlet
     * @param defaultAction L'action par défault. Devient la valeur de l'action si on ne trouve pas d'action dans la request. Permet de rediriger sur recherche ou list facilement
     * @return l'action
     */
    public static String configAction(HttpServletRequest request, String defaultAction){
        
         String action = request.getPathInfo();
        if (action == null) {
            action = defaultAction;
        }
        else if(action.length()>0){
            action = action.substring(1, action.length());
        }
        request.setAttribute("action", action);
        return action;
    }
    
        /***
     * Une méthode pour gérer la redirmap
     * @param request La request envoyée par la servlet
     * @param url adresse de redirection
     * @param msg message a afficher à l'utilisateur
     * @param err true si il s'agit d'une erreur. false si c'est une redirection de routine, l'utilisateur est alors redirigé par javascript  secondes après.
     */
    public static void redir(HttpServletRequest request, String url, String msg, Boolean err) {
        
        HashMap<String, String> redirmap = new HashMap<String, String>();
        redirmap.put("url", url);
        redirmap.put("msg", msg);
        request.setAttribute("redirmap", redirmap);
        if (err) {
            request.setAttribute("err", "true");
        }
    }
    
}
