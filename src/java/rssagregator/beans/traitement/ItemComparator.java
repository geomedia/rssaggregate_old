/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.util.Comparator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import rssagregator.beans.ContentRSS;
import rssagregator.services.tache.TacheRaffiner;

/**
 * Comparator utilisé pour déteminer si deux contenuRSS (Item Brute ou Raffinné) correspondent un un même article. Ce
 * comparator est utilisé dans le processus de Raffinage des item par la tache {@link TacheRaffiner} Le comparator
 * permet de déterminer si deux item sont strictement itentique; ont un contenu différent mais proviennent d'un même
 * article; ou si ce sont deux items différentes
 *
 * @author clem
 */
public class ItemComparator implements Comparator<ContentRSS> {

    /**
     * *
     *
     * @param o1
     * @param o2
     * @return -1 item provenant d'article différent 0 titem strictement equal ; 1 item prevenant du mm article
     */
    @Override
    public int compare(ContentRSS o1, ContentRSS o2) {

//        System.out.println("Comparaison");




        /**
         * *
         * Les statut
         *
         * -1 différent 0 0 inneploitable 1 semblable 2 equal
         */
        short link = -1;
        short titre = -1;
        short desc = -1;
        short guid = -1;
        short date = -1;

        // On commence par vérifier les eqalité et la comparabilité
        // Titre est exploitatble si non null et lenght >5
        if (o1.getTitre() != null && o2.getTitre() != null && o1.getTitre().length() > 5 && o2.getTitre().length() > 5) {
            if (o1.getTitre().trim().equals(o2.getTitre().trim())) {
                titre = 2; // equlité stricte
            } else {
                titre = -1;
            }
        } else {
            titre = 0;
        }

        // Desc 
        if (o1.getDescription() != null && o2.getDescription() != null && o2.getDescription().length() > 10 && o2.getDescription().length() > 10) {
//            System.out.println("Desc 1 " + o1.getDescription());
//            System.out.println("Desc 2 " + o2.getDescription());


            if (o1.getDescription().trim().equals(o2.getDescription().trim())) { // equalité stricte
                desc = 2;
            } else {
                Document documentTxt = Jsoup.parse(o1.getDescription());
                documentTxt.select("a").remove();    // Suppression de tous les liens
                documentTxt.select("li").remove();    // Suppression de tous les liens
                documentTxt.select("ol").remove();    // Suppression de tous les liens

                String text1 = documentTxt.text(); // Suppression du HTML

                documentTxt = Jsoup.parse(o2.getDescription());
                documentTxt.select("a").remove();
                documentTxt.select("li").remove();
                documentTxt.select("ol").remove();
                String text2 = documentTxt.text();
                if (!text1.isEmpty() && !text2.isEmpty()) {
                    if (text1.equals(text2)) {
                        desc = 1;
                    } else {
                        desc = -1; // différent
                    }
                } else {
                    desc = 0;
                }
            }

        } else {
            desc = 0;
        }


        // Date
        if (o1.getDatePub() != null && o2.getDatePub() != null) {
            if (o1.getDatePub().equals(o2.getDatePub())) {
                date = 2;
            } else {
                date = -1;
            }
        } else {
            date = 0;
        }


        // Lien
        if (o1.getLink() != null && o2.getLink() != null && !o1.getLink().isEmpty() && !o2.getLink().isEmpty()) {

            if (o1.getLink().equals(o2.getLink())) {
                link = 2;
            } else {
                link = -1;
            }

        } else {
            link = 0;
        }


        if (o1.getGuid() != null && o2.getGuid() != null && !o1.getGuid().isEmpty() && !o2.getGuid().isEmpty()) {
            if (o1.getGuid().equals(o2.getGuid())) {
                guid = 2;
            } else {
                guid = -1;
            }
        } else {
            guid = 0;
        }

        boolean similaire; // Les contenu doivent provenir du mm article mais ne sont pas strictementEqual
        boolean strictementEqual = false;

        // On exclu les item dont le titre et l'item ne sont pas exploitable
        if (titre == 0 || link == 0) {
            return -1; // Le comparator dit que les articles proviennent d'article different pour éviter de se tromper
        }

        if (guid == 2 && titre == 2 && desc == 2 && link == 2 && date == 2) {
            return 0; // strictement identique
        }

        if (guid > 0 && link == 1 && titre > 0) { // MM guid et mm lien m titre
            return 1;
        }

        if (titre > 0 && desc > 0) {
            return 1;
        }

        if (titre > 0 && link > 0) { // Si le titre et le lien sont ==
            return 1;
        }

        if (desc > 0 && link > 0) {
            return 1;
        }

        if (titre > 0 && guid > 0) {
            return 1;
        }
//        System.out.println("--------------------");
//        System.out.println("CAS NON GERE ");
//        System.out.println("TITRE : " + titre);
//        System.out.println("Desc : " + desc);
//        System.out.println("link : " + link);
//        System.out.println("guid : " + guid);
//        System.out.println("Date : " + date);
//        System.out.println("----------------");
        return -1; // tout autre cas il retourne -1


//        if (guid == 1 && link > 0 && titre == 1) { // mm GUID 
//            return 1;
//        }




        // prise de décision
        // si les guid sont exploitable
//        if (guid > 0) {
//            if (guid == 1) { // Si il sont semblable
//
//                if (link > 0) { // Si le lien est utilisable
//                    if (link == 1) { // Si les liens sont semblable
//                        // Si mm lien et mm guid . On considère l'item comme similaire si le titre est exploitable
//                        if (titre > 0) {
//                            return 1;
//                        }
//
//
//                    } else {// Si les liens sont dissemblable
//                        if (titre == 1) { // Si même titre ( GUID ==; LIEN; Innexploitable; TITRE ==)
//                            return 1;
//                        }
//                        if (desc == 1) { // Si mm des
//                            return 1;
//                        }
//                    }
//
//                }
//            } else {
//                if (link > 0) {
//                }
//
//
//            }
//            if (link > 0) {
//            }
//        } else if (link > 0) { // Si le lien est exploitable
//            if (link == 1) { // Si le lien est equal
//                return 1; // Si mm lien c'est le même acticle
//
//            } else { // Si les liens sont dissemblable
//                if (titre > 0 && desc > 0 && date > 0) {
//                    if (titre == 1 && desc == 1 && date == 1) { // Si même titre et mm desc 
//                        return 1;
//                    }
//                }
//            }
//        }

        // On commence par comparer les guid
//        if (o1.getGuid()
//                != null && o2.getGuid() != null) {
//            if (o1.getGuid().equals(o2.getGuid())) {
//
//                boolean toutPareil = true;
//                boolean similaire = true;
//
//
//
//                // On vérifi le titre
//                if (!o1.getTitre().equals(o2.getTitre())) {
//                    toutPareil = false;
//                }
//
//                // On vérifi les liens
//                if (!o1.getLink().equals(o2.getLink())) {
//                    toutPareil = false; // SI ils sont différent l'item ne peut être strict equal
//
//                }
//
//
//
//
//
//            }
//        }
//
//        // Si les liens sont equals
//        if (o1.getLink()
//                .equals(o2.getLink())) {
//        }



//        throw new UnsupportedOperationException(
//                "Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
