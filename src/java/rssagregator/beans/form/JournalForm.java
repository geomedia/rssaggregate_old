/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

/**
 *
 * @author clem
 */
public class JournalForm extends AbstrForm{

    public JournalForm(/*AbstrDao dao*/) {
//        super(dao);
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
    
    
}
