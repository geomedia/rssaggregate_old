<%-- 
    Document   : viewlog
    Created on : 10 déc. 2013, 13:06:01
    Author     : clem
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        
        <form>
            <select name="file">
                <option value="general.log">Erreur</option>
                <option value="err.log">General</option>
            </select>
            <br />
            <label>Nombre de ligne :</label><input type="text" name="nbrLigne"/>
            <input type="submit"/>
        </form>
        <hr />
        ${log}
        
    </body>
</html>
