///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package rssagregator.servlet;
//
//import au.com.bytecode.opencsv.CSVWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.List;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import rssagregator.beans.Flux;
//import rssagregator.beans.Item;
//import rssagregator.dao.DAOFactory;
//import rssagregator.dao.DaoItem;
//
///**
// *
// * @author clem
// */
//@WebServlet(name = "ExportSrvlt", urlPatterns = {"/Export"})
//public class ExportSrvlt extends HttpServlet {
//
//    /**
//     * Processes requests for both HTTP
//     * <code>GET</code> and
//     * <code>POST</code> methods.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        DaoItem daoItem = DAOFactory.getInstance().getDaoItem();
//
//        // récupération du format
//        String format = request.getParameter("format");
//        StringWriter sw = new StringWriter();
//
//        try {
//            Flux f = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(request.getParameter("id-flux")));
//            daoItem.setWhere_clause_flux(f);
//            request.setAttribute("idflux", f.getID().toString());
//        } catch (Exception e) {
//            daoItem.setWhere_clause_flux(null);
//        }
//
//        //Selection de l'ordre
//        try {
//            String s = request.getParameter("order");
//            String desc = request.getParameter("desc");
//            if (!s.equals("")) {
//                if (s.equals("dateRecup") || s.equals("datePub")) {
//                    daoItem.setOrder_by(s);
//                    if (desc.equals("true")) {
//                        daoItem.setOrder_desc(Boolean.TRUE);
//                    }
//                }
//            }
//        } catch (Exception e) {
//        }
//
//        // On execute la requete : 
//        List<Item> listItem;
//        listItem = daoItem.findCretaria();
//
//        if (format.equals("csv")) {
//
//            CSVWriter cSVWriter = new CSVWriter(sw);
//            List<String[]> data = new ArrayList<String[]>();
//
//            int i;
//            for (i = 0; i < listItem.size(); i++) {
//                Item it = listItem.get(i);
//                data.add(new String[]{it.getID().toString().trim(), it.getTitre().trim(), it.getDescription().trim()});
//
//            }
//
//
//            cSVWriter.writeAll(data);
//            cSVWriter.close();
//
//        }
//
//        response.setContentType("text/csv;charset=UTF-8");
//        PrintWriter out = response.getWriter();
//        try {
//            out.println(sw.toString());
//        } finally {
//            out.close();
//        }
//    }
//
//    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
//    /**
//     * Handles the HTTP
//     * <code>GET</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }
//
//    /**
//     * Handles the HTTP
//     * <code>POST</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }
//
//    /**
//     * Returns a short description of the servlet.
//     *
//     * @return a String containing servlet description
//     */
//    @Override
//    public String getServletInfo() {
//        return "Short description";
//    }// </editor-fold>
//}
