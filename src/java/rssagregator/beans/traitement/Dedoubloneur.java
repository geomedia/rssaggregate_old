package rssagregator.beans.traitement;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import org.apache.log4j.Logger;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;

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

    
    @Override
    List<Item> dedoublonne(List<Item> listItemCapture, Flux flux) {

        // C'est la liste retour qui sera retouné. On commence par copier toutes les références présente dans la listItemCapture
        int i = 0;
        int j = 0;
        List<Item> listRetour = new ArrayList<Item>();
        listRetour.addAll(listItemCapture);
        compteCapture[0]= listItemCapture.size();

        // dédoublonnage basé sur les hash en mémoire.
        for (i = 0; i < flux.getLastEmpruntes().size(); i++) {
            String umprunteItemDsFlux = flux.getLastEmpruntes().get(i);

            for (j = 0; j < listItemCapture.size(); j++) {
                Item item = listItemCapture.get(j);
                if (item.getHashContenu().equals(umprunteItemDsFlux)) {
                    listRetour.remove(item);
                    compteCapture[1]++;
                }
            }
        }

        // Si il reste encore des items, on pocède à une vérification à partir de la BASE DE DONNEE
        if (listRetour.size() > 0) {
            

//            System.out.println("DEDOUBLONNEUR : dédoublonage BDD du flux ID : " + flux.getID()+ ". Il y a : " + listRetour.size()+" item à vérifier");
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
                                compteCapture[2]++;
//                                System.out.println("DEDOUB : Suppression d'une Item car elle déjà lié au flux");
                                listRetour.remove(itemRetour);
                                trouve = true;
                            }
                        }
                        // L'item analysé existe dans la base de donnée mais n'est pas encore lié à notre flux. Il faut remplacer par l'item trouvé dans la base de donnée (concevation de  l'id et des paticulaité de l'item existant)
                        if (!trouve) {
                            listRetour.add(j, ItemBdd);
//                            ItemBdd.setIsNew(false);
                            listRetour.remove(itemRetour);
                            compteCapture[3]++;
                            
                        }
                    }
                }
            }
        }
         compteCapture[4] = listRetour.size();
        logger.debug("Item trouvé : " + compteCapture[0]+ "; Dedoub mémoire : "+ compteCapture[1]+"; BDD déjà lié : "+ compteCapture[2]+"; "+ "BDD lien crée : " + compteCapture[3]+ "Total nouv item : "+ compteCapture[4]);
       
        return listRetour;
    }
}