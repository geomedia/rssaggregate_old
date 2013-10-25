<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@page import="rssagregator.beans.Flux"%>
<%@page import="java.util.List"%>
<%
    JSONArray array = new JSONArray();
    List<Flux> listFlux = (List<Flux>) request.getAttribute("items");
    System.out.println("List SiZe : " + listFlux.size());
    int i;
    for (i = 0; i < listFlux.size(); i++) {
        JSONArray fl = new JSONArray();
        
        fl.add(0, listFlux.get(i).getID());
//        fl.put("id", listFlux.get(i).getID());
        fl.add(1, listFlux.get(i).toString());
        array.add(fl);
    }
    out.clear();
    out.print(array.toJSONString());
    System.out.println(array.toJSONString());
    out.flush();
%>
