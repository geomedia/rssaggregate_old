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
            <h1>Administration des <span>Item</span></h1></div></div>

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
                        <li><a href="flux?action=read-item&id=${flux.ID}">${flux}</a></li>
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



        <c:when test="${action=='list'}">

            <link rel="stylesheet" href="jquery-ui.css" />
            <script src="jquery-ui.js"></script>

            <script>
                $(function() {
                    $(".datepicker").datepicker();
                });</script>


            <div class="post">
                <h1>Liste des items</h1>
                <div>

                    <form method="GET" id="form">
                        <fieldset>
                            <legend title="truc"  >Pages : </legend> <c:forEach var="i" begin="1" end="${nbitem}" step="${itPrPage}" varStatus="varstat">
                                <button type="submit" name="firstResult" value="${i-1}">${i} - ${i+varstat.step-1}</button>
                            </c:forEach>


                            <label for="itPrPage">Item par page : </label>
                            <select name="itPrPage" onChange="this.form.submit();"> 

                                <c:forEach var="i" begin="20" end="500" step="20">

                                    <option value="${i}" <c:if test="${param.itPrPage==i}"> selected="true"</c:if>>${i}</option>
                                </c:forEach>

                            </select> 
                            <noscript>
                            <input type="submit" value="Changer"  />
                            </noscript>
                            <span>nombre de résultats : ${nbitem}</span>

                        </fieldset>


                        <br />
                        <fieldset>
                            <legend>Affiner la recherche</legend>
                            <label for="flux">Lie au flux : </label>
                            
                            <select id="journalSelection">
                                <option value="null">Journal : </option>
                                <option id="tous">tous</option>
                                <c:forEach items="${listJournaux}" var="j">
                                    <option value="${j.ID}">${j.nom}</option>
                                </c:forEach>
                                
                            </select>


                            <select id="fluxSelection" name="id-flux">
                                <option value="all">Tous</option>
                                <c:forEach items="${listflux}" var="fl">
                                    <option value="${fl.ID}" <c:if test="${idflux==fl.ID}"> selected="true"</c:if>>${fl}</option>                                
                                </c:forEach>
                            </select>
                            
                            <script src="dynListJournauxFLux.js"></script>
                            
                            
                            <label>Ordonner par : </label>
                            <select name="order">
                                <option value=""></option>
                                <option value="dateRecup" <c:if test="${param.order=='dateRecup'}">selected="true"</c:if>>Date de récupération</option>
                                <option value="datePub" <c:if test="${param.order=='datePub'}"> selected="true"</c:if>>Date de publication</option>
                                </select>
                                <label for="desc">Décroissant</label>
                                <input type="checkbox" name="desc" value="true" <c:if test="${param.desc=='true'}"> checked="true"</c:if>/>


                                <label for="date1">Date début : </label>
                                <input type="text" name="date1" class="datepicker"/>
                                <label for="date2">Date fin : </label>
                                <input type="text" name="date2" class="datepicker"/>

                                <input type="submit" value="Affiner la sélection">

                                <select name="vue" id="vue" onchange="subExport();">
                                    <option value="html">Export</option>
                                    <option value="csv">csv</option>
                                </select>
<!--                                <button type="submit"  formaction="Export" formtarget="_blank">Exporter</button>-->
                                <script>
                                    function subExport(){
                                        if($('#vue').val()=='csv'){
                                            $('#form').submit();
                                        }
                                    }
                                </script>

                            </fieldset>
                        </form>
                    </div>

                    <ul>
                    <c:forEach items="${listItem}" var="ite">
                        <li><p>
                                <a href="item?action=read&id=${ite.ID}">${ite.titre}</a>
                                <c:forEach items="${ite.listFlux}" var="fl">
                                    "${fl}" 
                                </c:forEach><fmt:formatDate value="${ite.dateRecup}" pattern="dd/MM/yyyy hh:mm:ss"/>
                            </p>
                            <p>${ite.description}</p>
                        </li>
                    </c:forEach>
                </ul>



                <!--                <div class="pagination">
                                    <a href="#" class="first" data-action="first">&laquo;</a>
                                    <a href="#" class="previous" data-action="previous">&lsaquo;</a>
                                    <input type="text" readonly="readonly" data-max-page="3" />
                                    <a href="http://sss" class="next" data-action="next">&rsaquo;</a>
                                    <a href="#" class="last" data-action="last">&raquo;</a>
                                </div>-->


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