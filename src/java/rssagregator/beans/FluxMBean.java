/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

/**
 *
 * @author clem
 */
public interface FluxMBean {

    public Long getID();

    public String getUrl();

    public String getInfoCollecte();
    public void setUrl(String url);
}
