<%-- 

Cette JSP permet de mettre en forme les données afficher par la grid de présentation des journaux.
    Document   : journalJsonGrid
    Created on : 18 oct. 2013, 12:08:14
    Author     : clem
--%>

<%@page import="org.jsoup.Jsoup"%>
<%@page import="rssagregator.beans.Flux"%>
<%@page import="rssagregator.beans.Item"%>
<%@page import="java.util.List"%>
<%@page import="rssagregator.beans.Journal"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    JSONObject export = new JSONObject();
//    export.put("total", request.getAttribute("total"));
    export.put("page", request.getAttribute("page"));
    export.put("total", request.getAttribute("total")); //the total pages of the query
    export.put("records", request.getAttribute("records")); //the total records from the query


    JSONArray rows = new JSONArray();
    export.put("rows", rows);


    // Poru chaque objet

    List<Item> listJ = (List<Item>) request.getAttribute("items");


    for (int i = 0; i < listJ.size(); i++) {
        Item j = listJ.get(i);


        JSONObject o1 = new JSONObject();
        rows.add(o1);
//        JSONArray o1array = new JSONArray();
        o1.put("ID", j.getID());


        if (j.getTitre() != null && !j.getTitre().isEmpty()) {
            o1.put("titre", j.getTitre());
        }
        else{
            o1.put("titre", "");
        }

        if (j.getDescription() != null && !j.getDescription().isEmpty()) {
            o1.put("description", Jsoup.parse(j.getDescription()).text());
        } else {
            o1.put("description", "");
        }
//        o1.put("description", j.getDescription());


        if (j.getDateRecup() != null) {
            o1.put("dateRecup", j.getDateRecup().getTime());

        }

        if (j.getDatePub() != null) {
            o1.put("datePub", j.getDatePub().getTime());

        }



//        o1array.add(j.getID());
//        o1array.add(j.getTitre());
//        if (j.getDescription() != null && !j.getDescription().isEmpty()) {
//            o1array.add(Jsoup.parse(j.getDescription()).text()); // TODO : potentiellement pompeur de ressource si beaucoup de client font des requetes en ajax. ?
//        }
//        else{
//            o1array.add(""); 
//        }

//        JSONArray fluxs = new JSONArray();
//        o1array.add(fluxs);

//        if (j.getDateRecup() != null) {
//            o1array.add(j.getDateRecup().getTime());
//
//        }

//        for (int k = 0; k < j.getListFlux().size(); k++) {
//            Flux fl = j.getListFlux().get(k);
//            JSONObject fljson = new JSONObject();
//            fljson.put("ID", fl.getID());
//            fljson.put("val", fl.toString());
//            fluxs.add(fljson);
//        }
//        journal.put("ID", j.get)

//        o1.put("cell", o1);

    }


    out.clear();
    out.print(export.toJSONString());
    out.flush();
%>