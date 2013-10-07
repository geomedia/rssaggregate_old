<%-- 
    Document   : itemJSONDesc
    Created on : 15 juil. 2013, 18:32:45
    Author     : clem
--%>

<%@page import="org.json.simple.JSONObject"%>
<%@page import="rssagregator.beans.Item"%>
<%@page import="java.util.List"%>
<%@page import="org.json.simple.JSONArray"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    
    JSONObject export = new JSONObject();
    
      
    
    export.put("firsResult", request.getAttribute("firsResult"));
//    export.put("itPrPage", request.getAttribute("itPrPage"));
    export.put("nbitem", request.getAttribute("nbitem"));
    
    JSONArray array = new JSONArray();
    export.put("items", array);
    
    

    List<Item> listItem = (List<Item>) request.getAttribute("listItem");

    int i;
    for (i = 0; i < listItem.size(); i++) {

        JSONObject obj = new JSONObject();
        obj.put("id", listItem.get(i).getID().toString());
        obj.put("titre", listItem.get(i).getTitre());
        obj.put("desc", listItem.get(i).getDescription());
        obj.put("link", listItem.get(i).getLink());

        JSONArray arrayjs = new JSONArray();
        int j;
        for (j = 0; j < listItem.get(i).getListFlux().size(); j++) {
//            tabString[j] = listItem.get(i).getListFlux().get(j).toString();
            arrayjs.add(listItem.get(i).getListFlux().get(j).toString());
        }
        
        obj.put("flux", arrayjs);

        array.add(obj);
    }
   
    out.clear();
    out.print(export.toJSONString());
 
    System.out.println(array.toJSONString());
    out.flush();

%>

