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
                        <!--                        Critere
                                                <form method="POST" action="${rootpath}journaux/recherche">
                                                    <label>Pays : </label>
                                                    <select name="pays">
                                                        <option value="">Tous</option>
                        <c:forEach var="country" items="${listCountry}">
                            <option>${country}</option>
                            <option value="${country.key}" <c:if test="${country.key==bean.pays}"> selected="true"</c:if>>${country.value}</option>
                        </c:forEach>
                    </select><br />

                    <label>Langue</label>
                    <select name="langue">
                        <option value="">Toutes</option>
                        <c:forEach items="${listLocal}" var="loc">
                            <option value="${loc.key}" <c:if test="${loc.key==bean.langue}"> selected="true"</c:if>>${loc.value}</option>>
                        </c:forEach>
                    </select>

                    <input type="submit" />
                </form>-->

                        <!--<script src="js/jquery-1.4.2.min.js" type="text/javascript"></script>-->
                        <script src="${rootpath}ress/jqgrid/js/i18n/grid.locale-fr.js" type="text/javascript"></script>
                        <script src="${rootpath}ress/jqgrid/js/jquery.jqGrid.min.js" type="text/javascript"></script>

                        <link rel="stylesheet" type="text/css" media="screen" href="css/ui-lightness/jquery-ui-1.7.1.custom.css" />
                        <link rel="stylesheet" type="text/css" media="screen" href="${rootpath}ress/jqgrid/css/ui.jqgrid.css" />
                        <link rel="stylesheet" type="text/css" media="screen" href="${rootpath}ress/jquery-ui-1.10.3.custom/css/base/jquery-ui.css" />




                        <table id="list" width="600"><tr><td></td></tr></table> 
                        <div id="pager"></div> 


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
                                return '<a href = "/RSSAgregate/journaux/read?id=' + rowObjcet[0] + '">' + rowObjcet[1] + '</a>';
                            }

                            $(function() {
                                $("#list").jqGrid({
                                    url: "${rootpath}journaux/list?vue=grid",
                                    datatype: "json",
                                    mtype: "GET",
                                    colNames: ["ID", "nom", "langue", "pays", "typeJournal", "urlAccueil"],
                                    colModel: [
                                        {name: "ID", width: 55, hidden: false, searchoptions: {sopt: ['cn', 'eq']}},
                                        {name: "nom", classtype: 'clem', sorttype: 'float', width: 90, formatter: myLinkFormatter, searchoptions: {sopt: ['cn', 'eq']}},
                                        {name: "langue", index: 'langue', key: true, search: true, width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
                                        {name: "pays", width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
                                        {name: "typeJournal", width: 80, align: "right", stype: 'select', editoptions: {value: {'': 'tous', 'autre': 'autre', 'quotidien': 'quotidien'}}},
                                        {name: "urlAccueil", width: 150, sortable: true, searchoptions: {sopt: ['cn', 'eq']}}
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


                        <!--                        <ul>
                        <c:forEach items="${listjournaux}" var="it">
                            <li><a href="${rootpath}journaux/read?id=${it.ID}"><c:out value="${it.nom}"></c:out></a></li>
                        </c:forEach>
                    </ul>-->
                    </c:when> 

                    <c:when test="${action=='mod' or action=='add'}">
                        <h2>Administration du journal : ${bean.nom}</h2>
                        <c:if test="${action=='mod'}">
                            <ul>
                                <li>  <a href="${rootpath}journaux/rem?id=${bean.ID}">Supprimer journal</a></li>
                                <li>  <a href="${rootpath}flux/list?journal-id=${bean.ID}">Parcourir les flux du journal</a></li>
                                <li><a href="${rootpath}flux/add?journal-id=${bean.ID}">Ajouter un flux au journal</a></li>
                                <li><a href="${rootpath}journaux/discover?id=${bean.ID}" id="linkDiscover">Ajout de tous les flux du journal par découverte</a><div id="sousFormDiscover"></div></li>
                            </ul>
                        </c:if>

                        ${form.resultat}
                        <form method="POST" action="${rootpath}journaux/${action}" id="beanForm">
                            <fieldset>
                                <legend>journal</legend>
                                <label for="url">Nom du journal<span class="requis">*</span></label>
                                <input type="text" id="nom" name="nom" value="<c:out value="${bean.nom}" />" size="20" maxlength="60" />
                                <span class="erreur" id="errnom"></span>
                                <br />


                                <label>Page Accueil du journal <span class="requis">*</span>: </label>
                                <input type="text" name="urlAccueil" value="<c:out value="${bean.urlAccueil}" />"/><span class="erreur" id="errurlAccueil"></span><br />


                                <label title="De nombreux journaux présente un page avec des liens pour chacun des flux RSS disponibles. Cette url sera utilisée si vous utilisez la fonctionnalité de découverte automatique des flux du journal">URL de la page présentant les flux RSS du journal : </label>
                                <input type="text" name="urlHtmlRecapFlux" value="<c:out value="${bean.urlHtmlRecapFlux}"></c:out>" /><span class="erreur" id="errurlHtmlRecapFlux"></span>
                                    <br />
                                    <label for="langue">Langue : <span class="requis">*</span></label>
                                    <select name="langue" id="langue">
                                        <option></option>
                                    <c:forEach items="${listLocal}" var="loc">
                                        <option value="${loc.key}" <c:if test="${loc.key==bean.langue}"> selected="true"</c:if>>${loc.value}</option>>
                                    </c:forEach>
                                </select><span class="erreur" id="errlangue"></span>

                                <br />


                                <label title="Renseignez ici le comportement qui doit être utilisé par défault pour les flux appartenant à ce journal. C'est ce comportement qui sera utilisé si vous utilisez la découverte automatique de flux.">Comportement par défault des flux du journal : </label>
                                <select name="comportementParDefaultDesFlux" id="comportementParDefaultDesFlux">
                                    <c:forEach items="${listcomportement}" var="compo">
                                        <option value="${compo.ID}">${compo}</option>
                                    </c:forEach>
                                </select><br />

                                <label>Découverte automatique des flux du journal</label>
                                <input type="checkbox" name="autoUpdateFlux" id="autoUpdateFlux" <c:if test="${bean.autoUpdateFlux=='true'}"> checked="checked="</c:if>/>

                                    <div id="updateDiv"></div>
                                    <br />

                                    <label for="pays">Pays : <span class="requis">*</span></label>
                                    <select name="pays" id="pays">
                                        <option></option>
                                    <c:forEach items="${listCountry}" var="country">
                                        <option value="${country.key}" <c:if test="${country.key==bean.pays}"> selected="true"</c:if>>${country.value}</option>
                                    </c:forEach>
                                </select>  <span class="erreur" id="errpays"></span>

                                <br />






                                <label for="">Fuseau Horaire : </label>
                                <select name="fuseauHorraire">
                                    <option></option>
                                    <c:forEach items="${fuseau}" var="fus">
                                        <option value="${fus}" <c:if test="${fus==bean.fuseauHorraire}"> selected="true"</c:if> >${fus}</option>
                                    </c:forEach>
                                </select><br />

                                <label title="Une variable informative permettant de renseigner la nature du journal en fonction principalement de sa périodicité de parution">Type de journal</label>
                                <select name="typeJournal" id="typeJournal">
                                    <option<c:if test="${bean.typeJournal=='quotidien'}"> selected="selected"</c:if>>quotidien</option>
                                    <option<c:if test="${bean.typeJournal=='hebdomadaire'}"> selected="selected"</c:if>>hebdomadaire</option>
                                    <option<c:if test="${bean.typeJournal=='mensuel'}"> selected="selected"</c:if>>mensuel</option>
                                    <option<c:if test="${bean.typeJournal=='pure-player'}"> selected="selected"</c:if>>pure-player</option>
                                    <option<c:if test="${bean.typeJournal=='autre'}"> selected="selected"</c:if>>autre</option>
                                    </select>
                                    <span class="erreur" id="errtypeJournal"></span>

                                    <br />

                                    <label>Information : </label><br />
                                    <textarea name="information" rows="10" cols="60">${bean.information}</textarea><br />

                                <input type="hidden" id="id" name="id" value="${bean.ID}">
                                <input type="hidden" name="vue" value="jsonform" />

                                <input type="submit" value="Inscription" class="sansLabel" />
                                <br />
                            </fieldset>
                        </form>

                        <script src="${rootpath}AjaxAddModBean.js"></script>
                        <script src="${rootpath}journalJs.js"></script>




                    </c:when>
                    <c:when test="${action=='read'}">
                        <c:import url="/WEB-INF/inc/editionBean.jsp" />
                        <p><strong>Titre :</strong> ${bean.nom}</p>
                        <p><strong>Page accueil : </strong>${bean.urlAccueil}</p>
                        <p><strong>Page HTML recaptulatif des flux : </strong>${bean.urlHtmlRecapFlux}</p>
                        <p><strong>Type de journal : </strong> ${bean.typeJournal}</p>
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

                    <c:when test="${action=='discover'}">
                        <h1>Découverte de Flux</h1>
                        <p>Liste des flux découverts et ajouté</p>
                        <ul>
                            <c:forEach items="${tacheDecouverte.listFluxDecouvert}" var="fl">
                                <li><a href="${rootpath}flux?read?id=${fl.ID}">${fl}</a></li>

                            </c:forEach>
                        </ul>
                    </c:when>
                </c:choose>

            </c:when>
        </c:choose>

    </div>
</div>
<c:import url="/WEB-INF/footerjsp.jsp" />