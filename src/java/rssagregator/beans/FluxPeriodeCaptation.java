/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.poi.util.Beta;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.utils.SqlDateAdapter;

/**
 * Cette entitée permet de stoquer un intervale de date permettant de renseigner la ou les périodes pendant lesquel le
 * flux a été capté. Un flux peut être capté de sa création à aujourd'hui mais aussi être retiré et remis en capture.
 * Exemple il est capté de du 01/01/2010 au 02/02/2012 puis sa capture reprend au 03/03/2013
 *
 * @author clem
 */
@Entity
@JsonFilter("serialisePourUtilisateur")
public class FluxPeriodeCaptation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;
    /**
     * *
     * Le flux concerné par cette période de captation
     */
//    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
//    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonIgnoreProperties
    @ManyToOne(optional = false)
    private Flux flux;
    
    
    private Long statSommeItemCapture;
    private Integer statQuartilePremier;
    private Integer statQuartileTrois;
    private Integer statDecilePremier;
    private Integer statDecileNeuf;
    private Integer statMedian;
    private Float statMoyenne;
    private Double statEcartType;
    private Integer statMin;
    private Integer statMax;
    
    
    
    private Float statMoyLundi;
    private Float statMoyMardi;
    private Float statMoyMercredi;
    private Float statMoyJeudi;
    private Float statMoyVendredi;
    private Float statMoySamedi;
    private Float statMoyDimanche;
    private Float statMedLundi;
    private Float statMedMardi;
    private Float statMedMercredi;
    private Float statMedJeudi;
    private Float statMedVendredi;
    private Float statMedSamedi;
    private Float statMedDimanche;
    private Double statEcartTypeLundi;
    private Double statEcartTypeMardi;
    private Double statEcartTypeMercredi;
    private Double statEcartTypeJeudi;
    private Double statEcartTypeVendredi;
    private Double statEcartTypeSamedi;
    private Double statEcartTypeDimanche;
//    private Float[] moyenneJour = new Float[7];
    /**
     * *
     * Il est important de concerver une trace du comportement de collecte utilisé durant cette période de captation.
     * Cette donnée est utilisée dans le dédoublonnage.
     */
    @Beta
    @OneToOne

//    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
//    @JsonIgnoreProperties
    private MediatorCollecteAction comportementDurantLaPeriode;

    /**
     * *
     * Constructeur par défault.
     */
    public FluxPeriodeCaptation() {
        
    }
    /**
     * *
     * Date de début de la période de captation du flux. Ce champs ne peut être null
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
//        @XmlJavaTypeAdapter(SqlDateAdapter.class)
    @Column(name = "dateDebut", nullable = false)
    private Date dateDebut;
    /**
     * *
     * Date de fin de la période de captation du flux. Peut être null si la période est encore ouverte
     */
//    @XmlJavaTypeAdapter(SqlDateAdapter.class)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date datefin;

    public Float getStatMoyLundi() {
        return statMoyLundi;
    }

    public void setStatMoyLundi(Float statMoyLundi) {
        this.statMoyLundi = statMoyLundi;
    }

    public Float getStatMoyMardi() {
        return statMoyMardi;
    }

    public void setStatMoyMardi(Float statMoyMardi) {
        this.statMoyMardi = statMoyMardi;
    }

    public Float getStatMoyMercredi() {
        return statMoyMercredi;
    }

    public void setStatMoyMercredi(Float statMoyMercredi) {
        this.statMoyMercredi = statMoyMercredi;
    }

    public Float getStatMoyJeudi() {
        return statMoyJeudi;
    }

    public void setStatMoyJeudi(Float statMoyJeudi) {
        this.statMoyJeudi = statMoyJeudi;
    }

    public Float getStatMoyVendredi() {
        return statMoyVendredi;
    }

    public void setStatMoyVendredi(Float statMoyVendredi) {
        this.statMoyVendredi = statMoyVendredi;
    }

    public Float getStatMoySamedi() {
        return statMoySamedi;
    }

    public void setStatMoySamedi(Float statMoySamedi) {
        this.statMoySamedi = statMoySamedi;
    }

    public Float getStatMoyDimanche() {
        return statMoyDimanche;
    }

    public void setStatMoyDimanche(Float statMoyDimanche) {
        this.statMoyDimanche = statMoyDimanche;
    }

    public Float getStatMedLundi() {
        return statMedLundi;
    }

    public void setStatMedLundi(Float statMedLundi) {
        this.statMedLundi = statMedLundi;
    }

    public Float getStatMedMardi() {
        return statMedMardi;
    }

    public void setStatMedMardi(Float statMedMardi) {
        this.statMedMardi = statMedMardi;
    }

    public Float getStatMedMercredi() {
        return statMedMercredi;
    }

    public void setStatMedMercredi(Float statMedMercredi) {
        this.statMedMercredi = statMedMercredi;
    }

    public Float getStatMedJeudi() {
        return statMedJeudi;
    }

    public void setStatMedJeudi(Float statMedJeudi) {
        this.statMedJeudi = statMedJeudi;
    }

    public Float getStatMedVendredi() {
        return statMedVendredi;
    }

    public void setStatMedVendredi(Float statMedVendredi) {
        this.statMedVendredi = statMedVendredi;
    }

    public Float getStatMedSamedi() {
        return statMedSamedi;
    }

    public void setStatMedSamedi(Float statMedSamedi) {
        this.statMedSamedi = statMedSamedi;
    }

    public Float getStatMedDimanche() {
        return statMedDimanche;
    }

    public void setStatMedDimanche(Float statMedDimanche) {
        this.statMedDimanche = statMedDimanche;
    }

    public Double getStatEcartTypeLundi() {
        return statEcartTypeLundi;
    }

    public void setStatEcartTypeLundi(Double statEcartTypeLundi) {
        this.statEcartTypeLundi = statEcartTypeLundi;
    }

    public Double getStatEcartTypeMardi() {
        return statEcartTypeMardi;
    }

    public void setStatEcartTypeMardi(Double statEcartTypeMardi) {
        this.statEcartTypeMardi = statEcartTypeMardi;
    }

    public Double getStatEcartTypeMercredi() {
        return statEcartTypeMercredi;
    }

    public void setStatEcartTypeMercredi(Double statEcartTypeMercredi) {
        this.statEcartTypeMercredi = statEcartTypeMercredi;
    }

    public Double getStatEcartTypeJeudi() {
        return statEcartTypeJeudi;
    }

    public void setStatEcartTypeJeudi(Double statEcartTypeJeudi) {
        this.statEcartTypeJeudi = statEcartTypeJeudi;
    }

    public Double getStatEcartTypeVendredi() {
        return statEcartTypeVendredi;
    }

    public void setStatEcartTypeVendredi(Double statEcartTypeVendredi) {
        this.statEcartTypeVendredi = statEcartTypeVendredi;
    }

    public Double getStatEcartTypeSamedi() {
        return statEcartTypeSamedi;
    }

    public void setStatEcartTypeSamedi(Double statEcartTypeSamedi) {
        this.statEcartTypeSamedi = statEcartTypeSamedi;
    }

    public Double getStatEcartTypeDimanche() {
        return statEcartTypeDimanche;
    }

    public void setStatEcartTypeDimanche(Double statEcartTypeDimanche) {
        this.statEcartTypeDimanche = statEcartTypeDimanche;
    }

    public Integer getStatMin() {
        return statMin;
    }

    public void setStatMin(Integer statMin) {
        this.statMin = statMin;
    }

    public Integer getStatMax() {
        return statMax;
    }

    public void setStatMax(Integer statMax) {
        this.statMax = statMax;
    }

    
    


    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDatefin() {
        return datefin;
    }

    public void setDatefin(Date datefin) {
        this.datefin = datefin;
    }

    public Flux getFlux() {
        return flux;
    }

    public void setFlux(Flux flux) {
        this.flux = flux;
    }

    public MediatorCollecteAction getComportementDurantLaPeriode() {
        return comportementDurantLaPeriode;
    }

    public void setComportementDurantLaPeriode(MediatorCollecteAction comportementDurantLaPeriode) {
        this.comportementDurantLaPeriode = comportementDurantLaPeriode;
    }

    public Long getStatSommeItemCapture() {
        return statSommeItemCapture;
    }

    public void setStatSommeItemCapture(Long statSommeItemCapture) {
        this.statSommeItemCapture = statSommeItemCapture;
    }

    public Integer getStatQuartilePremier() {
        return statQuartilePremier;
    }

    public void setStatQuartilePremier(Integer statQuartilePremier) {
        this.statQuartilePremier = statQuartilePremier;
    }

    public Integer getStatQuartileTrois() {
        return statQuartileTrois;
    }

    public void setStatQuartileTrois(Integer statQuartileTrois) {
        this.statQuartileTrois = statQuartileTrois;
    }

    public Integer getStatDecilePremier() {
        return statDecilePremier;
    }

    public void setStatDecilePremier(Integer statDecilePremier) {
        this.statDecilePremier = statDecilePremier;
    }

    public Integer getStatDecileNeuf() {
        return statDecileNeuf;
    }

    public void setStatDecileNeuf(Integer statDecileNeuf) {
        this.statDecileNeuf = statDecileNeuf;
    }

    public Integer getStatMedian() {
        return statMedian;
    }

    public void setStatMedian(Integer statMedian) {
        this.statMedian = statMedian;
    }

    public Float getStatMoyenne() {
        return statMoyenne;
    }

    public void setStatMoyenne(Float statMoyenne) {
        this.statMoyenne = statMoyenne;
    }

    public Double getStatEcartType() {
        return statEcartType;
    }

    public void setStatEcartType(Double statEcartType) {
        this.statEcartType = statEcartType;
    }

    @Override
    /**
     * *
     * Retourne l'intervale de date à un format lisible. "dd/MM/yyyy à dd/MM/yyyy". C'est pas très MVC. Normalement
     * c'est le role de la vue de mettre en forme la date. Mais rien n'empeche de le faire par la suite et d'oublier
     * cette redéclaration de toString.
     */
    public String toString() {
        String retour = "";
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMMM yyyy à hh'h'mm");
        if (this.dateDebut != null) {
            DateTime dt = new DateTime(this.getDateDebut());
            retour = fmt.print(dt);
        }
        if (this.datefin != null) {
            DateTime dt = new DateTime(this.datefin);
            retour += " à " + fmt.print(dt);
        } else {
            DateTime dt = new DateTime();
            retour += " à Maintennant (" + fmt.print(dt) + ")";
        }

        return retour;
    }
}
