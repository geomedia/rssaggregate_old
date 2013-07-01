<%-- 
    Document   : fluxJspopml
    Created on : 24 juin 2013, 16:49:00
    Author     : clem
--%>

<%@page import="com.sun.syndication.feed.module.DCModule"%>
<%@page import="org.jdom.Element"%>
<%@page import="com.sun.syndication.io.impl.ModuleGenerators"%>
<%@page import="com.sun.syndication.feed.module.ModuleImpl"%>
<%@page import="com.sun.syndication.feed.opml.Attribute"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.sun.syndication.feed.module.Module"%>
<%@page import="com.sun.syndication.feed.module.DCModuleImpl"%>
<%@page import="com.sun.syndication.io.impl.DCModuleGenerator"%>
<%@page import="org.jdom.output.XMLOutputter"%>
<%@page import="org.jdom.Document"%>
<%@page import="java.net.URL"%>
<%@page import="com.sun.syndication.feed.opml.Outline"%>
<%@page import="com.sun.syndication.io.WireFeedOutput"%>
<%@page import="rssagregator.beans.traitement.RomeParse"%>
<%@page import="com.sun.syndication.io.impl.OPML20Generator"%>
<%@page import="com.sun.syndication.io.impl.OPML20Parser"%>
<%@page import="com.sun.syndication.io.impl.OPML10Generator"%>
<%@page import="com.sun.syndication.feed.opml.Opml"%>
<%@page import="java.util.List"%>
<%@page import="rssagregator.beans.Flux"%>
<%@page contentType="text/xml" pageEncoding="UTF-8"%>


<%
    List<Flux> listflux = (List<Flux>) request.getAttribute("listflux");

    WireFeedOutput feedOutput = new WireFeedOutput();
    Opml opml = new Opml();
    opml.setWindowTop(10);
    opml.setDocs("http://truc.com");


    opml.setTitle("zozo");

    List<Outline> outlines = (List<Outline>) opml.getOutlines();





//    Element element = new Element("zazaza", "/myUri");
//    element.setName("zouzou");
//    element.setText("lolo");
//    cModuleGenerator.generate(module, element);
//    
//    System.out.println(""+ element);
//    List<Module> listMod = new ArrayList<Module>();
//    opml.setModules(listMod);




    int i;
    for (i = 0; i < listflux.size(); i++) {




        if (listflux.get(i).getParentFlux() == null) {
            Outline myOutline = listflux.get(i).getOpmlOutline();

//            DCModuleImpl module = new DCModuleImpl();
//            DCModuleGenerator cModuleGenerator = new DCModuleGenerator();
//            
//            Element element = new Element("zozoz");
//            cModuleGenerator.generate(module, element);
//            
//        
//            
//            System.out.println("URI : " + module.getUri());


//            DCModuleGenerator dcg = new DCModuleGenerator();
//            Element element= new Element("zozo");
//            dcg.generate(module, element);

//            myOutline.getModules().add(module);

            System.out.println(myOutline.getModules());



            outlines.add(myOutline);
        }
    }

    opml.setOutlines(outlines);

//    DCModuleImpl cModuleImpl = new DCModuleImpl();
//    cModuleImpl.setTitle("module titre");
//    List<Module> listmodule = new ArrayList<Module>();
//    listmodule.add(cModuleImpl);
//    
//    opml.setModules(listmodule);



    OPML10Generator generator = new OPML10Generator();
    OPML20Generator generator1 = new OPML20Generator();



    DCModule module = new DCModuleImpl();
    module.setTitle("ddddd");
    module.setType("type");
    opml.getModules().add(module);

//    List<Module> listmod = new ArrayList<Module>();
//    listmod.add(module);


    Element ell = new Element("trucs");




    Document doc = generator.generate(opml);
    Element root = doc.getRootElement();

    List<Module> lism = new ArrayList<Module>();
    

//    generator.generateItemModules(opml.getModules(), root);
//    generator.generatePersonModules(opml.getModules(), root);
//    generator.generateItemModules(listmod, ell);



    XMLOutputter xmlo = new XMLOutputter();

    out.clear();
    out.println(xmlo.outputString(doc));
//    out.println(xmlo.outputString(ell));

%>



