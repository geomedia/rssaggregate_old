<%-- 
Cette JSP doit être inclue au sein du bloc read de chacune des autres JSP. Elle permet de faire apparaitre le bouton éditer si l'utilisateur est un administrateur.
    Document   : incEditionBean
    Created on : 17 sept. 2013, 10:47:16
    Author     : clem
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:if test="${admin=='true'}">
    <p><a href="${rootpath}${srlvtname}/mod?id=${bean.ID}">Editer</a></p>
</c:if>