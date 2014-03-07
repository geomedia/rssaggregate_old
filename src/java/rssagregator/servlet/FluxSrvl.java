/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.xml.ws.http.HTTPException;
import rssagregator.beans.Flux;
import rssagregator.beans.FluxPeriodeCaptation;
import rssagregator.beans.FluxType;
import rssagregator.beans.Item;
import rssagregator.beans.Journal;
import rssagregator.beans.UserAccount;
import rssagregator.beans.form.ParseCsvForm;
import rssagregator.beans.incident.CollecteIncident;
import rssagregator.beans.traitement.CSVParse;
import rssagregator.beans.traitement.VisitorCollecteActionCSV;
import rssagregator.beans.traitement.ComportementCollecte;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.DAOGenerique;
import rssagregator.dao.DaoFlux;
import rssagregator.dao.DaoJournal;
import rssagregator.services.crud.ServiceCRUDFactory;
import rssagregator.services.ServiceCollecteur;
import rssagregator.services.tache.TacheRecupCallable;
import rssagregator.services.crud.ServiceCRUDFlux;
import rssagregator.utils.PropertyLoader;
import rssagregator.utils.ServletTool;

/**
 *
 * @author clem
 */
@WebServlet(name = "Flux", urlPatterns = {"/flux/*"})
@MultipartConfig(maxFileSize = 104857600, maxRequestSize = 524288000, fileSizeThreshold = 1048576) // Nécessaire pour l'envoie de fichier CSV
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

        //Liste des clause servant à criteria, ces variables seront envoyé dans la dao par la suite
        Journal journalLie;
        redirmap = null;


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
        
        List<Journal> journals = daoJournal.findallOrederByTitre(false); // On a besoin de la liste des journaux dans les pages html pour les menus déroulant. Il faut donc un attribut list journaux
        request.setAttribute("listjournaux", journals);


        //Il s'agit de la liste des flux à faire devant être affichées par la vue. (en cas de mod del list)
        List<Flux> flux = new ArrayList<Flux>();

        //=====================================================================================================================
        //                                              GESTION DES ACTIONS                                                   ||
        //=====================================================================================================================


        //------------------------------------------------------------------------------------------------------------
        //.                         .                   .ACTION : AJOUT 
        //------------------------------------------------------------------------------------------------------------
        // Si l'utilisateur à demander la mise à jour manuelle du flux 

        if (action.equals("add")) {
            //Quelques paramettres spécifique à la JSP FLUX.
            DAOGenerique dAOGenerique = DAOFactory.getInstance().getDAOGenerique();
            dAOGenerique.setClassAssocie(FluxType.class);
            request.setAttribute("listtypeflux", dAOGenerique.findall());
            
            journals = daoJournal.findallOrederByTitre(true); // On a besoin de la liste des journaux dans les pages html pour les menus déroulant. Il faut donc un attribut list journaux
            request.setAttribute("listjournaux", journals);
            
            request.setAttribute("listcomportement", DAOFactory.getInstance().getDAOComportementCollecte().findall());

            // On récupère la présélection du journal (utile pour ajouter un flux directement depuis la page journal
            try {
                request.setAttribute("jSelect", DAOFactory.getInstance().getDaoJournal().find(new Long(request.getParameter("journal-id"))));
            } catch (Exception e) {
            }
            
            ServletTool.actionADD(request, ATT_OBJ, ATT_FORM, Flux.class, true);
            //------------------------------------------------------------------------------------------------------------
            //.                                .  .  .ACTION : MODIFICATION
            //------------------------------------------------------------------------------------------------------------
        } else if (action.equals("mod")) {
            DAOGenerique dAOGenerique = DAOFactory.getInstance().getDAOGenerique();
            dAOGenerique.setClassAssocie(FluxType.class);
            request.setAttribute("listtypeflux", dAOGenerique.findall());
            request.setAttribute("listcomportement", DAOFactory.getInstance().getDAOComportementCollecte().findall());
            // GESTION DU BIND ET DE L'enregistremnet

            ServletTool.actionMOD(request, ATT_OBJ, ATT_FORM, Flux.class, true);



            //------------------------------------------------------------------------------------------------------------
            //.    .    .    .    .             .ACTION MAJ MANUELLE
            //------------------------------------------------------------------------------------------------------------
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
         
            //            // Restriction en fonction du journal
            try {
                Long idJournal = new Long(request.getParameter("journalid"));
                request.setAttribute("journalid", idJournal);
                journalLie = (Journal) daoJournal.find(idJournal);
                daoFlux.setCriteriaJournalLie(journalLie);
                
            } catch (Exception e) {
                logger.debug(e);
            }
            
            try {
                Long idType = new Long(request.getParameter("typeid"));
                request.setAttribute("typeid", idType);
                DAOGenerique daoG = DAOFactory.getInstance().getDAOGenerique();
                daoG.setClassAssocie(FluxType.class);
                
                FluxType fluxType= (FluxType) daoG.find(idType);
                daoFlux.setCriteriaFluxType(fluxType);
                
                
            } catch (Exception e) {
            }
            
            
            ServletTool.actionLIST(request, Flux.class, null, daoFlux);
            //------------------------------------------------------------------------------------------------------------
            //-----------------------------------------------------ACTION REMOVE ---------------------------------------
            //------------------------------------------------------------------------------------------------------------

        } else if (action.equals("rem")) {
            // On tente de supprimer. Si une exeption est levée pendant la suppression. On redirige l'utilisateur différement
            try {
                List<Flux> listFlux = ServletTool.getListFluxFromRequest(request, daoFlux);
                ServiceCRUDFlux service = (ServiceCRUDFlux) ServiceCRUDFactory.getInstance().getServiceFor(Flux.class);
                service.SupprimerListFlux(listFlux, true, null);
                
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
        } //------------------------------------------------------------------------------------------------------------
        //---------------------------------------------------ACTION READ
        //------------------------------------------------------------------------------------------------------------
        /**
         * *
         * Pour la lecture des information d'un beans
         */
        else if (action.equals("read")) {

            // récupération des principaux incidents de collecte
            try {
                Long id = new Long(request.getParameter("id"));
                
                List<CollecteIncident> indids = daoFlux.findPrincipauxIncident(id, 1, 5);
                request.setAttribute("indids", indids);
                ServletTool.actionREAD(request, Flux.class, ATT_OBJ);
            } catch (Exception e) {
                ServletTool.redir(request, "flux", "flux qui n'existent pas.", true);
            }
            
            
        } //------------------------------------------------------------------------------------------------------------
        //--------------------------------------------ACTION IMPORT CSV
        //------------------------------------------------------------------------------------------------------------
        /**
         * *
         * Gère l'import d'items en trois phase : upload, parse, enregistrement.
         */
        else if (action.equals("importcsv")) {
            
            String phase = request.getParameter("phase");   //On récupère la phase (upload, parse, save...)
            if (request.getParameter("init") != null && !request.getParameter("init").isEmpty() && request.getParameter("init").equals("true")) {
                phase = "";
                request.setAttribute("phase", "");
            }
            
            if (request.getMethod().equals("POST")) {
                System.out.println("POST");

                //---------------> Phase : upload
                if (phase != null && phase.equals("upload")) {
                    System.out.println("Phase upload");
                    try {
                        System.out.println("Try upload");
                        HttpSession session = request.getSession();

                        // Dans cette phase, on récupère le fichier, puis on instancie le collecteur pour le flux et l'on associe le fichier a ce comportement qui sera utilisé dans la phase suivant : le parsing

                        // On récupère le flux
                        Part part = request.getPart("csvfile");
                        
                        
                        String nomFichier = getNomFichier(part);
//Traitement du fichier envoyé
                        if (nomFichier != null && !nomFichier.isEmpty()) {
                            String nomChamp = part.getName();
                            request.setAttribute(nomChamp, nomFichier);

                            // écriture du fichier sur le disque

                            Date dateCurrent = new Date();
                            Long l = new Long(dateCurrent.getTime());
                            UserAccount user = (UserAccount) session.getAttribute("authuser");
                            String nomFichierUpload = "importCSV_" + l.toString() + "_" + user.getUsername() + ".csv";
                            
                            byte[] resu = returnByteFromUploadedFile(part);
                            session.setAttribute("stringCSV", resu);
                            request.setAttribute("phase", "parse");
                            
                            Flux fl = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(request.getParameter("id")));
                            VisitorCollecteActionCSV visitorCollecteActionCSV = new VisitorCollecteActionCSV();
                            session.setAttribute("visitor", visitorCollecteActionCSV);
                            
                            visitorCollecteActionCSV.setByteCSV(resu);
                            System.out.println("Fin upload");
                        }
                    } catch (Exception e) {
                        
                        logger.debug("Erreur", e);
                    }


                    //---------> Phase : parse
                } else if (phase != null && phase.equals("parse")) {
                    HttpSession session = request.getSession();
                    
                    request.setAttribute("phase", "parse");
                    ParseCsvForm form = new ParseCsvForm();
                    form.validate(request);
                    
                    Flux fl = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(request.getParameter("id")));
                    try {

                        // Création du visiteur permettant de récupérer les items depuis un CSV
                        VisitorCollecteActionCSV visitorCollecteActionCSV = (VisitorCollecteActionCSV) session.getAttribute("visitor");
                        
                        ComportementCollecte clonemediator = fl.getMediatorFlux().genererClone();
                        CSVParse parser = (CSVParse) form.bind(request, null, CSVParse.class);
                        clonemediator.setParseur(parser);
                        visitorCollecteActionCSV.setComportementCollecte(clonemediator);
                        
                        visitorCollecteActionCSV.visit(fl); // On lance la visite du flux. C'est à dire le travail de parsing
                        request.setAttribute("items", visitorCollecteActionCSV.getListItem());
                        
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(FluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (HTTPException ex) {
                        Logger.getLogger(FluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(FluxSrvl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    request.setAttribute("phase", "presave");
                } //------------> phase : saveItem
                else if (phase != null && phase.equals("saveItem")) {
                    
                    try {
                        //On récupère les items précédemment checké dans la requête
                        HttpSession session = request.getSession();
                        VisitorCollecteActionCSV visiteur = (VisitorCollecteActionCSV) session.getAttribute("visitor");
                        //Récupération du flux

                        Flux fl = (Flux) DAOFactory.getInstance().getDAOFlux().find(new Long(request.getParameter("id")));
//                    collecteAction.persiter(fl);
//                    ServiceCRUDFlux serviceCrud = (ServiceCRUDFlux) ServiceCRUDFactory.getInstance().getServiceFor(Flux.class);
                        ServiceCollecteur collecteur = ServiceCollecteur.getInstance();
                        List<Item> items = visiteur.getListItem();
                        
                        EntityManager em = DAOFactory.getInstance().getEntityManager();
                        em.getTransaction().begin();
                        
                        for (int i = 0; i < items.size(); i++) {
                            Item item = items.get(i);
                            collecteur.ajouterItemAuFlux(fl, item, em, false, null);
                        }
                        em.getTransaction().commit();
                        // si tout s'est bien déroulé 
                        session.setAttribute("imporComportement", null); // On détruit le comportement dans la session

                    } catch (Exception e) {
                        request.setAttribute("exception", e);
                    }
                    // TODO : c'est très moche. Il faut faire passer du cde de la couche srvlt a la couche service
                }
            }
            //---------------------------------------------------------------------------------------------------------
            //                          ACTION : statcaptation
            //---------------------------------------------------------------------------------------------------------
            /**
             * *
             * Permet d'afficher des stat de capture de flux en ajax.
             */
        } else if (action.equals("statcaptation")) {

            // On récup l'ID de la période de captation
            List<Long> ids = ServletTool.parseidFromRequest(request, null);
            
            if (!ids.isEmpty()) {
                
                DAOGenerique dao = DAOFactory.getInstance().getDAOGenerique();
                dao.setClassAssocie(FluxPeriodeCaptation.class);
                FluxPeriodeCaptation periode = (FluxPeriodeCaptation) dao.find(ids.get(0));
                
                
                ObjectMapper mapper = new ObjectMapper();
                
                FilterProvider filters = new SimpleFilterProvider().addFilter("serialisePourUtilisateur",
                        SimpleBeanPropertyFilter.serializeAllExcept("flux", "comportementDurantLaPeriode"));
                
                String jsonn = mapper.writer(filters).writeValueAsString(periode);
                request.setAttribute("jsonstr", jsonn);
                
                request.setAttribute("text", "Ok enregistrement réalisé");
                vue = "jsonPrint";
            }
        }


        //=============================================================================================
        //              Gestion de la VUE et de la redirection 
        //=============================================================================================

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
        } else if (vue.equals("jsonform")) {
            VUE = "/WEB-INF/jsonform.jsp";
        } else if (vue.equals("highchart")) {
            VUE = "/WEB-INF/highchartFlux.jsp";
        } else if (vue.equals("grid")) {
            VUE = "/WEB-INF/fluxJSONGrid.jsp";
        } else if (vue.equals("csv")) {
            VUE = "/WEB-INF/fluxCSV.jsp";
        } else if (vue.equals("jsonstr")) {
            VUE = "/WEB-INF/jsonPrint.jsp";
        } else if (action.equals("importcsv")) {
            String phase = request.getParameter("phase");
            
            if (phase == null || (phase != null && phase.isEmpty())) {
                VUE = "/WEB-INF/fluxHTML.jsp";
            } else if (phase.equals("upload")) {
                VUE = "/WEB-INF/fluxHTML.jsp";
            } else if (phase.equals("parse")) {
                VUE = "/WEB-INF/itemJSONGrid2.jsp";
            } else if (phase.equals("saveItem")) {
                VUE = "/WEB-INF/printText.jsp";
                
            }
            
        } else if (vue.equals("printText")) {
            VUE = "/WEB-INF/printText.jsp";
        } else if (vue.equals("jsonPrint")) {
            VUE = "/WEB-INF/jsonPrint.jsp";
        } else {
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            VUE = "/WEB-INF/fluxHTML.jsp";
        }
        if (VUE != null && !VUE.isEmpty()) {
            this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
        }
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
    
    private byte[] returnByteFromUploadedFile(Part part) throws IOException {
        /* Prépare les flux. */
        BufferedInputStream entree = null;
        ByteArrayOutputStream sortie = null;
        try {
            /* Ouvre les flux. */
            entree = new BufferedInputStream(part.getInputStream(), TAILLE_TAMPON);
            
            sortie = new ByteArrayOutputStream(TAILLE_TAMPON);

            /*
             * Lit le fichier reçu et écrit son contenu dans un fichier sur le
             * disque.
             */
            byte[] tampon = new byte[TAILLE_TAMPON];
            int longueur;
            while ((longueur = entree.read(tampon)) > 0) {
                sortie.write(tampon, 0, longueur);
//                sortie.write(tampon, 0, longueur);

            }
            
            byte[] resu = sortie.toByteArray();
            return resu;
        } finally {
            try {
                if (sortie != null) {
                    sortie.close();
                }
                
            } catch (IOException ignore) {
            }
            try {
                if (entree != null) {
                    entree.close();
                }
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
