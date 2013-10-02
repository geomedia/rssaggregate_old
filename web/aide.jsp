<%-- 
    Document   : aide
    Created on : 1 oct. 2013, 11:55:04
    Author     : clem
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="navmenu" value="aide" scope="request"></c:set>
<c:import url="/WEB-INF/headerjsp.jsp" />


<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            
        </div>
    </div>
</div>


<div id="content">
    <div class="post">
        <h1>Blabla</h1>
        ${navmenu}
    </div>
</div>

<c:import url="/WEB-INF/footerjsp.jsp" />