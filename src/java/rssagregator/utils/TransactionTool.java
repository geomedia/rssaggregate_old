/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;

/**
 *
 * @author clem
 */
public class TransactionTool {

    /**
     * *
     * Commit ou roolback la transaction en vérifiant l'existance d'une transaction
     *
     * @param em l'em pour lequel il faut agir
     * @param comit true si on veut commiter false pour roolbacker
     */
    public static void commitRollBackIfPossible(EntityManager em, boolean comit) {
        checkEmTransaction(em);// argumentNonNull(em);
            if (comit) {
                em.getTransaction().commit();
            } else {
                em.getTransaction().rollback();
            }
    }
    
        /***
     * Verifie si l'em possède bien une transaction
     * @param em
     * @throws TransactionRequiredException 
     */
    public static void checkEmTransaction(EntityManager em) throws TransactionRequiredException{
        if(em == null){
            throw new NullPointerException("L'Entity Manager est null");
        }
        
        if(!em.isJoinedToTransaction()){
            throw new TransactionRequiredException("La transaction n'est pas active");
        }
    }
}
