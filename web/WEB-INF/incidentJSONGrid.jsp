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
    JSONObject export = new JSONObject();
//    export.put("total", request.getAttribute("total"));
    export.put("page", request.getAttribute("page"));
    export.put("total", request.getAttribute("total")); //the total pages of the query
    export.put("records", request.getAttribute("records")); //the total records from the query
    
    System.out.println("JPS PAGE : "+request.getAttribute("page"));
    System.out.println("JPS TOTAL : "+request.getAttribute("total"));
    System.out.println("JPS RECORDS : "+request.getAttribute("records"));
            


    JSONArray rows = new JSONArray();
    export.put("rows", rows);


    // Poru chaque objet

    List<AbstrIncident> listJ = (List<AbstrIncident>) request.getAttribute("items");
    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
    
    if (listJ != null) {
        for (int i = 0; i < listJ.size(); i++) {
            AbstrIncident j = listJ.get(i);
            JSONObject o1 = new JSONObject();
            rows.add(o1);
//            o1.put("id", "1");
            
            
            JSONArray o1array = new JSONArray();
            o1array.add(j.getID());
            o1array.add(j.toString());
            o1array.add(j.getClass().getSimpleName());
            o1array.add(j.getMessageEreur());

            if (j.getDateDebut() != null) {
                DateTime dt2 = new DateTime(j.getDateDebut());
                o1array.add(fmt.print(dt2));
            }
            else{
                o1array.add("??");
            }

            if (j.getDateFin() != null) {
                System.out.println("DATE : " + j.getDateFin());
                 DateTime dt2 = new DateTime(j.getDateFin());
                o1array.add(fmt.print(dt2));
            } else {
                o1array.add("Non clos");
            }
            o1.put("cell", o1array);
        }
    }


    out.clear();
    System.out.println(export.toJSONString());
    out.print(export.toJSONString());
    out.flush();
%>