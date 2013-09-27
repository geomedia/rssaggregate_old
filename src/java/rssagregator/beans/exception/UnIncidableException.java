/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.exception;

import rssagregator.beans.incident.IncidentFactory;

/**
 * Exception utilisée par la factory {@link IncidentFactory} lorsqu'on lui demande de créer un incident pour une Tache n'étant pas incidable.
 * @author clem
 */
public class UnIncidableException extends Exception{

    public UnIncidableException(String message) {
        super(message);
    }
}
