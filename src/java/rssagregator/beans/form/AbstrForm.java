/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author clem
 */
public abstract class AbstrForm {

    protected Map<String, String[]> erreurs = new HashMap<String, String[]>();
    protected String resultat = "";
    protected Boolean valide = false;
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
     * @param request
     * @return
     */
    public abstract Object bind(HttpServletRequest request, Object objEntre, Class type); 
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
     * chacun des formulaire. Pour chacun les paramettres de la requête doivent
     * être récupéré et vérifié. Si il sont bon le formulaire les garde dans des
     * variable qui lui sont propre. Elles seront réutilisé lors pour peupler le
     * bean lors de l'usage de la méthode bind. Si il y a erreur la méthode
     * validate doit ajouter un enregitrement à la map erreur
     *
     * @return
     */
    public abstract Boolean validate(HttpServletRequest request);

    /**
     * *
     * N'est surement plus utilisé
     *
     * @param request
     * @param nomChamp
     * @return
     * @deprecated
     */
    @Deprecated
    private static String getValeurChamp(HttpServletRequest request, String nomChamp) {
        String valeur = request.getParameter(nomChamp);
        if (valeur == null || valeur.trim().length() == 0) {
            return null;
        } else {
            return valeur.trim();
        }
    }

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

    //    public AbstrDao getDao() {
    //        return dao;
    //    }
    //
    //    public void setDao(AbstrDao dao) {
    //        this.dao = dao;
    //    }
    /**
     * *
     * Permet de préciser au formulaire d'un s'agit d'une création d'entité. Il
     * devra ainsi instancier un nouvel objet du type envoyé et parfois
     * initialiser des varibales complémentaire (exemple, date de création)
     *
     * @return
     */
//    public Boolean getAddAction() {
//        return addAction;
//    }
//
//    public void setAddAction(Boolean addAction) {
//        this.addAction = addAction;
//    }
    public AbstrForm(/*AbstrDao dao*/) {
//        this.dao = dao;
//        addAction = false;
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
