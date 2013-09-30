/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.traitement.CSVParse;

/**
 *
 * @author clem
 */
public class ParseCsvForm extends AbstrForm {

    private char separator;
    private char quotechar;
    private char escape;
    private int line;
    private boolean strictQuotes = false;
    private boolean ignoreLeadingWhiteSpace = false;

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {


        if (this.valide) {
            CSVParse parse = new CSVParse();
            parse.setEscape(escape);
            parse.setIgnoreLeadingWhiteSpace(ignoreLeadingWhiteSpace);
            parse.setLine(line);
            parse.setQuotechar(quotechar);
            parse.setSeparator(separator);
            parse.setStrictQuotes(strictQuotes);
            
            return parse;
        }
        return null;




//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean validate(HttpServletRequest request) {


        String s;

        s = request.getParameter("separator");
        if (s != null) {
            if (s.equals("\\t")) {
                separator = '\t';
            }
        }

        s = request.getParameter("quotechar");
        if (s != null) {
            quotechar = s.charAt(0);
        }

        s = request.getParameter("escape");
        if (s != null) {
            escape = s.charAt(0);
        }

        s = request.getParameter("line");
        if (s != null) {
            try {
                line = new Integer(s);
            } catch (Exception e) {
            }
        }

        s = request.getParameter("strictQuotes");
        if (s != null) {
            strictQuotes = true;
        }

        s = request.getParameter("ignoreLeadingWhiteSpace");
        if (s != null) {
            ignoreLeadingWhiteSpace = true;
        }

        if (erreurs.isEmpty()) {
            this.valide = true;
        } else {
            this.valide = false;
        }

        return this.valide;
    }
}
