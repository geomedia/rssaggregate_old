/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

import java.io.Serializable;
import rssagregator.services.ServiceMailNotifier;
import rssagregator.services.tache.TacheEnvoyerMail;

/**
 * /!\ Cette classe N'est plus utilisé ca alourdi trop la base de données. Les mail qui ne partent pas sont maintenant stocké sur le disque dur du serveur
 * 
 * 
 * <p>Incident généré par le service {@link ServiceMailNotifier}, lorsqu'un mail
 * n'a pu être envoyé par la tâche {@link TacheEnvoyerMail}</p>
 * <p>Le corps du message et l'objet sont concervé dans cet incident</p>
 *
 * @author clem
 */
//@Entity(name = "i_mailincident")
public class ZZOLDMailIncident extends AbstrIncident implements Serializable {

//       public static final String desc = "Incident survenue lors de l'envoie d'un mail.";
//    
//    protected ZZOLDMailIncident() {
//    }
//
//    /**
//     * *
//     * Le corps du message qui n'a pu être envoyé
//     */
//    @Column (columnDefinition = "text")
//    protected String Message;
//    /**
//     * *
//     * L'objet du message qui n'a pu être envoyé
//     */
//    protected String Objet;
//
//
//    @Override
//    public int hashCode() {
//        int hash = 7;
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final ZZOLDMailIncident other = (ZZOLDMailIncident) obj;
//
//        if (!super.equals(obj)) {
//            return false;
//        }
//        return true;
//    }
//
//
//    public String getMessage() {
//        return Message;
//    }
//
//    public void setMessage(String Message) {
//        this.Message = Message;
//    }
//
//    public String getObjet() {
//        return Objet;
//    }
//
//    public void setObjet(String Objet) {
//        this.Objet = Objet;
//    }
//
//    /***
//     * Retourne toujours false. Une erreur d'envoie de mail ne doit jamais être envoyé par mail...
//     * @return 
//     */
//    @Override
//    public Boolean doitEtreNotifieParMail() {
//        return false;
//    }
}
