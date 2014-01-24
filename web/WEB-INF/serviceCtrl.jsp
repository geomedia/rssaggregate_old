<%-- 
    Document   : serviceCtrl
    Created on : 9 déc. 2013, 17:04:01
    Author     : clem
--%>

<%@page import="rssagregator.services.tache.AbstrTacheSchedule"%>
<%@page import="org.joda.time.DateTime"%>
<%@page import="org.joda.time.Duration"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Controle des Service</title>

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
                <th title="La tache est en cours d'execution">Running</th>
                <th title="Durée d'execution si la tache est lancée">Durée</th>
                <th title="date de derniere execution de la tache">Last Exe</th>
                <th title="prochine execution de la tache">Next Exe</th>
                <th title="derniere exeption de la tache">Exception</th>
                <th title="Temps entre next exe et maintenant">Retard</th>
            </tr>


            <c:forEach items="${service.tacheGereeParLeService}" var="tache">
                
                <c:set var="taches" value="${tache}" ></c:set>
                <tr>
                    <td>${tache}</td>
                    <td>${tache.running}</td>
                    <td> <c:if test="${tache.running eq true}">${tache.returnExecutionDuration()}</c:if></td>
                    <td><fmt:formatDate value="${tache.lasExecution}" pattern="dd/MM/yyyy HH:mm ss"/></td>
                    <td><fmt:formatDate value="${tache.nextExecution}" pattern="dd/MM/yyyy HH:mm ss"/></td>
                    <td>${tache.exeption}</td>
                    <td>${tache.returnRetard()}

                    </td>

                </tr>
            </c:forEach>





        </table>

    </body>
</html>
