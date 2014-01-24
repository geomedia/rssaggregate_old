/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

/**
 *
 * @author clem
 */
public class StringUtils {
    
    
    
    
    public static String returnAbrege(String grosString, int nbrChar, String pattern){
        ExceptionTool.argumentNonNull(grosString);
        ExceptionTool.argumentNonNull(pattern);
        
        
//                    String nomJoural = flux.getJournalLie().getNom();
            grosString = grosString.replaceAll(pattern, "");
            if (!grosString.isEmpty()) {
                if (grosString.length() > nbrChar) {
                    grosString = grosString.substring(0, nbrChar);
                } else {
                    grosString = grosString.substring(0, grosString.length());
                }
            }
            
            return grosString;
    }
    
    
}
