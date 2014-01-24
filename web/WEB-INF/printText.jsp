<%-- 
    Document   : printText
    Created on : 9 janv. 2014, 14:38:04
    Author     : clem
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/text" pageEncoding="UTF-8"%>

<c:choose>
    <c:when test="${empty exception}">
        <p>Opération réussie</p>

    </c:when>
        <c:when test="${not empty exception}">
            <p>Exeption durant le traitement : </p>
            ${exception}
        </c:when>
        
</c:choose>



${text}


