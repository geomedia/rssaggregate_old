package rssagregator.beans.traitement;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.persistence.Entity;
import org.apache.log4j.Logger;
import rssagregator.beans.DonneeBrute;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;
import rssagregator.services.ServiceCollecteur;

@Entity(name = "Dedoubloneur")
public class Dedoubloneur extends AbstrDedoublonneur {

    private static String description = "le dédoublonneur permet .....";

    public Dedoubloneur() {
        super();
        logger = Logger.getLogger(Dedoubloneur.class);
    }

    public String getDescription() {
        return description;
    }

    /**
     * *
     * Supprime de la liste des item envoyé en arguments les items déjà présent dans la base. Si l'item existe déjà dans
     * la base mais n'est pas lié au flux envoyé en argument, la liaison est crée avec l'objet provenant de la base
     *
     * @param listItemCapture
     * @param flux
     * @return
     */
    @Override
    public List<Item> dedoublonne(List<Item> listItemCapture, Flux flux) {

        compteCapture[0] = listItemCapture.size(); // première case du tableau de capture : nombre d'item capturés dans le flux

        //======================================================================================
        //..................................DEDOUBLONNAGE MÉMOIRE
        //======================================================================================
        /**
         * Chaque flux concerve les hash des dernières items capturées dans une list (transitien donc non persisté).
         * Dans ce premier bloc on va comparer les hash des dernière items à ces hash en mémoire (temps d'accès beaucoup
         * plus rapide que la BDD)
         */
//        Set<String> listLastEmprunte = flux.getLastEmpruntes();
        Set<String> listLastEmprunte = ServiceCollecteur.getInstance().getCacheHashFlux().returnLashHash(flux);
//        System.out.println("TAILLER DU SET : " + listLastEmprunte.size());
        if (listLastEmprunte != null) {
            for (Iterator<String> it = listLastEmprunte.iterator(); it.hasNext();) {
                String hashMemoire = it.next();
                // On parcours toutes les items capturée.
                for (Iterator<Item> it1 = listItemCapture.iterator(); it1.hasNext();) {
                    Item itemCapture = it1.next();
                    if (itemCapture.getHashContenu().equals(hashMemoire)) {
                        it1.remove();
                        compteCapture[1]++;
                        mediatorAReferer.nbDedoubMemoire++;
                    }
                }
            }
        }



        // On commence par vérifier que deux items capturée n'ont pas le même hash. Certain journaux fournissent des flux avec plusieurs fois le même items...
//        for (Iterator<Item> it = listItemCapture.iterator(); it.hasNext();) {
//            Item item1 = it.next();
//            int cpt = 0;
//            for (int i = 0; i < listItemCapture.size(); i++) {
//                Item item = listItemCapture.get(i);
//                if (item.getHashContenu().equals(item1.getHashContenu())) {
//                    cpt++;
//                }
//            }
//            if (cpt > 1) {
//                it.remove();
//            }
//
//        }
        this.dedoublonnageInterneduneListDItem(listItemCapture, true, false, false);

        //On désactive
//        if (false) {


        //==========================================================================================
        //. . . . . . . . . . . . . . . . DEDOUBLONNAGE BDD
        //==========================================================================================
        /**
         * Dans ce block on va chercher les hashs des items restante dans la base de données. Si on obtien des résultats
         * il faut alors observer si l'item est déjà lié au flux (dans ce cas l'item capturé doit être retirée de la
         * liste) ou si l'item existe dans le flux mais n'est pas lié au flux observé (dans ce cas la liaison doit être
         * effectuée)
         */
        // 
        if (listItemCapture.size() > 0) { //Si il reste encore des items dans la liste, on procède à la vérification.



            //--------> Formulation de la requète
            DaoItem dao = DAOFactory.getInstance().getDaoItem();



            // Pour chaque item
            for (ListIterator<Item> it = listItemCapture.listIterator(); it.hasNext();) {
                Item itemCapture = it.next();


                Item ItemBDD = dao.findItemAvecHashDansDonneSource(itemCapture.getHashContenu());

                //Si on a trouvé une item
                if (ItemBDD != null) {
                    System.out.println("--> ITEM BDD");
                    //Si l'item BDD posséde déjà le flux observé
                    boolean present = fluxPresentDansList(ItemBDD.getListFlux(), flux);
                    if (present) {
                        // On parcours tout les donnes brutes de l'item BDD

                        boolean trouve = false;
                        for (int i = 0; i < ItemBDD.getDonneeBrutes().size(); i++) {
                            DonneeBrute donneBruteBDD = ItemBDD.getDonneeBrutes().get(i);

                            if (donneBruteBDD.getHashContenu().equals(itemCapture.getHashContenu()) && donneBruteBDD.getFlux().getID().equals(flux.getID())) {
                                trouve = true;
                            }

                        }
                        if (trouve) {
                            it.remove();
                            mediatorAReferer.nbDedoubBdd++;
//                            ServiceCollecteur.getInstance().getCacheHashFlux().addHash(flux, ItemBDD.getHashContenu());
                            ServiceCollecteur.getInstance().getCacheHashFlux().addAllHashDeLItem(itemCapture, flux); //addHash(flux, ItemBDD.getHashContenu());
                        } else {
                            
                            ItemBDD.verserLesDonneeBruteAutreItem(itemCapture);
                            it.set(ItemBDD);
                        }

                    } else {
                        ItemBDD.verserLesDonneeBruteAutreItem(itemCapture);
                        it.set(ItemBDD);
                    }
                }
            }
















//                //Construction de la liste des hash a envoyé à la dao.
//                int z;
//                String hashParamSQL = "";
//                for (z = 0; z < listItemCapture.size(); z++) {
//                    hashParamSQL += "'" + listItemCapture.get(z).getHashContenu() + "', ";
//                }
//                if (hashParamSQL.length() > 2) {
//                    hashParamSQL = hashParamSQL.substring(0, hashParamSQL.length() - 2);
//                }
//                List<Item> itemBDD = dao.findHashFlux(hashParamSQL); // Il s'agit de la liste des item possédant les hash
//
//
//                //------> Parcours des items trouvée dans la base de donnée.
//                for (int i = 0; i < itemBDD.size(); i++) {
//                    Item ItemBdd = itemBDD.get(i);
//                    for (ListIterator<Item> it = listItemCapture.listIterator(); it.hasNext();) {
//                        Item itemCapture = it.next();
//                        //Si les hash entre item dans la base et item trouvée sont similaire
//                        if (ItemBdd.getHashContenu().equals(itemCapture.getHashContenu())) {
//
//                            //----------> SUPPRESSION DE L'ITEM COLLECTE SI ELLE EST DEJÀ LIÉE AU FLUX OBSERVÉ
//                            // On récupère les id des flux des items présente dans la BDD
//                            List<Flux> listfluxItemBDD = ItemBdd.getListFlux();
//                            int k;
//                            boolean trouve = false;
//                            for (k = 0; k < listfluxItemBDD.size(); k++) {
//                                // SI l'item courante possède la même id que dans la base de donnée, on supprime de la liste courante
//                                if (listfluxItemBDD.get(k).getID().equals(flux.getID())) {
//                                    compteCapture[2]++;
//                                    // On supprimer l'item de la liste a ajouté et on ajoute le hash au cache pour un dédoublonnage plus efficace a l'avenir
//                                    it.remove();
////                                ServiceCollecteur.getInstance().getCacheHashFlux().addHash(flux, itemRetour.getHashContenu());
//                                    trouve = true;
//                                    continue;
//
//                                }
//                            }
//                            //---------> AJOUT D'UNE LIAISON POUR L'ITEM TROUVÉ DANS LA BASE DE DONNÉE
//                            // L'item analysé existe dans la base de donnée mais n'est pas encore lié à notre flux. Il faut remplacer par l'item trouvé dans la base de donnée (concevation de  l'id et des paticulaité de l'item existant)
//                            if (!trouve) {
//                                ItemBdd.verserLesDonneeBruteAutreItem(itemCapture);
////                                itemRetour.verserLesDonneeBruteAutreItem(ItemBdd);
//                                it.set(ItemBdd);
//                                compteCapture[3]++;
//                            }
//                        }
//                    }
//                }

            //=========================================================================================================
            // . . . . . . . . . . . . DEDOUBLONNAGE PAR RAPPORT AUX COMPORTEMENTS DE COLLECTE ANCIENS
            //=========================================================================================================
            /**
             * Le comportement de collecte d'un flux peut être changé. Si un flux a possédé plusieurs comportement de
             * collecte, il est nécessaire de vérifier si l'item collecté ne correspond pas à à une item collecté à
             * l'aide d'un ancien comportement
             */
//                // -------> Récupétation des anciens comportement de collecte du flux.
//                List<FluxPeriodeCaptation> periodeCaptation = flux.getPeriodeCaptations();
//                HashSet<MediatorCollecteAction> comportementReleve = new HashSet<MediatorCollecteAction>(); // On crée un hashset pour être certain de n'ajouter qu'une fois le comportement
//                comportementReleve.add(flux.getMediatorFlux());
//                for (int k = 0; k < periodeCaptation.size(); k++) {
//                    FluxPeriodeCaptation fluxPeriodeCaptation = periodeCaptation.get(k);
//                    comportementReleve.add(fluxPeriodeCaptation.getComportementDurantLaPeriode());
//                }
//                comportementReleve.remove(flux.getMediatorFlux());
//
//
//                // ------------> SI on a plusieurs comportements
//                if (comportementReleve.size() > 0) {
//                    String concatHash = "";
//                    // Pour chaque item capturée (celles qui restent...)
//                    for (int k = 0; k < listItemCapture.size(); k++) {
//                        Item it = listItemCapture.get(k);
//                        // On calcul le hash avec l'ancien comportement de capture 
//
//                        for (Iterator<MediatorCollecteAction> it1 = comportementReleve.iterator(); it1.hasNext();) { // Pour chaque compotement relevé
//                            MediatorCollecteAction mediatorCollecteAction = it1.next();
//                            try {
//                                concatHash += "'" + mediatorCollecteAction.dedoubloneur.returnHash(it) + "', ";
//                            } catch (NoSuchAlgorithmException ex) {
//                                java.util.logging.Logger.getLogger(Dedoubloneur.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        }
//                    }
//                    if (concatHash.length() > 2) {
//                        concatHash = concatHash.substring(0, concatHash.length() - 2); // On retire simplement la chaine ", " a la fin
//
//                        // ------> REQUETE DE DEDOUBLE 
//                        List<Item> itemAncienHash = dao.findHashFlux(concatHash, null);
//                        // Si on en trouve 
//                        if (!itemAncienHash.isEmpty()) {
//                            // Il faut remplacer l'ancien par celui qui vient d'être trouvé.
//                            for (int k = 0; k < itemAncienHash.size(); k++) {
//                                Item itemBDDAncienHash = itemAncienHash.get(k);
//
//                                for (ListIterator<Item> it = listItemCapture.listIterator(); it.hasNext();) {
//                                    try {
//                                        Item itemCapture = it.next();
//                                        if (flux.getMediatorFlux().dedoubloneur.returnHash(itemBDDAncienHash).equals(itemCapture.getHashContenu())) {
//
//                                            //Si l'item a besoin d'être liée
//                                            if (!itemBDDAncienHash.getListFlux().contains(flux)) {
//                                                it.set(itemBDDAncienHash);
//                                            } //Sinon on la retire
//                                            else {
////                                            ServiceCollecteur.getInstance().getCacheHashFlux().addHash(flux, concatHash);
//                                                it.remove();
//                                            }
////                                        item = itemBDDAncienHash;
//                                        }
//                                    } catch (NoSuchAlgorithmException ex) {
//                                        java.util.logging.Logger.getLogger(Dedoubloneur.class.getName()).log(Level.SEVERE, null, ex);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
        }
//        }
        compteCapture[4] = listItemCapture.size();
        return listItemCapture;
    }
//    /***
//     * Cette méthode construit une chaine de cacartère comprenant tous les hash (utilisé pour formaliser la requête de dédoublonnage). En plus de collecté les hash propre a chacun des flux, elle va si le flux a déjà posséder différents 
//     * @param listItem
//     * @param flux
//     * @return 
//     */
//    public String construireChaineHash(List<Item> listItem, Flux flux){
//        
//        
//        return null;
//    }
}