/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.POJOCompteItem;
import rssagregator.beans.form.ItemForm;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;
import rssagregator.dao.SearchFilter;
import rssagregator.utils.ServletTool;

/**
 * La servlet permettant de gérer l'acces des utilisateurs aux items. Elle est aussi utilisée dans le processus de
 * synchronisation. Cette servlet doit gérer les types d'action suivant :  
 * <ul>
 * <li><strong>read : </strong>l'utilisateur a demandé a lire les informations détaillées d'une items. </li>
 * <li><strong>rechercher : </strong> Permet de charger la page html permettant d'interroger les données items contenues
 * dans la base de données</li>
 * <li><strong>list : </strong>utilisé par l'interface ajax pour interroger la base de donnée et renvoyé des
 * informations sur les flux au format Json</li>
 * <li><strong>xmlsync :</strong> utilisée par le serveur maitre pour récupérer des données items sur le serveur
 * esclave. Les données sont envoyées au format XML</li>
 * </ul>
 *
 *
 * @author clem
 */
@WebServlet(name = "Item", urlPatterns = {"/item/*"})
public class ItemSrvl extends HttpServlet {

    public String VUE = null;
    public static final String ATT_ITEM = "item";
    public static final String ATT_SERV_NAME = "item";
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ItemSrvl.class);

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");


        String action = ServletTool.configAction(request, "recherche");

        //récupération de la vue
        String vue = request.getParameter("vue");
        if (vue == null) {
            vue = "html";
        }

        //DAO
        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        daoItem.initcriteria();

        ItemForm form = new ItemForm();

        Item item = null;


        System.out.println("ACTION : " + action);
        request.setAttribute("navmenu", "item");
        request.setAttribute("srlvtname", ATT_SERV_NAME);
        /**
         * **=================================================================================
         * ...................................ACTION READ
         *///*=================================================================================
        // L'action read permet de lire les informations détaillées sur une item. Elle a besoin de gérer les paramettres suivant :
        //          - id : Il s'agit de l'id de l'item à lire
        if (action.equals("read")) {
            String idString = request.getParameter("id");
            if (idString != null && !idString.isEmpty()) {
                Long id = new Long(idString);
                request.setAttribute("id", id);
                item = (Item) daoItem.find(id);
            }
            request.setAttribute(ATT_ITEM, item);
        }

        /**
         * *=================================================================================
         * ....................................ACTION LIST :
         *///=================================================================================
        //Il s'agit de l'action demandant en AJAX des informations sur les items. Elles seront renvoyées en JSON. 
        if (action.equals("list")) {

            /**
             * Entrée des parametres pour compléter les vues
             */
            // On récupère le premier et dernier résult pour former des limites de requêtes.
            Integer nbrItemPrPage;
            try {
                nbrItemPrPage = new Integer(request.getParameter("itPrPage"));
            } catch (Exception e) {
                nbrItemPrPage = 20;
            }
            request.setAttribute("itPrPage", nbrItemPrPage);
            daoItem.setMaxResult(nbrItemPrPage);

            //Récupération du firs result
            Integer firsResult;
            try {
                firsResult = new Integer(request.getParameter("firstResult"));
                daoItem.setFistResult(firsResult);
                request.setAttribute("firstResult", firsResult);
                System.out.println("FIRST result  : " + firsResult);

            } catch (Exception e) {
                firsResult = 0;
                System.out.println("ERR first");
            }
            request.setAttribute("firsResult", firsResult);
            daoItem.setFistResult(firsResult);



            // SI on doit restreindre la sélection à un flux 
            try {
                String[] tabIdFluxString = request.getParameterValues("fluxSelection2");
                List<Flux> listFluxEntites = new ArrayList<Flux>();

                int i;
                for (i = 0; i < tabIdFluxString.length; i++) {
                    Flux f = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(tabIdFluxString[i]));
                    listFluxEntites.add(f);
                }
                daoItem.setWhere_clause_Flux(listFluxEntites);

            } catch (Exception e) {
                System.out.println("ERRRRRRRR" + e);
                daoItem.setWhere_clause_Flux(null);
            }

            //Selection de l'ordre
            try {
                String s = request.getParameter("order");
                String desc = request.getParameter("desc");
                if (!s.isEmpty()) {
                    if (s.equals("dateRecup") || s.equals("datePub") || s.equals("listFlux")) {
                        daoItem.setOrder_by(s);
                        if (desc.equals("true")) {
                            daoItem.setOrder_desc(Boolean.TRUE);
                        }
                    }
                }
            } catch (Exception e) {
            }

            // Récupération des date limites
            try {
                String d1 = request.getParameter("date1");
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
                DateTime dateTime = fmt.parseDateTime(d1);
                daoItem.setDate1(dateTime.toDate());
            } catch (Exception e) {
            }


            try {
                String d2 = request.getParameter("date2");
                DateTimeFormatter fmt2 = DateTimeFormat.forPattern("dd/MM/yyyy");
                DateTime dateTime2 = fmt2.parseDateTime(d2);
                daoItem.setDate2(dateTime2.toDate());
            } catch (Exception e) {
            }


            // Critère Sync Statut
            try {
                daoItem.setSynchStatut(new Integer(request.getParameter("syncStatut")));
            } catch (Exception e) {
            }

            //On récupère le nombre max d'item
            Integer nbItem = daoItem.findNbMax();
            request.setAttribute("nbitem", nbItem);


            //En fonction de la sélection demander on formule la bonne recherche
            List<Item> listItem;

            // si la vue est csv il faut enlever les limites
            if (!vue.equals("html") ^ vue.equals("jsondesc")) { // en java le ^ est un XOR
                daoItem.setFistResult(null);
                daoItem.setMaxResult(null);
            }

            listItem = daoItem.findCretaria();
            request.setAttribute("listItem", listItem);
        }
        //--------------------------------------------------------------------------------------------------------------
        //.........................LIST GRID
        //--------------------------------------------------------------------------------------------------------------
        // Va être fusionné avec list
        if (action.equals("listgrid")) {

            //Il faut récupérer les champs spéciaux de recherche (flux lié, date comme critère limitant...)
            if (request.getParameter("filters") != null && !request.getParameter("filters").isEmpty()) {
                String filter = request.getParameter("filters");
                JSONParser parse = new JSONParser();
                try {
                    JSONObject obj2 = (JSONObject) parse.parse(filter);
                    JSONArray rules = (JSONArray) obj2.get("spefield");
                    for (int i = 0; i < rules.size(); i++) {
                        JSONObject obj = (JSONObject) rules.get(i);
                        String field = (String) obj.get("field");
                        if (field.equals("idFlux")) {
                            JSONArray idString = (JSONArray) obj.get("data"); // C'est un tableau d'id en JSON
                            List<Flux> lfDao = new ArrayList<Flux>(); // La liste des flux qui servira dans les critère pour la dao
                            for (Iterator it1 = idString.iterator(); it1.hasNext();) { // Pour chaque ID on va chercher le flux qu'on inscrit dans une liste
                                try {
                                    String idStr = (String) it1.next();
                                    Flux flux = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(idStr));
                                    lfDao.add(flux);
                                } catch (Exception e) {
                                    logger.debug("Impossible de retrouver le flux ", e);
                                }
                            }

                            SearchFilter searchFilter = new SearchFilter();                            //On configure un nouveau critère
                            searchFilter.setData(lfDao);
                            searchFilter.setField("listFlux");
                            searchFilter.setOp("in");
                            searchFilter.setType(List.class);
                            daoItem.getCriteriaSearchFilters().getFilters().add(searchFilter);

                        } else if (field.equals("date1")) {
                            try {
                                String data = (String) obj.get("data");
                                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
                                DateTime dt1 = fmt.parseDateTime(data).withTimeAtStartOfDay();
                                SearchFilter searchFilter = new SearchFilter();
                                searchFilter.setData(dt1.toDate());
                                searchFilter.setOp("gt");
                                searchFilter.setType(Date.class);
                                searchFilter.setField("datePub");
                                daoItem.getCriteriaSearchFilters().getFilters().add(searchFilter);
                            } catch (Exception e) {
                                logger.debug("erreur lors de l'interprétation de la data1 ", e);
                            }

                        } else if (field.equals("date2")) {
                            try {
                                String data = (String) obj.get("data");
                                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
                                DateTime dt1 = fmt.parseDateTime(data).withTimeAtStartOfDay().plusDays(1);
                                SearchFilter searchFilter = new SearchFilter();
                                searchFilter.setData(dt1.toDate());
                                searchFilter.setOp("lt");
                                searchFilter.setType(Date.class);
                                searchFilter.setField("datePub");
                                daoItem.getCriteriaSearchFilters().getFilters().add(searchFilter);
                            } catch (Exception e) {
                                logger.debug("erreur lors de l'interprétation de la date 2", e);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.debug("erreur dans de traitement d'une requete dans list", e);
                }
            }


            ServletTool.actionLIST(request, Item.class, ATT_ITEM, DAOFactory.getInstance().getDaoItem());
        }

        /**
         * *=================================================================================
         * .............................ACTION RECHERCHER
         *///================================================================================
        // Action recherche, correspond à la demande de la page permettant de lister les flux. Il est nécessaire de fournir les paramettres permettant de construire les menus déroulant dans la JSP
        if (action.equals("recherche")) {
            // On récupère la liste des flux utile à la génération du menu déroulant
            request.setAttribute("listflux", DAOFactory.getInstance().getDAOFlux().findAllFlux(false));

            //List des journaux
            request.setAttribute("listJournaux", DAOFactory.getInstance().getDaoJournal().findall());
        }

        /**
         * *=============================================================================
         * ...........................ACTION COMPTE
         *///=============================================================================
        if (action.equals("comptejour")) {

            // On récupère les flux
            String[] fluxtab = request.getParameterValues("fluxSelection2");
            List<POJOCompteItem> listeCompte = new ArrayList<POJOCompteItem>();
            if (fluxtab != null) {

                for (int i = 0; i < fluxtab.length; i++) {
                    String string = fluxtab[i];
                    Flux f = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(string));
                    List<Flux> listF = new ArrayList<Flux>();
                    listF.add(f);

                    //Mise en forme de la requete
                    Date date1 = null;
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
                    try {
                        date1 = fmt.parseDateTime(request.getParameter("date1")).toDate();

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ERR date " + e);
                    }

                    Date date2 = null;
                    try {
                        date2 = fmt.parseDateTime(request.getParameter("date2")).toDate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    daoItem.initcriteria();
                    daoItem.setDate1(date1);
                    daoItem.setDate2(date2);
                    daoItem.setWhere_clause_Flux(listF);
                    List<Item> itDuflux = daoItem.findCretaria();

                    POJOCompteItem compteItem = new POJOCompteItem();
                    compteItem.setFlux(f);
                    compteItem.setItems(itDuflux);
                    compteItem.setDate1(date1);
                    compteItem.setDate2(date2);
                    compteItem.compte();
                    listeCompte.add(compteItem);

                }
            }

            request.setAttribute("compte", listeCompte);

        }





        /**
         * *=================================================================================
         * .............................ACTION XML SYNC
         *///================================================================================
        // Il s'agit de l'action permettant au serveur maitre de récupérer des items sur le serveur esclave. Il effectue une requete en POST avec pour attribut : 
        //      - idflux : le flux pour lequel il veut des information
        //      - hash  : une longue chaine de caractère comprenant les hashs des items qu'il a pu capturer pour le flux en question. La servlet doit renvoyer les items ne possédant pas ces hash
        //      - date1 : critère de date pour la synchronisation. Ce critère s'applique sur la date de récupération
        //      - date2 : critère de date de fin
        if (action.equals("xmlsync")) {
            // Récupération des critères expliqué plus haut. On a déjà l'id
            String hash = request.getParameter("hash");
            String date1 = request.getParameter("date1");
            String date2 = request.getParameter("date2");
            String idflux = request.getParameter("idflux");


            Flux flux = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(idflux));
            List<Flux> lFl = new ArrayList<Flux>();
            lFl.add(flux);

            // Configuration de la dao pour obtention des items concerné
            daoItem.setHashNotIn(hash);
            daoItem.setWhere_clause_Flux(lFl);

            List<Item> listItems = daoItem.findCretaria();
            request.setAttribute("listItem", listItems);

        }



        /**
         * *=================================================================================
         * ...............................GESTION DE LA VUE
         *///=================================================================================
        //utilisation de la vue en fonction des paramettres envoyé par l'utilisateur.
        if (vue.equals("html")) {
            VUE = "/WEB-INF/itemHTML.jsp";
        }
        if (vue.equals("csv")) {
            response.setHeader("Content-Disposition", "attachment; filename = items-export.csv");
            VUE = "/WEB-INF/itemCSV.jsp";
        } else if (vue.equals("csvexpert")) {
            response.setHeader("Content-Disposition", "attachment; filename = items-export.csv");
            VUE = "/WEB-INF/itemexpertCSV.jsp";
        } else if (vue.equals("jsondesc")) {
            System.out.println("ZOUZou");
            VUE = "/WEB-INF/itemJSONDesc.jsp";
        } else if (vue.equals("xls")) {
            response.setHeader("Content-Disposition", "attachment; filename = itemss-export.xls");
            VUE = "/WEB-INF/itemXLS.jsp";
        } else if (vue.equals("xmlsync")) {
            System.out.println("OUIIIII");
            VUE = "/WEB-INF/itemXMLsync.jsp";
        } else if (vue.equals("hightchart")) {
            VUE = "/WEB-INF/itemHighchart.jsp";
        } else if (vue.equals("grid")) {
            VUE = "/WEB-INF/itemJSONGrid.jsp";
        }

        this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
    }

    public static String getParam(String param, HttpServletRequest request) {

        String s = request.getParameter(param);
        if (s != null && !s.isEmpty()) {
            System.out.println("laa");
            return s;

        }
        return "";
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
