/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.beans.Conf;
import rssagregator.beans.form.DAOGenerique;

/**
 *
 * @author clem
 */
public class DAOConf extends AbstrDao {

    private Conf confCourante;

    public DAOConf(DAOFactory dAOFactory) {

        this.dAOFactory = dAOFactory;
        this.classAssocie = Conf.class;
    }

    public Conf getConfCourante() {
        return confCourante;
    }

    public void setConfCourante(Conf confCourante) {
        this.confCourante = confCourante;
    }
    
        /**
     * *
     * Modifi le statut Change de L'observable.
     */
    public void forceChange() {
        this.setChanged();
    }
    
        /**
     * *
     * Charge la config courante depuis la BDD. Cette méthode doit être lancée au démarrage de l'application (servlet start)
     */
      public void chargerDepuisBd() {

        //Chargement de la config depuis la base de donnée
        DAOGenerique dao = DAOFactory.getInstance().getDAOGenerique();
        dao.setClassAssocie(Conf.class);
        List<Object> confs = dao.findall();
        if (confs != null && confs.size() > 0) {
            Conf conf = (Conf) confs.get(0);
            this.confCourante = conf;
        } else {
            Conf defaultconf = new Conf();
            defaultconf.setActive(Boolean.TRUE);
            defaultconf.setNbThreadRecup(5);
            this.confCourante = defaultconf;
            try {
                dao.creer(confCourante);
            } catch (Exception ex) {
                Logger.getLogger(DAOConf.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      }
      
          /**
     * *
     * Enregistre la conf courante dans la base de donnée en utilisant ue DAO
     * générique; la mofification est ensuite motifiée aux observeurs.
     *
     * @param conf
     */
    public void modifierConf(Conf conf) throws Exception{ 
        AbstrDao dao = DAOFactory.getInstance().getDAOGenerique();
        dao.setClassAssocie(Conf.class);

         em = dAOFactory.getEntityManager();
                em.getTransaction().begin();
                em.merge(conf);
                em.getTransaction().commit();
        forceChange();

        notifyObservers();
    }
    
}
