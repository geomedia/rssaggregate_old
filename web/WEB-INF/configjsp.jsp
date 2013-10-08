 
<!--Document   : index
Created on : 22 avr. 2013, 14:36:12
Author     : clem-->

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!--Inclusion du menu haut-->
<c:import url="/WEB-INF/headerjsp.jsp" />

<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            <!--<h1>Administration des <span>Flux</span></h1></div></div>-->
            <h1>Administration du<span> serveur</span></h1>


        </div></div></div>


<div id="sidebar">
    <p><a href="${rootpath}config">générale</a></p>
    <p><a href="${rootpath}ComportementCollecte">Gérer les compotement </a></p>
    <p><a href="${rootpath}TypeFluxSrvl">Gérer les types de flux </a></p>
    <p><a href="${rootpath}user">Gérer les utilisateurs </a></p>
    <p><a href="${rootpath}slave">Gérer les serveurs esclave</a></p>
    <c:if test="${!master}"><p><a href="${rootpath}config/importflux">Obtenir flux du serveur maitre</a></p></c:if>
    <c:if test="${master}"><p><a href="${rootpath}config/importitem">Récupérer les données des serveurs esclaces</a></p></c:if>
                    </div> 

                    <div id="content">
                        <div class="post">

                        <c:choose >
                            <c:when test="${not empty redirmap}">
                                <c:import url="/WEB-INF/redirJspJavascriptPart.jsp" />
                            </c:when>


                            <c:when test="${empty redirmap}">
                                <c:choose>
                                    <c:when test="${action=='mod'}">

                                        <p>La majorité des paramètre de configuration de l'aggrégateur doivent être configuré à l'aide de fichiers systèmes. Voir la doc administrateur</p>
                                        <form method="post" action="${rootpath}config/${action}" id="beanForm">
                                            <fieldset>
                                                <legend>Paramètres du serveur:</legend>

                                                <label for="">Collecte active :   </label>
                                                <input type="checkbox" id="active" name="active"<c:if test="${conf.active=='true'}"> checked="true"</c:if>/>
                                                    <br />

                                                    <label>Nom du serveur</label>
                                                    <input type="text" name="servname" value="<c:out value="${form.erreurs['servname'][0]}" default="${conf.servname}"/>"/>
                                                <span class="erreur" id="errservname"></span>
                                                <br />

                                                <label>JMS provider :</label>
                                                <input type="text" name="jmsprovider" value="<c:out value="${form.erreurs['jmsprovider'][0]}" default="${conf.jmsprovider}"/>" />
                                                <span class="erreur" id="errjmsprovider"></span><br />

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
                                                <input type="hidden" name="id" value="1"/>

                                                <input type="hidden" name="vue" value="jsonform" />

                                            </form>
                                            <script src="${rootpath}AjaxAddModBean.js"></script>
                                        <script src="${rootpath}changePass.js"></script> 

                                        <script>
                                                    $(document).ready(function() {
                                                        masterSelect();
                                                    })


                                                    // Cette fonction est executé lors du click sur la checkbox permettant de définir le serveur comme master
                                                    //                                                var serveurSlave = [
                                            <%--<c:forEach items="${conf.serveurSlave}" var="serv">--%>
                                                //                                                {
                                                //                                                login: '${serv.login}',
                                                //                                                pass: '${serv.pass}',
                                                //                                                host: '${serv.servHost}',
                                                //                                                urlServletRecup: '${serv.url}',
                                                //                                                },
                                            <%--</c:forEach>--%>
                                                //                                                ];
                                                function masterSelect() {
                                                    if ($('#master').prop('checked')) {
                                                        $('#divSlave').empty();



                                                        //                                            $('#divSlave').append('<button type="button" onclick="addSlave()">ajouter des serveurs esclaves</button>');

                                                        //                                            for (i = 0; i < serveurSlave.length; i++) {
                                                        //                                            s = serveurSlave[i];

                                                        //                                            $('#divSlave').append('<div id="slave' + i + '"><label title="ip ou le dns du serveur a joindre">host :</label><input name="hostslave" value="' + s['host'] + '"/> \n\
                                                        //                                            <label>login :</label><input type="text" name="loginSlave" value="' + s['login'] + '" /> \n\
                                                        //                                            <label>pass : </label><input type="text" name="passSlave" value="' + s['pass'] + '" />\n\
                                                        //                                            <label title="exemple : http://172.17.200.197:8080/RSSAgregate">Url application  : </label><input type="text" name="urlSlave" value="' + s['urlServletRecup'] + '"/>\n\
                                                        //                                            <button value="' + i + '" type="button" onclick="remSlave(this)">Supprimer</button></div>');
                                                        //                                            }
                                                    }
                                                    else {
                                                        $('#divSlave').empty();
                                                        $('#divSlave').append('<label>Host du serveur maître</label><input type="text" name="hostMaster" value="${conf.hostMaster}" />\n\
                                        <br /><label>Durée avant purge</label><input type="text" name="purgeDuration" value="${conf.purgeDuration}" /><span class="erreur"> ${form.erreurs['purgeDuration'][1]}</span>');
                                                    }
                                                }


                                                //Fonction utilisé pour ajouter un serveur esclave dans la page
                                                //                                            function addSlave() {
                                                //                                            nbenfants = $('#divSlave div').length;
                                                //                                            $('#divSlave').append('<div id="slave' + nbenfants + '"><label>host :</label><input name="hostslave" value=""/> \n\
                                                //                                            <label>login :</label><input type="text" name="loginSlave" value="" /> \n\
                                                //                                            <label>pass : </label><input type="text" name="passSlave" value="" />\n\
                                                //                                            <label>Url Recupération : </label><input type="text" name="urlSlave" value=""/>\n\
                                                //                                            <button value="' + nbenfants + '" type="button" onclick="remSlave(this)">Supprimer</button></div>');
                                                //                                            }
                                                //Fonction utilisé pour supprimer un serveur esclave dans la page

                                                //                                            function remSlave(bt) {
                                                //                                            divname = '#slave' + bt.value;
                                                //                                            $(divname).remove()();
                                                //                                            }

                                        </script>
                                    </c:when>
                                    <c:when test="${action=='importflux'}">
                                        <p>Vous venez d'importer les flux provenant du serveur maitre voici quelques informations sur ce qui a été obtenu. Lors de cet import les items ne sont pas récupérées (votre serveur esclave n'a en effet pas vocation a archiver des items mais simplement à collecter les mêmes flux que le maitre afin d'assurer la pérénité de la collecte)</p>
                                        <h2>Flux importés : </h2>
                                        <ul>
                                            <c:forEach items="${listfluximporte}" var="fl">
                                                <li>${fl}
                                                    <ul>
                                                        <li>Journal : ${fl.journalLie}</li>
                                                        <li>Type de flux : ${fl.typeFlux}</li>
                                                        <li>Comportement : ${fl.mediatorFlux}</li>
                                                    </ul>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                        <!--ACTION : IMPORT ITEM-->
                                        <!--ce bloc est utilisé lorsque qu'on demande la synchronisation manuelle des items des serveur esclaves-->
                                    </c:when>
                                    <c:when test="${action=='importitem'}">
                                        <h2>Liste des items rapportées par synchronisation : </h2>

                                        <c:forEach items="${tacheGenerale.synchroSlave}" var="synch">
                                            <h2>Recup sur le serveur : ${synch.serveurSlave}</h2>
                                            <p>Etat : <c:if test="${synch.exeption==null}">OK</c:if><c:if test="${synch.exeption!=null}">ERREUR</c:if></p>
                                                <ul>
                                                <c:forEach items="${synch.itemTrouvees}" var="it">
                                                    <li>${it}</li>
                                                    </c:forEach>
                                            </ul>

                                        </c:forEach>

                                    </c:when>

                                    <c:when test="${action=='read'}">
                                        <c:if test="${admin=='true'}">
                                            <p><a href="${rootpath}${srlvtname}/mod?id=1">Editer</a></p>
                                        </c:if>
                                        <p><strong>Nom du serveur : </strong>${conf.servname}</p>
                                        <p><strong>Maitre : </strong><c:if test="${conf.master==true}">OUI</c:if><c:if test="${conf.master==false}">NON</c:if></p>

                                    </c:when>


                                </c:choose>


                            </c:when>
                        </c:choose>


                    </div>
                </div>

                <c:import url="/WEB-INF/footerjsp.jsp" />