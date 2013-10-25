<%-- 
    Document   : journalCSV
    Created on : 21 oct. 2013, 12:54:32
    Author     : clem
    Cette JSP permet d'exporter des flux sous forme d'un tableau CSV. Le CSV a vocation a récapituler l'ensemble des informations sur le flux.
--%>
<%@page import="rssagregator.beans.Flux"%>
<%@page import="rssagregator.beans.Journal"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="au.com.bytecode.opencsv.CSVWriter"%>
<%@page import="java.io.StringWriter"%>
<%@page contentType="document/csv" pageEncoding="UTF-8"%>
<%
    StringWriter sw = new StringWriter();
    CSVWriter cSVWriter = new CSVWriter(sw);
    List<String[]> data = new ArrayList<String[]>();
    List<Flux> listItem = (List<Flux>) request.getAttribute("items");

    data.add(new String[]{"ID", "NOM", "url", "type","journal"});
    for (int i = 0; i < listItem.size(); i++) {
        Flux j = listItem.get(i);

        String id = j.getID().toString();
        String nom = j.toString();
        String url = j.getUrl();
        String type = "aucun";
        if (j.getTypeFlux() != null) {
            type = j.getTypeFlux().toString();            
        }
        String journal = "aucun";
        if(j.getJournalLie()!=null){
            journal = j.getJournalLie().toString();
        }
        data.add(new String[]{id, nom, url, type, journal});
    }
        cSVWriter.writeAll(data);
    cSVWriter.close();
    out.clear();
    out.println(sw.toString());%>