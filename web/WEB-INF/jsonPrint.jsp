<%-- 

Cette JSP permet de mettre en forme les données afficher par la grid de présentation des journaux.
    Document   : journalJsonGrid
    Created on : 18 oct. 2013, 12:08:14
    Author     : clem
--%>

<%@page import="org.joda.time.DateTime"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="rssagregator.beans.incident.AbstrIncident"%>
<%@page import="java.util.List"%>
<%@page import="rssagregator.beans.Journal"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String jsonstr = (String)request.getAttribute("jsonstr");
    
    
   

    out.clear();
   out.print(jsonstr);
  
    out.flush();
%>