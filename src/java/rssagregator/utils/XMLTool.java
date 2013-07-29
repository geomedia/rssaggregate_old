/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;


import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author clem
 */
public class XMLTool {

    public static String serialise(Object bean) throws IOException {
//                    FileInputStream fileInputStream = new FileInputStream("saveXML.xml");
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(arrayOutputStream);
        String retour = null;

        try {
            encoder.writeObject(bean);
            encoder.flush();
        } catch (Exception e) {
        } finally {
            arrayOutputStream.close();
            encoder.close();
        }
        retour = arrayOutputStream.toString();
        return retour;
    }

    public static Object unSerialize(String xml) throws IOException {
        StringReader reader = new StringReader(xml);

        InputStream is = new ByteArrayInputStream(xml.getBytes());
        XMLDecoder decoder = new XMLDecoder(is);

        Object bean = null;

        try {
            bean = decoder.readObject();
        } catch (Exception e) {
        } finally {
            decoder.close();
            is.close();
            reader.close();
        }
        return bean;
    }

    // TEST DE LA DESERIALISATION
    public static void main(String[] args) {

//        Object bean = new Object();
//        bean.setID(2);
//        bean.setMessage("toto");
//
//        String r = null;
//        try {
//            r = XMLTool.serialise(bean);
//            System.out.println("=================");
//            System.out.println("SERALISE");
//            System.out.println(r);
//            System.out.println("=================");
//
//            System.out.println("DESERIALISE");
//        } catch (IOException ex) {
//            Logger.getLogger(XMLTool.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//
//        MessageBean mb;
//        try {
//            mb = XMLTool.unSerialize(r);
//            System.out.println("=======");
//            System.out.println("ID beans : " + mb.getID());
//            System.out.println("MESSAGE beans : " + mb.getMessage());
//
//        } catch (IOException ex) {
//            Logger.getLogger(XMLTool.class.getName()).log(Level.SEVERE, null, ex);
//        }



    }
//        // TEST DE LA SERIALISATION
//            public static void main(String[] args) {
//        // Test de la serialisation 
//        
//        MessageBean bean = new MessageBean();
//        bean.setID(2);
//        bean.setMessage("toto");
//        
//        String r = XMLTool.serialise(bean);
//        System.out.println(r);
//        
//    }
}
