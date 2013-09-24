/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import rssagregator.services.AbstrService;
import rssagregator.services.AbstrTacheSchedule;

/**
 *
 * @author clem
 */
public class ServiceXMLTool {

    /**
     * *
     * La méthode static doit parcourir le fichier servicedef.xml afin de
     * générer chaque service. Pour chaque chmo vim se service, elle crée les
     * tache définit dans le même xml et les lance suivant les parametres donnée
     * par le XML
     */
    public static void instancierServiceEtTache() throws IOException, JDOMException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        String propfile = PropertyLoader.loadProperti("serv.properties", "varpath");

        SAXBuilder sxb = new SAXBuilder();
        org.jdom.Document document;
        document = sxb.build(new File(propfile + "/servicedef.xml"));
        Element racine = document.getRootElement();

        List listService = racine.getChildren("service");


        for (int i = 0; i < listService.size(); i++) {
            Element elementService = (Element) listService.get(i);
            System.out.println("" + elementService);
            Attribute attributclass = elementService.getAttribute("class");
            System.out.println("Class : " + attributclass.getValue());

            // on instancie la class
            Class cService = Class.forName(attributclass.getValue());
            Method serviceGetInstance = cService.getMethod("getInstance");
            AbstrService service = (AbstrService) serviceGetInstance.invoke(null, new Object[0]);
            

            // On instancie chaque tache
            List listTache = elementService.getChildren("tache");
            for (int j = 0; j < listTache.size(); j++) {
                Element tacheElement = (Element) listTache.get(j);
                // Récupération de la class
                Attribute attClassTache = tacheElement.getAttribute("class");
                System.out.println("CLASS TACHE : " + attClassTache.getValue());
                Class cTache = Class.forName(attClassTache.getValue());
                Object tache = cTache.newInstance();
                // Paramettrage de la tache

                Element elementJour = tacheElement.getChild("schedulejourfixe");
                if (elementJour != null) {
                    // Récupération de l'attribut jour
                    Attribute attJour = elementJour.getAttribute("jour");
                    Attribute attheure = elementJour.getAttribute("heure");
                    Attribute attminute = elementJour.getAttribute("minute");

                    AbstrTacheSchedule castTache = (AbstrTacheSchedule) tache;
                    castTache.setJourSchedule(new Integer(attJour.getValue()));
                    castTache.setHeureSchedule(new Integer(attheure.getValue()));
                    castTache.setMinuteSchedule(new Integer(attminute.getValue()));
                    //On ajoute la tache au service. 

                 
                    service.schedule(castTache);

                } else {
                    Element elementDuree = tacheElement.getChild("scheduleduree");
                    Attribute attnbSec = elementDuree.getAttribute("nbSeconde");
                    AbstrTacheSchedule cast = (AbstrTacheSchedule) tache;
                    cast.setTimeSchedule(new Integer(attnbSec.getValue()));
                    service.schedule(cast);
                }
            }
            // On récupère la class
        }
    }

    public static void main(String[] args) {
        try {
            ServiceXMLTool.instancierServiceEtTache();

        } catch (IOException ex) {
            Logger.getLogger(ServiceXMLTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(ServiceXMLTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServiceXMLTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ServiceXMLTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ServiceXMLTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceXMLTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ServiceXMLTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ServiceXMLTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
