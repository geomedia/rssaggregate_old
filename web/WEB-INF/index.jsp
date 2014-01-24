<%-- 
    Document   : index
    Created on : 22 avr. 2013, 14:36:12
    Author     : clem
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%--<%@page contentType="text/html" pageEncoding="UTF-8"%>--%>
<!--Inclusion du menu haut-->
<c:import url="/WEB-INF/headerjsp.jsp" />

<div id="sidebar">
    <h2>Incidents de collecte ouverts (${nbrIncident})</h2>
    <c:set var="vide" value="true"></c:set>
    <ul>
        <c:forEach items="${incidList}" var="incid" end="10">
            <li><a href="${incid.urlAdmin}">${incid}</a></li>
            <c:set var="vide" value="false"></c:set>
        </c:forEach>
    </ul>
    <c:if test="${vide=='true'}">Aucun incident</c:if>
    <c:if test="${vide=='false'}"><a href="#">Voir tous les incidents</a></c:if>
    
</div>



<div id="content">
    <div class="post">
        <p>
        Le projet ANR Corpus-Geomedia traite de l'actualité mondiale saisie via les flux RSS d'une centaine de journaux dans le monde. 
Sur cette plateforme vous pourriez :
ajouter de nouveaux flux
consulter les items récoltés</p>
        <p>
            Actuellement ${nbrFlux} flux provenant de ${nbrJournaux} journaux de ${nbrPays} pays sont collectés .
            
        </p>


    </div>


</div>

<c:import url="/WEB-INF/footerjsp.jsp" />
<%--<c:out value="<p>Je suis un 'paragraphe'.</p>" escapeXml="false"/>--%>

