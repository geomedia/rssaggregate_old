<%-- 
    Document   : incidentJsp
    Created on : 22 mai 2013, 14:53:49
    Author     : clem
--%>

<%@page import="rssagregator.servlet.IncidentsSrvl"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>  <!--Il faut bien utiliser la vesion 1.1 d ela jstl l'autre ne permet pas d'utiliser les EL-->

<c:import url="/WEB-INF/headerjsp.jsp" />



<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            <c:import url="/WEB-INF/inc/titre.jsp" />
        </div></div>
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
                        <h2>Liste des incidents</h2>

                        <form method="POST" id="pagina" action="${rootpath}incidents/list">
                            <input type="hidden" name="action" value="list"/>
                            <input type="hidden" name="vue" value="jsondesc" />

                            <fieldset>
                                <legend>Pages : </legend>
                                <div>
                                    <span id="btPaginDiv"></span>
                                </div>


                                <input type="hidden" name="firstResult" id="firstResult" value="0"/> 

                                
                                <label>Type D'incident :</label>
                                <label>Tous : </label><input type="radio" name="type" value="AbstrIncident" /><br />
                                <label>Collecte </label>:<input type="radio" name="type" value="CollecteIncident" checked="checked" id="type" /><br />
                                <label>Synchronisation </label>: <input type="radio" name="type" value="SynchroIncident"><br />
                                <label>Serveur :</label> <input type="radio" name="type" value="ServerIncident"><br />
                                <label>Mail :</label> <input type="radio" name="type" value="MailIncident"/>



                                <br />
<!--                                <label>Entité par page</label>
                                <select id="itPrPage" name="itPrPage" onChange="this.form.submit();"> 
                                    <c:forEach var="i" begin="25" end="150" step="25">
                                        <option value="${i}" <c:if test="${itPrPage==i}"> selected="selected"</c:if>>${i}</option>
                                    </c:forEach>
                                </select><br />-->
                                <label>Voir : </label>
                                <input type="radio" id="clos" name="clos" value="true"<c:if test="${clos}"> checked="checked"</c:if> onclick="$('afin').click();">Incident clos
                                <input type="radio" name="clos" value="false"<c:if test="${!clos}"> checked="checked"</c:if> onclick="$('afin').click();">Incident non clos


                                    <br />
                                    <!--<button value="0" name="limiterFlux" id="limiterFlux" type="button">Limiter aux flux</button>-->
<!--                                    <div id="divLimiterFluxContener">
                                        <div id="divLimiterFlux">
                                            <label>Flux lie : </label>
                                            <table>
                                                <caption>Flux de provenance</caption>
                                                <tr>
                                                    <th>Journaux</th>
                                                    <th>Flux disponibles</th>
                                                    <th></th>
                                                    <th>Flux sélectionnés</th>
                                                </tr>
                                                <tr>
                                                    <td>
                                                        <select id="journalSelection" style="width: 300px">
                                                            <option value="null">Journal : </option>
                                                            <option id="tous">tous</option>
                                                        <c:forEach items="${listJournaux}" var="j">
                                                            <option value="${j.ID}">${j.nom}</option>
                                                        </c:forEach>
                                                    </select>
                                                </td>
                                                <td>
                                                    <select id="fluxSelection" name="oldid-flux" style="min-width: 300px; width: 400px" multiple="true">
                                                        <option value="all">Tous</option>
                                                        <c:forEach items="${listflux}" var="fl">
                                                            <option value="${fl.ID}">${fl}</option>                                
                                                        </c:forEach>
                                                    </select>

                                                </td>
                                                <td>

                                                    <button type="button" onclick="selectflux();"></button><br />
                                                    <button type="button" onclick="supp();"><--</button>
                                                </td>
                                                <td><select multiple="true" style="max-width: 300px; width: 300px" name="fluxSelection2" id="fluxSelection2">
                                                        <c:forEach items="${fluxsel}" var="f">
                                                            <option value="${f.ID}">${f}</option>
                                                        </c:forEach>

                                                    </select></td>
                                            </tr>

                                        </table>
                                    </div>
                                </div>-->-->

                                <input type="hidden" name="requestOnStart" id="requestOnStart" value="${requestOnStart}"/>
                                <script src="${rootpath}AjaxIncidDynGrid.js"></script>
                                <script src="${rootpath}dynListJournauxFLux.js"></script>


                                <button type="button" id="afin" >Affiner</button>
                            </fieldset>

                        </form>
                        <div id="disabledElement"></div>


                        <ul id="resudiv">

                        </ul>


                        <table id="list" width="600"><tr><td></td></tr></table> 
                        <div id="pager"></div> 


                        <script src="${rootpath}ress/jqgrid/js/i18n/grid.locale-fr.js" type="text/javascript"></script>
                        <script src="${rootpath}ress/jqgrid/js/jquery.jqGrid.min.js" type="text/javascript"></script>

                        <link rel="stylesheet" type="text/css" media="screen" href="css/ui-lightness/jquery-ui-1.7.1.custom.css" />
                        <link rel="stylesheet" type="text/css" media="screen" href="${rootpath}ress/jqgrid/css/ui.jqgrid.css" />
                        <link rel="stylesheet" type="text/css" media="screen" href="${rootpath}ress/jquery-ui-1.10.3.custom/css/base/jquery-ui.css" />

                        <script type="text/javascript">
                                    /***
                                     *  Utilisé par JQgrid pour formater le champ journal en un lien
                                     * @param {type} cellvalue
                                     * @param {type} options
                                     * @param {type} rowObjcet
                                     * @param {type} l4
                                     * @param {type} l5
                                     * @returns {String}
                                     */
                                    function myLinkFormatter(cellvalue, options, rowObjcet, l4, l5) {
//        alert("0" + rowObjcet[0]);
//        alert("1" + rowObjcet[1])
//        alert("2"+ rowObjcet[2])
        return '<a href = "/RSSAgregate/incidents/read?id=' + rowObjcet[0]+"&type=" +rowObjcet[2]+ '">' + rowObjcet[1] + '</a>';
                                    }

                                    $(function() {
                                        $("#list").jqGrid({
                                            loadonce: false,
                                            url: "${rootpath}incidents/list?vue=grid&type=CollecteIncident",
//                                            url: "${rootpath}item/list?vue=grid",
                                            datatype: "json",
                                            mtype: "GET",
                                            colNames: ["ID", "intitulé","type", "messageEreur", "dateDebut", "dateFin"],
                                            colModel: [
                                                {name: "ID", width: 55, hidden: true, searchoptions: {sopt: ['cn', 'eq']}},
                                                {name: "intitulé", classtype: 'clem', sorttype: 'float', width: 90, formatter: myLinkFormatter, searchoptions: {sopt: ['cn', 'eq']}},
                                                {name: "type", index: 'type', key: false, search: false, width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
                                                {name: "messageEreur", index: 'langue', key: true, search: true, width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
                                                {name: "dateDebut", width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
                                                {name: "dateFin", width: 80, align: "right", stype: 'select', editoptions: {value: {'': 'tous', 'autre': 'autre', 'quotidien': 'quotidien'}}},
                                            ],
                                            pager: "#pager",
                                            rowNum: 10,
                                            rowList: [10, 20, 30],
                                            sortname: "invid",
                                            sortorder: "desc",
                                            viewrecords: true,
                                            gridview: true,
                                            autoencode: true,
                                            caption: "Recherche parmis les journaux",
                                            sortable: true,
                                            sorttype: 'text',
                                            autowidth: true,
                                            exptype: "csvstring",
                                            root: "grid",
                                            ident: "\t"
//                                    filterToolbar: {searchOperators: true},
//                                    search: {
//                                        caption: "Search...",
//                                        Find: "Find",
//                                        Reset: "Reset",
//                                        odata: ['equal', 'not equal', 'less', 'less or equal', 'greater', 'greater or equal', 'begins with', 'does not begin with', 'is in', 'is not in', 'ends with', 'does not end with', 'contains', 'does not contain'],
//                                        groupOps: [{op: "AND", text: "all"}, {op: "OR", text: "any"}],
//                                        matchText: " match",
//                                        rulesText: " rules"
//                                    }
                                        }
                                        );

                                        optionsearch = {searchOperators: true, stringResult: true};
//                                jQuery("#list").jqGrid('filterToolbar', {searchOperators: true});
                                        jQuery("#list").filterToolbar(optionsearch);
                                        jQuery("#list").navGrid('#pager', {edit: false, add: false, del: false, search: false})
                                                .navButtonAdd('#pager', {
                                            caption: "'Export To CSV",
                                            buttonicon: "ui-icon-add",
                                            onClickButton: function() {

                                                opt = {exptype: "jsonstring"};
                                                $("#list").jqGrid('excelExport', {tag: 'csv', url: '${rootpath}journaux/list?vue=csv'});
                                            },
                                            position: "last"
                                        });
                                    });
                        </script>


                    </c:when>


                    <c:when test="${action=='mod'}">

                        <h2>Description de l'incident</h2>
                        <c:if test="${bean['class'].simpleName=='FluxIncident'}"><p>Flux Impacté : <a href="flux/mod?id=${bean.fluxLie.ID}">${bean.fluxLie}</a></p></c:if>
                        <p>Date début : <fmt:formatDate value="${bean.dateDebut}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
                        <p>Date fin : <fmt:formatDate value="${bean.dateFin}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
                        <p>Nombre de répétition dans la période : ${bean.nombreTentativeEnEchec}</p>
                        <p>Message d'erreur : ${bean.messageEreur}</p>
                        <p>Log JAVA de l'erreur : ${bean.logErreur}</p>

                        <form method="POST" action="${rootpath}incidents/mod?id=${bean.ID}">
                            <c:if test="${empty bean.dateFin}">
                                <label>Clore l'incident : </label><input type="checkbox" name="dateFin" /><br />
                            </c:if>


                            <input type="hidden" name="type" value="${bean['class'].simpleName}"/>
                            <textarea name="noteIndicent" id="noteIndicent" cols="80" rows="30">${bean.noteIndicent}</textarea><br />
                            <input type="submit">
                        </form>
                        ${bean['class'].simpleName}
                    </c:when>
                    <c:when test="${action=='read'}">

                        <c:if test="${admin == 'true'}"><p><a href="${rootpath}incidents/mod?id=${bean.ID}&type=${bean['class'].simpleName}">EDITER</a></p></c:if>



                        <c:if test="${bean['class'].simpleName=='CollecteIncident'}">
                            <p><strong>Flux impacté : </strong><a href="${rootpath}flux/read?id=${bean.fluxLie.ID}">${bean.fluxLie}</a></p>
                            </c:if>


                        <c:if test="${bean['class'].simpleName=='ServerIncident'}">

                        </c:if>



                        <p><strong>Date début :</strong> <fmt:formatDate value="${bean.dateDebut}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
                        <p><strong>Date fin :</strong> <fmt:formatDate value="${bean.dateFin}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
                        <p><strong>Nombre de répétition dans la période :</strong> ${bean.nombreTentativeEnEchec}</p>
                        <p><strong>Message d'erreur :</strong> ${bean.messageEreur}</p>
                        <p><strong>Log JAVA de l'erreur :</strong> ${bean.logErreur}</p>
                        <p><strong>Commentaire des administrateurs : </strong> ${bean.noteIndicent}</p>
                    </c:when>


                </c:choose>
            </c:when>
        </c:choose>

    </div>
</div>



<c:import url="/WEB-INF/footerjsp.jsp" />