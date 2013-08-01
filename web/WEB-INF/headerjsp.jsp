<%@page import="rssagregator.services.ServiceJMS"%>
<%@page import="rssagregator.dao.DAOFactory"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%--<%@page contentType="text/html" pageEncoding="UTF-8"%>--%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>

        <link href="/RSSAgregate/style.css" type="text/css" rel="stylesheet" media="all"/>
    </head>
    <body>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>

        <div id="banner">
            <a href ="/RSSAgregate/index">
                <%
                    if(DAOFactory.getInstance().getDAOConf().getConfCourante().getMaster()){
                        out.println("<img src=\"/RSSAgregate/ress/img/logo_mastervert.png\"/>");
                    }else{
                        out.println("<img src=\"/RSSAgregate/ress/img/logo_masterrouge.png\"/>");
                    }
                        
                    %>
    
            </a>
                    Statut JMS : <%
                    if(ServiceJMS.getInstance().getStatutConnection()){
                        out.println("OK");
                    }
                    else{
                        out.println("Erreur");
                    }
                    %>
        </div>

        <c:set var="rootpath" value="/RSSAgregate/" scope="request"></c:set>


        <div id="menu-wrapper">
            <div id="menu">
                <ul class="menu">

                    <li<c:if test="${navmenu=='item'}"> class="current_page_item"</c:if>><a href="${rootpath}item">Items</a></li>
                    <li<c:if test="${navmenu=='flux'}"> class="current_page_item"</c:if>><a href="${rootpath}flux">Flux</a></li>
                    <li<c:if test="${navmenu=='journaux'}"> class="current_page_item"</c:if>><a href="${rootpath}journaux">Jounaux</a></li>
                    <li<c:if test="${navmenu=='recap'}"> class="current_page_item"</c:if>><a href="${rootpath}recapActiviteGenerale">Récapitulatif de l'activité</a></li>
                    <li<c:if test="${navmenu=='incident'}"> class="current_page_item"</c:if>><a href="${rootpath}incidents">Incidents</a></li>
                    <li<c:if test="${navmenu=='config'}"> class="current_page_item"</c:if>><a href="${rootpath}config">Configuration générale</a></li>
                </ul>
            </div>
        </div>

        <div id="wrapper">
            <div id="page">
                <div id="page-bgtop">
                    <div id="page-bgbtm">