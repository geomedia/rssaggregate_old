<%-- 
    Document   : itemJSONDesc
    Created on : 15 juil. 2013, 18:32:45
    Author     : clem
    DESC        : Cette servlet permet de mettre en force la description des flux au format json. Les paramettre firstresult itprPage et nbItem permettent de construire le paginator en javascript
--%>


<%@page import="java.util.Locale"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="org.joda.time.format.ISODateTimeFormat"%>
<%@page import="org.apache.log4j.helpers.DateTimeDateFormat"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="org.joda.time.DateTime"%>
<%@page import="rssagregator.beans.incident.FluxIncident"%>
<%@page import="rssagregator.beans.Flux"%>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="rssagregator.beans.Item"%>
<%@page import="java.util.List"%>
<%@page import="org.json.simple.JSONArray"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>


<%
    JSONObject export = new JSONObject();

    export.put("firstResult", request.getAttribute("firstResult"));
//    export.put("itPrPage", request.getAttribute("itPrPage"));
    export.put("nbitem", request.getAttribute("nbitem"));

    JSONArray array = new JSONArray();
    export.put("items", array);


    List<FluxIncident> listIncident = (List<FluxIncident>) request.getAttribute("listobj");

    int i;
    for (i = 0; i < listIncident.size(); i++) {
        JSONObject obj = new JSONObject();

        obj.put("id", listIncident.get(i).getID());
        obj.put("messageEreur", listIncident.get(i).getMessageEreur());
        obj.put("flux", listIncident.get(i).getFluxLie().toString());



        //gestion de la date de début de l'incident. On va renvoyer une chaine de caractère formaté dans le json pour facilietr le travail en javascript
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTimeFormatter frenchFmt = fmt.withLocale(Locale.FRENCH);

        if (listIncident.get(i).getDateDebut() != null) {
            DateTime dt = new DateTime(listIncident.get(i).getDateDebut());
            obj.put("dateDebut", frenchFmt.print(dt));
        } else {
            obj.put("dateDebut", "N/A");
        }

        if (listIncident.get(i).getDateFin() != null) {
            DateTime dt = new DateTime(listIncident.get(i).getDateFin());
            obj.put("dateFin", frenchFmt.print(dt));
        } else {
            obj.put("dateFin", "Non Clos");
        }
        array.add(obj);
    }

    out.clear();
    out.print(export.toJSONString());
    System.out.println(export.toJSONString());
    out.flush();

%>