/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.traitement.CSVParse;

/**
 * Permet d'interpréter le formulaire de saisie permettant a l'utilisateur de saisir les informations relatives à
 * l'imports d'items depuis un fichier CSV.
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
    private int cTitre;
    private int cDescription;
    private int cLink;
    private int cGuid;
    private int cDatePub;
    private int cDateRecup;
    private int cCat;
    private int cContenu;
    private String forceEncoding;
    private String datePattern;

    @Override
    public Object bind(HttpServletRequest request, Object objEntre, Class type) {


        if (this.valide) {
            CSVParse parse = new CSVParse();
            parse.setEscape(escape);
            parse.setIgnoreLeadingWhiteSpace(ignoreLeadingWhiteSpace);
            parse.setLine(line);
            parse.setQuotechar(quotechar);
            System.out.println("Sepa bind " + separator);
            parse.setSeparator(separator);
            parse.setStrictQuotes(strictQuotes);

            parse.setcTitre(cTitre);
            parse.setcCat(cCat);
            parse.setcContenu(cContenu);
            parse.setcDatePub(cDatePub);
            parse.setcDateRecup(cDateRecup);
            parse.setcDescription(cDescription);
            parse.setcGuid(cGuid);
            parse.setcLink(cLink);
            parse.setForceEncoding(forceEncoding);
            parse.setDatePattern(datePattern);


            return parse;
        }
        return null;

    }

    @Override
    public Boolean validate(HttpServletRequest request) {
        String s;
        s = request.getParameter("separator");
        if (s != null && !s.isEmpty()) {
            if (s.equals("\\t")) {
                separator = '\t';
            } else {
                separator = s.charAt(0);
                System.out.println("SEPARATOR : " + separator);
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


        s = request.getParameter("cTitre");
        if (s != null) {
            cTitre = new Integer(s);
        }

        s = request.getParameter("cDescription");
        if (s != null) {
            System.out.println("C desc " + s);
            cDescription = new Integer(s);
        }

        s = request.getParameter("cLink");
        if (s != null) {
            cLink = new Integer(s);
        }

        s = request.getParameter("cGuid");
        if (s != null) {
            cGuid = new Integer(s);
        }

        s = request.getParameter("cDatePub");
        if (s != null) {
            cDatePub = new Integer(s);
        }

        s = request.getParameter("cDateRecup");
        if (s != null) {
            cDateRecup = new Integer(s);
        }

        s = request.getParameter("cCat");
        if (s != null) {
            cCat = new Integer(s);
        }

        s = request.getParameter("cContenu");
        if (s != null) {
            cContenu = new Integer(s);
        }

        if (erreurs.isEmpty()) {
            this.valide = true;
        } else {
            this.valide = false;
        }


        s = request.getParameter("forceEncoding");
        if (s != null && !s.isEmpty()) {
            if (Charset.isSupported(s)) {
                System.out.println("Charset supporté");
                forceEncoding = s;
            } else {
                System.out.println("NON supporté");
            }
        }


        s = request.getParameter("datePattern");
        if (s != null && !s.isEmpty()) {

            try {
                DateTimeFormatter fmt = DateTimeFormat.forPattern(s); // On tente d'interpréter le pattern 
                datePattern = s;
            } catch (Exception e) {
                logger.debug("Pattern Invalide");
            }
        }
        return this.valide;
    }
}
