<%-- 
    Document   : serviceCtrl
    Created on : 9 déc. 2013, 17:04:01
    Author     : clem
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        
                <style>
            
            table {
 border-width:1px; 
 border-style:solid; 
 border-color:black;
 width:50%;
  border-collapse:collapse
 }
td { 
 border-width:1px;
 border-style:solid; 
 border-color: black;
 width:50%;
 }

            
        </style>
        
    </head>
    <body>
        <h1>Controle des services</h1>

        <form>
            <select name="servicename">
                <option value="ServiceCollecteur">ServiceCollecteur</option>
                <option value="ServiceMailNotifier">ServiceMailNotifier</option>
                <option value="ServiceServer">ServiceServer</option>
            </select>
            
            <input type="submit" />
            
        </form>
        


        <h1>${service}</h1>


        <h2>Taches</h2>

        <table>

            <tr>
                <th>  </th>
                <th>Running</th>
                <th>Durée</th>
                <th>Last Exe</th>
                <th>Next Exe</th>
                <th>Exception</th>
            </tr>
    
                
                <c:forEach items="${service.mapTache.keySet()}" var="tache">
                    <tr>
                        <td>${tache}</td>
                        <td>${tache.running}</td>
                        <td> <c:if test="${tache.running eq true}">${tache.returnExecutionDuration()}</c:if></td>
                        <td><fmt:formatDate value="${tache.lasExecution}" pattern="dd/MM/yyyy HH:mm ss"/></td>
                        <td><fmt:formatDate value="${tache.nextExecution}" pattern="dd/MM/yyyy HH:mm ss"/></td>
                        <td>${tache.exeption}</td>

                    </tr>
                </c:forEach>



     

        </table>

    </body>
</html>
