<%-- 
    Document   : itemcsvjsp
    Created on : 25 juin 2013, 17:05:15
    Author     : clem
--%>
<%@page import="org.joda.time.DateTime"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="rssagregator.dao.SearchFiltersList"%>
<%@page import="rssagregator.dao.SearchFilter"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="rssagregator.beans.Flux"%>
<%@page import="rssagregator.beans.Item"%>
<%@page import="java.util.List"%>
<%@page import="java.io.CharArrayReader"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.StringReader"%>
<%@page import="com.sun.xml.internal.messaging.saaj.util.ByteOutputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.FileNotFoundException"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.IOException"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="org.apache.poi.hssf.usermodel.HSSFDataFormat"%>
<%@page import="org.apache.poi.hssf.usermodel.HSSFWorkbook"%>
<%@page import="org.apache.poi.ss.usermodel.Cell"%>
<%@page import="org.apache.poi.ss.usermodel.CellStyle"%>
<%@page import="org.apache.poi.ss.usermodel.DataFormat"%>
<%@page import="org.apache.poi.ss.usermodel.Font"%>
<%@page import="org.apache.poi.ss.usermodel.Row"%>
<%@page import="org.apache.poi.ss.usermodel.Sheet"%>
<%@page import="org.apache.poi.ss.usermodel.Workbook"%>
<%@page contentType="document/xls" pageEncoding="UTF-8"%>
<%


    List<Item> listItem = (List<Item>) request.getAttribute("items");

    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    // Récupération de la liste des flux demandé par l'utilisateur. Sans avoir celle ci on ne peut retirer les infos non demandé par l'utilisateur
    SearchFiltersList filtersList = (SearchFiltersList) request.getAttribute("filtersList");
    List<SearchFilter> listFiltre = filtersList.getFilters();
    List<Flux> fluxSelectionne = null;

    for (int i = 0; i < listFiltre.size(); i++) {
        SearchFilter fi = listFiltre.get(i);
        if (fi.getField().equals("listFlux")) {
            fluxSelectionne = (List) fi.getData();
        }
    }


    // create a new workbook
    Workbook wb = new HSSFWorkbook();
    // create a new sheet
//    Sheet s = wb.createSheet();

    // declare a row object reference
    Row r = null;
    // declare a cell object reference
    Cell c = null;

    // create 3 cell styles
    CellStyle cs = wb.createCellStyle();
    CellStyle cs2 = wb.createCellStyle();
    CellStyle cs3 = wb.createCellStyle();
    DataFormat df = wb.createDataFormat();
    // create 2 fonts objects
    Font f = wb.createFont();
    Font f2 = wb.createFont();
    //set font 1 to 12 point type
    f.setFontHeightInPoints((short) 12);
    //make it blue
    f.setColor((short) 0xc);
    // make it bold
    //arial is the default font
    f.setBoldweight(Font.BOLDWEIGHT_BOLD);
    //set font 2 to 10 point type
    f2.setFontHeightInPoints((short) 10);
    //make it red
    f2.setColor((short) Font.COLOR_RED);
    //make it bold
    f2.setBoldweight(Font.BOLDWEIGHT_BOLD);
    f2.setStrikeout(true);
    //set cell stlye
    cs.setFont(f);
    //set the cell format
    cs.setDataFormat(df.getFormat("#,##0.0"));
    //set a thin border
    cs2.setBorderBottom(cs2.BORDER_THIN);
    //fill w fg fill color
    cs2.setFillPattern((short) CellStyle.SOLID_FOREGROUND);
    //set the cell format to text see DataFormat for a full list
    cs2.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
    // set the font
    cs2.setFont(f2);
    // set the sheet name in Unicode
//    wb.setSheetName(0, "\u0422\u0435\u0441\u0442\u043E\u0432\u0430\u044F "
//            + "\u0421\u0442\u0440\u0430\u043D\u0438\u0447\u043A\u0430");

    // in case of plain ascii
    // wb.setSheetName(0, "HSSF Test");
    // create a sheet with 30 rows (0-29)
    int rownum;


    Map<Long, Sheet> assocsfluxSet = new HashMap<Long, Sheet>();

    for (rownum = 0; rownum < listItem.size(); rownum++) {
        for (int j = 0; j < listItem.get(rownum).getListFlux().size(); j++) {
            Flux fl = listItem.get(rownum).getListFlux().get(j);

            // On vérifie que le flux a bien été demandé par l'utilisateur

            boolean trouve = false;
            if (fluxSelectionne != null && !fluxSelectionne.isEmpty()) {

                for (int i = 0; i < fluxSelectionne.size(); i++) {
                    if (fluxSelectionne.get(i).getID().equals(fl.getID())) {
                        trouve = true;
                    }
                }
            }

            if (trouve) { // Si c'est bien un flux demandé par l'utilisateur

                Sheet sheet = assocsfluxSet.get(fl.getID());

                if (sheet == null) {
                    String nomsheet = fl.toString();
                    nomsheet = nomsheet.replace(":", "-");
                    nomsheet = nomsheet.replace("/", "");
                    nomsheet = nomsheet.substring(0, 10);
                    sheet = wb.createSheet(nomsheet + rownum + j);

//                sheet = wb.createSheet(fl.toString());

                    assocsfluxSet.put(fl.getID(), sheet);


                    r = sheet.createRow(0);
                    c = r.createCell(0);
                    c.setCellValue("ID");


                    c = r.createCell(1);
                    c.setCellValue("Titre");


                    c = r.createCell(2);
                    c.setCellValue("Description");

                    c = r.createCell(3);
                    c.setCellValue("Lien");

                    c = r.createCell(4);
                    c.setCellValue("Contenu");

                    c = r.createCell(5);
                    c.setCellValue("DatePub");

                    c = r.createCell(6);
                    c.setCellValue("DateRecup");

//                    

                }

                // On ajoute la donnée à la sheet

                Integer newindex = sheet.getLastRowNum();

                if (newindex == null) {
                    newindex = 0;
                } else {
                    newindex++;
                }


                r = sheet.createRow(newindex);

                c = r.createCell(0);
                c.setCellValue(listItem.get(rownum).getID());
//
//                    // Gestion des titre;
                c = r.createCell(1);
                c.setCellValue(listItem.get(rownum).getTitre());

                // Gestion de la description
                c = r.createCell(2);
                c.setCellValue(listItem.get(rownum).getDescription());



                c = r.createCell(3);
                c.setCellValue(listItem.get(rownum).getLink());


                // Gestion du contenu
                c = r.createCell(4);
                c.setCellValue(listItem.get(rownum).getContenu());

                //Gestion date recuo

                c = r.createCell(5);
                if (listItem.get(rownum).getDatePub() != null) {
                    c.setCellValue(fmt.print(new DateTime(listItem.get(rownum).getDatePub())));
                } else {
                    c.setCellValue("");
                }



                //Gestion de la date de publication
                c = r.createCell(6);
                if (listItem.get(rownum).getDateRecup() != null) {
//                    c = r.createCell(6);
                    c.setCellValue(fmt.print(new DateTime(listItem.get(rownum).getDateRecup())));
                } else {
                    c.setCellValue("");
                }



            }
        }
    }

    rownum++;

    cs3.setBorderBottom(cs3.BORDER_THICK);

    OutputStream outt = response.getOutputStream();
    wb.write(outt);
    outt.close();


%>