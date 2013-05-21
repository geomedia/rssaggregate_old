<%-- 
    Document   : index
    Created on : 22 avr. 2013, 14:36:12
    Author     : clem
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>  <!--Il faut bien utiliser la vesion 1.1 d ela jstl l'autre ne permet pas d'utiliser les EL-->
<%--<%@page contentType="text/html" pageEncoding="UTF-8"%>--%>
<!--Inclusion du menu haut-->
<c:import url="/WEB-INF/headerjsp.jsp" />

<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            <h1>Administration des <span>Item</span></h1></div></div>

</div>


<div id="content">


    <div class="post">
        <h1>Administration des Item</h1>
        <p>Truc</p>
    </div>


    <c:choose>
        <c:when test="${action=='read'}">
            <h2>${item.titre}</h2>
            
            <p>
                Provenance : 
            <ul>
                
                <c:forEach items="${item.listFlux}" var="flux">
                    <li><a href="flux?action=read-item&id=${flux.ID}">${flux.url}</a></li>
           
                </c:forEach>
                
            </ul>
            </p>
            
            
            <p>Date pub : <fmt:formatDate value="${item.datePub}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
            <p>Date récup <fmt:formatDate value="${item.dateRecup}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
            <p>Guid : ${item.guid}</p>
            <p>contenu : ${item.contenu}</p>


            <p>Description ${item.description}</p>
            <p></p>


        </c:when>

    </c:choose>

</div>

<c:import url="/WEB-INF/footerjsp.jsp" />