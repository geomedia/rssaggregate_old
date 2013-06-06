<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%--<%@page contentType="text/html" pageEncoding="UTF-8"%>--%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>

        <link href="style.css" type="text/css" rel="stylesheet" media="all"/>
    </head>
    <body>
        <div id="banner">
            <a href ="/RSSAgregate/index">
                <img src="ress/img/img01.jpg"/>
            </a>
        </div>




        <div id="menu-wrapper">
            <div id="menu">
                <ul class="menu">

                    <li<c:if test="${navmenu=='item'}"> class="current_page_item"</c:if>><a href="item">Items</a></li>
                    <li<c:if test="${navmenu=='flux'}"> class="current_page_item"</c:if>><a href="flux">Flux</a></li>
                    <li<c:if test="${navmenu=='journaux'}"> class="current_page_item"</c:if>><a href="journaux">Jounaux</a></li>
                    <li<c:if test="${navmenu=='recap'}"> class="current_page_item"</c:if>><a href="recapActiviteGenerale">Récapitulatif de l'activité</a></li>
                    <li<c:if test="${navmenu=='incident'}"> class="current_page_item"</c:if>><a href="incidents">Incidents</a></li>
                    <li<c:if test="${navmenu=='config'}"> class="current_page_item"</c:if>><a href="config">Configuration générale</a></li>
                </ul>
            </div>
        </div>

        <div id="wrapper">
            <div id="page">
                <div id="page-bgtop">
                    <div id="page-bgbtm">