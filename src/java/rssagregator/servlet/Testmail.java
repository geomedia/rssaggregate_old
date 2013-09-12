/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rssagregator.services.ServiceMailNotifier;

/**
 *
 * @author clem
 */
@WebServlet(name = "Testmail", urlPatterns = {"/Testmail"})
public class Testmail extends HttpServlet {

    private final static String MAILER_VERSION = "Java";

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();


//        envoyerMailSMTP("smtp.gmail.com", true);
        InternetAddress[] address = new InternetAddress[1];
//        try {
//            address[0] = new InternetAddress("clement.rillon@gmail.com");
//            ServiceMailNotifier.getInstance().envoyerMail(address, "mon truc", "Ceci est un mail de test");
//        } catch (AddressException ex) {
//            Logger.getLogger(Testmail.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (MessagingException ex) {
//            Logger.getLogger(Testmail.class.getName()).log(Level.SEVERE, null, ex);
//        }




        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Testmail</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Testmail at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    public static boolean envoyerMailSMTP(String serveur, boolean debug) {
        boolean result = false;
        try {
            Properties prop = System.getProperties();
//            prop.put("mail.smtp.host", serveur);
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true");
            prop.put("mail.smtp.host", "smtp.gmail.com");

//            Session session = Session.getDefaultInstance(prop, null);

            final String username = "clement.rillon";
            final String password = "#4lft3ldcm4";

            Session session = Session.getInstance(prop,
                    new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

//            Session.getInstance(prop,new javax.mail.Authenticator() {
//             return new PasswordAuthentication("clement.rillon", "df");   
//});

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("clement.rillon@gmail.com"));

            InternetAddress[] internetAddresses = new InternetAddress[1];
            internetAddresses[0] = new InternetAddress("clement.rillon@gmail.com");
            message.setRecipients(Message.RecipientType.TO, internetAddresses);
            message.setSubject("Test");
            message.setText("test mail");
            message.setHeader("X-Mailer", MAILER_VERSION);
            message.setSentDate(new Date());
            session.setDebug(debug);

            Transport.send(message);
            result = true;
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return result;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
