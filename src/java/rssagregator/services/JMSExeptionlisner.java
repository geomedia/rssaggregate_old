///*        MAINTENANT FUSIONNE AVEC LE service JMS
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package rssagregator.services;
//
//import java.io.EOFException;
//import javax.jms.ExceptionListener;
//import javax.jms.JMSException;
//
///**
// *
// * @author clem
// */
//public class JMSExeptionlisner implements ExceptionListener{
//
//    @Override
//    public void onException(JMSException jmse) {
//
//         // En cas de perte de connection       
//        if(jmse.getCause() instanceof EOFException){
//            // On met le booleean du service à false. Le daemon se chargera de relancer le service
//            ServiceJMS.getInstance().setStatutConnection(false);
//        }
//
//    }
//}
