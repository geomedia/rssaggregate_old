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
           <c:import url="/WEB-INF/inc/titre.jsp" />
        
        </div></div>


</div>
<div id="sidebar">
    <c:if test="${admin=='true'}"><p><a href="${rootpath}journaux/add">Ajouter</a></p></c:if>
    <p><a href="${rootpath}journaux/recherche">Liste</a></p>
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
                        <ul>
                            <c:forEach items="${listjournaux}" var="it">
                                <li><a href="${rootpath}journaux/read?id=${it.ID}"><c:out value="${it.nom}"></c:out></a></li>
                            </c:forEach>
                        </ul>
                    </c:when> 

                    <c:when test="${action=='mod' or action=='add'}">
                        <h2>Administration du journal : ${bean.nom}</h2>
                        <ul>
                            <li>  <a href="${rootpath}journaux/rem?id=${bean.ID}">Supprimer journal</a></li>
                            <li>  <a href="${rootpath}flux/list?journal-id=${bean.ID}">Parcourir les flux du journal</a></li>
                        </ul>

                        ${form.resultat}
                        <form method="post" action="${rootpath}journaux/${action}">
                            <fieldset>
                                <legend>journal</legend>
                                <label for="url">Nom du journal<span class="requis">*</span></label>
                                <input type="text" id="nom" name="nom" value="<c:out value="${bean.nom}" />" size="20" maxlength="60" />
                                <span class="erreur"> ${form.erreurs['nom']}</span>
                                <br />
                                
                                
                                <label>Page Accueil du journal : </label>
                                <input type="text" name="urlAccueil" value="<c:out value="${bean.urlAccueil}" />"/><br />
                                
                                <label>urlHtmlRecapFlux : </label>
                                <input type="text" name="urlHtmlRecapFlux" value="<c:out value="${bean.urlHtmlRecapFlux}"></c:out>"/>
                                    <br />
                                    <label for="langue">Langue : </label>
                                    <select name="langue" id="langue">
                                    <c:forEach items="${listLocal}" var="loc">
                                        <option value="${loc.key}" <c:if test="${loc.key==bean.langue}"> selected="true"</c:if>>${loc.value}</option>>
                                    </c:forEach>
                                </select>

                                <br />
                                <label for="pays">Pays :</label>
                                <select name="pays" id="pays">
                                    <c:forEach items="${listCountry}" var="country">
                                        <option value="${country.key}" <c:if test="${country.key==bean.pays}"> selected="true"</c:if>>${country.value}</option>
                                    </c:forEach>
                                </select>

                                <br />

                                <label for="">Fuseau Horaire : </label>
                                <select name="fuseauHorraire">
                                    <c:forEach items="${fuseau}" var="fus">
                                        <option value="${fus}" <c:if test="${fus==bean.fuseauHorraire}"> selected="true"</c:if> >${fus}</option>
                                    </c:forEach>
                                </select>


                                <br />

                                <textarea name="information">${bean.information}</textarea>

                                <input type="hidden" name="id" value="${bean.ID}">

                                <input type="submit" value="Inscription" class="sansLabel" />
                                <br />
                            </fieldset>
                        </form>
                    </c:when>
                        <c:when test="${action=='read'}">
                            <c:import url="/WEB-INF/inc/editionBean.jsp" />
                            <p><strong>Titre :</strong> ${bean.nom}</p>
                            <p><strong>Page accueil : </strong>${bean.urlAccueil}</p>
                            <p><strong>Page HTML recaptulatif des flux : </strong>${bean.urlHtmlRecapFlux}</p>
                            <p><strong>Langue : </strong>${bean.langue}</p>
                            <p><strong>Pays : </strong>${bean.pays}</p>
                            <p><strong>Fuseau Horraire : </strong>${bean.fuseauHorraire}</p>
                            <p><strong>Information : </strong>${bean.information}</p>
                            <p><strong>Flux liés : </strong></p>
                            <ul>
                                <c:forEach items="${bean.fluxLie}" var="fl"> 
                                    <li><a href="${rootpath}flux/read?id=${fl.ID}">${fl}</a></li>
                                    
                                </c:forEach>
                            </ul>
                        </c:when>
                </c:choose>

            </c:when>
        </c:choose>

    </div>
</div>
<c:import url="/WEB-INF/footerjsp.jsp" />