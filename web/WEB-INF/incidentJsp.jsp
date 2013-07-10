<%-- 
    Document   : incidentJsp
    Created on : 22 mai 2013, 14:53:49
    Author     : clem
--%>

<%@page import="rssagregator.servlet.IncidentsSrvl"%>
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


                        <li><a href="incidents?action=mod&id=${incid.ID}">${incid.fluxLie} : ${incid.messageEreur}</a>
                            <p>   Date début : <fmt:formatDate value="${incid.dateDebut}" pattern="dd/MM/yyyy hh:mm:ss"/>
                                Date fin : <c:if test="${empty incid.dateFin}"><strong>Incident non clos</strong></c:if><fmt:formatDate value="${incid.dateFin}" pattern="dd/MM/yyyy hh:mm:ss"/>
                            Durée : ${incid.duree}
                            </p>
                   
                        </li>
                    </c:forEach>
                </ul>

            </c:when>


            <c:when test="${action=='mod'}">

                <h2>Description de l'incident</h2>
                <p>Flux Impacté : <a href="flux?action=mod&id=${incident.fluxLie.ID}">${incident.fluxLie}</a></p>
                <p>Date début : <fmt:formatDate value="${incident.dateDebut}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
                <p>Date fin : <fmt:formatDate value="${incident.dateFin}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
                <p>Nombre de répétition dans la période : ${incident.nombreTentativeEnEchec}</p>
                


                <p>Message d'erreur : ${incident.messageEreur}</p>
                <p>Log JAVA de l'erreur : ${incident.logErreur}</p>

                <form method="POST" action="incidents?action=mod&id=${incident.ID}">
                    <textarea name="noteIndicent" id="noteIndicent" cols="80" rows="30">${incident.noteIndicent}</textarea><br />
                    <input type="submit">

                </form>


            </c:when>


        </c:choose>
    </div>
</div>



<c:import url="/WEB-INF/footerjsp.jsp" />