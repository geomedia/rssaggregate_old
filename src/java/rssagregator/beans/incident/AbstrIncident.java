//package rssagregator.beans.incident;
//
//import java.io.Serializable;
//import java.util.Date;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Inheritance;
//import javax.persistence.InheritanceType;
//import javax.persistence.Temporal;
//
////@Entity(name = "incident")
////@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Peu de champs supplémentaires dans les autres entités, on va conserver la stratégie la plus simple
//public class AbstrIncident implements Serializable {
//
//
//
//    public void AbstrIncident() {
//    }
//
//    /**
//     * Lorsque l'incident est constaté (création ou incrémentation du compteur
//     * nombreTentativeEnEchec), cette méthode est lancée. En cas d'erreur http,
//     * il est par exemple souhaitable de relancer une fois la récupération.
//     */
//    public void action() {
//    }
//
//  
//    
//}