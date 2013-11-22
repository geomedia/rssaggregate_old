/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import rssagregator.beans.DonneeBrute;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.Journal;
import rssagregator.beans.exception.ArgumentIncorrect;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;
import rssagregator.services.ServiceCollecteur;

/**
 * Ce dedoublonneur est utilisé en second Après de {@link Dedoubloneur}, il permet de compléter le traitement. Ce
 * deuxieme dédoublonneur va rechercher dans la base de donnée des items possédant le même titre et appartenant a un
 * flux du journal. Il va ensuite observer le contenu pour déterminer si l'item capturée est semblable ou non
 *
 * @author clem
 */
@Entity(name = "DedoubloneurComparaisonTitre")
public class DedoubloneurComparaisonTitre extends AbstrDedoublonneur {

//    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DedoubloneurComparaisonTitre.class);
    @Override
    public List<Item> dedoublonne(List<Item> listItemCapture, Flux flux) {

        this.dedoublonnageInterneduneListDItem(listItemCapture, false, true, true);

        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
        Journal journal = flux.getJournalLie();




        ItemComparator comparator = new ItemComparator();

        for (ListIterator<Item> it = listItemCapture.listIterator(); it.hasNext();) {
            Item itemCapture = it.next();
            boolean goNext = false;
            // Si l'item est nouvelle
            if (itemCapture.getID() == null) {
                if (journal != null) {
                    try {

                        List<Item> itemsSemblableBDD = daoItem.findItemPossedantTitreAppartenantAuJournal(itemCapture.getTitre(), journal);


                        Document documentTxtItemCapture = Jsoup.parse(itemCapture.getDescription());

                        documentTxtItemCapture.select("a").remove();
                        String textItemCapture = documentTxtItemCapture.text();

                        List<DonneeBrute> donneeBrutesItemCapture = itemCapture.getDonneeBrutes();


                        int iItemBDD = 0;
                        while (iItemBDD < itemsSemblableBDD.size() && goNext == false) {

//                        }
//                        for (int j = 0; j < itemsSemblableBDD.size(); j++) {
                            Item itemSemblableBDD = itemsSemblableBDD.get(iItemBDD);

                            // Il faut comparer les données brutes entre elles

                            List<DonneeBrute> donneBrutItemBDD = itemSemblableBDD.getDonneeBrutes();


                            int iDonneeBDD = 0;
                            while (iDonneeBDD < donneBrutItemBDD.size() && !goNext) {

//                            }
//                            
//                            for (int i = 0; i < donneBrutItemBDD.size(); i++) {
                                DonneeBrute donneeBruteITBDD = donneBrutItemBDD.get(iDonneeBDD);

                                int iDonneCapture = 0;
                                while (iDonneCapture < donneeBrutesItemCapture.size() && !goNext) {

//                                }
//                                for (int k = 0; k < donneeBrutesItemCapture.size(); k++) {
                                    DonneeBrute donneeBruteCapture = donneeBrutesItemCapture.get(iDonneCapture);

                                    int retour = comparator.compare(donneeBruteCapture, donneeBruteITBDD);
                                    System.out.println("-> Retour comparator : " + retour);
                                    if (retour == 0) { // Les donnée brutes sont strictement identique

                                        if (donneeBruteITBDD.getFlux().getID().equals(flux.getID())) {
                                            logger.debug("Le comparator a dit item strictement identique");
                                            it.remove();
                                            mediatorAReferer.nbDedoubBdd++;
                                            goNext = true;
                                        } else {
                                            itemSemblableBDD.verserLesDonneeBruteAutreItem(itemCapture);
                                            it.set(itemSemblableBDD); // On remplace     
                                           
                                            goNext = true;
                                        }

                                    } else if (retour == 1) { // Les données proviennent du mm article mais ne sont pas strictement identique

                                        logger.debug("versement");
                                        itemSemblableBDD.verserLesDonneeBruteAutreItem(itemCapture);
                                        it.set(itemSemblableBDD); // On remplace
                                        goNext = true;
                                      

                                    }

                                    iDonneCapture++;
                                }

                                iDonneeBDD++;
                            }









                            /**
                             * *
                             * === Comparaison de la description === Si on trouve une item possédant le même texte de
                             * description elle est considérée comme similaire
                             */
//                            if (!textItemCapture.isEmpty()) {
//                               
//
//                                Document documentTxt = Jsoup.parse(itemSemblableBDD.getDescription());
//
//                                documentTxt.select("a").remove();    // Suppression de tous les liens
//                                String textItemSemblableBdd = documentTxt.text(); // Suppression du HTML
//
//
//
//                                if (textItemCapture.equals(textItemSemblableBdd)) { // Si les deux texte sont semblable
//                                    // On cherche a savoir si l'item BDD est déjà lié au flux
//                                    Boolean trouve = fluxPresentDansList(itemSemblableBDD.getListFlux(), flux);
//
//                                    if (trouve) { // Si c'est déjà lié
//                                        logger.debug("item déjà lié au flux inspecté on supprime");
//                                        ServiceCollecteur.getInstance().getCacheHashFlux().addHash(flux, itemCapture.getHashContenu());
//                                        it.remove();
//
//                                    } else {
//                                        try {
//                                            logger.debug("Text description similaire. Liason avec une item déjà existance");
//                                            itemSemblableBDD.verserLesDonneeBruteAutreItem(itemCapture);
//                                            it.set(itemSemblableBDD); // On remplace       
//
//                                        } catch (Exception e) {
//                                            logger.debug("err", e);
//                                        }
//                                    }
//                                    break; // L'item trouvé a été traité on passe a la suivante
////                        continue; // L'item trouvé a été traité on passe a la suivante
//                                }
//                            }
                            /**
                             * *
                             * Comparaison basé sur la date de publication Si Les titre sont == et date de pub == On
                             * considère que c'est la même item
                             */
//                            if (itemCapture.getDatePub() != null && itemSemblableBDD.getDatePub() != null) {
//
//                                if (itemCapture.getDatePub().equals(itemSemblableBDD.getDatePub())) {
//                                    Boolean trouve = fluxPresentDansList(itemSemblableBDD.getListFlux(), flux);
//                                    if (trouve) {
//                                        it.remove();
//                                    } else {
//                                        itemSemblableBDD.verserLesDonneeBruteAutreItem(itemCapture);
//                                        it.set(itemSemblableBDD);
//                                    }
//                                    break;
//                                }
//                            }
                            iItemBDD++;
                        }

                    } catch (NullPointerException ex) {
                        Logger.getLogger(DedoubloneurComparaisonTitre.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ArgumentIncorrect ex) {
                        Logger.getLogger(DedoubloneurComparaisonTitre.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }



        // Suppression des possible doublons a l'intérieur de la liste


//        for (Iterator<Item> it = listItemCapture.iterator(); it.hasNext();) {
//            Item item = it.next();
//            int cpt = 0;
//            if (item.getID() != null) {
//                for (Iterator<Item> it1 = listItemCapture.iterator(); it1.hasNext();) {
//                    Item item1 = it1.next();
//                    if(item1.getID() != null){
//                        if(item1.getID().equals(item.getID())){
//                            cpt ++;
//                        }
//                    }
//                }
//                if(cpt>1){
//                    it.remove();
//                }
//            }
//
//        }



        return listItemCapture;
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String[] args) {


        String s1 = "\"Le président américain a dû s'excuser publiquement du lancement catastrophique de l'\"Obamacare\".<img width='1' height='1' src='http://rss.lemonde.fr/c/205/f/3050/s/33b85fff/sc/11/mf.gif' border='0'/><br clear='all'/>\"";
        String s2 = "\"Le président américain a dû s'excuser publiquement du lancement catastrophique de l'\"Obamacare\".<img width='1' height='1' src='http://rss.lemonde.fr/c/205/f/3050/s/33b675a3/sc/11/mf.gif' border='0'/><br clear='all'/><br/><br/><a href=\"http://da.feedsportal.com/r/180264259429/u/89/f/3050/c/205/s/33b675a3/sc/11/rc/1/rc.htm\"><img src=\"http://da.feedsportal.com/r/180264259429/u/89/f/3050/c/205/s/33b675a3/sc/11/rc/1/rc.img\" border=\"0\"/></a><br/><a href=\"http://da.feedsportal.com/r/180264259429/u/89/f/3050/c/205/s/33b675a3/sc/11/rc/2/rc.htm\"><img src=\"http://da.feedsportal.com/r/180264259429/u/89/f/3050/c/205/s/33b675a3/sc/11/rc/2/rc.img\" border=\"0\"/></a><br/><a href=\"http://da.feedsportal.com/r/180264259429/u/89/f/3050/c/205/s/33b675a3/sc/11/rc/3/rc.htm\"><img src=\"http://da.feedsportal.com/r/180264259429/u/89/f/3050/c/205/s/33b675a3/sc/11/rc/3/rc.img\" border=\"0\"/></a><br/><br/><a href=\"http://da.feedsportal.com/r/180264259429/u/89/f/3050/c/205/s/33b675a3/a2.htm\"><img src=\"http://da.feedsportal.com/r/180264259429/u/89/f/3050/c/205/s/33b675a3/a2.img\" border=\"0\"/></a><img width=\"1\" height=\"1\" src=\"http://pi.feedsportal.com/r/180264259429/u/89/f/3050/c/205/s/33b675a3/a2t.img\" border=\"0\"/>\"";

        String s1Parse = Jsoup.parse(s1).text();
        String s2Parse = Jsoup.parse(s2).text();

        System.out.println(s1Parse);
        System.out.println(s2Parse);
        if (s1Parse.equals(s2Parse)) {
            System.out.println("Equals");
        }




    }
}
