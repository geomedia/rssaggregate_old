/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.persistence.Transient;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.services.tache.TacheRecupCallable;

/**
 * Objet permettan de collecter un flux depuis son flux XML. Utilisé par la tache {@link TacheRecupCallable}.
 *
 * @author clem
 */
public class VisitorHTTP extends VisitorCollecteFlux implements ComportementVisitor {

    /**
     * *
     * Permet de collecter un flux RSS par requête sur son adresse http.
     *
     * @param flux
     * @throws Exception
     */
    /**
     * *
     * Permet de collecter un flux en utilisant son url
     *
     * @param flux
     * @throws Exception
     */
    @Override
    public void visit(Flux flux) throws Exception {
        // On commence par cloner le comportement du flux
        MediatorCollecteAction cloneCollecteAction = flux.getMediatorFlux().genererClone();


//        InputStream retourInputStream = null;
        byte[] contenu = null;
        ExecutorService executor = null;
        executor = Executors.newFixedThreadPool(1);

        try {
            //-----------------------
            // REQUESTEUR
            //-----------------------
            // Le requesteur est chargé d'établir la connection HTTP et de récupérer un tableau d'octet contenant la page.

            if (cloneCollecteAction.getRequesteur() != null) {
                // Si le time out n'est pas précisé on applique 15 s
                if (cloneCollecteAction.getRequesteur().timeOut == null) {
                    cloneCollecteAction.getRequesteur().timeOut = 15;
                }


                cloneCollecteAction.getRequesteur().setUrl(flux.getUrl());
                Future futureRequest = executor.submit(cloneCollecteAction.getRequesteur());
                futureRequest.get(cloneCollecteAction.getRequesteur().getTimeOut(), TimeUnit.SECONDS);


                if (cloneCollecteAction.getParseur() != null) {
                    cloneCollecteAction.getParseur().setContenuAParser(cloneCollecteAction.getRequesteur().getResu());
                }

            }

            //-----------------------------
            //  Parseur
            //-----------------------------
            // On parse le retour du serveur. Le parseur est chargé de détecter l'encodage de caractère et de créer les Item
            //Le parseur doit se comporter comme une thread car il faut limiter le temps d'execution. Très facile avec un runnable.

            listItem = new ArrayList<Item>();
            Future<List<Item>> futurs;

            futurs = executor.submit(cloneCollecteAction.getParseur());
            // On donne 30 seconde au parseur pour effectuer sa tache
            try {
                listItem = futurs.get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.debug("Erreur lors du parsing", e);
                throw e;
            }

//            cloneCollecteAction.setNbrItemCollecte(listItem.size());
            nbItTrouve = (short) listItem.size();


            //-----------------------------
            //  Dedoublonneur
            //-----------------------------

            // Génrer hash
            if (cloneCollecteAction.getDedoubloneur() != null && cloneCollecteAction.getDedoubloneur().getEnable() != null && cloneCollecteAction.getDedoubloneur().getEnable()) {
                cloneCollecteAction.getDedoubloneur().calculHash(listItem);
            }


            // calcul des Md5
            if (cloneCollecteAction.getDedoubloneur() != null && cloneCollecteAction.getDedoubloneur().getEnable() != null && cloneCollecteAction.getDedoubloneur().getEnable()) {
//                this.dedoubloneur.calculHash(listItem);
                // On lance le premier dédoublonneur. Il est chargé de dédoublonner par rapport aux hash.

                cloneCollecteAction.getDedoubloneur().setVisitor(this); // Le dedoublonneur a besoin de connaitre le visiteur pour completer quelques stat de traitement
                listItem = cloneCollecteAction.getDedoubloneur().dedoublonne(listItem, flux);
            }

        } catch (Exception e) {
            logger.info("erreur lors de lu traitement : " + e);
            throw e; // On remonte l'erreur. Elle sera traité par le service en passant par la tâche
        } finally {
            // Quoi qu'il arrive, il faut fermer la connection. et détruire le pool du parseur. 
            if (cloneCollecteAction.getRequesteur() != null) {
                cloneCollecteAction.getRequesteur().clore();
            }
            if (executor != null) { // Fermeture obligatoire de l'executor service
                try {
                    executor.shutdownNow();
                } catch (Exception e) {
                    logger.debug("Erreur lors de la fermeture de l'executor service", e);
                }

            }
        }
    }

}
