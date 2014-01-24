<%@page import="rssagregator.utils.ComparatorTrieListFluxParType"%>
<%@page import="java.util.Collections"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@page import="rssagregator.beans.Flux"%>
<%@page import="java.util.List"%>
<%
    JSONArray array = new JSONArray();
    List<Flux> listFlux = (List<Flux>) request.getAttribute("items");
    Collections.sort(listFlux, new ComparatorTrieListFluxParType());
    
    int i;
    for (i = 0; i < listFlux.size(); i++) {
        JSONArray fl = new JSONArray();

        fl.add(0, listFlux.get(i).getID());
        fl.add(1, listFlux.get(i).toString());
        
        
        if (listFlux.get(i).getTypeFlux() != null) {
            System.out.println("TYPE FLUX : " + listFlux.get(i).getTypeFlux().getDenomination());
            fl.add(2, listFlux.get(i).getTypeFlux().getDenomination());
        }
        else{
            fl.add(2, "Autre");
        }

        array.add(fl);
    }
    
    out.clear();
    out.print(array.toJSONString());
    System.out.println(array.toJSONString());
    out.flush();
%>
