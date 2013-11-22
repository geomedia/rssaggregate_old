<%-- 
    Document   : index
    Created on : 22 avr. 2013, 14:36:12
    Author     : clem
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>  <!--Il faut bien utiliser la vesion 1.1 d ela jstl l'autre ne permet pas d'utiliser les EL-->
<%--<%@page contentType="text/html" pageEncoding="UTF-8"%>--%>
<!--Inclusion du menu haut-->
<c:import url="/WEB-INF/headerjsp.jsp" />

<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            <c:import url="/WEB-INF/inc/titre.jsp" />

        </div></div>

</div>
<!--<script src="simplePagination/jquery-1.7.2.min.js"></script>-->

<!--<script src="js/scripts.js"></script>-->
<link type="text/css" rel="stylesheet" href="simplePagination/simplePagination.css"/>

<div id="content">

    <c:choose>
        <c:when test="${action=='read'}">
            <div class="post">
                <h2><a href="${item.link}">${item.titre}</a></h2>
                <p>
                    Provenance : 
                <ul>

                    <c:forEach items="${item.listFlux}" var="flux">
                        <li><a href="${flux.readURL}">${flux}</a></li>
                        </c:forEach>

                </ul>
                </p>
                <p>Date pub : <fmt:formatDate value="${item.datePub}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
                <p>Date récup <fmt:formatDate value="${item.dateRecup}" pattern="dd/MM/yyyy hh:mm:ss"/></p>
                <p>Guid : ${item.guid}</p>
                <p>contenu : ${item.contenu}</p>


                <p>Description ${item.description}</p>
                <p></p>
            </div>
        </c:when>



        <c:when test="${action=='recherche'}">

            <!--            <link rel="stylesheet" href="jquery-ui.css" />
                        <script src="jquery-ui.js"></script>-->

            <script>
                $(function() {
                    $(".datepicker").datepicker({dateFormat: "yy-mm-dd"});
                });</script>



            <div class="post">
                <h1>Liste des items</h1>
                <div>


                    <form method="GET" id="pagina" action="${rootpath}item/list">


                        <input type="hidden" id="firstResult" value="0"/>


                        <br />
                        <fieldset>
                            <legend>Affiner la recherche</legend>

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
                                        
                                        <ul id="fluxSelection" name="oldid-flux" style="min-width: 300px; width: 400px" class="connectedSortable">
                                            <c:forEach items="${listflux}" var="fl">
                                                <li class="boxelement" value="${fl.ID}">${fl}</li>                                
                                            </c:forEach>
                                            </li>
                                        </ul>

                                    </td>
                                    <td>

                                        <!--                                        <button type="button" onclick="selectflux();">--></button><br />
                                        <!--<button type="button" onclick="supp();"><--</button>-->
                                    </td>
                                    <td><ul style="max-width: 300px; width: 300px" name="fluxSelection2" id="fluxSelection2" class="connectedSortable"></ul></td>
                                </tr>

                            </table>


                            <script src="dynListJournauxFLux.js"></script>   <!--Le Script permettant de gérer la sélection des flux -->
                            <script src="AjaxItemDynGrid.js"></script> <!--Le script permettant de gérer la Grid d'affichage des Item-->
                            <br />

                            <!--                            <label>Ordonner par : </label>
                                                        <select id="order" name="order">
                                                            <option value=""></option>
                                                            <option value="dateRecup" <c:if test="${param.order=='dateRecup'}">selected="true"</c:if>>Date de récupération</option>
                                                            <option value="datePub" <c:if test="${param.order=='datePub'}"> selected="true"</c:if>>Date de publication</option>
                                                            <option value="listFlux" <c:if test="${param.order=='listFlux'}"> selected="true"</c:if>>Flux</option>
                                                            </select>-->


                                <!--                                <label for="desc">Décroissant</label>
                                                                <input type="checkbox" name="desc" value="true" <c:if test="${param.desc=='true'}"> checked="true"</c:if>/>-->


                                <label for="date1">Date début : </label>
                                <input type="text" name="date1" class="datepicker" id="date1"/>
                                <label for="date2">Date fin : </label>
                                <input type="text" name="date2" class="datepicker" id="date2"/>

                                <input type="button" value="Affiner la sélection" id="afin">

                                <select name="vue" id="vue" onchange="subExport();">
                                    <option value="html">Export de la sélection</option>
                                    <option value="csv">CSV</option>
                                    <option value="csvexpert">CSV Expert</option>
                                    <option value="xls">XLS</option>
                                </select>
                                <!--                                <button type="submit"  formaction="Export" formtarget="_blank">Exporter</button>-->
                                <script>
                function subExport() {
                    if ($('#vue').val() == 'csv' || $('#vue').val() == 'csvexpert' || $('#vue').val() == 'xls') {
                        var old = $('#order').val();
                        $('#order').val('listFlux');
                        //                        $('#afin').click()
                        $('#pagina').submit();
                        $('#order').val(old);
                    }
                }
                                </script>

                            </fieldset>

                        </form>


                        <script src="${rootpath}ress/jqgrid/js/i18n/grid.locale-fr.js" type="text/javascript"></script>
                    <script src="${rootpath}ress/jqgrid/js/jquery.jqGrid.min.js" type="text/javascript"></script>
                    <!--<script src="${rootpath}ress/jqgrid/plugins/grid.addons.js" type="text/javascript"></script>-->

                    <link rel="stylesheet" type="text/css" media="screen" href="css/ui-lightness/jquery-ui-1.7.1.custom.css" />
                    <link rel="stylesheet" type="text/css" media="screen" href="${rootpath}ress/jqgrid/css/ui.jqgrid.css" />
                    <link rel="stylesheet" type="text/css" media="screen" href="${rootpath}ress/jquery-ui-1.10.3.custom/css/base/jquery-ui.css" />

                    <table id="list" width="600"><tr><td></td></tr></table> 
                    <div id="pager"></div> 

                    <div id="mysearch">ssss</div>




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
                    return '<a href = "/RSSAgregate/item/read?id=' + rowObjcet[0] + '">' + rowObjcet[1] + '</a>';
                }
                function  fluxFormatter(cellvalue, options, rowObjcet, l4) {
//                    alert(JSON.stringify(cellvalue))
                    txt = "<ul>";
                    pp="";
                    for (i = 0; i < cellvalue.length; i++) {
                        txt = +"<li>" + cellvalue[i]['val'] + "</li>";
                        pp+= "<div class=\"boxelement\">"+cellvalue[i]['val']+"</div>  ";

                    }
                    txt += "</ul>";
//return "AA";
return pp;
                    return txt;
                }
                /***
                 * Supprimer les balise html coté utilisateur
                 * @param {type} cellvalue
                 * @param {type} options
                 * @param {type} rowObjcet
                 * @param {type} l4
                 * @returns {@exp;@call;$@call;text}
                 */
                function descFormatter(cellvalue, options, rowObjcet, l4) {
                    var d = document;
                    var odv = d.createElement("div");
                    $(odv).append(cellvalue);
                    return $(odv).text();
                }



                $(document).ready(function() {
//                    var myfilter = {groupOp: "AND", rules: []};
//
//// addFilteritem("invdate", "gt", "2007-09-06");
//                    myfilter.rules.push({field: "invdate", op: "gt", data: "2007-09-06"});
//
//// addFilteritem("invdate", "lt", "2007-10-04");
//                    myfilter.rules.push({field: "invdate", op: "lt", data: "2007-10-04"});
//
//// addFilteritem("name", "bw", "test");
//                    myfilter.rules.push({field: "name", op: "bw", data: "test"});

                    var grid = $("#list");

                    grid.jqGrid({
                        loadonce: false,
                        url: "${rootpath}item/list?vue=grid",
                        datatype: "json",
                        mtype: "GET",
                        colNames: ["ID", "titre", "description", "flux"],
                        colModel: [
                            {name: "ID", width: 55, hidden: true},
                            {name: "titre", width: 90, formatter: myLinkFormatter, searchoptions: {sopt: ['cn', 'eq']}},
                            {name: "description", index: 'description', key: true, formatter: descFormatter, search: true, width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
                            {name: "flux", index: 'flux', key: true, search: true, width: 80, formatter: fluxFormatter, align: "right", searchoptions: {sopt: ['cn', 'eq']}}
//                            {name: "pays", width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
//                            {name: "typeJournal", width: 80, align: "right", stype: 'select', editoptions: {value: {'': 'tous', 'autre': 'autre', 'quotidien': 'quotidien'}}},
//                            {name: "urlAccueil", width: 150, sortable: true, searchoptions: {sopt: ['cn', 'eq']}}
                        ],
                        pager: '#pager',
                        rowNum: 10,
                        rowList: [10, 20, 30, 50, 100],
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
                        ident: "\t",
                        height: 500,
                        search: true,
//                        search: {
//                            modal: true,
//                            Find: 'txt recherche',
//                            multipleSearch: true,
//                            sFilter: 'lalalalaa'
//                        },
                        multipleSearch: true,
                        postData: {
                            filters: {groupOp: "AND", rules: [/*{field: "titre", op: "gt", data: "truc"}, {field: "nom", op: "lt", data: "ss"}*/]}
                        }

                    }
                    );
                    grid.jqGrid('navGrid', '#pager', {add: false, edit: false, del: false, search: true, refresh: true},
                    {}, {}, {}, {multipleSearch: true, multipleGroup: true, showQuery: true});

                    optionsSearch = {
                        multipleSearch: true, multipleGroup: true, showQuery: true
                    };


                    jQuery("#list").navGrid('#pager', {add: false, edit: false, del: false, search: true, refresh: true}, {}, {}, {}, optionsSearch).navButtonAdd('#pager',
                            {
                                caption: "'Export To CSV",
                                buttonicon: "ui-icon-add",
                                onClickButton: function() {
                                    opt = {exptype: "jsonstring"};
                                    $("#list").jqGrid('excelExport', {tag: 'csv', url: '${rootpath}item/list?vue=csv'});
                                },
                                position: "last"
                            });

                });

                    </script>                    








                </div>

                <div>
                    <ul id="resudiv">

                    </ul>

                </div>

                <!--                    <ul>    MAINTENANT GERRÉE EN aAJAX-->  
                <%--<c:forEach items="${listItem}" var="ite">--%>
                <!--<li><p>-->
                        <!--<a href="item?action=read&id=${ite.ID}">${ite.titre}</a>-->
                <%--<c:forEach items="${ite.listFlux}" var="fl">--%>
                    <!--"${fl}"--> 
                <%--</c:forEach><fmt:formatDate value="${ite.dateRecup}" pattern="dd/MM/yyyy hh:mm:ss"/>--%>
                <!--</p>-->
                <!--<p>${ite.description}</p>-->
                <!--</li>-->
                <%--</c:forEach>--%>
                <!--</ul>-->


            </div>
        </c:when>

    </c:choose>








    <!--
    
        <script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
        <script src="jqPagination/js/jquery.jqpagination.js"></script>-->





</div>



<!--<script>


                                $(document).ready(function() {

                                    $('.pagination').jqPagination({
                                        link_string: 'item?page={page_number}',
                                        max_page: ${maxPage},
                                        current_page: ${pageCourante},
                                        paged: function(page) {
                                            $('.log').prepend('<li>Requested page ' + page + '</li>');
                                            document.location.href = "item?page=" + page;
                                        }
                                    });

                                });


</script>-->
<c:import url="/WEB-INF/footerjsp.jsp" />