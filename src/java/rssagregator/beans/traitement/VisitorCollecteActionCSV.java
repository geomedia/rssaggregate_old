/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;

/**
 * Objet permettant de collecter les items d'un flux depuis un CSV
 *
 * @author clem
 */
public class VisitorCollecteActionCSV extends VisitorCollecteFlux implements ComportementVisitor {

    /**
     * *
     * Le fichier CSV qu'il faut exploiter
     */
    File fichier;
    /**
     * *
     * Tableau d'octet contenant le csv. Il sera lu en fonction de l'encodage déterminé dans le parseur
     *
     * @see {@link AbstrParseur#forceEncoding}
     */
    byte[] byteCSV;

    /**
     * *
     * Pour collecter des items depuis un csv
     *
     * @param flux
     * @throws Exception
     */
    @Override
    public void visit(Flux flux) throws Exception {
        // On supprive le requesteur

        // On commence par cloner le comportement du flux


        if (comportementCollecte == null) {
            comportementCollecte = flux.getMediatorFlux().genererClone();
        }


        ExecutorService executor = null;
        try {
            //-----------------------
            // REQUESTEUR
            //-----------------------
            /**
             * *
             * Pas de requesteur pour ce visiteur. C'est en amont que le tableau de byte doit être entré
             */
            comportementCollecte.setRequesteur(null);


            //-----------------------------
            //  Parseur
            //-----------------------------
            // On parse le retour du serveur. Le parseur doit se comporter comme une thread car il faut limiter le temps d'execution.
            executor = Executors.newFixedThreadPool(1);
            listItem = new ArrayList<Item>();
            Future<List<Item>> futurs;

//          FileInputStream fileInputStream=  new FileInputStream(fichier);
            comportementCollecte.getParseur().setContenuAParser(byteCSV);


            futurs = executor.submit(comportementCollecte.getParseur());

            // On attend la fin du parse jsuqu'a 120 secondes

            try {
                listItem = futurs.get(120, TimeUnit.SECONDS); // On fixe un delay maximale a 120 seconde pour le parse
            } catch (Exception e) {
                logger.debug("ERR", e);
            }

            nbItTrouve = (short) listItem.size();

            //-----------------------------
            //  Dedoublonneur
            //-----------------------------


            // Génrer hash
            if (comportementCollecte.getDedoubloneur() != null && comportementCollecte.getDedoubloneur().getEnable() != null && comportementCollecte.getDedoubloneur().getEnable()) {
                comportementCollecte.getDedoubloneur().calculHash(listItem);
            }

            final Flux ff = flux;
            final VisitorCollecteFlux finalVisitor = this;

            
            Runnable dedoubRun = new Runnable() {
                @Override
                public void run() {
                    if (comportementCollecte.getDedoubloneur() != null && comportementCollecte.getDedoubloneur().getEnable() != null && comportementCollecte.getDedoubloneur().getEnable()) {
                        // On lance le premier dédoublonneur. Il est chargé de dédoublonner par rapport aux hash.
                        comportementCollecte.getDedoubloneur().setVisitor(finalVisitor);

                        listItem = comportementCollecte.getDedoubloneur().dedoublonne(listItem, ff);
                    }
//                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            };
            Future futurDedoub = executor.submit(dedoubRun);
            
            // On execute en laissant 120 secondes
            try {
                futurDedoub.get(400, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw e;
            }
            
            // On définit a importé la valeur du champs
            for (int i = 0; i < listItem.size(); i++) {
                Item item = listItem.get(i);
                item.setImported(true);
            }
            
            

        } catch (Exception e) {
            logger.info("erreur lors de lu traitement : " + e);
            throw e; // On remonte l'erreur. Elle sera traité par le service en passant par la tâche
        } finally {
            // Quoi qu'il arrive, il faut fermer la connection. et détruire le pool du parseur. 
            if (comportementCollecte.getRequesteur() != null) {
                comportementCollecte.getRequesteur().clore();
            }
            if (executor != null) {
                executor.shutdownNow();
            }
        }

    }

    public File getFichier() {
        return fichier;
    }

    public void setFichier(File fichier) {
        this.fichier = fichier;
    }

    public byte[] getByteCSV() {
        return byteCSV;
    }

    public void setByteCSV(byte[] byteCSV) {
        this.byteCSV = byteCSV;
    }
}
