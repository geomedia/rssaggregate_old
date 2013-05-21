/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans.form;

/**
 *
 * @author clem
 */
public class JournalForm extends AbstrForm{
//    private Map<String, String> erreurs = new HashMap<String, String>();
//    private String resultat = "";
//    private Boolean valide = false;
//    private DaoJournal dao;

    public JournalForm(/*AbstrDao dao*/) {
//        super(dao);
    }

    
    
//    public Journal bind(HttpServletRequest request, Journal journal) {
//        // SI flux est null (cas d'un ajout, on crée un nouveau flux
//        if (journal == null) {
//            journal = new Journal();
//        }
//
//        try {
//            ClemBeanUtils.populate(journal, request);
//            erreurs = ClemBeanUtils.check(this, journal);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(JournalForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(JournalForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(JournalForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(JournalForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(JournalForm.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        if (erreurs.isEmpty()) {
//            resultat = "Traitement effectué";
//            valide = true;
//
//        } else {
//            resultat = "Erreur lors de la validation des données";
//            valide = false;
//        }
//        return journal;
//    }
//
//    private static String getValeurChamp(HttpServletRequest request, String nomChamp) {
//        String valeur = request.getParameter(nomChamp);
//        if (valeur == null || valeur.trim().length() == 0) {
//            return null;
//        } else {
//            return valeur.trim();
//        }
//    }

    public void check_nom(String nom) throws Exception {
        if (nom == null || nom.length() == 0) {
            throw new Exception("Ne peut être null");
        }
    }
    
    public void check_langue(String nom) throws Exception {
        if (nom == null || nom.length() == 0) {
            throw new Exception("Ne peut être null");
        }
    }
    
//    public Boolean getValide() {
//        return valide;
//    }
//
//    public void setValide(Boolean valide) {
//        this.valide = valide;
//    }
//
//    public Map<String, String> getErreurs() {
//        return erreurs;
//    }
//
//    public void setErreurs(Map<String, String> erreurs) {
//        this.erreurs = erreurs;
//    }
//
//    public String getResultat() {
//        return resultat;
//    }
//
//    public void setResultat(String resultat) {
//        this.resultat = resultat;
//    }
//
//    public DaoJournal getDao() {
//        return dao;
//    }
//
//    public void setDao(DaoJournal dao) {
//        this.dao = dao;
//    }
    
    
}
