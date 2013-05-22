<%-- 
    Document   : incidentJsp
    Created on : 22 mai 2013, 14:53:49
    Author     : clem
--%>

<%@page import="servlet.IncidentsSrvl"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>  <!--Il faut bien utiliser la vesion 1.1 d ela jstl l'autre ne permet pas d'utiliser les EL-->

<c:import url="/WEB-INF/headerjsp.jsp" />



<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            <h1>Administration des <span>Incidents</span></h1></div></div>


</div>

<div id="content">
    <div class="post">
        <c:choose>
            <c:when test="${action=='list'}">
                <h2>Liste des incidents</h2>
                <ul>
                <c:forEach items="${listobj}" var="incid">
                    
                    
                    <li><a href="incidents?action=read&id=${incid.ID}">${incid.messageEreur}</a> ${incid.fluxLie.url}
                    </li>
                </c:forEach>
                </ul>
                
            </c:when>
                
                <c:when test="${action=='read'}">
                    ${incident.messageEreur}
                    ${incid.fluxLie.url}
                </c:when>
            
            
            
        </c:choose>
    </div>
</div>



<c:import url="/WEB-INF/footerjsp.jsp" />