/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.traitement;

import java.util.List;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;

/**
 * Les VisitorCollecteFlux sont chargé du traitement de collecte du flux. Pour cela ils utilisent différents objets :
 * requesteur parseur dedoublonneur...
 *
 * @author clem
 */
public abstract class VisitorCollecteFlux implements ComportementVisitor {

    protected transient short nbItTrouve = 0;
    protected transient short nbDedoubMemoire = 0;
    protected transient short nbDedoubBdd = 0;
    protected transient short nbLiaisonCree = 0;
    protected transient short nbNouvelle = 0;
    protected transient short nbDoublonInterneAuflux = 0;
    /**
     * *
     * La liste des items récupérées par le visiteur
     */
    List<Item> listItem = null;
    /**
     * *
     * Pour spécifier un comportement de collecte a utiliser. Sinon c'est le comportement du flux qui doit être utilisé.
     */
    ComportementCollecte comportementCollecte;
    /**
     * *
     * Le logger
     */
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());


    /**
     * *
     * Permet de collecter le flux.
     *
     * @param flux
     * @throws Exception
     */
    @Override
    public abstract void visit(Flux flux) throws Exception;

    public short getNbItTrouve() {
        return nbItTrouve;
    }

    public void setNbItTrouve(short nbItTrouve) {
        this.nbItTrouve = nbItTrouve;
    }

    public short getNbDedoubMemoire() {
        return nbDedoubMemoire;
    }

    public void setNbDedoubMemoire(short nbDedoubMemoire) {
        this.nbDedoubMemoire = nbDedoubMemoire;
    }

    public short getNbDedoubBdd() {
        return nbDedoubBdd;
    }

    public void setNbDedoubBdd(short nbDedoubBdd) {
        this.nbDedoubBdd = nbDedoubBdd;
    }

    public short getNbLiaisonCree() {
        return nbLiaisonCree;
    }

    public void setNbLiaisonCree(short nbLiaisonCree) {
        this.nbLiaisonCree = nbLiaisonCree;
    }

    public short getNbNouvelle() {
        return nbNouvelle;
    }

    public void setNbNouvelle(short nbNouvelle) {
        this.nbNouvelle = nbNouvelle;
    }

    public short getNbDoublonInterneAuflux() {
        return nbDoublonInterneAuflux;
    }

    public void setNbDoublonInterneAuflux(short nbDoublonInterneAuflux) {
        this.nbDoublonInterneAuflux = nbDoublonInterneAuflux;
    }

    public ComportementCollecte getComportementCollecte() {
        return comportementCollecte;
    }

    public void setComportementCollecte(ComportementCollecte comportementCollecte) {
        this.comportementCollecte = comportementCollecte;
    }

    public List<Item> getListItem() {
        return listItem;
    }

    public void setListItem(List<Item> listItem) {
        this.listItem = listItem;
    }
}
