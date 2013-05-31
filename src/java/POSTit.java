
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

