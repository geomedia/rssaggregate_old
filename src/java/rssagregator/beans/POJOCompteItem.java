package rssagregator.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.chrono.CopticChronology;
import rssagregator.dao.DAOFactory;
import rssagregator.utils.ExceptionTool;

/**
 * *
 * Sous classe de {@link POJOCompteurFluxItem} permettant de réaliser le compte du nombre d'item jour pour un flux. Il
 * faut fournir la liste d'item {@link #items} et déclanceher la méthode {@link #compte} afin de génerer la map
 * {@link #compte}
 */
public class POJOCompteItem {

    /**
     * *
     * Le flux sur lequel porte le compte
     */
    Flux flux;
    /**
     * *
     * Map matérialisant le compte. Chaque entrée est matérialisé par une date (le jour) et un integer : le nombre
     * d'item capturée pour ce jour
     */
    Map<Date, Integer> compte;
    /**
     * *
     * La liste des items pour laquelle doit être effectué le compte
     */
    List<Item> items;
    /**
     * *
     * la date de début du compte
     */
    Date date1;
    /**
     * *
     * la date de fin du compte
     */
    Date date2;
    /**
     * *
     * Nombre total d'item
     */
    Long somme;
    /**
     * *
     * Nombre moyen d'item capturés par jours
     */
    Float moyenne;
    /**
     * Médiane du nombre d'item capturé par jour
     */
    Integer mediane;
    /**
     * *
     * Nombre maximale d'item jour dans la période
     */
    Integer max;
    /**
     * *
     * Nombre minimale d'item jour dans la période
     */
    Integer min;
    /**
     * *
     * Quartile d'item jour dans la période
     */
    Integer quartilePremier;
    Integer quartileTrois;
    /**
     * *
     * decile d'item jour dans la période
     */
    Integer decilePremier;
    Integer decileNeuf;
    Double ecartType;
    private Float[] statMoyDayOfWeek = new Float[7];
    private Integer[] statMedDayOfWeek = new Integer[7];
    private Double[] statEcartypeDayOfWeek = new Double[7];

//    private Float statMoyLundi;
//    private Float statMoyMardi;
//    private Float statMoyMercredi;
//    private Float statMoyJeudi;
//    private Float statMoyVendredi;
//    private Float statMoySamedi;
//    private Float statMoyDimanche;
    public POJOCompteItem() {
        compte = new TreeMap<Date, Integer>();
        items = new ArrayList<Item>();
    }

    /**
     * *
     * Ajoute une information au tableau de compte (le jour et le nombre d'item). Cette méthode peut être utiliser afin
     * de ne pas passer par la méthode compte s'appuyant sur une liste d'item. En effet cette méthode peut bugger si la
     * liste est très importante (centaine de millier d'item). On préfère alors faire les compte en s'appuyant sur des
     * requete sql native et en envoyant les résultat ici
     *
     * @param d
     * @param nbr
     */
    public void addCompte(Date d, Integer nbr) {
        compte.put(new DateTime(d).withTimeAtStartOfDay().toDate(), nbr);
    }

    /**
     * *
     * getter pour {@link #flux}
     *
     * @return
     */
    public Flux getFlux() {
        return flux;
    }

    /**
     * *
     * setter pour {@link #flux}
     *
     * @param flux
     */
    public void setFlux(Flux flux) {
        this.flux = flux;
    }

    public Map<Date, Integer> getCompte() {
        return compte;
    }

    public void setCompte(Map<Date, Integer> compte) {
        this.compte = compte;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public Date getDate2() {
        return date2;
    }

    public void setDate2(Date date2) {
        this.date2 = date2;
    }

    public Long getSomme() {
        return somme;
    }

    public void setSomme(Long somme) {
        this.somme = somme;
    }

    public Float getMoyenne() {
        return moyenne;
    }

    public void setMoyenne(Float moyenne) {
        this.moyenne = moyenne;
    }

    public Integer getMediane() {
        return mediane;
    }

    public void setMediane(Integer mediane) {
        this.mediane = mediane;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getQuartilePremier() {
        return quartilePremier;
    }

    public void setQuartilePremier(Integer quartilePremier) {
        this.quartilePremier = quartilePremier;
    }

    public Integer getQuartileTrois() {
        return quartileTrois;
    }

    public void setQuartileTrois(Integer quartileTrois) {
        this.quartileTrois = quartileTrois;
    }

    public Integer getDecilePremier() {
        return decilePremier;
    }

    public void setDecilePremier(Integer decilePremier) {
        this.decilePremier = decilePremier;
    }

    public Integer getDecileNeuf() {
        return decileNeuf;
    }

    public void setDecileNeuf(Integer decileNeuf) {
        this.decileNeuf = decileNeuf;
    }

    public Double getEcartType() {
        return ecartType;
    }

    public void setEcartType(Double ecartType) {
        this.ecartType = ecartType;
    }

    public Float[] getStatMoyDayOfWeek() {
        return statMoyDayOfWeek;
    }

    /**
     * *
     * retroune le nombre moyen d'item pour le jour
     *
     * @param statMoyDayOfWeek
     */
    public void setStatMoyDayOfWeek(Float[] statMoyDayOfWeek) {
        this.statMoyDayOfWeek = statMoyDayOfWeek;
    }

    public Integer[] getStatMedDayOfWeek() {
        return statMedDayOfWeek;
    }

    public void setStatMedDayOfWeek(Integer[] statMedDayOfWeek) {
        this.statMedDayOfWeek = statMedDayOfWeek;
    }

    public Double[] getStatEcartypeDayOfWeek() {
        return statEcartypeDayOfWeek;
    }

    public void setStatEcartypeDayOfWeek(Double[] statEcartypeDayOfWeek) {
        this.statEcartypeDayOfWeek = statEcartypeDayOfWeek;
    }

    /**
     * *
     * Permet de retrouver le nombre d'item dans le compte pour la date envoyé en argument
     *
     * @param date la date demandé
     * @return Le nombre d'item, null si rien n'a été trouvé
     */
    public Integer getValue(Date date) throws Exception {
        // On commence par formater la date
        DateTime dt = new DateTime(date).withTimeAtStartOfDay();

        if (compte == null) {
            throw new Exception("il faut initialiser les comptes avant de lancer cette méthode");
        }

        Integer value = compte.get(dt.toDate());

        if (value != null) {
            return value;
        }

        return null;
    }

    /**
     * *
     * Génère le map {@link #compte} en fonction des item trouvée dans {@link #items} et des deux date {@link #date1} et
     * {@link #date2}
     */
    public void compte() throws Exception {


        if (date1 == null) {
            throw new Exception("La date 1 doit être renseigné avant de lancer le compte");
        }
        if (date2 == null) {
            throw new Exception("La date 2 doit être renseigner avant de lancer le compte");
        }

        if (this.items == null) {
            throw new Exception("Il faut ajouter une liste d'item avant de lancer la méthode compte");
        }

        // On commence par initialiser la map de compte
        DateTime dt1 = new DateTime(date1).withTimeAtStartOfDay();
        DateTime dt2 = new DateTime(date2).withEarlierOffsetAtOverlap();
        DateTime dtIt = new DateTime(date1).withTimeAtStartOfDay();
        Interval interval = new Interval(dt1, dt2);




//        while (dtIt.isBefore(dt2)) {
        while (interval.contains(dtIt)) {
            compte.put(dtIt.toDate(), 0);
            dtIt = dtIt.plusDays(1);
        }

        for (Iterator<Item> it = items.iterator(); it.hasNext();) {
            Item item = it.next();
            // On récupère la date.
            if (item.getDateRecup() != null) {
                DateTime dt = new DateTime(item.getDateRecup()).withTimeAtStartOfDay();

                Integer cptDay = compte.get(dt.toDate());
                if (cptDay == null) {
                    compte.put(dt.toDate(), 1);
                } else {
                    cptDay++;
                    compte.put(dt.toDate(), cptDay);
                }
            }
        }
    }

    public void initialiserCompte() {
        // On commence par initialiser la map de compte
        DateTime dt1 = new DateTime(date1).withTimeAtStartOfDay();
        DateTime dt2 = new DateTime(date2).withEarlierOffsetAtOverlap();
        DateTime dtIt = new DateTime(date1).withTimeAtStartOfDay();
        Interval interval = new Interval(dt1, dt2);



//        while (dtIt.isBefore(dt2)) {
        while (interval.contains(dtIt)) {
            compte.put(dtIt.toDate(), 0);
            dtIt = dtIt.plusDays(1);
        }
    }

    
    /***
     * Comme la méthode {@link #compte} mais en utilisant sql pour remplir le compte par jour
     * @param em 
     */
    public void comptItemJourSemaineBySQL(EntityManager em) {
        // Si aucun Em n'est envoyé on démarre
        if (em == null) {
            em = DAOFactory.getInstance().getEntityManager();
        }


        Query qCount = em.createNativeQuery("SELECT \n"
                + "date_trunc('day', item.daterecup) as \"day\", COUNT(*)\n"
                + "\n"
                + "FROM \n"
                + "  public.item, \n"
                + "  public.flux, \n"
                + "  public.item_flux\n"
                + "WHERE \n"
                + "  item.id = item_flux.item_id AND\n"
                + "  flux.id = item_flux.listflux_id\n"
                + "AND flux.id=? \n"
                + "AND item.daterecup BETWEEN ? and ? \n"
                + "GROUP BY 1 \n"
                + "ORDER BY 1 \n"
                + "  ;");

        qCount.setParameter(1, flux.getID());
        qCount.setParameter(2, date1);
        qCount.setParameter(3, date2);


        qCount.setParameter(1, flux.getID());
        qCount.setParameter(2, date1);
        qCount.setParameter(3, date2);

        List<Object[]> results = qCount.getResultList();
        initialiserCompte();

// On fournit a l'objet de compte les résultat de la native query
        for (int i = 0; i < results.size(); i++) {
            Object[] objects = results.get(i);
            java.sql.Timestamp ts = (java.sql.Timestamp) objects[0];
            Long val = (Long) objects[1];
            addCompte(ts, val.intValue());
        }
    }

    /**
     * *
     * Calcul la moyenne médiane décile et quartile pour les items envoyés.
     */
    public void calculterBoxPloat() {
        if (date1 == null) {
            throw new NullPointerException("date1 est null");
//            throw new NullArgumentException("date1");
        } else if (date2 == null) {
            throw new NullPointerException("date2 est null");
        }

        if (this.compte == null) {
            throw new NullPointerException("Vous devez initialiser les comptes en utilisant au préalable la méthode compte()");
        }

        DescriptiveStatistics stats = new DescriptiveStatistics();

        for (Map.Entry<Date, Integer> entry : compte.entrySet()) {
            Date date = entry.getKey();
            Integer integer = entry.getValue();
            stats.addValue(integer);
        }


        Double dl = stats.getMin();
        min = ((Double) stats.getMin()).intValue();
        max = ((Double) stats.getMax()).intValue();
        ecartType = stats.getStandardDeviation();
        moyenne = ((Double) stats.getMean()).floatValue();
        mediane = ((Double) stats.getPercentile(50)).intValue();
        quartilePremier = ((Double) stats.getPercentile(25)).intValue();
        quartileTrois = ((Double) stats.getPercentile(75)).intValue();
        decilePremier = ((Double) stats.getPercentile(10)).intValue();
        decileNeuf = ((Double) stats.getPercentile(90)).intValue();
        somme = ((Double) stats.getSum()).longValue();



        // Calcul des moyennes par jour

//        Float[] sommeJour = new Float[7]; // Initialisation

        Map<Integer, DescriptiveStatistics> mapStatJour = new HashMap<Integer, DescriptiveStatistics>();

        Integer[] nbrJour = new Integer[8];
        for (int i = 0; i < 7; i++) {
            DescriptiveStatistics stat = new DescriptiveStatistics();
            mapStatJour.put(i + 1, stat);
//            Long long1 = sommeJour[i];
//            sommeJour[i] = new Float(0);
//            nbrJour[i] = 0;
        }

        for (Map.Entry<Date, Integer> entry : compte.entrySet()) {
            Date date = entry.getKey();
            DateTime dt = new DateTime(date);
            Integer integer = entry.getValue();
            int dayOfWeek = dt.getDayOfWeek();
            mapStatJour.get(dayOfWeek).addValue(integer);

//            sommeJour[dayOfWeek] += integer;
//            nbrJour[dayOfWeek]++;

        }

        for (int i = 0; i < 7; i++) {
            DescriptiveStatistics statDuJour = mapStatJour.get(i + 1);

            Double moy = statDuJour.getMean();
            if (moy.equals(Double.NaN)) {
                moy = new Double(0);
            }

            Double ecat = statDuJour.getStandardDeviation();
            if (ecat.equals(Double.NaN)) {
            }

            statMoyDayOfWeek[i] = moy.floatValue();
            statMedDayOfWeek[i] = ((Double) statDuJour.getPercentile(50)).intValue();
            statEcartypeDayOfWeek[i] = ((Double) statDuJour.getStandardDeviation());

        }

    }

    /**
     * *
     * Calcul le nombre moyen d'item sur la periode ou entre les deux date envoyé en argument
     *
     * @param date1
     * @param date2
     * @return Nombre moyen d'item. C'est un int arrondi
     * @throws NullPointerException : si date1 ou date2 sont null
     *
     */
    public Float calculerMoyenne(Date date1, Date date2) throws NullPointerException {

        if (date1 == null) {
            throw new NullPointerException("date1 est null");
//            throw new NullArgumentException("date1");
        } else if (date2 == null) {
            throw new NullPointerException("date2 est null");
        }

        if (this.compte == null) {
            throw new NullPointerException("Vous devez initialiser les comptes en utilisant au préalable la méthode compte()");
        }

        DateTime dt1 = new DateTime(date1).withTimeAtStartOfDay();
        DateTime dt2 = new DateTime(date2).withEarlierOffsetAtOverlap();
        Interval interval = new Interval(dt1, dt2);

        Integer s = 0;
        Integer nbrIt = 0;
        for (Map.Entry<Date, Integer> entry : compte.entrySet()) {
            Date date = entry.getKey();
            DateTime dtIt = new DateTime(date);

            if (interval.contains(dtIt)) {
                Integer integer = entry.getValue();
                s = s + integer;
                nbrIt++;
            }
        }

        if (nbrIt > 0) {
            moyenne = s.floatValue() / nbrIt.floatValue();
        } else {
            moyenne = new Float(0);
        }
        return moyenne;
    }

    /**
     * *
     * Cette méthode parcours l'ensemble des comptes et vérifié pour chaque jours si la date dépasse ou le le seuil par
     * rapport à la moyenne
     *
     * @param seuil : Le seuil est un pourcentage donc un int entre 0 et 100
     * @return Une hash map avec en cle la date ou danormale. En valeur le nombre d'item
     * @throws NullPointerException : si le seuil envoyé en argument est null
     */
    @Deprecated
    public Map<Date, Integer> detecterAnomalieParrapportAuSeuil(Integer seuil) {


        if (seuil == null) {
            throw new NullPointerException("impossible de calculter avec un seuil null");
        }

        if (moyenne == null) {
            throw new NullPointerException("Il faut calculer la moyenne avant de lancer ce calcul");
        }

        if (this.compte == null) {
            throw new NullPointerException("Il faut lancer le compte avant de faire ce calcul");
        }


        Float seuilMax = moyenne + (moyenne * seuil / 100);
        Float seuilMin = moyenne - (moyenne * seuil / 100);
        Map<Date, Integer> dateAnormal = new HashMap<Date, Integer>();


        for (Map.Entry<Date, Integer> entry : compte.entrySet()) {

            Date date = entry.getKey();
            Integer integer = entry.getValue();
            if (integer < seuilMin || moyenne > seuilMax) {
                dateAnormal.put(date, integer);
            }

        }
        return dateAnormal;
    }

    /**
     * *
     * trouver les jours consécutif pour lequels on passe en dessous d'un certain nombre d'item capturé par jour
     *
     * @param nbrItemMin Le nombre d'item servant de seuil a la détection. Si on a pour un jour un nombre d'item
     * strictement inférieur, on considère le jour comme anormale
     * @param jour le nombre de jour consécutifs
     * @param aPartidu : date a partir de laquel le calcul est effectué. Les jour précédent cette date seront ignorées.
     * Si ce paramettre est null toutes les date sont ispecté
     * @return une map (trie par jour avec les jours pour lequel on est passé en dessous du seuil. En valeur de la map
     * on a le nombre d'item pour le jour;
     */
    public Map<Date, Integer> detecterAnomalieNbrMinimalItem(Integer nbrItemMin, Integer jour, Date aPartidu) {
        ExceptionTool.argumentNonNull(nbrItemMin);
        ExceptionTool.argumentNonNull(jour);
        if (this.compte == null) {
            throw new NullPointerException("Il faut lancer le compte avant de faire ce calcul");
        }

        Map<Date, Integer> retour = new TreeMap(); // La map qui sera retournée comprenant les jours anormaux ainsi que le nombre d'item
        Map<Date, Integer> ajoutMap = new TreeMap();

        int i = 0;
        for (Map.Entry<Date, Integer> entry : compte.entrySet()) {
            Date date = entry.getKey();
            Integer nbrPourJourObserve = entry.getValue();


            if ((aPartidu != null && aPartidu.before(date)) || aPartidu == null) {
                // Si le nombre d'item pour cette journée est anormale, on ajoute à la map
                if (nbrPourJourObserve < nbrItemMin) {
                    ajoutMap.put(date, nbrPourJourObserve);
                }

                // Si on a un nombre de jours consécutif supérieur à la var jour envoyé en argument
                if (ajoutMap.size() >= jour) {
                    retour.putAll(ajoutMap);
                }

                // Si on repasse au dessus du seuil d'item, on clear 

                if (nbrPourJourObserve >= nbrItemMin) {
                    ajoutMap.clear();
                }
            }
            i++;

        }

        return retour;
    }
}
