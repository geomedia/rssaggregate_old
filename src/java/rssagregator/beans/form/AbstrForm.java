/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * Tout les formulaires doivent implémenter cette classe abstraite. Ces objets
 * formulaires permettent de valider les données envoyées par les utilisateurs
 * et ensuite de nourrir les java beans. Les servlets doivent ainsi utiliser les
 * formulaires en commencant par valider la donnée. les données envoyée par
 * l'utilisateur sont alors vérifiée et stockée dans l'objet form. Le
 * déclanchement de la méthode bind permet d'inscrire les données stockées dans
 * le formulaire dans le bean envoyé en argument (uniquement si le formulaire
 * est valide).
 * <p></p>
 *
 * @author clem
 */
public abstract class AbstrForm {

    /***
     * Expression régulière permettant de matcher une url
     */
    public static final String REG_EXP_HTTP_URL = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    
    /***
     * Toutes les lettre les accents francais les espace et les tirret
     */
    public static final String REG_EXP_ALPHANUM_FR = "[A-Za-zéèàê -]*";
    public static final String ERR_ALPHANUM_FR= "Seul les lettres espaces et tirret sont acceptés";
    public static final String ERR_NE_PEUT_ETRE_NULL= "Ce champs doit impérativement être complété";
    public static final String ERR_URL_INCORRECTE= "La valeur saisie ne correspond pas à une url (http://site.com)";
    
    
    
    
    /**
     * *
     * Ce hash map d'erreur permet de stocker des messages d'erreurs lorsque
     * l'utilisateur cherche à inscrire une donnée invalide.
     */
    protected Map<String, String[]> erreurs = new HashMap<String, String[]>();
    /**
     * * 
     * Un message générique pour informer l'utilisateur à la fin du traitement
     * exemple : "Erreur de saisie" ou "Donnée valider"...
     */
    protected String resultat = "";
    /**
     * *
     * Permet de stocker le résultat de la validation. True si les données
     * envoyées par l'utilisateur sont conformes sinon false...
     */
    protected Boolean valide = false;
    
    /***
     * Lors du bind, ce booleean peut être passé à false. Le message d'erreur a afficher est alors stocké dans this.resultat.
     */
    protected Boolean operationOk = true;
    
    
    /**
     * *
     * Préciser le type d'action (add ou mod). La gestion du bind peut en effet
     * être différencier pour certain traitement.
     */
    protected String action;

    /**
     * *
     * Permet de préciser si l'action est
     */
//    protected Boolean addAction;
//    protected AbstrDao dao;
    /**
     * Rempli l'objet envoyé avec les donnée du formulaire envoyés
     *
     * @param request la requete ou l'on va chercher les paramettres envoyés en
     * POST par les utilisateurs
     * @return Le bean complété. Null si on a cherché à binder un formulaire non
     * valide
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
     * Permet de valider le formulaire. Cette méthode est à redéclarer dans
     * chacun des formulaires. Chacun les paramettres de la requête sont
     * récupérés et vérifiés. Si ils sont bon le formulaire les garde dans des
     * variable qui lui sont propre. Elles seront réutilisées pour peupler le
     * bean lors de l'usage de la méthode bind. Si il y a erreur la méthode
     * validate doit ajouter un enregitrement à la map erreur
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

    public AbstrForm() {
    }

    public Boolean getOperationOk() {
        return operationOk;
    }

    public void setOperationOk(Boolean operationOk) {
        this.operationOk = operationOk;
    }

    
    
    
    /**
     * *
     * Permet de préciser au formulaire le type d'action (add ou mod)
     *
     * @return
     */
    public String getAction() {
        return action;
    }

    /**
     * *
     * Permet de préciser au formulaire le type d'action (add ou mod)
     *
     * @param action
     */
    public void setAction(String action) {
        this.action = action;
    }
}
