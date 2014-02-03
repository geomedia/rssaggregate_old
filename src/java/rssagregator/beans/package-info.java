/**
 * 
 * <p>Les classes formalisants les différentes entités gérées par l'agrégateur de flux : Jounaux, Flux, Item... Les beans
 * sont persistés dans la base de données. On observe differents type de beans
 * <ul>
 * <li>normal : il sont placé directement dans le package beanss. Ils correcpondent aux entités tel que Journaux, Flux,
 * Item</li>
 * <li>incident: placé dans la package incident. Lorsqu'une Tache est en échec, un incident est généré</li>
 *
 * </ul>
 * </p>
 */
package rssagregator.beans;