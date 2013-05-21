package rssagregator.beans.traitement;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Vector;
import rssagregator.beans.Item;

public interface IfsRequester {

    public void requete(String urlArg) throws MalformedURLException, IOException;

    public Integer getHttpStatut();

    public InputStream getInputStream();

    public String getHttpResult();

    public void setHttpResult(String HttpResult);
}