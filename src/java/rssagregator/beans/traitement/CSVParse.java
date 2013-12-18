/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import au.com.bytecode.opencsv.CSVReader;
import com.sun.syndication.io.FeedException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.Item;

/**
 * Ce parseur permet de parcourir un fichier CSV afin de générer des beans ${@link Item}. L'ordre des colonne est fixe.
 * Aux utilisateur de fournir un fichier compatible. Ordre : <ul>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * </ul>
 *
 *
 * @author clem
 */
public class CSVParse extends AbstrParseur {

    /**
     * *
     * Le fichier csv qui doit être analysé
     */
    File csvFile;
    private char separator;
    private char quotechar;
    private char escape;
    private int line;
    private boolean strictQuotes;
    private boolean ignoreLeadingWhiteSpace;
    private int cTitre;
    private int cDescription;
    private int cLink;
    private int cGuid;
    private int cDatePub;
    private int cDateRecup;
    private int cCat;
    private int cContenu;

    @Override
    public List<Item> execute(InputStream xml) throws IOException, IllegalArgumentException, FeedException {

        List<Item> listItem = new ArrayList<Item>();

        
        
        
//        FileInputStream fileInputStream = new FileInputStream(csvFile);

//        CSVReader cSVReader = new CSVReader(new InputStreamReader(xml));


        //Parcours du fichier ligne à ligne
//        CSVReader reader = new CSVReader(new FileReader(csvFile));
        CSVReader reader = new CSVReader(new InputStreamReader(xml), separator, quotechar, escape, line, strictQuotes, ignoreLeadingWhiteSpace);


        String[] nextLine;
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        while ((nextLine = reader.readNext()) != null) {
            String titre = nextLine[cTitre];
            String description = nextLine[cDescription];
            String link = nextLine[cLink];
            String guid = nextLine[cGuid];
            String datePub = nextLine[cDatePub];
            String dateRecup = nextLine[cDateRecup];
            String cat = nextLine[cCat];
            String contenu = nextLine[cContenu];
            
            System.out.println("datepub : " + datePub);
            System.out.println("date recup : " + dateRecup);
            System.out.println("Coll date recup : " + cDateRecup);

            Item item = new Item();
            item.setTitre(titre);
            item.setDescription(description);
            item.setLink(link);
            item.setGuid(guid);
            item.setCategorie(cat);
            item.setContenu(contenu);
//            item.setDatePub(new Date(datePub));

            // TODO : on a peut être une perte de performance avec cette librairie. Optimisation ?
            item.setDatePub(fmt.parseDateTime(datePub).toDate());
            item.setDateRecup(fmt.parseDateTime(dateRecup).toDate());
            listItem.add(item);

        }

//        for (int i = 0; i < listItem.size(); i++) {
//            Item item = listItem.get(i);
//            System.out.println("Titre : " + item.getTitre());
//            System.out.println("Desc : " + item.getDescription());
//            System.out.println("Date Pub " + item.getDatePub());
//            System.out.println("Date Recup : "+ item.getDateRecup());
//        }
//        
        return listItem;




//        return super.execute(xml); //To change body of generated methods, choose Tools | Templates.
    }

    public CSVParse() {
    }

//    public File getCsvFile() {
//        return csvFile;
//    }
//
//    public void setCsvFile(File csvFile) {
//        this.csvFile = csvFile;
//    }
    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public char getQuotechar() {
        return quotechar;
    }

    public void setQuotechar(char quotechar) {
        this.quotechar = quotechar;
    }

    public char getEscape() {
        return escape;
    }

    public void setEscape(char escape) {
        this.escape = escape;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public boolean isStrictQuotes() {
        return strictQuotes;
    }

    public void setStrictQuotes(boolean strictQuotes) {
        this.strictQuotes = strictQuotes;
    }

    public boolean isIgnoreLeadingWhiteSpace() {
        return ignoreLeadingWhiteSpace;
    }

    public void setIgnoreLeadingWhiteSpace(boolean ignoreLeadingWhiteSpace) {
        this.ignoreLeadingWhiteSpace = ignoreLeadingWhiteSpace;
    }

    public int getcDescription() {
        return cDescription;
    }

    public void setcDescription(int cDescription) {
        this.cDescription = cDescription;
    }

    public int getcLink() {
        return cLink;
    }

    public void setcLink(int cLink) {
        this.cLink = cLink;
    }

    public int getcGuid() {
        return cGuid;
    }

    public void setcGuid(int cGuid) {
        this.cGuid = cGuid;
    }

    public int getcDatePub() {
        return cDatePub;
    }

    public void setcDatePub(int cDatePub) {
        this.cDatePub = cDatePub;
    }

    public int getcDateRecup() {
        return cDateRecup;
    }

    public void setcDateRecup(int cDateRecup) {
        this.cDateRecup = cDateRecup;
    }

    public int getcCat() {
        return cCat;
    }

    public void setcCat(int cCat) {
        this.cCat = cCat;
    }

    public int getcContenu() {
        return cContenu;
    }

    public void setcContenu(int cContenu) {
        this.cContenu = cContenu;
    }

    public int getcTitre() {
        return cTitre;
    }

    public void setcTitre(int cTitre) {
        this.cTitre = cTitre;
    }
    
    

    public static void main(String[] args) {
        try {
            File f = new File("/home/clem/parseTestLeMonde.csv");
            CSVParse parse = new CSVParse();

//              CSVReader reader = new CSVReader(new FileReader(csvFile), separator, quotechar, escape, line, strictQuotes, ignoreLeadingWhiteSpace);


            parse.setLine(0);
            parse.setEscape('\\');
            parse.setSeparator('\t');
            parse.setQuotechar('"');
//            parse.setCsvFile(f);
            parse.setStrictQuotes(false);
            parse.setIgnoreLeadingWhiteSpace(true);


            parse.execute(null);

        } catch (IOException ex) {
            Logger.getLogger(CSVParse.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CSVParse.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FeedException ex) {
            Logger.getLogger(CSVParse.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
