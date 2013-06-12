<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%--<%@page contentType="text/html" pageEncoding="UTF-8"%>--%>
<!--Inclusion du menu haut-->
<c:import url="/WEB-INF/headerjsp.jsp" />

<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            <h1><span>Récapitulatif l'activité</span> des flux</h1></div></div>


</div>
<div id="sidebar">
    <p><a href="journaux?action=add">Blabla</a></p>
    <p><a href="journaux?action=list">Bla</a></p>
</div>

<div id="content">
    <div class="post">

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>

        <script src="http://code.highcharts.com/highcharts.js"></script>
        <script src="http://code.highcharts.com/modules/exporting.js"></script>
        
         
        <div id="container" style="min-width: 400px; height: 400px; margin: 0 auto"></div>

       <script src="TESThighchartsscript.js"></script>




    </div>
</div>

<c:import url="/WEB-INF/footerjsp.jsp" />