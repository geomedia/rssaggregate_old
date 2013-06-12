//package rssagregator.beans;
//
//import dao.DAOFactory;
//import java.util.Date;
//import java.util.List;
//import java.util.Observable;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import rssagregator.beans.incident.AbstrIncident;
//import rssagregator.services.ServiceGestionIncident;
//
//public class TacheRecup extends Observable implements Runnable {
//
//    /**
//     * *
//     * Les items capturées par la tache
//     */
//    List<Item> nouvellesItems;
//    /**
//     * *
//     * Le flux de la tache
//     */
//    public Flux flux;
//    /**
//     * A chaque récupération, il faut inscrire dans cette variable la date.
//     * Cette variable est ainsi modifié au lancement de la méthode run
//     */
//    public Date DateDerniereRecup;
//    /**
//     * *
//     * Lorsqu'une exeption survient, on stocke sa référence ici
//     */
//    public AbstrIncident incident;
//
//    public TacheRecup(Flux flux) {
//        this.flux = flux;
//        this.addObserver(flux);
//        incident = null;
//    }
//
//    /**
//     * La methode relevant le flux. A la fin elle notifie les résultat au beans
//     * flux. Au début du run un verrou est posé sur le flux. Cette méthode
//     * utilise le médiator traitement du flux (qui utilise les parseur et autre
//     * Rafinneur) pour produire le résultat et le notifier au flux par le relaod
//     * (patern Observer).
//     */
//    @Override
//    public void run() {
//        synchronized (this.flux) {
//            try {
//                nouvellesItems = this.flux.getMediatorFlux().executeActions(this.flux);
//
//
//                System.out.println("###############################################################");
//                System.out.println("Lancement de la tache : " + flux.getUrl());
//                System.out.println("Nombre d'item rapporté pa le médiatoAction (nouvelles ou a lier) : " + nouvellesItems.size());
//                System.out.println("###############################################################");
//                // On enregistre ces nouvelles items
//
//                int i;
//                for (i = 0; i < nouvellesItems.size(); i++) {
//                    this.flux.getItem().add(nouvellesItems.get(i));
//                    this.flux.getLastEmpruntes().add(nouvellesItems.get(i).getHashContenu());
//                }
//
//                // On supprime des hash pour éviter l'accumulation. On en laisse 20 en plus du nombre d'item contenues dans le flux.
//
//                Integer nbr = flux.getMediatorFlux().getNbrItemCollecte() + 19;
//                if (nbr > 0 && nbr < flux.getLastEmpruntes().size()) {
//                    for (i = nbr; i < flux.getLastEmpruntes().size(); i++) {
//
//                        flux.getLastEmpruntes().remove(i);
//                        System.out.println("TACHE RECUP : SUPPRESSION D'UN HASH");
//                    }
//                }
//
//                flux.fermerLesIncidentOuvert();
//
//                DAOFactory.getInstance().getDAOFlux().modifierFlux(flux);
//                
////                ListeFluxCollecteEtConfigConrante.getInstance().modifierFlux(flux);
//                System.out.println("Tache RECUP : NBR item Collecté après dédoublonage : " + flux.getItem().size());
//
//            }
//            catch (Exception ex) { // On capture toute erreur pour l'envoyer au gestionnaire de d'incident
//                this.incident = ServiceGestionIncident.getInstance().gererIncident(ex, this.flux);
//                System.out.println("TACHE RECUP : Capture d'une Exception");
//                Logger.getLogger(TacheRecup.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
//
//    public List<Item> getNouvellesItems() {
//        return nouvellesItems;
//    }
//
//    public void setNouvellesItems(List<Item> nouvellesItems) {
//        this.nouvellesItems = nouvellesItems;
//    }
//
//    public Flux getFlux() {
//        return flux;
//    }
//
//    public void setFlux(Flux flux) {
//        this.flux = flux;
//    }
//
//    public Date getDateDerniereRecup() {
//        return DateDerniereRecup;
//    }
//
//    public void setDateDerniereRecup(Date DateDerniereRecup) {
//        this.DateDerniereRecup = DateDerniereRecup;
//    }
//
//    public AbstrIncident getIncident() {
//        return incident;
//    }
//
//    public void setIncident(AbstrIncident incident) {
//        this.incident = incident;
//    }
//}