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
    <p><a href="${rootpath}config">générale</a></p>
    <p><a href="${rootpath}ComportementCollecte">Gérer les compotement </a></p>
    <p><a href="${rootpath}TypeFluxSrvl">Gérer les types de flux </a></p>
    <p><a href="${rootpath}slave">Gérer les serveurs esclave</a></p>
</div>

<div id="content">
    <div class="post">

        <h1>Administration du serveur</h1>
        <p></p>

        <c:choose >
            <c:when test="${not empty redirmap}">
                <c:import url="/WEB-INF/redirJspJavascriptPart.jsp" />
            </c:when>
            <c:when test="${empty redirmap}">

        ${form.resultat}
        <c:forEach items="form.erreurs" var="e">
            ${e}
        </c:forEach>
        
        <form method="post" action="${rootpath}config/${action}" id="form">
            <fieldset>
                <legend>Paramètres du serveur:</legend>

                <label for="">Collecte active :   </label>
                <input type="checkbox" id="active" name="active"<c:if test="${conf.active=='true'}"> checked="true"</c:if>/>
                    <br />

                    <label for="nbThreadRecup">Nombre de Thread de collecte : <span class="requis">*</span></label>
                    <input type="text" id="nbThreadRecup" name="nbThreadRecup" value="<c:out value="${form.erreurs['nbThreadRecup'][0]}" default="${conf.nbThreadRecup}"/>" size="20" maxlength="60" />
                <span class="erreur"> ${form.erreurs['nbThreadRecup'][1]}</span>
                <br />

                <label>Nom du serveur</label>
                <input type="text" name="servname" value="<c:out value="${form.erreurs['servname'][0]}" default="${conf.servname}"/>"/>
                <span class="erreur"> ${form.erreurs['servname'][1]}</span>
                <br />

                <label>Login :</label>
                <input name="login" value="<c:out value="${form.erreurs['login'][0]}" default="${conf.login}"/>"/>
                <span class="erreur"> ${form.erreurs['login'][1]}</span>
                <br />


                <label>JMS provider :</label>
                <input type="text" name="jmsprovider" value="<c:out value="${form.erreurs['jmsprovider'][0]}" default="${conf.jmsprovider}"/>" />
                <span class="erreur"> ${form.erreurs['jmsprovider'][1]}</span><br />


                <button type="button" onclick="changepass()" value="0" id="btchgpass">Changer de mot de passe</button>
                <span class="erreur"> ${form.erreurs['pass'][1]}</span>
                <div id="chgpass"></div> <br />


                <label for="master">Serveur Maitre :</label>
                <input type="checkbox" name="master" id="master" <c:if test="${conf.master}"> checked="checked"</c:if> onclick="masterSelect()"/>


                    <fieldset id="divSlave">

                    </fieldset>

                    <br />
                    <button type="button" onclick="subfunction()">Valider</button>
                    <!--<input type="submit" />-->
                </fieldset>

            </form>

            <script>
                $(document).ready(function() {
                masterSelect();
                })

                function changepass() {
                if ($('#btchgpass').val() === '0') {
                $('#chgpass').append('<label>Mot de pass</label><input name="pass1" id="pass1"/> <br /> <label>Retaper : </label><input name="pass2" id="pass2"/>');
                $('#btchgpass').text('Annuler le changement de mot de passe');
                $('#btchgpass').val('1')
                }
                else if ($('#btchgpass').val() === '1') {
                $('#btchgpass').val(0); 
                $('#chgpass').empty();
                $('#btchgpass').text('Changer de mot de passe');
                }
                }
                function  subfunction() {
                if ($('#btchgpass').val() === '0') {
                $('#form').submit(); 
                }
                else if ($('#btchgpass').val() === '1') {
                if ($('#pass1').val() === $('#pass2').val()) {
                //Exeptionnellement on fait une vérification de mot saisie coté client. Elle est a réeffectuer coté serveur mais du fait de l'enploi de l'ajout dynamique de la div contenant des mot de passe, on ne peut notifier le client par la voie classique.
                if ($('#pass1').val().length < 3) {
                alert('mot de passe trop court');
                } else {
                $('#form').submit();
                }
                }
                else {
                alert('Les deux mots de pass ne conindent pas !!');
                }
                }
                }


                // Cette fonction est executé lors du click sur la checkbox permettant de définir le serveur comme master
                var serveurSlave = [
            <c:forEach items="${conf.serveurSlave}" var="serv">
                {
                login: '${serv.login}',
                pass: '${serv.pass}',
                host: '${serv.host}',
                urlServletRecup: '${serv.url}'
                },</c:forEach>
                ];
                function masterSelect() {
                if ($('#master').prop('checked')) {
                $('#divSlave').empty();
                $('#divSlave').append('<button type="button" onclick="addSlave()">ajouter des serveurs esclaves</button>');

                for (i = 0; i < serveurSlave.length; i++) {
                s = serveurSlave[i];

                $('#divSlave').append('<div id="slave' + i + '"><label>host :</label><input name="hostslave" value="' + s['host'] + '"/> \n\
                <label>login :</label><input type="text" name="loginSlave" value="' + s['login'] + '" /> \n\
                <label>pass : </label><input type="text" name="passSlave" value="' + s['pass'] + '" />\n\
                <label>Url Recupération : </label><input type="text" name="urlSlave" value="' + s['urlServletRecup'] + '"/>\n\
                <button value="' + i + '" type="button" onclick="remSlave(this)">Supprimer</button></div>');
                }
                }
                else {
                $('#divSlave').empty(); 
                $('#divSlave').append('<label>Host du serveur maître</label><input type="text" name="hostMaster" value="${conf.hostMaster}" />\n\
            <br /><label>Durée avant purge</label><input type="text" name="purgeDuration" value="${conf.purgeDuration}" /><span class="erreur"> ${form.erreurs['purgeDuration'][1]}</span>');
            }
            }


            //Fonction utilisé pour ajouter un serveur esclave dans la page
            function addSlave() {
            nbenfants = $('#divSlave div').length;
            $('#divSlave').append('<div id="slave' + nbenfants + '"><label>host :</label><input name="hostslave" value=""/> \n\
            <label>login :</label><input type="text" name="loginSlave" value="" /> \n\
            <label>pass : </label><input type="text" name="passSlave" value="" />\n\
            <label>Url Recupération : </label><input type="text" name="urlSlave" value=""/>\n\
            <button value="' + nbenfants + '" type="button" onclick="remSlave(this)">Supprimer</button></div>');
            }
            //Fonction utilisé pour supprimer un serveur esclave dans la page

            function remSlave(bt) {
            divname = '#slave' + bt.value;
            $(divname).remove()();
            }

        </script>
                  </c:when>
        </c:choose>


    </div>
</div>

<c:import url="/WEB-INF/footerjsp.jsp" />