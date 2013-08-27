<%-- 
    Document   : itemJSONDesc
    Created on : 15 juil. 2013, 18:32:45
    Author     : clem
    DESC        : Cette servlet permet de mettre en force la description des flux au format json. Les paramettre firstresult itprPage et nbItem permettent de construire le paginator en javascript. Dans cette vue, on n'a besoin que de peu d'information sur les flux : leur facon de se présenter par tostring et leur ID
--%>


<%@page import="rssagregator.beans.Flux"%>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="rssagregator.beans.Item"%>
<%@page import="java.util.List"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page contentType="text/xml" pageEncoding="UTF-8"%>


<%
    JSONObject export = new JSONObject();

    export.put("firstResult", request.getAttribute("firstResult"));
//    export.put("itPrPage", request.getAttribute("itPrPage"));
    export.put("nbitem", request.getAttribute("nbitem"));

    JSONArray array = new JSONArray();
    export.put("items", array);


    List<Flux> listFlux = (List<Flux>) request.getAttribute("listflux");

    int i;
    for (i = 0; i < listFlux.size(); i++) {
        JSONObject obj = new JSONObject();

        obj.put("id", listFlux.get(i).getID());
        obj.put("flux", listFlux.get(i).toString());

        array.add(obj);
    }

    out.clear();
    out.print(export.toJSONString());
    System.out.println(export.toJSONString());
    out.flush();

%>