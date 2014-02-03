/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.traitement.ComportementCollecte;

/**
 *
 * La DAO permettant d'intérragir avec la base de données pour des beans de type {@link ComportementCollecte}
 *
 * @author clem
 */
public class DAOComportementCollecte extends AbstrDao {

    /**
     * *
     * Le constructeur par défaut de la dao
     *
     * @param f
     */
    public DAOComportementCollecte(DAOFactory f) {
        this.dAOFactory = f;
        this.em = f.getEntityManager();
        this.classAssocie = ComportementCollecte.class;
    }

    /**
     * *
     * Permet de un {@link ComportementCollecte} dans la base de données. Utilise la méthode eponyme de la classe
     * {@link AbstrDao} et ensuite configure le comportement par défault (variable
     *
     * @Beta)
     * @param obj Le beans de type {@link ComportementCollecte} a créer
     * @throws Exception
     */
    @Override
    public void creer(Object obj) throws Exception {
        super.creer(obj);
        configDefault((ComportementCollecte) obj);

    }

    /**
     * *
     * Permet de modifier un {@link ComportementCollecte} dans la base de données. Utilise la méthode eponyme de la
     * classe {@link AbstrDao} et ensuite configure le comportement par défault (variable
     *
     * @Beta)
     * @param obj Le beans de type {@link ComportementCollecte} à modifier
     * @throws Exception
     */
    @Override
    public void modifier(Object obj) throws Exception {
        super.modifier(obj); //To change body of generated methods, choose Tools | Templates.

//        ComportementCollecte objMed = (ComportementCollecte) obj;
        // Si le comportment modifié est comportement par défaut, il faut vérifier que les autres comportement du système ne sont pas eux aussi comportement par defaut
        configDefault((ComportementCollecte) obj);
    }

    /**
     * *
     * Si le comportement envoyé est dit par defaut, la méthode parcours l'ensemble des autres comportement pour leur
     * enlever cette particularitée
     *
     * @param mediator : le comportement de capture qui vient d'être configuré comme par défaut
     */
    public void configDefault(ComportementCollecte mediator) {
        if (mediator.getDefaut()) {
            List<Object> listMed = findall();
            int i;
            for (i = 0; i < listMed.size(); i++) {
                ComportementCollecte med = (ComportementCollecte) listMed.get(i);
                if (med.getDefaut() && med.getID() != mediator.getID()) {
                    med.setDefaut(false);
                    try {
                        modifier(med);
                    } catch (Exception ex) {
                        Logger.getLogger(DAOComportementCollecte.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
