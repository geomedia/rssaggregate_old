<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:choose>
    <c:when test="${firstResult - itPrPage<0}">
        <c:set var="end2" value="0"></c:set>
        <c:set var="begin2" value="10"></c:set>
    </c:when>
    <c:when test="${firstResult - (itPrPage)*5<=0}">
        <c:set var="end2" value="${firstResult- itPrPage}"></c:set>
        <c:set var="begin2" value="0"></c:set>
    </c:when>

    <c:when test="${firstResult - (itPrPage)*5>=0}">
        <c:set var="end2" value="${firstResult - itPrPage}"></c:set>
        <c:set var="begin2" value="${firstResult - (itPrPage)*5}"></c:set>
    </c:when>
</c:choose>
<div id="btPaginDiv">
<c:forEach var="j" begin="${begin2}" end="${end2}" step="${itPrPage}" varStatus="varstat2">
    <button type="submit" name="firstResult" value="${j}">${j} - ${j+varstat2.step}</button>
</c:forEach>



<c:choose>
    <c:when test="${firstResult + (itPrPage)*10<nbitem}">
        <c:set var="begin" value="${firstResult}"></c:set>
        <c:set var="end" value="${firstResult + (itPrPage)*10}"></c:set>
    </c:when>
    <c:when test="${firstResult + (itPrPage)*10>nbitem}">
        <c:set var="begin" value="${firstResult}"></c:set>
        <c:set var="end" value="${nbitem}"></c:set>

    </c:when>

</c:choose>

    
<c:forEach var="i" begin="${begin}" end="${end}" step="${itPrPage}"  varStatus="varstat">
    <button type="button" name="firstResult" value="${i}" <c:if test="${i==firstResult}">style="color: red"</c:if> onclick="paginsubmit(this)">${i} - ${i+varstat.step}<c:if test="${varstat.last}">...</c:if></button>
</c:forEach>
    </div>
    
     <input type="hidden" id="firstResult" value="${firstResult}" name="firstResult">
