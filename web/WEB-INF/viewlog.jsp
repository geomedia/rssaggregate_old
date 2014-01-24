<%-- 
    Document   : viewlog
    Created on : 10 déc. 2013, 13:06:01
    Author     : clem
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<c:import url="/WEB-INF/headerjsp.jsp" />

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
        <button type="button" id="Colorise" onclick="Colorise();">Colorise</button>
        
        <hr />
        ${log}
        
        
        <script>
            function Colorise(){
                
                
                $('p').each(function (index, value ){
                  alert( index + ": " + $(value).text() );
                  $(value).text('aaaaa')
                })
//                $('p').te
                
                nb = $('p').size();
                alert('nb p :' + nb);
            }
            
        </script>
        
    </body>
</html>
