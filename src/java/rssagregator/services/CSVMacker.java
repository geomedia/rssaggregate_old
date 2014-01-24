/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.services;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.io.FileUtils;
import org.apache.naming.java.javaURLContextFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import rssagregator.beans.Conf;
import rssagregator.beans.Flux;
import rssagregator.beans.Item;
import rssagregator.dao.DAOFactory;
import rssagregator.dao.SearchFiltersList;
import rssagregator.utils.StringUtils;

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
//
//
//        List<String[]> data1 = new ArrayList<String[]>();
//
//        //--Ecriture des entete
//        data1.add(new String[]{"ID Item", "Titre", "Description", "Contenu", "Catégorie", "Lien", "Date Récup", "Date publication", "ID flux", "typeFlux", "Journal"});
//
//        try {
//            cSVWriter.writeAll(data1);
//        } catch (Exception e) {
//        } finally {
//            cSVWriter.close(); 
//            fileWriter.close();
//        }

        System.out.println("-----------------------------------");
        System.out.println("Date1 " + date1);
        System.out.println("Date2 " + date2);
        EntityManager em = DAOFactory.getInstance().getEntityManager();

        for (int i = 0; i < fluxDemande.size(); i++) {
            //On crée le fichier data avec entete
            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[]{"ID_Item", "Titre", "Description", "Contenu", "Categorie", "Lien", "Date_Recup", "Date_publication", "ID_flux", "typeFlux", "Journal"});

            Flux flux = fluxDemande.get(i);
            flux = em.find(Flux.class, flux.getID()); // Il faut rafraichir


            try {
                // Compte pour le flux
                Query cptQuery = em.createQuery("SELECT COUNT(i) FROM Item i JOIN i.listFlux f WHERE f.ID=:fid");
                cptQuery.setParameter("fid", flux.getID());
                Long countPrFlux = (Long) cptQuery.getResultList().get(0);

                // On boucle tout les 10 000 Pour ne pas effectuer des requete a ralonge
                for (int j = 0; j < countPrFlux; j = j + 10000) {

                    em.clear(); // Il va y avoir des millier//millions d'entité dans l'em pour eviter un prob on clear a chaque tout de boucle
//                    Query query = em.createQuery("SELECT DISTINCT(i) FROM Item i LEFT JOIN i.listFlux f, i.listFlux.typeFlux t, i.listFlux.journalLie j WHERE f.ID = :idf");
                    Query query = em.createQuery("SELECT DISTINCT(i) FROM Item i JOIN FETCH i.listFlux As f LEFT JOIN FETCH f.typeFlux t, f.journalLie j WHERE f.ID = :idf"); // On fetch toute les entité qui seront utilisée pendant le traitement
                    query.setParameter("idf", flux.getID());
                    query.setFirstResult(j);
                    query.setMaxResults(10000);


                    // Pour chaque item

                    List<Item> items = query.getResultList();
                    for (int k = 0; k < items.size(); k++) {
                        Item item = items.get(k);

                        String id = "";
                        if (item.getID() != null) {
                            id = item.getID().toString();
                        }

                        String titre = "";
                        if (item.getTitre() != null) {

                            titre = item.getTitre();
                            if (purgehtml) {
                                titre = Jsoup.parse(titre).text();
                            }
                        }


                        String desc = "";
                        if (item.getDescription() != null) {
                            desc = item.getDescription().replace('\n', ' ');
                            if (purgehtml) {
                                desc = Jsoup.parse(desc).text();
                            }
                        }

                        String dateRecup = "";
                        if (item.getDateRecup() != null) {
                            DateTime dt = new DateTime(item.getDateRecup());
                            dateRecup = fmt.print(dt);
                        }

                        String datePub = "";
                        if (item.getDatePub() != null) {
                            DateTime dtPub = new DateTime(item.getDatePub());
                            datePub = fmt.print(dtPub);
                        }

                        String cat = "";
                        if (item.getCategorie() != null) {
                            cat = item.getCategorie();
                        }

                        String contenu = "";
                        if (item.getContenu() != null) {
                            contenu = item.getContenu();
                            if (purgehtml) {
                                contenu = Jsoup.parse(contenu).text();
                            }
                        }

                        String lien = "";
                        if (item.getLink() != null) {
                            lien = item.getLink();
                        }

                        String idflux = "";
                        if (flux.getID() != null) {
                            idflux = flux.getID().toString();
                        }

                        String typeFlux = "";
                        if (flux.getTypeFlux() != null) {
                            typeFlux = flux.getTypeFlux().getDenomination();
                        }


                        String journal = "";
                        if (flux.getJournalLie() != null) {
                            journal = flux.getJournalLie().getNom();
                        }

                        data.add(new String[]{id, titre, desc, contenu, cat, lien, dateRecup, datePub, idflux, typeFlux, journal});
                        itemEnregistre++;
//                        System.out.println("ITEM : " + item);

                        //Enregistrement du fichier si on a itere sur plus de 10 000 items
                        if (itemEnregistre > nbrLigneParFichie) {
                            try {
                                save(data, nbrFichier, flux);
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
            save(data, nbrFichier, flux);
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
//        String path = System.getProperty("confpath");
        exportPath = webDir + "upload/" + repName;
        System.out.println("EXP Path " + exportPath);
        new File(exportPath).mkdir();

        Conf c = DAOFactory.getInstance().getDAOConf().getConfCourante();


        redirPath = c.getServurl() + "/upload/" + repName + "/";

    }

    public static void main(String[] args) {
        String chaine = "youpAi : ! \\ zouzu";
//        System.out.println("replace" + chaine.replaceAll("[!:\\\\] ", ""));

        Pattern parPattern = Pattern.compile("[a-z]*");

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
    private void save(List<String[]> data, Integer numFichier, Flux flux) throws IOException {
        FileWriter fileWriter = null;
        CSVWriter cSVWriter2 = null;
        //-------------------------------------------------------------
        //              Construction du nom de fichier
        //-------------------------------------------------------------
        String nomFichier = "";
        String nomJoural = "";
        if (flux.getJournalLie() != null && flux.getJournalLie().getNom() != null) {
            nomJoural = StringUtils.returnAbrege(flux.getJournalLie().getNom(), 20, "[^a-zA-Z0-9]");
        }
        if (nomJoural.isEmpty()) {
            nomJoural = "journal";
            if (flux.getJournalLie() != null) {
                nomJoural += flux.getJournalLie().getID().toString();
            }
        }
        nomFichier += nomJoural + "-";

        if (flux.getTypeFlux() != null) {
            nomFichier += StringUtils.returnAbrege(flux.getTypeFlux().getDenomination(), 20, "[^a-zA-Z0-9]");
        }

        nomFichier += "-ID" + flux.getID().toString();


        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        if (date1 != null) {
            nomFichier += "-" + fmt.print(new DateTime(date1));
        }
        if (date2 != null) {
            nomFichier += "_" + fmt.print(new DateTime(date2));
        }

//        nomFichier += "__F__" + numFichier.toString();


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

            System.out.println("Enregistrement data leangh " + data.size());
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
        data.clear(); // On surge le fichier data
        //---------------------------------------------------------------------------
        //                              Assemblage 
        //---------------------------------------------------------------------------
        // Si ce n'est pas le premier fichier. Il faut réassembler
        if (numFichier != 0) {
            File f1 = new File(exportPath + nomFichier + "__F__0.csv");
            File fCurrent = new File(exportPath + nomFichier + "__F__" + numFichier.toString() + ".csv");

//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            // Lecture du premier fichier
//                     FileInputStream fis = new FileInputStream(fCurrent);
//            int buf = 0;
//            while((buf=fis.read())!=-1){
//                baos.write(buf);
//            }
//         
//            // apprend dans le second
//            FileOutputStream fos = new FileOutputStream(f1);
//            fos.write(baos.toByteArray());


            String content = FileUtils.readFileToString(fCurrent);
            FileUtils.write(f1, content, true);


//            FileOutputStream fos = new FileOutputStream(f1);
//                     Suppression du fichier
            fCurrent.delete();
        }
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
//        System.out.println("Assemblage");
//
//        for (int i = 0; i <= nbrfichier; i++) {
//            System.out.println("IT");
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
}
