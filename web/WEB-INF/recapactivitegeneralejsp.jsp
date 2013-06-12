<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

        <!--<script src="http://code.highcharts.com/modules/exporting.js"></script>-->

        <!--<script src="http://code.highcharts.com/highcharts.js"></script>-->
        <script src="highcharts.js"></script>
        <script src="exporting.js"></script>




        <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />

        <script src="jquery-ui.js"></script>
        <link rel="stylesheet" href="/resources/demos/style.css" />

        <script>
            $(function() {
                $(".datepicker").datepicker();
            });</script>



        <form method="POST" action="recapActiviteGenerale">
            <input type="hidden" name="action" value="print"/>
            <label for="date1">Date début : </label>
            <input type="text" name="date1" class="datepicker"/>
            <label for="date2">Date fin : </label>
            <input type="text" name="date2" class="datepicker"/>
            <br />
            <select multiple="true" name="flux">
                <option value="">NULL</option>
                <c:forEach items="${listFlux}" var="fl">
                    <option value="${fl.ID}">${fl}</option>
                </c:forEach>
            </select>
            <br />
            <label for="temporalite">Temporalité : </label>
            <select name="temporalite">
                <option>jour</option>
                <option>mois</option>
            </select>
            <input type="submit" />
        </form>
        <!--<p>Date: <input type="text" id="datepicker" /></p>-->

        <div id="container" style="min-width: 400px; height: 400px; margin: 0 auto"></div>



        <script>

            var chart; // global
            /**
             * Request data from the server, add it to the graph and set a timeout to request again
             */




            $(document).ready(function() {
                chart = new Highcharts.Chart({
                    chart: {
                        renderTo: 'container',
                        zoomType: 'x',
//             type: 'line',
//            defaultSeriesType: 'spline',
                        events: {
                            load: requestData

                        }
                    },
                    title: {
                        text: 'Récapitulatif de l\'activité des flux'
                    },
                    xAxis: {
//                            categories: ['Jan', 'Feb']
                        type: 'datetime',
                        maxZoom: 48 * 3600 * 1000

//            maxZoom: 20 * 1000
                    },
                    yAxis: {
                        minPadding: 0.2,
                        maxPadding: 0.2,
                        title: {
                            text: 'Nombre d\'items',
                            margin: 80
                        }
                    }
                    ,
                    series: [
            <c:forEach items="${recapActivite.listFlux}" var="fl">
                        {
                            name: '${fl}',
                            data: [],
                         
//                            pointStart: Date.UTC(2010, 1, 1),

                            pointStart: Date.UTC(<fmt:formatDate value="${recapActivite.date1}" pattern="yyyy"/>, <fmt:formatDate value="${recapActivite.date1}" pattern="MM"/> -1, <fmt:formatDate value="${recapActivite.date1}" pattern="dd"/>),
                           
                <c:if test="${param.temporalite=='jour'}">pointInterval: 24 * 3600 * 1000 // one day</c:if>
//                            pointInterval: 24 * 3600 * 1000 // one day
                                },</c:forEach>
                            ]
                        });
                    });
                    function requestData() {
                        $.ajax({
                            url: 'http://localhost:8084/RSSAgregate/recapActiviteGenerale?action=json',
//        success: alert("oui"),

                            success: function(point) {
                                var series = chart.series[0],
                                        shift = series.data.length > 20; // shift if the series is longer than 20


                                // add the point
//            chart.series[0].addPoint([110100,30], true, shift);
                                for (var iter = 0; iter < point.length; iter++) {
                                    chart.series[iter].setData(point[iter]);
                                }

//                            chart.series[0].setData(point);
//            chart.series[0].deleteContents();
//            chart.series[0].addpoints([10,20,30,40], true);
//            alert(point);
//            chart.series[0].update(true, true);

//            chart.series[0].setVisible(true, true);

//            chart.series[0].
//            chart.addSeries(point, true, shift);
//            chart.series[series.size-1].select(true);

                                // call it again after one second
//            setTimeout(requestData, 100000);    

                            },
                            // error : clem(), 

                            cache: false
                        });
                    }



            </script>


        </div>
    </div>

<c:import url="/WEB-INF/footerjsp.jsp" />