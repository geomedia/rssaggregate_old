/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import rssagregator.beans.ContentRSS;
import rssagregator.beans.DoublonDe;
import rssagregator.beans.Item;

/**
 *
 * Un processus de Raffinage assez basique. Il compare les titre description et lien et prend une décision en fonction
 * de la méthode equal.
 *
 * @author clem
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class RaffineurSimpleImplementation extends AbstrRaffineur {

    @Override
    public void raffinerContenu(ContentRSS i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void findSimilarIteminBDD(EntityManager em) {


        String titre = itemObserve.getTitre();
        String guid = itemObserve.getGuid();
        String link = itemObserve.getLink();
        String hash = itemObserve.getHashContenu();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> cq = cb.createQuery(Item.class);
        Root root = cq.from(Item.class);
//        root.fetch("itemRaffinee");

        Predicate where = null;

        if (titre != null && !titre.isEmpty()) {
            if (where == null) {
                where = cb.like(root.get("titre"), "" + titre + "");
            } else {
                where = cb.or(where, cb.like(root.get("titre"), "" + titre + ""));
            }
        }

        if (guid != null && !guid.isEmpty()) {
            if (where == null) {
                where = cb.equal(root.get("guid"), guid);
            } else {
                where = cb.or(where, cb.equal(root.get("guid"), guid));
            }
        }
//                
        if (link != null && !link.isEmpty()) {
            if (where == null) {
                where = cb.equal(root.get("link"), link);
            } else {
                where = cb.or(where, cb.equal(root.get("link"), link));
            }
        }

        /***
         * Restriction a une liste de flux 
         */
        if (flux != null && !flux.isEmpty()) {
            Join join = root.join("listFlux");

            if (where == null) {
                where = join.in(flux);
            } else {
                where = cb.and(where, join.in(flux));
               
            }
        }



//        // Exclusion de l'item de la recherche
//        if (where == null) {
//            cb.notEqual(root.get("ID"), itemObserve.getID());
//        } else {
//            cb.and(cb.notEqual(root.get("ID"), itemObserve.getID()));
//        }

        cq.where(where);

        TypedQuery<Item> tq = em.createQuery(cq);
        List<Item> resu;
        resu = tq.getResultList();
        resu.remove(itemObserve);

        //---------------------AJOUT DES ITEMS A LA LISTE DES ITEM COMPARE--------------------
        if (!resu.isEmpty()) {
            for (int i = 0; i < resu.size(); i++) {
                Item item = resu.get(i);

                ComparaisonItem comparaisonItem = new ComparaisonItem();
                comparaisonItem.setItemComparee(itemObserve);
                comparaisonItem.setItemReference(item);
                listComparaison.add(comparaisonItem);
            }
        }
    }

    @Override
    protected void compareSimilarAndSort() {
        //-------------Calcul des points

        for (int i = 0; i < listComparaison.size(); i++) {
            ComparaisonItem comparaisonItem = listComparaison.get(i);
            //---> Ici l'algo qui permet d'attribuer des points
            comparaisonItem.evaluer(this);
        }

        //--------------Trie de la liste
        comparator = new ItemComparaisonComparator(this);

        Collections.sort(listComparaison, comparator);
    }

    @Override
    protected void decide() {



        // On observe le premier item de la liste
        if (!listComparaison.isEmpty()) { // Si il a un score supérieur a 90 
            ComparaisonItem first = listComparaison.get(0);
            if (first.getScore() >= 90) {
                Item itemReference = first.getItemReference();

                itemObserve.addDoublon(itemReference, this); // On ajoute le doublon a l'item observé.
            } else {
                itemObserve.addDoublon(itemObserve, this);
            }
        } else { // Sinon pas de doublon 
            itemObserve.addDoublon(itemObserve, this);
        }
    }

    /**
     * *
     * On va surement créer un autre en se basant sur une librarie tel que http://sourceforge.net/projects/simmetrics/
     *
     * @param o1
     * @param o2
     * @return
     */
    @Override
    protected float evaluerSimilitudeItem(Item o1, Item o2) {
        /**
         * *
         * Les statut
         *
         * -1 différent 0 nineploitable 1 semblable 2 equal
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


        // On exclu les item dont le titre OU l'item ne sont pas exploitable
        if (titre == 0 || link == 0) {
            return 0; // Le comparator dit que les articles proviennent d'article different pour éviter de se tromper
        }

        if (guid == 2 && titre == 2 && desc == 2 && link == 2 && date == 2) {
            return 100; // strictement identique
        }

        if (guid > 0 && link > 0 && titre > 0) { // MM guid et mm lien m titre
            return 90;
        }

        if (titre > 0 && desc > 0) {
            return 90;
        }

        if (titre > 0 && link > 0) { // Si le titre et le lien sont ~=
            return 90;
        }

        if (desc > 0 && link > 0) { // desc et lien ~=
            return 90;
        }

        if (titre > 0 && guid > 0) { // titre gui ~=
            return 90;
        }

        if (link > 0 && guid > 0 && titre != 0) { //lien et guid == et titre exploitable
            return 90;
        }
        return 0; // tout autre cas il retourne -1
    }

    /**
     * *
     * Permet de faire ressortir les item possédant une
     */
    public class comparatorNomine implements Comparator<ComparaisonItem> {

        AbstrRaffineur raffineur;

        @Override
        public int compare(ComparaisonItem o1, ComparaisonItem o2) {

            Item itemRef1 = o1.getItemReference();
            Item itemRef2 = o2.getItemReference();


            DoublonDe doublonDe1 = itemRef1.returnDoublonforRaffineur(raffineur);
            DoublonDe doublonDe2 = itemRef2.returnDoublonforRaffineur(raffineur);

            if (doublonDe1.isOriginal() && !doublonDe2.isOriginal()) {
                return -1;
            } else if (!doublonDe1.isOriginal() && doublonDe2.isOriginal()) {
                return 1;
            } else if (doublonDe1.isOriginal() && doublonDe2.isOriginal()) {
                return 0;
            }
//            System.out.println("----> ");
//            return 0;

//            DoublonDe doublonDe1 = o1.get
//            
//            if(o1.getItemReference().returnDoublonforRaffineur(raffineur))



            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
