<%-- 
    Document   : highchartFlux
    Created on : 11 oct. 2013, 14:38:52
    Author     : clem
--%>

<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>


<%
    JSONObject highchart = new JSONObject();
    
    JSONObject option = new JSONObject();
    highchart.put("option", option);
    
    option.put("text", "mon titre");
    option.put("x", 20);
    
    //-----------xAxis
    JSONObject xAxis = new JSONObject();
    JSONArray categories = new JSONArray();
    categories.add("A");
    categories.add("B");
    xAxis.put("categories ", categories);
    highchart.put("xAxis", xAxis);
    
    //--------yAxis
    JSONObject yAxis = new JSONObject();
    JSONObject yAxisTitle = new JSONObject();
    yAxisTitle.put("text", "titre yAxis");
    yAxis.put("titre", yAxisTitle);
    highchart.put("yAxis", yAxis);
    
    
    //series
    JSONArray series = new JSONArray();
    highchart.put("series", series);
    //On ajoute des données fixe mais plus tard ca bouclera
    JSONObject o1 = new JSONObject();
    o1.put("name", "tokio");
    JSONArray o1Array = new JSONArray();
    o1Array.add(7.0);
    o1Array.add(6.9);
    o1Array.add(14.5);
    o1.put("data", o1Array);
    series.add(o1);
    
    
    
    
     out.clear();
    out.print(highchart.toJSONString());
    out.flush();%>