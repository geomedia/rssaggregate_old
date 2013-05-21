/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import dao.AbstrDao;

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
//        catch (NumberFormatException e) {
//            System.out.println("");
//            throw new Exception("Ceci n'est pas un nombre entier");
//        }
        catch(Exception e){
            throw new Exception("Ceci n'est pas un nombre entier");
        }

        
    }
}
