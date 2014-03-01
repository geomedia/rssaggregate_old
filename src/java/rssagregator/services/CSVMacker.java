/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import rssagregator.beans.Conf;
import rssagregator.beans.ContentRSS;
import rssagregator.beans.DoublonDe;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.beans.traitement.AbstrRaffineur;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.SearchFiltersList;
import rssagregator.utils.comparator.FileNameComparator;

/**
 *
 * @author clem
 */
public class CSVMacker implements Callable<Object> {

    SearchFiltersList filtre;
    List<Flux> fluxDemande;
    Date date1;
    Date date2;
    int nbrLigneParFichie = 10000;
    boolean escapebySlash = false;
    boolean rafine = false;
    /**
     * *
     * Détermine si le CSVMacker doit ou non supprimer le code HTML dans la derscription le titre et le contenu
     */
    boolean purgehtml = false;
    /**
     * *
     * Pour fonctionner le CSVMaker a besoin de connaitre le repéertoire webdir ou il doit placer le sfichier
     */
    private String webDir;

    public CSVMacker(String webDir) {
        this.webDir = webDir;
    }

    public CSVMacker(String webDir, boolean purgehtml) {
        this.purgehtml = purgehtml;
        this.webDir = webDir;
    }

    public CSVMacker(String webDir, boolean purgehtml, boolean escaBySlash) {
        this.purgehtml = purgehtml;
        this.webDir = webDir;
        this.escapebySlash = escaBySlash;
    }
//    File fileDestination;
    /**
     * **
     * Le répertoire dans lequel seront stocké les fichiers crée pas la Thread. Ce répertoire est crée par la thread
     * avec la mathore {@link #createUniqRep()
     */
    private String exportPath;
    /**
     * *
     * Le path vers lequel l'utilisateur doit être redirigé
     */
    private String redirPath;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    @Override
    public Object call() throws Exception {

        // Création du répertoire ou serotn stoqué les fichier exporté par l'utilisateur
        createUniqRep();

//        CSVWriter cSVWriter = new CSVWriter(fileWriter, '\t', '"', '\\');
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        long itemEnregistre = 0;
        int nbrFichier = 0;


        EntityManager em = DAOFactory.getInstance().getEntityManager();

        for (int i = 0; i < fluxDemande.size(); i++) {
            //On crée le fichier data avec entete
            List<String[]> data = new ArrayList<String[]>();




            Flux flux = fluxDemande.get(i);
            flux = em.find(Flux.class, flux.getID()); // Il faut rafraichir
            String nomFichier = constructionNomFichier(flux);
            List<AbstrRaffineur> raffFlux = flux.getMediatorFlux().getRaffineur();


            //------------------------------------------------
            //              Construction de l'entete
            //------------------------------------------------

            List<String> enteteData = new ArrayList<String>();

            enteteData.add("ID_Item");
            enteteData.add("Titre");
            enteteData.add("Description");
            enteteData.add("Contenu");
            enteteData.add("Categorie");
            enteteData.add("Lien");
            enteteData.add("Date_Recup");
            enteteData.add("Date_publication");
            enteteData.add("ID_flux");
            enteteData.add("typeFlux");
            enteteData.add("Journal");

            for (int j = 0; j < raffFlux.size(); j++) {
                AbstrRaffineur abstrRaffineur = raffFlux.get(j);
                enteteData.add("Raffinage " + abstrRaffineur.getClass().getSimpleName() + " ID " + abstrRaffineur.getID());
            }

            data.add((String[]) enteteData.toArray(new String[enteteData.size()]));

            try {
                // Compte pour le flux
                Query cptQuery;

                if (date1 != null && date2 != null) {
                    cptQuery = em.createQuery("SELECT COUNT(i) FROM Item i JOIN i.listFlux f WHERE f.ID=:fid AND i.dateRecup > :d1 AND i.dateRecup < :d2");
                    cptQuery.setParameter("d1", date1);
                    cptQuery.setParameter("d2", date2);
                } else {
                    cptQuery = em.createQuery("SELECT COUNT(i) FROM Item i JOIN i.listFlux f WHERE f.ID=:fid");
                }


                cptQuery.setParameter("fid", flux.getID());
                Long countPrFlux = (Long) cptQuery.getResultList().get(0);

                // On boucle tout les 10 000 Pour ne pas effectuer des requete a ralonge
                for (int j = 0; j < countPrFlux; j = j + 10000) {

                    em.clear(); // Il va y avoir des millier//millions d'entité dans l'em pour eviter un prob on clear a chaque tout de boucle
                    Query query;
                    if (date1 != null && date2 != null) {
                        query = em.createQuery("SELECT DISTINCT(i) FROM Item i LEFT OUTER JOIN FETCH i.doublon dl JOIN FETCH i.listFlux As f LEFT JOIN FETCH f.typeFlux t, f.journalLie j WHERE f.ID = :idf AND i.dateRecup > :d1 AND i.dateRecup<:d2"); // On fetch toute les entité qui seront utilisée pendant le traitement
                        query.setParameter("d1", date1);
                        query.setParameter("d2", date2);
                    } else {
                        query = em.createQuery("SELECT DISTINCT(i) FROM Item i LEFT OUTER JOIN FETCH i.doublon d JOIN FETCH i.listFlux As f LEFT JOIN FETCH f.typeFlux t, f.journalLie j WHERE f.ID = :idf"); // On fetch toute les entité qui seront utilisée pendant le traitement

                    }

                    query.setParameter("idf", flux.getID());
                    query.setFirstResult(j);
                    query.setMaxResults(10000);

                    //-------------------------------------------------------------------------
                    //                  Gestion des lignes du CSV
                    //-------------------------------------------------------------------------

                    // Pour chaque item, création d'un eligne dans le CSV avec kes infos

                    List<ContentRSS> items = query.getResultList();
                    for (int k = 0; k < items.size(); k++) {

                        List<String> itemData = new ArrayList<String>();
//                        Set<String> itemData = new LinkedHashSet<String>();

                        ContentRSS item = items.get(k);

                        String id = "";
                        if (item.getID() != null) {
                            id = item.getID().toString();
                        }
                        itemData.add(id);

                        String titre = "";
                        if (item.getTitre() != null) {
                            titre = item.getTitre();
                            if (purgehtml) {
                                titre = Jsoup.parse(titre).text();
                            }
                        }
                        itemData.add(titre);

                        String desc = "";
                        if (item.getDescription() != null) {
                            desc = item.getDescription().replace('\n', ' ');
                            if (purgehtml) {
                                desc = Jsoup.parse(desc).text();
                            }
                        }
                        itemData.add(desc);

                        String contenu = "NULL";
                        if (item.getContenu() != null) {
                            contenu = item.getContenu();
                            if (purgehtml) {
                                contenu = Jsoup.parse(contenu).text();
                            }
                        }
                        itemData.add(contenu);




                        String cat = "NULL";
                        if (item.getCategorie() != null) {
                            cat = item.getCategorie();
                        }
                        itemData.add(cat);

                        String lien = "";
                        if (item.getLink() != null) {
                            lien = item.getLink();
                        }
                        itemData.add(lien);






                        String dateRecup = "";
                        if (item.getDateRecup() != null) {
                            DateTime dt = new DateTime(item.getDateRecup());
                            dateRecup = fmt.print(dt);
                        }
                        itemData.add(dateRecup);

                        String datePub = "";
                        if (item.getDatePub() != null) {
                            DateTime dtPub = new DateTime(item.getDatePub());
                            datePub = fmt.print(dtPub);
                        }
                        itemData.add(datePub);







                        String idflux = "";
                        if (flux.getID() != null) {
                            idflux = flux.getID().toString();
                        }
                        itemData.add(idflux);

                        String typeFlux = "";
                        if (flux.getTypeFlux() != null) {
                            typeFlux = flux.getTypeFlux().getDenomination();
                        }
                        itemData.add(typeFlux);

                        String journal = "";
                        if (flux.getJournalLie() != null) {
                            journal = flux.getJournalLie().getNom();
                        }
                        itemData.add(journal);

                        //---> Raffinage
                        for (int l = 0; l < raffFlux.size(); l++) {
                            AbstrRaffineur abstrRaffineur = raffFlux.get(l);
                            DoublonDe doublon = ((Item) item).returnDoublonforRaffineur(abstrRaffineur);
                            if (doublon != null) {
//                                if (doublon.getItemRef().getID().equals(doublon.getItemDoublon().getID())) {
//                                    itemData.add("-1");
//                                } else {
                                itemData.add(doublon.getItemRef().getID().toString());
//                                }

                            } else {
                                itemData.add("??");
                            }
                        }

//                        itemData.toArray(new String[itemData.size()]);


                        data.add((String[]) itemData.toArray(new String[itemData.size()]));
//                        data.add((String[]) itemDataSet.toArray(new String) itemData.toArray(new String[itemData.size()]));
                        itemEnregistre++;

                        //Enregistrement du fichier si on a itere sur plus de 10 000 items
                        if (itemEnregistre > nbrLigneParFichie) {
                            try {

                                save(data, nbrFichier, nomFichier);
                                itemEnregistre = 0;
                                nbrFichier++;
                            } catch (Exception e) {
                                logger.debug("Erreur lors de l'enregistremnt du fichier ", e);
                            }
                        }
                        em.detach(item); // On retire l'item de l'em au cas c'est toujours un peu de mémoire gagné ?
                    }
                }

            } catch (Exception e) {
                logger.debug("err", e);
            }

            // Avant de passer au flux suivant on enregistre
            save(data, nbrFichier, nomFichier);
            assemblageFichier(nomFichier, exportPath);

            nbrFichier = 0;
        }
        return null;

    }

    /**
     * *
     * Crée le répertoire dans lequel seront stoqué les fichier d'export.
     */
    private void createUniqRep() {
        String repName = "EXPORT--" + rssagregator.utils.FileUtils.contructMailFileName() + "/";
        exportPath = webDir + "upload/" + repName;
        new File(exportPath).mkdir();

        Conf c = DAOFactory.getInstance().getDAOConf().getConfCourante();
        redirPath = c.getServurl() + "/upload/" + repName + "/";
    }


    /**
     * *
     * Construit le nom de fichier a partir du flux envoyé en arguement
     *
     * @param flux
     * @return
     */
    public String constructionNomFichier(Flux flux) {

        String nomFichier = "";
        String pays = (flux.getJournalLie() != null && flux.getJournalLie().getPays() != null && !flux.getJournalLie().getPays().isEmpty()) ? flux.getJournalLie().getPays() : "XX";
        String ville = (flux.getJournalLie() != null && flux.getJournalLie().getCodeVille() != null && !flux.getJournalLie().getCodeVille().isEmpty()) ? flux.getJournalLie().getCodeVille() : "XX";
        String langue = (flux.getJournalLie() != null && flux.getJournalLie().getLangue() != null && !flux.getJournalLie().getLangue().isEmpty()) ? flux.getJournalLie().getLangue() : "XX";
        String codeJournal = (flux.getJournalLie() != null && flux.getJournalLie().getCodeJournal() != null && !flux.getJournalLie().getCodeJournal().isEmpty()) ? flux.getJournalLie().getCodeJournal() : "XX";
        String codeTypeFlux = (flux.getTypeFlux() != null && flux.getTypeFlux().getCodeType() != null && !flux.getTypeFlux().getCodeType().isEmpty()) ? flux.getTypeFlux().getCodeType() : "XXX";

        nomFichier = langue + "_" + pays + "_" + codeJournal + "_" + codeTypeFlux;
        return nomFichier;

    }

    /**
     * *
     * Sauvegarde les ligne du scv dans un fichier
     *
     * @param data
     * @param numFichier
     * @param flux
     * @throws IOException
     */
    private void save(List<String[]> data, Integer numFichier, String nomFichier) throws IOException {
        FileWriter fileWriter = null;
        CSVWriter cSVWriter2 = null;
        //-------------------------------------------------------------
        //              Construction du nom de fichier
        //-------------------------------------------------------------


//        String nomFichier = constructionNomFichier(flux);

//        // Mini rappel sur les ternaire en java : variable = (condition) ? valeur_si_vrai : valeur_si_faux; 
//        String pays = (flux.getJournalLie() != null && flux.getJournalLie().getPays() != null && !flux.getJournalLie().getPays().isEmpty()) ? flux.getJournalLie().getPays() : "XX";
//        String ville = (flux.getJournalLie() != null && flux.getJournalLie().getCodeVille() != null && !flux.getJournalLie().getCodeVille().isEmpty()) ? flux.getJournalLie().getCodeVille() : "XX";
//        String langue = (flux.getJournalLie() != null && flux.getJournalLie().getLangue() != null && !flux.getJournalLie().getLangue().isEmpty()) ? flux.getJournalLie().getLangue() : "XX";
//        String codeJournal = (flux.getJournalLie() != null && flux.getJournalLie().getCodeJournal() != null && !flux.getJournalLie().getCodeJournal().isEmpty()) ? flux.getJournalLie().getCodeJournal() : "XX";
//        String codeTypeFlux = (flux.getTypeFlux() != null && flux.getTypeFlux().getCodeType() != null && !flux.getTypeFlux().getCodeType().isEmpty()) ? flux.getTypeFlux().getCodeType() : "XXX";
//
//        nomFichier = langue + "_" + pays + "_" + codeJournal + "_" + codeTypeFlux;

        //--------------------------------------------------------------------------------
        //                      Enregistrement 
        //--------------------------------------------------------------------------------
        try {
            fileWriter = new FileWriter(exportPath + nomFichier + "__F__" + numFichier.toString() + ".csv");

            if (escapebySlash) {
                cSVWriter2 = new CSVWriter(fileWriter, '\t', '"', '\\');
            } else {
                cSVWriter2 = new CSVWriter(fileWriter, '\t', '"');
            }

            cSVWriter2.writeAll(data);


        } catch (Exception e) {
            logger.debug("Erreur lors de l'enregistrement");
        } finally {
            if (cSVWriter2 != null) {
                try {
                    cSVWriter2.close();
                } catch (Exception e) {
                }
            }

            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (Exception e) {
                }
            }
        }

        data.clear(); // On purge le fichier data
        //---------------------------------------------------------------------------
        //                              Assemblage 
        //---------------------------------------------------------------------------
        // Si ce n'est pas le premier fichier. Il faut réassembler
//        if (numFichier != 0) {
//            File f1 = new File(exportPath + nomFichier + "__F__0.csv");
//            File fCurrent = new File(exportPath + nomFichier + "__F__" + numFichier.toString() + ".csv");
//            
//            String content = FileUtils.readFileToString(fCurrent);
//            FileUtils.write(f1, content, true);
//            
//            fCurrent.delete();
//        }
    }

    /**
     * *
     * Assemble tous les fichiers d'un répertoire {@link #dir} commencant par arg nomFichier.
     *
     * @param nomfichier
     * @param dir
     */
    public void assemblageFichier(String nomfichier, String dir) {

        // On récupère tous les fichier 
        File rep = new File(dir);

        final String fName = nomfichier;
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {

                return name.startsWith(fName);
            }
        };


        File[] fichiers = rep.listFiles(filenameFilter);
        Arrays.sort(fichiers, new FileNameComparator());

        for (int i = 0; i < fichiers.length; i++) {
            File file = fichiers[i];
            // Si ce n'est pas la première itération on assemble avec le pré
            if (i > 0) {
//                File prec = fichiers[i - 1];
                try {
                    // On met le contenu du fichier courant dans le premier
                    String content = FileUtils.readFileToString(file);
                    FileUtils.write(fichiers[0], content, true);

                    // On supprime le fichier précédent
                    file.delete();
                } catch (Exception e) {
                    logger.debug("Execption ", e);
                }
            }
        }

        //---> Il faut renommer le premier fichier

        fichiers[0].renameTo(new File(dir + nomfichier + ".csv"));
    }

//    /**
//     * *
//     * réassemble les fichiers pour les flux considérées
//     *
//     * @param nbrfichier
//     * @throws IOException
//     */
//    private void assemblage() throws IOException {
//
//
//        // On parcours le répertoire
//        // on prend un fichier
//        //------ si c'est un 0 . on boucle sur les  autres pour 
//
//
//
//        // On cherche tous les fichiers qui ne sont pas au format nimportequel caractère __F__0.csv
//        //On les ajoute dans une liste
//        // On prend le premier 
//
//
//
//
//
//
//        for (int i = 0; i <= nbrfichier; i++) {
//            String nomfichier = "/home/clem/CSV" + nbrfichier + ".csv";
//            FileInputStream fis = new FileInputStream(nomfichier);
//
//            FileWriter fileWriter = new FileWriter("/home/clem/final.csv");
//
//            fileWriter.append(fis.toString());
//
//            fileWriter.flush();
//            fileWriter.close();
//        }
//    }
    public SearchFiltersList getFiltre() {
        return filtre;
    }

    public void setFiltre(SearchFiltersList filtre) {
        this.filtre = filtre;
    }

    public List<Flux> getFluxDemande() {
        return fluxDemande;
    }

    public void setFluxDemande(List<Flux> fluxDemande) {
        this.fluxDemande = fluxDemande;
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public Date getDate2() {
        return date2;
    }

    public void setDate2(Date date2) {
        this.date2 = date2;
    }

    public String getRedirPath() {
        return redirPath;
    }

    public void setRedirPath(String redirPath) {
        this.redirPath = redirPath;
    }

    public boolean isRafine() {
        return rafine;
    }

    public void setRafine(boolean rafine) {
        this.rafine = rafine;
    }
}
