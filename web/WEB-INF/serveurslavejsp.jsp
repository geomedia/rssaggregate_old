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
    <p><a href="${rootpath}slave/list">Liste</a></p>
</div>

<div id="content">
    <div class="post">
        <c:choose >
            <c:when test="${not empty redirmap}">
                <p>${form.resultat}
                </p>
                <p>${redirmap['msg']}. </p>
                <c:if test="${err!='true'}">
                    Vous serez redirigé dans 3 secondes à l'adresse : <a href="${redirmap['url']}">${redirmap['url']}</a>
                    <script type="text/JavaScript">
                        <!--
                        setTimeout("location.href = '${redirmap['url']}';",3000);
                        -->
                    </script>
                </c:if> 
            </c:when>
            <c:when test="${empty redirmap}">
                <c:choose>
                    <c:when test="${action=='add' or action=='mod'}">
                        <c:if test="${action=='mod'}"><p><a href="${rootpath}slave/rem&id=${obj.id}">Supprimer</a></p></c:if>
                        <form method="POST">
                            <label>Host :</label>
                            <input type="text" name="servHost" value="${obj.servHost}"/><br/>

                            <label>Login : </label>
                            <input type="text" name="login"/><br />

                            <label>Pass : </label>
                            <input type="text" name="pass"/>
                            <input type="submit" />
                        </form>
                    </c:when>
                    
                    <c:when test="${action=='list'}">
                        <ul>
                        <c:forEach items="${list}" var="serv">
                            <li><a href="slave?action=mod&id=${serv.id}">${serv.servHost}</a></li>
                        </c:forEach>
                        </ul>
                    </c:when>
                </c:choose>
            </c:when>
        </c:choose>
    </div>

</div>

<c:import url="/WEB-INF/footerjsp.jsp" />