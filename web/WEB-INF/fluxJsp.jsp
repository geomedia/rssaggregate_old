<%-- 
    Document   : index
    Created on : 22 avr. 2013, 14:36:12
    Author     : clem
--%>
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
        <c:choose>
            <c:when test="${action=='list'}">
                <ul>
                    <c:forEach items="${listflux}" var="flux">
                        <li><a href="flux?action=mod&id=${flux.ID}"><c:out value="${flux.url}"></c:out></a></li>
                        </c:forEach>
                </ul>
            </c:when>
            <c:when test="${action=='read-item' or action=='mod' or action=='maj' or action=='read-incident'}">
                <h1>Administration du flux : ${flux.url}</h1>
                <ul>
                    <li><a href="flux?action=read-item&id=${flux.ID}">Parcourir les items du flux</a></li>
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

                        <input type="hidden" name="id" value="${flux.ID}">

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
                            
                           
                            <!--<p>${it.description}</p>-->
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
                
                
                
                <c:when test="${action=='read-incident'}">
                    <h2>Liste des incidets du flux</h2>
                    <c:forEach items="${flux.incidentsLie}" var="incid">
                        <li class="item">
                            <h3>indid</h3>
                            <p>Début : ${incid.dateDebut} fin : ${incid.dateFin}</p>
                            <p>${incid.messageEreur}</p>
                            
                        </li>
                        
                    </c:forEach>
                </c:when>
                
                <c:when test="${action=='maj'}">
                    
                    MAJ
                    <h2>Nouvelles items capturés : </h2>
                    <ul>
                         <c:forEach items="${flux.tacheRechup.nouvellesItems}" var="it">
                        <li class="item"><h3>${it.titre}</h3>
                            <p>${it.description}</p>
                        </li>
                    </c:forEach>
                    </ul>
                    
                </c:when>
                    

        </c:choose>


    </div>
</div>

<c:import url="/WEB-INF/footerjsp.jsp" />