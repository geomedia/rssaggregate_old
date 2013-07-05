<%-- 
    Document   : simpleadd
    Created on : 26 juin 2013, 11:11:51
    Author     : clem
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <ul>
            <li><a href="Simpleadd?action=addcompo">Add un comportement basique</a></li>
            <li><a href="Simpleadd?action=addjournal">Add un journal sans nom</a></li>
            <li><a href="Simpleadd?action=vider">Vider</a></li>
            <li><a href="Simpleadd?action=add">Ajout</a></li>
            <li><a href="Simpleadd?action=recolte">recolte</a></li>
        </ul>
        
        <c:if test="${action=='add'}">
            
  
        
        <form method="POST">
            <label>Liste URL : </label>
            <textarea name="txt" id="txt" rows="20" cols="60"></textarea><br/>

            <label>Comportement</label>
            
            <select name="comportement">
                <c:forEach items="${listcompo}" var="compo">
                    <option value="${compo.ID}">${compo}</option>
                </c:forEach>
            </select> <br />
            

            <label>Journal : </label>
            <select name="journal">
                <c:forEach items="${listjournaux}" var="j">
                    <option value="${j.ID}">${j.nom}</option>
                </c:forEach>
            </select><br />
            
            <label>Periodicite : </label>
            <input type="text" name="periodicite"/>
                   

            <input type="submit" />
        </form>
                  </c:if>

    </body>
</html>
