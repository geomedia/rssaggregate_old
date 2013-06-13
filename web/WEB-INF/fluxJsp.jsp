<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%-- 
    Document   : index
    Created on : 22 avr. 2013, 14:36:12
    Author     : clem
--%>
<%@page import="javax.el.ValueExpression"%>
<%@page import="servlet.FluxSrvl"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>  <!--Il faut bien utiliser la vesion 1.1 d ela jstl l'autre ne permet pas d'utiliser les EL-->
<%--<%@taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>--%>
<%--<%@page contentType="text/html" pageEncoding="UTF-8"%>--%>
<!--Inclusion du menu haut-->
<c:import url="/WEB-INF/headerjsp.jsp" />





<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            <h1>Administration des <span>Flux</span></h1></div></div>


</div>


<div id="sidebar">
    <p><a href="flux?action=add">Ajouter</a></p>
    <p><a href="flux?action=list">Liste</a></p>
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

                    <c:when test="${action=='list'}">




                        <form method="POST">

                            <fieldset>
                                <legend>Pages : </legend>
                                <c:forEach var="i" begin="1" end="${nbitem}" step="${itPrPage}" varStatus="varstat">
                                    <button type="submit" name="firstResult" value="${i-1}">${i} - ${i+varstat.step-1}</button>
                                </c:forEach>

                                <label>Item par page</label>

                                <select name="itPrPage" onChange="this.form.submit();"> 
                                    <c:forEach var="i" begin="10" end="100" step="20">
                                        <option value="${i}" <c:if test="${itPrPage==i}"> selected="true"</c:if>>${i}</option>
                                    </c:forEach>
                                </select> 

                            </fieldset>

                            <fieldset>
                                <legend>Affiner la recherche</legend>
                                <label>Appartenant au journal : </label>
                                <select name="journal-id">
                                    <option value="">TOUS</option>
                                    <c:forEach items="${listjournaux}" var="j">
                                        <option value="${j.ID}" <c:if test="${j.ID==journalid}"> selected="true"</c:if>>${j.nom}</option>    
                                    </c:forEach>
                                </select>
                                <input type="submit" value="Affiner">
                            </fieldset>
                        </form>


                        <ul>
                            <c:forEach items="${listflux}" var="flux">
                                <li><a href="flux?action=mod&id=${flux.ID}"><c:out value="${flux}"></c:out></a></li>
                                </c:forEach>
                        </ul>
                    </c:when>
                    <c:when test="${action=='read-item' or action=='mod' or action=='maj' or action=='read-incident'}">
                        <h1>Administration du flux : ${flux.url}</h1>
                        <ul>
                            <li><a href="item?id-flux=${flux.ID}">Parcourir les items du flux</a></li>
                            <li><a href="flux?action=mod&id=${flux.ID}">Configurer le flux</a></li>
                            <li><a href="flux?action=maj&id=${flux.ID}">Mettre à jour manuellement</a></li>
                            <li><a href="flux?action=rem&id=${flux.ID}">Supprimer le flux</a></li>
                            <li><a href="flux?action=read-incident&id=${flux.ID}">Parcourir les incidents</a></li>
                        </ul>
                    </c:when>
                </c:choose>

                <c:choose>
                    <c:when test="${action=='add' or action=='mod'}">
                        ${form.resultat}
                        <form method="post" action="flux?action=<c:out value="${action}"></c:out>">
                                <fieldset>
                                    <legend>Paramètres :</legend>
                                    <label for="url">URL du flux<span class="requis">*</span></label>
                                    <input type="text" id="url" name="url" value="<c:out value="${form.erreurs['url'][0]}" default="${flux.url}" />" size="20" maxlength="60" />
                                <span class="erreur"> ${form.erreurs['url'][1]}</span>

                                <label for="active">Actif<span class="requis"></span></label>
                                <input type="checkbox" id="active" name="active" <c:if test="${flux.active=='true'}">checked="true"</c:if>/>

                                    <br />
                                    <label for="periodiciteCollecte">Périodicié de la collecte en seconde</label>
                                    <input type="text" id="periodiciteCollecte" name="periodiciteCollecte" value="<c:out value="${flux.periodiciteCollecte}" default="900"/>">
                                <br />

                                <label for="journalLie">Journal :</label>
                                <select name="journalLie">
                                    <c:forEach items="${listjournaux}" var="journal">
                                        <option<c:if test="${journal.nom==flux.journalLie.nom}"> selected="true"</c:if> value="${journal.ID}">${journal.nom}</option>
                                    </c:forEach>
                                </select>


                                <br />
                                <label>Type de flux</label>
                                <select name="typeFlux">
                                    <c:forEach items="${listtypeflux}" var="typeflux">
                                        <option<c:if test="${flux.typeFlux.denomination==typeflux.denomination}"> selected="true" </c:if> value="${typeflux.ID}">${typeflux.denomination}</option>
                                    </c:forEach>
                                </select>
                                <br />
                                
                                <label for="infoCollecte">Information :</label><br />
                                <textarea id="infoCollecte" name="infoCollecte" rows="20" cols="80">${flux.infoCollecte}</textarea>
                                
                                <input type="hidden" name="id" value="${flux.ID}">
                                <br />
                                <input type="submit" value="Enregitrer" class="sansLabel" />
                                <br />
                            </fieldset>
                        </form>
                    </c:when>
                    <c:when test="${action=='read-item'}">

                        <h2>Parcourir les items</h2>
                        <ul>
                            <c:forEach items="${flux.item}" var="it">
                                <li class="item"><h3><a href="item?action=read&id=${it.ID}">${it.titre}</a></h3>

                                    <p>Date mise en ligne : <fmt:formatDate value="${it.datePub}" pattern="dd/MM/yyyy hh:mm:ss"/></p>

                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>


                    <c:when test="${action=='read-incident'}">
                        <h2>Liste des incidets du flux</h2>
                        <c:forEach items="${flux.incidentsLie}" var="incid">
                            
                            <li class="item">
                                <h3><a href="incidents?action=mod&id=${incid.ID}">${incid}</a></h3>
                                <p>Début : ${incid.dateDebut} fin : ${incid.dateFin}</p>
                                <p>${incid.messageEreur}</p>

                            </li>

                        </c:forEach>
                    </c:when>

                    <c:when test="${action=='maj'}">
                        <h2>Nouvelles items capturés : </h2>
                        <ul>
                            <c:set var="rien" value="<li>Collecte terminée avec succès. Aucune Item n'a cependant été trouvé dans le flux.</li>"></c:set>
                            <c:forEach items="${flux.tacheRechup.nouvellesItems}" var="it" varStatus="varstat">
                                <li class="item"><h3>${it.titre}</h3>
                                    <p>${it.description}</p>
                                </li>
                                <c:set var="rien" value=""></c:set>
                            </c:forEach>
                            ${rien}
                        </ul>


                        <c:if test="${flux.tacheRechup.incident!=null}">

                            <p>Erreur lors de la collecte du FLUX</p>
                            ${flux.tacheRechup.incident.messageEreur}
                            <a href="incidents?action=mod&id=${flux.tacheRechup.incident.ID}">Voir le détail de l'incident</a>

                        </c:if>

                    </c:when>
                </c:choose>
            </c:when>
        </c:choose>
    </div>
</div>

<c:import url="/WEB-INF/footerjsp.jsp" />