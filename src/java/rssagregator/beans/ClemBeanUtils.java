/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rssagregator.beans;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.servlet.http.HttpServletRequest;
import rssagregator.beans.form.AbstrForm;
import rssagregator.dao.DAOFactory;

/**
 * <p>CETTE CLASSE N'EST PLUS UTILISÉE</p>
 * <p>Auparavant la méthode populate permettait par réflexivité de nourrir les
 * beans depuis la requête envoyée par l'utilisateur. Mais cette pratique
 * générique a vite montré des limites. Le bind des données est maintenant
 * effectuée avec une pratique plus rustique mais fiable</p>
 *
 * @author clem
 */
@Deprecated
public class ClemBeanUtils {

    protected static String PERSISTENCE_UNIT_NAME = "RSSAgregatePU2";

    /**
     * *
     * Permet de peuples un bean avec les données une request au travers d'un
     * formulaire. Fonctione pour tous les types de beans du projet
     *
     * @param bean
     * @param request
     * @param form
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Deprecated
    public static void populate(Object bean, HttpServletRequest request, AbstrForm form) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {


        // On initialise le hashmap d'erreur;
        form.setErreurs(new HashMap<String, String[]>());

        //On parcours chaque paramettre de la requete

        // on parcours chaque champ du beans

        Field[] tabfield = bean.getClass().getDeclaredFields();

//        // Il faut ajouter les field de la classe parente si elle exite
//        
//        Field[] tabfieldmere=  bean.getClass().getSuperclass().getDeclaredFields();
//           int i;
//           
//           Field[] tabfield = new Field[tabfieldcurrent.length  + tabfieldmere.length ];
//           for(i=0; i<tabfieldcurrent.length;i++){
//               tabfield[i]= tabfieldcurrent[i];
//           }
//           
//           int j = 0;
//           for (j=0;j<tabfieldmere.length;j++){
//               tabfield[i] = tabfieldmere[j];
//               i++;
//           }
//        


        int i;

        for (i = 0; i < tabfield.length; i++) {
            String nomVariable = tabfield[i].getName();
            String nomSetter = "set" + nomVariable.substring(0, 1).toUpperCase() + nomVariable.substring(1, nomVariable.length());
            Method setter = simpleGetMethod(bean, nomSetter);
            String contenuRequest = request.getParameter(nomVariable);
            // Si on n'a pas trouvé le setter, on ne peut rien faire ce n'est pas un champs corect de JavaBean
            if (setter != null) {
                Class typeArgSetter = setter.getParameterTypes()[0];
                // si le champs est annoté par @Colum : pour des sting ou des boolleen
                if (tabfield[i].isAnnotationPresent(Column.class)) {
                    if (typeArgSetter.equals(String.class)) {
                        setter.invoke(bean, contenuRequest);
                    }
                    // si c'est un boollen
                    if (typeArgSetter.equals(Boolean.class)) {
                        // Pour une checkbox si on n'a pas de contenu dans l'argument c'est false, si il y a du contenu c'est vrai (pour les fomulaire html)
                        if (contenuRequest == null || contenuRequest.isEmpty()) {
                            setter.invoke(bean, Boolean.FALSE);
                        } else {
                            setter.invoke(bean, Boolean.TRUE);
                        }
                    }
                    if (typeArgSetter.equals(Integer.class) && contenuRequest != null && !contenuRequest.isEmpty()) {
                        //Convertion du string en int
                        try {
                            Integer intContenu = Integer.parseInt(contenuRequest);
                            setter.invoke(bean, intContenu);
                        } catch (Exception e) {

                            form.getErreurs().put(nomVariable, new String[]{contenuRequest, "Il faut inscrire un nombre entier"});

                        }
                    }
                }
//Si c'est une annotation faisant référence à une entité relation ***toOne
                if (tabfield[i].isAnnotationPresent(ManyToOne.class) || tabfield[i].isAnnotationPresent(OneToOne.class)) {

                    String parameter = request.getParameter(nomVariable);

                    if (parameter != null) {
                        try {
                            Long id = new Long(parameter);
                            EntityManager em;
                            EntityManagerFactory emf;
                            em = DAOFactory.getInstance().getEntityManager();
                            Object objTrouve = em.find(typeArgSetter, id);
                            setter.invoke(bean, objTrouve);
                        } catch (Exception e) {
                        }



                    }
                }
            }

        }
    }

    /**
     * *
     * Renvoie la methode pour le beans
     *
     * @param nomSetter
     * @return La methode ou null
     */
    private static Method simpleGetMethod(Object bean, String nomSetter) {
        int i;
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (i = 0; i < methods.length; i++) {

            if (methods[i].getName().equals(nomSetter)) {
                return methods[i];
            }
        }
        return null;
    }

//    public static Map<String, String> check(Object objetFormulaire, Object beans) throws SecurityException, NoSuchMethodException {
//        Map<String, String> erreurs = new HashMap<String, String>();
//        
//        // On parcours chaque champs du beans
//        Field[] beanFields = beans.getClass().getDeclaredFields();
//
//        int i;
//        for (i = 0; i < beanFields.length; i++) {
//            //Recherche de la méthode de check pour le champs du beans
//            String nomMethod = "check_" + beanFields[i].getName();
//            Method mCheck = simpleGetMethod(objetFormulaire, nomMethod);
//            //            // récupération du contenu du fields
//            try {
//                String nomGetter = "get" + beanFields[i].getName().substring(0, 1).toUpperCase() + beanFields[i].getName().substring(1, beanFields[i].getName().length());
//                Method getter = beans.getClass().getMethod(nomGetter);
//                beans.getClass().getMethod(nomGetter);
//                Object contenuFieldBean = getter.invoke(beans);
//                // On lanche la methode de check
//                if (mCheck != null) {
//                    try {
//                        Object retour = mCheck.invoke(objetFormulaire, contenuFieldBean);
//                    } catch (IllegalAccessException ex) {
////                        Logger.getLogger(ClemBeanUtils.class.getName()).log(Level.SEVERE, null, ex);
//                    } catch (IllegalArgumentException ex) {
////                        Logger.getLogger(ClemBeanUtils.class.getName()).log(Level.SEVERE, null, ex);
//                    } 
//                    catch (InvocationTargetException ex) {
//                        erreurs.put(beanFields[i].getName(), ex.getTargetException().getMessage());
//                    }
//                    catch(Exception ex){
//
//                    }
//                }
//            } catch (IllegalAccessException ex) {
//                Logger.getLogger(ClemBeanUtils.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IllegalArgumentException ex) {
//                Logger.getLogger(ClemBeanUtils.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (InvocationTargetException ex) {
//                Logger.getLogger(ClemBeanUtils.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//
//        return erreurs;
//    }
    @Deprecated
    public static void check(AbstrForm objetFormulaire, Object beans) throws SecurityException, NoSuchMethodException {
//        Map<String, String> erreurs = new HashMap<String, String>();
        // On parcours chaque champs du beans
        Field[] beanFields = beans.getClass().getDeclaredFields();

        int i;
        for (i = 0; i < beanFields.length; i++) {
            //Recherche de la méthode de check pour le champs du beans
            String nomMethod = "check_" + beanFields[i].getName();
            Method mCheck = simpleGetMethod(objetFormulaire, nomMethod);
            //            // récupération du contenu du fields
            try {
                String nomGetter = "get" + beanFields[i].getName().substring(0, 1).toUpperCase() + beanFields[i].getName().substring(1, beanFields[i].getName().length());
                Method getter = beans.getClass().getMethod(nomGetter);
                beans.getClass().getMethod(nomGetter);
                Object contenuFieldBean = getter.invoke(beans);
                // On lanche la methode de check
                if (mCheck != null) {
                    try {
                        Object retour = mCheck.invoke(objetFormulaire, contenuFieldBean);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(ClemBeanUtils.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(ClemBeanUtils.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
//                        erreurs.put(beanFields[i].getName(), ex.getTargetException().getMessage());
                        objetFormulaire.getErreurs().put(beanFields[i].getName(), new String[]{contenuFieldBean.toString(), ex.getTargetException().getMessage()});
                    } catch (Exception ex) {
                    }
                }
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ClemBeanUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ClemBeanUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(ClemBeanUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


//        return erreurs;
    }

    private ClemBeanUtils() {
    }
}
