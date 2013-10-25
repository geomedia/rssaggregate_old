/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.ArrayList;
import java.util.List;

/**
 *  Permet de contenir un ensemble de {@link SearchFilter}
 * @author clem
 */
public class SearchFiltersList {
    List<SearchFilter> filters = new ArrayList<SearchFilter>();

    public SearchFiltersList() {
    }
    

    
    
    /***
     * Retourne la requête sous forme d'une chaine JSON. 
     * @return 
     */
    public String returnJsonString(){
        throw new UnsupportedOperationException("pas encore implémenté");
    }

    public List<SearchFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<SearchFilter> filters) {
        this.filters = filters;
    }
}
