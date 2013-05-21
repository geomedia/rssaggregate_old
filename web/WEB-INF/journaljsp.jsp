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
            <h1>Administration des <span>Journaux</span></h1></div></div>


</div>
<div id="sidebar">
    <p><a href="journaux?action=add">Ajouter</a></p>
    <p><a href="journaux?action=list">Liste</a></p>
</div>

<div id="content">
    <div class="post">

        <p>
   
        </p>
        
        <c:choose>
            <c:when test="${action=='list'}">
                <ul>
                    <c:forEach items="${listjournaux}" var="it">
                        <li><a href="journaux?action=mod&id=${it.ID}"><c:out value="${it.nom}"></c:out></a></li>

                    </c:forEach>
                </ul>
            </c:when>

            <c:when test="${action=='mod' or action=='add'}">
                            <h2>Administration du journal : ${journal.nom}</h2>
                         <a href="journaux?action=rem&id=${journal.ID}">Supprimer journal</a>
                ${form.resultat}
                <form method="post" action="journaux?action=<c:out value="${action}"></c:out>">
                        <fieldset>
                            <legend>journal</legend>
                            <label for="url">Nom du journal<span class="requis">*</span></label>
                            <input type="text" id="nom" name="nom" value="<c:out value="${journal.nom}" />" size="20" maxlength="60" />
                        <span class="erreur"> ${form.erreurs['nom']}</span>

                        <label for="langue">Langue du journal<span class="requis">*</span></label>
                        <input type="text" id="nom" name="langue" value="<c:out value="${journal.langue}" />" size="20" maxlength="60" />
                        <span class="erreur"> ${form.erreurs['langue']}</span>

                        <br />
                        <label for="note">Note <span class="requis">*</span></label>
                        <br />
                        <input type="hidden" name="id" value="${journal.ID}">

                        <input type="submit" value="Inscription" class="sansLabel" />
                        <br />
                    </fieldset>
                </form>
            </c:when>
        </c:choose>

    </div>
</div>
<c:import url="/WEB-INF/footerjsp.jsp" />