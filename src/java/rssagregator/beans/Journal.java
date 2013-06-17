package rssagregator.beans;

import java.util.List;
import java.util.TimeZone;
import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Un journal : Le monde, le Figaro... Chaque journal est instancier dans un
 * objet. Un journal peut contenir plusieurs flux
 */
@Entity
@Cacheable(value = true)
public class Journal extends Bean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    /**
     * Le nom du journal : Le monde par exemple
     */
    @Column(name = "nom", unique = true)
    private String nom;
    /**
     * Le fuseaeu horraire. Cette information est importante pour effectuer des
     * calculs de date. Dans la table item, on enregistre la date de reception.
     * En connaissant le fuseau horraire du journal on peut calculer une
     * nouvelle date afin d'approcher d'estimer la date d'émission. Cette
     * information sur la date doit être comparée à la date d'émission trouvé
     * dans les balises XML
     */
    @Column(name = "fuseauHorraire")
    private String fuseauHorraire;
    /**
     * La langue d'écriture du journal
     */
    @Column(name = "langue", length = 4)
    private String langue;
    
    
    @Column(name = "pays", length = 60)
    private String pays;
    
    /**
     * Un journal possède plusieurs flux.
     */
    @OneToMany(mappedBy = "journalLie", fetch = FetchType.LAZY)
    private List<Flux> fluxLie;

    public Journal() {
        this.fluxLie = new ArrayList<Flux>();
    }
 
    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getFuseauHorraire() {
        
        return fuseauHorraire;
    }

    public void setFuseauHorraire(String fuseauHorraire) {
        this.fuseauHorraire = fuseauHorraire;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public List<Flux> getFluxLie() {
        return fluxLie;
    }

    public void setFluxLie(List<Flux> fluxLie) {
        this.fluxLie = fluxLie;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    
    
    
    //    @Override
    //    public boolean equals(Object obj) {
    //        return false;
    //    }
    //    @Override
    //    public boolean equals(Object obj) {
    //
    //int h1 = this.hashCode();
    //int h2 = obj.hashCode();
    //
    //
    //obj.hashCode();
    //        Journal jc = (Journal) obj;
    //        if(this.nom.equals(jc.nom)){
    //            return false;
    //        }
    //
    //
    //                return true;
    ////        if(!this.langue.equals(jc.langue))
    ////            return false;
    ////
    ////
    ////
    ////        return true;
    //    }

   

    public static void main(String[] args) {
        Journal j1 = new Journal();
        j1.setNom("j1");
        Journal j2 = new Journal();
        j2.setNom("j1");

        if (j1.compareBeans(j2, false)) {
            System.out.println("egal");
        } else {
            System.out.println("innegal");
        }

    }
}