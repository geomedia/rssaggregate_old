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
    <c:if test="${admin=='true'}"><p><a href="${rootpath}TypeFluxSrvl/add">Ajouter</a></p></c:if>
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
                                <c:if test="${admin=='true'}"> <li><a href="${rootpath}TypeFluxSrvl/rem?id=${bean.ID}">Supprimer ce type</a></li></c:if>
                                </ul>
                            </div>

                            <form method="POST" action="${rootpath}TypeFluxSrvl/${action}?id=${bean.ID}" id="beanForm">
                            <label for="denomination">Dénomination<span class="requis">*</span> : </label>
                            <input name="denomination" id="denomination" value="${bean.denomination}"/>
                            <span id="errdenomination" class="erreur"></span>
                            <br />
                            
                            <label>Code : </label>
                            <input type="text" name="codeType" value="${bean.codeType}"/><span class="erreur" id="errcodeType"></span>
                            <br />

                            <label for="description">Description :</label><br>

                            <textarea name="description" id="description" cols="60" rows="15">${bean.description}</textarea>
                            <br />
                            
                            
                            <input type="hidden" name="vue" value="jsonform"/>

                            <input type="submit"/>
                        </form>
                        <script src="${rootpath}AjaxAddModBean.js"></script>

                    </c:when>
                    <c:when test="${action=='read'}">
                        <c:import url="/WEB-INF/inc/editionBean.jsp" />
                        <p><strong>Dénomination : </strong>${bean.denomination}</p>
                        <p><strong>Description : </strong>${bean.description}</p>
                        <p>Flux Appartenant à cet type : </p>
                        <ul>
                            <c:forEach items="${bean.fluxLie}" var="fl">
                                <li><a href="${rootpath}flux/read?id=${fl.ID}">${fl}</li>
                                </c:forEach>
                        </ul>
                    </c:when>
                </c:choose>
            </c:when>
        </c:choose>
    </div>
</div>
<c:import url="/WEB-INF/footerjsp.jsp" />