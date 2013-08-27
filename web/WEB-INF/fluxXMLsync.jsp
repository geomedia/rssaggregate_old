<%-- 
    Document   : fluxXMLsync Cette vue est utilisée pour fournir un XML permettant de sérialiser l'objet java. Elle permet aux serveur esclave de récupérer les flux en dehors des messages JMS. Cette vue est ainsi impliqué lorsque l'utilisateur click sur recevoir tout les flux dans l'interface web des serveur slave
    Created on : 1 août 2013, 16:30:26
    Author     : clem
--%>

<%@page import="rssagregator.utils.XMLTool"%>
<%@page import="java.util.List"%>
<%@page import="rssagregator.beans.Flux"%>
<%@ page language="java" contentType="application/xml; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    List<Flux> listFlux = (List<Flux>) request.getAttribute("listflux");
    int i;
    String listSerialise = XMLTool.serialise(listFlux);
//    System.out.println("je suis la");
    out.clear();
//    out.print("coucou");
       out.print(listSerialise);
    out.flush();
 
%>