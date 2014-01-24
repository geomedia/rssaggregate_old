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

//    CSVWriter cSVWriter = new CSVWriter(sw);
    CSVWriter cSVWriter = new CSVWriter(sw, '\t');


    List<String[]> data = new ArrayList<String[]>();
    List<Item> listItem = (List<Item>) request.getAttribute("items");

    SearchFiltersList filtersList = (SearchFiltersList) request.getAttribute("filtersList");
    List<SearchFilter> listFiltre = filtersList.getFilters();
    List<Flux> fluxSelectionne = null;

    for (int i = 0; i < listFiltre.size(); i++) {
        SearchFilter fi = listFiltre.get(i);
        if (fi.getField().equals("listFlux")) {
            fluxSelectionne = (List) fi.getData();
        }
    }


    // Gestion des enetes
    data.add(new String[]{"ID Item", "Titre", "Description", "Contenu", "Catégorie", "Lien", "Date Récup", "Date publication", "ID flux", "typeFlux", "Journal"});


//    DateTimeFormatter fmt = ISODateTimeFormat.dateHourMinute();
    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    int i;
    for (i = 0; i < listItem.size(); i++) {
        Item it = listItem.get(i);




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
        String datePub = "";
        if (it.getDatePub() != null) {
            DateTime dtPub = new DateTime(it.getDatePub());
            datePub = fmt.print(dtPub);
        }

        String cat = "";
        if (it.getCategorie() != null) {
            cat = it.getCategorie();
        }

        String contenu = "";
        if (it.getContenu() != null) {
            contenu = it.getContenu();
        }

        String lien = "";
        if (it.getLink() != null) {
            lien = it.getLink();
        }

        
        
        List<Flux> listFl = it.getListFlux(); // Pour chaque flux de l'item
        
        for (int j = 0; j < listFl.size(); j++) {

            Flux fluxObserve = listFl.get(j);
//            if(fluxSelectionne.contains(fluxObserve)){
                
//            }
//            
//            
            Boolean trouve = false;
            for (int idx = 0; idx < fluxSelectionne.size(); idx++) {
                Flux elem = fluxSelectionne.get(idx);
                if (elem.getID().equals(listFl.get(j).getID())) {
                    trouve = true;
                }
            }
//
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

                data.add(new String[]{id, titre, desc, contenu, cat, lien, dateRecup, datePub, idFlux, typeFl, journal});
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
