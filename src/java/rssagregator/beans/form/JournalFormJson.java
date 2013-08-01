/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author clem
 */
public class JournalFormJson {
    protected Boolean valide; // La statut de la validation
     protected String resultat = ""; // Permet de stocker un message générale sur le formulaire ("toute les modification on été effectuées ; erreur lors de la validation du formulaire ..."

         protected Map<String, String[]> erreurs = new HashMap<String, String[]>();
     
    public JournalFormJson() {
    valide = false;
    }

    
    

public Object bind(String json, Object bean){
    System.out.println("Le json : " + json);
        return null;
    
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

    public Boolean getValide() {
        return valide;
    }

    public void setValide(Boolean valide) {
        this.valide = valide;
    }

    public String getResultat() {
        return resultat;
    }

    public void setResultat(String resultat) {
        this.resultat = resultat;
    }

    public Map<String, String[]> getErreurs() {
        return erreurs;
    }

    public void setErreurs(Map<String, String[]> erreurs) {
        this.erreurs = erreurs;
    }
    
    
    
    
    
}
