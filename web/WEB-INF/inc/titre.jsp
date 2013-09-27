<%-- 
    Document   : titre
    Created on : 27 sept. 2013, 11:46:14
    Author     : clem
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<c:set var="beantitle" value="${srlvtname}"></c:set>


<c:choose>
    <c:when test="${action=='recherche' and srlvtname=='journaux'}">
        <c:set var="beantitle" value="journaux"></c:set>
    </c:when>
    <c:when test="${(action=='add' or action=='mod' or action=='read')and srlvtname=='journaux'}">
        <c:set var="beantitle" value="journal"></c:set>
    </c:when>


    <c:when test="${action=='recherche' and srlvtname=='item'}">
        <c:set var="beantitle" value="items"></c:set>
    </c:when>

    <c:when test="${(action=='add' or action=='mod' or action=='read')and srlvtname=='item'}">
        <c:set var="beantitle" value="items"></c:set>
    </c:when>


     <c:when test="${action=='recherche' and srlvtname=='incidents'}">
        <c:set var="beantitle" value="incidents"></c:set>
    </c:when>
    
    <c:when test="${(action=='add' or action=='mod' or action=='read')and srlvtname=='incidents'}">
        <c:set var="beantitle" value="incident"></c:set>
    </c:when>



</c:choose>



<c:if test="${action=='recherche'}"><h1>Liste des <span>${beantitle}</span></h1></c:if>
<c:if test="${action=='add'}"><h1>Ajout d'un <span>${beantitle}</span></h1></c:if>
<c:if test="${action=='mod'}"><h1>Modification d'un <span>${beantitle}</span></h1></c:if>
<c:if test="${action=='read'}"><h1>Consultation d'un <span>${beantitle}</span></h1></c:if>





