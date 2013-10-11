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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
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
            Attribute attributclass = elementService.getAttribute("class");

            // on instancie la class
            Class cService = Class.forName(attributclass.getValue());
            Method serviceGetInstance = cService.getMethod("getInstance");
            AbstrService service = (AbstrService) serviceGetInstance.invoke(null, new Object[0]);


            //Définition du pool si précisé
            Element ElementPool = elementService.getChild("pool");
            if (ElementPool != null) {
                Attribute attNbThread = ElementPool.getAttribute("nbThread");
                Integer nbThread = new Integer(attNbThread.getValue());

                Attribute attMethodeInstanciation = ElementPool.getAttribute("methodeInstanciation");
                Method methodFactory = Executors.class.getMethod(attMethodeInstanciation.getValue(), int.class);
                ScheduledExecutorService es = (ScheduledExecutorService) methodFactory.invoke(null, nbThread);
                service.setExecutorService(es);
            }

            // On instancie chaque tache
            List listTache = elementService.getChildren("tache");
            for (int j = 0; j < listTache.size(); j++) {
                Element tacheElement = (Element) listTache.get(j);
                // Récupération de la class
                Attribute attClassTache = tacheElement.getAttribute("class");
                Class cTache = Class.forName(attClassTache.getValue());
                AbstrTacheSchedule castTache = (AbstrTacheSchedule) cTache.newInstance();
                castTache.addObserver(service);
//                AbstrTacheSchedule castTache = (AbstrTacheSchedule) tache;

                // Paramettrage de la tache

                // récupération du paramettre schedule. Si on ne le trouve pas la tache est considérée comme schedulé
                Attribute attSchedule = tacheElement.getAttribute("scheduled");
                if (attSchedule != null) {
                    if (attSchedule.getValue().equals("true")) { 
                        castTache.setSchedule(Boolean.TRUE);
                    } else {
                        castTache.setSchedule(Boolean.FALSE);
                    }
                } else {
                    castTache.setSchedule(Boolean.TRUE);
                }
                
                //Envoie de la tâche au service

                Element elementJour = tacheElement.getChild("schedulejourfixe");
                Element Elementscheduleduree = tacheElement.getChild("scheduleduree");
                Element Elementtouslesjoura = tacheElement.getChild("touslesjoura");
                if (elementJour != null) {
                    // Récupération de l'attribut jour
                    Attribute attJour = elementJour.getAttribute("jour");
                    Attribute attheure = elementJour.getAttribute("heure");
                    Attribute attminute = elementJour.getAttribute("minute");

                    castTache.setJourSchedule(new Integer(attJour.getValue()));
                    castTache.setHeureSchedule(new Integer(attheure.getValue()));
                    castTache.setMinuteSchedule(new Integer(attminute.getValue()));
                    //On ajoute la tache au service. 

                    service.schedule(castTache);

                } else if (Elementscheduleduree != null) {
//                    Elementscheduleduree = tacheElement.getChild("scheduleduree");
                    Attribute attnbSec = Elementscheduleduree.getAttribute("nbSeconde");
                    castTache.setTimeSchedule(new Integer(attnbSec.getValue()));
                    service.schedule(castTache);
                } else if (Elementtouslesjoura != null) {
                    Attribute attHeure = Elementtouslesjoura.getAttribute("heure");
                    Attribute attMinute = Elementtouslesjoura.getAttribute("minute");
                    castTache.setHeureSchedule(new Integer(attHeure.getValue()));
                    castTache.setMinuteSchedule(new Integer(attMinute.getValue()));
                    castTache.setJourSchedule(null);
                    castTache.setTimeSchedule(null);
                    service.schedule(castTache);
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
