<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%-- 
    Document   : index
    Created on : 22 avr. 2013, 14:36:12
    Author     : clem
--%>
<%@page import="javax.el.ValueExpression"%>
<%@page import="rssagregator.servlet.FluxSrvl"%>
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
            <h1>Administration des <span>Comportements de Collecte</span></h1></div></div>


</div>


<div id="sidebar">
    <c:if test="${admin=='true'}"><p><a href="${rootpath}ComportementCollecte/add">Ajouter</a></p></c:if>
    <p><a href="${rootpath}ComportementCollecte/recherche">Liste</a></p>
</div>

<div id="content">
    <div class="post">
        <c:choose >


            <c:when test="${not empty redirmap}">
                <!--redicrection si il y a des erreur-->
                <c:import url="/WEB-INF/redirJspJavascriptPart.jsp" />
            </c:when>

            <c:when test="${empty redirmap}">
                <c:choose>
                    <c:when test="${action=='add' or action=='mod'}">
                        <!--Affichage du formulaire permettant de gérer le comportement lors de action ADD ou MOD-->
                        <ul>
                            <li><a href="${rootpath}ComportementCollecte/rem?id=${bean.ID}">Supprimer le comportement</a></li>
                        </ul>

                        <form method="POST" id="beanForm">
                            <fieldset>
                                <legend>Information propre au comportement</legend>
                                <label>Comportement utilisé par défaut :</label>
                                <input type="checkbox" name="defaut" <c:if test="${bean.defaut}"> checked="true"</c:if> /><br />
                                
                                    <label> Nom du comportement<span class="requis">*</span> : </label>
                                    <input type="text" name="comportement_nom" value="${bean.nom}"/><span class="erreur" id="errcomportement_nom"></span><br/>
                                
                                <label>Description du comportement : </label><br />
                                <textarea name="comportement_desc" rows="10" cols="60">${bean.description}</textarea><br />

                                <label title="Nombre de secondes entre deux levée de flux">Periodicité de collecte<span class="requis">*</span> : </label>
                                <input  name="periodiciteCollecte" type="text" value="${bean.periodiciteCollecte}" /><span class="erreur" id="errperiodiciteCollecte"></span>
                            </fieldset>

                            <fieldset>
                                <legend>Request : </legend>
                                <label>Time Out<span class="requis">*</span> : </label>
                                <input name="requester_time_out" value="${bean.requesteur.timeOut}"/><span class="erreur" id="errrequester_time_out"></span><br />

                                <label>Request Property</label><button type="button"  onclick="addProp();">ajouter</button>

                                <script>
                                    function addProp() {
                                        $('#blocProp').append('<label>cle : </label><input type="text" name="requestPropertyCle" />');
                                        $('#blocProp').append('<label>valeur : </label><input type="text" value="${tab[1]}" name="requestPropertyValue" /><br />');
                                    }

                                </script>
                                <div id="blocProp">
                                    <c:forEach items="${bean.requesteur.requestProperty}" var="tab">
                                        <input type="text" value="${tab[0]}" name="requestPropertyCle" />
                                        <input type="text" value="${tab[1]}" name="requestPropertyValue" /><br />
                                    </c:forEach>
                                </div>


                            </fieldset>

                            <fieldset>

                                <legend>Parseur</legend>
                            </fieldset>

                            <fieldset>
                                <legend>Dedoublonneur</legend>
                                <label>Dédoublonner sur le titre</label>
                                <input type="checkbox" name="dedoub_titre" <c:if test="${bean.dedoubloneur.deboubTitle}"> checked="true"</c:if>"><br />

                                    <label>Dédoublonner sur la description</label>
                                    <input type="checkbox" name="dedoub_description" <c:if test="${bean.dedoubloneur.deboudDesc}"> checked="true"</c:if>/><br/>

                                    <label>Decoublonner sur le lien : </label>
                                    <input type="checkbox" name="dedoubLink" <c:if test="${bean.dedoubloneur.dedoubLink}"> checked="true"</c:if> /> <br />

                                    <label>Dedoublonner sur le guid</label>
                                    <input type="checkbox" name="dedouGUID" <c:if test="${bean.dedoubloneur.dedouGUID}"> checked="true"</c:if> /><br />

                                    <label>Dedoubloner sur la date de publication</label>
                                    <input type="checkbox" name="dedoubDatePub" <c:if test="${bean.dedoubloneur.dedoubDatePub}"> checked="true"</c:if>/><br />

                                    <label>Dedoubloner sur les catégories</label>
                                    <input type="checkbox" name="dedoubCategory" <c:if test="${bean.dedoubloneur.dedoubCategory}"> checked="true"</c:if>/>

                                </fieldset>
                                
                                
                   

                                <br />
                                <input type="hidden" name="vue" value="jsonform" />
                                <input type="submit">
                            </form>
                            <script src="${rootpath}AjaxAddModBean.js"></script>
                    </c:when>
                    <c:when test="${action=='recherche'}">

                        <c:forEach items="${list}" var="compo">
                            <li><a href="${rootpath}ComportementCollecte/read?id=${compo.ID}">${compo}</a></li>
                            </c:forEach>

                    </c:when>
                    <c:when test="${action=='read'}">

                        <c:import url="/WEB-INF/inc/editionBean.jsp" />
                        <p><strong>Nom : </strong>${bean.nom}</p>
                        <p><strong>Description : </strong> ${bean.description}</p>
                        <p><strong>Periodicité de collecte : </strong>${bean.periodiciteCollecte}</p>
                        <p><strong>Time out : </strong>${bean.requesteur.timeOut}</p>
                        <h2>Dedoublonage : </h2>
                        <p><strong>Titre : </strong><c:choose>
                                <c:when test="${bean.dedoubloneur.deboubTitle=='true'}">OUI</c:when>
                                <c:when test="${bean.dedoubloneur.deboubTitle=='false'}">NON</c:when>
                            </c:choose>

                        <p><strong>Description : </strong>
                            <c:choose>
                                <c:when test="${bean.dedoubloneur.deboudDesc=='true'}">OUI</c:when>
                                <c:when test="${bean.dedoubloneur.deboudDesc=='false'}">NON</c:when>
                            </c:choose></p>

                        <p><strong>Lien :</strong>
                            <c:choose>
                                <c:when test="${bean.dedoubloneur.dedoubLink=='true'}">OUI</c:when>
                                <c:when test="${bean.dedoubloneur.dedoubLink=='false'}">NON</c:when>
                            </c:choose>
                        </p>
                        <p><strong>GUID : </strong>
                            <c:choose>
                                <c:when test="${bean.dedoubloneur.dedouGUID=='true'}">OUI</c:when>
                                <c:when test="${bean.dedoubloneur.dedouGUID=='false'}">NON</c:when>
                            </c:choose>
                        </p>

                        <p><strong>Categorie : </strong>
                            <c:choose>
                                <c:when test="${bean.dedoubloneur.dedoubCategory=='true'}">OUI</c:when>
                                <c:when test="${bean.dedoubloneur.dedoubCategory=='false'}">NON</c:when>
                            </c:choose>
                        </p>
                        <p><strong>Date de publication : </strong>
                            <c:choose>
                                <c:when test="${bean.dedoubloneur.dedoubDatePub=='true'}">OUI</c:when>
                                <c:when test="${bean.dedoubloneur.dedoubDatePub=='false'}">NON</c:when>
                            </c:choose></p>

                        <p><strong>Liste des flux controlé</strong></p>
                        <ul>
                            <c:forEach items="${bean.listeFlux}" var="fl">
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