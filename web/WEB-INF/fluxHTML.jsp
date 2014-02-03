<%@page import="java.util.Map"%>
<%@page import="java.nio.charset.Charset"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%-- 
    Document   : index
    Created on : 22 avr. 2013, 14:36:12
    Author     : clem
Cette JSP est utilisée pour afficher les informations relatives aux flux a l'utilisateur sous forme de page HTML. Gestion des différentes actions de l'utilisateur
--%>
<%@page import="javax.el.ValueExpression"%>
<%@page import="rssagregator.servlet.FluxSrvl"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  

<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>  <!--Il faut bien utiliser la vesion 1.1 d ela jstl l'autre ne permet pas d'utiliser les EL-->
<%--<%@taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>--%>
<%--<%@page contentType="text/html" pageEncoding="UTF-8"%>--%>
<!--Inclusion du menu haut-->
<c:import url="/WEB-INF/headerjsp.jsp" />

<script src="${rootpath}AjaxFluxDyn.js"></script>


<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            <c:import url="/WEB-INF/inc/titre.jsp" />
        </div></div>


</div>

<div id="sidebar">
    <c:if test="${admin=='true'}"><p><a href="${rootpath}flux/add">Ajouter</a></p></c:if>

        <p><a href="${rootpath}flux/recherche">Recherche</a></p>
</div>




<div id="content">
    <div class="post">
        <c:choose >
            <c:when test="${not empty redirmap}">
                <p>${form.resultat}
                </p>
                <p>${redirmap['msg']}. </p>
                <c:if test="${err!='true'}">
                    Vous serez redirigé dans 3 secondes à l'adresse : <a href="${rootpath}${redirmap['url']}">${redirmap['url']}</a>
                    <script type="text/JavaScript">
                        <!--
                        setTimeout("location.href = '${rootpath}${redirmap['url']}';",3000);
                        -->
                    </script>
                </c:if>


            </c:when>
            <c:when test="${empty redirmap}">
                <c:choose>


                    <c:when test="${action=='recherche'}">
                        <!--                        <form method="POST" id="pagina">
                                                    <input type="hidden" id="firstResult" value="0"/>
                        
                                                    <fieldset>
                                                        <legend>Pages : </legend>
                        <%--<c:import url="/WEB-INF/paginator.jsp" />--%>
                        <div>
                            <span id="btPaginDiv"></span>
                        </div>

                        <label>Flux par page</label>
                        <select id="itPrPage" name="itPrPage" onChange="$('#afin').click();"> 
                        <c:forEach var="i" begin="10" end="150" step="20">
                            <option value="${i}" <c:if test="${itPrPage==i}"> selected="selected"</c:if>>${i}</option>
                        </c:forEach>
                    </select> 

                </fieldset>

                <fieldset>
                    <legend>Affiner la recherche</legend>
                    <label>Appartenant au journal : </label>
                    <select name="journalid" id="journalid">
                        <option value="">TOUS</option>
                        <c:forEach items="${listjournaux}" var="j">
                            <option value="${j.ID}" <c:if test="${j.ID==journalid}"> selected="selected"</c:if>>${j.nom}</option>    
                        </c:forEach>
                    </select>
                    <button type="button" id="afin">Affiner</button>
                    <input type="submit" value="Affiner" onclick="$('#vue').val('')" id="sub" />

                    <select name="vue" id="vue" onchange="subExport()()">
                        <option value="html">Exporter</option>
                        <option value="opml">opml</option>
                    </select>
                    <script>

                        function subExport() {
                            if ($('#vue').val() == 'opml') {

                                $('#pagina').attr('target', '_blank');
                                $('#pagina').submit();
                                $('#pagina').attr('target', '');
                                $('#vue').val('html');


                            }
                        }
                    </script>
                    <button type="submit"  formaction="flux" formtarget="_blank" value="vue">Exporter</button>
                </fieldset>
            </form>-->
                        <script src="${rootpath}AjaxFluxDyn.js"></script>






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
                                // Lors du classement après recherche sur le client side, le rowObjet ne peut être lu de la même manière. La ligne suivant permet de pallier à ce problème
                                id = rowObjcet[0];
                                texteLien = rowObjcet[1];
                                if (rowObjcet[0] === undefined) {
                                    id = rowObjcet['ID'];
                                    texteLien = rowObjcet['nom'];
                                }
                                return '<a href = "/RSSAgregate/flux/read?id=' + id + '">' + texteLien + '</a>';
                            }

                            $(function() {
                                $("#list").jqGrid({
                                    url: "${rootpath}flux/list?vue=grid",
                                    loadonce: true,
                                    datatype: "json",
                                    mtype: "GET",
                                    colNames: ["ID", 'nom', "Journal", "Type", "active", "created"],
                                    colModel: [
                                        {name: "ID", key: true, width: 55, hidden: true},
                                        {name: "nom", width: 55, search: true, formatter: myLinkFormatter, searchoptions: {sopt: ['cn', 'eq']}},
                                        {name: "journalLie", width: 90, searchoptions: {sopt: ['cn', 'eq']}},
                                        {name: "typeFlux", title: 'Type', search: true, width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
                                        {name: "active", width: 80, align: "right", searchoptions: {sopt: ['cn', 'eq']}},
                                        {name: "created", width: 80, align: "right", stype: 'select', editoptions: {value: {'': 'tous', 'autre': 'autre', 'quotidien': 'quotidien'}}},
                                    ],
                                    pager: "#pager",
                                    rowNum: 10,
                                    rowList: [30, 50, 100, 150, 300, 500],
                                    sortname: "invid",
                                    sortorder: "desc",
                                    viewrecords: true,
                                    gridview: true,
                                    autoencode: true,
                                    caption: "Recherche parmis les flux",
                                    sortable: true,
                                    sorttype: 'text',
                                    autowidth: true,
                                    exptype: "csvstring",
                                    root: "grid",
                                    multiselect: true,
                                    //                                            scrollrows : true,
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
                                        $("#list").jqGrid('excelExport', {tag: 'csv', url: '${rootpath}flux/list?vue=csv'});
                                    },
                                    position: "last"
                                })
                                        .navButtonAdd('#pager',
                                        {
                                            caption: "Supprimer",
                                            buttonicon: "ui-icon-add",
                                            onClickButton: function() {
                                                reponse = confirm('Vous vous apprétez à supprimer un flux. Toutes les items associées seront supprimée. Cette manipulation est irréverssible. Confirmez vous votre choix ?');
                                                if (reponse) {
                                                    selRowId = $('#list').jqGrid('getGridParam', 'selarrrow');
                                                    //chaine = "";
                                                    //for (i = 0; i < selRowId.length; i++) {
                                                    //    chaine += 'id=' + selRowId[i] + ',';
                                                    //}
                                                    //if (chaine.length > 2) {
                                                    //    chaine = chaine.substr(0, chaine.length - 1);
                                                    //}
                                                    //url = ${rootpath} + 'flux/rem?' + chaine;
                                                    url = ${rootpath} + 'flux/rem?id=' + selRowId;
                                                    location.href = url;
                                                }

                                            },
                                        })
                                        .navButtonAdd('#pager', {
                                    caption: "Mise a jour",
                                    buttonicon: "ui-icon-add",
                                    onClickButton: function() {
                                        alert('maj' + formatIdParamFromSelectedRow());
                                        location.href = ${rootpath} + 'flux/maj?' + formatIdParamFromSelectedRow();
                                    }
                                });
                            });
                            /***
                             * Parcours les items sélectionné par dans la grid et renvoi une chaine de caractère sous la forme id=nul,id=num2. Permet de formater les paramettres dans une url
                             * @returns {unresolved} */
                            function formatIdParamFromSelectedRow() {
                                selRowId = $('#list').jqGrid('getGridParam', 'selarrrow');
                                ch = "";
                                for (i = 0; i < selRowId.length; i++) {
                                    ch += 'id=' + selRowId[i] + ',aa';
                                }
                                if (ch.length > 2) {
                                    ch = ch.substr(0, ch.length - 1);
                                }
<!--alert(selRowId);-->
                                return 'id=' + selRowId;
                                return ch;

                            }
                        </script>



                        <!--                        <form id="formaction2">
                                                    <ul id="resudiv">
                        
                                                    </ul>
                                                    <button type="button" value="0" id="bts">Tout sélectionner</button>
                                                    <select name="action" id="act">
                                                        <option value="rem">Supprimer</option>
                                                        <option value="maj">Mettre à jour</option>
                                                    </select>
                        
                                                    <script>
                                                        $('#bts').click(function() {
                        
                                                            if (this.value === '1') {
                                                                this.textContent = 'Déselectionner tout';
                                                                this.value = '0';
                                                                $("#resudiv").find(':checkbox').prop('checked', false);
                                                            }
                                                            else if (this.value === '0') {
                                                                this.textContent = 'Tout sélectionner';
                                                                this.value = '1';
                                                                $("#resudiv").find(':checkbox').prop('checked', true);
                                                            }
                                                        });
                                                    </script>
                                                    <button type="button" onclick="actionsub();"> OK</button>
                                                </form>-->
                        <script>
                            //Petite fonction pour la soumission du formulaire permettant la mise à jour et la suppression en nombre
                            function actionsub() {
                                action = $('#act').val();
                                if (action === 'rem') {
                                    reponse = confirm('Vous vous apprétez à supprimer un flux. Toutes les items associées seront supprimée. Cette manipulation est irréverssible. Confirmez vous votre choix ?');
                                    if (reponse) {
                                        $('#formaction2').attr('action', '${rootpath}flux/' + action);
                                        $('#formaction2').submit();
                                    }
                                }
                                else if (action === 'maj') {
                                    $('#formaction2').attr('action', '${rootpath}flux/' + action);
                                    $('#formaction2').submit();
                                }
                            }

                        </script>
                    </c:when>
                    <c:when test="${action=='read-item' or action=='mod' or action=='read-incident'}">
                        <h1>Administration du flux : ${bean.url}</h1>
                        <ul>
                            <li><a href="${rootpath}item?id-flux=${bean.ID}">Parcourir les items du flux</a></li>
                            <li><a href="${rootpath}flux/mod?id=${bean.ID}">Configurer le flux</a></li>
                            <li><a href="${rootpath}flux/maj?id=${bean.ID}">Mettre à jour manuellement</a></li>
                            <li><a id="suppLink" href="${rootpath}flux/rem?id=${bean.ID}">Supprimer le flux</a></li>
                            <li><a href="${rootpath}incidents/recherche?fluxSelection2=${bean.ID}&type=CollecteIncident">Parcourir les incidents</a></li>
                            <li><a href="${rootpath}flux/importcsv?id=${bean.ID}">Importer des items</a></li>
                            <script>
                                $(document).ready(function() {
                                    $('#suppLink').on('click', function truc2(e) {

                                        reponse = confirm('Vous vous apprétez à supprimer un flux. Toutes les items associées seront supprimée. Cette manipulation est irréverssible. Confirmez vous votre choix ?');
                                        if (reponse) {
                                            return true;
                                        } else {
                                            e.preventDefault();
                                            return false;
                                        }
                                    });
                                });</script>


                        </ul>
                    </c:when> 

                    <c:when test="${action=='highchart'}">



                        <script>
                            $(function() {
                                $(".datepicker").datepicker({dateFormat: "yy-mm-dd"});
                            });</script>



                        <form method="POST" action="${rootpath}item/comptejour?vue=hightchart" id="form">
                            <input type="hidden" name="action" value="print"/>
                            <label for="date1">Date début : </label>
                            <input type="text" name="date1" class="datepicker" id="date1"/>
                            <label for="date2">Date fin : </label>
                            <input type="text" name="date2" class="datepicker" id="date2"/>
                            <br />


                            <label>Flux : </label>

                            <table>
                                <tr>
                                    <td>
                                        <select id="journalSelection">
                                            <option value="null">Journal : </option>
                                            <option id="tous">tous</option>
                                            <c:forEach items="${listjournaux}" var="j">
                                                <option value="${j.ID}">${j.nom}</option>
                                            </c:forEach>

                                        </select>
                                    </td>

                                    <td>

                                        <ul id="fluxSelection" name="oldid-flux" style="min-width: 300px; width: 400px" class="connectedSortable">
                                            <c:forEach items="${listflux}" var="fl">
                                                <li value="${fl.ID}">${fl}</li>                                
                                                </c:forEach>
                                            </li>
                                        </ul>


                                        <!--                                        <select multiple="multiple" name="flux" id="fluxSelection" style="width: 300px">
                                                                                    <option value="">NULL</option>
                                        <c:forEach items="${listFlux}" var="fl">
                                            <option value="${fl.ID}">${fl}</option>
                                        </c:forEach>
                                    </select>-->
                                    </td>
                                    <td>
                                        <button type="button" onclick="selectflux();">--></button><p />

                                        <button type="button" onclick="supp();"><--</button>
                                    </td>
                                    <td>
                                        <ul style="max-width: 300px; width: 300px" name="fluxSelection2" id="fluxSelection2" class="connectedSortable"></ul>
                                        <!--<select id="fluxSelection2" name="fluxSelection2" multiple="multiple" style="width: 300px"></select>-->
                                    </td>
                                </tr>
                            </table>



                            <br />
                            <!--                            <label for="temporalite">Temporalité : </label>
                                                        <select name="temporalite">
                                                            <option>jour</option>
                                                            <option>mois</option>
                                                        </select>-->
                            <input type="submit" />
                        </form>

                        <script src="${rootpath}dynListJournauxFLux.js"></script>  <!--Le script permettant de sélectionner dynamiquement les journaux-->

                        <div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
                        <!--                        <script>
                                                    $.getJSON('http://localhost:8080/RSSAgregate/flux?vue=highchart', function(essence) {
                                                    $('#container').highcharts(essence);
                                                    });
                        
                                                </script>-->

                        <script src="${rootpath}FluxRecapHighChart.js"></script> <!--Le script permettant l'affichage du graphique en utilisant Highchart-->
                        <script src="${rootpath}AjaxFluxDyn.js"></script> <!--Le script permettant l'affichage du graphique en utilisant Highchart-->


                    </c:when>
                </c:choose>

                <c:choose> 
                    <c:when test="${action=='add' or action=='mod'}">
                        ${form.resultat}
                        <form method="POST" action="${rootpath}flux/${action}" id="beanForm">

                            <input type="hidden" value="<c:out value="${action}"></c:out>" name="action">
                                <fieldset>
                                    <legend>Paramètres :</legend>

                                    <label for="active" title="L'agrégateur doit t'il collecter ce flux ?">Actif : <span class="requis"></span></label>
                                    <input type="checkbox" id="active" name="active" <c:if test="${bean.active=='true'}">checked="checked"</c:if>/>

                                    <br />
                                    <label for="url" title="Adresse a laquelle on trouve le XML du flux">URL du flux<span class="requis">*</span></label>
                                    <input type="text" id="url" name="url" value="${bean.url}" size="20" maxlength="2000" />
                                <span class="erreur" id="errurl"></span><br />


                                <label title="Indiquez la page html de la rubrique capturée. Exemple, la page international du monde http://www.lemonde.fr/international/">Page html</label>
                                <input name="htmlUrl" type="text" value="<c:out value="${form.erreurs['htmlUrl'][0]}" default="${bean.htmlUrl}" />"/>
                                <span class="erreur"> ${form.erreurs['htmlUrl'][1]}</span>
                                <span class="erreur" id="errhtmlUrl"></span>
                                <br />

                                <label title="Ensemble de paramettres régulant la collecte du flux. CE PARAMETTRE EST PRIMORDIALE">Comportement de collecte : <span class="requis">*</span></label>
                                <select name="mediatorFlux">
                                    <c:forEach items="${listcomportement}" var="compo">
                                        <option value="${compo.ID}" <c:if test="${bean.mediatorFlux.ID==compo.ID}"> selected="true"</c:if> >${compo}</option>
                                    </c:forEach>
                                </select>
                                <span class="erreur" id="errmediatorFlux"></span>
                                <br />

                                <label for="journalLie" title="Un journal comprends plusieurs flux... Si vous ne trouvez pas le journal concerné, allez dans Journal-> ajouter">Journal :<span class="requis">*</span></label>
                                <select name="journalLie" id="journalLie">
                                    <option value="-1">Aucun</option>
                                    <c:forEach items="${listjournaux}" var="journal">
                                        <option<c:if test="${journal.ID==bean.journalLie.ID}"> selected="selected"</c:if><c:if test="${journal.ID==jSelect.ID}"> selected="selected"</c:if> value="${journal.ID}">${journal}</option>
                                    </c:forEach>
                                </select> <span class="erreur" id="errjournalLie"></span>


                                <br />
                                <label title="La rubrique du journal concernée : international, A la Une ... Pour ajouter des types de flux allez dans la configuration générale">Type de flux<span class="requis">*</span></label>
                                <select name="typeFlux">
                                    <option value=""></option>
                                    <c:forEach items="${listtypeflux}" var="typeflux">
                                        <option<c:if test="${bean.typeFlux.denomination==typeflux.denomination}"> selected="true" </c:if> value="${typeflux.ID}">${typeflux.denomination}</option>
                                    </c:forEach>
                                </select><span class="erreur" id="errtypeFlux"></span>
                                <br />

                                <label for="nom" title="Paramettre facultatif : Par défaut le flux sera nommé en fonction du journal et du type de flux sélectionné. Ce paramettre permet de forcer un nom">Nom du flux : </label>
                                <input type="text" name="nom" value="${bean.nom}" />
                                <span class="erreur" id="errnom"></span>

                                <br />


                                <label for="parentFlux" title="Certain flux sont des sous classement d'autres. Exemple le flux Europe est un sous flux de International">Sous flux de : </label>
                                <select name="parentFlux" id="parentFlux">
                                    <option>NULL</option>
                                    <c:forEach items="${bean.journalLie.fluxLie}" var="fluxJournal">
                                        <option <c:if test="${fluxJournal.ID==flux.parentFlux.ID}"> selected="true"</c:if>>${fluxJournal}</option>
                                    </c:forEach>
                                </select> 
                                <br />




                                <label title="Cette variable est complété automatiquement à l'ajout d'un nouveau flux">Flux créé le : <fmt:formatDate value="${bean.created}" pattern="dd/MM/yyyy hh:mm"/></label> <br />


                                <label title="L'administrateur doit qualifier si un flux est stable ou non afin de ne pas recevoir de notifications abusives. Un flux est qualifié de stable si il emmet un nombre conséquent d'items avec régularité et si il ne subit pas de pannes récurentes">Flux stable : </label>
                                <input name="estStable" id="estStable" type="checkbox" <c:if test="${bean.estStable == 'true'}">checked="checked" </c:if> /><br />


                                    <label for="infoCollecte" title="Les administrateurs peuvent consigner des informations sur les flux dans ce champs de libre saisie">Information :</label><br />
                                    <textarea id="infoCollecte" name="infoCollecte" rows="20" cols="80">${bean.infoCollecte}</textarea>
                                <input type="hidden" name="id" value="${bean.ID}">
                                <br />

                                <input type="hidden" name="vue" value="jsonform"/>
                                <input type="submit" value="Enregitrer" class="sansLabel" />
                                <br />
                            </fieldset>
                        </form>

                        <script src="${rootpath}AjaxAddModBean.js"></script>


                    </c:when>


                    <c:when test="${action=='maj'}">
                        <table border="1">
                            <tr>
                                <th>Flux</th>
                                <th>Item trouvée</th>
                                <th>Doublon Interne au flux</th>
                                <th>Dedoub mémoire</th>
                                <th>Dedoub BDD</th>
                                <th>Item liée</th>
                                <th>Item nouvelle</th>
                                <th>Statut</th>
                            </tr>





                            <c:forEach items="${listTache}" var="tache">
                                <tr>
                                    <td> ${tache.flux}</td>

                                    <td>${tache.visitorHTTP.nbItTrouve}</td>
                                    <td>${tache.visitorHTTP.nbDoublonInterneAuflux}</td>
                                    <td>${tache.visitorHTTP.nbDedoubMemoire}</td>
                                    <td>${tache.visitorHTTP.nbDedoubBdd}</td>
                                    <td>${tache.visitorHTTP.nbLiaisonCree}</td>
                                    <td>${tache.visitorHTTP.nbNouvelle}</td>
                                    <td>
                                        <c:set var="erreur" value="0"></c:set>

                                        <c:if test="${not empty tache.exeption}">
                                            <c:set var="erreur" value="1"></c:set>
                                            ${tache.exeption} 
                                        </c:if>

                                        <c:if test="${erreur!=1}">OK</c:if>
                                    </tr>
                            </c:forEach>


                        </table>
                    </c:when>

                    <c:when test="${action=='read'}">
                        <c:import url="/WEB-INF/inc/editionBean.jsp" />

                        <h2>Paramètres du flux</h2>

                        <p><strong>Url : </strong> <a  target="_blank"href="${bean.url}">${bean.url}</a></p>
                        <p><strong>Nom du flux :</strong> ${bean.nom}</p>
                        <p><strong>Page HTML :</strong> ${bean.htmlUrl}</p>
                        <p><strong>Comportement de collecte :</strong><a href="${rootpath}ComportementCollecte/read?id=${bean.mediatorFlux.ID}"> ${bean.mediatorFlux}</a></p>
                        <p><strong>Journal : </strong><a href="${rootpath}journaux/read?id=${bean.journalLie.ID}">${bean.journalLie}</a></p>
                        <p><strong>Type de flux :</strong><a href="${rootpath}TypeFluxSrvl/read?id=${bean.typeFlux.ID}"> ${bean.typeFlux}</a></p>
                        <p><strong>Ajouté le :</strong> ${bean.created}</p>
                        <p><strong title="Un flux est considéré comme stable si il retourne tous les jours un nombre important et régulier d'items ">Flux stable : </strong><c:if test="${bean.estStable == 'true'}">OUI</c:if><c:if test="${bean.estStable == 'false'}">NON</c:if></p>
                            <p><strong>Période de captation : </strong><ul>
                            <c:forEach items="${bean.periodeCaptations}" var="periode">
                                <li>${periode}</li>   

                            </c:forEach>
                        </ul>

                        </p>
                        <hr />
                        <h2>Principaux incidents de collecte</h2>
                        <p>Ci dessous sont affichés les incidents ayant perturbé la collecte pendant plus de 4 heures.</p>



                        <table border="1">
                            <tr>
                                <th>Intitulé</th>
                                <th>Description</th>
                                <th>dateDébut</th>
                                <th>dateFin</th>
                            </tr>


                            <c:forEach items="${indids}" var="inci">
                                <tr>
                                    <td><a href="${inci.urlAdmin}">${inci}</a></td>
                                    <td>${inci.messageEreur}</td>
                                    <td><fmt:formatDate value="${inci.dateDebut}" pattern="dd/MM/yyyy hh:mm"/></td>
                                    <td><c:if test="${not empty inci.dateFin}"><fmt:formatDate value="${inci.dateFin}" pattern="dd/MM/yyyy hh:mm"/></c:if>
                                        <c:if test="${empty inci.dateFin}">Non clos</c:if>
                                        </td></tr>
                                </c:forEach>

                        </table>
                        <p><a href="#">Voir tous les incidents du flux</a></p>



                        <hr />


                        <h2>Indice de captation</h2>



                        <label>Periode de captation : </label> 
                        <select id="selectionPeriod">
                            <option>Selection</option>
                            <c:forEach items="${bean.periodeCaptations}" var="pCapt">
                                <option value="${pCapt.ID}">${pCapt}</option>
                            </c:forEach>
                        </select>

                        <div id="affichageCaptation"></div>

                        <div id="container" style="height: 400px; margin: auto; min-width: 310px; max-width: 600px"></div>



                    </c:when>


                    <c:when test="${action == 'importcsv'}">

                        <p>
                            Vous vous apprétez à importer des items. Avant de commencer la procédure, veillez à ce que les fichier csv soient correct ! Les points suivant sont à observer : 
                        <ul>
                            <li>Encodage de caractère. Il est nécessaire de connaitre l'encodage de caractère de votre fichier (UTF-8, ISO-8859-1, ISO-8859-15...)</li>
                            <li>les date doivent être au format : yyyy-MM-dd HH:mm:ss</li>
                            <li>il est préférable d'utiliser la tabulation comme séparateur de champs</li>
                            <li>N'insérer pas de données avec des problèmes d'encodage de caractères</li>
                        </ul>
                        L'import se déroule en trois phase : l'envoie du fichier csv, le parsing du fichier, la vérification des données parsée et enfin l'enregistrement. Une fois les données enregistrées, il vous sera impossible de les supprimer simplement. Il ne sera en effet pas possible de séparer les items importé des anciennes items. Ne faites ainsi pas cette opération à la légère. Peut être devriez-vous dans un premier temps vérifier le bon déroullement des opérations en effectant l'ajout sur le serveur de test.
                        </p>

                        <form method="POST"  enctype="multipart/form-data" id="formUpload" >
                            <input type="hidden" name="phase" value="upload" />
                            <input type="hidden" name="init" value="false" />
                            
                            <label>Votre fichier CSV : </label>
                            <input type="file" name="csvfile" />

                            <input type="submit" value="Upload">
                        </form>


                        <form method="POST">
                            <input type="hidden" name="init" value="true"/>
                            <input value="réinitialiser" type="submit" />
                        </form>




                        <c:if test="${phase=='parse' or phase=='presave'}">
                            <form method="POST" id="formParse">
                                <input type="hidden" name="phase" value="parse" />
                                <input type="hidden" name="init" value="false"/>
                                <fieldset>
                                    <legend>Paramettre de Parsing</legend>

                                    <label title="Encodage de caractère utilisé par votre fichier.">Charset : </label>
                                    <select name="forceEncoding" id="forceEncoding">
                                        <%
                                            Map<String, Charset> map = Charset.availableCharsets();
                                            for (Map.Entry<String, Charset> entry : map.entrySet()) {
                                                String string = entry.getKey();
                                                Charset charset = entry.getValue();

                                                if (charset.toString().equals("UTF-8")) {
                                                    out.print("<option value=\"" + charset + "\" selected=\"true\">" + string + "</option>");
                                                } else {
                                                    out.print("<option value=\"" + charset + "\">" + string + "</option>");
                                                }
                                            }
                                        %>
                                    </select>


                                    <br />



                                    <label>Date Pattern :</label><input type="text" name="datePattern" value="yyyy-MM-dd HH:mm:ss"/><br />
                                    <label title="the delimiter to use for separating entries quotechar">Separator : </label><input name="separator" type="text" value="<c:out value="\t"></c:out>" /><br />
                                    <label title="the character to use for quoted elements escape">Quotechar :</label><input name="quotechar" type="text"  value="<c:out value="\""></c:out>" /><br />
                                    <label title="the character to use for escaping a separator or quote line">Escape  :</label><input name="escape" type="text" value="<c:out value="\\"></c:out>" /><br />
                                        <label title="the line number to skip for start reading strictQuotes">First Line : </label><input name="line" type="text" value="0" /><br />
                                        <label title="sets if characters outside the quotes are ignored">StrictQuotes : </label><input name="strictQuotes" type="checkbox"/><br/>
                                        <label title="it true, parser should ignore white space before a quote in a field">IgnoreLeadingWhiteSpace : </label><input name="ignoreLeadingWhiteSpace" type="checkbox"/><br />

                                        <label>Correspondance colonne : </label>
                                        <table>

                                            <tr>
                                                <th>Titre</th>
                                                <th>Description</th>
                                                <th>Link</th>
                                                <th>GUID</th>
                                                <th>Date Publication</th>
                                                <th>DateRecup</th>
                                                <th>Categorie</th>
                                                <th>Contenu</th>
                                            </tr>
                                            <tr>
                                                <td>
                                                    <select name="cTitre">
                                                        <option value="0" selected="selected">Col 1</option>
                                                        <option value="1">Col 2</option>
                                                        <option value="2">Col 3</option>
                                                        <option value="3">Col 4</option>
                                                        <option value="4">Col 5</option>
                                                        <option value="5">Col 6</option>
                                                        <option value="6">Col 7</option>
                                                        <option value="7">Col 8</option>
                                                        <option value="8">Col9</option>
                                                        <option value="9">Col 10</option>
                                                        <option value="10">Col 11</option>
                                                    </select>
                                                </td>
                                                <td>
                                                    <select name="cDescription">
                                                        <option value="0" selected="selected">Col 1</option>
                                                        <option value="1"selected="true">Col 2</option>
                                                        <option value="2">Col 3</option>
                                                        <option value="3">Col 4</option>
                                                        <option value="4">Col 5</option>
                                                        <option value="5">Col 6</option>
                                                        <option value="6">Col 7</option>
                                                        <option value="7">Col 8</option>
                                                        <option value="8">Col9</option>
                                                        <option value="9">Col 10</option>
                                                        <option value="10">Col 11</option>
                                                    </select>
                                                </td>
                                                <td>
                                                    <select name="cLink">
                                                        <option value="0" selected="selected">Col 1</option>
                                                        <option value="1">Col 2</option>
                                                        <option value="2" selected="true">Col 3</option>
                                                        <option value="3">Col 4</option>
                                                        <option value="4">Col 5</option>
                                                        <option value="5">Col 6</option>
                                                        <option value="6">Col 7</option>
                                                        <option value="7">Col 8</option>
                                                        <option value="8">Col9</option>
                                                        <option value="9">Col 10</option>
                                                        <option value="10">Col 11</option>
                                                    </select>
                                                </td>


                                                <td>
                                                    <select name="cGuid">
                                                        <option value="0" selected="selected">Col 1</option>
                                                        <option value="1">Col 2</option>
                                                        <option value="2">Col 3</option>
                                                        <option value="3" selected="true">Col 4</option>
                                                        <option value="4">Col 5</option>
                                                        <option value="5">Col 6</option>
                                                        <option value="6">Col 7</option>
                                                        <option value="7">Col 8</option>
                                                        <option value="8">Col9</option>
                                                        <option value="9">Col 10</option>
                                                        <option value="10">Col 11</option>
                                                    </select>
                                                </td>
                                                <td>
                                                    <select name="cDatePub">
                                                        <option value="0" selected="selected">Col 1</option>
                                                        <option value="1">Col 2</option>
                                                        <option value="2">Col 3</option>
                                                        <option value="3">Col 4</option>
                                                        <option value="4" selected="true">Col 5</option>
                                                        <option value="5">Col 6</option>
                                                        <option value="6">Col 7</option>
                                                        <option value="7">Col 8</option>
                                                        <option value="8">Col9</option>
                                                        <option value="9">Col 10</option>
                                                        <option value="10">Col 11</option>
                                                    </select>
                                                </td>

                                                <td>
                                                    <select name="cDateRecup">
                                                        <option value="0" selected="selected">Col 1</option>
                                                        <option value="1">Col 2</option>
                                                        <option value="2">Col 3</option>
                                                        <option value="3">Col 4</option>
                                                        <option value="4">Col 5</option>
                                                        <option value="5" selected="true">Col 6</option>
                                                        <option value="6">Col 7</option>
                                                        <option value="7">Col 8</option>
                                                        <option value="8">Col9</option>
                                                        <option value="9">Col 10</option>
                                                        <option value="10">Col 11</option>
                                                    </select>
                                                </td>

                                                <td>
                                                    <select name="cCat">
                                                        <option value="0" selected="selected">Col 1</option>
                                                        <option value="1">Col 2</option>
                                                        <option value="2">Col 3</option>
                                                        <option value="3">Col 4</option>
                                                        <option value="4">Col 5</option>
                                                        <option value="5">Col 6</option>
                                                        <option value="6" selected="true">Col 7</option>
                                                        <option value="7">Col 8</option>
                                                        <option value="8">Col9</option>
                                                        <option value="9">Col 10</option>
                                                        <option value="10">Col 11</option>
                                                    </select>
                                                </td>
                                                <td>
                                                    <select name="cContenu">
                                                        <option value="0" selected="selected">Col 1</option>
                                                        <option value="1">Col 2</option>
                                                        <option value="2">Col 3</option>
                                                        <option value="3">Col 4</option>
                                                        <option value="4">Col 5</option>
                                                        <option value="5">Col 6</option>
                                                        <option value="6">Col 7</option>
                                                        <option value="7" selected="true">Col 8</option>
                                                        <option value="8">Col9</option>
                                                        <option value="9">Col 10</option>
                                                        <option value="10">Col 11</option>
                                                    </select>
                                                </td>

                                            </tr>
                                        </table>


                                    </fieldset>
                                    <input type="submit" value="Parsssser"/>


                                </form>

                        </c:if>


                        <table id="list" width="600"><tr><td></td></tr></table> 
                        <div id="pager"></div> 

                        <div id="preSave"></div>




                        <div id="infoResultats"></div>

                        <c:if test="${!empty sessionScope.imporComportement}">


                        </c:if>


                    </c:when>
                </c:choose>
            </c:when>
        </c:choose>
    </div>
</div>

<c:import url="/WEB-INF/footerjsp.jsp" />