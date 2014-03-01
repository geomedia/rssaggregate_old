/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import org.apache.commons.beanutils.PropertyUtils;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import rssagregator.beans.exception.RessourceIntrouvable;
import rssagregator.services.AbstrService;
import rssagregator.services.tache.AbstrTache;
import rssagregator.services.tache.TacheFactory;

/**
 *
 * @author clem
 */
public class ServiceXMLTool {

    /**
     * *
     * La méthode static doit parcourir le fichier servicedef.xml afin de générer chaque service. Pour chaque chmo vim
     * se service, elle crée les tache définit dans le même xml et les lance suivant les parametres donnée par le XML
     */
    public static void instancierServiceEtTache() throws IOException, JDOMException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NamingException, RessourceIntrouvable, Exception {

        String varPath = (String) PropertyLoader.returnConfPath() + "servicedef.xml";

        SAXBuilder sxb = new SAXBuilder();
        org.jdom.Document document;
        document = sxb.build(new File(varPath));
        Element racine = document.getRootElement();


        /**
         * **
         * Configuration de la factory
         */
        TacheFactory tacheFactory = TacheFactory.getInstance();
//        List listTacheconf = racine.getChildren("tachedefaultconf");
//        for (int i = 0; i < listTacheconf.size(); i++) {
//            
//            Element elementTachedefaultconf = (Element) listTacheconf.get(i);
//            Attribute att_ClassTache = elementTachedefaultconf.getAttribute("class");
//
//            // récup de la propriété
//            List listElementpropertie = elementTachedefaultconf.getChildren("propertie");
//            for (int j = 0; j < listElementpropertie.size(); j++) {
//                Element elementPropertie = (Element) listElementpropertie.get(j);
//
//
//                // Class
//                Attribute att_ClassPropertie = elementPropertie.getAttribute("class");
//                Attribute att_Key = elementPropertie.getAttribute("key");
//                Attribute att_Value = elementPropertie.getAttribute("value");
//
//
//                try {
//                    Class cTache = Class.forName(att_ClassTache.getValue());
//                    
//                    
//                    Field field = tacheFactory.getClass().getField("ATT_" + cTache.getSimpleName() + "_" + att_Key.getValue());
//                    Class c = Class.forName(att_ClassPropertie.getValue());
//                    
//                    if (c.equals(Short.class)) {
//                        
//                        Short val = new Short(att_Value.getValue());
////                        cast = new Short(att_Value.getValue());
//                        field.set(TacheFactory.class, val);
//                    }
//
//                } catch (NoSuchFieldException ex) {
//                    Logger.getLogger(ServiceXMLTool.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (SecurityException ex) {
//                    Logger.getLogger(ServiceXMLTool.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//
//
//
//        }





        /**
         * *
         * Instanciation des services et taches
         */
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
                ScheduledExecutorService es = (ScheduledExecutorService) methodFactory.invoke(null, nbThread.intValue());
                service.setExecutorService(es);
            }

            // On instancie chaque tache
            List listTache = elementService.getChildren("tache");
            for (int j = 0; j < listTache.size(); j++) {

                Element tacheElement = (Element) listTache.get(j);
                // Récupération de la class
                Attribute attClassTache = tacheElement.getAttribute("class");
                Class cTache = Class.forName(attClassTache.getValue());
//                AbstrTache castTache = (AbstrTache) cTache.newInstance();
                AbstrTache castTache = tacheFactory.getNewTask(cTache, Boolean.FALSE);

                castTache.addObserver(service);
//                AbstrTache castTache = (AbstrTache) tache;

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
                    castTache.setTimeSchedule(null);
                    byte b = 3;
                    castTache.setTypeSchedule(b);
                    
                    //On ajoute la tache au service. 
                    service.getTacheProducteur().produire(castTache);

                } else if (Elementscheduleduree != null) {
//                    Elementscheduleduree = tacheElement.getChild("scheduleduree");
                    Attribute attnbSec = Elementscheduleduree.getAttribute("nbSeconde");
                    castTache.setTimeSchedule(new Integer(attnbSec.getValue()));
                    Byte type = 1;
                    castTache.setSchedule(Boolean.TRUE);
                    castTache.setTypeSchedule(type);
                    castTache.completerNextExecution();
                    service.getTacheProducteur().produire(castTache);
                } else if (Elementtouslesjoura != null) {
                    Attribute attHeure = Elementtouslesjoura.getAttribute("heure");
                    Attribute attMinute = Elementtouslesjoura.getAttribute("minute");
                    castTache.setHeureSchedule(new Integer(attHeure.getValue()));
                    castTache.setMinuteSchedule(new Integer(attMinute.getValue()));
                    castTache.setJourSchedule(null);
                    castTache.setTimeSchedule(null);
                    byte type = 2;
                    castTache.setTypeSchedule(type);
                    service.getTacheProducteur().produire(castTache);
                }


                //---------------------------------------------------
                // Chargement des élément var
                //---------------------------------------------------
                /**
                 * *
                 * Il est possible de préciser dans le fichier la valeur d'une variable de la tache. C'est le role de ce
                 * petit block. La variable pour être définit par ce biaix doit posséder un constructeur acceptant un
                 * String pour argument. La variable doit posséder un getter et setter dans l'objet
                 */
                // On récupère les élément var qui permettent de préciser des variable dans les taches depuis servicedef.xml. Surclass ce qui est définit dans la factory
                List listElVar = tacheElement.getChildren("var");
                for (int k = 0; k < listElVar.size(); k++) {
                    Element elVar = (Element) listElVar.get(k);

                    // On récupère l'attribut class
                    Attribute attClass = elVar.getAttribute("class");
                    String valClass = attClass.getValue();
                    Class c = Class.forName(valClass);


                    Attribute attVal = elVar.getAttribute("value");
                    String val = attVal.getValue();

                    Attribute attname = elVar.getAttribute("name");
                    String name = attname.getValue();


                    // iNSTANCIATION de la propriétée
                    Constructor ctr = c.getConstructor(String.class);
                    Object obj = ctr.newInstance(val);


                    // Injection de la propriété dans l'objet
                    PropertyUtils.setProperty(castTache, name, obj);

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
        } catch (NamingException ex) {
            Logger.getLogger(ServiceXMLTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RessourceIntrouvable ex) {
            Logger.getLogger(ServiceXMLTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServiceXMLTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
