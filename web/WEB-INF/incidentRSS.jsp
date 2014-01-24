<%-- 
Une JSP permettant de visionner les incidents sous forme de flux RSS.
    Document   : incidentRSS
    Created on : 14 janv. 2014, 15:38:44
    Author     : clem
--%>

<%@page import="rssagregator.beans.Conf"%>
<%@page import="rssagregator.dao.DAOFactory"%>
<%@page import="com.sun.syndication.feed.synd.SyndContentImpl"%>
<%@page import="com.sun.syndication.feed.synd.SyndEntryImpl"%>
<%@page import="com.sun.syndication.feed.synd.SyndContent"%>
<%@page import="com.sun.syndication.feed.synd.SyndEntry"%>
<%@page import="rssagregator.beans.incident.AbstrIncident"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.sun.syndication.io.SyndFeedOutput"%>
<%@page import="java.io.StringWriter"%>
<%@page import="java.io.Writer"%>
<%@page import="com.sun.syndication.feed.synd.SyndFeedImpl"%>
<%@page import="com.sun.syndication.feed.synd.SyndFeed"%>
<%@page contentType="text/xml" pageEncoding="UTF-8"%>
<%    
    SyndFeed feed = new SyndFeedImpl();
    feed.setFeedType("rss_2.0");
    feed.setTitle("clem");
          Conf c = DAOFactory.getInstance().getDAOConf().getConfCourante();
        String url = c.getServurl();
        
    feed.setLink(url);
    feed.setDescription("Flux RSS permettant de suivre les incidents survenues sur le server de collecte GEOMEDIA");
    
    List entries = new ArrayList();
    List<AbstrIncident> incids = (List<AbstrIncident>) request.getAttribute("incids");
    
    for (int i = 0; i < incids.size(); i++) {
        AbstrIncident abstrIncident = incids.get(i);
        SyndEntry entry;
        SyndContent description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue(abstrIncident.getMessageEreur());
        entry = new SyndEntryImpl();
        
        entry.setTitle(abstrIncident.toString());
        entry.setDescription(description);
        entry.setPublishedDate(abstrIncident.getDateDebut());
        
        entry.setLink(abstrIncident.getUrlAdmin());
        
        entries.add(entry);
    }
    
    feed.setEntries(entries);
    
    
    
    
    Writer writer = new StringWriter();
    SyndFeedOutput output = new SyndFeedOutput();
    out.clear();
    output.output(feed, out);
    out.flush();
%>
