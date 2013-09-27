/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import javax.servlet.http.HttpServletRequest;

/**
 * Les Item ne sont pas encore modifiable. Cette fonctionnalité n'est pas perçut comme nécessaire au projet. Cette classe ne fait rien...
 * @author clem
 */
public class ItemForm extends AbstrForm{

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean validate(HttpServletRequest request) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
