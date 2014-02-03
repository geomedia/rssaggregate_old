/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

/**
 * <p>Certain incident ({@link NotificationAjoutFlux}, {@link AnomalieCollecte}...) ne sont pas des incident technique à
 * proprement parlé. Il s'agit putôt de notification. Le fait d'implémenter cette interface permet de les différencier.
 * Cette différenciation permet de séparer incident de notification dans les mail d'alerte</p>
 * <p>Pour l'instant, on a deux type de notification
 * <ul>
 * <li>{@link NotificationAjoutFlux}</li>
 * <li>{@link AnomalieCollecte}</li>
 * <ul>
 * 
 * 
 * </p>
 *
 * @author clem
 */
public interface Notification {
}
