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
    <p><a href="TypeFluxSrvl?action=add">Ajouter</a></p>
    <p><a href="TypeFluxSrvl?action=list">Liste</a></p>
</div>

<div id="content">
    <div class="post">

        <c:choose >
            <c:when test="${not empty redirmap}">

            </c:when>
            <c:when test="${empty redirmap}">

                <c:choose>
                    <c:when test="${action=='list'}">
                        <ul>
                            <c:forEach items="${list}" var="t"> 
                                <li><a href="TypeFluxSrvl?action=mod&id=${t.ID}"> ${t.denomination}</a></li>
                                </c:forEach>
                        </ul>
                    </c:when>
                    
                    <c:when test="${action=='mod'}">
                <form method="post" action="TypeFluxSrvl?action=${action}&id=${obj.ID}">
                    
                    <label for="denomination">Dénomination : </label>
                    <input name="denomination" value="${obj.denomination}"/>

                    
                    <input type="submit"/>


                </form>

            </c:when>
                    
                    
                    
                    
                </c:choose>
            </c:when>

            

        </c:choose>





    </div>
</div>
<c:import url="/WEB-INF/footerjsp.jsp" />