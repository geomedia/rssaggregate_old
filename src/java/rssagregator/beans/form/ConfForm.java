/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.Conf;
import rssagregator.beans.ServeurSlave;

/**
 * La class permettant de vérifier et binder les données saisie par l'utilisateur avec le beans <strong>Conf</strong
 * @aut
 *
 *
 * hor clem
 */
public class ConfForm extends AbstrForm {
    //-------------------------------------
    // Les variable récupérée dans la requête

    private String servname;
    private String jmsprovider;
    private Boolean master;
    private List<ServeurSlave> serveurSlave;
    private String hostMaster;
    private Integer purgeDuration;
    //-------------------------------------

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
        erreurs = new HashMap<String, String[]>();
        Conf conf = (Conf) objEntre;
        if (objEntre == null) {
            conf = new Conf();
        }
        String s;
        s = request.getParameter("active");
        if (s != null && !s.isEmpty()) {
            conf.setActive(true);
        } else {
            conf.setActive(false);
        }
        
        if (valide) {
            conf.setServname(servname);
            conf.setJmsprovider(jmsprovider);
            conf.setMaster(master);
            conf.setServeurSlave(serveurSlave);
            conf.setHostMaster(hostMaster);
            conf.setPurgeDuration(purgeDuration);
        }
        return objEntre;
    }
    
    public void check_nbThreadRecup(String entre) throws Exception {
        try {
            Integer i = Integer.parseInt(entre);
        } catch (Exception e) {
            throw new Exception("Ceci n'est pas un nombre entier");
        }
    }
    
    @Override
    public Boolean validate(HttpServletRequest request) {
        //=====================================================================================
        // CAPTURE DE LA SAISIE DE CHAQUE ELEMENT DANS LA REQUEST ET VÉRIFICATION DE CEUX CI
        //=====================================================================================
        String s;
        //----------------------->Nom de serveur
        s = request.getParameter("servname");
        if (s != null && !s.isEmpty()) {
            Pattern p = Pattern.compile("^[a-z]*$");
            Matcher m = p.matcher(s);
            if (!m.find()) {
                erreurs.put("servname", new String[]{s, "doit être composé uniquement de minuscules"});
            } else {
                this.servname = s;
            }
        }
        else{
            erreurs.put("servname", new String[]{ERR_NE_PEUT_ETRE_NULL, ERR_NE_PEUT_ETRE_NULL});
        }
        // Vérification doit être uniquement composé de caractère

        //------------------->JMS Provider
        s = request.getParameter("jmsprovider");
        if (s != null && !s.isEmpty()) {
            if (s.matches("^(https?|tcp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
                
                this.jmsprovider = s;
            }
            else{
                erreurs.put("jmsprovider", new String[]{"URL incorrect", "ne peu"});
            }
        }


        //------------------>Master statut
        s = request.getParameter("master");
        if (s == null) {
            this.master = false;
        } else {
            this.master = true;
        }

        // --------------->Serveur esaclaves lié à la config
        String[] tabslavehost = request.getParameterValues("hostslave");
        String[] tabslavelogin = request.getParameterValues("loginSlave");
        String[] tabslavepass = request.getParameterValues("passSlave");
        String[] tabUrlSlave = request.getParameterValues("urlSlave");
        int i;
        this.serveurSlave = new ArrayList<ServeurSlave>();
        if (tabslavehost != null && tabslavelogin != null && tabslavepass != null && tabUrlSlave != null) {
            if (tabslavehost.length == tabslavelogin.length && tabslavepass.length == tabslavehost.length && tabslavehost.length == tabUrlSlave.length) {
                for (i = 0; i < tabslavehost.length; i++) {
                    ServeurSlave slave = new ServeurSlave();
                    slave.setServHost(tabslavehost[i]);
                    slave.setLogin(tabslavelogin[i]);
                    slave.setPass(tabslavepass[i]);
                    slave.setUrl(tabUrlSlave[i]);
                    this.serveurSlave.add(slave);
                }
            }
        }

        // ------------------> Host du serveur maitre
        s = request.getParameter("hostMaster");
        if (s != null) {
            this.hostMaster = s;
        }

        //------------------> DureePurge
        s = request.getParameter("purgeDuration");
        if (s != null) {
            try {
                Integer val = new Integer(s);
                this.purgeDuration = val;
            } catch (Exception e) {
                erreurs.put("purgeDuration", new String[]{s, "Il faut un nombre entier"});
            }
        }
        //-------------------------------------------------------------------------------------
        if (erreurs.isEmpty()) {
            resultat = "Traitement effectué";
            this.valide = true;
        } else {
            resultat = "Erreur lors de la validation des données";
            this.valide = false;
        }
        return this.valide;
    }
}
