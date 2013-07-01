/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



$(document).ready(function() {

    var $journalSelection = $('#journalSelection');
    var $fluxSelection = $('#fluxSelection');



    // à la sélection d une région dans la liste
    $journalSelection.on('change', function truc() {
        var val = $(this).val(); // on récupère la valeur de la région

        if (val != '') {
            $fluxSelection.empty(); // on vide la liste des départements

            $.ajax({
                url: 'flux?action=list&vue=json',
                data: 'journal-id=' + val, // on envoie $_GET['id_region']
                dataType: 'json',
                success: function(json) {
//                    $fluxSelection.append('<option value=NULL>NULL</option>');
                    $.each(json, function(index, value) {

                        $fluxSelection.append('<option value="' + value[0] + '">' + value[1] + '</option>');
                    });
                }
            });
        }
    });
    

   
   
   
});