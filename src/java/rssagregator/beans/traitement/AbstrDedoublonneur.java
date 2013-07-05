/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DaoItem;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.buf.HexUtils;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;

/**
 *
 * @author clem
 */
//@Entity()
@Entity
@Table(name =  "tr_dedoub")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstrDedoublonneur implements Serializable,Cloneable {

    public AbstrDedoublonneur() {
        // On initialise le tableau de compte capture
        compteCapture = new Integer[6];
        compteCapture[0] = 0;
        compteCapture[1] = 0;
        compteCapture[2] = 0;
        compteCapture[3] = 0;
        compteCapture[4] = 0;
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long ID;
    @Transient
    protected Logger logger = Logger.getLogger(AbstrDedoublonneur.class);
    @Column(name = "deboubTitle")
    private Boolean deboubTitle;
    @Column(name = "dedoubLink")
    private Boolean dedoubLink;
    @Column(name = "deboudDesc")
    private Boolean deboudDesc;
    @Column(name = "dedoubDatePub")
    private Boolean dedoubDatePub;
    @Column(name = "dedouGUID")
    private Boolean dedouGUID;
    @Column(name = "dedoubCategory")
    private Boolean dedoubCategory;
    
    /***
     * 1=nombre item trouvé ; 2 dedoub memoire; 3 BDD item lié ;4 BDD item déjà présente mais lien ajouté ;  5 item nouvelles
     */
    @Transient
    protected Integer[] compteCapture;
    

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
    }

    @Deprecated
    public Boolean testDoublonageBDD(Item get, Flux flux) {
        // On test si on peu trouver une item possédant le hash pour le flux
        DaoItem dao = DAOFactory.getInstance().getDaoItem();
//        dao.findHashFlux(get.getHashContenu(), flux);


        return false;
    }

    abstract List<Item> dedoublonne(List<Item> listItemCapture, Flux flux);
//    {
//
//        // C'est la liste retour qui sera retouné. On commence par copier toutes les références présente dans la listItemCapture
//        int i = 0;
//        int j = 0;
//        List<Item> listRetour = new ArrayList<Item>();
//        listRetour.addAll(listItemCapture);
//
//
//
//        // dédoublonnage basé sur les hash en mémoire.
//         int nbrdedoub =0; // sert juste à l'affichage dans les logs
//        for (i = 0; i < flux.getLastEmpruntes().size(); i++) {
//            String umprunteItemDsFlux = flux.getLastEmpruntes().get(i);
//           
//            for (j = 0; j < listItemCapture.size(); j++) {
//                Item item = listItemCapture.get(j);
//                if (item.getHashContenu().equals(umprunteItemDsFlux)) {
//                    listRetour.remove(item);
//                    nbrdedoub++;
////                    System.out.println("DEDOUBLONNEUR : SUPPRESSION d'un Flux après calcul HASH");
//                }
//            }
//        }
//        logger.debug("Flux "+flux.getID()+". Suppression de " + nbrdedoub+" par DedoubMemoire");
////        System.out.println("DEBOUBLONNEUR : Suppression de " + nbrdedoub+" par DedoubMemoire" );
//
//        // Si il reste encore des items, on pocède à une vérification à partir de la BASE DE DONNEE
//        if (listRetour.size() > 0) {
//            
//            logger.debug("Flux : "+flux.getID()+". Dédoublonage BDD pour " + listRetour.size()+" item à vérifier");
////            System.out.println("DEDOUBLONNEUR : dédoublonage BDD du flux ID : " + flux.getID()+ ". Il y a : " + listRetour.size()+" item à vérifier");
//            //Supression de toutes les items déjà lié au flux
//            DaoItem dao = DAOFactory.getInstance().getDaoItem();
//            List<Item> ListitemDejaPresenteBDD = dao.findHashFlux(listRetour, flux);
//            for (i = 0; i < ListitemDejaPresenteBDD.size(); i++) {
//                Item ItemBdd = ListitemDejaPresenteBDD.get(i);
//                for (j = 0; j < listRetour.size(); j++) {
//                    Item itemRetour = listRetour.get(j);
//
//                    //Si les hash sont similaires
//                    if (ItemBdd.getHashContenu().equals(itemRetour.getHashContenu())) {
//
//                        //Il est nécéssaire de supprimer l'item du retour si il est déjà lié au flux analysé.
//                        // On récupère les id des flux des items présente dans la BDD
//                        List<Flux> listfluxItemBDD = ItemBdd.getListFlux();
//                        int k;
//                        boolean trouve = false;
//                        for (k = 0; k < listfluxItemBDD.size(); k++) {
//
//                            // SI l'item courante possède la même id que dans la base de donnée, on supprime de la liste courante
//                            if (listfluxItemBDD.get(k).getID().equals(flux.getID())) {
//                                logger.debug("Flux : "+flux.getID()+". Suppression d'une Item car elle déjà lié au flux");
////                                System.out.println("DEDOUB : Suppression d'une Item car elle déjà lié au flux");
//                                listRetour.remove(itemRetour);
//                                trouve = true;
//                            }
//                        }
//                        // L'item analysé existe dans la base de donnée mais n'est pas encore lié à notre flux. Il faut remplacer par l'item trouvé dans la base de donnée (concevation de  l'id et des paticulaité de l'item existant)
//                        if (!trouve) {
//                            listRetour.add(j, ItemBdd);
//                            listRetour.remove(itemRetour);
//                            logger.debug("Flux : "+flux.getID()+". Lien réalisé entre une item déjà enregistrée et le flux observé");
////                            System.out.println("DEDOUB : Lien réalisé entre une item déjà enregistrée et le flux observé");
//
//                        }
//                    }
//                }
//            }
//        }
//        logger.debug("Nombre d'item après dédoub : " + listRetour.size());
//        return listRetour;
//    }

    public Boolean getDeboubTitle() {
        return deboubTitle;
    }

    public void setDeboubTitle(Boolean deboubTitle) {
        this.deboubTitle = deboubTitle;
    }

    public Boolean getDedoubLink() {
        return dedoubLink;
    }

    public void setDedoubLink(Boolean dedoubLink) {
        this.dedoubLink = dedoubLink;
    }

    public Boolean getDeboudDesc() {
        return deboudDesc;
    }

    public void setDeboudDesc(Boolean deboudDesc) {
        this.deboudDesc = deboudDesc;
    }

    public Boolean getDedoubDatePub() {
        return dedoubDatePub;
    }

    public void setDedoubDatePub(Boolean dedoubDatePub) {
        this.dedoubDatePub = dedoubDatePub;
    }

    public Boolean getDedouGUID() {
        return dedouGUID;
    }

    public void setDedouGUID(Boolean dedouGUID) {
        this.dedouGUID = dedouGUID;
    }

    public Boolean getDedoubCategory() {
        return dedoubCategory;
    }

    public void setDedoubCategory(Boolean dedoubCategory) {
        this.dedoubCategory = dedoubCategory;
    }

    public Integer[] getCompteCapture() {
        return compteCapture;
    }

    public void setCompteCapture(Integer[] compteCapture) {
        this.compteCapture = compteCapture;
    }
    
    

    /**
     * *
     * Calcul les hash pour la list des items envoyés en paramètre
     *
     * @param listItem
     */
    protected void calculHash(List<Item> listItem) throws NoSuchAlgorithmException {
        int i;

        for (i = 0; i < listItem.size(); i++) {
            String concat = "";
            Item item = listItem.get(i);


            if (this.deboubTitle && item.getTitre() != null) {
                concat += item.getTitre();
            }

            if (this.deboudDesc && item.getDescription() != null) {
                concat += item.getDescription();
            }


            if (this.dedouGUID && item.getDescription() != null) {
                concat += item.getGuid();
            }

            if (this.dedoubLink && item.getLink() != null) {
                concat += item.getLink();
            }


            if (this.dedoubDatePub && item.getDatePub() != null) {
                concat += item.getDatePub().toString();
            }

            if (this.dedoubCategory && item.getCategorie() != null) {
                concat += item.getCategorie();
            }


//                concat = item.getTitre() + item.getDescription();
            MessageDigest digest = MessageDigest.getInstance("MD5");

            digest.reset();

            byte[] hash = digest.digest(concat.getBytes());
            String hashString = new String(HexUtils.toHexString(hash));
            item.setHashContenu(hashString);
        }
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    @Override
    protected AbstrDedoublonneur clone() throws CloneNotSupportedException {
        AbstrDedoublonneur clone = (AbstrDedoublonneur) super.clone();
        clone.compteCapture = this.compteCapture.clone();
        
        return clone; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}