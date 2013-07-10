<%-- 
    Document   : itemcsvjsp
    Created on : 25 juin 2013, 17:05:15
    Author     : clem
--%>
<%@page import="rssagregator.utils.CsvComparator1"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>
<%@page import="rssagregator.beans.Flux"%>
<%@page import="org.joda.time.format.ISODateTimeFormat"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="org.joda.time.DateTime"%>
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
    List<Item> listItem = (List<Item>) request.getAttribute("listItem");


    

    DateTimeFormatter fmt = ISODateTimeFormat.dateHourMinute();
    int i;
    for (i = 0; i < listItem.size(); i++) {
        Item it = listItem.get(i);


        DateTime dt = new DateTime(it.getDateRecup());
        DateTime datePub = new DateTime(it.getDatePub());
 

        List<Flux> listFl = it.getListFlux();
        int j;
        for (j = 0; j < listFl.size(); j++) { 

            String typeFl="";
            if (listFl.get(j).getTypeFlux() != null) {
                typeFl = listFl.get(j).getTypeFlux().getDenomination();
            }
            
            String journal="";
            if(listFl.get(j).getJournalLie()!=null){
                journal = listFl.get(j).getJournalLie().getNom();
            }
            data.add(new String[]{it.getID().toString().trim(), it.getTitre().trim() , it.getDescription().trim(), fmt.print(datePub), fmt.print(dt), "?", it.getGuid(), listFl.get(j).getID().toString(),typeFl, journal });
        }
    }

// On trie la liste    
    Comparator<String[]> compa = new CsvComparator1();
    Collections.sort(data, compa);

    // Gestion des enetes
    data.add(0, new String[]{
        "ID Item",
        "Titre", 
        "Description", 
        "datePub",
        "Date Récup",
        "Date relative calculé par rapport aux Fuseaux", 
        "Guid",
        "ID flux", 
        "typeFlux",
        "Journal"});

    
    cSVWriter.writeAll(data);
    cSVWriter.close();
    out.clear();
    out.println(sw.toString());


%>
