<%-- 

Cette JSP permet de mettre en forme les données afficher par la grid de présentation des journaux.
    Document   : journalJsonGrid
    Created on : 18 oct. 2013, 12:08:14
    Author     : clem
--%>

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
        JSONArray o1array = new JSONArray();
        o1array.add(j.getID());
        o1array.add(j.getTitre());
        o1array.add(j.getDescription());
        
        JSONArray fluxs = new JSONArray();
//        export.put("flux", fluxs);
        o1array.add(fluxs);
        for(int k=0;k<j.getListFlux().size();k++){
            Flux fl = j.getListFlux().get(k);
            JSONObject fljson = new JSONObject();
            fljson.put("ID", fl.getID());
            fljson.put("val", fl.toString());
            fluxs.add(fljson);
        }
//        journal.put("ID", j.get)
        
        o1.put("cell", o1array);
    }


    out.clear();
    System.out.println(export.toJSONString());
    out.print(export.toJSONString());
    out.flush();
%>