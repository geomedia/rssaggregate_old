<%-- 
    Document   : userHTML
    Created on : 28 août 2013, 12:11:44
    Author     : clem
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:import url="/WEB-INF/headerjsp.jsp" />

<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            <h1>Administration des <span>Utilisateurs</span></h1>
        </div>
    </div> 
</div>
<div id="sidebar">  
    <ul>
        <li><a href="${rootpath}user/add">Ajouter</a></li>
        <li><a href="${rootpath}user/recherche">Listing</a></li>
    </ul>
</div>

<div id="content">
    <div class="post">

        <c:choose >
            <c:when test="${not empty redirmap}">
                <c:import url="/WEB-INF/redirJspJavascriptPart.jsp" />
            </c:when>
            <c:when test="${empty redirmap}">
                <c:choose>
                    <c:when test="${action=='add' or action=='mod'}">



                        <c:choose>
                            <c:when test="${action=='mod' and bean.rootAccount=='true'}">
                                <p>Vous ne pouvez modifier le compte root depuis l'interface web. Veuillez modifier le fichier de configuration approprié</p>
                            </c:when>
                            <c:when test="${action=='add' or action=='mod' and bean.rootAccount=='false'}">
                                <c:if test="${action=='mod'}"><a href="${rootpath}user/rem?id=${bean.ID}">Supprimer l'utilisateur</a></c:if>
                                    <form id="beanForm" method="POST">

                                        <label title="Champ utilisé à titre informatif">Nom de l'utilisateur : </label>
                                        <input type="text" name="username" value="${bean.username}" />
                                    <span class="erreur" id="errusername"></span>
                                    <br />


                                    <label>email : </label><input type="text" name="mail" id="mail" value="${bean.mail}"/>
                                    <span class="erreur" id="errmail"></span>
                                    <br />


                                    <label>Administrateur : </label>
                                    <input type="checkbox" name="adminstatut"<c:if test="${bean.adminstatut=='true'}"> checked="checked"</c:if> />
                                    <span class="erreur" id="erradminstatut"></span>
                                    <br />
                                    
                                    
                                    <label>Reçoit les mails d'alerte :</label>
                                        <input type="checkbox" name="adminMail" <c:if test="${bean.adminMail=='true'}">checked="checked"</c:if>/> 
                                    <span class="erreur" id="erradminMail"></span><br />


                                    <button type="button" id="btchgpass" value="0" onclick="changepass()">changer de mot de passe</button>
                                    <span class="erreur">${form.erreurs['pass'][1]}</span><br />


                                    <div id="chgpass"></div>
                                    <input type="hidden" name="vue" value="jsonform"/>
                                    <button type="button" onclick="subfunction();">Valider</button>

                                    <!--<input type="submit"/>-->
                                </form>          
                                <!--script permettant la gestion du changement de mot de pass-->
                                <script src="${rootpath}changePass.js"></script> 
                                <script src="${rootpath}AjaxAddModBean.js"></script>
                            </c:when>

                        </c:choose>


                    </c:when>
                    <c:when test="${action=='recherche'}">
                        <ul>
                            <c:forEach items="${list}" var="u">
                                <li><a href="${rootpath}user/read?id=${u.ID}">${u}</a></li>
                                </c:forEach>
                        </ul>
                    </c:when>
                    <c:when test="${action=='read'}">
                        <div><a href="${rootpath}user/mod?id=${bean.ID}">EDITER</a></div>

                        <p><strong>Nom : </strong> ${bean.username}</p>
                        <p><strong>Email : </strong> ${bean.mail}</p>
                        <p><strong>Administrateur : </strong> <c:if test="${bean.adminstatut=='true'}">OUI</c:if><c:if test="${bean.adminstatut=='false'}">NON</c:if></p>
                        <p><strong>Compte root : </strong> <c:if test="${bean.rootAccount=='true'}">OUI</c:if><c:if test="${bean.rootAccount=='false'}">NON</c:if></p>
                    </c:when>
                    <c:when test="${action=='ident'}">
                        <form method="POST">
                            <span class="erreur">${err}</span>
                            <label>Email : </label>
                            <input type="text" name="mail"/><br />
                            <label>Mot de passe : </label>
                            <input type="password" name="pass" /><br />
                            <input type="submit"/>
                        </form>
                        Identification
                    </c:when>
                </c:choose>

            </c:when>
        </c:choose>
    </div>
</div>

<c:import url="/WEB-INF/footerjsp.jsp" />