/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.dao;

import java.util.List;
import javax.persistence.Query;
import rssagregator.beans.UserAccount;

/**
 *
 * @author clem
 */
public class DAOUser extends AbstrDao {

    public DAOUser(DAOFactory daof) {
        this.dAOFactory = daof;
        this.classAssocie = UserAccount.class;
        this.em = daof.getEntityManager();
    }

    /**
     * *
     * Recherche l'utilisateur en se basant sur le mail envoyé en argument
     *
     * @param m Le beans utilisateur ou null si aucun résultat
     * @return
     */
    public UserAccount findPrMail(String m) {
//        String req = ;
        Query query = em.createQuery("SELECT u FROM UserAccount u WHERE LOWER( u.mail) = :mail");
        query.setParameter("mail", m.toLowerCase());

        try {
            Object r = query.getSingleResult();
          
            return (UserAccount) r;
        } catch (Exception e) {
        }
        return null;
    }

    /***
     * Une méthode qui retourne la liste des utilisateur administrateur et devant recevoir les mails de notification
     * @return 
     */
    public List<UserAccount> findUserANotifier() {
        String req = "SELECT u FROM UserAccount u  WHERE u.adminMail=true and u.adminstatut=true";
        Query query = em.createQuery(req);
        return query.getResultList();
    }
    public static void main(String[] args) {
        DAOUser dao = DAOFactory.getInstance().getDAOUser();
        List<UserAccount> list = dao.findUserANotifier();
        for (int i = 0; i < list.size(); i++) {
            UserAccount userAccount = list.get(i);
            System.out.println(""+userAccount);
        }
    }

    public UserAccount findPrUsernamel(String s) {
        //        String req = ;
        Query query = em.createQuery("SELECT u FROM UserAccount u WHERE LOWER(u.username) = :username");
        query.setParameter("username", s.toLowerCase());

        try {
            Object r = query.getSingleResult();
          
            return (UserAccount) r;
        } catch (Exception e) {
        }
        return null;
    }
}
