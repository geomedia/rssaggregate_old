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
 * Class permettant de valider et binder les données issues de requêtes dans un
 * bean <strong>UserAccount</strong>
 *
 * @author clem
 */
public class UserForm extends AbstrForm {

    //-----------------------------
    // Les variables devant être observées
    private String mail;
    private String encPassword;
    Boolean adminstatut;
    private String username;
    private Boolean adminMail;
    //-----------------------------
    // Expression régulière permettant de valider un email.
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UserForm.class);

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
        UserAccount u = (UserAccount) objEntre;
        // Instanciation d'un utilisateur si les donnée envoyée en argument sont null
        if (u == null) {
            u = new UserAccount();
        }
        //================================================================================================
        //                              BIND DES PARAMETRE DE LA REQUETE
        //================================================================================================
        if (valide) {
            u.setMail(mail);

            //PASSWORD : Il est nécessaire de le crypter avant de l'enregistrer
            try {
                u.setEncPassword(encPassword);
            } catch (NoSuchAlgorithmException ex) {
                logger.error("Erreur lors du chiffrement du mot de pass.");
                Logger.getLogger(UserForm.class.getName()).log(Level.SEVERE, null, ex);
            }

            u.setAdminstatut(adminstatut);
            u.setUsername(username);
            u.setAdminMail(adminMail);

            return u;
        } else {
            return null;
        }
    }

    @Override
    public Boolean validate(HttpServletRequest request) {
        erreurs = new HashMap<String, String[]>();
        //-----------------------------------------MAIL---------------------------------------------------
        String s;
        s = request.getParameter("mail");
        if (s != null && !s.isEmpty()) {
            if (s.matches(EMAIL_PATTERN)) {
                mail = s;
            } else {
                erreurs.put("mail", new String[]{s, "l'email n'est pas correct"});
            }
        } else {
            erreurs.put("mail", new String[]{s, "Ce champ ne peut être vide"});
        }

        //----------------------------------------Mot de pass--------------------------------------------
        String p1 = request.getParameter("pass1");
        String p2 = request.getParameter("pass2");
        if (p1 != null && p2 != null) {
            boolean err = false;
            if (!p1.equals(p2)) {
                err = true;
                erreurs.put("pass", new String[]{p1, "mot de pass null"});
            }
            if (p1.length() < 3) {
                erreurs.put("pass", new String[]{p1, "trop court"});
                err = true;
            }
            if (!err) {
                encPassword = p1;
            }
        }

        //--------------------------------ADMIN STATUT---------------------------------------------------
        s = request.getParameter("adminstatut");
        if (s != null && !s.isEmpty()) {
            adminstatut = true;
        } else {
            adminstatut = false;
        }

        //------------------------------USER NAME----------------------------------
        s = request.getParameter("username");
        if (s != null && !s.isEmpty()) {
            if (!s.matches("[A-Za-zéèàê -]*")) {
//           if(!s.matches("[\\w]*")){
                erreurs.put("username", new String[]{s, "Présence de caractères interdits, n'utilisez que des lettres de a à z des espaces et des tirrets"});
            } else {
                username = s;
            }
        } else {
            erreurs.put("username", new String[]{s, "Ce champs ne peut être nul"});
        }


        //------------------------------Mail Administration------------------------------
        s = request.getParameter("adminMail");
        if (s != null && !s.isEmpty()) {
            if (!adminstatut) {
                erreurs.put("adminMail", new String[]{s, "Il est impossible de recevoir les mail sans être administrateur"});
            } else {
                adminMail = true;
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
        return valide;
    }
}
