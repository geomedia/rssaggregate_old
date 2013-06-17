/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoFlux;
import rssagregator.dao.DaoItem;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;

/**
 *
 * @author clem
 */
public abstract class AbstrDedoublonneur {

    /**
     * *
     * Test si l'on a déjà enregitré l'item.
     *
     * @param item
     * @param flux
     * @return True si l'item à déjà été enregistrée. False si l'item est
     * nouvelle et n'a pas encoe été taité
     */
    @Deprecated
    public Boolean testDoublonageMemoire(Item item, Flux flux) {
        int i = 0;
        // test basé sur les dernières emprunte en mémoire.
        while (i < flux.getLastEmpruntes().size()) {
            if (item.getHashContenu().equals(flux.getLastEmpruntes().get(i))) {
                return Boolean.TRUE;
            }
            i++;
        }

        return false;
//               // test sur la base de données.
//            
//            DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
//            Item itemTrouve = daoItem.findByHash(item.getHashContenu());
//            
//            if(itemTrouve == null){
//                return false;
//            }

    }

    @Deprecated
    public Boolean testDoublonageBDD(Item get, Flux flux) {
        // On test si on peu trouver une item possédant le hash pour le flux
        DaoItem dao = DAOFactory.getInstance().getDaoItem();
//        dao.findHashFlux(get.getHashContenu(), flux);


        return false;
    }

    List<Item> dedoublonne(List<Item> listItemCapture, Flux flux) {

        // C'est la liste retour qui sera retouné. On commence par copier toutes les références présente dans la listItemCapture
        int i = 0;
        int j = 0;
        List<Item> listRetour = new ArrayList<Item>();
        listRetour.addAll(listItemCapture);



        // dédoublonnage basé sur les hash en mémoire.
        for (i = 0; i < flux.getLastEmpruntes().size(); i++) {
            String umprunteItemDsFlux = flux.getLastEmpruntes().get(i);
            for (j = 0; j < listItemCapture.size(); j++) {
                Item item = listItemCapture.get(j);
                if (item.getHashContenu().equals(umprunteItemDsFlux)) {
                    listRetour.remove(item);
                    System.out.println("DEDOUBLONNEUR : SUPPRESSION d'un Flux après calcul HASH");

                }
            }
        }

        // Si il reste encore des items, on pocède à une vérification à partir de la BASE DE DONNEE
        if (listRetour.size() > 0) {
            System.out.println("DEDOUBLONNEUR : dédoublonage BDD du flux ID : " + flux.getID()+ ". Il y a : " + listRetour.size()+" item à vérifier");
            //Supression de toutes les items déjà lié au flux
            DaoItem dao = DAOFactory.getInstance().getDaoItem();
            List<Item> ListitemDejaPresenteBDD = dao.findHashFlux(listRetour, flux);
            for (i = 0; i < ListitemDejaPresenteBDD.size(); i++) {
                Item ItemBdd = ListitemDejaPresenteBDD.get(i);
                for (j = 0; j < listRetour.size(); j++) {
                    Item itemRetour = listRetour.get(j);

                    //Si les hash sont similaires
                    if (ItemBdd.getHashContenu().equals(itemRetour.getHashContenu())) {

                        //Il est nécéssaire de supprimer l'item du retour si il est déjà lié au flux analysé.
                        // On récupère les id des flux des items présente dans la BDD
                        List<Flux> listfluxItemBDD = ItemBdd.getListFlux();
                        int k;
                        boolean trouve = false;
                        for (k = 0; k < listfluxItemBDD.size(); k++) {

                            // SI l'item courante possède la même id que dans la base de donnée, on supprime de la liste courante
                            if (listfluxItemBDD.get(k).getID().equals(flux.getID())) {
                                System.out.println("DEDOUB : Suppression d'une Item car elle déjà lié au flux");
                                listRetour.remove(itemRetour);
                                trouve = true;
                            }
                        }
                        // L'item analysé existe dans la base de donnée mais n'est pas encore lié à notre flux. Il faut remplacer par l'item trouvé dans la base de donnée (concevation de  l'id et des paticulaité de l'item existant)
                        if (!trouve) {
                            listRetour.add(j, ItemBdd);
                            listRetour.remove(itemRetour);
                            System.out.println("DEDOUB : Lien réalisé entre une item déjà enregistrée et le flux observé");

                        }
                    }
                }
            }
        }
        return listRetour;
    }
}