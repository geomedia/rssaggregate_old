<%-- 
    Document   : itemcsvjsp
    Created on : 25 juin 2013, 17:05:15
    Author     : clem
--%>
<%@page import="rssagregator.utils.ServletTool"%>
<%@page import="rssagregator.dao.SearchFilter"%>
<%@page import="rssagregator.dao.SearchFiltersList"%>
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
    List<Item> listItem = (List<Item>) request.getAttribute("items");

    SearchFiltersList filtersList = (SearchFiltersList) request.getAttribute("filtersList");
    List<SearchFilter> listFiltre = filtersList.getFilters();
    List<Flux> fluxSelectionne = null;

    for (int i = 0; i < listFiltre.size(); i++) {
        SearchFilter fi = listFiltre.get(i);
        if (fi.getField().equals("listFlux")) {
            System.out.println("DATA :   " + fi.getData().getClass());
            fluxSelectionne = (List) fi.getData();
        }
    }


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

            Boolean trouve = false;
            for (int idx = 0; idx < fluxSelectionne.size(); idx++) {
                Flux elem = fluxSelectionne.get(idx);
                if (elem.getID().equals(listFl.get(j).getID())) {
                    trouve = true;
                }
            }

            
            if (trouve) {
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
    }


//    Comparator<String[]> compa = new CsvComparator1();
//
//// On trie la liste

    cSVWriter.writeAll(data);
    cSVWriter.close();
    out.clear();
    out.println(sw.toString());


%>
