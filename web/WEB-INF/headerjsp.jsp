<%@page import="rssagregator.services.ServiceSynchro"%>
<%@page import="rssagregator.beans.UserAccount"%>

<%@page import="rssagregator.dao.DAOFactory"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%--<%@page contentType="text/html" pageEncoding="UTF-8"%>--%>
<c:set var="rootpath" value="/RSSAgregate/" scope="request"></c:set>
    <!DOCTYPE html>
    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>RSSAgregate</title>


            <link href="${rootpath}/ress/style.css" type="text/css" rel="stylesheet" media="all" />
        <link rel="stylesheet" href="${rootpath}ress/jquery-ui.css" />
        <script src="${rootpath}ress/jquery-1.10.2.min.js"></script>
        <script src="${rootpath}ress/jquery-ui.js"></script>
        <script src="${rootpath}ress/JMSReconnection.js"></script>
        <script src="${rootpath}ress/modernizr.custom.36191.js"></script>
        <!--<script src="http://code.jquery.com/jquery-1.9.1.js"></script>-->
        <!--<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>-->
        <!--<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>-->
        <!--<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />-->
        <!--        <link rel="stylesheet" href="/resources/demos/style.css" />-->



        <!--Paramètre de la toolbox info du jquery IU-->
        <script>
            $(function() {
            $(document).tooltip({
            show: {
            effect: false,
            delay: 30
            }
            });
            $("#hide-option").tooltip({
            hide: {
            effect: false,
            delay: 70
            }
            });
            $("#open-event").tooltip({
            show: null,
            position: {
            my: "left top",
            at: "left bottom"
            },
            open: function(event, ui) {
            ui.tooltip.animate({top: ui.tooltip.position().top + 10}, "fast");
            }
            })
            ;
            });
        </script>
    </head>
    <body>



        <header style="width: 1200px; height: 116px">

            <div style="float: left">

                <a href ="/RSSAgregate/index" style="display: block;">
                    <%
                        if (DAOFactory.getInstance().getDAOConf().getConfCourante().getMaster()) {
                            out.println("<img src=\"/RSSAgregate/ress/img/logo_mastervert.png\"/>");
                            request.setAttribute("master", true);
                        } else {
                            out.println("<img src=\"/RSSAgregate/ress/img/logo_masterrouge.png\"/>");
                            request.setAttribute("master", false);
                        }
                    %>
                </a>
            </div>


            <div style="float: right">

                <%
                    UserAccount u = (UserAccount) session.getAttribute("authuser");
                    if (u != null) {
                        out.println(u.getMail());
                %><a href="${rootpath}ident/logout">Deconnection</a><%
                        if (u.getAdminstatut()) {
                            request.setAttribute("admin", true);
                        }
                %> | <a href="${rootpath}user/mod?id=<% out.print(u.getID()); %>">Mon compte</a><%
                    } else {
                        out.print("deco");
                    }

                %>


                Statut JMS <span id="JMSstat"><%
                    if (ServiceSynchro.getInstance().getStatutConnection()) {
                        out.println("OK");
                    } else {
                        out.println("Erreur");
                    %></span>
                <button type="button" id="jmsrecoBT">Reconnection</button>
                <span id="pinfoJMS"></span>
            </div>

            <%
                }
            %>

        </header>
        <div style="clear: both"></div>

            <nav id="topNav">  
                <ul>  
                    <li <c:if test="${navmenu=='item'}"> class="current_page_item"</c:if>><a href="${rootpath}item">Items</a></li>  
                <li <c:if test="${navmenu=='flux'}"> class="current_page_item"</c:if>>  
                    <a href="${rootpath}flux">Flux</a>
                    <ul>    
                        <li><a href="${rootpath}flux/recherche">Rechercher</a></li>  
                        <c:if test="${admin == 'true'}">  <li><a href="${rootpath}flux/add">Ajouter</a></li>  </c:if>
                        <!--<li><a href="${rootpath}recapActiviteGenerale">Récapitulatif de l'activité</a></li>-->
                        <li><a href="${rootpath}flux/highchart">Récapitulatif de l'activité</a></li>
                    </ul>                  
                </li>  
                <li <c:if test="${navmenu=='journaux'}"> class="current_page_item"</c:if>><a href="${rootpath}journaux">Journaux</a>
                        <ul>
                            <li><a href="${rootpath}journaux/recherche">Recherche</a></li>
                        <c:if test="${admin == 'true'}"> <li><a href="${rootpath}journaux/add">Ajout</a></li></c:if>

                    </ul> 
                </li>  

                <li <c:if test="${navmenu=='incident'}"> class="current_page_item"</c:if>><a href="${rootpath}incidents">Incidents</a></li>  



                    <li <c:if test="${navmenu=='typeflux'}"> class="current_page_item"</c:if>><a href="${rootpath}TypeFluxSrvl/recherche">Types de flux</a>
                        <ul>
                        <c:if test="${admin == 'true'}"> <li ><a href="${rootpath}TypeFluxSrvl/add">Ajouter</a></li></c:if>
                        <li><a href="${rootpath}TypeFluxSrvl/recherche">Rechercher</a></li>
                    </ul>

                </li>

                <li <c:if test="${navmenu=='ComportementCollecte'}"> class="current_page_item"</c:if>><a href="${rootpath}ComportementCollecte/recherche">Comportement de collecte</a>
                        <ul>
                        <c:if test="${admin == 'true'}"><li><a href="${rootpath}ComportementCollecte/add">Ajouter</a></li></c:if>
                        <li><a href="${rootpath}ComportementCollecte/recherche">Rechercher</a></li>
                    </ul>

                </li>

               <c:if test="${admin == 'true'}"> <li <c:if test="${navmenu=='slave'}"> class="current_page_item"</c:if>><a href="${rootpath}slave/recherche">Serveurs esclaves</a>
                        <ul>
                            <li><a href="${rootpath}slave/recherche">Recherche</a></li>
                        <li><a href="${rootpath}slave/add">Ajouter</a></li>
                        <li><a href="${rootpath}slave/importitem">Synch Manuelle</a></li>
                    </ul>
                   </li></c:if>

                <c:if test="${admin == 'true'}"><li <c:if test="${navmenu=='user'}"> class="current_page_item"</c:if>><a href="${rootpath}user/recherche">Utilisateurs</a>
                        <ul>
                            <li><a href="${rootpath}user/recherche">Recherche</a></li>
                        <li><a href="${rootpath}user/add">Ajouter</a></li>
                    </ul>
                    </li></c:if>


                    <c:if test="${admin == 'true'}"><li <c:if test="${navmenu=='config'}"> class="current_page_item"</c:if>><a href="${rootpath}config/read?id=1">Configuration</a></li></c:if>
                <li <c:if test="${navmenu=='aide'}"> class="current_page_item"</c:if>><a href="${rootpath}aide.jsp">Aide</a></li>  
            </ul>  
        </nav> 

        <script>
            var el = document.getElementsByTagName("body")[0];
            el.className = "";
        </script>


        <script>
            (function($) {

            //cache nav
            var nav = $("#topNav");

            //add indicators and hovers to submenu parents
            nav.find("li").each(function() {
            if ($(this).find("ul").length > 0) {

            $("<span>").text("^").appendTo($(this).children(":first"));

            //show subnav on hover
            $(this).mouseenter(function() {
            $(this).find("ul").stop(true, true).slideDown();
            });

            //hide submenus on exit
            $(this).mouseleave(function() {
            $(this).find("ul").stop(true, true).slideUp();
            });
            }
            });
            })(jQuery);
        </script>

        <div id="wrapper">
            <div id="page">
                <div id="page-bgtop">
                    <div id="page-bgbtm">