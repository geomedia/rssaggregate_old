/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.Journal;

/**
 *
 * @author clem
 */
public class JournalForm extends AbstrForm {

    
    private String nom;
    private String urlAccueil;
    private String urlHtmlRecapFlux;
    private String langue;
    private String pays;
    private String fuseauHorraire;
    private String information;

    public JournalForm() {
    super();
    }
    
    


    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
//        return super.bind(request, objEntre, type); //To change body of generated methods, choose Tools | Templates.

        if(this.action.equals("add")){
            try {
                objEntre = type.newInstance();
//                objEntre =  type.newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(JournalForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(JournalForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Journal journal = (Journal) objEntre;
        
       journal.setNom(nom);
       journal.setUrlAccueil(urlAccueil);
       journal.setUrlHtmlRecapFlux(urlHtmlRecapFlux);
       journal.setLangue(langue);
       journal.setPays(pays);
       journal.setFuseauHorraire(fuseauHorraire);
       journal.setInformation(information);

        return journal;
    }

    public void check_nom(String nom) throws Exception {
        if (nom == null || nom.length() == 0) {
            throw new Exception("Ne peut être null");
        }
    }

    public void check_langue(String nom) throws Exception {
        if (nom == null || nom.length() == 0) {
            throw new Exception("Ne peut être null");
        }
    }

    @Override
    public Boolean validate(HttpServletRequest request) {
           //Bind du nom
        String s = request.getParameter("nom");
        if(s!=null && !s.isEmpty()){
            nom = s;
//            journal.setNom(s);
//            System.out.println("--> NOM : " + journal.getNom());
        }
        
        
        s =request.getParameter("urlAccueil");
        if(s!=null && !s.isEmpty()){
            urlAccueil = s;
//            journal.setUrlAccueil(s);
        }
        
        s = request.getParameter("urlHtmlRecapFlux");
        if(s!=null && !s.isEmpty()){
            urlHtmlRecapFlux = s;
//            journal.setUrlHtmlRecapFlux(s);
        }
        
        s = request.getParameter("langue");
        if(s!=null && !s.isEmpty()){
            langue =s;
//            journal.setLangue(s);
        }
        
        s = request.getParameter("pays");
        if(s!=null && !s.isEmpty()){
            pays =s;
//            journal.setPays(s);
        }
        
        s= request.getParameter("fuseauHorraire");
        if(s!=null && !s.isEmpty()){
            fuseauHorraire= s;
//            journal.setFuseauHorraire(s);
        }
                
        s = request.getParameter("information");
        if(s!=null && !s.isEmpty()){
            information =s;
//            journal.setInformation(s);
        }
        
        
        valide = erreurs.isEmpty();
        return valide;
        
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
