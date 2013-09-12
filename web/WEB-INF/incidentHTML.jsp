<%-- 
    Document   : incidentJsp
    Created on : 22 mai 2013, 14:53:49
    Author     : clem
--%>

<%@page import="rssagregator.servlet.IncidentsSrvl"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>  <!--Il faut bien utiliser la vesion 1.1 d ela jstl l'autre ne permet pas d'utiliser les EL-->

<c:import url="/WEB-INF/headerjsp.jsp" />



<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            <h1>Administration des <span>Incidents</span></h1></div></div>


</div>

<div id="content"> 
    <div class="post">

        <c:choose>
            <c:when test="${not empty redirmap}">
                <c:import url="/WEB-INF/redirJspJavascriptPart.jsp" />
            </c:when>

            <c:when test="${empty redirmap}">
                <c:choose>
                    <c:when test="${action=='recherche'}">
                        <h2>Liste des incidents</h2>

                        <form method="POST" id="pagina">

                            <fieldset>
                                <legend>Pages : </legend>
                                <div>
                                    <span id="btPaginDiv"></span>
                                </div>


                                <input type="hidden" id="firstResult" value="0"/> 

                                <label>Type D'incident :</label>
                                Flux :<input type="radio" name="type" value="CollecteIncident" checked="checked" id="type" />
                                Synchronisation : <input type="radio" name="type" value="SynchroIncident">
                                Mail : <input type="radio" name="type" value="MailIncident"/>
                                
                                <br />
                                <label>Entité par page</label>
                                <select id="itPrPage" name="itPrPage" onChange="this.form.submit();"> 
                                    <c:forEach var="i" begin="25" end="150" step="25">
                                        <option value="${i}" <c:if test="${itPrPage==i}"> selected="selected"</c:if>>${i}</option>
                                    </c:forEach>
                                </select><br />
                                <label>Voir : </label>
                                <input type="radio" id="clos" name="clos" value="true"<c:if test="${clos}"> checked="checked"</c:if> onclick="$('afin').click();">Incident clos
                                <input type="radio" name="clos" value="false"<c:if test="${!clos}"> checked="checked"</c:if> onclick="$('afin').click();">Incident non clos


                                    <button type="button" id="afin" >Affiner</button>
                                </fieldset>

                            </form>
                            <script src="AjaxIncidDyn.js"></script>
                            <ul id="resudiv">

                            </ul>

                    </c:when>


                    <c:when test="${action=='mod'}">

                        <h2>Description de l'incident</h2>
                        <p>Flux Impacté : <a href="flux/mod?id=${incident.fluxLie.ID}">${incident.fluxLie}</a></p>
                        <p>Date début : <fmt:formatDate value="${incident.dateDebut}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
                        <p>Date fin : <fmt:formatDate value="${incident.dateFin}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
                        <p>Nombre de répétition dans la période : ${incident.nombreTentativeEnEchec}</p>
                        <p>Message d'erreur : ${incident.messageEreur}</p>
                        <p>Log JAVA de l'erreur : ${incident.logErreur}</p>

                        <form method="POST" action="${rootpath}incidents/mod?id=${incident.ID}">
                            <label>Clore l'incident : </label><input type="checkbox" name="dateFin"/><br />

                            <textarea name="noteIndicent" id="noteIndicent" cols="80" rows="30">${incident.noteIndicent}</textarea><br />
                            <input type="submit">
                        </form>


                    </c:when>
                    <c:when test="${action=='read'}">

                        <p><a href="${rootpath}incidents/mod?id=${incident.ID}">EDITER</a></p>

                        <c:if test="${incident['class'].simpleName=='FluxIncident'}">
                            <p><strong>Flux impacté : </strong>${incident.fluxLie}</p>
                        </c:if>
                        <c:if test="${incident['class'].simpleName=='ServerIncident'}">
                            <p><strong>Service impacté : </strong>${incident.serviceEnErreur}</p>
                        </c:if>



                        <p><strong>Date début :</strong> <fmt:formatDate value="${incident.dateDebut}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
                        <p><strong>Date fin :</strong> <fmt:formatDate value="${incident.dateFin}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
                        <p><strong>Nombre de répétition dans la période :</strong> ${incident.nombreTentativeEnEchec}</p>
                        <p><strong>Message d'erreur :</strong> ${incident.messageEreur}</p>
                        <p><strong>Log JAVA de l'erreur :</strong> ${incident.logErreur}</p>
                        <p><strong>Commentaire des administrateurs : </strong> ${incident.noteIndicent}</p>
                    </c:when>


                </c:choose>
            </c:when>
        </c:choose>

    </div>
</div>



<c:import url="/WEB-INF/footerjsp.jsp" />