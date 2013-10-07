<%-- 
    Document   : jsonform
    Created on : 3 oct. 2013, 13:37:20
    Author     : clem
--%>

<%@page import="rssagregator.dao.DAOFactory"%>
<%@page import="java.util.Map"%>
<%@page import="rssagregator.beans.form.AbstrForm"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONObject"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    JSONObject export = new JSONObject();
    JSONArray array = new JSONArray();
    
    System.out.println("json serv 1");
//     On parcours les erreurs
    AbstrForm form = (AbstrForm) request.getAttribute("form");
    System.out.println("-->> FORM"+form);
    Map<String, String[]> erreurMap = form.getErreurs();
    for (Map.Entry<String, String[]> en : erreurMap.entrySet()) {
        String key = en.getKey();
        String value = en.getValue()[0];
        JSONObject obj = new JSONObject();
        obj.put("key", key);
        obj.put("value", value);
        array.add(obj);
        
    }
    export.put("erreurs", array);
        System.out.println("json serv 2");
    
    if(erreurMap.isEmpty()){
        export.put("valid", true);
    }
    else{
        export.put("valid", false);
    }
    System.out.println("json serv 1");
    //Gestion de la redirection
    String rootpath = (String)request.getAttribute("rootpath");
    String servUrl = DAOFactory.getInstance().getDAOConf().getConfCourante().getServurl();
    String servlet = (String) request.getAttribute("srlvtname");
    String action = (String)request.getAttribute("action");
    String rediraction ="";
    if(action.equals("add")){
        rediraction = "recherche";
    }
    else if(action.equals("mod")){
        rediraction = "read?id="+request.getParameter("id");
    }
    
    
    
    
   export.put("redirUrl", servUrl+servlet+"/"+rediraction);


System.out.println(export.toJSONString());
    out.clear();
    out.print(export.toJSONString());
    out.flush();
%>