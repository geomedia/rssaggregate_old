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


<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            <h1>Administration des <span>Types de Flux</span></h1></div></div>


</div>
<div id="sidebar">
    <p><a href="${rootpath}TypeFluxSrvl/add">Ajouter</a></p>
    <p><a href="${rootpath}TypeFluxSrvl/recherche">Liste</a></p>
</div>

<div id="content">
    <div class="post">

        <c:choose >
            <c:when test="${not empty redirmap}">
                <c:import url="/WEB-INF/redirJspJavascriptPart.jsp" />
            </c:when>
            <c:when test="${empty redirmap}">

                <c:choose>
                    <c:when test="${action=='recherche'}">

                        <ul>
                            <c:forEach items="${list}" var="t"> 
                                <li><a href="${rootpath}TypeFluxSrvl/read?id=${t.ID}"> ${t.denomination}</a></li>
                                </c:forEach>
                        </ul>
                    </c:when>

                    <c:when test="${action=='mod' or action=='add'}">
                        <div>
                            <ul>
                                <li><a href="${rootpath}TypeFluxSrvl/rem?id=${obj.ID}">Supprimer ce type</a></li>
                            </ul>
                        </div>

                        <form method="POST" action="${rootpath}TypeFluxSrvl/${action}?id=${obj.ID}">
                            <label for="denomination">Dénomination : </label>
                            <input name="denomination" value="${obj.denomination}"/>
                            <input type="submit"/>
                        </form>
                    </c:when>
                    <c:when test="${action=='read'}">
                        <p><a href="${rootpath}${srlvtname}/mod?id=${obj.ID}">EDITER</a></p>
                        <p><strong>Dénomination : </strong>${obj.denomination}</p>
                    </c:when>
                </c:choose>
            </c:when>
        </c:choose>
    </div>
</div>
<c:import url="/WEB-INF/footerjsp.jsp" />