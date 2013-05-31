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
            <h2>${item.titre}</h2>
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
        <div class="post">
                <h1>Liste des items</h1>
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

            <p></p>


            <div class="pagination">
                <a href="#" class="first" data-action="first">&laquo;</a>
                <a href="#" class="previous" data-action="previous">&lsaquo;</a>
                <input type="text" readonly="readonly" data-max-page="3" />
                <a href="#" class="next" data-action="next">&rsaquo;</a>
                <a href="#" class="last" data-action="last">&raquo;</a>
            </div>

            <form action="item" method="POST">
                <label>Item par page</label>
                <select name="nbrItemPrPage" onChange="this.form.submit();"> 

                    <option value=""></option> 
                    <option value="5">10</option> 
                    <option value="20">20</option>
                    <option value="50">50</option>
                    <option value="100">100</option> 
                    <option value="200">200</option> 
                    <option value="500">500</option> 
                </select> 
                <noscript>
                <input type="submit" value="Changer"  />
                </noscript>
            </form>
        </div>
    </c:when>

</c:choose>










<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
<script src="jqPagination/js/jquery.jqpagination.js"></script>





</div>



<script>


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


</script>
<c:import url="/WEB-INF/footerjsp.jsp" />