
import rssagregator.beans.Flux;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author clem
 */
public class POSTit {
    //TODO : Utiliser postgre à la plac de MySQL
    //TODO : Qui porte la relation item ou flux.; Faire le point sur le lazy load. il n'est pas utile de charger toute les items d'un flux pour une simple mise à jour.
    //TODO : faire le point sur la cascading et autre dans les relation many to many
    
    public static void main(String[] args) {
        
        System.out.println("enum : " + Langage.JAVA);
    }
    
public enum Langage {
    
JAVA("Langage JAVA", 
        "Eclipse"),
  C ("Lanage C", "Code Block"),
  CPlus ("Langage C++", "Visual studio"),
  PHP ("Langage PHP", "PS Pad");
    private String name = "";
  private String editor = "";
  
   Langage(String name, String editor){
    this.name = name;
    this.editor = editor;
  }
     public void getEditor(){
    System.out.println("Editeur : " + editor);
  }
    
  public String toString(){
    return name;
  }
}
}

