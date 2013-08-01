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
                url: 'flux/list?vue=json',
                data: 'journalid=' + val, // on envoie $_GET['id_region']
                dataType: 'json',
                success: function(json) {
                    $.each(json, function(index, value) {
                        $fluxSelection.append('<option value="' + value[0] + '">' + value[1] + '</option>');
                    });
                }
            });
        }
    });
}
);

// Ajoute un flux de la liste de gauche à la liste de droite en vérifiant que ce flux n'est pas déja ajouté. 
function selectflux() {
    $('#fluxSelection  option:selected').each(function(aaa) {
        i = 0;
        var test = $('#fluxSelection2  option');
        ajout = true;
        for (j = 0; j < test.length; j++) {
            if (test[j].value === $(this).val()) {
                ajout = false;
            }
        }
        if (ajout) {
            $('#fluxSelection2').append('<option value="' + $(this).val() + '">' + $(this).text() + '</option>');
        }
    }
    );
}
// Supprimer le ou les flux sélectionné de la liste de droite
function supp() {
    // récup de la selection dans la liste 2
    $('#fluxSelection2  option:selected').each(function(aaa) {
        $(this).remove();
    }
    
    );

}

