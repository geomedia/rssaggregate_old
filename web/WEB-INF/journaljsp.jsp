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
            <c:when test="${not empty redirmap}">
                <p>${form.resultat}
                </p>
                <p>${redirmap['msg']}. Vous serez redirigé dans 3seconde à l'adresse <a href="${redirmap['url']}">${redirmap['url']}</a></p>
                <script type="text/JavaScript">
                    <!--
                    setTimeout("location.href = '${redirmap['url']}';",3000);
                    -->
                </script>

            </c:when>
            <c:when test="${empty redirmap}">


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
                        <ul>
                            <li>  <a href="journaux?action=rem&id=${journal.ID}">Supprimer journal</a></li>
                            <li>  <a href="flux?action=list&journal-id=${journal.ID}">Parcourir les flux du journal</a></li>
                            
                        </ul>
                      
                      
                        ${form.resultat}
                        <form method="post" action="journaux?action=<c:out value="${action}"></c:out>">
                                <fieldset>
                                    <legend>journal</legend>
                                    <label for="url">Nom du journal<span class="requis">*</span></label>
                                    <input type="text" id="nom" name="nom" value="<c:out value="${journal.nom}" />" size="20" maxlength="60" />
                                <span class="erreur"> ${form.erreurs['nom']}</span>

                                <br />
                                
                                
                                <label>urlHtmlRecapFlux : </label>
                                <input type="text" name="urlHtmlRecapFlux" value="<c:out value="${journal.urlHtmlRecapFlux}"></c:out>"/>

                                
                                
                                <br />
                                <label for="langue">Langue : </label>
                                <select name="langue" id="langue">
                                    <c:forEach items="${listLocal}" var="loc">

                                        <option value="${loc.key}" <c:if test="${loc.key==journal.langue}"> selected="true"</c:if>>${loc.value}</option>>

                                    </c:forEach>
                                </select>


                                <br />
                                <label for="pays">Pays :</label>
                                <select name="pays" id="pays">
                                    <c:forEach items="${listCountry}" var="country">
                                        <option value="${country.key}" <c:if test="${country.key==journal.pays}"> selected="true"</c:if>>${country.value}</option>
                                    </c:forEach>

                                </select>

                                <br />

                                <label for="">Fuseau Horaire : </label>
                                <select name="fuseauHorraire">
                                    <c:forEach items="${fuseau}" var="fus">
                                        <option value="${fus}" <c:if test="${fus==journal.fuseauHorraire}"> selected="true"</c:if> >${fus}</option>

                                    </c:forEach>
                                </select>


                                <br />
                                
                                <textarea name="information">${journal.information}</textarea>
                                
                                <input type="hidden" name="id" value="${journal.ID}">

                                <input type="submit" value="Inscription" class="sansLabel" />
                                <br />
                            </fieldset>
                        </form>
                    </c:when>
                </c:choose>

            </c:when>
        </c:choose>





    </div>
</div>
<c:import url="/WEB-INF/footerjsp.jsp" />