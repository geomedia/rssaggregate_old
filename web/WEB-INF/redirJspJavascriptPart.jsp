<%-- 
Ce bout de JPS Est inclu dans les JSP CRUD pour à la fin d'une action rediriger l'utilisateur en fonction de la redirmap envoyé par la servlet
    Document   : redirJspJavascriptPart
    Created on : 31 juil. 2013, 19:18:19
    Author     : clem
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

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
 