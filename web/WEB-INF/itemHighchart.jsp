<%-- 
    Document   : itemHighchart
    Created on : 11 oct. 2013, 15:50:47
    Author     : clem
--%>

<%@page import="rssagregator.beans.POJOCompteItem"%>
<%@page import="org.joda.time.DateTime"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    JSONObject highchart = new JSONObject();
    JSONObject title = new JSONObject();
    highchart.put("title", title);
    List<POJOCompteItem> list = (List<POJOCompteItem>) request.getAttribute("compte");

    title.put("text", "Compte du nombre d'item par jours");
    title.put("x", -20);

    //-----------xAxis
    JSONObject xAxis = new JSONObject();
    JSONArray categories = new JSONArray();

    // On va parcourir chacun des jour
    if(!list.isEmpty()){
         DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM");
        POJOCompteItem compte = list.get(0);
        
        for (Map.Entry<Date, Integer> entry : compte.getCompte().entrySet()) {
             Date date = entry.getKey();
             categories.add(fmt.print(new DateTime(date)));
        }
    }
    else{
        System.out.println("ELSE Liste Vide");
    }

    xAxis.put("categories", categories);

    JSONObject xTitle = new JSONObject();
    xTitle.put("text", "blablabla");
    xAxis.put("title", xTitle);

    highchart.put("xAxis", xAxis);

    //--------yAxis
    JSONObject yAxis = new JSONObject();
    JSONObject yAxisTitle = new JSONObject();
    yAxisTitle.put("text", "Nombre d'item capturées");
    yAxis.put("title", yAxisTitle);
    highchart.put("yAxis", yAxis);


    //---------Tooltip
    JSONObject tooltip = new JSONObject();
    tooltip.put("valueSuffix", " items");
    highchart.put("tooltip", tooltip);



    //-------------SERIES
    JSONArray series = new JSONArray();
    highchart.put("series", series);
    //On ajoute des données fixe mais plus tard ca bouclera
    // On récuère les compte

    for (int i = 0; i < list.size(); i++) {
        POJOCompteItem compteItem = list.get(i);
        JSONObject o1 = new JSONObject();
        o1.put("name", compteItem.getFlux().toString());
        JSONArray o1Array = new JSONArray();
        for (Map.Entry<Date, Integer> entry : compteItem.getCompte().entrySet()) {
            Date date = entry.getKey();
            Integer integer = entry.getValue();
            o1Array.add(integer.longValue());
        }
        o1.put("data", o1Array);
        series.add(o1);

    }


//    JSONObject o1 = new JSONObject();
//    o1.put("name", "tokio");
//    JSONArray o1Array = new JSONArray();
//    o1Array.add(7.0);
//    o1Array.add(6.9);
//    o1Array.add(14.5);
//    o1.put("data", o1Array);

    System.out.println(highchart.toJSONString());
    out.clear();
    out.print(highchart.toJSONString());
    out.flush();%>