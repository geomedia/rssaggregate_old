/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.UserAccount;

/**
 *
 * @author clem
 */
public class UserForm extends AbstrForm {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
        UserAccount u = (UserAccount) objEntre;
        if (u == null) {
            u = new UserAccount();
        }
        erreurs = new HashMap<String, String[]>();

        //================================================================================================
        //                              BIND DES PARAMETRE DE LA REQUETE
        //================================================================================================
        String s;

        //-----------------------------------------MAIL---------------------------------------------------
        s = request.getParameter("mail");
        if (s != null && !s.isEmpty()) {
            if (s.matches(EMAIL_PATTERN)) {
                u.setMail(s);
            } else {
                erreurs.put("mail", new String[]{s, "l'email n'est pas correct"});
            }
        }
        else{
            erreurs.put("mail", new String[]{s, "Ce champ ne peut être vide"});
        }


        //----------------------------------------Mot de pass--------------------------------------------
       String p1 = request.getParameter("pass1");
       String p2 = request.getParameter("pass2");
       if(p1 != null && p2!=null){
           boolean err = false;
           if(!p1.equals(p2)){
               err = true;
               erreurs.put("pass", new String[]{p1, "mot de pass null"});
           }
           if(p1.length()<3){
               erreurs.put("pass", new String[]{p1, "trop court"});
               err = true;
           }
           if(!err){
               try {
                   u.setEncPassword(p1);
               } catch (NoSuchAlgorithmException ex) {
                   Logger.getLogger(UserForm.class.getName()).log(Level.SEVERE, null, ex);
                   erreurs.put("pass", new String[]{p1, "Erreur technique lors du chiffrement du mot de passe. Veuillez contacter un administrateur"});
                   
               }
            
           }
       }
       
       //--------------------------------ADMIN STATUT---------------------------------------------------
       s = request.getParameter("adminstatut");
       if(s!=null && !s.isEmpty()){
           u.setAdminstatut(true);
       }
       else{
           u.setAdminstatut(false);
       }
       
       
       //------------------------------USER NAME----------------------------------
       s= request.getParameter("username");
       if(s != null && !s.isEmpty()){
           if(!s.matches("[A-Za-zéèàê -]*")){
//           if(!s.matches("[\\w]*")){
                erreurs.put("username", new String[]{s, "Présence de caractères interdits, n'utilisez que des lettres de a à z des espaces et des tirrets"});
           }
           else{
             u.setUsername(s);
           }
       }
       else{
           erreurs.put("username", new String[]{s, "Ce champs ne peut être nul"});
       }
       
       
       //------------------------------Mail Administration------------------------------
       s = request.getParameter("adminMail");
       if(s != null && !s.isEmpty()){
           if(!u.getAdminstatut()){
               erreurs.put("adminMail", new String[]{s, "Il est impossible de recevoir les mail sans être administrateur"});
           }
           else{
               u.setAdminMail(true);
           }
       }
       
       
       
        //================================================================================================
        //                                      VALIDATION
        //================================================================================================
        if (erreurs.isEmpty()) {
            resultat = "Traitement effectué";
            valide = true;

        } else {
            resultat = "Erreur lors de la validation des données";
            valide = false;
        }

//        if (erreurs.isEmpty()) {
//            this.valide = true;
//        } else {
//            this.valide = false;
//        }
        return u;
    }
}
