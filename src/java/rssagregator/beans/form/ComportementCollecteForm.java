/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.reflections.Reflections;
import rssagregator.beans.traitement.AbstrRaffineur;
import rssagregator.beans.traitement.ComportementCollecte;

/**
 * Le formulaire permettant de valider et binder un beans {@link ComportementCollecte}
 *
 * @author clem
 */
public class ComportementCollecteForm extends AbstrForm {

    private Integer requester_time_out;
    private Integer periodiciteCollecte;
    private List<String[]> RequestProperty = new ArrayList<String[]>();
    private Boolean DeboubTitle;
    private Boolean DeboudDesc;
    private Boolean dedouGUID;
    private Boolean dedoubLink;
    private Boolean dedoubDatePub;
    private Boolean dedoubCategory;
    private String comportement_nom;
    private String comportement_desc;
//    private Boolean defaut;
    private List<Class> raffClass = new ArrayList<Class>();

    protected ComportementCollecteForm() {
    }

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
        ComportementCollecte collecte = (ComportementCollecte) objEntre;
        if (collecte == null) {
            
            collecte = ComportementCollecte.getDefaultInstance();
        }
        
        
            //---> On ajoute tous les raffineurs possible au nouveau comportement
//            if (collecte.getRaffineur().isEmpty()) {
            Reflections reflections = new Reflections("rssagregator.beans.traitement");
            Set<Class<? extends AbstrRaffineur>> imp = reflections.getSubTypesOf(AbstrRaffineur.class);
            for (Iterator<Class<? extends AbstrRaffineur>> it = ComportementCollecte.getRequesteurClass().iterator(); it.hasNext();) {
                Class<? extends AbstrRaffineur> class1 = it.next();

                if (!collecte.possedeUnRaffineurDeType(class1)) { // <Si le comportement ne coppede pas le comportement. On le cree
                    System.out.println("JAI pas");
                    try {
                        AbstrRaffineur r = class1.newInstance();
                        r.setActif(true);
                        collecte.getRaffineur().add(r);
                    } catch (InstantiationException ex) {
                        Logger.getLogger(ComportementCollecteForm.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(ComportementCollecteForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else{
                    System.out.println("--> J'AI:");
                }
            }
        

        if (valide) {
            collecte.getRequesteur().setTimeOut(requester_time_out);
            collecte.setPeriodiciteCollecte(periodiciteCollecte);

            collecte.getRequesteur().setRequestProperty(null);
            for (int i = 0; i < RequestProperty.size(); i++) {
                String[] strings = RequestProperty.get(i);
                collecte.getRequesteur().addRequestProperty(strings[0], strings[1]);
            }

            collecte.getDedoubloneur().setDeboubTitle(DeboubTitle);
            collecte.getDedoubloneur().setDeboudDesc(DeboudDesc);
            collecte.getDedoubloneur().setDedouGUID(dedouGUID);
            collecte.getDedoubloneur().setDedoubLink(dedoubLink);
            collecte.getDedoubloneur().setDedoubDatePub(dedoubDatePub);
            collecte.getDedoubloneur().setDedoubCategory(dedoubCategory);

            collecte.setNom(comportement_nom);
            collecte.setDescription(comportement_desc);
//            collecte.setDefaut(defaut);


//            Fixation de la valeur actif innactif des raffineur en conction de la selection utilisateur.
            for (Iterator<AbstrRaffineur> it = collecte.getRaffineur().iterator(); it.hasNext();) {
                AbstrRaffineur abstrRaffineur = it.next();
                abstrRaffineur.setActif(false);
                for (int k = 0; k < raffClass.size(); k++) {
                    Class class2 = raffClass.get(k);
                    if (class2.equals(abstrRaffineur.getClass())) {
                        abstrRaffineur.setActif(true);
                        break;
                    }
                }
            }


//            for (int i = 0; i < raffClass.size(); i++) {
//
//                Class class1 = raffClass.get(i);
//                for (Iterator<AbstrRaffineur> it = collecte.getRaffineur().iterator(); it.hasNext();) {
//                    AbstrRaffineur abstrRaffineur = it.next();
//                    boolean trouve = false;
////                }
////                for (int j = 0; j < collecte.getRaffineur().size(); j++) {
////                    AbstrRaffineur abstrRaffineur = collecte.getRaffineur().get(j);
//                    for (int k = 0; k < raffClass.size(); k++) {
//                        Class class2 = raffClass.get(k);
//                        if (class2.equals(class1)) {
//                            trouve = true;
//                        }
//                    }
//
//                    if (!trouve) {
//                        it.remove();
//
//                    }
//
//
//                }
//
//
//
//            }



        }
        return collecte;
    }

    @Override
    public Boolean validate(HttpServletRequest request) {
        String s;
        erreurs = new HashMap<String, String[]>();

        //-----------------------------------------------------------------------------
        //...................RECUPERATION ET VALIDATION DES DONNEES
        //-----------------------------------------------------------------------------

        //-----------> TIME OUT
        s = request.getParameter("requester_time_out");
        if (s != null && !s.isEmpty()) {
            try {
                requester_time_out = new Integer(s);
            } catch (Exception e) {
                erreurs.put("requester_time_out", new String[]{"N'est pas un numéric", "N'est pas un chiffre"});
            }
        } else {
            erreurs.put("requester_time_out", new String[]{"Ne peut être nul", ""});
        }


        //-----------> PERIODICITE DE COLLECTE 
        s = request.getParameter("periodiciteCollecte");
        if (s != null && !s.isEmpty()) {
            try {
                periodiciteCollecte = new Integer(s);
            } catch (Exception e) {
                erreurs.put("periodiciteCollecte", new String[]{"N'est pas un numéric", ""});
            }
        } else {
            erreurs.put("periodiciteCollecte", new String[]{"Ne peut être nul", ""});
        }

        String[] cle = request.getParameterValues("requestPropertyCle");
        String[] valeur = request.getParameterValues("requestPropertyValue");
        int i;

        if (cle != null && valeur != null && cle.length == valeur.length) {
            for (i = 0; i < cle.length; i++) {
                if (!cle[i].trim().isEmpty() && !valeur[i].trim().isEmpty()) {
                    RequestProperty.add(new String[]{cle[i], valeur[i]});
                }
            }
        }

        //----------> DEDOUBLONNAGE TITE
        String dedoub_titre = request.getParameter("dedoub_titre");
        if (dedoub_titre == null || dedoub_titre.isEmpty()) {
            DeboubTitle = false;
        } else {
            DeboubTitle = true;
        }

        //---------> DEDOUBLONNAGE DESCRIPTION
        s = request.getParameter("dedoub_description");
        if (s == null || s.isEmpty()) {
            DeboudDesc = false;
        } else {
            DeboudDesc = true;
        }

        //----------> DEDOUBLONNAGE GUUID
        s = request.getParameter("dedouGUID");
        if (s == null || s.isEmpty()) {
            dedouGUID = false;
        } else {
            dedouGUID = true;
        }

        //-------> DEDOUBLONNAGE LINK
        s = request.getParameter("dedoubLink");
        if (s == null || s.isEmpty()) {
            dedoubLink = false;
        } else {
            dedoubLink = true;
        }

        //-------> DEDOUBLONNAGE DATE PUBLICATION
        //Rappel : on ne permet pas le dédoublonnage sur la date de récup car cette date varie par définition a chaque récupération...
        s = request.getParameter("dedoubDatePub");
        if (s == null || s.isEmpty()) {
            dedoubDatePub = false;
        } else {
            dedoubDatePub = true;
        }

        //-------> DEDOUBLONNAGE CATEGORIE
        s = request.getParameter("dedoubCategory");
        if (s == null || s.isEmpty()) {
            dedoubCategory = false;
        } else {
            dedoubCategory = true;
        }

        // Les paramettre propre au mediateur Comportement collect
        //------------> NOM DU COMPORTEMENT
        s = request.getParameter("comportement_nom");
        if (s != null && !s.isEmpty()) {
            comportement_nom = s;
        } else {
            erreurs.put("comportement_nom", new String[]{"ne peut être null", "ne peu"});
        }

        //----------> DESCRIPTION DU COMPORTEMENT
        s = request.getParameter("comportement_desc");
        if (s != null) {
            comportement_desc = s;
        }

//        //----------> COMPORTEMNENT PAR DEFAUT
//        s = request.getParameter("defaut");
//        if (s == null || s.isEmpty()) {
//            defaut = false;
//        } else {
//            defaut = true;
//        }

        //----> Raffineur
        System.out.println("=================");
        String raffString[] = request.getParameterValues("raffineur");

        if (raffString != null && raffString.length > 0) {
            for (int j = 0; j < raffString.length; j++) {
                String string = raffString[j];
                System.out.println("COCHE " + string);
                try {
//                                  File root = new File(".");
//                    URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});

                    Class cc = Class.forName(string);
                    this.raffClass.add(cc);

                    System.out.println("---> on a la class : " + cc);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ComportementCollecteForm.class.getName()).log(Level.SEVERE, null, ex);
                }
//                catch (MalformedURLException ex) {
//                    Logger.getLogger(ComportementCollecteForm.class.getName()).log(Level.SEVERE, null, ex);
//                }


            }
        }
        System.out.println("=================");




//        s = request.getParameter("secondDedoub");
//        if (s == null || s.isEmpty()) {
//            secondDedoub = false;
//        } else {
//            secondDedoub = true;
//        }

        //---------------------------------------------------------------
        //---------------------------------------------------------------
        if (erreurs.isEmpty()) {
            this.setValide(true);
        } else {
            this.setValide(false);
        }
        return valide;
    }

    public static void main(String[] args) {
        try {
            Class c = Class.forName("rssagregator.beans.traitement.RaffineurSimpleImplementation");
            System.out.println("Class " + c);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ComportementCollecteForm.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
