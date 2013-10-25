<%-- 
    Document   : journalCSV
    Created on : 21 oct. 2013, 12:54:32
    Author     : clem
--%>
<%@page import="rssagregator.beans.Flux"%>
<%@page import="java.util.Date"%>
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
//    export.put("total", request.getAttribute("total"));
//    export.put("total", 10);
    export.put("page", 1);
    export.put("total", request.getAttribute("totalPage")); //the total pages of the query
    export.put("records", request.getAttribute("records")); //the total records from the query
//    export.put("records", 100); //the total records from the query


    JSONArray rows = new JSONArray();
    export.put("rows", rows);


    // Poru chaque objet
//    colNames: ["ID", "journal", "type", "actif", "ajouté le"],
    List<Flux> listJ = (List<Flux>) request.getAttribute("items");
    for (int i = 0; i < listJ.size(); i++) {
        Flux j = listJ.get(i);
        JSONObject o1 = new JSONObject();
        rows.add(o1);
//        o1.put("id", "1");
        JSONArray o1array = new JSONArray();
        o1array.add(j.getID());
        o1array.add(j.toString());

        if (j.getJournalLie() != null) {
//             o1array.add(j.getJournalLie().toString());
             o1array.add(j.getJournalLie().getNom());
        } else {
            o1array.add("aucun");
        }

        if (j.getTypeFlux() != null) {
            o1array.add(j.getTypeFlux().toString());
        } else {
            o1array.add("aucun");
        }

        if (j.getActive() != null) {
            o1array.add(j.getActive().toString());
        } else {
            o1array.add("false");
        }

        if (j.getCreated() != null) {
            o1array.add(j.getCreated().toString());
        } else {
            o1array.add(new Date(0));
        }

        o1.put("cell", o1array);
    }


    out.clear();
    System.out.println(export.toJSONString());
    out.print(export.toJSONString());
    out.flush();
%>