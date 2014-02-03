/**
 * *
 * <p>Les formulaire permettent d'interpréter les requêtes utilisateurs, afin de valider et nourrir des beans. Mais
 * aussi interpréter des requête de cherche. Chaque type de beans pouvant faire l'objet d'une modif CRUD par
 * l'utilisateur doit aisni posséder son formulaire permettant de valider les données.
 * 
 * Les formulaires sont utilisés par les servlet qui instancient des objets formulaires et utilisent les méthode
 * {@link #validate(javax.servlet.http.HttpServletRequest)} et
 * {@link #bind(javax.servlet.http.HttpServletRequest, java.lang.Object, java.lang.Class)} pour interpréter la requete
 * utilisateur et crée ou modifier des beans (Flux, Journaux...)
 * 
 * </p>
 * <p>
 * L'instanciation des formulaire se fait depuis la {@link FORMFactory}
 * </p>
 * <p>Tous les formulaire héritent de AbstrForm VOIR LA DOC DE CETTE CLASSE POUR UNE DESCRIPTION DETAILLE DE L'USAGE DES FORMULAIRES</p>
 */
package rssagregator.beans.form;