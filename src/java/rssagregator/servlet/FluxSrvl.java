/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.sun.syndication.io.FeedException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.ws.http.HTTPException;
import net.sf.json.util.PropertyFilter;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxPeriodeCaptation;
import rssagregator.beans.FluxType;
import rssagregator.beans.Journal;
import rssagregator.beans.form.ParseCsvForm;
import rssagregator.beans.traitement.CSVParse;
import rssagregator.beans.traitement.MediatorCollecteAction;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOGenerique;
import rssagregator.dao.DaoFlux;
import rssagregator.dao.DaoJournal;
import rssagregator.services.crud.ServiceCRUDFactory;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.tache.TacheRecupCallable;
import rssagregator.services.crud.ServiceCRUDFlux;
import rssagregator.services.tache.TacheFactory;
import rssagregator.utils.ServletTool;

/**
 *
 * @author clem
 */
@WebServlet(name = "Flux", urlPatterns = {"/flux/*"})
@MultipartConfig(location = "/home/clem", maxFileSize = 10485760, maxRequestSize = 52428800, fileSizeThreshold = 1048576) // Nécessaire pour l'envoie de fichier CSV
public class FluxSrvl extends HttpServlet {

    public static final String ATT_FORM = "form";
    public static final String ATT_OBJ = "bean";
    public static final String ATT_ACTION = "action";
    public static final String ATT_LISTOBJ = "listflux";
    public static final String ATT_SERV_NAME = "flux";
    public static final int TAILLE_TAMPON = 10240; // 10 ko
    public String VUE = "/WEB-INF/fluxJsp.jsp";
    Map redirmap = new HashMap<String, String>(); // La redirmap est envoyée à la jsp, il s'agit d'une hash map pouvant contenir les couple clé valeur (url : l'adresse de redirection) ( msg : le message a faire parvenir à l'utilisateur)
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FluxSrvl.class);

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

//        System.out.println("TEST : " + TacheFactory.ATT_TacheRecupCallable_maxExecuteTime);
        //Liste des clause servant à criteria, ces variables seront envoyé dans la dao par la suite
        Journal journalLie = null;
        String order_by = null;
        Boolean order_desc = null;
        Integer firstResult = null;
        Integer itPrPage = null;
        redirmap = null;

        SecurityManager manager = System.getSecurityManager();
        System.out.println("SECUMANAGER : " + manager);


        // Un simple attribut pour que le menu brille sur la navigation courante
        request.setAttribute("navmenu", "flux");

        // récupération de l'action (list, mod del ...). Si aucune action n'est précisée, alors on recherche qui correpond à la page d'acceuil de la recherhc des flux.
        String action = ServletTool.configAction(request, "recherche");

        request.setAttribute("srlvtname", ATT_SERV_NAME);

        // On récupère la vue
        String vue = request.getParameter("vue");
        if (vue == null) {
            vue = "html";
        }
        request.setAttribute("vue", vue);

        DaoJournal daoJournal = DAOFactory.getInstance().getDaoJournal();
        DaoFlux daoFlux = DAOFactory.getInstance().getDAOFlux();

        List<Object> journals = daoJournal.findall(); // On a besoin de la liste des journaux dans les pages html pour les menus déroulant. Il faut donc un attribut list journaux
        request.setAttribute("listjournaux", journals);

        //Il s'agit de la liste des flux à faire devant être affichées par la vue. (en cas de mod del list)
        List<Flux> flux = new ArrayList<Flux>();

        //=====================================================================================================================
        //                                              GESTION DES ACTIONS                                                   ||
        //=====================================================================================================================

        // Si l'utilisateur à demander la mise à jour manuelle du flux 
        //--------------------------------------------ACTION : AJOUT --------------------------------------------------------------------
        if (action.equals("add")) {
            //Quelques paramettres spécifique à la JSP FLUX.
            DAOGenerique dAOGenerique = DAOFactory.getInstance().getDAOGenerique();
            dAOGenerique.setClassAssocie(FluxType.class);
            request.setAttribute("listtypeflux", dAOGenerique.findall());
            request.setAttribute("listcomportement", DAOFactory.getInstance().getDAOComportementCollecte().findall());
            // GESTION DU BIND ET DE L'enregistremnet

            // On récupère la présélection du journal (utile pour ajouter un flux directement depuis la page journal
            try {
                Journal jSelect = (Journal) DAOFactory.getInstance().getDaoJournal().find(new Long(request.getParameter("journal-id")));
                request.setAttribute("jSelect", jSelect);
                System.out.println("On a selection le journa;l " + jSelect);
            } catch (Exception e) {
                System.out.println("ERRRRR" + e);
                logger.debug("err recup journal ", e);
            }

            ServletTool.actionADD(request, ATT_OBJ, ATT_FORM, Flux.class, true);

            //----------------------------------------------------ACTION : MODIFICATION----------------------------------------------------
        } else if (action.equals("mod")) {
            DAOGenerique dAOGenerique = DAOFactory.getInstance().getDAOGenerique();
            dAOGenerique.setClassAssocie(FluxType.class);
            request.setAttribute("listtypeflux", dAOGenerique.findall());
            request.setAttribute("listcomportement", DAOFactory.getInstance().getDAOComportementCollecte().findall());
            // GESTION DU BIND ET DE L'enregistremnet

            ServletTool.actionMOD(request, ATT_OBJ, ATT_FORM, Flux.class, true);



            //------------------------------------------------ACTION MAJ MANUELLE--------------------------------------------------------
        } else if (action.equals("maj")) {
            //Récupération d'une liste de flux
            List<Flux> listFlux = new ArrayList<Flux>();
            try {
                listFlux = new ArrayList<Flux>();
                List<Long> listId = ServletTool.parseidFromRequest(request, null);
                for (int i = 0; i < listId.size(); i++) {
                    Long long1 = listId.get(i);
                    listFlux.add((Flux) daoFlux.find(long1));
                }

                List<TacheRecupCallable> listTache = ServiceCollecteur.getInstance().majManuellAll(listFlux);
                request.setAttribute(ATT_LISTOBJ, listFlux);
                request.setAttribute("listTache", listTache);


            } catch (NumberFormatException e) {
                ServletTool.redir(request, "flux/maj", "Flux Inconnu", true);
            } catch (NoResultException e) {
                ServletTool.redir(request, "flux/maj", "Flux Inconnu", true);
            } catch (Exception e) {
                logger.debug("exx", e);
//                AbstrIncident incid = ServiceGestionIncident.getInstance().gererIncident(e, flux);
//                ServletTool.redir(request, "flux/maj", "ERREUR LORS DE La récup DU FLUX. : " + e.toString(), true);
            }

        } // Si l'action est liste, on récupère la liste des flux
        //-------------------------------------------------------- ACTION LIST --------------------------------------------------------
        else if (action.equals("list")) {
            System.out.println("----------------");
            System.out.println("ACTION LIST");
            System.out.println("----------------");
            //            // Restriction en fonction du journal
            try {
                Long idJournal = new Long(request.getParameter("journalid"));
                request.setAttribute("journalid", idJournal);
                journalLie = (Journal) daoJournal.find(idJournal);
                daoFlux.setCriteriaJournalLie(journalLie);

            } catch (Exception e) {
                logger.debug(e);
            }

            ServletTool.actionLIST(request, Flux.class, null, daoFlux);
            System.out.println("-->--> LIST ACTION");
//            // On restreint la liste des flux affiché
//            List<Flux> list = null;
//            // Restriction en fonction du journal
//            try {
//                Long idJournal = new Long(request.getParameter("journalid"));
//                request.setAttribute("journalid", idJournal);
//                journalLie = (Journal) daoJournal.find(idJournal);
//            } catch (Exception e) {
//                logger.debug(e);
//            }
//            //On récupère le nombre max d'item
//            Integer nbItem = daoFlux.findNbMax(journalLie);
//            request.setAttribute("nbitem", nbItem);
//            System.out.println("nbitem" + nbItem);
//
//
//            // On récupère le nombre d'item par page
//            try {
//                itPrPage = new Integer(request.getParameter("itPrPage"));
//            } catch (Exception e) {
//                itPrPage = 30;
//            }
//            request.setAttribute("itPrPage", itPrPage);
//
//            //On restreint les items à trouver dans la recherche
//            try {
//                firstResult = new Integer(request.getParameter("firstResult"));
//                request.setAttribute("firstResult", firstResult);
//            } catch (Exception e) {
//                firstResult = 0;
//                System.out.println("YYY" + request.getParameter("firstResult"));
//                System.out.println("" + e);
//            }
//
//            list = daoFlux.findCretaria(journalLie, order_by, order_desc, firstResult, itPrPage, null, null);
//            System.out.println("LIST FLUX SIZE : " + list);
//            request.setAttribute(ATT_LISTOBJ, list);
            //-----------------------------------------------------ACTION REMOVE ---------------------------------------



        } else if (action.equals("rem")) {
            logger.debug("Action remmm");
            // On tente de supprimer. Si une exeption est levée pendant la suppression. On redirige l'utilisateur différement
            try {
                List<Flux> listFlux = ServletTool.getListFluxFromRequest(request, daoFlux);
                ServiceCRUDFlux service = (ServiceCRUDFlux) ServiceCRUDFactory.getInstance().getServiceFor(Flux.class);
                service.SupprimerListFlux(listFlux, true, null);

//                for (int i = 0; i < listFlux.size(); i++) {
//                    Flux flux1 = listFlux.get(i);
////                    ServiceCollecteur.getInstance().removeFluxWithItem(flux1);
//                    AbstrServiceCRUD service = ServiceCRUDFactory.getInstance().getServiceFor(flux1);
//                    service.supprimer(flux1);
//                }
//                daoFlux.removeall(listFlux);
                ServletTool.redir(request, "flux", "Suppression du flux effecué.", false);
            } catch (NoResultException e) {
                ServletTool.redir(request, "flux", "Vous demandez a supprimer des flux qui n'existent pas.", true);
            } catch (NumberFormatException e) {
                ServletTool.redir(request, "flux", "Vous demandez a supprimer des flux qui n'existent pas.", true);
            } catch (Exception ex) {
                Logger.getLogger(FluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
                ServletTool.redir(request, "flux?action=mod&id=", "ERREUR LORS DE LA SUPPRESSION DU FLUX. : " + ex.toString(), true);
            }
        } //-----------------------------------------------------ACTION RECHERCHE -----------------------------------------
        else if (action.equals("recherche")) {
        } //---------------------------------------------------ACTION READ--------------------------------------------------
        else if (action.equals("read")) {
            ServletTool.actionREAD(request, Flux.class, ATT_OBJ);
        } //-----------------------------------------------------ACTION IMPORT CSV
        else if (action.equals("importcsv")) {
            String varPath = DAOFactory.getInstance().getDAOConf().getConfCourante().getVarpath();
            //On récupère la phase
            String phase = request.getParameter("phase");



            if (request.getMethod().equals("POST")) {

                // On récupère le flux
                if (phase != null && phase.equals("upload")) {
                    Part part = request.getPart("csvfile");
                    String nomFichier = getNomFichier(part);

//Traitement du fichier envoyé
                    if (nomFichier != null && !nomFichier.isEmpty()) {
                        String nomChamp = part.getName();
                        request.setAttribute(nomChamp, nomFichier);
                        System.out.println("Le nom de fichier : " + nomFichier);

                        // écriture du fichier sur le disque

                        ecrireFichier(part, "import.csv", varPath + "upload/");
                        request.setAttribute("phase", "parse");


//                        try {
//                            
//                            List<Item> itemParse = parse.execute(null);
//                          
//
//                        } catch (IllegalArgumentException ex) {
//                            Logger.getLogger(FluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
//                        } catch (FeedException ex) {
//                            Logger.getLogger(FluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
//                        }
                    }

                } else if (phase.equals("parse")) {
                    request.setAttribute("phase", "parse");
                    ParseCsvForm form = new ParseCsvForm();
                    form.validate(request);
                    CSVParse parse = (CSVParse) form.bind(request, null, CSVParse.class);
                    parse.setInputStream(new FileInputStream(varPath + "upload/import.csv"));


                    Flux fl = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(request.getParameter("id")));
                    try {
                        MediatorCollecteAction clonemediator = fl.getMediatorFlux().genererClone();
                        clonemediator.setRequesteur(null); // On retire le requesteur
                        clonemediator.setParseur(parse);

                        clonemediator.executeActions(fl);

//                            request.setAttribute("itemParsees", itemParse);
                        HttpSession session = request.getSession();
                        System.out.println("-----> IMPORT");
                        session.setAttribute("imporComportement", clonemediator);
                        System.out.println("FINN ----- ");

                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(FluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(FluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (HTTPException ex) {
                        Logger.getLogger(FluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (FeedException ex) {
                        Logger.getLogger(FluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(FluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (phase != null && phase.equals("saveItem")) {


                    //On récupère les items précédemment checké dans la requête
                    HttpSession session = request.getSession();
                    MediatorCollecteAction collecteAction = (MediatorCollecteAction) session.getAttribute("imporComportement");
                    //Récupération du flux

                    Flux fl = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(request.getParameter("id")));
                    collecteAction.persiter(fl);
//                    request.setAttribute("itemImport", itemImport);


                }

            }
        } else if (action.equals("statcaptation")) {

            // On récup l'ID de la période de captation
            List<Long> ids = ServletTool.parseidFromRequest(request, null);

            if (!ids.isEmpty()) {
                // 

                DAOGenerique dao = DAOFactory.getInstance().getDAOGenerique();
                dao.setClassAssocie(FluxPeriodeCaptation.class);
                FluxPeriodeCaptation periode = (FluxPeriodeCaptation) dao.find(ids.get(0));

                System.out.println("PERIODE DEMANDE : " + periode);

                ObjectMapper mapper = new ObjectMapper();

                FilterProvider filters = new SimpleFilterProvider().addFilter("serialisePourUtilisateur",
                        SimpleBeanPropertyFilter.serializeAllExcept("flux", "comportementDurantLaPeriode"));
                
                String jsonn =  mapper.writer(filters).writeValueAsString(periode);
                request.setAttribute("jsonstr", jsonn);
                System.out.println(""+jsonn);
                
                vue = "jsonstr";

            }

        }

        //------------------------------------------------------------------------
        //              Gestion de la VUE et de la redirection 
        //------------------------------------------------------------------------

        if (vue.equals("json")) {
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "inline");
            VUE = "/WEB-INF/fluxJSON.jsp";
        } else if (vue.equals("opml")) {
            response.setContentType("application/xml;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            VUE = "/WEB-INF/fluxOPML.jsp";
        } else if (vue.equals("jsondesc")) {
            VUE = "/WEB-INF/fluxJSONDesc.jsp";
        } else if (vue.equals("fluxXMLsync")) {
            VUE = "/WEB-INF/fluxXMLsync.jsp";
            System.out.println("coucou");
        } else if (vue.equals("jsonform")) {
            VUE = "/WEB-INF/jsonform.jsp";
        } else if (vue.equals("highchart")) {
            VUE = "/WEB-INF/highchartFlux.jsp";
        } else if (vue.equals("grid")) {
            VUE = "/WEB-INF/fluxJSONGrid.jsp";
            System.out.println("GRID VUEE");
        } else if (vue.equals("csv")) {
            VUE = "/WEB-INF/fluxCSV.jsp";
        }
        else if(vue.equals("jsonstr")){
            VUE = "/WEB-INF/jsonPrint.jsp";
        }
        
        
        else {
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            VUE = "/WEB-INF/fluxHTML.jsp";
        }
        this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
    }

    private static String getNomFichier(Part part) {
        /* Boucle sur chacun des paramètres de l'en-tête "content-disposition". */
        for (String contentDisposition : part.getHeader("content-disposition").split(";")) {
            /* Recherche de l'éventuelle présence du paramètre "filename". */
            if (contentDisposition.trim().startsWith("filename")) {
                /* Si "filename" est présent, alors renvoi de sa valeur, c'est-à-dire du nom de fichier. */
                return contentDisposition.substring(contentDisposition.indexOf('=') + 1);
            }
        }
        /* Et pour terminer, si rien n'a été trouvé... */
        return null;
    }

    private void ecrireFichier(Part part, String nomFichier, String chemin) throws IOException {
        /* Prépare les flux. */
        BufferedInputStream entree = null;
        BufferedOutputStream sortie = null;
        try {
            /* Ouvre les flux. */
            entree = new BufferedInputStream(part.getInputStream(), TAILLE_TAMPON);
            sortie = new BufferedOutputStream(new FileOutputStream(new File(chemin + nomFichier)),
                    TAILLE_TAMPON);

            /*
             * Lit le fichier reçu et écrit son contenu dans un fichier sur le
             * disque.
             */
            byte[] tampon = new byte[TAILLE_TAMPON];
            int longueur;
            while ((longueur = entree.read(tampon)) > 0) {
                sortie.write(tampon, 0, longueur);
            }
        } finally {
            try {
                sortie.close();
            } catch (IOException ignore) {
            }
            try {
                entree.close();
            } catch (IOException ignore) {
            }
        }
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
