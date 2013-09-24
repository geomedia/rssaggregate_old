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
 * La class permettant de vérifier et binder les données saisie par
 * l'utilisateur avec le beans <strong>Conf</strong @aut
 *
 *
 * hor clem
 */
public class ConfForm extends AbstrForm {

    private Integer nbThreadRecup;
    private String servname;
    private String login;
    private String pass;
    private String jmsprovider;
    private Boolean master;
    private List<ServeurSlave> serveurSlave;
    private String hostMaster;
    private Integer purgeDuration;
    private String jourSync;
    private Integer heureSync;

    public ConfForm(/*AbstrDao dao*/) {
//        super(dao);
    }

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
            conf.setNbThreadRecup(nbThreadRecup);
            conf.setServname(servname);
            conf.setLogin(login);
            conf.setPass(pass);
            conf.setJmsprovider(jmsprovider);
            conf.setMaster(master);
            conf.setServeurSlave(serveurSlave);
            conf.setHostMaster(hostMaster);
            conf.setPurgeDuration(purgeDuration);
            conf.setJourSync(jourSync);
            conf.setHeureSync(heureSync);
        }


        //=====================================================================================
        // CAPTURE DE LA SAISIE DE CHAQUE ELEMENT DANS LA REQUEST ET VÉRIFICATION DE CEUX CI
        //=====================================================================================

    

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
        
        
        String s;
            // ----------------------------NOMBRE DE Thread
        s = request.getParameter("nbThreadRecup");
        try {
//            Integer val = new Integer(s);
            this.nbThreadRecup = new Integer(s);
//            conf.setNbThreadRecup(val);
        } catch (Exception e) {
            erreurs.put("nbThreadRecup", new String[]{s, "Ceci n'est pas un nombre entier"});
        }


        //-----------------------------Nom de serveur
        s = request.getParameter("servname");
        if (s != null) {
            Pattern p = Pattern.compile("^[a-z]*$");
            Matcher m = p.matcher(s);
            if (!m.find()) {
                erreurs.put("servname", new String[]{s, "doit être composé uniquement de minuscules"});
            } else {
                this.servname = s;
            }

        }
//        conf.setServname(s);


        // Vérification doit être uniquement composé de caractère



        //----------------------------Login

        s = request.getParameter("login");
        if (s != null) {
            Pattern p = Pattern.compile("^[a-zA-Z0-9]*$");
            Matcher m = p.matcher(s);
            if (!m.find()) {
                erreurs.put("login", new String[]{s, "doit être composé uniquement de caractères alphanumérics"});
            } else {
                this.login = s;
            }
        }

        //------------------------------Pass du serveur courant

        s = request.getParameter("pass1");
//        conf.setPass(s);

        String m1 = request.getParameter("pass1");
        String m2 = request.getParameter("pass2");

        if (m1 != null && m2 != null) {
            if (m1.equals(m2) && m1.length() > 3) {
                this.pass = m1;
//                conf.setPass(m1);
            } else {
                erreurs.put("pass", new String[]{s, "mot de passe incorrect"});
            }
        }

        //--------------------JMS Provider
        s = request.getParameter("jmsprovider");
        if (s != null && !s.isEmpty()) {
            this.jmsprovider = s;
//            conf.setJmsprovider(s);
        }

        //-------------------Master statut
        s = request.getParameter("master");
        if (s == null) {
            this.master = false;
//            conf.setMaster(false);
        } else {
            this.master = true;
//            conf.setMaster(true);
        }

        // ------------------Serveur esaclaves lié à la config
        String[] tabslavehost = request.getParameterValues("hostslave");
        String[] tabslavelogin = request.getParameterValues("loginSlave");
        String[] tabslavepass = request.getParameterValues("passSlave");
        String[] tabUrlSlave = request.getParameterValues("urlSlave");
        int i;


        this.serveurSlave = new ArrayList<ServeurSlave>();
//        conf.setServeurSlave(new ArrayList<ServeurSlave>());
        if (tabslavehost != null && tabslavelogin != null && tabslavepass != null && tabUrlSlave != null) {
            if (tabslavehost.length == tabslavelogin.length && tabslavepass.length == tabslavehost.length && tabslavehost.length == tabUrlSlave.length) {
                for (i = 0; i < tabslavehost.length; i++) {
                    ServeurSlave slave = new ServeurSlave();
                    slave.setHost(tabslavehost[i]);
                    slave.setLogin(tabslavelogin[i]);
                    slave.setPass(tabslavepass[i]);
                    slave.setUrl(tabUrlSlave[i]);
                    this.serveurSlave.add(slave);
//                    conf.getServeurSlave().add(slave);
                }
            }
        }

        // ------------------Host du serveur maitre
        s = request.getParameter("hostMaster");
        if (s !=null) {
            this.hostMaster =s;
        }
//        conf.setHostMaster(s);


        //-------------------DureePurge
        s = request.getParameter("purgeDuration");
        if (s != null) {
            try {
                Integer val = new Integer(s);
//                conf.setPurgeDuration(val);
                this.purgeDuration = val;
            } catch (Exception e) {
                erreurs.put("purgeDuration", new String[]{s, "Il faut un nombre entier"});
            }
        }

        //-------------------------Jour de la synchronisation
        s = request.getParameter("jourSync");
        if (s != null && (s.equals("lu") || s.equals("ma") || s.equals("me") || s.equals("je") || s.equals("ve") || s.equals("sa") || s.equals("di"))) {
//            conf.setJourSync(s);
            this.jourSync =s;
//            System.out.println("JOURRRR : " + s);
        }

        //-------------------------Heure de synchronisation
        s = request.getParameter("heureSync");
        if (s != null) {
            Integer val = null;
            try {
                val = new Integer(s);
            } catch (Exception e) {
                erreurs.put("heureSync", new String[]{s, "Ceci n'est pas un nombre entier"});
            }

            if (val != null && (val < 0 || val > 23)) {
                erreurs.put("heureSync", new String[]{s, "Il faut un entier entre 0 et 23"});
            } else {
                this.heureSync = val;
//                conf.setHeureSync(val);
            }
        }

        //=============================================================================================

        if (erreurs.isEmpty()) {
            resultat = "Traitement effectué";
            valide = true;

        } else {
            resultat = "Erreur lors de la validation des données";
            valide = false;
        }

        for (i = 0; i < erreurs.size(); i++) {
//            System.out.println("erreur"+ erreurs.get(i)[0]+"   //  " + erreurs.get(i)[1]);
            System.out.println("" + erreurs.toString());
        }

        
        return this.valide;
        
    }
}
