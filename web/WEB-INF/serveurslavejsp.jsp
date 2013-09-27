<%-- 

CETTE JSP N'EST PLUS UTILISE LA GESTION DES SERVEUR ESCLAVE SE FAIT MAINTENANT DIRECTEMENT PAR LA CONF
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
            <h1>Administration des <span>Serveur Esclaves</span></h1></div></div>


</div>
<div id="sidebar">
    <p><a href="${rootpath}slave/add">Ajouter</a></p>
    <p><a href="${rootpath}slave/recherche">Liste</a></p>
</div>

<div id="content">
    <div class="post">
        <c:choose >
            <c:when test="${not empty redirmap}">
                <c:import url="/WEB-INF/redirJspJavascriptPart.jsp" />
            </c:when>
            <c:when test="${empty redirmap}">
                <c:choose>
                    <c:when test="${action=='add' or action=='mod'}">
                        <c:if test="${action=='mod'}"><p><a href="${rootpath}slave/rem?id=${bean.ID}">Supprimer</a></p></c:if>
                            <form method="POST">
                                <label>Host :</label>
                                <input type="text" name="servHost" value="${bean.servHost}"/><br/>

                            <label>Login : </label>
                            <input type="text" name="login" value="${bean.login}" /><br />

                            <label>Pass : </label>
                            <input type="text" name="pass" value="${bean.pass}" /><br />

                            <label for="url">Url de l'application : </label>
                            <input type="text" name="url" id="url" value="${bean.url}"/><br />
                            <input type="submit" />
                        </form>
                    </c:when>

                    <c:when test="${action=='recherche'}">
                        <ul>
                            <c:forEach items="${list}" var="serv">
                                <li><a href="${rootpath}slave/read?id=${serv.ID}">${serv.servHost}</a></li>
                                </c:forEach>
                        </ul>
                    </c:when>
                        
                    <c:when test="${action=='read'}">
                        <c:import url="/WEB-INF/inc/editionBean.jsp" />
                        <p><strong>Host :</strong> ${bean.servHost}</p>
                        <p><strong>Login : </strong>${bean.login}</p>
                        <p><strong>URL application :</strong> ${bean.url}</p>
                        <p></p>
                    </c:when>
                </c:choose>
            </c:when>
        </c:choose>
    </div>

</div>

<c:import url="/WEB-INF/footerjsp.jsp" />