/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.incident;

/**
 * Certain incident ({@link NotificationAjoutFlux}, {@link AnomalieCollecte}...) ne sont pas des incident technique à
 * proprement parlé. Il s'agit putôt de notification. Le fait d'implémenter cette interface permet de les différencier
 *
 * @author clem
 */
public interface Notification {
}
