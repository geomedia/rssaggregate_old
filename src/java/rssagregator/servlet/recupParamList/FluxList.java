/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet.recupParamList;

import javax.servlet.http.HttpServletRequest;
import rssagregator.dao.SearchFilter;
import rssagregator.dao.SearchFiltersList;

/**
 * L'objectif de ce type de class est de récupérer les paramettres dans la HttpServletRequest afin de construire les
 * filtres de recharche {@link SearchFilter} à destination des DAO. Plus simplement, des attribut de la requête peuvent aussi être fixé en fonction des paramettres découverts
 *
 * @author clem
 */
public class FluxList {

    String action;
    HttpServletRequest request;
    SearchFiltersList filtersList;
    
    public void interpreterRequete(){

        
    }
}
