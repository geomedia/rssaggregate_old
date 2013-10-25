<%-- 
    Document   : journalCSV
    Created on : 21 oct. 2013, 12:54:32
    Author     : clem
--%>
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
    List<Journal> listItem = (List<Journal>) request.getAttribute("items");
    
    data.add(new String[]{"nom", "langue", "pays"});
    for(int i=0; i<listItem.size(); i++){
        Journal j = listItem.get(i);
        String nom = j.getNom();
        String langue = j.getLangue();
        String pays = j.getPays();
        data.add(new String[]{nom, langue, pays});
        
    }

    cSVWriter.writeAll(data);
    cSVWriter.close();
    out.clear();
    out.println(sw.toString());%>