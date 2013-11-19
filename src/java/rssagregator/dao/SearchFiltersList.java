/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Permet de contenir un ensemble de {@link SearchFilter}
 *
 * @author clem
 */
public class SearchFiltersList {

    List<SearchFilter> filters = new ArrayList<SearchFilter>();
    protected String criteriaSidx; // La colonne sur laquel il faut ordonner les resultat
    protected String criteriaSord; // le sens de l'ordre asc desc
    protected Integer criteriaStartRow; //Premier enregistrement permettant de construire la limite
    protected Integer criteriaPage;  // Utile pour la dao ?
    protected Integer criteriaRow; // Nombre d'enregistrement pour construire la limite (second paramettre de la limite).
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SearchFiltersList.class);

    public SearchFiltersList() {
    }

    /**
     * *
     * Retourne la requête sous forme d'une chaine JSON.
     *
     * @return
     */
    public String returnJsonString() {
        throw new UnsupportedOperationException("pas encore implémenté");
    }

    public List<SearchFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<SearchFilter> filters) {
        this.filters = filters;
    }

//    /**
//     * *
//     * Permet de parser un json string et de définir les scritères du filtre
//     *
//     * @param string
//     */
//    @Deprecated // ----> C'est maintenant une fonction inclu dans les objet formulaire. Chaque formulaire est propre a un beans et peu sépcifier comment on récupère les parametres lors d'un action list
//    public void feedFromJSONString(String string) {
//        System.out.println("ICI");
//        if (string != null && !string.isEmpty()) {
//
//            JSONParser parse = new JSONParser();
//            SearchFiltersList filters = new SearchFiltersList(); // On récupère l'objet filters
//
//
//            //-----> Recupération des champs spéciaux : spefield
//            try {
//                JSONObject filter = (JSONObject) parse.parse(string);
//                JSONArray speFieldsArray = (JSONArray) filter.get("spefield");
//                System.out.println("Nombre de CHAMPS SPE : " + speFieldsArray.size());
//
//                if (speFieldsArray != null) {
//                    for (int i = 0; i < speFieldsArray.size(); i++) {
//                        System.out.println("=============================");
//                        System.out.println("----> IT : " + i);
//                        System.out.println("=============================");
//                        SearchFilter searchFilter = new SearchFilter();
//                        JSONObject speField = (JSONObject) speFieldsArray.get(i);
//
//                        String field = (String) speField.get("field");
//                        JSONArray dataArray = (JSONArray) speField.get("data"); // C'est un tableau d'id en JSON
//                        String op = (String) speField.get("op");
//
//
//                        //-------------INTERPRETATION D'un FluxId
//                        if (field.equals("idFlux")) {
//                            //Concaténation du tableau d'id
//                            String concat = "";
//                            for (int j = 0; j < dataArray.size(); j++) {
//                                Long id = (Long) dataArray.get(j);
//                                concat += id + ",";
//                                System.out.println("ID : " + id);
//                            }
//                            concat = concat.substring(0, concat.length() - 1);
//                            System.out.println("Concat : " + concat);
//                            List<Long> listLongId = ServletTool.parseidFromRequest(null, concat);
//                            List<Flux> listFlux = new ArrayList<Flux>();
//                            for (int j = 0; j < listLongId.size(); j++) {
//                                Long long1 = listLongId.get(j);
//                                Flux f = (Flux) DAOFactory.getInstance().getDAOFlux().find(long1);
//                                if (f != null) {
//                                    listFlux.add(f);
//                                }
//
//                            }
//                            searchFilter.setData(listFlux);
//                            searchFilter.setOp(op);
//                            searchFilter.setField(field);
//                            this.filters.add(searchFilter);
//
//                            System.out.println("--------------");
//                        }
//
//                        //---------Interprétation d'une date
//                        if (dataArray.size() == 1 && dataArray.get(0).getClass().equals(String.class)) {
//                            String data = (String) dataArray.get(0);
//                            System.out.println("---> STRING DATE : " + data);
//                            // On cherche a vérifier par rapport à un pattern de date
//                            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
//                            try {
//                                DateTime dt = fmt.parseDateTime(data);
//                                searchFilter.setData(dt.toDate());
//                                searchFilter.setOp(op);
//                                searchFilter.setField(field);
//                                filters.getFilters().add(searchFilter);
//                            } catch (Exception e) {
//                                logger.error("e", e);
//                            }
//                        } else {
//                            System.out.println("DEBUG");
//                            System.out.println("SIZE : " + dataArray.size());
//                            System.out.println("CLASS : " + dataArray.get(0).getClass());
//                        }
//
//
//                    }
//                }
//            } catch (Exception e) {
//                logger.debug("err", e);
//            }
//        }
//    }

    public String getCriteriaSidx() {
        return criteriaSidx;
    }

    public void setCriteriaSidx(String criteriaSidx) {
        this.criteriaSidx = criteriaSidx;
    }

    public String getCriteriaSord() {
        return criteriaSord;
    }

    public void setCriteriaSord(String criteriaSord) {
        this.criteriaSord = criteriaSord;
    }

    public Integer getCriteriaStartRow() {
        return criteriaStartRow;
    }

    public void setCriteriaStartRow(Integer criteriaStartRow) {
        this.criteriaStartRow = criteriaStartRow;
    }

    public Integer getCriteriaPage() {
        return criteriaPage;
    }

    public void setCriteriaPage(Integer criteriaPage) {
        this.criteriaPage = criteriaPage;
    }

    public Integer getCriteriaRow() {
        return criteriaRow;
    }

    public void setCriteriaRow(Integer criteriaRow) {
        this.criteriaRow = criteriaRow;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.filters != null ? this.filters.hashCode() : 0);
        hash = 79 * hash + (this.criteriaSidx != null ? this.criteriaSidx.hashCode() : 0);
        hash = 79 * hash + (this.criteriaSord != null ? this.criteriaSord.hashCode() : 0);
        hash = 79 * hash + (this.criteriaStartRow != null ? this.criteriaStartRow.hashCode() : 0);
        hash = 79 * hash + (this.criteriaPage != null ? this.criteriaPage.hashCode() : 0);
        hash = 79 * hash + (this.criteriaRow != null ? this.criteriaRow.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SearchFiltersList other = (SearchFiltersList) obj;
        if (this.filters != other.filters && (this.filters == null || !this.filters.equals(other.filters))) {
            return false;
        }
        if ((this.criteriaSidx == null) ? (other.criteriaSidx != null) : !this.criteriaSidx.equals(other.criteriaSidx)) {
            return false;
        }
        if ((this.criteriaSord == null) ? (other.criteriaSord != null) : !this.criteriaSord.equals(other.criteriaSord)) {
            return false;
        }
        if (this.criteriaStartRow != other.criteriaStartRow && (this.criteriaStartRow == null || !this.criteriaStartRow.equals(other.criteriaStartRow))) {
            return false;
        }
        if (this.criteriaPage != other.criteriaPage && (this.criteriaPage == null || !this.criteriaPage.equals(other.criteriaPage))) {
            return false;
        }
        if (this.criteriaRow != other.criteriaRow && (this.criteriaRow == null || !this.criteriaRow.equals(other.criteriaRow))) {
            return false;
        }
        return true;
    }

   
    
    
    
    
}
