///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package rssagregator.services;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.StringReader;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.apache.log4j.net.SMTPAppender;
//import rssagregator.beans.Conf;
//import rssagregator.dao.DAOConf;
//import rssagregator.dao.DAOFactory;
//
///**
// * Cette tâche permet d'inspecter le fichier le log. Si de nouvelles lignes ont été inscrite. Cette tâche est gérée par
// * le {@link ServiceServer}
// *
// * @author clem
// */
//public class TacheCheckLogErr extends AbstrTacheSchedule<TacheCheckLogErr> {
//
//    /**
//     * *
//     * Les lignes nouvelles dans le fichiers de log. Celles ci seront envoyées par mail par le service
//     * {@link ServiceServer}
//     */
//    String newLogLines;
//    File fErrLog = null;
//    File fErrOldLog = null;
//    FileReader fileReader1 = null;
//    FileReader fileReader2 = null;
//    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TacheCheckLogErr.class);
//
//    @Override
//    public TacheCheckLogErr call() throws Exception {
//             System.out.println("debut");
//        
//        try {
//            this.exeption = null;
//
////            Conf c = DAOFactory.getInstance().getDAOConf().getConfCourante();
////            String varpath = c.getVarpath();
//            String varpath = "/var/lib/RSSAgregate/";
//            
//            System.out.println("PATH : " + varpath);
//
//
//            // Chargement du fichier err.log
////            fErrLog = new File(varpath + "log/err.log");
//            String errStr = null; 
//            String errOldStr = null; 
//            
//            
//            try {
//                errStr = readTxtFile(varpath + "log/err.log");
//            } catch (Exception e) {
//                logger.info("info ", e);
//            }
//            
//            try {
//                errOldStr = readTxtFile(varpath + "log/olderr.log");
//            } catch (Exception e) {
//                logger.info("ee", e);
//            }
//            
//            System.out.println("File 1 " + errStr);
//            System.out.println("===");
//            System.out.println(errOldStr);
//       
//
//
//
//        } catch (Exception e) {
//            this.exeption = e;
//
//        } finally {
//
//            if (fErrLog != null) {
//            }
//
//            this.setChanged();
//            notifyObservers();
//            return this;
//
//        }
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    public String readTxtFile(String uriFileToRead) throws FileNotFoundException, Exception {
//        BufferedReader in = new BufferedReader(new FileReader(uriFileToRead));
//        String line = "";
//        String retour = "";
//        try {
//            while ((line = in.readLine()) != null) {
//                
//                retour += line;
//                System.out.println(line);
//            }
//            return retour;
//        } catch (Exception e) {
//            logger.info("Erreur", e);
//            throw e;
//        } finally {
//            //On ferme quoi qu'il arrive
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException ex) {
//                    logger.info("ee", ex);
//                    Logger.getLogger(TacheCheckLogErr.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
//    
//    
//    public static void main(String[] args) {
//        
//        
//        TacheCheckLogErr checkLogErr = new TacheCheckLogErr();
//        try {
//            checkLogErr.call();
//        } catch (Exception ex) {
//            Logger.getLogger(TacheCheckLogErr.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//}
