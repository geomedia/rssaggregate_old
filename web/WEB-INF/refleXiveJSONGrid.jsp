<%-- 

Cette JSP permet de mettre en forme les données afficher par la grid de présentation des journaux.
    Document   : journalJsonGrid
    Created on : 18 oct. 2013, 12:08:14
    Author     : clem
--%>

<%@page import="java.util.List"%>
<%@page import="rssagregator.beans.Journal"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    JSONObject export = new JSONObject();
    export.put("total", request.getAttribute("total"));
    export.put("page", request.getAttribute("page"));
    export.put("total", request.getAttribute("totalPage")); //the total pages of the query
    export.put("records", request.getAttribute("records")); //the total records from the query
    

    JSONArray rows = new JSONArray();
    export.put("rows", rows);

    
    //On récupère la liste des champs 

    // Poru chaque objet
    List<Object> listJ = (List<Object>) request.getAttribute("items");
    for (int i = 0; i < listJ.size(); i++) {
        Object j = listJ.get(i);
        JSONObject o1 = new JSONObject();
        rows.add(o1);
        o1.put("id", "1");
        JSONArray o1array = new JSONArray();
//        o1array.add(j.getID());
//        o1array.add(j.getNom());
//        o1array.add(j.getLangue());
//        o1array.add(j.getPays());
//        o1array.add(j.getTypeJournal());
//        o1array.add(j.getUrlAccueil());
        o1.put("cell", o1array);
    }


    out.clear();
    out.print(export.toJSONString());
    out.flush();
%>