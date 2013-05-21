package rssagregator.beans;

import dao.DAOFactory;
import dao.DaoFlux;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import rssagregator.services.ListeFluxCollecteEtConfigConrante;
import rssagregator.services.ServiceGestionIncident;

public class TacheRecup extends Observable implements Runnable {
List<Item> nouvellesItems;
    /**
     * *
     * Le flux de la tache
     */
    public Flux flux;
    /**
     * A chaque récupération, il faut inscrire dans cette variable la date.
     * Cette variable est ainsi modifié au lancement de la méthode run
     */
    public Date DateDerniereRecup;

    public TacheRecup(Flux flux) {
        this.flux = flux;
        this.addObserver(flux);
    }

    /**
     * La methode relevant le flux. A la fin elle notifie les résultat au beans
     * flux. Au début du run un verrou est posé sur le flux. Cette méthode
     * utilise le médiator traitement du flux (qui utilise les parseur et autre
     * Rafinneur) pour produire le résultat et le notifier au flux par le relaod
     * (patern Observer).
     */
    @Override
    public void run() {
        synchronized(this.flux){
            try {
             nouvellesItems = this.flux.getMediatorFlux().executeActions(this.flux);
            System.out.println("###############################################################");
            System.out.println("Lancement de la tache : " + flux.getUrl());
            System.out.println("Nombre d'item rapporté pa le médiatoAction (nouvelles ou a lier) : " + nouvellesItems.size());
            System.out.println("###############################################################");
            // On enregistre ces nouvelles items
    
            int i;
           
            for (i = 0; i < nouvellesItems.size(); i++) {
                this.flux.getItem().add(nouvellesItems.get(i));
            }
    
            
            flux.fermerLesIncidentOuvert();
                ListeFluxCollecteEtConfigConrante.getInstance().modifierFlux(flux);
            
            System.out.println("NBR item en définitive : " + flux.getItem().size());

        } catch (MalformedURLException ex) {
            Logger.getLogger(TacheRecup.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            
                ServiceGestionIncident.getInstance().gererIncident(ex, this.flux);
            Logger.getLogger(TacheRecup.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TacheRecup.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }

    public List<Item> getNouvellesItems() {
        return nouvellesItems;
    }

    public void setNouvellesItems(List<Item> nouvellesItems) {
        this.nouvellesItems = nouvellesItems;
    }
    
    
    
}