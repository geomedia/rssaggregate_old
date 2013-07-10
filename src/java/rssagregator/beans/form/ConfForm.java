/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import rssagregator.dao.AbstrDao;

/**
 *
 * @author clem
 */
public class ConfForm extends AbstrForm {

    public ConfForm(/*AbstrDao dao*/) {
//        super(dao);
    }

    public void check_nbThreadRecup(String entre) throws Exception {
        try {
                    Integer i = Integer.parseInt(entre);
        } 
        catch(Exception e){
            throw new Exception("Ceci n'est pas un nombre entier");
        }

        
    }
}
