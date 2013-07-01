/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import rssagregator.beans.Item;


/**
 *
 * @author clem
 */
public class MoxyTest {
    public static void main(String[] args) {
        try {
            JAXBContext jAXBContext = JAXBContext.newInstance(Truc.class);
            FileReader bindingsFile = new FileReader("xml-bindings.xml");
            XmlBindings bindings = (XmlBindings) jAXBContext.createUnmarshaller().unmarshal(bindingsFile);
            
            
            
            
        } catch (JAXBException ex) {
            Logger.getLogger(MoxyTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MoxyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
