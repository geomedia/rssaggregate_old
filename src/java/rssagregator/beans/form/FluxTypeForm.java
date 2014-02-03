/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.FluxType;

/**
 * Objet formulaire permettant de valider les données saisies par l'utilisateur pour le beans <strong>FluxType</strong>
 *
 * @author clem
 */
public class FluxTypeForm extends AbstrForm {

    //---------------------------------------
    //Les variables devant être récupérée dans la requête
    private String denomination;
    private String description;

    //---------------------------------------
    protected FluxTypeForm() {
    }
    
    @Override
    public Boolean validate(HttpServletRequest request) {
        String s;
        erreurs = new HashMap<String, String[]>();
        //-----------------------------------------------------------
        //-----------> DENOMINATION 
        s = request.getParameter("denomination");
        if (s != null) {
            if (s.matches(REG_EXP_ALPHANUM_FR)) {
                denomination = s;
            } 
            else {
                erreurs.put("denomination", new String[]{ERR_ALPHANUM_FR, ERR_ALPHANUM_FR});
            }
        }

        //-----------> DESCRIPTION 
        s = request.getParameter("description");
        if (s != null) {
            description = s;
        }

        //-----------------------------------------------------------
        if (erreurs.isEmpty()) {
            this.valide = true;
        } else {
            this.valide = false;
        }
        return this.valide;
    }

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {

        if (this.valide) {
            FluxType fluxType = (FluxType) objEntre;
            // Instanciation du type de flux si l'objet envoyé est null
            if (fluxType == null) {
                try {
                    fluxType = (FluxType) type.newInstance();
                } catch (InstantiationException ex) {
                    Logger.getLogger(FluxTypeForm.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(FluxTypeForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // Bind des valeurs
            fluxType.setDenomination(denomination);
            fluxType.setDescription(description);
            return fluxType;
        } else {
            return null;
        }
    }
}
