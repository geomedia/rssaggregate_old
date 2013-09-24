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
 *
 * @author clem
 */
public class FluxTypeForm extends AbstrForm {

    private String denomination;
    private String description;

    @Override
    public Boolean validate(HttpServletRequest request) {

        String s;
        erreurs = new HashMap<String, String[]>();

        s = request.getParameter("denomination");
        if (s != null) {
            denomination = s;
        }


        s = request.getParameter("description");
        if (s != null) {
            description = s;
        }

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
            System.out.println("----> VaLiDe");
//            FluxType fluxType = new FluxType();
            
            FluxType fluxType = (FluxType) objEntre;
            if(fluxType==null){
                try {
                    fluxType = (FluxType) type.newInstance();
                } catch (InstantiationException ex) {
                    Logger.getLogger(FluxTypeForm.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(FluxTypeForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
            fluxType.setDenomination(denomination);
            fluxType.setDescription(description);
            return fluxType;
        } else {
            return null;
        }
    }
}
