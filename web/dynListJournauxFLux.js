/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/***
 * Fonction permettant le dragdrop dans la liste de journaux
 */
$(document).ready(function() {
//    $('#fluxSelection li').draggable({helper: "clone", connectWith: ".connectedSortable"})
    ;
    $("#fluxSelection, #fluxSelection2").sortable({
        connectWith: ".connectedSortable",
        remove: uiRemove,
        placeholder: "sortable-placeholder",
        helper: "clone",
        revert: true
    });
    

    
//    $("#fluxSelection li").draggable({helper: "clone"});
});


/***
 * Lorsqu'un élément est recut dans la liste 2 il faut vérifier qu'il n'est pas déjà présent. Cette fontion permet d'annuler le drop si l'élémetn est déjà présent
 * @param {type} event
 * @param {type} ui
 * @returns {Boolean}
 */
function uiRemove(event, ui) {
    trouve = 0;
    for (i = 0; i < $('#fluxSelection2 li').length; i++) {
        if (ui['item'].val() === $('#fluxSelection2 li')[i]['value']) {
//                          alert("trouve");
            trouve++;
        }
    }
    if (trouve > 1) {
        return false;
    }
}

$(document).ready(function() {

    // à la sélection d une région dans la liste
    $('#journalSelection').on('change', function truc() {
        var val = $(this).val(); // on récupère la valeur de la région

        if (val != '') {
            $('#fluxSelection').empty(); // on vide la liste des départements
            $.ajax({
                url: '/RSSAgregate/flux/list?vue=json',
                data: 'journalid=' + val, // on envoie $_GET['id_region']
                dataType: 'json',
                success: function(json) {
                    $.each(json, function(index, value) {
                        $('#fluxSelection').append('<li class=\"boxelement\" value="' + value[0] + '">' + value[1] + '</li>');
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

