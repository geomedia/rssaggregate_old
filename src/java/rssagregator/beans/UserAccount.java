/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.tomcat.util.buf.HexUtils;

/**
 * Classe permettant de générer les utilisateur du système
 *
 * @author clem
 */
@Entity
public class UserAccount implements Serializable, BeanSynchronise{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    @Column(name = "mail")
    private String mail;
    /**
     * *
     * Mot de passe stocké en md5
     */
    @Column(name = "pass")
    private String pass;
    @Column(name = "adminstatut")
    private Boolean adminstatut;
    
    
    private String username;
    /***
     * Boolean qui permet de déterminer si ce compte doit recevoir les mails d'alerte du système.
     */
    private Boolean adminMail;
    

    public UserAccount() {
        adminstatut = Boolean.FALSE;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Boolean getAdminstatut() {
        return adminstatut;
    }

    public void setAdminstatut(Boolean adminstatut) {
        this.adminstatut = adminstatut;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getAdminMail() {
        return adminMail;
    }

    public void setAdminMail(Boolean adminMail) {
        this.adminMail = adminMail;
    }
    
    
    
    

    /**
     * *
     * Permet de définir un mdp en la chiffrant
     *
     * @param entre chaine non chiffrée
     */
    public void setEncPassword(String entre) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.reset();
        byte[] hash = digest.digest(entre.getBytes());
        String hashString = new String(HexUtils.toHexString(hash));
        this.setPass(hashString);
    }

    /**
     * *
     * Test si le mot de passe envoyé en argument correspond avec l'utilisateur
     *
     * @param pTest mot de passe a envoyé non chiffré
     * @return Bool
     */
    public Boolean authWithThisPass(String pTest) throws NoSuchAlgorithmException {
        if (pTest != null && !pTest.isEmpty()) {
            //On commence par chiffrer le mot de passe envoyé en argumeent 
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();
            byte[] hash = digest.digest(pTest.getBytes());
            String enc = new String(HexUtils.toHexString(hash));

            if (this.pass != null && this.pass.equals(enc)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    //TODO : Revoir la methode equal et notamment hash
    //    @Override
    //    public boolean equals(Object obj) {
    //        if (obj instanceof UserAccount) {
    //            UserAccount u2 = (UserAccount) obj;
    //            if ((this.adminstatut != u2.adminstatut) || (this.mail.equals(u2.mail)) || (this.pass.equals(u2.pass)) || (this.username.equals(u2.username))) {
    //                return false;
    //            }
    //            else{
    //                return true;
    //            }
    //        } else {
    //            return false;
    //        }
    //    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.mail != null ? this.mail.hashCode() : 0);
        hash = 97 * hash + (this.pass != null ? this.pass.hashCode() : 0);
        hash = 97 * hash + (this.adminstatut != null ? this.adminstatut.hashCode() : 0);
        hash = 97 * hash + (this.username != null ? this.username.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserAccount other = (UserAccount) obj;
        if ((this.mail == null) ? (other.mail != null) : !this.mail.equals(other.mail)) {
            return false;
        }
        if ((this.pass == null) ? (other.pass != null) : !this.pass.equals(other.pass)) {
            return false;
        }
        if (this.adminstatut != other.adminstatut && (this.adminstatut == null || !this.adminstatut.equals(other.adminstatut))) {
            return false;
        }
        if ((this.username == null) ? (other.username != null) : !this.username.equals(other.username)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if(username != null && !username.isEmpty()){
            return username;
        }
//        if((mail==null || mail.isEmpty()) || (username!=null && username.isEmpty())){
//            return username;
//        }
        else if(mail!=null && !mail.isEmpty()){
            return mail;
        }
        else {
            return "??";
        }
    }
    
    
    
    
}
