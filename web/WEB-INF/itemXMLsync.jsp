<%-- 
    Document   : itemXMLsync
    Created on : 2 août 2013, 15:39:23
    Author     : clem
--%>

<%@page import="rssagregator.utils.XMLTool"%>
<%@page import="rssagregator.beans.Item"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="application/xml; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    List<Item> list = (List<Item>) request.getAttribute("listItem");
    String retour = XMLTool.serialise(list);
    out.clear();
        out.print(retour);
    out.flush();

%>