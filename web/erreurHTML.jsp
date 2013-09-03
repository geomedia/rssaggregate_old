<%-- 
    Cette JSP est utilisé pour afficher a l'utilisateur qu'il n'a pas le droit d'effectuer l'action demandée. On lui affiche aussi la raison 
    Document   : erreurHTML
    Created on : 27 août 2013, 17:46:18
    Author     : clem
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:import url="/WEB-INF/headerjsp.jsp" />
<!--<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>-->

<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            <h1>Acces <span>refusé !</span></h1></div></div>
</div>

<div id="content">
    <div class="post">
        ${accesmsg}
    </div>
</div>
<c:import url="/WEB-INF/footerjsp.jsp" />