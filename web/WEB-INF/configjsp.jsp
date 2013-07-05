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
            <h1>Administration des <span>Flux</span></h1></div></div>


</div>
<div id="sidebar">
    <p><a href="config">générale</a></p>
    <p><a href="ComportementCollecte">Gérer les compotement </a></p>
    <p><a href="TypeFluxSrvl">Gérer les types de flux </a></p>
    <p><a href="slave">Gérer les serveurs esclave</a></p>
</div>

<div id="content">
    <div class="post">

        <h1>Administration du serveur</h1>
        <p></p>
        ${form.resultat}

        <form method="post" action="config">
            <fieldset>
                <legend>Paramètres du serveur:</legend>

                <label for="">Collecte active :   </label>
                <input type="checkbox" id="active" name="active"<c:if test="${conf.active=='true'}"> checked="true"</c:if>/>
                    <br />

                    <label for="nbThreadRecup">Nombre de Thread de collecte : <span class="requis">*</span></label>
                    <input type="text" id="nbThreadRecup" name="nbThreadRecup" value="<c:out value="${form.erreurs['nbThreadRecup'][0]}" default="${conf.nbThreadRecup}"/>" size="20" maxlength="60" />
                <span class="erreur"> ${form.erreurs['nbThreadRecup'][1]}</span>
                <br />

                <label>Login :</label>
                <input name="login"/>
                <br />

                <label>Changer de mot de pass</label>
                <input name="pass1"/> <br />
                <label>Retaper : </label>
                <input name="pass2"/><br />

                <input type="submit" />
            </fieldset>

        </form>

    </div>
</div>

<c:import url="/WEB-INF/footerjsp.jsp" />