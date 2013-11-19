/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import rssagregator.dao.AbstrDao;
import rssagregator.dao.SearchFilter;
import rssagregator.dao.SearchFiltersList;
import rssagregator.servlet.JournauxSrvl;
import rssagregator.utils.ServletTool;

/**
 * Tout les formulaires doivent implémenter cette classe abstraite. Ces objets formulaires permettent de valider les
 * données envoyées par les utilisateurs et ensuite de nourrir les java beans. Les servlets doivent ainsi utiliser les
 * formulaires en commencant par valider la donnée. les données envoyée par l'utilisateur sont alors vérifiée et stockée
 * dans l'objet form. Le déclanchement de la méthode bind permet d'inscrire les données stockées dans le formulaire dans
 * le bean envoyé en argument (uniquement si le formulaire est valide).
 * <p></p>
 *
 * @author clem
 */
public abstract class AbstrForm {

    /**
     * *
     * Expression régulière permettant de matcher une url
     */
    public static final String REG_EXP_HTTP_URL = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    /**
     * *
     * Toutes les lettre les accents francais les espace et les tirret
     */
    public static final String REG_EXP_ALPHANUM_FR = "[A-Za-zéèàê -]*";
    public static final String ERR_ALPHANUM_FR = "Seul les lettres espaces et tirret sont acceptés";
    public static final String ERR_NE_PEUT_ETRE_NULL = "Ce champs doit impérativement être complété";
    public static final String ERR_URL_INCORRECTE = "La valeur saisie ne correspond pas à une url (http://site.com)";
    protected static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AbstrForm.class);
    /**
     * *
     * Ce hash map d'erreur permet de stocker des messages d'erreurs lorsque l'utilisateur cherche à inscrire une donnée
     * invalide.
     */
    protected Map<String, String[]> erreurs = new HashMap<String, String[]>();
    /**
     * *
     * Un message générique pour informer l'utilisateur à la fin du traitement exemple : "Erreur de saisie" ou "Donnée
     * valider"...
     */
    protected String resultat = "";
    /**
     * *
     * Permet de stocker le résultat de la validation. True si les données envoyées par l'utilisateur sont conformes
     * sinon false...
     */
    protected Boolean valide = false;
    /**
     * *
     * Lors du bind, ce booleean peut être passé à false. Le message d'erreur a afficher est alors stocké dans
     * this.resultat.
     */
    protected Boolean operationOk = true;
    /**
     * *
     * Lorqu'on cherche à récupérer les paramettres lors d'une action list en utilisant la méthode
     * {@link #parseListeRequete(javax.servlet.http.HttpServletRequest, rssagregator.dao.AbstrDao)} cette
     * {@link SearchFiltersList} est complétée
     */
    SearchFiltersList filtersList = new SearchFiltersList();
    /**
     * *
     * Préciser le type d'action (add ou mod). La gestion du bind peut en effet être différencier pour certain
     * traitement.
     */
    protected String action;
    /**
     * *
     * La class du beans. Le formulaire a besoin de connaitre le type du beans sur lequel il doit intéragir. Cette
     * variable est automatiquement remplie par la factory {@link FORMFactory}
     */
    protected Class beanClass;

    /**
     * *
     * Permet de préciser si l'action est
     */
//    protected Boolean addAction;
//    protected AbstrDao dao;
    /**
     * Rempli l'objet envoyé avec les donnée du formulaire envoyés
     *
     * @param request la requete ou l'on va chercher les paramettres envoyés en POST par les utilisateurs
     * @return Le bean complété. Null si on a cherché à binder un formulaire non valide
     */
    public abstract Object bind(HttpServletRequest request, Object objEntre, Class type);
    //Pendant longtemps, nous avons utilisé une méthode générique reposant sur la réflexivité pour binder et valider les formulaire. Cette démarche s'est avéré être source de bug. Chaque formulaire doit maintenant redéclarer la méthode qui est devenue abstraite
//    {
    // SI flux est null (cas d'un ajout, on crée un nouveau flux
//
//        if (objEntre == null) {
//
//            try {
//                objEntre = type.newInstance();
//            } catch (InstantiationException ex) {
//                Logger.getLogger(AbstrForm.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IllegalAccessException ex) {
//                Logger.getLogger(AbstrForm.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//        // On hydrate/peuple le beans avec les données du formulaire
//        try {
//
//            ClemBeanUtils.populate(objEntre, request, this);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
////         On lance les vérifaication
//        try {
////            erreurs = ClemBeanUtils.check(this, objEntre);
//            ClemBeanUtils.check(this, objEntre);
//        } catch (SecurityException ex) {
//            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(FluxForm.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        if (erreurs.isEmpty()) {
//            resultat = "Traitement effectué";
//            valide = true;
//
//        } else {
//            resultat = "Erreur lors de la validation des données";
//            valide = false;
//        }
//        return objEntre;
//    }

    /**
     * *
     * Permet de valider le formulaire. Cette méthode est à redéclarer dans chacun des formulaires. Chacun les
     * paramettres de la requête sont récupérés et vérifiés. Si ils sont bon le formulaire les garde dans des variable
     * qui lui sont propre. Elles seront réutilisées pour peupler le bean lors de l'usage de la méthode bind. Si il y a
     * erreur la méthode validate doit ajouter un enregitrement à la map erreur
     *
     * @return
     */
    public abstract Boolean validate(HttpServletRequest request);

    public Map<String, String[]> getErreurs() {
        return erreurs;
    }

    public void setErreurs(Map<String, String[]> erreurs) {
        this.erreurs = erreurs;
    }

    public String getResultat() {
        return resultat;
    }

    public void setResultat(String resultat) {
        this.resultat = resultat;
    }

    public Boolean getValide() {
        return valide;
    }

    public void setValide(Boolean valide) {
        this.valide = valide;
    }

    protected AbstrForm() {
    }

    public Boolean getOperationOk() {
        return operationOk;
    }

    public void setOperationOk(Boolean operationOk) {
        this.operationOk = operationOk;
    }

    /**
     * *
     * getter de {@linkplain #action}
     *
     * @return
     */
    public String getAction() {
        return action;
    }

    /**
     * *
     * setter de {@linkplain #action}
     *
     * @param action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * *
     * Cette méthode peut êter utilisée pour parser la requête et extraire les filtres de recherche. Permet de déplacer
     * un bloc de texte qui pourrait être contenu dans la servlet et de factoriser par la même occasion.
     */
    public void parseListeRequete(HttpServletRequest request, AbstrDao dao) throws Exception {
        this.recupStandartFilters(request, dao, filtersList);
    }

    /***
     * getter for {@linkplain #filtersList}
     * @return 
     */
    public SearchFiltersList getFiltersList() {
        return filtersList;
    }

    
    /***
     * setter for {@linkplain #filtersList}
     * @param filtersList 
     */
    public void setFiltersList(SearchFiltersList filtersList) {
        this.filtersList = filtersList;
    }

    /**
     * **
     * Permet de factoriser la récupération des paramètre standart à toutes les actions list pour tous les beans
     *
     * @param request : la request émmanant de la servlet
     * @param beansClass : la class du beans traité
     * @param dao : dao permettant le compte total
     * @param filters
     */
    protected void recupStandartFilters(HttpServletRequest request, AbstrDao dao, SearchFiltersList filters) {

        //Compte du nombre total de résultat
//        Integer count = null;
//        try {
//            count = dao.cptCriteria();
//            request.setAttribute("count", count);
//        } catch (Exception e) {
//            logger.debug("err count", e);
//        }


        // ROW
        if (request.getParameter("vue") != null && !request.getParameter("vue").equals("csv")) {
            Integer limit = null;
            if (request.getParameter("rows") != null && !request.getParameter("rows").isEmpty()) {
                try {
                    limit = new Integer(request.getParameter("rows"));
                    request.setAttribute("rows", limit);
                    filters.setCriteriaRow(limit);
//                    dao.setCriteriaRow(limit);
                } catch (Exception e) {
                }
            } else {
            }

            //-----PAGE
            Integer page = null;
            if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
                try {
                    page = new Integer(request.getParameter("page"));

                    request.setAttribute("page", new Integer(request.getParameter("page")));
                    Integer startRows = limit * page - limit;
//                    dao.setCriteriaStartRow(startRows);
                    filters.setCriteriaStartRow(startRows);

                } catch (Exception e) {
                    logger.debug("Erreur", e);
                }
            } else {
                request.setAttribute("page", new Integer(1));
            }
        }



        // Traitement de l'ordre 
        if (request.getParameter("sidx") != null && !request.getParameter("sidx").isEmpty()) {
            try {
                request.setAttribute("sidx", request.getParameter("sidx"));
                filters.setCriteriaSidx(request.getParameter("sidx"));
            } catch (Exception e) {
            }
        }

        if (request.getParameter("sord") != null && !request.getParameter("sord").isEmpty()) {
            filters.setCriteriaSord(request.getParameter("sord"));
            request.setAttribute("sord", request.getParameter("sord"));
        }



        //Gestion des paramettre filtre permet de configurer les where clause dans criteria en fonction de ce qui est envoyé par JQGRID
        if (request.getParameter("filters") != null && !request.getParameter("filters").isEmpty()) {
            String filter = request.getParameter("filters");
            JSONObject obj = new JSONObject();
            JSONParser parse = new JSONParser();
            try {
                JSONObject obj2 = (JSONObject) parse.parse(filter);
                JSONArray rules = (JSONArray) obj2.get("rules");
                for (int i = 0; i < rules.size(); i++) {
                    JSONObject object = (JSONObject) rules.get(i);
                    String field = (String) object.get("field");
                    String op = (String) object.get("op");
                    String data = (String) object.get("data");
                    System.out.println("field : " + field);
                    System.out.println("op : " + op);
                    System.out.println("data : " + data);

                    SearchFilter filt = new SearchFilter();


                    filt.setData(data);
                    filt.setField(field);
                    filt.setOp(op);
                    //On essai de retrouver le type du champs par reflexivité
                    try {
                        System.out.println("On tente de trouver le type");
                        filt.setType(this.beanClass.getDeclaredField(field).getType());

                    } catch (NoSuchFieldException ex) {
                        Logger.getLogger(ServletTool.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SecurityException ex) {
                        Logger.getLogger(ServletTool.class.getName()).log(Level.SEVERE, null, ex);
                    }


                    this.filtersList.getFilters().add(filt);
//                    dao.getCriteriaSearchFilters().getFilters().add(filt);
//                    System.out.println("--> nb filter : " + dao.getCriteriaSearchFilters().getFilters().size());
                }

            } catch (ParseException ex) {
                Logger.getLogger(JournauxSrvl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * *
     * Getter for {@linkplain #beanClass}
     *
     * @return
     */
    public Class getBeanClass() {
        return beanClass;
    }

    /**
     * *
     * Setter for {@linkplain #beanClass}
     *
     * @param beanClass
     */
    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }
}
