<%-- 
    Document   : itemcsvjsp
    Created on : 25 juin 2013, 17:05:15
    Author     : clem
--%>
<%@page contentType="document/csv" pageEncoding="UTF-8"%>
<%@page import="java.io.StringWriter"%>
<%@page import="rssagregator.beans.Item"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="au.com.bytecode.opencsv.CSVWriter"%>



<%
            StringWriter sw = new StringWriter();
     CSVWriter cSVWriter = new CSVWriter(sw);
            List<String[]> data = new ArrayList<String[]>();
            List<Item> listItem = (List<Item>)request.getAttribute("listItem");
            

            int i;
            for (i = 0; i < listItem.size(); i++) {
                Item it = listItem.get(i);
                data.add(new String[]{it.getID().toString().trim(), it.getTitre().trim(), it.getDescription().trim()});

            }


            cSVWriter.writeAll(data);
            cSVWriter.close();
            out.clear();
            out.println(sw.toString());
    
    
    %>