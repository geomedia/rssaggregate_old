 
<!--Document   : index
Created on : 22 avr. 2013, 14:36:12
Author     : clem-->

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!--Inclusion du menu haut-->
<c:import url="/WEB-INF/headerjsp.jsp" />

<div id="header-wrapper">
    <div id="header">
        <div id="logo">
            <!--<h1>Administration des <span>Flux</span></h1></div></div>-->
            <h1>Administration du<span> serveur</span></h1>
        </div></div></div>


<div id="sidebar">
    <p><a href="${rootpath}config">générale</a></p>
    <p><a href="${rootpath}ComportementCollecte">Gérer les compotement </a></p>
    <p><a href="${rootpath}TypeFluxSrvl">Gérer les types de flux </a></p>
    <p><a href="${rootpath}user/recherche/">Gérer les utilisateurs </a></p>
                    </div> 

                    <div id="content">
                        <div class="post">

                        <c:choose >
                            <c:when test="${not empty redirmap}">
                                <c:import url="/WEB-INF/redirJspJavascriptPart.jsp" />
                            </c:when>


                            <c:when test="${empty redirmap}">
                                <c:choose>
                                   
                             


                                    <c:when test="${action=='read'}">
       
                          

                                    </c:when>


                                </c:choose>


                            </c:when>
                        </c:choose>

                    </div>
                </div>

                <c:import url="/WEB-INF/footerjsp.jsp" />