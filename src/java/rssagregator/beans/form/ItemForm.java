/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rssagregator.beans.Flux;
import rssagregator.dao.AbstrDao;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.SearchFilter;
import rssagregator.dao.SearchFiltersList;

/**
 * Les Item ne sont pas encore modifiable. Cette fonctionnalité n'est pas perçut comme nécessaire au projet. Cette
 * classe ne fait rien...
 *
 * @author clem
 */
public class ItemForm extends AbstrForm {

    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ItemForm.class);

    protected ItemForm() {
    }
    
    
    
    
    

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean validate(HttpServletRequest request) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public void parseListeRequete(HttpServletRequest request, AbstrDao dao) throws Exception {
//
////        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
//        this.filtersList = new SearchFiltersList();
//        
//        //-----------> Récupération des parametres standarts
//        this.recupStandartFilters(request, dao, this.filtersList);
//
//        //================================================================================================
//        //..........................ACTION LIST
//        //================================================================================================
//        if (action.equals("list")) {
//            
//            if (request.getParameter("filters") != null && !request.getParameter("filters").isEmpty()) {
//                String filter = request.getParameter("filters");
//                JSONParser parse = new JSONParser();
//                
//                //-------> Récupération des champs spéciaux
//                
//                try {
//                    JSONObject obj2 = (JSONObject) parse.parse(filter);
//                    JSONArray rules = (JSONArray) obj2.get("spefield");
//                    for (int i = 0; i < rules.size(); i++) {
//                        JSONObject obj = (JSONObject) rules.get(i);
//                        String field = (String) obj.get("field");
//                        String op = (String) obj.get("op");
//                        if (field.equals("listFlux")) {
//                            JSONArray idString = (JSONArray) obj.get("data"); // C'est un tableau d'id en JSON
//                            List<Flux> lfDao = new ArrayList<Flux>(); // La liste des flux qui servira dans les critère pour la dao
//                            for (Iterator it1 = idString.iterator(); it1.hasNext();) { // Pour chaque ID on va chercher le flux qu'on inscrit dans une liste
//                                try {
//
//                                    Object oID = it1.next();
//                                    Flux flux = null;
//                                    if (oID.getClass().equals(String.class)) {
////                                        flux = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long((String) oID));
//                                        flux = new Flux(); 
//                                        flux.setID(new Long((String)oID));
//                                    } else if (oID.getClass().equals(Long.class)) {
////                                        flux = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long((Long) oID));
//                                        flux = new Flux();
//                                        flux.setID(new Long((Long)oID));
//                                    }
//
//                                    if (flux != null) {
//                                        lfDao.add(flux);
//                                    }
//                                } catch (Exception e) {
//                                    logger.debug("Impossible de retrouver le flux ", e);
//                                }
//                            }
//
//                            if (!lfDao.isEmpty()) {
//                                SearchFilter searchFilter = new SearchFilter();                            //On configure un nouveau critère
//                                searchFilter.setData(lfDao);
//                                searchFilter.setField("listFlux");
//                                searchFilter.setOp("in");
//                                searchFilter.setType(List.class);
//                                filtersList.getFilters().add(searchFilter);
//                            }
//
//
//                        } else if (field.equals("dateRecup") && op.equals("gt")) {
//                            try {
//                                String data = (String) obj.get("data");
//                                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
//                                DateTime dt1 = fmt.parseDateTime(data).withTimeAtStartOfDay();
//                                SearchFilter searchFilter = new SearchFilter();
//                                searchFilter.setData(dt1.toDate());
//                                searchFilter.setOp("gt");
//                                searchFilter.setType(Date.class);
//                                searchFilter.setField("dateRecup");
////                                daoItem.getCriteriaSearchFilters().getFilters().add(searchFilter);
//                                filtersList.getFilters().add(searchFilter);
//                            } catch (Exception e) {
//                                logger.debug("erreur lors de l'interprétation de la data1 ", e);
//                            }
//
//                        } else if (field.equals("dateRecup") && op.equals("lt")) {
//                            try {
//                                String data = (String) obj.get("data");
//                                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
//                                DateTime dt1 = fmt.parseDateTime(data).withTimeAtStartOfDay().plusDays(1);
//                                SearchFilter searchFilter = new SearchFilter();
//                                searchFilter.setData(dt1.toDate());
//                                searchFilter.setOp("lt");
//                                searchFilter.setType(Date.class);
//                                searchFilter.setField("dateRecup");
////                                daoItem.getCriteriaSearchFilters().getFilters().add(searchFilter);
//                                filtersList.getFilters().add(searchFilter);
//                            } catch (Exception e) {
//                                logger.debug("erreur lors de l'interprétation de la date 2", e);
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    logger.debug("erreur dans de traitement d'une requete dans list", e);
//                }
//            }
//        }
//
//    }
}
