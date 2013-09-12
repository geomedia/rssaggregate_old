/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import java.io.Serializable;
import javax.persistence.Entity;

/**
 * Cette entitée permet de stocker des informations sur les pannes serveur mail. 
 * @author clem
 */
@Entity(name = "i_mailincident")
public class MailIncident extends AbstrIncident implements Serializable{

    
    public MailIncident() {
    }
    
    
    /***
     * Le corps du message qui n'a pu être envoyé
     */
    protected String Message;
    /***
     * L'objet du message qui n'a pu être envoyé
     */
    protected String Objet;
    
    


    //    @OneToOne
    //    private Flux fluxLie;
    //
    //    @OneToOne
    //    private Journal journalLie;
    //
    //
    //    public Flux getFluxLie() {
    //        return fluxLie;
    //    }
    //
    //    public void setFluxLie(Flux fluxLie) {
    //        this.fluxLie = fluxLie;
    //    }





    @Override
    public int hashCode() {
        int hash = 7;
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
        final MailIncident other = (MailIncident) obj;
        
        if(!super.equals(obj)){
            return false;
        }
        return true;
    }


    


    public static void main(String[] args) {
        
        MailIncident s = new MailIncident();
        MailIncident s2 = new MailIncident();
        
//        s2.setLogErreur("dd");
        
        if(s.equals(s2)){
            System.out.println("EQUAL");
        }
        else{
            System.out.println("INNEQUAL");
        }
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getObjet() {
        return Objet;
    }

    public void setObjet(String Objet) {
        this.Objet = Objet;
    }
    
    

}
