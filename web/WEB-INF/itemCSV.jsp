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
    System.out.println("--->" + listItem);


    // Gestion des enetes
    data.add(new String[]{"ID Item", "Titre", "Description", "Date Récup", "Date relative calculé par rapport aux Fuseaux", "ID flux", "typeFlux", "Journal"});


    DateTimeFormatter fmt = ISODateTimeFormat.dateHourMinute();
    int i;
    for (i = 0; i < listItem.size(); i++) {
        Item it = listItem.get(i);





        List<Flux> listFl = it.getListFlux();


        String id = "";
        if (it.getID() != null) {
            id = it.getID().toString();
        }

        String titre = "";
        if (it.getTitre() != null) {
            titre = it.getTitre().trim();
        }

        String desc = "";
        if (it.getDescription() != null) {
            desc = it.getDescription().trim();
        }

        String dateRecup = "";
        if (it.getDateRecup() != null) {
            DateTime dt = new DateTime(it.getDateRecup());
            dateRecup = fmt.print(dt);
        }

        String dateCalcule = "??";

        int j;
        for (j = 0; j < listFl.size(); j++) {

            String idFlux = "";
            if (listFl.get(j).getID() != null) {
                idFlux = listFl.get(j).getID().toString();
            }

            String typeFl = "";
            if (listFl.get(j).getTypeFlux() != null) {
                typeFl = listFl.get(j).getTypeFlux().getDenomination();
            }

            String journal = "";
            if (listFl.get(j).getJournalLie() != null) {
                journal = listFl.get(j).getJournalLie().getNom();
            }

            data.add(new String[]{id, titre, desc, dateRecup, dateCalcule, idFlux, typeFl, journal});
        }
    }


//    Comparator<String[]> compa = new CsvComparator1();
//
//// On trie la liste
//    Collections.sort(data, compa);

    cSVWriter.writeAll(data);
    cSVWriter.close();
    out.clear();
    out.println(sw.toString());


%>
