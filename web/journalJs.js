/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {

 $('#autoUpdateFlux:checkbox').change(function() {
     
     if($('#updateDiv').children().length===0){
         
         $('#updateDiv').empty();
         $('#updateDiv').append("<label>Périodicité en seconde entre deux vérification</label><input name=\"periodiciteDecouverte\" id=\"periodiciteDecouverte\" /><br />\n\
<label>Activer les flux après leur découverte</label><input type=\"checkbox\" name=\"activerFluxDecouvert\" />\n\
");
     }
     else{
         $('#updateDiv').empty();
     }
     
 });


    /***
     * Cette fonction fait apparaitre un sous formulaire lors du click sur le lien #linkDiscover
     */
    $('#linkDiscover').click(function() {

        if ($('#sousFormDiscover').children().length === 0) {
            $('#linkDiscover').text("annuler")
            $('#sousFormDiscover').empty();
            $('#sousFormDiscover').append("<form action=\"/RSSAgregate/journaux/discover\">\n\
        \n\
<fieldset>\n\
<label>Enregistrer les flux découverts dans la base de données</label><input type=\"checkbox\" name=\"persist\" /><br />\n\
<label>Activer flux découverts</label><input type=\"checkbox\" name=\"active\" />\n\
<input type=\"hidden\" name=\"id\" value=" + $('#id').val() + ">\n\
<input type=\"submit\">Cette action devrait prendre entre 5 et 30 secondes\n\
</fieldset>\n\
</form>");
        }
        else {
            $('#linkDiscover').text("Ajout de tous les flux du journal par découverte");
            $('#sousFormDiscover').empty();
        }

        return false;
    });

});